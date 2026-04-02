# Loan 12 Su Quan Online Restoration Plan

Ghi chu moi:
- Workspace da duoc reset gon lai vao 2026-04-02.
- Cac thu muc thu nghiem cu da bi xoa.
- Diem bat dau moi la `loan-12-su-quan.jar` va `client-base/extracted`.
- Server moi hien tai da quay ve dung cap cong goc `1236/1238`.
- Scope source moi hien tai: `register/login/auth`.

Trang thai:
- `[x]` Da xong
- `[~]` Dang lam / da co tien trien nhung chua on dinh
- `[ ]` Chua lam
- `[!]` Can quyet dinh / blocker

Muc tieu tong:
- Bien project hien tai thanh server online co the cho nhieu client ket noi.
- Di tu muc "client vao duoc game" toi "choi duoc gameplay co ban", sau do moi mo rong.
- Dung `loan-12-su-quan.jar` lam tai lieu tham chieu protocol/client flow, khong coi no la full source server.

## 0. Moc hien tai

- [x] Giai ma va decompile JAR goc de xem flow client.
- [x] Tao workspace moi `rebuild/` de phat trien lai.
- [x] Copy cac class client quan trong vao `rebuild/client-base` lam moc tham chieu.
- [~] Da tao source moi trong `rebuild/server/src` cho flow register/login/auth.
- [~] Da noi module auth/register moi nguoc vao `server/OfflineServer.java` theo kieu hybrid.
- [x] Xac dinh client online hardcode host/port trong `eh/km/dv/kq`.
- [x] Tao ban JAR LAN patch IP/port de tro ve may local.
- [x] Server Java tu viet da listen duoc `7236/7238`.
- [x] Login co ban da thong.
- [x] Da vao duoc world map `M99`.
- [x] Da click duoc `Hoa Lu`.
- [~] Client xin map/resource sau khi vao `Hoa Lu`, nhung van vo o khau parse/render resource map.
- [ ] Chua co gameplay online that.

## 1. Nguyen tac phat trien

- [ ] Moi thay doi protocol phai duoc ghi lai thanh tai lieu.
- [ ] Khong fix "cam tinh" neu chua co log client/server di kem.
- [ ] Moi milestone phai co ban chay duoc va test lai bang KEmulator.
- [ ] Uu tien vertical slice hoan chinh truoc khi mo rong tinh nang.

## 2. Kien truc muc tieu

### 2.1 Packet va protocol

- [ ] Tach parser/serializer packet ra khoi `OfflineServer.java`.
- [ ] Dinh nghia model cho `CMD`, `tag`, request, response.
- [ ] Tao packet logger doc de so sanh request/response.
- [ ] Tao packet replay tool cho cac flow quan trong.

### 2.2 Auth va session

- [x] Auth port `7236`.
- [x] Game port `7238`.
- [~] Redirect host/port local da patch.
- [~] Auth/register da duoc tach module hoa mot phan trong server hybrid.
- [ ] Session model ro rang giua auth va game.
- [ ] Xu ly reconnect, duplicate login, timeout session.

### 2.3 Character va persistence

- [~] Account dang luu JSON.
- [~] Character dang luu JSON.
- [ ] Chot flow "chua co nhan vat" va "da co nhan vat" dung ban online.
- [ ] Save/load full thong tin char.
- [ ] Chuyen persistence sang DB (`SQLite` truoc, sau do co the `MySQL/PostgreSQL`).

### 2.4 Map va resource

- [~] Co world map `M99`.
- [~] Co flow click `Hoa Lu`.
- [~] Co resource gia lap cho map.
- [ ] Chot format map packet dung voi client.
- [ ] Chot format resource packet dung voi client.
- [ ] Nap map thuc su khong crash.
- [ ] Teleport giua zone/room.

### 2.5 Gameplay

- [ ] NPC co ban.
- [ ] Inventory co ban.
- [ ] Equip item.
- [ ] Monster spawn.
- [ ] Combat co ban.
- [ ] Drop item.
- [ ] Quest/tutorial co ban.

### 2.6 Online multiplayer

- [ ] Hien thi nhieu nguoi cung map.
- [ ] Dong bo di chuyen.
- [ ] Chat co ban.
- [ ] Party/guild sau.
- [ ] Chong dup item / validate packet server-side.

### 2.7 Van hanh

- [ ] File config rieng cho host/port/env.
- [ ] Log rotation.
- [ ] Backup account/char.
- [ ] GM/admin command.
- [ ] Huong dan deploy LAN/public IP.

## 3. Milestone chi tiet

## Milestone A. Protocol foundation

Muc tieu:
- Client dang nhap on dinh.
- Vao world map on dinh.
- Vao duoc 1 map that, khong crash.

Checklist:
- [x] Tim ra host/port client online.
- [x] Patch JAR cho LAN.
- [ ] Tach `PacketReader`, `PacketWriter`, `TagReader`, `TagWriter`.
- [ ] Tao bang thong ke `CMD -> huong di -> tag quan trong`.
- [ ] Viet tai lieu flow:
  - [x] `CMD 4` auth
  - [x] `CMD 9` profile sync
  - [x] `CMD 29` world entry
  - [x] `CMD 11` room/map info
  - [x] `CMD 13` zone enter
  - [x] `CMD 6` resource
  - [ ] `CMD 15` map info chi tiet
  - [ ] `CMD 30/42` char/bootstrap theo flow online that
- [ ] So sanh resource client muon voi resource server dang tra.
- [ ] Giai quyet loi load map `Hoa Lu` den muc vao duoc scene.

## Milestone B. Character lifecycle

Muc tieu:
- Tao account.
- Tao nhan vat.
- Vao game.
- Thoat vao lai van giu du lieu.

Checklist:
- [x] Dang ky account co ban.
- [~] Login account co ban.
- [ ] Chot man tao nhan vat dung flow online.
- [ ] Luu class, avatar, gioi tinh, ten hien thi.
- [ ] Luu vi tri spawn.
- [ ] Luu stat co ban.
- [ ] Luu inventory rong mac dinh.
- [ ] Test full:
  - [ ] account moi
  - [ ] account cu
  - [ ] logout/login lai

## Milestone C. First playable map

Muc tieu:
- Vao `Hoa Lu`.
- Di lai duoc.
- Co NPC / object / portal co ban.

Checklist:
- [ ] Lay hoac phuc dung data map `Hoa Lu`.
- [ ] Dung resource tile/object dung format client.
- [ ] Xac dinh spawn point.
- [ ] Dung object layer / collision co ban.
- [ ] Dung portal sang map khac.
- [ ] Kiem tra cache RMS cua KEmulator va flow first-load/reload.

## Milestone D. Core gameplay alpha

Muc tieu:
- Choi duoc loop co ban.

Checklist:
- [ ] Inventory packet.
- [ ] Equip packet.
- [ ] Shop packet.
- [ ] NPC dialog packet.
- [ ] Monster list/update packet.
- [ ] Combat packet.
- [ ] HP/MP/stat sync.
- [ ] Drop/loot packet.
- [ ] Quest huong dan dau game.

## Milestone E. Multiplayer alpha

Muc tieu:
- Hai client cung online thay nhau.

Checklist:
- [ ] Session manager.
- [ ] Player entity manager.
- [ ] Broadcast player enter/leave map.
- [ ] Broadcast move/action.
- [ ] Chat local/world.
- [ ] Xu ly reconnect an toan.

## Milestone F. Production readiness

Muc tieu:
- Chay on dinh tren LAN/public test.

Checklist:
- [ ] DB that.
- [ ] Migration data tu JSON sang DB.
- [ ] Config theo env.
- [ ] Backup script.
- [ ] Monitoring/log errors.
- [ ] Admin command / GM panel toi thieu.
- [ ] Huong dan mo port/firewall/NAT.

## 4. Viec can lam ngay

Uu tien 1:
- [x] Tao workspace rebuild moi tu JAR decompiled.
- [ ] Refactor `server/OfflineServer.java` thanh nhieu class nho hon.
- [ ] Tao thu muc `server/docs` de ghi packet notes.
- [ ] Tao tai lieu `protocol-cmd-notes.md`.
- [ ] Chot flow `Hoa Lu` vao scene thanh cong.

Uu tien 2:
- [ ] Chot create-character flow dung client that.
- [ ] Chuyen account/char storage sang `SQLite`.
- [ ] Tao `server-config.json` hoac `.properties`.

Uu tien 3:
- [ ] Bat dau bo packet inventory/NPC/shop.
- [ ] Tao map thu hai sau `Hoa Lu`.

## 5. Backlog chia nho

### 5.1 Logging va tooling

- [ ] Luu raw packet hex theo session.
- [ ] Gan timestamp va username vao log.
- [ ] Tao script clear RMS test nhanh.
- [ ] Tao script start/restart server nhanh.

### 5.2 Client research

- [x] Decompile bang CFR.
- [x] Copy nhom class nen sang `rebuild/client-base`.
- [ ] Cross-check voi decompile thu muc `decompiled`.
- [ ] Danh dau class chinh:
  - [x] `dv`, `kq`, `kw`
  - [x] `oe`, `ny`, `oj`, `ok`, `ox`, `oy`
  - [~] `gw`, `nu`, `dd`, `lz`
- [ ] Lap bang "class nao phu trach man nao".

### 5.3 Data va DB

- [ ] Thiet ke bang `accounts`.
- [ ] Thiet ke bang `characters`.
- [ ] Thiet ke bang `items`.
- [ ] Thiet ke bang `maps`.
- [ ] Thiet ke bang `sessions`.

### 5.4 Bao mat va on dinh

- [ ] Validate packet input.
- [ ] Gioi han size packet.
- [ ] Chot hash/password flow.
- [ ] Chan crash do tag thieu/du lieu loi.

## 6. Dinh nghia "Alpha choi duoc"

Ban alpha duoc xem la dat khi:
- [ ] Client login on dinh.
- [ ] Tao duoc nhan vat moi.
- [ ] Vao duoc `Hoa Lu`.
- [ ] Di chuyen duoc trong map.
- [ ] Thay duoc it nhat 1 NPC.
- [ ] Mo duoc inventory.
- [ ] Danh duoc 1 quai.
- [ ] Nhat duoc 1 vat pham.
- [ ] Logout/login lai khong mat du lieu.
- [ ] Co the cho 2 client vao cung luc.

## 7. Nhat ky quyet dinh

- [x] Su dung JAR goc lam tai lieu tham chieu, khong coi la full server source.
- [x] Giu huong phat trien server emulator bang Java de bam sat project hien tai.
- [x] Uu tien vertical slice truoc: login -> world map -> Hoa Lu -> first playable scene.
- [ ] Quyet dinh thoi diem chuyen JSON -> SQLite.
- [ ] Quyet dinh muc tieu ngan han: offline on dinh truoc hay online alpha truoc.

## 8. Cach cap nhat file nay

Nguyen tac:
- Xong viec nao thi tick viec do.
- Viec dang lam nhung chua on dinh thi de `[~]`.
- Neu phat sinh blocker, them dong `[!]` ngay duoi muc lien quan.
- Neu co milestone moi, them xuong cuoi file, khong xoa lich su cu.
