# Graphics And Map Plan

## Muc Tieu

Dung lai pipeline do hoa va map theo kieu co the san xuat lap lai, khong va tung anh mot cach cam tinh.

Muc tieu gan:
- Hoan thanh `Hoa Lu` nhu mot vertical slice.
- Map phai dep, lien khoi, nhan vat dung dung mat dat, co the di chuyen tren layout do.
- Sau khi `Hoa Lu` on dinh, clone pipeline sang map khac.

Muc tieu xa:
- Tach ro `protocol`, `map data`, `art asset`, `collision`.
- Co kha nang thay style hoac tao bien the map ma khong pha gameplay.

## Nguyen Tac

1. `Gameplay first, art second, polish third`
   Gameplay va collision phai dung truoc. Art dep nhung sai mat dat se lam client nhin rat gia.

2. `Mot map = mot layout logic ro rang`
   Khong ve tranh nen roi co gang ep player dung len sau. Layout phai duoc xay tu san choi, slope, ledge, spawn.

3. `Reference-driven`
   Moi quyet dinh ve bo cuc, mau, thoi tiet, prop deu phai dua tren anh/video trong [REFERENCE_INDEX.md](e:/L12SQ/ref/REFERENCE_INDEX.md).

4. `Vertical slice truoc`
   Chi can mot map dung chuan, sau do moi nhan ra cac map khac.

## Art Direction Hien Tai

Co 2 nhom reference ro rang:

- `Rainy Hoa Lu`
  Anh/video cu cho thay layout dat vang, co xanh day, troi xam, mua, cay thap.
  Nhom nay rat co gia tri cho `collision`, `slope`, `ledge`, `spawn`.

- `Sunny Fantasy`
  Anh moi cho thay bai canh dep hon, trong sang hon, co dao xa, nuoc, may, pagoda, cay nhiet doi.
  Nhom nay co gia tri cho `palette`, `background`, `atmosphere`.

Quyet dinh lam viec:
- `Geometry/layout` se bam `Rainy Hoa Lu`.
- `Color/background polish` co the muon mot phan tu `Sunny Fantasy`.
- Khong tron 2 mood trong cung mot ban build neu chua co chu dich ro rang.

## Vertical Slice Hoa Lu

### Definition Of Done

`Hoa Lu` chi duoc xem la xong khi dat du 6 dieu kien:

1. Player spawn tren mat dat hop le.
2. Player di duoc tren san duoi, ledge giua, slope trai.
3. NPC/mob khong bi troi hoac nam sai layer.
4. Map khong co khoang den, rach nen, hoac prop bi ghep vo ly.
5. Background, terrain, prop va collision khop nhau.
6. Cung layout do co the duoc tai lai on dinh sau khi vao map nhieu lan.

### Layout Muc Tieu

Ban `Hoa Lu` mau se co:
- 1 mat san duoi lien khoi cho spawn va gameplay co ban.
- 1 slope trai de noi len tang tren.
- 1 ledge giua co chieu dai ro rang.
- 1 vach phai de khoa bo cuc.
- 2-3 prop cay/bui dat dung cho de tao chieu sau.

## Asset Pipeline

### 1. Raw Reference

Tat ca anh va video goc nam trong:
- [ref/raw/images](e:/L12SQ/ref/raw/images)
- [ref/raw/videos](e:/L12SQ/ref/raw/videos)

Khong sua truc tiep file raw.

### 2. Derived Reference

Tat ca frame va crop nam trong:
- [ref/derived/video_frames](e:/L12SQ/ref/derived/video_frames)
- [ref/derived/crops](e:/L12SQ/ref/derived/crops)
- [ref/analysis](e:/L12SQ/ref/analysis)

Dung nhom nay de:
- so bo cuc
- do ty le
- doc slope/ledge
- xac dinh spawn va collision

### 3. Runtime Asset

Tam thoi asset van duoc sinh bang code trong:
- [InstallResourceCatalog.java](e:/L12SQ/server/src/l12sq/server/runtime/InstallResourceCatalog.java)

Ve sau nen tach thanh 4 khoi:
- `background`
- `terrain`
- `props`
- `sprites`

## Map Data Pipeline

Tam thoi layout dang bi chia o 2 noi:
- art trong [InstallResourceCatalog.java](e:/L12SQ/server/src/l12sq/server/runtime/InstallResourceCatalog.java)
- logic/spawn trong [WorldPackets.java](e:/L12SQ/server/src/l12sq/server/runtime/WorldPackets.java)

Huong chuan hoa tiep theo:

1. Chot grid logic cho `Hoa Lu`
   10x8 hien tai chi la tam.

2. Ve layout theo grid
   Moi slope, ledge, floor phai map duoc vao grid.

3. Dong bo 3 lop:
   art silhouette
   collision layer
   spawn/room entry

4. Chi khi 3 lop nay khop nhau moi bat dau polish visual.

## Milestone

### M1. Reference Freeze

- Gom va index toan bo media.
- Danh dau media nao dung cho `layout`, media nao dung cho `style`.
- Chon 1 huong cho `Hoa Lu v1`.

### M2. Layout Lock

- Vach ra silhouette cuoi cung cua `Hoa Lu`.
- Khoa:
  spawn
  slope trai
  ledge giua
  floor duoi
  vach phai

### M3. Collision And Movement

- Sua [WorldPackets.java](e:/L12SQ/server/src/l12sq/server/runtime/WorldPackets.java) de grid logic khop silhouette.
- Test player dung dung dat va di duoc tren cac tang.

### M4. Visual Pass 1

- Mau troi, nuoc, dat, co.
- Cay, bui, prop chinh.
- Bo nhung chi tiet lam roi bo cuc.

### M5. Visual Pass 2

- Chieu sau nen xa.
- Detail edge, pattern dat, shadow nhe.
- NPC/prop placement.

### M6. Clone Pipeline

- Tai su dung pipeline cho map 2.
- Khong copy pixel mu quang; chi copy cong thuc:
  layout -> collision -> asset -> polish

## Cach Lam Viec Moi Vong

Moi vong tiep theo nen theo format nay:

1. Chon 1 muc duy nhat
   Vi du: `sua slope trai`, `sua ledge giua`, `doi palette dat`.

2. Sua code va bump resource version.

3. Restart server va test.

4. Chup lai man hinh ket qua.

5. Chot `giu` hoac `bo`.

Khong sua 5 thu cung luc neu chua co layout lock.

## Viec Toi Se Lam Tiep

Thu tu hop ly nhat:

1. Khoa layout `Hoa Lu` theo reference rainy.
2. Dong bo collision/spawn de player dung dung dat.
3. Sau do moi polish background theo huong sunny neu anh muon.

## Viec Anh Co The Ho Tro

- Tiep tuc bo anh/video vao `ref/raw`.
- Neu co anh map cu full man hinh, uu tien gui.
- Neu co anh luc nhan vat dang dung tren cac tang khac nhau, rat quy.
- Neu co cache map cu hoac RMS, do la du lieu gia tri nhat.
