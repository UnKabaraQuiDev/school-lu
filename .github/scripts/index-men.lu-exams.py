#!/usr/bin/env python3

import csv
import time
import subprocess
from pathlib import Path

import requests


BASE_URL = "https://portal.education.lu"
API_URL = f"{BASE_URL}/DesktopModules/ResourceManager/API/Items/GetFolderContent"

ROOT_FOLDER_ID = 1919

GIT_DIR = Path(
    subprocess.check_output(
        [
            "git",
            "-C",
            str(Path(__file__).resolve().parent),
            "rev-parse",
            "--show-toplevel",
        ],
        text=True,
    ).strip()
)

OUTPUT_CSV = GIT_DIR / "men.lu" / "index.csv"
COOKIE_FILE = GIT_DIR / ".local" / "cookies.txt"

USER_AGENT = (
    "Mozilla/5.0 (X11; Linux x86_64; rv:145.0) "
    "Gecko/20100101 Firefox/145.0"
)

REQUEST_DELAY = 0.2
PAGE_SIZE = 20


class PortalSession:
    def __init__(self):
        self.session = requests.Session()

        self.session.headers.update({
            "User-Agent": USER_AGENT,
            "Accept": "application/json, */*",
            "Accept-Language": "en,en-US;q=0.5",
            "Referer": f"{BASE_URL}/Services/Examens",
            "groupid": "-1",
            "moduleid": "21960",
            "tabid": "2319",
            "Sec-GPC": "1",
            "Cache-Control": "no-cache",
            "Pragma": "no-cache",
        })

        self.load_cookies()

    def load_cookies(self):
        """
        Load cookies from a Netscape-style cookie file if it exists.
        """
        if not COOKIE_FILE.exists():
            return

        try:
            import http.cookiejar

            jar = http.cookiejar.MozillaCookieJar(COOKIE_FILE)
            jar.load(
                ignore_discard=True,
                ignore_expires=True,
            )

            self.session.cookies.update(jar)

        except Exception as e:
            print(f"Warning: could not load cookies: {e}")

    def save_cookies(self):
        """
        Save the current cookies for reuse on the next run.
        """
        try:
            COOKIE_FILE.parent.mkdir(
                parents=True,
                exist_ok=True,
            )

            import http.cookiejar

            jar = http.cookiejar.MozillaCookieJar(COOKIE_FILE)

            for cookie in self.session.cookies:
                jar.set_cookie(cookie)

            jar.save(
                ignore_discard=True,
                ignore_expires=True,
            )

        except Exception as e:
            print(f"Warning: could not save cookies: {e}")

    def get_folder_content(
        self,
        folder_id,
        start_index=0,
        num_items=PAGE_SIZE,
    ):
        """
        Fetch one page of folder contents directly from the
        ResourceManager API.
        """
        time.sleep(REQUEST_DELAY)

        params = {
            "folderId": folder_id,
            "startIndex": start_index,
            "numItems": num_items,
            "sorting": "ItemName",
        }

        print(
            f"Fetching API: folderId={folder_id}, "
            f"startIndex={start_index}, numItems={num_items}"
        )

        try:
            response = self.session.get(
                API_URL,
                params=params,
                timeout=30,
            )

            response.raise_for_status()

        except requests.RequestException as e:
            print(
                f"ERROR: Request failed for folder "
                f"{folder_id}: {e}"
            )
            raise SystemExit(1)

        self.save_cookies()

        content_type = response.headers.get(
            "Content-Type",
            "",
        )

        if "json" not in content_type.lower():
            print(
                f"ERROR: Unexpected response type for folder "
                f"{folder_id}"
            )
            print(f"       Content-Type: {content_type}")
            print(f"       Response: {response.text[:1000]!r}")
            raise SystemExit(1)

        try:
            data = response.json()

        except ValueError:
            print(
                f"ERROR: Response was not valid JSON "
                f"for folder {folder_id}"
            )
            print(f"       Response: {response.text[:1000]!r}")
            raise SystemExit(1)

        if not isinstance(data, dict):
            print(
                f"ERROR: Unexpected JSON structure "
                f"for folder {folder_id}"
            )
            raise SystemExit(1)

        return data


class Indexer:
    def __init__(self):
        self.client = PortalSession()

        self.rows = []
        self.visited = set()

    def get_folder_items(self, folder_id):
        """
        Fetch every item in a folder, handling API pagination.
        """
        first_page = self.client.get_folder_content(
            folder_id=folder_id,
            start_index=0,
            num_items=PAGE_SIZE,
        )

        folder = first_page.get("folder")

        if not folder:
            print(
                f"ERROR: API response for folder "
                f"{folder_id} has no folder object"
            )
            raise SystemExit(1)

        total_count = first_page.get("totalCount", 0)

        items = list(first_page.get("items", []))

        print(
            f"       Folder: {folder.get('folderPath', '')}"
            f" | {len(items)}/{total_count} items"
        )

        # Fetch additional pages if necessary.
        start_index = len(items)

        while start_index < total_count:
            page = self.client.get_folder_content(
                folder_id=folder_id,
                start_index=start_index,
                num_items=PAGE_SIZE,
            )

            page_items = page.get("items", [])

            if not page_items:
                print(
                    f"WARNING: API returned no items at "
                    f"startIndex={start_index}"
                )
                break

            items.extend(page_items)
            start_index += len(page_items)

            print(
                f"       Progress: "
                f"{len(items)}/{total_count} items"
            )

        return folder, items

    def index_folder(
        self,
        folder_id,
        parent_id=None,
        parents=None,
    ):
        """
        Recursively index a folder.

        `parents` contains the names of all ancestor folders.
        """
        if parents is None:
            parents = []

        if folder_id in self.visited:
            print(
                f"Skipping already visited folder: "
                f"{folder_id}"
            )
            return

        self.visited.add(folder_id)

        folder, items = self.get_folder_items(folder_id)

        for item in items:
            item_id = item.get("itemId")
            title = item.get("itemName", "")
            is_folder = item.get("isFolder", False)

            if not title:
                print(
                    f"WARNING: Item {item_id} has no name"
                )
                continue

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
                f" | id={item_id}"
                f" | type={'folder' if is_folder else 'file'}"
            )

            if is_folder:
                self.index_folder(
                    folder_id=item_id,
                    parent_id=item_id,
                    parents=parents + [title],
                )

    def write_csv(self):
        OUTPUT_CSV.parent.mkdir(
            parents=True,
            exist_ok=True,
        )

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
        print(
            f"Wrote {len(self.rows)} entries "
            f"to {OUTPUT_CSV}"
        )


def main():
    indexer = Indexer()

    try:
        indexer.index_folder(
            folder_id=ROOT_FOLDER_ID,
        )
    finally:
        indexer.client.save_cookies()

    indexer.write_csv()


if __name__ == "__main__":
    main()