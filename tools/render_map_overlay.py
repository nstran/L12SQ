from __future__ import annotations

import json
from pathlib import Path

from PIL import Image, ImageDraw


ROOT = Path(__file__).resolve().parents[1]
MAP_DIR = ROOT / "server" / "assets" / "maps" / "hoalu"
MAP_SPEC_PATH = MAP_DIR / "hoalu.json"
BACKGROUND_PATH = MAP_DIR / "background.png"
OUTPUT_PATH = MAP_DIR / "debug" / "hoalu_walkable_overlay.png"

WALKABLE_FILL = (52, 199, 89, 96)
SPAWN_FILL = (255, 204, 0, 180)
GRID_LINE = (255, 255, 255, 90)
LABEL_BG = (8, 17, 28, 170)
LABEL_FG = (255, 255, 255, 255)
ROOM_OUTLINE = (255, 59, 48, 220)


def load_spec() -> dict:
    return json.loads(MAP_SPEC_PATH.read_text(encoding="utf-8"))


def resolve_canvas_size(background: Image.Image, spec: dict) -> tuple[int, int]:
    width = int(spec["width"]) * int(spec["tileSize"])
    height = int(spec["height"]) * int(spec["tileSize"])
    return max(background.width, width), max(background.height, height)


def draw_grid(draw: ImageDraw.ImageDraw, spec: dict, canvas_size: tuple[int, int]) -> None:
    tile = int(spec["tileSize"])
    cols = int(spec["width"])
    rows = int(spec["height"])
    width, height = canvas_size

    for col in range(cols + 1):
        x = col * tile
        draw.line((x, 0, x, min(height, rows * tile)), fill=GRID_LINE, width=1)

    for row in range(rows + 1):
        y = row * tile
        draw.line((0, y, min(width, cols * tile), y), fill=GRID_LINE, width=1)


def draw_walkable(draw: ImageDraw.ImageDraw, spec: dict) -> None:
    tile = int(spec["tileSize"])

    for item in spec.get("walkableRanges", []):
        row = int(item["row"])
        start_col = int(item["startCol"])
        end_col = int(item["endCol"])
        x0 = start_col * tile
        y0 = row * tile
        x1 = (end_col + 1) * tile
        y1 = (row + 1) * tile
        draw.rectangle((x0, y0, x1 - 1, y1 - 1), fill=WALKABLE_FILL)

    for item in spec.get("spawnCells", []):
        row = int(item["row"])
        col = int(item["col"])
        x0 = col * tile
        y0 = row * tile
        x1 = (col + 1) * tile
        y1 = (row + 1) * tile
        draw.rectangle((x0 + 4, y0 + 4, x1 - 5, y1 - 5), fill=SPAWN_FILL)


def draw_room_entry(draw: ImageDraw.ImageDraw, spec: dict) -> None:
    room = spec.get("roomEntry", {})
    center_x = int(room.get("centerX", 0))
    center_y = int(room.get("centerY", 0))
    width = int(room.get("width", 0))
    height = int(room.get("height", 0))
    label = str(room.get("label", "Khu 1"))

    x0 = center_x - width // 2
    y0 = center_y - height // 2
    x1 = center_x + width // 2
    y1 = center_y + height // 2
    draw.rectangle((x0, y0, x1, y1), outline=ROOM_OUTLINE, width=2)

    text_box = (x0, max(0, y0 - 18), x0 + max(64, len(label) * 8), y0)
    draw.rectangle(text_box, fill=LABEL_BG)
    draw.text((text_box[0] + 4, text_box[1] + 2), label, fill=LABEL_FG)


def draw_legend(draw: ImageDraw.ImageDraw, spec: dict) -> None:
    map_name = spec.get("mapName", "Hoa Lu")
    tile_size = int(spec["tileSize"])
    cols = int(spec["width"])
    rows = int(spec["height"])
    lines = [
        f"Map: {map_name}",
        f"Grid: {cols} x {rows}",
        f"Tile: {tile_size}px",
        "Green = walkable",
        "Yellow = spawn",
        "Red = room entry",
    ]

    box_width = 132
    box_height = 16 + len(lines) * 12
    draw.rectangle((6, 6, 6 + box_width, 6 + box_height), fill=LABEL_BG)
    y = 12
    for line in lines:
        draw.text((12, y), line, fill=LABEL_FG)
        y += 12


def main() -> None:
    spec = load_spec()
    background = Image.open(BACKGROUND_PATH).convert("RGBA")
    canvas_width, canvas_height = resolve_canvas_size(background, spec)

    base = Image.new("RGBA", (canvas_width, canvas_height), (0, 0, 0, 0))
    base.alpha_composite(background, (0, 0))

    overlay = Image.new("RGBA", base.size, (0, 0, 0, 0))
    draw = ImageDraw.Draw(overlay)
    draw_walkable(draw, spec)
    draw_grid(draw, spec, base.size)
    draw_room_entry(draw, spec)
    draw_legend(draw, spec)

    result = Image.alpha_composite(base, overlay).convert("RGB")
    OUTPUT_PATH.parent.mkdir(parents=True, exist_ok=True)
    result.save(OUTPUT_PATH)
    print(OUTPUT_PATH)


if __name__ == "__main__":
    main()
