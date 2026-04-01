/*
 * Decompiled with CFR 0.152.
 */
public final class lf
extends lb {
    public byte e;
    public int f;
    public int g;
    public int h;
    public int i;
    public int j;
    public int k;
    public int l;
    public int m;
    public int n;
    public int o;
    public int p;
    public int q;
    public int r;
    public int s;
    public int t;
    public int u;
    public int v;
    public int w;
    public int x;
    public int y;
    public int z;
    public int A;
    public int B;
    public int C;
    public lj[] D;
    public lt[] E;
    public lk[] F;
    public int G;
    public int H;
    public int I;
    public int J;
    public int K;
    public int L;
    public int M;
    public int N;
    public int O;
    public int P;
    public boolean Q = false;
    public String R = "";
    public String S = null;
    public String T = "";
    public String U = "";
    public byte V;
    public di W;
    public di X;
    public di Y;
    public long Z;
    public int aa;
    public boolean ab = false;
    public boolean ac = false;
    public int ad;
    public lr[] ae;
    public boolean af = true;

    public final lf a() {
        int n;
        lf lf2 = new lf(this.a);
        new lf(this.a).b = this.b;
        lf2.d = this.d;
        lf2.c = this.c;
        lf2.f = this.f;
        lf2.g = this.g;
        lf2.s = this.s;
        lf2.r = this.r;
        lf2.u = this.u;
        lf2.t = this.t;
        lf2.w = this.w;
        lf2.v = this.v;
        lf2.z = this.z;
        if (this.D != null) {
            lf2.D = new lj[this.D.length];
            n = 0;
            while (n < lf2.D.length) {
                lf2.D[n] = this.D[n].d();
                ++n;
            }
        }
        if (this.F != null) {
            lf2.F = new lk[this.F.length];
            n = 0;
            while (n < lf2.F.length) {
                lf2.F[n] = this.F[n].b();
                ++n;
            }
        }
        if (this.E != null) {
            lf2.E = new lt[this.E.length];
            n = 0;
            while (n < lf2.E.length) {
                lt lt2 = this.E[n];
                lt lt3 = new lt(lt2.a);
                new lt(lt2.a).d = lt2.d;
                lt3.c = lt2.c;
                lt3.b = lt2.b;
                lt3.f = lt2.f;
                lt3.e = lt2.e;
                lt3.g = lt2.g;
                lt3.h = lt2.h;
                lf2.E[n] = lt3;
                ++n;
            }
        }
        lf2.G = this.G;
        lf2.H = this.H;
        lf2.J = this.J;
        lf2.K = this.K;
        lf2.L = this.L;
        lf2.M = this.M;
        lf2.N = this.N;
        lf2.Q = this.Q;
        lf2.ab = this.ab;
        lf2.C = this.C;
        lf2.i = this.i;
        lf2.A = this.A;
        lf2.B = this.B;
        lf2.k = this.k;
        lf2.x = this.x;
        lf2.y = this.y;
        lf2.h = this.h;
        lf2.j = this.j;
        lf2.W = this.W.a();
        lf2.Y = this.Y.a();
        lf2.X = this.X.a();
        return lf2;
    }

    public lf(int n) {
        super(n);
    }

    public final String toString() {
        String string = "[CHARATER ] name = " + this.b + "   id = " + this.a + "\n";
        string = String.valueOf(string) + "Streng = " + this.h + "\n";
        string = String.valueOf(string) + "vitalit = " + this.k + "\n";
        string = String.valueOf(string) + "agility = " + this.j + "\n";
        string = String.valueOf(string) + "magic = " + this.i + "\n";
        string = String.valueOf(string) + "addStreng = " + this.l + "\n";
        string = String.valueOf(string) + "addvitalit = " + this.o + "\n";
        string = String.valueOf(string) + "addagility = " + this.m + "\n";
        string = String.valueOf(string) + "addmagic = " + this.l + "\n";
        string = String.valueOf(string) + "   - HP: " + this.s + " / " + this.r + "\n";
        string = String.valueOf(string) + "   - Mana: " + this.u + " / " + this.t + "\n";
        string = String.valueOf(string) + "   - Power: " + this.w + " / " + this.v + "\n";
        string = String.valueOf(string) + "   - MinDam: " + this.x + " / " + this.y + "\n";
        string = String.valueOf(string) + "   - Dodgerate: " + this.A + " hitrate: " + this.B + "\n";
        string = String.valueOf(string) + "   - Defende: " + this.z + " criticaldamge " + this.C + "\n";
        string = String.valueOf(string) + "   - Skills: ";
        string = String.valueOf(string) + "    - h\u1ec7: " + this.g + "\n";
        int n = 0;
        while (n < this.E.length) {
            string = String.valueOf(string) + this.E[n].toString();
            ++n;
        }
        string = String.valueOf(string) + "\n   - Equinemt: ";
        n = 0;
        while (n < this.D.length) {
            string = String.valueOf(string) + this.D[n].toString() + "\n";
            ++n;
        }
        string = String.valueOf(string) + "\n";
        return string;
    }

    public final lj a(int n) {
        if (this.D != null && this.D.length > 0) {
            int n2 = 0;
            while (n2 < this.D.length) {
                if (this.D[n2].d == n) {
                    return this.D[n2];
                }
                ++n2;
            }
        }
        return null;
    }

    public final lj b() {
        if (this.D != null && this.D.length > 0) {
            int n = 0;
            while (n < this.D.length) {
                if (this.D[n].d == 4) {
                    this.af = true;
                    return this.D[n];
                }
                ++n;
            }
        }
        this.af = false;
        return null;
    }

    public final int c() {
        int n = 0;
        if (this.D != null) {
            int n2 = 0;
            while (n2 < this.D.length) {
                if (this.D[n2].i >= 7 && this.D[n2].d != 8 && this.D[n2].d != 4) {
                    ++n;
                }
                ++n2;
            }
            n2 = 0;
            if (n >= 4) {
                n = 0;
                while (n < this.D.length) {
                    if (this.D[n].d != 8 && this.D[n].d != 4) {
                        if (this.D[n].i < 9) {
                            return 1;
                        }
                        if (this.D[n].i < 11) {
                            return 2;
                        }
                        if (this.D[n].i < 13) {
                            return 3;
                        }
                        if (this.D[n].i >= 13) {
                            ++n2;
                        }
                        if (n2 >= 4) {
                            return 4;
                        }
                    }
                    ++n;
                }
            }
        }
        return 0;
    }
}
