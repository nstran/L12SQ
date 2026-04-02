# JAR Baseline

Source of truth hien tai:
- `d:\L12SQ\loan-12-su-quan.jar`

Xac nhan truc tiep tu JAR:
- `dv` dung auth connection port `1236`
- `kq` dung game connection port `1238`
- `dv` lay host tu `eh.z`
- `kq` lay host tu `km.a`
- Client la MIDlet `com.mg.sq.SQMIDlet`

Pham vi build server giai doan dau:
1. `CMD 5` version
2. `CMD 2/3` salt challenge
3. `CMD 130` registration captcha
4. `CMD 131` registration submit
5. `CMD 4` auth tren port `1236`
6. `CMD 4` game auth tren port `1238`

Nguyen tac:
- Chi dung `1236` va `1238`
- Host quang ba mac dinh: `192.168.1.226`
- Moi flow moi them vao server phai doi chieu lai voi JAR goc
