from PIL import Image
import os

SOURCE_IMG = r'd:\L12SQ\ref\raw\images\image copy.png'
TARGET_DIR = r'd:\L12SQ\server\assets\maps\hoalu'
TARGET_IMG = os.path.join(TARGET_DIR, 'background.png')
TARGET_OVERLAY = os.path.join(TARGET_DIR, 'overlay.png')

# Target Tiled Size (Multiples of 32)
# 1065 -> 1088 (34 tiles)
# 263 -> 256 (8 tiles) - crop bottom or scale? 
# User says "hợp lý nhất", I will scale height to 256 and pad width to 1088.

W_TILES = 34
H_TILES = 5
TILE_SIZE = 32

TARGET_W = W_TILES * TILE_SIZE
TARGET_H = H_TILES * TILE_SIZE

def process():
    if not os.path.exists(SOURCE_IMG):
        print(f"Error: {SOURCE_IMG} not found")
        return

    img = Image.open(SOURCE_IMG).convert("RGB")
    orig_w, orig_h = img.size
    
    # Scale height to 160, maintain aspect ratio for width then pad
    scale = TARGET_H / orig_h
    new_w = int(orig_w * scale)
    img_scaled = img.resize((new_w, TARGET_H), Image.Resampling.LANCZOS)
    
    # Create target canvas (1088x160)
    final_img = Image.new("RGB", (TARGET_W, TARGET_H), (0, 0, 0))
    # Place scaled image at (0, 0)
    final_img.paste(img_scaled, (0, 0))
    
    # CONVERT TO 8-BIT INDEXED COLOR (CRITICAL FOR J2ME MEMORY)
    final_img = final_img.convert("P", palette=Image.Palette.ADAPTIVE)
    
    # Save background
    os.makedirs(TARGET_DIR, exist_ok=True)
    final_img.save(TARGET_IMG, optimize=True)
    print(f"Saved optimized background to {TARGET_IMG} ({TARGET_W}x{TARGET_H})")
    
    # Create empty transparent overlay
    overlay = Image.new("RGBA", (TARGET_W, TARGET_H), (0, 0, 0, 0))
    overlay.save(TARGET_OVERLAY)
    print(f"Saved empty overlay to {TARGET_OVERLAY}")

if __name__ == "__main__":
    process()
