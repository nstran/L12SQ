# Hoa Lu Slice Spec

## Muc Dich

Tai lieu nay khoa `Hoa Lu` thanh mot map mau co the dung de:
- ve do hoa cho dung huong
- dong bo collision/spawn
- test movement
- clone pipeline sang map khac

## Reference Chinh

### Layout / Geometry

- [7687236765567_f354.jpg](e:/L12SQ/ref/derived/video_frames/7687236765567_f354.jpg)
- [7687236765567_f486.jpg](e:/L12SQ/ref/derived/video_frames/7687236765567_f486.jpg)
- [7687236765567_f619.jpg](e:/L12SQ/ref/derived/video_frames/7687236765567_f619.jpg)
- [gamecrop_montage.png](e:/L12SQ/ref/analysis/gamecrop_montage.png)

Dung de khoa:
- slope trai
- san tren
- ledge giua
- vach phai
- do cao san duoi

### Style / Palette

- [z7687428751492_5b09cebbb99876366abb4d18411184aa.jpg](e:/L12SQ/ref/raw/images/z7687428751492_5b09cebbb99876366abb4d18411184aa.jpg)
- [z7687428763519_bd8b52ca0b055ceb43b66c2fa1a7a407.jpg](e:/L12SQ/ref/raw/images/z7687428763519_bd8b52ca0b055ceb43b66c2fa1a7a407.jpg)
- [z7687428767472_9dd431a42b82c5807d591d64fd4b943d.jpg](e:/L12SQ/ref/raw/images/z7687428767472_9dd431a42b82c5807d591d64fd4b943d.jpg)

Dung de khoa:
- palette troi
- background xa
- mau cliff sang
- grass edge day va tuoi

## Layout Muc Tieu

### Silhouette

`Hoa Lu` v1 se co 4 khoi chinh:

1. `Bottom floor`
   San chinh cho spawn va movement co ban.

2. `Left ramp`
   Noi tu san duoi len san tren.

3. `Top floor`
   San tren chay ngang phia tren, la noi dat NPC/mob canh quan.

4. `Mid ledge`
   Mot ledge giua co chieu dai ro rang, noi vao khoi dat ben phai.

### Rule Hinh Hoc

- Khong co nen den, ho, hoac khoang trong vo ly.
- Moi ledge phai "an" vao mot khoi dat that.
- Cay va prop chi duoc dat tren mat co, khong duoc treo giua khong.
- Spawn player phai tren `bottom floor`, khong spawn trong slope.
- NPC guard dat tren `top floor`.

## Collision Spec

### Spawn

- Spawn mac dinh: tang duoi, lech trai tam man hinh.
- Khong spawn tai ledge giua.
- Spawn phai nam tren o co floor flag that.

### Walkable

Can co toi thieu 3 vung walkable:
- floor duoi
- floor tren
- ledge giua

Slope trai la vung noi giua floor duoi va floor tren.

### Test Cases

`Hoa Lu` chi dat khi pass du 4 test:

1. Vao map khong bi tut xuong nen.
2. Player dung yen tren san duoi.
3. Player di duoc qua trai/phai tren san duoi.
4. Neu input duoc mo khoa, player co the len/xuong theo slope va ledge.

## Art Spec

### Background

2 bien the duoc phep:

- `Rainy classic`
  Gan video cu, uu tien geometry.

- `Sunny polished`
  Giu geometry cua rainy, nhung palette va background xa lay tu bo sunny.

Rule:
- Khong tron geometry sunny neu chua xong rainy geometry.
- Co the dung sunny lam `polish pass`, khong dung lam `layout source`.

### Terrain

- Cliff than map: mau kem vang, hoa tiet mem, khong qua net nhu gach.
- Grass edge: day, tuoi, co highlight tren va dark line duoi.
- Shadow rat nhe, khong dark mode.

### Props

Uu tien it nhung dat dung cho:
- 1-2 cay tron
- 1 cay la dai hoac palm o canh
- 1 bui hoa nho neu can

Khong dua qua nhieu nha, cong, pagoda vao foreground cho toi khi layout on.

## Production Thu Tu

1. `Lock geometry`
   Sua [InstallResourceCatalog.java](e:/L12SQ/server/src/l12sq/server/runtime/InstallResourceCatalog.java) de silhouette map dung.

2. `Sync collision`
   Sua [WorldPackets.java](e:/L12SQ/server/src/l12sq/server/runtime/WorldPackets.java) de room entry, spawn, logic layer khop silhouette.

3. `Movement verify`
   Vao map, dung, di qua lai tren floor duoi.

4. `Art pass 1`
   Mau troi, cliff, grass, trees.

5. `Art pass 2`
   Background xa, polish, prop.

## Viec Se Lam Ngay Sau Tai Lieu Nay

Vong tiep theo chi tap trung vao 2 viec:

1. Khoa silhouette `Hoa Lu` cho dung geometry rainy.
2. Dong bo `WorldPackets` de player dung tren bottom floor do.
