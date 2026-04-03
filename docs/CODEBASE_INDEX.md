# Codebase Index

Ngay cap nhat: 2026-04-03

## 1. Tong quan workspace hien tai

Workspace sau lan reset hien tai thuc te chi con 3 thu muc chinh:

- `docs/`: plan, baseline, ghi chu reset workspace.
- `server/`: server Java viet lai.
- `tools/`: cong cu doi chieu JAR va cac dump `javap`.

File goc de doi chieu:

- `loan-12-su-quan.jar`: client J2ME goc.

Luu y:

- `docs/WORKSPACE_RESET.md` va `ONLINE_GAME_PLAN.md` nhac toi `client-base/extracted`, nhung thu muc nay hien khong con trong workspace thuc te.
- Do do, nguon doi chieu client hien tai trong repo la `loan-12-su-quan.jar` va cac file `tools/*.javap.txt`.

## 2. Muc tieu ky thuat cua repo

Repo nay chua phai server game day du. No dang la mot emulator Java nho, muc tieu ngan han la lam cho client JAR cu:

1. dang ky / dang nhap duoc,
2. auth duoc qua 2 cong `1236/1238`,
3. vao duoc world map `M99`,
4. mo duoc flow tao nhan vat,
5. click duoc hotspot `Hoa Lu`,
6. chua crash som khi client doi packet/resource co ban.

Theo `ONLINE_GAME_PLAN.md`, muc tieu dai hon la di tu vertical slice `login -> world map -> Hoa Lu` den alpha multiplayer co gameplay co ban.

## 3. Ban do thu muc

### Root

- `ONLINE_GAME_PLAN.md`: roadmap phuc hoi, milestone, backlog va tinh trang da lam.
- `loan-12-su-quan.jar`: client tham chieu.

### `docs/`

- `JAR_BASELINE.md`: fact quan trong lay truc tiep tu JAR.
- `WORKSPACE_RESET.md`: nhat ky reset workspace.
- `CODEBASE_INDEX.md`: tai lieu index nay.

### `server/`

- `run-server.ps1`: script compile/run.
- `README.md`: ghi chu cu, da loi thoi mot phan.
- `src/l12sq/server/...`: toan bo source server hien tai.
- `build/`: output compile sinh ra boi script.
- `data/`: du lieu JSON luc runtime.

### `tools/`

- `PatchClientHost.java`: patch host trong constant pool cua class file trong JAR.
- `PacketProbe.java`: dung reflection de bom packet gia vao parser client `kw`.
- `*.javap.txt`: dump bytecode cua nhom class client quan trong.

## 4. Kien truc server hien tai

### Entry point

- `server/src/l12sq/server/Main.java`
  - Tao `ServerConfig.defaults()`.
  - Tao `JsonAccountStore`.
  - Tao `JsonCharacterStore`.
  - Tao `GameServer` va `start()`.

### Config

- `server/src/l12sq/server/config/ServerConfig.java`
  - Config dang hardcode:
    - host quang ba: `192.168.1.226`
    - auth port: `1236`
    - game port: `1238`
    - duong dan data la path tuyet doi `d:\L12SQ\...`
  - Chua doc tu file/env.

### Runtime core

- `server/src/l12sq/server/runtime/GameServer.java`
  - Day la class trung tam va dang om gan nhu toan bo protocol/game flow.
  - Mo 2 `ServerSocket`, moi ket noi tao 1 thread rieng.
  - Doc packet bang `TlvCodec`.
  - Dieu phoi theo `CMD`.
  - Tu build packet response TLV.
  - Tu sinh install resource placeholder.
  - Tu luu/nap character JSON.

### Network codec

- `server/src/l12sq/server/net/TlvCodec.java`
  - `readAuthRequest(...)`: auth packet format.
  - `readGameRequest(...)`: game packet format.
  - `parseTags(...)`: parser TLV `tag + int length + value`.
  - `sendEmpty`, `sendSingleTag`, `sendPacket`.
  - Helpers `tagString`, `tagHex`, `randomSalt`.

- `server/src/l12sq/server/net/PacketRequest.java`
  - record nho chua `command`, `payload`, `tags`, `payloadLength`, `subCount`.

### Auth

- `server/src/l12sq/server/auth/AuthService.java`
  - Lop business auth rat mong.
  - Register luu thang `passwordHex`.
  - Login dang cho phep fallback rat rong:
    - neu hash trung thi OK,
    - neu tai khoan co hash `OFFLINE` thi OK,
    - neu `passwordHex` khong rong thi cung co the OK.
  - Nghia la auth hien tai la tam de client vao duoc flow.

- `server/src/l12sq/server/auth/CaptchaFactory.java`
  - Sinh PNG captcha don gian bang AWT.
  - Hien tai text captcha hardcode la `1234`.

### Persistence

- `server/src/l12sq/server/storage/AccountStore.java`
- `server/src/l12sq/server/storage/CharacterStore.java`
  - Interface hoa storage, de sau nay doi sang DB.

- `server/src/l12sq/server/storage/JsonAccountStore.java`
  - Luu account trong 1 file JSON object don gian.
  - Parser/saver viet tay bang regex/string builder.
  - Username duoc normalize lowercase.

- `server/src/l12sq/server/storage/JsonCharacterStore.java`
  - Moi nhan vat la 1 file JSON theo username.
  - Hien chi luu:
    - username
    - gender
    - element
    - hairOptionId
    - hairColorId
    - faceOptionId
    - skinOptionId
    - skinColorId

## 5. CMD map ma server hien tai dang xu ly

Trong `GameServer.route(...)`, server dang xu ly cac `CMD` sau:

- `1`: ping/ack rong.
- `2`, `3`: salt challenge.
- `4`: auth.
- `5`: version/install manifest.
- `6`: install resource request/chunk.
- `8`: create character submit.
- `9`: profile sync/bootstrap.
- `11`: world map hotspot data.
- `29`: map join.
- `30`: no-character bootstrap ack.
- `42`: start button ack.
- `130`: registration captcha.
- `131`: registration submit.

Nhung dieu nay xac nhan duoc tu client dump `tools/kw.javap.txt`, noi `kw` la packet dispatcher game-side.

## 6. Luong chay thuc te hien tai

### Auth channel `1236`

Theo `docs/JAR_BASELINE.md`, JAR goc dung auth port `1236`. Server hien tai emu cac buoc:

1. Client gui `CMD 2/3` de xin salt.
2. Client co the gui `CMD 130` de lay captcha dang ky.
3. Client gui `CMD 131` de dang ky.
4. Client gui `CMD 4` de auth.
5. Neu OK, server tra `CMD 1` rong roi `CMD 2` tag `3 = advertisedHost`.

### Game channel `1238`

Theo `tools/kq.javap.txt`, class `kq` cua client:

- lay host tu `km.a`,
- dung port `1238`.

Luong game-side ma server dang gia lap:

1. `CMD 5`: gui install manifest lan dau.
2. `CMD 4`: auth tren game port.
3. `CMD 9`: profile sync.
4. Neu da co char:
   - load JSON,
   - gui `CMD 9` kieu character profile,
   - gui `CMD 29` de vao `M99`.
5. Neu chua co char:
   - gui `CMD 9` kieu start profile,
   - danh dau `awaitingCharacterCreation = true`,
   - gui `CMD 8` de hien option tao nhan vat.
6. Khi client gui `CMD 8`, server parse option da chon, luu JSON, gui lai `CMD 9`, roi `CMD 29`.
7. Khi client gui `CMD 11`, server gui 1 hotspot `Hoa Lu` tren `M99`.
8. `CMD 30` va `CMD 42` dang duoc ack de giu flow bootstrap/start khong gãy.

## 7. Phan tich `GameServer` theo cum chuc nang

### 7.1 Listener va session

- `start()` mo 2 listener thread.
- `listen(...)` moi client tao mot thread rieng.
- `SessionContext` dang giu:
  - `authenticated`
  - `awaitingCharacterCreation`
  - `createCharacterOptionsSent`
  - `username`
  - resource preload state
  - map hien tai

Hien chua co:

- session manager dung nghia,
- bridge giua auth va game channel,
- timeout,
- reconnect,
- duplicate login,
- danh sach player online.

### 7.2 Profile va character

`handleProfileSync(...)` dang la diem quan trong nhat cua game bootstrap.

Nhanh `co character`:

- load tu `CharacterStore`,
- build packet profile qua `buildCharacterProfile(...)`,
- set map hien tai `M99`,
- gui `CMD 29`.

Nhanh `chua co character`:

- build packet qua `buildStartProfile(...)`,
- bat co `awaitingCharacterCreation`,
- gui option create-char qua `sendCreateCharacterOptions(...)`.

Co mot chi tiet quan trong:

- `buildNoCharacterProfile(...)` co ton tai, va ro rang duoc viet de phuc vu no-character branch.
- Nhung hien tai code khong goi den method nay.
- Nghia la flow "chua co nhan vat" dang duoc gia lap bang `buildStartProfile(...)`, khong phai bang packet no-character dung nghia.

Day la dau hieu ro rang rang flow tao nhan vat hien tai moi la workaround theo huong "du client di tiep", chua phai protocol cuoi cung.

### 7.3 Install resource

`handleVersionCheck(...)` va `handleInstallResourceRequest(...)` gia lap resource install.

Resource hien tai duoc tao trong memory boi:

- `createInstallResources()`
- `metadataBytes(...)`
- `spriteSheetBytes(...)`
- `drawSpriteFrame(...)`

No khong nap resource that tu game cu. No ve PNG placeholder/sprite rat co ban chi de parser va UI client khong vo ngay.

### 7.4 World map

`sendWorldMapHotspots(...)` hien chi gui:

- map `M99`
- 1 marker `Hoa Lu`

Toa do va bounding box dang hardcode.

### 7.5 Logging

`logRequest(...)` in:

- `CMD`
- payload length
- so tag
- mot so tag noi dung

No huu ich cho reverse-engineering, nhung chua:

- dump hex raw,
- gan session id,
- gan username chac chan,
- ghi file log de so sanh qua nhieu lan chay.

## 8. Doi chieu voi client JAR / dump

### Fact chac chan tu repo hien tai

- `docs/JAR_BASELINE.md` ghi ro:
  - auth port `1236`
  - game port `1238`
  - host auth tu `eh.z`
  - host game tu `km.a`
  - MIDlet la `com.mg.sq.SQMIDlet`

- `tools/kq.javap.txt` xac nhan:
  - constructor `kq` lay `km.a` lam danh sach host
  - dat port `1238`

- `tools/kw.javap.txt` xac nhan:
  - `CMD 8` vao method `f(ks)` de parse create-character options.
  - `CMD 9` re nhanh theo tag `134`:
    - neu `tag 134 > 0` thi parse nhu character profile qua `a(ks)`,
    - neu khong thi vao `b(ks)` cho branch khac.
  - `CMD 29` doc `tag 9`, `20`, `21` de notify map join.

- `tools/PacketProbe.java` duoc viet de test offline 2 packet quan trong:
  - payload `CMD 8`
  - payload `CMD 9`

Dieu nay cho thay nguoi phuc hoi truoc day da dung cach rat dung:

- lay parser that cua client lam "oracle",
- roi bom packet gia de xem client chap nhan hay vo.

## 9. Tinh trang du lieu runtime

Hien co:

- `server/data/accounts.json`
  - dang co 1 account `nst`.
- chua thay file nhan vat trong `server/data/chars`.

Storage hien tai chi de bootstrap flow, chua du cho game online thuc:

- khong co inventory,
- khong co vi tri,
- khong co stat day du,
- khong co sessions,
- khong co items,
- khong co map state.

## 10. Khoang trong ky thuat lon nhat

### Protocol

- Chua tach protocol layer khoi `GameServer`.
- Chua co bang tai lieu `CMD -> tag -> nghia`.
- Chua co replay/log raw packet.

### Auth va session

- Auth dang tam va khong an toan.
- Chua co session xuyen suot tu auth sang game.
- Chua chan login trung.

### Character lifecycle

- No-character branch chua dung packet final.
- Chua co create-character dung nghia theo online flow that.
- Character schema rat nghèo.

### Map/resource

- Resource dang la placeholder render luc runtime.
- Chua co map data that.
- `Hoa Lu` hien moi la hotspot tren world map, chua phai scene gameplay that.

### Multiplayer

- Hoan toan chua co entity manager, broadcast, movement sync, chat, combat.

## 11. Diem rui ro / no ky thuat can nho

1. `ServerConfig` dang hardcode host va path tuyet doi. Di chuyen may la vo ngay.
2. `GameServer` la monolith gan 1 file om network, auth, profile, resources, map bootstrap.
3. Thread model la `1 ket noi = 1 thread`, chua co gioi han hoac quan ly tai nguyen.
4. `AuthService.canLogin(...)` dang cho phep fallback qua rong, khong dung de van hanh that.
5. `buildNoCharacterProfile(...)` dang la dead code, rat co the la dau moi cho no-character flow dung.
6. `pendingResourceAnnouncements` dang co state machine do dang, nhung khong thay noi nao nap du lieu vao set nay.
7. `sendCreateCharacterOptions(...)` dang gui option/hinh gia lap; ten option va mapping sprite chua chac da trung game goc.
8. JSON parser/save viet tay bang regex de bi mong manh neu schema mo rong.

## 12. Nhan dinh kien truc hien tai

Neu nhin dung ban chat, repo nay dang o giai doan:

- da dung huong reverse-engineering,
- da co mot vertical slice bootstrap co the chay,
- nhung protocol va content deu chua "that",
- phan online hien tai la emulator tam de mo khoa client flow.

Noi cach khac:

- Day la nen rat tot de phuc hoi tiep.
- Nhung moi quyet dinh tiep theo nen dua tren packet log va parser client that, khong nen doan cam tinh.

## 13. Nguon su that nen uu tien ve sau

Thu tu uu tien khi co mau thuan thong tin:

1. `loan-12-su-quan.jar`
2. `tools/*.javap.txt`
3. `tools/PacketProbe.java`
4. `docs/JAR_BASELINE.md`
5. `ONLINE_GAME_PLAN.md`
6. source server hien tai

Ly do:

- JAR va parser client moi quyet dinh packet nao dung.
- Server hien tai co nhieu cho la workaround de client di tiep, nen khong nen mac dinh do la protocol chuan.

## 14. Huong tu van tiep theo

Tu bay gio co the tu van theo 4 huong ro rang:

1. Reverse protocol theo tung `CMD`.
2. Refactor server de de mo rong.
3. Chot lai flow `create-character` dung nghia.
4. Phuc hoi map/resource `Hoa Lu` de vao scene that.

Neu bat dau lam tiep, uu tien ky thuat hop ly nhat la:

- lap bang `CMD 4/8/9/11/29/30/42`,
- xac dinh dung branch `CMD 9` no-character,
- log raw packet client khi click vao `Hoa Lu`,
- tach `GameServer` thanh `AuthChannel`, `GameChannel`, `PacketRouter`, `ProfileService`, `InstallResourceService`.
