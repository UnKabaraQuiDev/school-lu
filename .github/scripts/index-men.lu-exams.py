import csv
import re
import time
from pathlib import Path
from urllib.parse import urljoin, urlencode

import requests
from bs4 import BeautifulSoup
import subprocess


BASE_URL = "https://portal.education.lu"
ROOT_URL = f"{BASE_URL}/Services/Examens"

GIT_DIR = Path(
    subprocess.check_output(
        ["git", "-C", str(Path(__file__).resolve().parent), "rev-parse", "--show-toplevel"],
        text=True
    ).strip()
)

OUTPUT_CSV = GIT_DIR / "exams" / "men.lu-index.csv"
COOKIE_FILE = GIT_DIR / ".local" / "cookies.txt"

USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/139.0.0.0 Safari/537.36"

REQUEST_DELAY = 0.2


class PortalSession:
    def __init__(self):
        self.session = requests.Session()

        self.session.headers.update({
            "User-Agent": USER_AGENT,
            "Accept": "text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,*/*;q=0.8",
            "Accept-Language": "en-US,en;q=0.9",
            "Connection": "keep-alive",
        })

        self.load_cookies()

    def load_cookies(self):
        """
        Load cookies from a Netscape-style cookie file if it exists.
        """
        if not Path(COOKIE_FILE).exists():
            return

        try:
            import http.cookiejar

            jar = http.cookiejar.MozillaCookieJar(COOKIE_FILE)
            jar.load(ignore_discard=True, ignore_expires=True)
            self.session.cookies.update(jar)
        except Exception as e:
            print(f"Warning: could not load cookies: {e}")

    def save_cookies(self):
        """
        Save the current cookies for reuse on the next run.
        """
        try:
            import http.cookiejar

            jar = http.cookiejar.MozillaCookieJar(COOKIE_FILE)

            for cookie in self.session.cookies:
                jar.set_cookie(cookie)

            jar.save(ignore_discard=True, ignore_expires=True)
        except Exception as e:
            print(f"Warning: could not save cookies: {e}")

    def get(self, url):
        time.sleep(REQUEST_DELAY)

        response = self.session.get(
            url,
            timeout=30,
        )

        response.raise_for_status()

        self.save_cookies()

        return response


class Indexer:
    def __init__(self):
        self.client = PortalSession()

        self.rows = []
        self.visited = set()

    def get_page(self, folder_id=None):
        """
        Fetch either the root Examens page or a folder page.

        Exit gracefully if the request fails or the response is invalid.
        """
        if folder_id is None:
            url = ROOT_URL
        else:
            url = f"{ROOT_URL}?{urlencode({'folderId': folder_id})}"

        print()
        print(f"Fetching: {url}")

        try:
            response = self.client.get(url)
        except requests.RequestException as e:
            print(f"ERROR: Request failed for {url}")
            print(f"       {e}")
            raise SystemExit(1)
        
        recv_dir = GIT_DIR / ".local"
        recv_dir.mkdir(parents=True, exist_ok=True)

        recv_file = recv_dir / "recv.html"

        print(f"       Status: {response.status_code}")

        if not response.ok:
            print(f"ERROR: Invalid HTTP response for {url}")
            print(f"       Status: {response.status_code}")
            print(f"       Response: {response.text[:1000]!r}")
            raise SystemExit(1)

        if not response.text or not response.text.strip():
            print(f"ERROR: Empty response for {url}")
            print(f"       Status: {response.status_code}")
            raise SystemExit(1)

        # Basic sanity check that we actually received HTML.
        content_type = response.headers.get("Content-Type", "")
        print(f"       Content-Type: {content_type}")
        print(f"       Response: {response.text[:1000]!r}")

        if "html" not in content_type.lower():
            print(f"ERROR: Unexpected response type for {url}")
            print(f"       Content-Type: {content_type}")
            print(f"       Response: {response.text[:1000]!r}")
            raise SystemExit(1)

        # Make sure BeautifulSoup can parse something meaningful.
        soup = BeautifulSoup(response.text, "html.parser")

        if not soup.find("html") and not soup.find("div"):
            print(f"ERROR: Invalid/empty HTML response for {url}")
            print(f"       Response: {response.text[:1000]!r}")
            raise SystemExit(1)
        
        return response.text

    @staticmethod
    def get_direct_or_nested_title(element):
        """
        Get the title from a <p title="..."> element.
        """
        title = element.get("title")

        if title:
            return title.strip()

        return element.get_text(strip=True)

    @staticmethod
    def is_pdf_title(title):
        return title.lower().endswith(".pdf")

    @staticmethod
    def extract_folder_id(folder):
        """
        Extract the ID from:

            id="thumbnail-12345"
        """
        element_id = folder.get("id", "")

        match = re.fullmatch(r"thumbnail-(\d+)", element_id)

        if not match:
            return None

        return match.group(1)

    def parse_container(self, container):
        """
        Find the title and folder/file elements belonging to one
        rm-card-container.

        The important part is that descendants are searched rather than
        only direct children.
        """

        # Find all p[title] elements anywhere inside the card.
        title_elements = container.find_all("p", attrs={"title": True})

        if not title_elements:
            return []

        results = []

        for p in title_elements:
            title = self.get_direct_or_nested_title(p)

            if not title:
                continue

            # A PDF is a file. We don't recurse into it.
            if self.is_pdf_title(title):
                results.append({
                    "type": "file",
                    "title": title,
                    "id": None,
                })
                continue

            # Find a matching folder somewhere inside this card.
            folder = container.find(
                "div",
                class_=lambda classes: (
                    classes is not None
                    and "rm-circular" in classes
                    and "rm-folder" in classes
                ),
                id=re.compile(r"^thumbnail-\d+$"),
            )

            if folder:
                folder_id = self.extract_folder_id(folder)

                if folder_id:
                    results.append({
                        "type": "folder",
                        "title": title,
                        "id": folder_id,
                    })

        return results

    def parse_page(self, html):
        soup = BeautifulSoup(html, "html.parser")

        containers = soup.find_all(
            "div",
            class_="rm-card-container",
        )

        results = []

        for container in containers:
            results.extend(self.parse_container(container))

        return results

    def index_folder(self, folder_id=None, parent_id=None, parents=None):
        """
        Recursively index a folder.

        parents contains the titles of all ancestors.
        """
        if parents is None:
            parents = []

        # Prevent accidental loops.
        visit_key = folder_id if folder_id is not None else "ROOT"

        if visit_key in self.visited:
            print(f"Skipping already visited folder: {visit_key}")
            return

        self.visited.add(visit_key)

        html = self.get_page(folder_id)
        items = self.parse_page(html)

        for item in items:
            title = item["title"]
            item_id = item["id"]

            combined_title = "/".join(
                parents + [title]
            )

            row = {
                "Id": item_id or "",
                "ParentId": parent_id or "",
                "Title": title,
                "CombinedTitle": combined_title,
            }

            self.rows.append(row)

            print(
                f"Indexed: {combined_title}"
                f" | id={item_id or 'file'}"
            )

            # Only folders are recursively crawled.
            if item["type"] == "folder":
                self.index_folder(
                    folder_id=item_id,
                    parent_id=item_id,
                    parents=parents + [title],
                )

    def write_csv(self):
        with open(
            OUTPUT_CSV,
            "w",
            newline="",
            encoding="utf-8-sig",
        ) as f:
            writer = csv.DictWriter(
                f,
                fieldnames=[
                    "Id",
                    "ParentId",
                    "Title",
                    "CombinedTitle",
                ],
            )

            writer.writeheader()
            writer.writerows(self.rows)

        print()
        print(f"Wrote {len(self.rows)} entries to {OUTPUT_CSV}")


def main():
    indexer = Indexer()

    try:
        indexer.index_folder()
    finally:
        indexer.client.save_cookies()

    indexer.write_csv()


if __name__ == "__main__":
    main()