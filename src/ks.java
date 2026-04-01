/*
 * Decompiled with CFR 0.152.
 */
public final class ks {
    public int a;
    public int b;
    public kr[] c;

    public final boolean a(short s) {
        if (this.c == null) {
            return false;
        }
        int n = 0;
        while (n < this.c.length) {
            if (this.c[n].a == s) {
                return true;
            }
            ++n;
        }
        return false;
    }

    public final int b(short s) {
        int n = 0;
        if (this.c == null) {
            return 0;
        }
        int n2 = 0;
        while (n2 < this.c.length) {
            if (this.c[n2].a == s) {
                ++n;
            }
            ++n2;
        }
        return n;
    }

    public final int a(short s, int n, int n2) {
        int n3 = 0;
        if (this.c == null) {
            return 0;
        }
        int n4 = n;
        int n5 = n2;
        if (n < 0) {
            n4 = -1;
        } else if (n >= this.c.length) {
            n4 = this.c.length - 1;
        }
        if (n2 < 0) {
            n5 = this.c.length;
        }
        if (n4 + 1 >= n5) {
            return 0;
        }
        n = n4 + 1;
        while (n < this.c.length && n < n5) {
            if (this.c[n].a == s) {
                ++n3;
            }
            ++n;
        }
        return n3;
    }

    public final int a(short s, int n) {
        if (this.c == null) {
            return -1;
        }
        int n2 = n;
        if (n < 0) {
            n2 = -1;
        } else if (n >= this.c.length) {
            n2 = this.c.length - 1;
        }
        n = n2 + 1;
        while (n < this.c.length) {
            if (this.c[n].a == s) {
                return n;
            }
            ++n;
        }
        return -1;
    }

    public final int b(short s, int n, int n2) {
        if (this.c == null) {
            return -1;
        }
        int n3 = n;
        int n4 = n2;
        if (n < 0) {
            n3 = -1;
        } else if (n >= this.c.length) {
            n3 = this.c.length - 1;
        }
        if (n2 < 0) {
            n4 = this.c.length;
        }
        if (n3 + 1 >= n4) {
            return -1;
        }
        n = n3 + 1;
        while (n < this.c.length && n < n4) {
            if (this.c[n].a == s) {
                return n;
            }
            ++n;
        }
        return -1;
    }

    public final int b(short s, int n) {
        if (this.c == null) {
            return -1;
        }
        int n2 = 0;
        while (n2 < this.c.length) {
            if (this.c[n2].a == s) {
                if (n == 0) {
                    return n2;
                }
                --n;
            }
            ++n2;
        }
        return -1;
    }

    public final byte[] c(short s) {
        if (this.c == null) {
            return null;
        }
        int n = 0;
        while (n < this.c.length) {
            if (this.c[n].a == s) {
                return this.c[n].b;
            }
            ++n;
        }
        return null;
    }

    public final String d(short s) {
        return ks.a(this.c(s));
    }

    public final long a(short s, long l) {
        return ks.a(this.c(s), l);
    }

    public final int c(short s, int n) {
        return ks.a(this.c(s), n);
    }

    public final byte a(short s, byte by) {
        return ks.a(this.c(s), by);
    }

    public final byte[] c(short s, int n, int n2) {
        int n3 = this.b(s, n, n2);
        s = (short)n3;
        if (n3 >= 0) {
            return this.a((int)s);
        }
        return null;
    }

    public final String d(short s, int n, int n2) {
        int n3 = this.b(s, n, n2);
        s = (short)n3;
        if (n3 >= 0) {
            return this.b((int)s);
        }
        return null;
    }

    public final long a(short s, int n, int n2, long l) {
        int n3 = this.b(s, n, n2);
        s = (short)n3;
        if (n3 >= 0) {
            long l2 = 0L;
            n = s;
            ks ks2 = this;
            return ks.a(ks2.a(n), l2);
        }
        return 0L;
    }

    public final int a(short s, int n, int n2, int n3) {
        int n4 = this.b(s, n, n2);
        s = (short)n4;
        if (n4 >= 0) {
            return this.a((int)s, n3);
        }
        return n3;
    }

    public final byte a(short s, int n, int n2, byte by) {
        int n3 = this.b(s, n, n2);
        s = (short)n3;
        if (n3 >= 0) {
            return this.a((int)s, by);
        }
        return by;
    }

    public final byte[] a(int n) {
        if (this.c == null || n < 0 || n >= this.c.length) {
            return null;
        }
        return this.c[n].b;
    }

    public final String b(int n) {
        return ks.a(this.a(n));
    }

    public final int a(int n, int n2) {
        return ks.a(this.a(n), n2);
    }

    public final byte a(int n, byte by) {
        return ks.a(this.a(n), by);
    }

    private static String a(byte[] byArray) {
        if (byArray == null) {
            return null;
        }
        try {
            return new String(byArray, 0, byArray.length, "UTF-8");
        }
        catch (Throwable throwable) {
            return new String(byArray);
        }
    }

    private static long a(byte[] byArray, long l) {
        if (byArray == null) {
            return l;
        }
        return p.d(byArray);
    }

    private static int a(byte[] byArray, int n) {
        if (byArray == null) {
            return n;
        }
        return p.c(byArray);
    }

    private static byte a(byte[] byArray, byte by) {
        if (byArray == null) {
            return by;
        }
        return byArray[0];
    }

    public final String toString() {
        String string = "";
        string = String.valueOf(string) + " [Service]: " + this.b + " [Length]: " + this.a + " [Number Of Keys]: " + (this.c != null ? String.valueOf(this.c.length) : "Null");
        if (this.c != null) {
            string = String.valueOf(string) + "\n";
            int n = 0;
            while (n < this.c.length) {
                string = String.valueOf(string) + "  - [Key]: " + this.c[n].a + " [Value length]: " + this.c[n].b.length + "\n";
                ++n;
            }
        }
        return string;
    }
}
