/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.microedition.io.Connector
 *  javax.microedition.io.SocketConnection
 */
import javax.microedition.io.Connector;
import javax.microedition.io.SocketConnection;

public final class dv
implements eh,
Runnable {
    private static dv B;
    private boolean C = false;
    boolean a = false;
    boolean b = false;
    private String[] D;
    private String E = "1236";
    private int F = 0;
    private Object G = new Object();
    private dw H;
    boolean c = false;
    private em I;
    private en J;
    private a K;
    private int L = 3;
    boolean d;
    boolean e;
    String f;
    String g;
    String h;
    short i;
    byte[] j;
    byte[] k;
    byte[] l;
    private byte[] M;
    ea m;
    private long N = 0L;
    long n = 0L;
    private long O = 0L;
    int o = 0;
    int p = 0;
    int q = 0;
    private int P = 0;
    int r = 0;
    int s;
    private int Q;
    int t;
    int u;
    private int R;
    private int S;
    int v;
    private long T = 0L;
    String w;
    private short U;
    private long V;
    short x = 0;
    short y = 0;
    private SocketConnection W;

    private dv() {
        this.D = eh.z;
        this.K = new a(10);
    }

    public static dv a() {
        if (B == null) {
            B = new dv();
        }
        return B;
    }

    public final void a(ea ea2) {
        if (this.F == 0) {
            if (this.D == null || this.D.length == 0) {
                throw new RuntimeException("No IP address for connection. Please use setIp() to assign array of IP address");
            }
            this.F = 1;
        }
        this.m = ea2;
        if (!this.C) {
            this.C = true;
            new Thread(this).start();
            return;
        }
        this.t();
    }

    public final void b() {
        this.h = null;
        this.a = false;
        this.c = false;
        this.b = false;
        this.F = 0;
        this.K.a();
        if (this.I != null) {
            this.I.a();
            this.I = null;
        }
        if (this.J != null) {
            this.J.a();
            this.J = null;
        }
        if (this.W != null) {
            try {
                this.W.close();
                return;
            }
            catch (Exception exception) {}
        }
    }

    public final void c() {
        this.b();
        this.C = false;
        this.t();
        B = null;
        System.gc();
    }

    private void t() {
        try {
            Object object = this.G;
            synchronized (object) {
                this.G.notify();
                return;
            }
        }
        catch (Throwable throwable) {
            return;
        }
    }

    final void d() {
        this.b();
        this.b = true;
        this.F = 4;
        this.t();
    }

    public final void run() {
        short s = 0;
        block114: while (this.C) {
            try {
                switch (this.F) {
                    case 0: {
                        Object object2 = this.G;
                        synchronized (object2) {
                            try {
                                this.G.wait();
                            }
                            catch (Throwable throwable) {}
                            break;
                        }
                    }
                    case 4: {
                        Object object2;
                        s = 0;
                        while (s < this.D.length) {
                            try {
                                cw.a("[OLA] Reconnecting to " + this.D[s]);
                                this.W = (SocketConnection)Connector.open((String)("socket://" + this.D[s].trim() + ":" + this.E));
                                this.W.setSocketOption((byte)1, 5);
                                this.I = new em(this.W.openInputStream());
                                this.J = new en(this.W.openOutputStream());
                                if (this.H != null) {
                                    this.I.a(this.H);
                                    this.J.a(this.H);
                                }
                                object2 = this.D[0];
                                this.D[0] = this.D[s];
                                this.D[s] = object2;
                                this.F = 2;
                                this.u();
                                break;
                            }
                            catch (SecurityException securityException) {
                                this.F = 0;
                                if (this.H == null) break;
                                this.H.a(31, null, (short)0);
                                break;
                            }
                            catch (Throwable throwable) {
                                ++s;
                            }
                        }
                        if (s < this.D.length) continue block114;
                        this.F = 0;
                        if (this.H == null) continue block114;
                        this.H.a(1, null, (short)0);
                        break;
                    }
                    case 1: {
                        Object object;
                        Object object2;
                        s = 0;
                        while (s < this.D.length) {
                            try {
                                object2 = this.D[s].trim();
                                if (this.H != null) {
                                    this.H.i_();
                                }
                                cw.a("[OLA] Connect to " + this.D[s]);
                                this.W = (SocketConnection)Connector.open((String)("socket://" + (String)object2 + ":" + this.E));
                                this.W.setSocketOption((byte)1, 5);
                                this.I = new em(this.W.openInputStream());
                                this.J = new en(this.W.openOutputStream());
                                if (this.H != null) {
                                    this.I.a(this.H);
                                    this.J.a(this.H);
                                }
                                object = this.D[0];
                                this.D[0] = this.D[s];
                                this.D[s] = object;
                                this.F = 2;
                                this.u();
                                break;
                            }
                            catch (SecurityException securityException) {
                                this.F = 0;
                                if (this.H == null) break;
                                this.H.a(31, null, (short)0);
                                break;
                            }
                            catch (Throwable throwable) {
                                ++s;
                            }
                        }
                        if (s < this.D.length) continue block114;
                        this.F = 0;
                        if (this.H == null) continue block114;
                        this.H.a(1, null, (short)0);
                        break;
                    }
                    case 3: {
                        Object object;
                        Object object2;
                        s = 0;
                        while (s < this.D.length) {
                            try {
                                cw.a("[OLA] Changing to " + this.D[s]);
                                object2 = this.D[s].trim();
                                if (this.H != null) {
                                    this.H.i_();
                                }
                                this.W = (SocketConnection)Connector.open((String)("socket://" + (String)object2 + ":" + this.E));
                                this.W.setSocketOption((byte)1, 5);
                                this.I = new em(this.W.openInputStream());
                                this.J = new en(this.W.openOutputStream());
                                if (this.H != null) {
                                    this.I.a(this.H);
                                    this.J.a(this.H);
                                }
                                object = this.D[0];
                                this.D[0] = this.D[s];
                                this.D[s] = object;
                                this.F = 2;
                                cw.a("[OLA] Connect success to " + this.D[s]);
                                this.v();
                                break;
                            }
                            catch (SecurityException securityException) {
                                this.F = 0;
                                if (this.H == null) break;
                                this.H.a(31, null, (short)0);
                                break;
                            }
                            catch (Throwable throwable) {
                                ++s;
                            }
                        }
                        if (s < this.D.length) continue block114;
                        this.F = 0;
                        if (this.H == null) continue block114;
                        this.H.a(1, null, (short)0);
                        break;
                    }
                    case 2: {
                        Object object;
                        Object object2;
                        if (this.K.d() > 0) {
                            object2 = (k)this.K.a(0);
                            object = new el();
                            short s2 = ((k)object2).C;
                            s = s2;
                            switch (s2) {
                                case 95: {
                                    Object object3 = object;
                                    Object object4 = object2;
                                    ((el)object3).a((short)52, "5.1.2");
                                    ((el)object3).a((short)38, ((k)object4).D);
                                    ((el)object3).a((short)61, ((k)object4).c);
                                    if (((k)object4).o != null) {
                                        ((el)object3).a((short)94, ((k)object4).o);
                                    }
                                    ((el)object3).a((short)50, ((k)object4).d);
                                    if (((k)object4).a != null) {
                                        ((el)object3).a((short)86, ((k)object4).a);
                                    }
                                    ((el)object3).a((short)51, String.valueOf(String.valueOf(((k)object4).x)) + "x" + String.valueOf(((k)object4).y));
                                    if (((k)object4).p == null) break;
                                    ((el)object3).a((short)14, ((k)object4).p);
                                    break;
                                }
                                case 96: {
                                    Object object3 = object;
                                    Object object4 = object2;
                                    ((el)object3).a((short)52, "5.1.2");
                                    ((el)object3).a((short)38, ((k)object4).D);
                                    ((el)object3).a((short)61, ((k)object4).c);
                                    if (((k)object4).o != null) {
                                        ((el)object3).a((short)94, ((k)object4).o);
                                    }
                                    ((el)object3).a((short)50, ((k)object4).d);
                                    if (((k)object4).a != null) {
                                        ((el)object3).a((short)86, ((k)object4).a);
                                    }
                                    ((el)object3).a((short)51, String.valueOf(String.valueOf(((k)object4).x)) + "x" + String.valueOf(((k)object4).y));
                                    if (((k)object4).p == null) break;
                                    ((el)object3).a((short)14, ((k)object4).p);
                                    break;
                                }
                                case 97: {
                                    Object object3 = object;
                                    Object object4 = object2;
                                    ((el)object3).a((short)2, ((k)object4).b);
                                    ((el)object3).a((short)43, ((k)object4).M);
                                    ((el)object3).a((short)12, (byte)((k)object4).E);
                                    ((el)object3).a((short)52, ((k)object4).f);
                                    ((el)object3).a((short)38, (byte)((k)object4).D);
                                    ((el)object3).a((short)61, ((k)object4).c);
                                    if (((k)object4).o != null) {
                                        ((el)object3).a((short)94, ((k)object4).o);
                                    }
                                    if (((k)object4).d != null) {
                                        ((el)object3).a((short)50, ((k)object4).d);
                                    }
                                    if (((k)object4).a != null) {
                                        ((el)object3).a((short)86, ((k)object4).a);
                                    }
                                    ((el)object3).a((short)51, String.valueOf(((k)object4).x) + "x" + ((k)object4).y);
                                    if (((k)object4).p != null) {
                                        ((el)object3).a((short)14, ((k)object4).p);
                                    }
                                    if (((k)object4).n != null) {
                                        ((el)object3).a((short)79, ((k)object4).n);
                                        break;
                                    }
                                    String string = av.a();
                                    String string2 = av.b();
                                    String string3 = av.c();
                                    String string4 = av.d();
                                    if (string != null) {
                                        ((el)object3).a((short)81, string);
                                    }
                                    if (string2 != null) {
                                        ((el)object3).a((short)82, string2);
                                    }
                                    if (string4 != null) {
                                        ((el)object3).a((short)80, string4);
                                    }
                                    if (string3 == null) break;
                                    ((el)object3).a((short)79, string3);
                                    break;
                                }
                                case 14: {
                                    Object object3 = object;
                                    Object object4 = object2;
                                    if (((k)object4).J > 0) {
                                        ((el)object3).a((short)220, (byte)((k)object4).J);
                                    }
                                    ((el)object3).a((short)7, ((k)object4).h);
                                    ((el)object3).a((short)8, ((k)object4).g);
                                    if (l.a(((k)object4).q)) break;
                                    ((el)object3).a((short)109, ((k)object4).q);
                                    break;
                                }
                                case 42: {
                                    break;
                                }
                                case 50: {
                                    Object object3 = object;
                                    Object object4 = object2;
                                    ((el)object3).a((short)7, ((k)object4).j);
                                    if (((k)object4).K == 0) break;
                                    ((el)object3).a((short)255, (byte)((k)object4).K);
                                    break;
                                }
                                case 54: {
                                    Object object3 = object;
                                    Object object4 = object2;
                                    ((el)object3).a((short)7, ((k)object4).j);
                                    if (l.a(null)) break;
                                    ((el)object3).a((short)22, (String)null);
                                    break;
                                }
                                case 55: {
                                    Object object3 = object;
                                    Object object4 = object2;
                                    ((el)object3).a((short)7, ((k)object4).j);
                                    ((el)object3).a((short)62, (byte)0);
                                    break;
                                }
                                case 65: {
                                    Object object3 = object;
                                    Object object4 = object2;
                                    if (((k)object4).j != null) {
                                        ((el)object3).a((short)7, ((k)object4).j);
                                    }
                                    ((el)object3).a((short)30, ((k)object4).m);
                                    break;
                                }
                                case 66: {
                                    Object object3 = object;
                                    Object object4 = object2;
                                    if (((k)object4).B == 0) break;
                                    ((el)object3).a((short)124, ((k)object4).B);
                                    break;
                                }
                                case 67: {
                                    break;
                                }
                                case 44: {
                                    Object object3 = object;
                                    Object object4 = object2;
                                    ((el)object3).a((short)7, ((k)object4).j);
                                    break;
                                }
                                case 21: {
                                    Object object3 = object;
                                    Object object4 = object2;
                                    ((el)object3).a((short)12, (byte)((k)object4).E);
                                    String string = ((k)object4).k;
                                    if (!l.a(string)) {
                                        ((el)object3).a((short)13, string);
                                    }
                                    if (((k)object4).Q) break;
                                    ((el)object3).a((short)10, new byte[]{1});
                                    break;
                                }
                                case 23: {
                                    Object object3 = object;
                                    Object object4 = object2;
                                    ((el)object3).a((short)200, ((k)object4).u);
                                    ((el)object3).a((short)204, ((k)object4).A);
                                    break;
                                }
                                case 25: {
                                    Object object3 = object;
                                    Object object4 = object2;
                                    if (((k)object4).K != 0) {
                                        ((el)object3).a((short)255, (byte)((k)object4).K);
                                    }
                                    ((el)object3).a((short)43, ((k)object4).M);
                                    ((el)object3).a((short)46, (byte[])null);
                                    break;
                                }
                                case 115: {
                                    Object object3 = object;
                                    Object object4 = object2;
                                    if (((k)object4).l == null) {
                                        ((el)object3).a((short)214, (short)((k)object4).A);
                                        break;
                                    }
                                    ((el)object3).a((short)218, ((k)object4).B);
                                    break;
                                }
                                case 6: {
                                    break;
                                }
                                case 9: {
                                    Object object3 = object;
                                    Object object4 = object2;
                                    ((el)object3).a((short)39, ((k)object4).B);
                                    ((el)object3).a((short)53, (byte)0);
                                    break;
                                }
                                case 11: {
                                    Object object3 = object;
                                    Object object4 = object2;
                                    ((el)object3).a((short)7, ((k)object4).j);
                                    if (((k)object4).K == 0) break;
                                    ((el)object3).a((short)255, (byte)((k)object4).K);
                                    break;
                                }
                                case 12: {
                                    Object object3 = object;
                                    Object object4 = object2;
                                    String string = ((k)object4).e;
                                    if (string != null) {
                                        ((el)object3).a((short)4, string);
                                    }
                                    if (((k)object4).N != null) {
                                        ((el)object3).a((short)23, ((k)object4).N);
                                        break;
                                    }
                                    if (((k)object4).l == null) break;
                                    ((el)object3).a((short)24, ((k)object4).l);
                                    break;
                                }
                                case 15: {
                                    dv.a((k)object2, (el)object);
                                    break;
                                }
                                case 16: {
                                    Object object3 = object;
                                    Object object4 = object2;
                                    ((el)object3).a((short)7, ((k)object4).j);
                                    if (((k)object4).i == null) break;
                                    ((el)object3).a((short)21, ((k)object4).i);
                                    break;
                                }
                                case 17: {
                                    Object object3 = object;
                                    Object object4 = object2;
                                    ((el)object3).a((short)21, ((k)object4).i);
                                    break;
                                }
                                case 35: {
                                    break;
                                }
                                case 103: {
                                    long l2;
                                    Object object3 = object;
                                    Object object4 = object2;
                                    String string = ((k)object4).j;
                                    if (string != null) {
                                        ((el)object3).a((short)7, string.toLowerCase());
                                    }
                                    if (0L != (l2 = ((k)object4).v)) {
                                        ((el)object3).a((short)72, l2);
                                    }
                                    if (((k)object4).K == 0) break;
                                    ((el)object3).a((short)255, (byte)((k)object4).K);
                                    break;
                                }
                                case 116: {
                                    Object object3 = object;
                                    Object object4 = object2;
                                    String string = ((k)object4).m;
                                    if (string != null) {
                                        ((el)object3).a((short)30, string);
                                    }
                                    if (((k)object4).K == 0) break;
                                    ((el)object3).a((short)255, (byte)((k)object4).K);
                                    break;
                                }
                                case 124: {
                                    if (((k)object2).K == 0) break;
                                    ((el)object).a((short)255, (byte)((k)object2).K);
                                    break;
                                }
                                case 125: {
                                    el el2 = object;
                                    Object object3 = object2;
                                    Object object4 = this;
                                    if (!((k)object3).j.toLowerCase().equals(((dv)object4).f.toLowerCase())) {
                                        el2.a((short)7, ((k)object3).j);
                                    }
                                    el2.a((short)205, ((k)object3).A);
                                    el2.a((short)45, (byte)((k)object3).F);
                                    break;
                                }
                                case 106: {
                                    long l3;
                                    String string;
                                    Object object3 = object;
                                    Object object4 = object2;
                                    String string5 = ((k)object4).g;
                                    if (string5 != null) {
                                        ((el)object3).a((short)8, string5);
                                    }
                                    if ((string = ((k)object4).l) != null) {
                                        ((el)object3).a((short)24, string);
                                    }
                                    if (((k)object4).L != 0) {
                                        ((el)object3).a((short)12, (byte)((k)object4).L);
                                    }
                                    if (((k)object4).J != 0) {
                                        ((el)object3).a((short)220, (byte)((k)object4).J);
                                    }
                                    if ((l3 = ((k)object4).v) != 0L) {
                                        ((el)object3).a((short)72, l3);
                                    }
                                    string5 = av.a();
                                    String string6 = av.b();
                                    object4 = av.c();
                                    String string7 = av.d();
                                    if (string5 == null || string6 == null || object4 == null || string7 == null) break;
                                    ((el)object3).a((short)81, string5);
                                    ((el)object3).a((short)82, string6);
                                    ((el)object3).a((short)79, (String)object4);
                                    ((el)object3).a((short)80, string7);
                                    break;
                                }
                                case 109: {
                                    Object object3 = object;
                                    Object object4 = object2;
                                    ((el)object3).a((short)7, ((k)object4).j);
                                    ((el)object3).a((short)209, ((k)object4).O);
                                    break;
                                }
                                case 111: {
                                    Object object3 = object;
                                    Object object4 = object2;
                                    ((el)object3).a((short)72, ((k)object4).v);
                                    break;
                                }
                                case 114: {
                                    Object object3 = object;
                                    Object object4 = object2;
                                    String string = ((k)object4).l;
                                    if (string != null) {
                                        ((el)object3).a((short)24, string);
                                    } else {
                                        ((el)object3).a((short)214, (short)((k)object4).A);
                                    }
                                    if (((k)object4).K == 0) break;
                                    ((el)object3).a((short)255, (byte)((k)object4).K);
                                    break;
                                }
                                case 39: {
                                    Object object3 = object;
                                    Object object4 = object2;
                                    ((el)object3).a((short)7, ((k)object4).h);
                                    ((el)object3).a((short)24, ((k)object4).l);
                                    if (l.a(((k)object4).q)) break;
                                    ((el)object3).a((short)109, ((k)object4).q);
                                    break;
                                }
                                case 104: {
                                    Object object3 = object;
                                    Object object4 = object2;
                                    String string = ((k)object4).j;
                                    short s3 = ((k)object4).H;
                                    String string3 = ((k)object4).g;
                                    if (string != null) {
                                        ((el)object3).a((short)7, string);
                                    }
                                    if (s3 != 0) {
                                        ((el)object3).a((short)66, (byte)s3);
                                    }
                                    if (string3 == null) break;
                                    ((el)object3).a((short)8, string3);
                                    break;
                                }
                                case 129: {
                                    break;
                                }
                                case 130: {
                                    Object object3 = object;
                                    Object object4 = object2;
                                    ((el)object3).a((short)20, (byte)0);
                                    break;
                                }
                                case 131: {
                                    Object object3 = object;
                                    Object object4 = object2;
                                    ((el)object3).a((short)20, (byte)0);
                                    ((el)object3).a((short)7, ((k)object4).j);
                                    break;
                                }
                                case 132: {
                                    Object object3 = object;
                                    Object object4 = object2;
                                    ((el)object3).a((short)20, (byte)0);
                                    ((el)object3).a((short)7, ((k)object4).j);
                                    break;
                                }
                                case 133: {
                                    Object object3 = object;
                                    Object object4 = object2;
                                    ((el)object3).a((short)20, (byte)0);
                                    break;
                                }
                                case 134: {
                                    Object object3 = object;
                                    Object object4 = object2;
                                    ((el)object3).a((short)4, ((k)object4).e);
                                    break;
                                }
                                case 135: {
                                    Object object3 = object;
                                    Object object4 = object2;
                                    ((el)object3).a((short)4, ((k)object4).e);
                                    break;
                                }
                                case 136: {
                                    break;
                                }
                                case 137: {
                                    break;
                                }
                                case 138: {
                                    break;
                                }
                                case 140: {
                                    break;
                                }
                                case 144: {
                                    break;
                                }
                                case 193: {
                                    Object object3 = object;
                                    Object object4 = object2;
                                    ((el)object3).a((short)4, ((k)object4).e);
                                    if (((k)object4).K == 0) break;
                                    ((el)object3).a((short)255, (byte)((k)object4).K);
                                    break;
                                }
                                case 194: {
                                    Object object3 = object;
                                    Object object4 = object2;
                                    ((el)object3).a((short)72, ((k)object4).v);
                                    if (((k)object4).K == 0) break;
                                    ((el)object3).a((short)255, (byte)((k)object4).K);
                                    break;
                                }
                                case 199: {
                                    Object object3 = object;
                                    Object object4 = object2;
                                    if (((k)object4).q != null) {
                                        ((el)object3).a((short)109, ((k)object4).q);
                                    }
                                    if (((k)object4).K == 0) break;
                                    ((el)object3).a((short)255, (byte)((k)object4).K);
                                    break;
                                }
                                case 147: {
                                    Object object3 = object;
                                    Object object4 = object2;
                                    if (((k)object4).j == null) break;
                                    ((el)object3).a((short)7, ((k)object4).j);
                                    break;
                                }
                                case 148: {
                                    Object object3 = object;
                                    Object object4 = object2;
                                    ((el)object3).a((short)114, (byte)((k)object4).E);
                                    break;
                                }
                                case 149: {
                                    Object object3 = object;
                                    Object object4 = object2;
                                    ((el)object3).a((short)110, ((k)object4).j);
                                    ((el)object3).a((short)114, (byte)0);
                                    break;
                                }
                                case 150: {
                                    ((el)object).a((short)110, ((k)object2).j);
                                    break;
                                }
                                case 158: {
                                    Object object3 = object;
                                    Object object4 = object2;
                                    if (((k)object4).K != 0) {
                                        ((el)object3).a((short)255, (byte)((k)object4).K);
                                    }
                                    if (((k)object4).j != null) {
                                        ((el)object3).a((short)7, ((k)object4).j);
                                    }
                                    ((el)object3).a((short)22, (String)null);
                                    break;
                                }
                                case 159: {
                                    Object object3 = object;
                                    Object object4 = object2;
                                    ((el)object3).a((short)7, ((k)object4).j);
                                    ((el)object3).a((short)129, (String)null);
                                    break;
                                }
                                case 160: {
                                    Object object3 = object;
                                    Object object4 = object2;
                                    ((el)object3).a((short)129, (String)null);
                                    ((el)object3).a((short)12, (byte)((k)object4).E);
                                    break;
                                }
                                case 162: {
                                    Object object3 = object;
                                    Object object4 = object2;
                                    ((el)object3).a((short)129, (String)null);
                                    if (((k)object4).g != null) {
                                        ((el)object3).a((short)8, ((k)object4).g);
                                    }
                                    if (((k)object4).J != 0) {
                                        ((el)object3).a((short)220, (byte)((k)object4).J);
                                    }
                                    if (l.a(((k)object4).q)) break;
                                    ((el)object3).a((short)109, ((k)object4).q);
                                    break;
                                }
                                case 163: {
                                    Object object3 = object;
                                    Object object4 = object2;
                                    ((el)object3).a((short)129, (String)null);
                                    ((el)object3).a((short)24, ((k)object4).l);
                                    if (l.a(((k)object4).q)) break;
                                    ((el)object3).a((short)109, ((k)object4).q);
                                    break;
                                }
                                case 164: {
                                    Object object3 = object;
                                    Object object4 = object2;
                                    ((el)object3).a((short)39, ((k)object4).B);
                                    break;
                                }
                                case 165: {
                                    Object object3 = object;
                                    Object object4 = object2;
                                    ((el)object3).a((short)129, (String)null);
                                    break;
                                }
                                case 166: {
                                    Object object3 = object;
                                    Object object4 = object2;
                                    ((el)object3).a((short)7, ((k)object4).j);
                                    ((el)object3).a((short)110, (String)null);
                                    if (0L != 0L) {
                                        ((el)object3).a((short)130, 0L);
                                    }
                                    if (0L == 0L) break;
                                    ((el)object3).a((short)59, 0L);
                                    break;
                                }
                                case 167: {
                                    Object object3 = object;
                                    Object object4 = object2;
                                    ((el)object3).a((short)111, ((k)object4).j);
                                    ((el)object3).a((short)114, ((k)object4).P);
                                    break;
                                }
                                case 168: {
                                    break;
                                }
                                case 169: {
                                    if (0L != 0L) {
                                        ((el)object).a((short)9, 0L);
                                    }
                                    if (((k)object2).K == 0) break;
                                    ((el)object).a((short)255, (byte)((k)object2).K);
                                    break;
                                }
                                case 170: {
                                    ((el)object).a((short)109, ((k)object2).j);
                                    break;
                                }
                                case 171: {
                                    Object object3 = object;
                                    Object object4 = object2;
                                    if (((k)object4).j != null) {
                                        ((el)object3).a((short)7, ((k)object4).j);
                                    }
                                    ((el)object3).a((short)115, ((k)object4).P);
                                    if (((k)object4).K == 0) break;
                                    ((el)object3).a((short)255, (byte)((k)object4).K);
                                    break;
                                }
                                case 175: {
                                    Object object3 = object;
                                    Object object4 = object2;
                                    if (((k)object4).K == 0) break;
                                    ((el)object3).a((short)255, (byte)((k)object4).K);
                                    break;
                                }
                                case 172: {
                                    Object object3 = object;
                                    Object object4 = object2;
                                    if (((k)object4).g != null) {
                                        ((el)object3).a((short)8, ((k)object4).g);
                                    }
                                    if (((k)object4).K == 0) break;
                                    ((el)object3).a((short)255, (byte)((k)object4).K);
                                    break;
                                }
                                case 173: {
                                    Object object3 = object;
                                    Object object4 = object2;
                                    ((el)object3).a((short)110, ((k)object4).j);
                                    ((el)object3).a((short)114, (byte)0);
                                    break;
                                }
                                case 161: {
                                    Object object3 = object;
                                    Object object4 = object2;
                                    ((el)object3).a((short)129, (String)null);
                                    break;
                                }
                                case 113: {
                                    String string6;
                                    String string4;
                                    String string3;
                                    Object object3 = object;
                                    Object object4 = object2;
                                    byte by = ((k)object4).P;
                                    if (by == 1) {
                                        ((el)object3).a((short)213, p.a((short)((k)object4).A));
                                        ((el)object3).a((short)212, ((k)object4).P);
                                        ((el)object3).a((short)37, 0);
                                        ((el)object3).a((short)68, (byte)0);
                                        string3 = av.a();
                                        string4 = av.b();
                                        String string = av.c();
                                        string6 = av.d();
                                        if (string3 != null && string4 != null && string != null && string6 != null) {
                                            ((el)object3).a((short)81, string3);
                                            ((el)object3).a((short)82, string4);
                                            ((el)object3).a((short)79, string);
                                            ((el)object3).a((short)80, string6);
                                        }
                                    } else {
                                        ((el)object3).a((short)214, p.a((short)((k)object4).A));
                                        ((el)object3).a((short)23, ((k)object4).N);
                                    }
                                    if (((k)object4).K == 0) break;
                                    ((el)object3).a((short)255, (byte)((k)object4).K);
                                    break;
                                }
                                case 100: {
                                    break;
                                }
                                case 101: {
                                    Object object3 = object;
                                    Object object4 = object2;
                                    ((el)object3).a((short)200, ((k)object4).u);
                                    ((el)object3).a((short)204, ((k)object4).z);
                                    if (((k)object4).K == 0) break;
                                    ((el)object3).a((short)255, (byte)((k)object4).K);
                                    break;
                                }
                                case 102: {
                                    Object object3 = object;
                                    Object object4 = object2;
                                    ((el)object3).a((short)65, ((k)object4).t);
                                    if (((k)object4).K == 0) break;
                                    ((el)object3).a((short)255, (byte)((k)object4).K);
                                    break;
                                }
                                case 20: {
                                    Object object3 = object;
                                    Object object4 = object2;
                                    ((el)object3).a((short)21, ((k)object4).i);
                                    ((el)object3).a((short)26, (String)null);
                                    break;
                                }
                                case 18: {
                                    Object object3 = object;
                                    Object object4 = object2;
                                    ((el)object3).a((short)21, ((k)object4).i);
                                    break;
                                }
                                case 19: {
                                    Object object3 = object;
                                    Object object4 = object2;
                                    ((el)object3).a((short)7, ((k)object4).j);
                                    ((el)object3).a((short)21, ((k)object4).i);
                                    break;
                                }
                                case 91: {
                                    Object object3 = object;
                                    Object object4 = object2;
                                    ((el)object3).a((short)72, ((k)object4).v);
                                    if (((k)object4).E == 0) break;
                                    ((el)object3).a((short)90, (byte)((k)object4).E);
                                    break;
                                }
                                case 92: {
                                    Object object3 = object;
                                    Object object4 = object2;
                                    ((el)object3).a((short)108, ((k)object4).w);
                                    if (((k)object4).v != 0L) {
                                        ((el)object3).a((short)72, ((k)object4).v);
                                    }
                                    if (((k)object4).K == 0) break;
                                    ((el)object3).a((short)255, (byte)((k)object4).K);
                                    break;
                                }
                                case 79: {
                                    Object object3 = object;
                                    Object object4 = object2;
                                    if (((k)object4).j != null) {
                                        ((el)object3).a((short)7, ((k)object4).j);
                                    }
                                    if (((k)object4).q != null) {
                                        ((el)object3).a((short)109, ((k)object4).q);
                                    }
                                    ((el)object3).a((short)114, (byte)0);
                                    break;
                                }
                                case 80: {
                                    Object object3 = object;
                                    Object object4 = object2;
                                    if (((k)object4).K != 0) {
                                        ((el)object3).a((short)255, (byte)((k)object4).K);
                                    }
                                    ((el)object3).a((short)114, (byte)0);
                                    ((el)object3).a((short)109, ((k)object4).q);
                                    break;
                                }
                                case 82: {
                                    Object object3 = object;
                                    Object object4 = object2;
                                    if (((k)object4).K != 0) {
                                        ((el)object3).a((short)255, (byte)((k)object4).K);
                                    }
                                    ((el)object3).a((short)109, ((k)object4).q);
                                }
                            }
                            try {
                                this.J.a((el)object, s);
                            }
                            catch (Exception exception) {
                                this.K.a(object2);
                            }
                            continue block114;
                        }
                        object2 = this.G;
                        synchronized (object2) {
                            try {
                                this.G.wait();
                            }
                            catch (Throwable throwable) {}
                            break;
                        }
                    }
                }
            }
            catch (Exception exception) {}
        }
    }

    private static void a(k k2, el el2) {
        el2.a((short)21, k2.i);
        int n = 0;
        while (n < k2.r.length) {
            el2.a((short)7, du.e(k2.r[n]));
            if (k2.s != null && !l.a(k2.s[n])) {
                el2.a((short)22, k2.s[n]);
            }
            ++n;
        }
    }

    public final void a(dw dw2) {
        this.H = dw2;
        if (this.I != null) {
            this.I.a(dw2);
        }
        if (this.J != null) {
            this.J.a(dw2);
        }
    }

    public final void a(String string, String string2, short s) {
        this.b = false;
        if (!this.a) {
            throw new RuntimeException("Connection is not READY. Please wait until ChatConnectionListener with method notifyConnected returns");
        }
        if (!l.a(string) && !l.b(string2)) {
            this.f = string.toLowerCase().trim();
            this.g = string2;
            this.i = s;
            this.e();
        }
    }

    private void u() {
        if (this.m == null) {
            throw new RuntimeException("There's no device information. Please call setDevice before start connection");
        }
        k k2 = new k(95);
        new k(95).D = 0;
        k2.c = this.m.b;
        k2.o = null;
        k2.d = this.m.a;
        k2.a = this.m.d;
        k2.x = this.m.e;
        k2.y = this.m.f;
        k2.p = this.m.c;
        this.K.a(k2);
    }

    final void a(String string) {
        if (string != null) {
            if (this.I != null) {
                this.I.a();
                this.I = null;
            }
            if (this.J != null) {
                this.J.a();
                this.J = null;
            }
            this.F = 3;
            if (this.D == null) {
                this.D = new String[]{string};
            } else {
                Object object;
                int n = 0;
                while (n < this.D.length) {
                    if (l.a(string, this.D[n])) {
                        object = this.D[0];
                        this.D[0] = string;
                        this.D[n] = object;
                        break;
                    }
                    ++n;
                }
                if (n >= this.D.length) {
                    object = new String[this.D.length + 1];
                    System.arraycopy(this.D, 0, object, 1, this.D.length);
                    object[0] = string;
                    this.D = object;
                }
            }
            this.t();
            return;
        }
        this.v();
    }

    private void v() {
        cw.a("[OLA] Request Authentication");
        k k2 = new k(96);
        new k(96).D = 0;
        k2.c = this.m.b;
        k2.o = null;
        k2.d = this.m.a;
        k2.a = this.m.d;
        k2.x = this.m.e;
        k2.y = this.m.f;
        k2.p = this.m.c;
        this.K.a(k2);
        this.t();
    }

    final void e() {
        cw.a("[OLA] Submit Authentication");
        this.j = dl.a(this.k, this.g);
        k k2 = new k(97);
        new k(97).E = this.i;
        k2.b = this.f;
        k2.M = this.j;
        k2.D = 0;
        k2.c = this.m.b;
        k2.f = "5.1.2";
        k2.o = null;
        k2.d = this.m.a;
        k2.a = this.m.d;
        k2.x = this.m.e;
        k2.y = this.m.f;
        k2.p = this.m.c;
        if (!l.b(null) && !l.b(null)) {
            k2.n = String.valueOf(null) + " " + null;
        }
        this.K.a(k2);
        this.t();
    }

    final void f() {
        this.L = 3;
        this.N = 0L;
    }

    public final void g() {
        if (this.c) {
            long l2 = System.currentTimeMillis();
            if (l2 - this.N >= 120000L) {
                if (this.L == 0) {
                    this.d();
                    return;
                }
                --this.L;
                if (this.F == 2) {
                    k k2 = new k(42);
                    this.K.a(k2);
                }
                this.N = l2;
                this.t();
            }
            dv dv2 = this;
            long l3 = System.currentTimeMillis();
            if (l3 - dv2.O >= 300000L) {
                dv2.O = System.currentTimeMillis();
                k k3 = new k(168);
                dv2.K.a(k3);
                dv2.t();
            }
        }
    }

    final void h() {
        this.L = 3;
    }

    public final void a(int n) {
        k k2 = new k(9);
        new k(9).B = n;
        k2.G = 0;
        this.K.a(k2);
        this.t();
    }

    public final void i() {
        k k2 = new k(140);
        this.K.a(k2);
        this.t();
    }

    public final void a(String string, String string2, String string3) {
        this.a(string, string2, null, (short)0);
    }

    public final void a(String string, String string2, String string3, short s) {
        if (string == null) {
            throw new RuntimeException("Null receiver Id");
        }
        k k2 = new k(14);
        if (s > 0) {
            k2.J = s;
        }
        k2.h = du.e(string);
        k2.g = string2;
        if (!l.a(string3)) {
            k2.q = "id=" + string3;
        }
        this.K.a(k2);
        this.t();
    }

    public final void b(String string) {
        k k2 = new k(44);
        new k(44).j = du.e(string);
        this.K.a(k2);
        this.t();
    }

    private void a(short s, String string, boolean bl) {
        this.h = string;
        this.i = s;
        k k2 = new k(21);
        new k(21).E = s;
        if (!l.a(string)) {
            this.i = (short)2;
            k2.k = string;
            k2.E = this.i;
        } else {
            this.h = null;
        }
        k2.Q = true;
        this.K.a(k2);
        this.t();
    }

    public final void c(String string) {
        this.a((short)2, string, true);
    }

    public final void a(short s) {
        this.a(s, null, true);
    }

    public final void j() {
        k k2 = new k(6);
        this.K.a(k2);
        this.t();
    }

    public final void b(int n) {
        this.o = 0;
        this.P = 0;
        this.r = 0;
        this.V = 0L;
        this.p = 0;
        this.o = 0;
        k k2 = new k(115);
        new k(115).l = "Test";
        k2.B = n;
        this.K.a(k2);
        this.t();
    }

    final void c(int n) {
        ++this.P;
        if (n > 0) {
            if (this.P % 9 == 0) {
                long l2 = System.currentTimeMillis();
                if (l2 - this.V <= 1000L) {
                    try {
                        Thread.sleep(1000L);
                    }
                    catch (Throwable throwable) {
                        Throwable throwable2 = throwable;
                        throwable.printStackTrace();
                    }
                }
                this.V = System.currentTimeMillis();
            }
        } else {
            this.V = System.currentTimeMillis();
        }
        k k2 = new k(115);
        k2.A = this.r = n + 1;
        this.K.a(k2);
        this.t();
    }

    public final void a(String object, String string) {
        String string2 = object;
        object = null;
        String[] stringArray = new String[]{string};
        string = string2;
        object = this;
        Object object2 = stringArray;
        if (!(stringArray == null || ((String[])object2).length == 0)) {
            object2 = new k(15);
            new k(15).i = string;
            object2.r = stringArray;
            object2.s = null;
            ((dv)object).K.a(object2);
            super.t();
        }
    }

    public final void a(String string, long l2, short s) {
        k k2 = new k(103);
        new k(103).j = string;
        k2.v = l2;
        k2.K = s;
        this.x = s;
        this.K.a(k2);
        this.t();
    }

    public final void a(long l2, long l3, short s) {
        k k2 = new k(92);
        new k(92).v = l3;
        k2.w = l2;
        this.n = l2;
        k2.K = s;
        this.x = s;
        this.K.a(k2);
        this.t();
    }

    public final void a(long l2, short s) {
        k k2 = new k(91);
        new k(91).v = l2;
        k2.E = s;
        this.K.a(k2);
        this.t();
    }

    public final void b(String string, String string2, short s) {
        this.a(string, 0L, string2, (short)0, s);
    }

    public final void a(String string, long l2, String string2, short s) {
        this.a(string, l2, null, (short)1, s);
    }

    private void a(String string, long l2, String string2, short s, short s2) {
        k k2 = new k(106);
        new k(106).g = string;
        k2.v = l2;
        k2.l = string2;
        k2.L = s;
        k2.J = s2;
        this.K.a(k2);
        this.t();
    }

    final void b(String string, String object) {
        byte[] byArray;
        if (l.a((String)object)) {
            return;
        }
        k k2 = new k(109);
        try {
            byArray = l.c((String)object, null);
        }
        catch (Exception exception) {
            byArray = ((String)object).getBytes();
        }
        if (byArray != null) {
            byte[] byArray2 = new byte[byArray.length + 1];
            object = byArray2;
            byArray2[0] = 5;
            System.arraycopy(byArray, 0, object, 1, byArray.length);
            k2.j = string;
            k2.O = (byte[])object;
            this.K.a(k2);
            this.t();
        }
    }

    public final void a(long l2) {
        k k2 = new k(111);
        new k(111).v = l2;
        this.K.a(k2);
        this.t();
    }

    public final void a(String string, short s) {
        k k2 = new k(116);
        new k(116).m = string;
        k2.K = s;
        this.K.a(k2);
        this.y = s;
        this.t();
    }

    public final void b(String string, short s) {
        if (this.d) {
            this.H.c_();
            return;
        }
        dv dv2 = this;
        this.d = true;
        dv2.l = null;
        dv2.s = 0;
        dv2.u = 0;
        dv2.S = 0;
        dv2.t = 0;
        dv2.H.a(dv2.s, dv2.u);
        this.w = string;
        this.U = s;
        k k2 = new k(114);
        new k(114).l = string;
        k2.K = this.U;
        this.K.a(k2);
        this.t();
    }

    public final void c(String object, short s) {
        this.l();
        this.w = null;
        this.U = s;
        object = new k(114);
        new k(114).l = null;
        ((k)object).K = this.U;
        this.K.a(object);
        this.t();
    }

    public final void k() {
        this.d(0);
    }

    final void d(int n) {
        int n2 = n;
        Object object = this;
        if (((dv)object).d) {
            ++((dv)object).S;
            ((dv)object).H.a(((dv)object).s, ((dv)object).u);
            if (n2 > 0) {
                if (((dv)object).S % 9 == 0) {
                    long l2 = System.currentTimeMillis();
                    if (l2 - ((dv)object).T <= 1000L) {
                        try {
                            Thread.sleep(1000L);
                        }
                        catch (Throwable throwable) {
                            Throwable throwable2 = throwable;
                            throwable.printStackTrace();
                        }
                    }
                    ((dv)object).T = System.currentTimeMillis();
                }
            } else {
                ((dv)object).T = System.currentTimeMillis();
            }
        }
        object = new k(114);
        ((k)object).A = this.t = n + 1;
        ((k)object).K = this.U;
        this.K.a(object);
        this.t();
    }

    public final void l() {
        this.w = null;
        this.d = false;
        this.l = null;
        this.s = 0;
        this.u = 0;
        this.U = 0;
    }

    public final void m() {
        this.e = false;
        this.M = null;
        this.Q = 0;
        this.R = 0;
    }

    public final void b(String string, String object, String string2) {
        object = new k(39);
        new k(39).h = du.e(string);
        ((k)object).l = string2;
        if (!l.a(null)) {
            ((k)object).q = "id=" + null;
        }
        this.K.a(object);
        this.t();
    }

    private void a(String string, short s, String object) {
        object = new k(104);
        new k(104).j = du.e(string);
        ((k)object).H = s;
        ((k)object).g = null;
        this.K.a(object);
        this.t();
    }

    public final void d(String string) {
        this.a(string, (short)1, null);
    }

    public final void e(String string) {
        this.a(string, (short)0, null);
    }

    public final void n() {
        k k2 = new k(100);
        this.K.a(k2);
        this.t();
    }

    public final void a(long l2, int n) {
        k k2 = new k(101);
        new k(101).u = l2;
        k2.z = n;
        this.K.a(k2);
        this.t();
    }

    public final void b(long l2) {
        k k2 = new k(102);
        new k(102).t = l2;
        this.K.a(k2);
        this.t();
    }

    public final void o() {
        k k2 = new k(128);
        this.K.a(k2);
        this.t();
    }

    public final void b(short s) {
        k k2 = new k(124);
        new k(124).K = (short)2412;
        this.K.a(k2);
        this.t();
    }

    public final void a(String string, int n, short s) {
        k k2 = new k(125);
        new k(125).j = du.e(string);
        k2.A = n;
        k2.F = s;
        this.K.a(k2);
        this.t();
    }

    public final void f(String string) {
        if (l.a(string)) {
            return;
        }
        k k2 = new k(134);
        new k(134).e = string;
        this.K.a(k2);
        this.t();
    }

    public final void g(String string) {
        if (l.a(string)) {
            return;
        }
        k k2 = new k(135);
        new k(135).e = string;
        this.K.a(k2);
        this.t();
    }

    public final void p() {
        k k2 = new k(136);
        this.K.a(k2);
        this.t();
    }

    public final void q() {
        k k2 = new k(35);
        this.K.a(k2);
        this.t();
    }

    public final void r() {
        k k2 = new k(137);
        this.K.a(k2);
        this.t();
    }

    public final void s() {
        k k2 = new k(138);
        this.K.a(k2);
        this.t();
    }

    public final void c(String string, String string2) {
        k k2 = new k(65);
        new k(65).j = string2;
        k2.m = string;
        this.K.a(k2);
        this.t();
    }
}
