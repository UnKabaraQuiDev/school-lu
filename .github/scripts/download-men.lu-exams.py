#!/usr/bin/env python3

import csv
import subprocess
import time
from pathlib import Path
from urllib.parse import unquote

import requests


BASE_URL = "https://portal.education.lu"
DOWNLOAD_URL = (
    f"{BASE_URL}/DesktopModules/ResourceManager/API/Items/Download"
)

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
DOWNLOAD_DIR = GIT_DIR / "men.lu" / "downloads"
COOKIE_FILE = GIT_DIR / ".local" / "cookies.txt"

USER_AGENT = (
    "Mozilla/5.0 (X11; Linux x86_64; rv:145.0) "
    "Gecko/20100101 Firefox/145.0"
)

REQUEST_DELAY = 0.2


class PortalSession:
    def __init__(self):
        self.session = requests.Session()

        self.session.headers.update({
            "User-Agent": USER_AGENT,
            "Accept": "*/*",
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

    def download(self, file_id):
        time.sleep(REQUEST_DELAY)

        params = {
            "forceDownload": "true",
            "fileId": file_id,
            "tabId": "2319",
        }

        response = self.session.get(
            DOWNLOAD_URL,
            params=params,
            timeout=60,
            stream=True,
        )

        response.raise_for_status()

        self.save_cookies()

        return response


def get_filename(response, fallback):
    """
    Try to get the filename supplied by the server.

    Example:
        Content-Disposition: attachment; filename="foo.pdf"
    """
    content_disposition = response.headers.get(
        "Content-Disposition",
        "",
    )

    if "filename=" in content_disposition:
        filename = content_disposition.split(
            "filename=",
            1,
        )[1].strip().strip('"')

        return unquote(filename)

    return fallback


def safe_path(path):
    """
    Prevent absolute paths and path traversal from escaping
    DOWNLOAD_DIR.
    """
    path = Path(path)

    safe_parts = []

    for part in path.parts:
        if part in ("", ".", ".."):
            continue

        # Windows drive names / absolute paths.
        if len(part) == 2 and part[1] == ":":
            continue

        safe_parts.append(part)

    return Path(*safe_parts)

def download_file(client, file_id, combined_title):
    """
    Download one file using its API ID.

    CombinedTitle determines the complete relative path,
    including the filename.

    Existing files are skipped before making an HTTP request.
    """
    combined_path = safe_path(combined_title)
    output_file = DOWNLOAD_DIR / combined_path

    # Skip without making an HTTP request.
    if output_file.exists():
        print(f"       Already exists: {output_file}")
        return

    output_file.parent.mkdir(
        parents=True,
        exist_ok=True,
    )

    response = None

    try:
        print(
            f"Downloading: {combined_title}"
            f" | id={file_id}"
        )

        response = client.download(file_id)

        with open(output_file, "wb") as f:
            for chunk in response.iter_content(
                chunk_size=1024 * 1024
            ):
                if chunk:
                    f.write(chunk)

        print(f"       Saved: {output_file}")

    except requests.RequestException as e:
        print(
            f"ERROR: Failed to download "
            f"{combined_title} (id={file_id})"
        )
        print(f"       {e}")

        # Remove a partially downloaded file.
        if output_file.exists():
            output_file.unlink()

    finally:
        if response is not None:
            response.close()

def main():
    if not OUTPUT_CSV.exists():
        raise SystemExit(
            f"CSV does not exist: {OUTPUT_CSV}"
        )

    DOWNLOAD_DIR.mkdir(
        parents=True,
        exist_ok=True,
    )

    client = PortalSession()

    with open(
        OUTPUT_CSV,
        "r",
        newline="",
        encoding="utf-8-sig",
    ) as f:
        reader = csv.DictReader(f)

        required_columns = {
            "Id",
            "Title",
            "CombinedTitle",
        }

        missing = required_columns - set(
            reader.fieldnames or []
        )

        if missing:
            raise SystemExit(
                "CSV is missing required columns: "
                + ", ".join(sorted(missing))
            )

        for row in reader:
            file_id = row["Id"].strip()
            title = row["Title"].strip()
            combined_title = row["CombinedTitle"].strip()

            if not file_id:
                # Folder entries have no downloadable file ID in
                # the current index format.
                print(
                    f"Skipping folder: {combined_title}"
                )
                continue

            if not combined_title:
                print(
                    f"Skipping item {file_id}: "
                    "empty CombinedTitle"
                )
                continue

            filename = combined_title.rsplit("/", 1)[-1]

            if "." not in filename:
                print(
                    f"Skipping item {file_id}: "
                    "no file extension"
                )
                continue

            download_file(
                client=client,
                file_id=file_id,
                combined_title=combined_title,
            )

    client.save_cookies()


if __name__ == "__main__":
    main()