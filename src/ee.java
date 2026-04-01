/*
 * Decompiled with CFR 0.152.
 */
import java.io.InputStream;

final class ee {
    private InputStream a;
    private dw b;

    public ee(InputStream inputStream) {
        this.a = inputStream;
    }

    public final dz a() {
        int n;
        int n2;
        dz dz2 = new dz();
        byte[] byArray = new byte[7];
        int n3 = this.a(byArray);
        if (n3 <= 0) {
            return null;
        }
        int n4 = p.a(byArray[0], byArray[1]);
        dz2.a = n2 = p.a(byArray[2], byArray[3], byArray[4], byArray[5]);
        dz2.b = n = p.a(byArray[6]);
        if (n2 > 0) {
            dy[] dyArray = new dy[n4];
            n4 = 0;
            while (n4 < dyArray.length) {
                byte[] byArray2 = new byte[5];
                if (this.a(byArray2) < 0) {
                    return null;
                }
                short s = (short)p.a(byArray2[0]);
                int n5 = p.a(byArray2[1], byArray2[2], byArray2[3], byArray2[4]);
                if (this.a(byArray2 = new byte[n5]) < 0) {
                    return null;
                }
                dyArray[n4] = new dy();
                dyArray[n4].a = s;
                dyArray[n4].b = byArray2;
                ++n4;
            }
            dz2.c = dyArray;
        }
        int n6 = n3 + n2;
        if (this.b != null) {
            this.b.a(n6);
        }
        return dz2;
    }

    private int a(byte[] byArray) {
        int n = 0;
        while (n < byArray.length) {
            int n2 = this.a.read(byArray, n, byArray.length - n);
            if (n2 < 0) {
                return -1;
            }
            n += n2;
        }
        return n;
    }

    public final void b() {
        try {
            this.a.close();
            return;
        }
        catch (Throwable throwable) {
            return;
        }
    }

    public final void a(dw dw2) {
        this.b = dw2;
    }
}
