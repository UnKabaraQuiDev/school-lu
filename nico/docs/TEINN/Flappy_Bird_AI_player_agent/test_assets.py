"""import os

project_root = os.path.dirname(os.path.abspath(__file__))
assets_path = os.path.join(project_root, "assets")

print(f"📁 Project root: {project_root}")
print(f"📁 Assets path: {assets_path}")
print(f"📁 Assets exists: {os.path.exists(assets_path)}")

if os.path.exists(assets_path):
    print("\n📄 Files in assets/:")
    for f in os.listdir(assets_path):
        full_path = os.path.join(assets_path, f)
        size = os.path.getsize(full_path)
        print(f"   • {f} ({size} bytes)")
else:
    print("\n❌ assets/ folder not found!")"""


# test_assets.py - updated path
import pygame
import os

pygame.init()
# 👇 Point to sprites subfolder
ASSETS = os.path.join(os.path.dirname(__file__), "assets", "sprites")

files = ["background-day.png", "base.png", "bluebird-upflap.png", "pipe-green.png"]
for f in files:
    path = os.path.join(ASSETS, f)
    if os.path.exists(path):
        img = pygame.image.load(path)
        print(f"✅ {f} loaded ({img.get_width()}x{img.get_height()})")
    else:
        print(f"❌ {f} NOT FOUND at {path}")