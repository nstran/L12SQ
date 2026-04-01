/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.microedition.io.Connector
 *  javax.microedition.io.SocketConnection
 */
import javax.microedition.io.Connector;
import javax.microedition.io.SocketConnection;

public class i {
    private a d;
    public int a;
    public int b;
    public int c;

    public i() {
    }

    public static SocketConnection a(String string, int n) {
        int n2 = 5;
        String string2 = string;
        string2 = (SocketConnection)Connector.open((String)("socket://" + string2 + ":" + n));
        string2.setSocketOption((byte)1, 5);
        return string2;
    }

    public i(String string, int n, int n2, int n3) {
        this.d = new a();
        this.b = -1;
        this.c = 0;
        this.a = n3;
        this.a(string, n, n2);
    }

    public void a(String string, int n, int n2) {
        this.d.a(new dm(string, n, n2));
    }

    public int a() {
        int n = 0;
        int n2 = 0;
        while (n2 < this.d.d()) {
            n += this.a(n2);
            ++n2;
        }
        return n -= 4;
    }

    public int a(int n) {
        return this.b(n).c() + 8;
    }

    public dm b(int n) {
        return (dm)this.d.b(n);
    }

    public lq c(int n) {
        return this.b(n).a();
    }

    public lq b() {
        return this.b(this.c).a();
    }

    public int c() {
        return this.d.d();
    }
}
