/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.microedition.io.SocketConnection
 */
import javax.microedition.io.SocketConnection;

public final class kq
implements Runnable,
km {
    private short k;
    private static kq l = null;
    private static final Object m = new Object();
    private boolean n;
    private final s o;
    private SocketConnection p = null;
    private kw q = null;
    private kx r = null;
    private String[] s = null;
    private int t = 1238;
    int c = 3;
    private int u = 0;
    private kp v;
    private ko w;
    private kn x;
    String d;
    String e;
    private String y;
    private long z;
    String f;
    String g;
    a h = new a();
    public static int i = 0;
    public static int j;
    private long A = 0L;
    private int B = 0;

    protected kq() {
        this.s = km.a;
        this.y = System.getProperty("microedition.platform");
        if (this.y == null || this.y.length() == 0) {
            this.y = "OlaJ2ME";
        }
        this.c = 3;
        this.n = false;
        this.o = new s(16);
        this.k = 1;
        this.z = 0L;
        new Thread(this).start();
    }

    public static kq a() {
        if (l == null) {
            l = new kq();
        }
        return l;
    }

    public final void b() {
        this.k = (short)4;
        l = null;
        this.d(false);
    }

    private static kv a(ku ku2) {
        kv kv2 = new kv();
        block0 : switch (ku2.a) {
            case 3: {
                break;
            }
            case 2: {
                break;
            }
            case 4: {
                kv2.a((short)9, ku2.b);
                kv2.a((short)10, ku2.c);
                kv2.a((short)1, ku2.h);
                kv2.a((short)11, ku2.y);
                break;
            }
            case 5: {
                kv2.b((short)41, ku2.l);
                kv2.a((short)11, "0.17.0");
                kv2.b((short)170, ku2.m);
                if (!ku2.n) break;
                kv2.a((short)254, 1);
                break;
            }
            case 8: {
                kv2.a((short)16, ku2.o);
                kv2.a((short)15, ku2.p);
                int n = 0;
                while (n < ku2.N.length) {
                    kv2.b((short)90, ku2.N[n].a);
                    kv2.b((short)96, ku2.N[n].e.a);
                    ++n;
                }
                break;
            }
            case 9: {
                kv2.a((short)9, ku2.b);
                if (ku2.O >= 0) {
                    kv2.a((short)134, ku2.O);
                }
                if (ku2.an <= 0) break;
                kv2.b((short)23, ku2.an);
                break;
            }
            case 30: {
                kv2.a((short)15, ku2.p);
                break;
            }
            case 29: {
                kv2.a((short)9, ku2.b);
                break;
            }
            case 10: {
                kv2.b((short)118, ku2.D);
                kv2.b((short)119, ku2.E);
                kv2.b((short)120, ku2.F);
                kv2.b((short)121, ku2.G);
                break;
            }
            case 27: {
                int n = 0;
                while (n < ku2.H.length) {
                    kv2.b((short)64, ku2.H[n]);
                    kv2.b((short)67, ku2.I[n]);
                    ++n;
                }
                break;
            }
            case 6: {
                kv2.b((short)4, ku2.d);
                if (ku2.e < 0) break;
                kv2.b((short)7, ku2.e);
                break;
            }
            case 11: {
                kv2.a((short)20, ku2.A);
                if (ku2.l == 0) break;
                kv2.b((short)41, ku2.l);
                break;
            }
            case 15: {
                kv2.a((short)20, ku2.A);
                if (ku2.l == 0) break;
                kv2.b((short)41, ku2.l);
                break;
            }
            case 13: {
                kv2.a((short)20, ku2.A);
                kv2.b((short)21, ku2.C);
                break;
            }
            case 43: {
                kv2.a((short)20, ku2.A);
                break;
            }
            case 12: {
                kv2.a((short)192, ku2.j);
                break;
            }
            case 16: {
                kv2.a((short)9, ku2.b);
                if (ku2.h != null) {
                    kv2.a((short)1, ku2.h);
                }
                if (ku2.K) {
                    kv2.b((short)25, 1);
                }
                if (ku2.ac > 0L) {
                    kv2.a((short)132, ku2.ac);
                }
                if (ku2.ab) {
                    kv2.a((short)141, 1);
                }
                if (ku2.ad) {
                    kv2.a((short)167, 1);
                }
                if (!ku2.ae) break;
                kv2.a((short)169, 1);
                break;
            }
            case 17: {
                kv2.a((short)31, ku2.k ? 1 : 0);
                kv2.a((short)28, ku2.x);
                if (l.b(ku2.h)) break;
                kv2.a((short)1, ku2.h);
                break;
            }
            case 23: {
                kv2.a((short)28, ku2.x);
                break;
            }
            case 14: {
                kv2.a((short)28, kq.a().f);
                kv2.b((short)41, ku2.l);
                break;
            }
            case 19: {
                kv2.a((short)28, kq.a().f);
                kv2.b((short)41, ku2.l);
                kv2.b((short)33, ku2.r);
                kv2.b((short)34, ku2.s);
                kv2.b((short)33, ku2.t);
                kv2.b((short)34, ku2.u);
                break;
            }
            case 20: {
                kv2.a((short)28, kq.a().f);
                kv2.b((short)41, ku2.l);
                kv2.b((short)64, ku2.q);
                break;
            }
            case 44: {
                kv2.a((short)28, kq.a().f);
                kv2.b((short)41, ku2.l);
                kv2.b((short)114, ku2.L);
                break;
            }
            case 47: {
                kv2.a((short)28, kq.a().f);
                kv2.b((short)41, ku2.l);
                kv2.b((short)33, ku2.v);
                kv2.b((short)34, ku2.w);
                break;
            }
            case 84: {
                kv2.b((short)114, ku2.L);
                break;
            }
            case 40: {
                kv2.a((short)28, ku2.x);
                break;
            }
            case 25: {
                int n = 0;
                while (n < ku2.i.length) {
                    kv2.a((short)9, ku2.i[n]);
                    ++n;
                }
                kv2.a((short)1, ku2.h);
                break;
            }
            case 32: {
                kv2.a((short)77, ku2.J);
                break;
            }
            case 41: {
                kv2.a((short)77, ku2.J);
                break;
            }
            case 33: {
                kv2.a((short)77, ku2.J);
                break;
            }
            case 42: {
                kv2.a((short)9, ku2.b);
                break;
            }
            case 37: {
                int n = 0;
                while (n < ku2.Q.length) {
                    if (ku2.Q[n] != null) {
                        kv2.a((short)83, ku2.Q[n]);
                    }
                    ++n;
                }
                kv2.a((short)89, ku2.R);
                break;
            }
            case 127: {
                kv2.a((short)191, ku2.M);
                break;
            }
            case 48: {
                kv2.b((short)114, ku2.L);
                kv2.a((short)83, ku2.P);
                break;
            }
            case 49: {
                kv2.a((short)9, ku2.b);
                kv2.a((short)40, ku2.aa ? (byte)1 : 0);
                break;
            }
            case 55: {
                kv2.a((short)147, ku2.S);
                switch (ku2.S) {
                    case 0: {
                        kv2.a((short)9, ku2.b);
                        break;
                    }
                    case 1: {
                        kv2.a((short)150, ku2.af);
                        kv2.a((short)31, ku2.k ? 1 : 0);
                        break;
                    }
                    case 4: {
                        kv2.a((short)150, ku2.af);
                        kv2.a((short)83, ku2.P);
                        kv2.b((short)106, ku2.f);
                        break;
                    }
                    case 3: {
                        kv2.a((short)150, ku2.af);
                        kv2.b((short)114, ku2.L);
                        kv2.b((short)106, ku2.f);
                        break;
                    }
                    case 2: {
                        kv2.a((short)150, ku2.af);
                        kv2.a((short)132, ku2.ag);
                        break;
                    }
                    case 5: {
                        if (ku2.af == null) break;
                        kv2.a((short)150, ku2.af);
                        break;
                    }
                    case 6: {
                        kv2.a((short)150, ku2.af);
                        kv2.b((short)41, j);
                        break;
                    }
                    case 7: {
                        kv2.a((short)150, ku2.af);
                    }
                }
                break;
            }
            case 56: {
                kv2.a((short)147, ku2.S);
                switch (ku2.S) {
                    case 0: {
                        break;
                    }
                    case 1: {
                        kv2.a((short)152, ku2.T);
                        kv2.b((short)148, ku2.U);
                        break;
                    }
                    case 2: {
                        if (ku2.Y == null) break;
                        int n = 0;
                        while (n < ku2.Y.length) {
                            kv2.b((short)153, ku2.Y[n]);
                            ++n;
                        }
                        break;
                    }
                }
                break;
            }
            case 129: {
                kv2.a((short)147, ku2.S);
                switch (ku2.S) {
                    case 1: {
                        break;
                    }
                    case 2: {
                        kv2.a((short)162, ku2.ah);
                    }
                }
                break;
            }
            case 7: {
                kv2.a((short)147, ku2.S);
                switch (ku2.S) {
                    case 0: {
                        kv2.a((short)165, ku2.ai ? 1 : 0);
                        break;
                    }
                    case 1: {
                        kv2.a((short)166, ku2.ai ? 1 : 0);
                    }
                }
                break;
            }
            case 57: {
                kv2.a((short)147, ku2.aj);
                kv2.a((short)20, ku2.A);
                kv2.b((short)21, ku2.C);
                break;
            }
            case 64: {
                break;
            }
            case 65: {
                kv2.a((short)192, ku2.j);
                break;
            }
            case 18: {
                kv2.a((short)28, ku2.x);
                break;
            }
            case 130: {
                break;
            }
            case 131: {
                kv2.a((short)9, ku2.b);
                kv2.a((short)10, ku2.c);
                kv2.a((short)26, ku2.ak);
                kv2.a((short)162, ku2.ah);
                kv2.a((short)179, ku2.al);
                kv2.a((short)177, ku2.am);
                kv2.a((short)16, ku2.o);
                break;
            }
            case 51: {
                kv2.b((short)114, ku2.L);
                break;
            }
            case 83: {
                kv2.b((short)114, ku2.L);
                kv2.b((short)106, ku2.f);
                break;
            }
            case 86: {
                break;
            }
            case 99: {
                break;
            }
            case 100: {
                kv2.a((short)186, ku2.B);
                kv2.a((short)187, ku2.z);
                if (ku2.P != null) {
                    kv2.a((short)83, ku2.P);
                }
                if (ku2.L <= 0) break;
                kv2.b((short)114, ku2.L);
                kv2.b((short)106, ku2.f);
                break;
            }
            case 112: {
                if (ku2.P != null) {
                    kv2.a((short)83, ku2.P);
                    kv2.a((short)132, ku2.ag);
                    break;
                }
                if (ku2.L > 0) {
                    kv2.b((short)114, ku2.L);
                    kv2.b((short)106, ku2.f);
                    kv2.a((short)132, ku2.ag);
                    break;
                }
                if (ku2.Z == null) break;
                kv2.a((short)175, ku2.Z);
                break;
            }
            case 113: {
                kv2.a((short)175, ku2.Z);
                break;
            }
            case 116: {
                break;
            }
            case 114: {
                switch (ku2.V) {
                    case 0: {
                        break block0;
                    }
                    case 1: {
                        kv2.a((short)152, ku2.T);
                        kv2.b((short)13, ku2.W);
                        kv2.b((short)148, ku2.U);
                        break block0;
                    }
                }
                break;
            }
            case 115: {
                kv2.a((short)175, ku2.Z);
                break;
            }
            case 101: {
                int n;
                kv2.a((short)186, ku2.B);
                if (ku2.Q != null) {
                    n = 0;
                    while (n < ku2.Q.length) {
                        kv2.a((short)83, ku2.Q[n]);
                        ++n;
                    }
                }
                if (ku2.X != null) {
                    n = 0;
                    while (n < ku2.X.length) {
                        kv2.b((short)114, ku2.X[n]);
                        kv2.b((short)106, ku2.g[n]);
                        ++n;
                    }
                }
                if (ku2.ag <= 0L) break;
                kv2.a((short)132, ku2.ag);
                break;
            }
            case 96: {
                kv2.a((short)83, ku2.P);
                break;
            }
            case 97: {
                kv2.a((short)186, ku2.B);
                kv2.a((short)187, ku2.z);
                if (ku2.P != null) {
                    kv2.a((short)83, ku2.P);
                }
                if (ku2.L <= 0) break;
                kv2.b((short)114, ku2.L);
                kv2.b((short)106, ku2.f);
                break;
            }
            case 98: {
                int n;
                kv2.a((short)186, ku2.B);
                if (ku2.Q != null) {
                    n = 0;
                    while (n < ku2.Q.length) {
                        kv2.a((short)83, ku2.Q[n]);
                        ++n;
                    }
                }
                if (ku2.X != null) {
                    n = 0;
                    while (n < ku2.X.length) {
                        kv2.b((short)114, ku2.X[n]);
                        kv2.b((short)106, ku2.g[n]);
                        ++n;
                    }
                }
                if (ku2.ag <= 0L) break;
                kv2.a((short)132, ku2.ag);
                break;
            }
            case 132: {
                break;
            }
            case 133: {
                if (ku2.am != null) {
                    kv2.a((short)182, ku2.am);
                }
                kv2.a((short)183, ku2.aq);
                kv2.a((short)185, ku2.ao);
                kv2.a((short)184, ku2.ap);
            }
        }
        return kv2;
    }

    private void z() {
        if (this.q != null) {
            this.q.c = this.v;
            this.q.d = this.w;
            this.q.e = this.x;
        }
    }

    private static void A() {
        Object object = m;
        synchronized (object) {
            try {
                m.wait();
            }
            catch (Throwable throwable) {}
            return;
        }
    }

    private void b(ku ku2) {
        this.o.a(ku2);
    }

    /*
     * Unable to fully structure code
     */
    public final void run() {
        block19: while (!this.n) {
            try {
                switch (this.k) {
                    case 4: {
                        cw.a("[ConnetionHanldle] Disconnecting...");
                        var1_1 = this;
                        if (var1_1.o != null) {
                            var1_1.o.c();
                        }
                        var1_1 = this;
                        if (var1_1.q != null) {
                            var1_1.v = null;
                            var1_1.q.c = null;
                            var1_1.w = null;
                            var1_1.q.d = null;
                            var1_1.x = null;
                            var1_1.q.e = null;
                        }
                        try {
                            if (this.q != null) {
                                this.q.a();
                            }
                            if (this.r != null) {
                                this.r.a();
                            }
                            if (this.p != null) {
                                this.p.close();
                            }
                        }
                        catch (Throwable v0) {}
                        this.q = null;
                        this.r = null;
                        this.p = null;
                        this.c = 3;
                        this.n = true;
                        this.k = 0;
                        cw.a(" [ConnetionHanldle] Disconnected!!");
                        return;
                    }
                    case 0: {
                        kq.A();
                        break;
                    }
                    case 1: {
                        var1_1 = this;
                        cw.a("[ConnetionHanldle] Opening connection ...");
                        var2_2 = pa.l();
                        if (var2_2 != null) {
                            var1_1.s = var2_2;
                        }
                        var2_3 = 0;
                        while (var2_3 < var1_1.s.length) {
                            try {
                                var1_1.p = i.a(var1_1.s[var2_3].trim(), var1_1.t);
                                var1_1.q = new kw(var1_1.p.openInputStream());
                                var1_1.r = new kx(var1_1.p.openOutputStream());
                                super.z();
                                var3_5 = var1_1.s[0];
                                var1_1.s[0] = var1_1.s[var2_3];
                                var1_1.s[var2_3] = var3_5;
                                pa.a(var1_1.s);
                                var1_1.k = (short)3;
                                cw.a("[ConnetionHanldle] Connected to " + var1_1.s[0]);
                                if (var1_1.v == null) break;
                                break;
                            }
                            catch (SecurityException v1) {
                                var1_1.k = 0;
                                if (var1_1.v != null) {
                                    var1_1.v.a(2, "Thi\u1ebft b\u1ecb kh\u00f4ng cho ph\u00e9p k\u1ebft n\u1ed1i m\u1ea1ng.");
                                }
                                cw.a("[ConnetionHanldle] Connection FAIL!!!");
                                break;
                            }
                            catch (Throwable v2) {
                                cw.a("[ConnetionHanldle] Connection FAIL!!!");
                                ++var2_3;
                            }
                        }
                        if (var2_3 < var1_1.s.length) continue block19;
                        var1_1.k = 0;
                        if (var1_1.v == null) continue block19;
                        var1_1.v.a(1, "Kh\u00f4ng th\u1ec3 k\u1ebft n\u1ed1i v\u1edbi m\u00e1y ch\u1ee7.");
                        break;
                    }
                    case 2: {
                        try {
                            cw.a(" [ConnetionHanldle] Changing connection ...");
                            if (this.q != null) {
                                this.q.a();
                            }
                            if (this.r != null) {
                                this.r.a();
                            }
                            if (this.p != null) {
                                this.p.close();
                            }
                            this.q = null;
                            this.r = null;
                            this.p = null;
                            this.p = i.a(this.s[0].trim(), this.t);
                            this.q = new kw(this.p.openInputStream());
                            this.r = new kx(this.p.openOutputStream());
                            this.z();
                            cw.a("[ConnetionHanldle] Change success to " + this.s[0]);
                            pa.a(this.s);
                            this.k = (short)3;
                            var1_1 = new ku(3);
                            new ku(3).b = gr.e;
                            this.b((ku)var1_1);
                            if (this.v == null) continue block19;
                            this.v.v();
                            break;
                        }
                        catch (SecurityException v3) {
                            if (this.v != null) {
                                this.v.a(2, (String)null);
                            }
                        }
                        catch (Throwable v4) {
                            if (this.v == null) ** GOTO lbl105
                            this.v.a(1, (String)null);
                        }
lbl105:
                        // 3 sources

                        this.k = 0;
                        break;
                    }
                    case 3: {
                        var1_1 = this;
                        var1_1 = (ku)var1_1.o.b();
                        if (var1_1 != null) {
                            var2_4 = kq.a((ku)var1_1);
                            try {
                                if (var2_4 == null) continue block19;
                                this.r.a(var2_4, var1_1.a);
                            }
                            catch (Throwable v5) {
                                var1_1 = v5;
                                v5.printStackTrace();
                                this.B();
                            }
                            continue block19;
                        }
                        kq.A();
                    }
                }
            }
            catch (Throwable v6) {
                var1_1 = v6;
                v6.printStackTrace();
                this.B();
            }
        }
    }

    private void B() {
        if (this.k == 0) {
            return;
        }
        if (this.v != null) {
            this.v.x();
        }
        this.k = 0;
    }

    private void d(boolean bl) {
        if (bl && this.k != 3 && this.k != 2) {
            return;
        }
        try {
            Object object = m;
            synchronized (object) {
                m.notify();
                return;
            }
        }
        catch (Throwable throwable) {
            return;
        }
    }

    public final void a(kp kp2) {
        this.v = kp2;
        if (this.q != null) {
            this.q.c = kp2;
        }
    }

    public final void a(ko ko2) {
        this.w = ko2;
        if (this.q != null) {
            this.q.d = ko2;
        }
    }

    public final void a(kn kn2) {
        this.x = kn2;
        if (this.q != null) {
            this.q.e = kn2;
        }
    }

    public final void c() {
        long l2 = System.currentTimeMillis();
        if (this.A == 0L) {
            this.A = l2;
        } else {
            ++this.B;
            if (this.B >= 100) {
                if (l2 - this.A <= 3000L && this.v != null) {
                    this.v.U();
                }
                this.B = 0;
                this.A = l2;
            }
        }
        if (l2 - this.z >= 30000L) {
            if (this.c == 0) {
                this.B();
                return;
            }
            --this.c;
            if (this.k == 3) {
                ku ku2 = new ku(1);
                this.b(ku2);
            }
            this.z = l2;
            this.d(true);
        }
    }

    public final void a(String object) {
        cw.a((String)object);
        if (object != null) {
            String[] stringArray;
            if (this.v != null) {
                this.v.w();
            }
            int n = 0;
            while (n < this.s.length) {
                if (((String)object).equals(this.s[n])) {
                    stringArray = this.s[0];
                    this.s[0] = object;
                    this.s[n] = stringArray;
                    break;
                }
                ++n;
            }
            if (n >= this.s.length) {
                String[] stringArray2 = new String[this.s.length + 1];
                stringArray = stringArray2;
                stringArray2[0] = object;
                System.arraycopy(this.s, 0, stringArray, 1, this.s.length);
                this.s = stringArray;
            }
            this.k = (short)2;
            this.d(false);
            return;
        }
        if (this.v != null) {
            this.v.v();
        }
        object = this;
        ku ku2 = new ku(3);
        super.b(ku2);
        super.d(true);
    }

    final void a(String string, byte[] byArray, byte[] object) {
        object = new ku(4);
        new ku(4).b = string;
        object.c = byArray;
        object.l = this.u;
        object.y = "0.17.0";
        object.h = System.getProperty("microedition.platform");
        this.b((ku)object);
        this.d(true);
    }

    public final void a(String object, String string, int n) {
        kq kq2 = this;
        if (kq2.k == 0) {
            kq2.k = 1;
            kq2.d(false);
        }
        this.d = object;
        this.e = string;
        this.u = n;
        object = new ku(2);
        this.b((ku)object);
        this.d(true);
    }

    public final void a(int n) {
        ku ku2 = new ku(6);
        new ku(6).d = n;
        ku2.e = -1;
        this.b(ku2);
        this.d(true);
    }

    public final void a(int n, int n2) {
        ku ku2 = new ku(6);
        new ku(6).d = n;
        ku2.e = n2;
        this.b(ku2);
        this.d(true);
    }

    public final void a(int n, int n2, boolean bl) {
        ku ku2 = new ku(5);
        new ku(5).l = n2;
        ku2.m = n;
        ku2.n = false;
        this.b(ku2);
        this.d(true);
    }

    public final void a(int n, int n2, di di2, di di3, di di4) {
        ku ku2 = new ku(8);
        new ku(8).o = (byte)n;
        ku2.p = (byte)n2;
        ku2.N = new di[3];
        ku2.N[0] = di2;
        ku2.N[1] = di3;
        ku2.N[2] = di4;
        this.b(ku2);
        this.d(true);
    }

    public final void b(String string) {
        ku ku2 = new ku(42);
        new ku(42).b = string;
        this.b(ku2);
        this.d(true);
    }

    public final void a(String[] stringArray) {
        ku ku2 = new ku(37);
        new ku(37).Q = stringArray;
        ku2.R = (byte)2;
        this.b(ku2);
        this.d(true);
    }

    public final void b(String[] stringArray) {
        ku ku2 = new ku(37);
        new ku(37).Q = stringArray;
        ku2.R = 1;
        this.b(ku2);
        this.d(true);
    }

    public final void c(String[] stringArray) {
        ku ku2 = new ku(37);
        new ku(37).Q = stringArray;
        ku2.R = 0;
        this.b(ku2);
        this.d(true);
    }

    public final void d() {
        ku ku2 = new ku(127);
        new ku(127).M = 1;
        this.b(ku2);
        this.d(true);
    }

    public final void a(int n, String string) {
        ku ku2 = new ku(48);
        new ku(48).L = n;
        ku2.P = string;
        this.b(ku2);
        this.d(true);
    }

    public final void a(String string, byte by) {
        ku ku2 = new ku(9);
        new ku(9).b = string;
        ku2.O = by;
        this.b(ku2);
        this.d(true);
    }

    public final void a(String string, int n, byte by) {
        ku ku2 = new ku(9);
        new ku(9).an = 91;
        ku2.b = string;
        ku2.O = (byte)101;
        this.b(ku2);
        this.d(true);
    }

    public final void b(int n) {
        ku ku2 = new ku(30);
        new ku(30).p = (byte)n;
        this.b(ku2);
        this.d(true);
    }

    public final void a(int n, int n2, int n3, int n4) {
        ku ku2 = new ku(10);
        new ku(10).D = n;
        ku2.E = n2;
        ku2.F = n3;
        ku2.G = n4;
        this.b(ku2);
        this.d(true);
    }

    public final void a(int[] nArray, int[] nArray2) {
        ku ku2 = new ku(27);
        new ku(27).H = nArray;
        ku2.I = nArray2;
        this.b(ku2);
        this.d(true);
    }

    public final void e() {
        ku ku2 = new ku(52);
        this.b(ku2);
        this.d(true);
    }

    public final void a(String string, int n) {
        ku ku2 = new ku(11);
        new ku(11).A = string;
        ku2.l = n;
        this.b(ku2);
        this.d(true);
    }

    public final void c(String string) {
        ku ku2 = new ku(29);
        new ku(29).b = string;
        this.b(ku2);
        this.d(true);
    }

    public final void b(String string, int n) {
        ku ku2 = new ku(13);
        new ku(13).A = string;
        ku2.C = n;
        this.b(ku2);
        this.d(true);
    }

    public final void d(String string) {
        ku ku2 = new ku(43);
        new ku(43).A = string;
        this.b(ku2);
        this.d(true);
    }

    public final void e(String string) {
        ku ku2 = new ku(12);
        new ku(12).j = string;
        this.b(ku2);
        this.d(true);
    }

    public final void f() {
        ku ku2 = new ku(28);
        this.b(ku2);
        this.d(true);
    }

    public final void a(String string, boolean bl) {
        ku ku2 = new ku(16);
        new ku(16).b = string;
        ku2.K = bl;
        this.b(ku2);
        this.d(true);
    }

    public final void a(String string, String string2, boolean bl, long l2, boolean bl2, boolean bl3) {
        ku ku2 = new ku(16);
        new ku(16).b = string;
        ku2.h = string2;
        ku2.ab = bl;
        ku2.ac = l2;
        ku2.ad = bl2;
        ku2.ae = bl3;
        this.b(ku2);
        this.d(true);
    }

    public final void a(boolean bl, String string, String string2) {
        ku ku2 = new ku(17);
        new ku(17).k = bl;
        ku2.x = string;
        ku2.h = string2;
        this.b(ku2);
        this.d(true);
    }

    public final void g() {
        ku ku2 = new ku(126);
        this.b(ku2);
        this.d(true);
    }

    public final void f(String string) {
        ku ku2 = new ku(49);
        new ku(49).b = string;
        ku2.aa = true;
        this.b(ku2);
        this.d(true);
    }

    public final void h() {
        ku ku2 = new ku(49);
        new ku(49).b = this.d;
        ku2.aa = false;
        this.b(ku2);
        this.d(true);
    }

    public final void c(int n) {
        ku ku2 = new ku(14);
        new ku(14).x = this.f;
        ku2.l = n;
        this.b(ku2);
        this.d(true);
    }

    public final void a(int n, int n2, int n3, int n4, int n5) {
        ku ku2 = new ku(19);
        new ku(19).r = (byte)n;
        ku2.s = (byte)n2;
        ku2.t = (byte)n3;
        ku2.u = (byte)n4;
        ku2.l = n5;
        this.b(ku2);
        this.d(true);
    }

    public final void b(int n, int n2) {
        ku ku2 = new ku(20);
        new ku(20).q = n;
        ku2.l = n2;
        this.b(ku2);
        this.d(true);
    }

    public final void c(int n, int n2) {
        ku ku2 = new ku(44);
        new ku(44).L = n;
        ku2.l = n2;
        this.b(ku2);
        this.d(true);
    }

    public final void i() {
        ku ku2 = new ku(40);
        new ku(40).x = this.f;
        this.b(ku2);
        this.d(true);
    }

    public final void a(int n, int n2, int n3) {
        ku ku2 = new ku(47);
        new ku(47).v = n;
        ku2.w = n2;
        ku2.l = n3;
        this.b(ku2);
        this.d(true);
    }

    public final void d(int n) {
        ku ku2 = new ku(84);
        new ku(84).L = n;
        this.b(ku2);
        this.d(true);
    }

    public final void a(String string, long l2) {
        ku ku2 = new ku(112);
        new ku(112).P = string;
        ku2.ag = l2;
        this.b(ku2);
        this.d(true);
    }

    public final void g(String string) {
        ku ku2 = new ku(112);
        new ku(112).Z = string;
        this.b(ku2);
        this.d(true);
    }

    public final void a(int n, int n2, long l2) {
        ku ku2 = new ku(112);
        new ku(112).L = n;
        ku2.f = n2;
        ku2.ag = l2;
        this.b(ku2);
        this.d(true);
    }

    public final void h(String string) {
        ku ku2 = new ku(113);
        new ku(113).Z = string;
        this.b(ku2);
        this.d(true);
    }

    public final void b(int n, int n2, int n3) {
        ku ku2 = new ku(114);
        new ku(114).V = 1;
        ku2.T = n;
        ku2.W = 10;
        ku2.U = n3;
        this.b(ku2);
        this.d(true);
    }

    public final void j() {
        ku ku2 = new ku(114);
        new ku(114).V = 0;
        this.b(ku2);
        this.d(true);
    }

    public final void k() {
        ku ku2 = new ku(116);
        this.b(ku2);
        this.d(true);
    }

    public final void i(String string) {
        ku ku2 = new ku(115);
        new ku(115).Z = string;
        this.b(ku2);
        this.d(true);
    }

    public final void e(int n) {
        ku ku2 = new ku(51);
        new ku(51).L = n;
        this.b(ku2);
        this.d(true);
    }

    public final void l() {
        ku ku2 = new ku(56);
        new ku(56).S = 0;
        this.b(ku2);
        this.d(true);
    }

    public final void d(int n, int n2) {
        ku ku2 = new ku(56);
        new ku(56).S = 1;
        ku2.T = n;
        ku2.U = n2;
        this.b(ku2);
        this.d(true);
    }

    public final void a(int[] nArray) {
        ku ku2 = new ku(56);
        new ku(56).S = (byte)2;
        ku2.Y = nArray;
        this.b(ku2);
        this.d(true);
    }

    public final void j(String string) {
        ku ku2 = new ku(55);
        new ku(55).b = string;
        ku2.S = 0;
        this.b(ku2);
        this.d(true);
    }

    public final void a(boolean n) {
        ku ku2 = new ku(55);
        new ku(55).k = n;
        ku2.S = 1;
        ku2.af = (String)this.h.c();
        if (n == 0) {
            this.h.a(this.h.d() - 1);
        } else {
            n = 0;
            int n2 = this.h.d() - 1;
            while (n < n2) {
                ku ku3 = new ku(55);
                new ku(55).k = false;
                ku3.S = 1;
                ku3.af = (String)this.h.b(n);
                this.b(ku3);
                ++n;
            }
            this.h.a();
            j = 0;
        }
        this.b(ku2);
        this.d(true);
    }

    public final void k(String string) {
        ku ku2 = new ku(55);
        new ku(55).S = (byte)4;
        ku2.af = this.g;
        ku2.P = string;
        ku2.f = 1;
        this.b(ku2);
        this.d(true);
    }

    public final void l(String string) {
        ku ku2 = new ku(55);
        new ku(55).S = (byte)4;
        ku2.af = this.g;
        ku2.P = string;
        ku2.f = 0;
        this.b(ku2);
        this.d(true);
    }

    public final void e(int n, int n2) {
        ku ku2 = new ku(55);
        new ku(55).S = (byte)3;
        ku2.af = this.g;
        ku2.L = n;
        ku2.f = n2;
        this.b(ku2);
        this.d(true);
    }

    public final void f(int n) {
        ku ku2 = new ku(55);
        new ku(55).S = (byte)2;
        ku2.af = this.g;
        ku2.ag = n;
        this.b(ku2);
        this.d(true);
    }

    public final void m() {
        ku ku2 = new ku(55);
        new ku(55).S = (byte)5;
        ku2.af = this.g;
        this.b(ku2);
        this.d(true);
    }

    public final void n() {
        ku ku2 = new ku(55);
        new ku(55).S = (byte)6;
        ku2.af = this.g;
        this.b(ku2);
        this.d(true);
    }

    public final void o() {
        ku ku2 = new ku(55);
        new ku(55).S = (byte)7;
        ku2.af = this.g;
        this.b(ku2);
        this.d(true);
    }

    public final void p() {
        ku ku2 = new ku(31);
        this.b(ku2);
        this.d(true);
    }

    public final void m(String string) {
        ku ku2 = new ku(32);
        new ku(32).J = string;
        this.b(ku2);
        this.d(true);
    }

    public final void n(String string) {
        ku ku2 = new ku(41);
        new ku(41).J = string;
        this.b(ku2);
        this.d(true);
    }

    public final void o(String string) {
        ku ku2 = new ku(33);
        new ku(33).J = string;
        this.b(ku2);
        this.d(true);
    }

    public final void a(String[] stringArray, String string) {
        ku ku2 = new ku(25);
        new ku(25).h = string;
        ku2.i = stringArray;
        this.b(ku2);
        this.d(true);
    }

    public final void a(String string, String string2) {
        ku ku2 = new ku(25);
        new ku(25).h = string2;
        ku2.i = new String[1];
        ku2.i[0] = string;
        this.b(ku2);
        this.d(true);
    }

    public final void q() {
        ku ku2 = new ku(255);
        this.b(ku2);
        this.d(true);
    }

    public final void a(String string, byte[] byArray, String string2, String string3, Long l2, String string4, byte by) {
        ku ku2 = new ku(131);
        new ku(131).b = string;
        ku2.c = byArray;
        ku2.ak = string2;
        ku2.ah = string3;
        ku2.al = l2;
        ku2.am = string4;
        ku2.o = by;
        this.b(ku2);
        this.d(true);
    }

    public final void r() {
        ku ku2 = new ku(130);
        this.b(ku2);
        this.d(true);
    }

    public final void s() {
        ku ku2 = new ku(129);
        new ku(129).S = 1;
        this.b(ku2);
        this.d(true);
    }

    public final void p(String string) {
        ku ku2 = new ku(129);
        new ku(129).S = (byte)2;
        ku2.ah = string;
        this.b(ku2);
        this.d(true);
    }

    public final void b(boolean bl) {
        ku ku2 = new ku(7);
        new ku(7).S = 0;
        ku2.ai = bl;
        this.b(ku2);
        this.d(true);
    }

    public final void c(boolean bl) {
        ku ku2 = new ku(7);
        new ku(7).S = 1;
        ku2.ai = bl;
        this.b(ku2);
        this.d(true);
    }

    public final void a(int n, String string, int n2) {
        ku ku2 = new ku(57);
        new ku(57).aj = n;
        ku2.A = string;
        ku2.C = n2;
        this.b(ku2);
        this.d(true);
    }

    public final void t() {
        ku ku2 = new ku(64);
        this.b(ku2);
        this.d(true);
    }

    public final void q(String string) {
        ku ku2 = new ku(65);
        new ku(65).j = string;
        this.b(ku2);
        this.d(true);
    }

    public final void u() {
        cw.a("[ConnetionHanldle] requestMatchReady");
        ku ku2 = new ku(18);
        new ku(18).x = this.f;
        this.b(ku2);
        this.d(true);
    }

    public final void v() {
        cw.a("[ConnetionHanldle] requestSyncData");
        ku ku2 = new ku(23);
        new ku(23).x = this.f;
        this.b(ku2);
        this.d(true);
    }

    public final void w() {
        ku ku2 = new ku(86);
        this.b(ku2);
        this.d(true);
    }

    public final void f(int n, int n2) {
        ku ku2 = new ku(83);
        new ku(83).L = n;
        ku2.f = n2;
        this.b(ku2);
        this.d(true);
    }

    public final void x() {
        ku ku2 = new ku(99);
        this.b(ku2);
        this.d(true);
    }

    public final void a(String string, byte by, String string2) {
        ku ku2 = new ku(100);
        new ku(100).B = string;
        ku2.z = by;
        ku2.P = string2;
        this.b(ku2);
        this.d(true);
    }

    public final void b(String string, byte by, String string2) {
        ku ku2 = new ku(97);
        new ku(97).B = string;
        ku2.z = by;
        ku2.P = string2;
        this.b(ku2);
        this.d(true);
    }

    public final void a(String string, byte by, int n, int n2) {
        ku ku2 = new ku(100);
        new ku(100).B = string;
        ku2.z = by;
        ku2.L = n;
        ku2.f = n2;
        this.b(ku2);
        this.d(true);
    }

    public final void b(String string, byte by, int n, int n2) {
        ku ku2 = new ku(97);
        new ku(97).B = string;
        ku2.z = by;
        ku2.L = n;
        ku2.f = n2;
        this.b(ku2);
        this.d(true);
    }

    public final void a(String string, String[] stringArray, int[] nArray, int[] nArray2, long l2) {
        ku ku2 = new ku(101);
        new ku(101).B = string;
        ku2.Q = stringArray;
        ku2.X = nArray;
        ku2.g = nArray2;
        ku2.ag = l2;
        this.b(ku2);
        this.d(true);
    }

    public final void b(String string, String[] stringArray, int[] nArray, int[] nArray2, long l2) {
        ku ku2 = new ku(98);
        new ku(98).B = string;
        ku2.Q = stringArray;
        ku2.X = nArray;
        ku2.g = nArray2;
        ku2.ag = l2;
        this.b(ku2);
        this.d(true);
    }

    public final void r(String string) {
        if (cw.b()) {
            cw.a("[requestUpgradeEquipment] eqipKey  " + string);
        }
        ku ku2 = new ku(96);
        new ku(96).P = string;
        this.b(ku2);
        this.d(true);
    }

    public final void y() {
        ku ku2 = new ku(132);
        this.b(ku2);
        this.d(true);
    }

    public final void a(String string, String string2, String string3, String string4) {
        ku ku2 = new ku(133);
        new ku(133).ao = string;
        ku2.ap = string2;
        ku2.aq = string3;
        ku2.am = string4;
        this.b(ku2);
        this.d(true);
    }
}
