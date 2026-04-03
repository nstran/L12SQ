# Map Authoring

Huong dung hien tai:

- Client JAR cho ta `format map` va `logic collision`.
- Client JAR khong dam bao co san `du lieu toa do di duoc` cua tung map online.
- Vi vay, map thuc te cua server se duoc author lai trong `server/assets/maps/<map>/`.

## Hoa Lu

File chinh:

- `server/assets/maps/hoalu/hoalu.json`: grid, walkable range, spawn cell, room entry
- `server/assets/maps/hoalu/background.png`: anh nen map
- `server/assets/maps/hoalu/overlay.png`: layer phu
- `server/assets/maps/hoalu/tileset.png`: tileset ma client dang nap

Y nghia `hoalu.json`:

- `width`, `height`: kich thuoc grid map
- `tileSize`: kich thuoc moi o
- `walkableRanges`: nhung o co the di duoc
- `spawnCells`: o spawn uu tien
- `roomEntry`: vung click/vao khu

## Debug Overlay

De nhin nhanh o nao di duoc, chay:

```powershell
python d:\L12SQ\tools\render_map_overlay.py
```

Anh output:

- `server/assets/maps/hoalu/debug/hoalu_walkable_overlay.png`

Quy uoc mau:

- Green: o di duoc
- Yellow: o spawn
- Red: room entry

## Quy trinh dung map

1. Chinh `hoalu.json`.
2. Chay `render_map_overlay.py`.
3. Xem anh overlay co khop dia hinh khong.
4. Build va test lai trong KEmulator.
5. Lap lai cho toi khi di chuyen thuc te khop map.
