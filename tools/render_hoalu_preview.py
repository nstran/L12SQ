from pathlib import Path
from PIL import Image, ImageDraw


WIDTH = 240
HEIGHT = 160
SCALE = 4

SKY_TOP = (105, 211, 233)
SKY_BOTTOM = (156, 224, 220)
WATER_TOP = (103, 210, 232)
WATER_BOTTOM = (1, 159, 205)
CLOUD = (219, 238, 211)
EARTH_BASE = (239, 211, 157)
EARTH_MID = (237, 203, 118)
EARTH_SHADE = (226, 177, 114)
EARTH_HIGHLIGHT = (253, 238, 196)
GRASS_LIGHT = (159, 212, 90)
GRASS_BRIGHT = (216, 238, 212)
GRASS_MID = (89, 160, 51)
GRASS_DARK = (36, 109, 42)
FOLIAGE_LIGHT = (101, 161, 87)
FOLIAGE_MID = (94, 161, 52)
FOLIAGE_DARK = (36, 109, 42)
TRUNK = (143, 98, 46)
TRUNK_LIGHT = (180, 125, 65)
ROOF_RED = (181, 109, 92)
ROOF_GOLD = (229, 197, 109)
FLOWER_PINK = (255, 84, 145)


def lerp(a, b, t):
    return int(round(a + (b - a) * t))


def blend(c1, c2, t):
    return tuple(lerp(c1[i], c2[i], t) for i in range(3))


def fill_vertical_blend(draw, box, top, bottom):
    x0, y0, x1, y1 = box
    height = max(1, y1 - y0)
    for i in range(height):
        color = blend(top, bottom, i / max(1, height - 1))
        draw.line((x0, y0 + i, x1 - 1, y0 + i), fill=color)


def polygon_bounds(points):
    xs = [p[0] for p in points]
    ys = [p[1] for p in points]
    return min(xs), min(ys), max(xs), max(ys)


def render_earth_texture(base_img, points):
    mask = Image.new("L", (WIDTH, HEIGHT), 0)
    mask_draw = ImageDraw.Draw(mask)
    mask_draw.polygon(points, fill=255)

    texture = Image.new("RGBA", (WIDTH, HEIGHT), (0, 0, 0, 0))
    draw = ImageDraw.Draw(texture)
    bounds = polygon_bounds(points)
    draw.rectangle(bounds, fill=EARTH_BASE + (255,))

    for y in range(bounds[1] + 4, bounds[3] + 8, 11):
        offset = 1 if ((y // 11) % 2 == 0) else 10
        for x in range(bounds[0] + offset, bounds[2] + 12, 20):
            draw.line((x, y + 4, x + 6, y + 1), fill=EARTH_MID + (255,), width=1)
            draw.line((x + 6, y + 1, x + 12, y + 4), fill=EARTH_MID + (255,), width=1)
            draw.line((x, y + 4, x + 6, y + 8), fill=EARTH_MID + (255,), width=1)
            draw.line((x + 6, y + 8, x + 12, y + 4), fill=EARTH_MID + (255,), width=1)

    for y in range(bounds[1] + 4, bounds[3] + 8, 11):
        offset = 3 if ((y // 11) % 2 == 0) else 12
        for x in range(bounds[0] + offset, bounds[2] + 12, 20):
            draw.line((x, y + 4, x + 4, y + 2), fill=EARTH_HIGHLIGHT + (255,), width=1)

    for y in range(bounds[1] + 8, bounds[3] + 8, 22):
        draw.line((bounds[0], y, bounds[2], y + 2), fill=EARTH_SHADE + (100,), width=1)

    base_img.paste(texture, (0, 0), mask)


def draw_cloud(draw, x, y, w, h):
    draw.ellipse((x, y + 3, x + w // 2, y + h - 2), fill=CLOUD)
    draw.ellipse((x + w // 4, y, x + w // 4 + w // 2, y + h), fill=CLOUD)
    draw.ellipse((x + w // 2, y + 3, x + w // 2 + w // 3, y + h - 2), fill=CLOUD)


def draw_sky_curls(draw, x, y, w, h):
    curl = (233, 248, 251)
    for offset in range(0, w, 14):
        draw.arc((x + offset, y + ((offset // 14) % 2) * 4, x + offset + 10, y + h), 210, 330, fill=curl, width=1)


def draw_distant_island(draw, x, y, w, h):
    draw.ellipse((x, y + 8, x + w, y + 8 + h), fill=(216, 238, 212))
    draw.rectangle((x + 4, y + 6, x + w - 4, y + 9), fill=(196, 248, 126))


def draw_pagoda_island(draw, x, y, w, h):
    draw.ellipse((x + 4, y + h - 8, x + w - 4, y + h + 2), fill=(216, 238, 212))
    draw.polygon([(x + w // 2, y + 6), (x + 8, y + h - 6), (x + w - 8, y + h - 6)], fill=(230, 219, 178))
    draw.rectangle((x + w // 2 - 3, y + h - 12, x + w // 2 + 3, y + h - 2), fill=(153, 110, 76))
    draw.polygon([(x + w // 2, y), (x + w // 2 - 10, y + 7), (x + w // 2 + 10, y + 7)], fill=(202, 126, 104))
    draw.rectangle((x + 8, y + h - 10, x + w - 8, y + h - 7), fill=GRASS_LIGHT)


def draw_floating_spire(draw, x, y, w, h):
    draw.polygon([(x + w // 2, y), (x + 4, y + h - 10), (x + w - 4, y + h - 10)], fill=(230, 220, 176))
    draw.rectangle((x + 5, y, x + w - 5, y + 4), fill=GRASS_LIGHT)
    draw.rectangle((x + w // 2 - 2, y + 2, x + w // 2 + 2, y + h - 3), fill=(74, 193, 235))
    draw.rectangle((x + w // 2 - 1, y + 3, x + w // 2 + 1, y + h - 3), fill=(63, 151, 195))


def draw_roof(draw, x, y, w, h, alpha=False):
    # alpha ignored in preview; kept for shape parity
    draw.polygon([(x, y + h - 4), (x + w // 2, y), (x + w, y + h - 4)], fill=ROOF_RED)
    draw.polygon([(x + 3, y + h - 6), (x + w // 2, y + 2), (x + w - 3, y + h - 6)], fill=ROOF_GOLD)
    draw.rectangle((x + w // 2 - 5, y + h - 4, x + w // 2 + 5, y + h + 5), fill=(180, 128, 74))


def draw_grass_contour(draw, points):
    draw.line(points, fill=GRASS_BRIGHT, width=7)
    draw.line(points, fill=GRASS_LIGHT, width=4)
    draw.line(points, fill=GRASS_DARK, width=1)
    for (x1, y1), (x2, y2) in zip(points, points[1:]):
        dx, dy = x2 - x1, y2 - y1
        length = max(1.0, (dx * dx + dy * dy) ** 0.5)
        nx, ny = -dy / length, dx / length
        if ny > 0:
            nx, ny = -nx, -ny
        step = 5
        count = int(length // step)
        for i in range(count + 1):
            t = i / max(1, count)
            px = round(x1 + dx * t)
            py = round(y1 + dy * t)
            blade = 5 + (i % 3)
            tx = round(px + nx * blade)
            ty = round(py + ny * blade)
            draw.line((px, py, tx, ty), fill=GRASS_DARK, width=1)


def draw_banana_plant(draw, x, y):
    draw.rectangle((x + 20, y + 28, x + 28, y + 60), fill=TRUNK)
    draw.ellipse((x, y + 8, x + 22, y + 48), fill=FOLIAGE_MID)
    draw.ellipse((x + 12, y, x + 36, y + 44), fill=FOLIAGE_LIGHT)
    draw.ellipse((x + 26, y + 10, x + 46, y + 46), fill=FOLIAGE_MID)
    draw.ellipse((x + 26, y + 24, x + 36, y + 38), fill=(208, 178, 69))
    draw.ellipse((x + 32, y + 28, x + 42, y + 42), fill=(208, 178, 69))
    draw.ellipse((x + 36, y + 32, x + 46, y + 45), fill=(208, 178, 69))


def draw_flower_bush(draw, x, y):
    draw.ellipse((x, y + 4, x + 18, y + 14), fill=FOLIAGE_MID)
    draw.ellipse((x + 10, y, x + 26, y + 12), fill=FOLIAGE_LIGHT)
    draw.ellipse((x + 22, y + 4, x + 40, y + 14), fill=FOLIAGE_MID)
    for fx, fy in [(6, 6), (16, 3), (25, 7)]:
        draw.ellipse((x + fx, y + fy, x + fx + 4, y + fy + 4), fill=FLOWER_PINK)


def draw_rope_ladder(draw, x, y, h):
    draw.rectangle((x, y, x + 3, y + h), fill=(108, 72, 39))
    draw.rectangle((x + 9, y, x + 12, y + h), fill=(108, 72, 39))
    for row in range(y + 6, y + h, 10):
        draw.rectangle((x + 1, row, x + 11, row + 3), fill=(150, 106, 61))


def draw_hero_tree(draw, x, y):
    draw.rectangle((x + 18, y + 26, x + 30, y + 70), fill=TRUNK)
    draw.rectangle((x + 14, y + 40, x + 20, y + 60), fill=TRUNK)
    draw.rectangle((x + 28, y + 34, x + 34, y + 52), fill=TRUNK)
    draw.rectangle((x + 21, y + 28, x + 23, y + 66), fill=TRUNK_LIGHT)
    draw.rectangle((x + 25, y + 31, x + 27, y + 61), fill=TRUNK_LIGHT)
    draw.ellipse((x, y + 10, x + 28, y + 32), fill=FOLIAGE_MID)
    draw.ellipse((x + 14, y, x + 44, y + 24), fill=FOLIAGE_LIGHT)
    draw.ellipse((x + 30, y + 9, x + 56, y + 30), fill=FOLIAGE_MID)
    draw.ellipse((x + 12, y + 14, x + 46, y + 34), fill=FOLIAGE_LIGHT)


def render_preview():
    image = Image.new("RGB", (WIDTH, HEIGHT), SKY_TOP)
    draw = ImageDraw.Draw(image)

    fill_vertical_blend(draw, (0, 0, WIDTH, 108), SKY_TOP, SKY_BOTTOM)
    fill_vertical_blend(draw, (0, 92, WIDTH, HEIGHT), WATER_TOP, WATER_BOTTOM)
    draw.rectangle((0, 90, WIDTH, 94), fill=(241, 252, 255))

    draw_cloud(draw, 18, 18, 40, 15)
    draw_cloud(draw, 74, 18, 54, 18)
    draw_cloud(draw, 148, 20, 52, 17)
    draw_sky_curls(draw, 104, 26, 92, 28)

    draw_distant_island(draw, 18, 108, 54, 16)
    draw_distant_island(draw, 150, 104, 60, 18)
    draw_pagoda_island(draw, 26, 58, 46, 34)
    draw_pagoda_island(draw, 178, 70, 38, 28)
    draw_floating_spire(draw, 144, 44, 30, 50)
    draw_floating_spire(draw, 112, 66, 22, 32)
    draw_floating_spire(draw, 192, 62, 18, 24)
    draw_roof(draw, 76, 84, 38, 18)

    # Hanging island ceiling.
    top_points = [(0, 0), (240, 0), (240, 52), (220, 54), (198, 88), (178, 96), (160, 74), (140, 82), (120, 96), (96, 88), (72, 74), (46, 66), (20, 56), (0, 54)]
    render_earth_texture(image, top_points)
    draw_grass_contour(draw, [(0, 54), (20, 56), (46, 66), (72, 74), (96, 88), (120, 96), (140, 82), (160, 74), (178, 96), (198, 88), (220, 54), (240, 52)])
    draw.ellipse((118, 66, 128, 84), fill=FOLIAGE_LIGHT)
    draw.ellipse((150, 82, 162, 90), fill=FOLIAGE_LIGHT)

    # Bottom flat floor.
    floor_points = [(0, 134), (240, 134), (240, 160), (0, 160)]
    render_earth_texture(image, floor_points)
    draw_grass_contour(draw, [(0, 134), (240, 134)])

    # Waterfall and rocks.
    draw.rectangle((146, 63, 150, 106), fill=(74, 193, 235))
    draw.rectangle((148, 63, 149, 106), fill=(63, 151, 195))
    for x in [110, 122, 134, 168, 180]:
        draw.ellipse((x, 116, x + 8, 120), fill=(231, 212, 161))

    draw_banana_plant(draw, 4, 78)
    draw_flower_bush(draw, 102, 124)
    draw_flower_bush(draw, 132, 124)
    draw_rope_ladder(draw, 228, 12, 116)
    draw_hero_tree(draw, 188, 72)

    return image


def main():
    out_dir = Path(r"E:\L12SQ\ref\prototypes")
    out_dir.mkdir(parents=True, exist_ok=True)

    base = render_preview()
    base_path = out_dir / "hoalu_concept_v2_base.png"
    base.save(base_path)

    preview = base.resize((WIDTH * SCALE, HEIGHT * SCALE), Image.Resampling.NEAREST)
    preview_path = out_dir / "hoalu_concept_v2_preview.png"
    preview.save(preview_path)

    print(base_path)
    print(preview_path)


if __name__ == "__main__":
    main()
