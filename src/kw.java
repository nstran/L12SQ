/*
 * Decompiled with CFR 0.152.
 */
import com.mg.smsgame.MGMIDlet;
import java.io.InputStream;

final class kw
implements Runnable,
km {
    public kp c;
    public ko d;
    public kn e;
    private kt f;
    private boolean g = false;

    public kw(InputStream inputStream) {
        this.f = new kt(inputStream);
        this.g = false;
        new Thread(this).start();
    }

    public final void run() {
        int n = 10;
        this.g = false;
        block103: while (!this.g) {
            Object[] objectArray;
            short s2;
            int n2;
            Object object;
            Object object2;
            Object object3;
            Object object4 = null;
            try {
                Object object5;
                block145: {
                    object4 = this;
                    if (((kw)object4).f == null) {
                        cw.a("[SocketReader] input null");
                        object5 = null;
                    } else {
                        object3 = ((kw)object4).f;
                        Object[] objectArray2 = object2 = new byte[7];
                        object4 = ((kt)object3).a;
                        int n3 = j.a((InputStream)object4, objectArray2, 0);
                        object = new ks();
                        if (n3 <= 0) {
                            cw.a("[InputBuffer] readPacket() PACKET_FAIL");
                            object5 = null;
                        } else {
                            int n4;
                            kq.i += n3;
                            n2 = p.a(object2[0], object2[1]);
                            ((ks)object).a = n4 = p.a(object2[2], object2[3], object2[4], object2[5]);
                            ((ks)object).b = n3 = p.a(object2[6]);
                            if (n4 > 0) {
                                kq.i += n4;
                                object2 = new kr[n2];
                                n2 = 0;
                                while (n2 < ((byte[])object2).length) {
                                    InputStream inputStream = ((kt)object3).a;
                                    byte[] byArray = new byte[5];
                                    objectArray2 = byArray;
                                    if (j.a(inputStream, objectArray2, 0) < 0) {
                                        object5 = null;
                                        break block145;
                                    }
                                    s2 = (short)p.a(byArray[0]);
                                    inputStream = ((kt)object3).a;
                                    n4 = p.a(byArray[1], byArray[2], byArray[3], byArray[4]);
                                    objectArray = new byte[n4];
                                    objectArray2 = objectArray;
                                    if (j.a(inputStream, objectArray2, 0) < 0) {
                                        object5 = null;
                                        break block145;
                                    }
                                    object2[n2] = (byte)new kr(s2, (byte[])objectArray);
                                    ++n2;
                                }
                                ((ks)object).c = (kr[])object2;
                            }
                            object5 = object4 = object;
                        }
                    }
                }
                if (object5 == null) {
                    if (--n <= 0) {
                        this.g = true;
                    } else {
                        try {
                            Thread.sleep(3000L);
                        }
                        catch (Throwable throwable) {}
                    }
                } else {
                    n = 10;
                }
            }
            catch (Throwable throwable) {
                object4 = throwable;
                throwable.printStackTrace();
                if (--n <= 0) {
                    this.g = true;
                } else {
                    try {
                        Thread.sleep(1000L);
                    }
                    catch (Throwable throwable2) {}
                }
                object4 = null;
            }
            object3 = object4;
            object4 = this;
            if (object3 == null) {
                cw.a("[PAT] Get NULL packet on SocketReader.process()");
                continue;
            }
            if (((kw)object4).c == null) {
                throw new NullPointerException("Main listener can't be NULL");
            }
            try {
                int n5 = ((ks)object3).b;
                block4 : switch (n5) {
                    case 0: {
                        n5 = ((ks)object3).a((short)0, (byte)-1);
                        object = ((ks)object3).d((short)1);
                        switch (n5) {
                            case 7: {
                                if (((kw)object4).d == null) continue block103;
                                ((kw)object4).d.a();
                                break;
                            }
                            case 3: {
                                ((kw)object4).c.u();
                                break;
                            }
                            case 8: {
                                ((kw)object4).c.b((String)object);
                                break;
                            }
                            default: {
                                ((kw)object4).c.a(n5, (String)object);
                                break;
                            }
                        }
                        continue block103;
                    }
                    case 1: {
                        kq.a().c = 3;
                        break;
                    }
                    case 2: {
                        kq.a().a(((ks)object3).d((short)3));
                        break;
                    }
                    case 5: {
                        super.c((ks)object3);
                        break;
                    }
                    case 3: {
                        object4 = ((ks)object3).c((short)2);
                        object = kq.a().d;
                        String string = kq.a().e;
                        byte[] byArray = y.a((byte[])object4, string);
                        kq.a().a((String)object, byArray, (byte[])object4);
                        break;
                    }
                    case 8: {
                        super.f((ks)object3);
                        break;
                    }
                    case 9: {
                        byte by = ((ks)object3).a((short)134, (byte)-1);
                        n5 = by;
                        if (by > 0) {
                            lf lf2 = super.a((ks)object3);
                            ((kw)object4).c.a(lf2, (byte)n5);
                            break;
                        }
                        super.b((ks)object3);
                        break;
                    }
                    case 30: {
                        super.r((ks)object3);
                        break;
                    }
                    case 29: {
                        String string = ((ks)object3).d((short)9);
                        String string2 = ((ks)object3).d((short)20);
                        s2 = ((ks)object3).a((short)21, 0, -1, 0);
                        ((kw)object4).c.a(string, string2, (int)s2);
                        break;
                    }
                    case 10: {
                        super.b((ks)object3);
                        ((kw)object4).c.S();
                        break;
                    }
                    case 27: {
                        ((kw)object4).c.S();
                        break;
                    }
                    case 36: {
                        super.D((ks)object3);
                        break;
                    }
                    case 42: {
                        super.s((ks)object3);
                        break;
                    }
                    case 37: {
                        byte by = ((ks)object3).a((short)89, (byte)-1);
                        if (by == 0) {
                            super.b((ks)object3);
                            break;
                        }
                        if (by != 1 && by != 2) continue block103;
                        objectArray = new String[((ks)object3).b((short)83)];
                        n5 = 0;
                        while (n5 < objectArray.length) {
                            objectArray[n5] = ((ks)object3).b(((ks)object3).b((short)83, n5));
                            ++n5;
                        }
                        if (by == 1) {
                            ((kw)object4).c.c((String[])objectArray);
                            break;
                        }
                        ((kw)object4).c.d((String[])objectArray);
                        break;
                    }
                    case 48: {
                        String string = ((ks)object3).d((short)83);
                        int n6 = ((ks)object3).c((short)114, -1);
                        int n7 = ((ks)object3).c((short)106, -1);
                        ((kw)object4).c.b(string, n6, n7);
                        break;
                    }
                    case 11: {
                        super.d((ks)object3);
                        break;
                    }
                    case 15: {
                        super.e((ks)object3);
                        break;
                    }
                    case 43: {
                        super.g((ks)object3);
                        break;
                    }
                    case 6: {
                        n5 = ((ks)object3).c((short)4, 0);
                        int n8 = ((ks)object3).a(((ks)object3).b((short)7, 0), -1);
                        if (n8 < 0) {
                            int n9 = ((ks)object3).a(((ks)object3).b((short)6, 0), -1);
                            int n10 = ((ks)object3).a(((ks)object3).b((short)5, 0), -1);
                            if (((kw)object4).e == null) continue block103;
                            ((kw)object4).e.a(n5, n9, n10);
                            break;
                        }
                        byte[] byArray = ((ks)object3).c((short)8);
                        if (((kw)object4).e == null) continue block103;
                        ((kw)object4).e.a(n5, n8, byArray);
                        break;
                    }
                    case 13: {
                        String string = ((ks)object3).d((short)20);
                        int n11 = p.c(((ks)object3).c((short)21));
                        byte by = ((ks)object3).c((short)22)[0];
                        ((kw)object4).c.a(string, n11, (int)by);
                        break;
                    }
                    case 16: {
                        String string = ((ks)object3).d((short)9);
                        objectArray = ((ks)object3).d((short)1);
                        ((kw)object4).c.b(string, (String)objectArray);
                        break;
                    }
                    case 17: {
                        String string = ((ks)object3).d((short)28);
                        object = ((ks)object3).d((short)9);
                        String string3 = ((ks)object3).d((short)1);
                        lf lf3 = super.a((ks)object3, 0, -1);
                        super.a((ks)object3, 0, -1).b = object;
                        long l = ((ks)object3).a((short)132, 0L);
                        byte by = ((ks)object3).a((short)167, (byte)0);
                        n2 = ((ks)object3).a((short)169, (byte)0);
                        ((kw)object4).c.a(lf3, string3, l, string, by > 0, n2 > 0);
                        break;
                    }
                    case 22: {
                        super.k((ks)object3);
                        break;
                    }
                    case 18: {
                        n5 = ((ks)object3).c((short)41, 0);
                        object = new no(n5, 6);
                        boolean bl = ((ks)object3).a((short)32);
                        if (((ks)object3).a((short)39)) {
                            ((no)object).G = true;
                        } else {
                            ((no)object).d = bl;
                        }
                        if (((kw)object4).d == null) continue block103;
                        ((kw)object4).d.a((no)object);
                        break;
                    }
                    case 19: {
                        short s3;
                        n5 = ((ks)object3).c((short)41, 0);
                        object = new no(n5, 0);
                        new no(n5, 0).k = ((ks)object3).a((short)44, 0L);
                        int n12 = ((ks)object3).a(((ks)object3).b((short)33, 0), -1);
                        int n13 = ((ks)object3).a(((ks)object3).b((short)34, 0), -1);
                        int n14 = ((ks)object3).a(((ks)object3).b((short)33, 1), -1);
                        s2 = s3 = ((ks)object3).a(((ks)object3).b((short)34, 1), -1);
                        int n15 = n14;
                        n5 = n13;
                        n2 = n12;
                        Object object6 = object;
                        ((no)object).l = n2;
                        ((no)object6).n = n15;
                        ((no)object6).m = n5;
                        ((no)object6).o = s2;
                        super.a((ks)object3, (no)object);
                        break;
                    }
                    case 24: {
                        cw.a("[processUpdateMatch]======================================");
                        n5 = ((ks)object3).c((short)41, 0);
                        object = new no(n5, 8);
                        kw.b((ks)object3, (no)object);
                        boolean bl = ((ks)object3).a((short)32);
                        byte[] byArray = ((ks)object3).c((short)39);
                        if (byArray != null) {
                            ((kw)object4).d.a((no)object);
                            ((no)object).G = true;
                            super.a((ks)object3, byArray[0], ((no)object).b);
                            break;
                        }
                        ((no)object).d = bl;
                        ((kw)object4).d.a((no)object);
                        break;
                    }
                    case 20: {
                        super.n((ks)object3);
                        break;
                    }
                    case 44: {
                        int n16;
                        n5 = ((ks)object3).c((short)41, 0);
                        object = new no(n5, 1);
                        new no(n5, 1).k = ((ks)object3).a((short)44, 0L);
                        ((no)object).e = n16 = ((ks)object3).c((short)114, 0);
                        super.a((ks)object3, (no)object);
                        break;
                    }
                    case 47: {
                        if (((kw)object4).d == null) continue block103;
                        no no2 = new no(7);
                        new no(7).m = ((ks)object3).c((short)34, 2);
                        no2.l = ((ks)object3).c((short)33, 2);
                        if (((kw)object4).d == null) continue block103;
                        ((kw)object4).d.a(no2);
                        break;
                    }
                    case 84: {
                        n5 = ((ks)object3).c((short)114, -1);
                        int n17 = ((ks)object3).c((short)106, -1);
                        ((kw)object4).c.e(n5, n17);
                        super.b((ks)object3);
                        break;
                    }
                    case 40: {
                        if (((kw)object4).d == null) continue block103;
                        byte[] byArray = ((ks)object3).c((short)39);
                        if (byArray == null) {
                            ((kw)object4).d.a(-1);
                            break;
                        }
                        ((kw)object4).d.a(byArray[0]);
                        super.a((ks)object3, byArray[0], -1);
                        break;
                    }
                    case 25: {
                        String string = ((ks)object3).d((short)9);
                        objectArray = ((ks)object3).d((short)1);
                        if (((kw)object4).d != null) {
                            ((kw)object4).d.a(string, (String)objectArray);
                        }
                        ((kw)object4).c.c(string, (String)objectArray);
                        break;
                    }
                    case 12: {
                        String string = ((ks)object3).d((short)192);
                        object = ((ks)object3).d((short)1);
                        long l = ((ks)object3).c((short)157, 0);
                        String string4 = ((ks)object3).d((short)9);
                        byte by = ((ks)object3).a((short)40, (byte)-1);
                        switch (by) {
                            case 0: {
                                ((kw)object4).c.a(string, string4);
                                break block4;
                            }
                            case 1: {
                                ((kw)object4).c.a(string, kw.d((ks)object3, ((ks)object3).b((short)9, 0), -1), (String)object, l);
                                break block4;
                            }
                            case 2: {
                                ((kw)object4).c.b(string, kw.d((ks)object3, ((ks)object3).b((short)9, 0), -1), (String)object, l);
                                break block4;
                            }
                            case 3: {
                                super.i((ks)object3);
                            }
                        }
                        break;
                    }
                    case 21: {
                        n5 = ((ks)object3).c((short)41, 0);
                        object = new no(n5, 3);
                        super.a((ks)object3, (no)object);
                        break;
                    }
                    case 28: {
                        ((kw)object4).c.T();
                        break;
                    }
                    case 23: {
                        super.m((ks)object3);
                        break;
                    }
                    case 31: {
                        super.h((ks)object3);
                        break;
                    }
                    case 32: {
                        ((kw)object4).c.y();
                        break;
                    }
                    case 41: {
                        ((kw)object4).c.z();
                        break;
                    }
                    case 33: {
                        super.o((ks)object3);
                        break;
                    }
                    case 38: {
                        kw.p((ks)object3);
                        break;
                    }
                    case 34: {
                        ns.a(new nr(((ks)object3).c((short)80, -1), ((ks)object3).d((short)81), ""));
                        String string = ((ks)object3).d((short)149);
                        ((kw)object4).c.a(string, (byte)0);
                        break;
                    }
                    case 35: {
                        super.q((ks)object3);
                        break;
                    }
                    case 45: {
                        ((kw)object4).c.c(((ks)object3).d((short)1));
                        break;
                    }
                    case 46: {
                        kp kp2 = ((kw)object4).c;
                        ((ks)object3).d((short)9);
                        kp2.a(((ks)object3).a((short)132, 0L));
                        break;
                    }
                    case 55: {
                        super.t((ks)object3);
                        break;
                    }
                    case 56: {
                        super.w((ks)object3);
                        break;
                    }
                    case 57: {
                        super.u((ks)object3);
                        break;
                    }
                    case 7: {
                        n5 = 0;
                        byte by = ((ks)object3).a((short)147, (byte)-1);
                        switch (by) {
                            case 0: {
                                n5 = ((ks)object3).a((short)165, (byte)-1);
                                ((kw)object4).c.c(n5 == 1);
                                break block4;
                            }
                            case 1: {
                                n5 = ((ks)object3).a((short)166, (byte)-1);
                                ((kw)object4).c.d(n5 == 1);
                            }
                        }
                        break;
                    }
                    case 125: {
                        ((kw)object4).c.a(((ks)object3).d((short)149), (byte)0);
                        break;
                    }
                    case 52: {
                        super.x((ks)object3);
                        break;
                    }
                    case 128: {
                        ((kw)object4).c.s(((ks)object3).d((short)1));
                        break;
                    }
                    case 129: {
                        byte by = ((ks)object3).a((short)147, (byte)-1);
                        n5 = by;
                        switch (by) {
                            case 0: {
                                ((kw)object4).c.a(((ks)object3).d((short)162), ((ks)object3).c((short)161) != null, ((ks)object3).c((short)163) != null);
                                break block4;
                            }
                            case 1: {
                                ((kw)object4).c.a(((ks)object3).d((short)162), ((ks)object3).c((short)101) != null, ((ks)object3).c((short)161) != null, ((ks)object3).c((short)163) != null);
                                break block4;
                            }
                            case 2: {
                                ((kw)object4).c.b(((ks)object3).d((short)162), ((ks)object3).c((short)101) != null, ((ks)object3).c((short)161) != null, ((ks)object3).c((short)163) != null);
                            }
                        }
                        break;
                    }
                    case 64: {
                        super.j((ks)object3);
                        break;
                    }
                    case 65: {
                        String string = ((ks)object3).d((short)192);
                        object = ((ks)object3).d((short)1);
                        long l = ((ks)object3).a((short)157, 0L);
                        int n18 = ((ks)object3).c((short)194, 0);
                        cw.a("[processJoinRoom]  " + (String)object + ":  " + l);
                        ((kw)object4).c.a(string, (String)object, l, n18);
                        break;
                    }
                    case 130: {
                        byte[] byArray = ((ks)object3).c((short)176);
                        object = ((ks)object3).c((short)2);
                        ((kw)object4).c.a(byArray, (byte[])object);
                        break;
                    }
                    case 131: {
                        String string = ((ks)object3).d((short)1);
                        if (((kw)object4).c == null) continue block103;
                        ((kw)object4).c.o(string);
                        break;
                    }
                    case 4: {
                        if (((kw)object4).c == null) continue block103;
                        ((kw)object4).c.R();
                        break;
                    }
                    case 51: {
                        lk[] lkArray;
                        n5 = ((ks)object3).c((short)114, 0);
                        object = ((ks)object3).d((short)1);
                        String string = ((ks)object3).d((short)83);
                        if (n5 > 0) {
                            gr.a(n5, 1);
                        }
                        if (object != null) {
                            ((kw)object4).c.w((String)object);
                        }
                        if (string != null) {
                            int n19 = ((ks)object3).b((short)83, 0);
                            int n20 = ((ks)object3).a((short)83, n19);
                            lj lj2 = kw.a((ks)object3, n19, n20, true);
                            ((kw)object4).c.c(lj2);
                        }
                        if ((lkArray = kw.a((ks)object3, 1)).length <= 0) continue block103;
                        ((kw)object4).c.a(lkArray);
                        break;
                    }
                    case 83: {
                        n5 = ((ks)object3).c((short)114, -1);
                        int n21 = ((ks)object3).c((short)106, 0);
                        ((kw)object4).c.f(n5, n21);
                        break;
                    }
                    case 99: {
                        String string = ((ks)object3).d((short)186);
                        object = ((ks)object3).d((short)1);
                        if (cw.b()) {
                            cw.a("[processRequestUpgradeEquipment]  session  " + string + "  message  " + (String)object);
                        }
                        if (((kw)object4).c == null) continue block103;
                        ((kw)object4).c.e(string, (String)object);
                        break;
                    }
                    case 100: {
                        ((ks)object3).d((short)186);
                        byte by = ((ks)object3).a((short)187, (byte)0);
                        String string = ((ks)object3).d((short)83);
                        int n22 = ((ks)object3).c((short)114, -1);
                        int n23 = ((ks)object3).c((short)106, -1);
                        long l = ((ks)object3).a((short)132, 0L);
                        String string5 = ((ks)object3).d((short)1);
                        n5 = ((ks)object3).a((short)188, (byte)0);
                        if (string != null) {
                            if (by == 0) {
                                ((kw)object4).c.a(string, string5, (byte)n5, l);
                            } else {
                                ((kw)object4).c.b(string, string5, (byte)n5, l);
                            }
                        }
                        if (n22 <= 0) continue block103;
                        if (by == 0) {
                            ((kw)object4).c.a(string5, (byte)n5, l);
                            break;
                        }
                        ((kw)object4).c.a(n22, n23, string5, (byte)n5, l);
                        break;
                    }
                    case 101: {
                        super.B((ks)object3);
                        break;
                    }
                    case 96: {
                        String string = ((ks)object3).d((short)186);
                        object = ((ks)object3).d((short)83);
                        String string6 = ((ks)object3).d((short)1);
                        if (cw.b()) {
                            cw.a("[processRequestUpgradeEquipment]  equipKey  " + (String)object + " message" + string6);
                        }
                        if (((kw)object4).c == null) continue block103;
                        ((kw)object4).c.a(string, (String)object, string6);
                        break;
                    }
                    case 97: {
                        ((ks)object3).d((short)186);
                        byte by = ((ks)object3).a((short)187, (byte)0);
                        String string = ((ks)object3).d((short)83);
                        int n24 = ((ks)object3).c((short)114, -1);
                        int n25 = ((ks)object3).c((short)106, -1);
                        long l = ((ks)object3).a((short)132, 0L);
                        String string7 = ((ks)object3).d((short)1);
                        n5 = ((ks)object3).a((short)188, (byte)0);
                        if (cw.b()) {
                            cw.a("[processModifiedUpgradeEquipment]readyStatus   " + n5);
                        }
                        if (string != null) {
                            if (by == 0) {
                                ((kw)object4).c.d(string, string7, (byte)n5, l);
                            } else {
                                ((kw)object4).c.c(string, string7, (byte)n5, l);
                            }
                        }
                        if (n24 <= 0) continue block103;
                        if (by == 0) {
                            ((kw)object4).c.b(string7, (byte)n5, l);
                            break;
                        }
                        ((kw)object4).c.b(n24, n25, string7, (byte)n5, l);
                        break;
                    }
                    case 98: {
                        super.C((ks)object3);
                        break;
                    }
                    case 112: {
                        int n26;
                        String string = ((ks)object3).d((short)83);
                        if (string != null) {
                            ((ks)object3).d((short)175);
                            long l = ((ks)object3).a((short)157, 0L);
                            ((kw)object4).c.a(string, l);
                        }
                        if ((n26 = ((ks)object3).c((short)114, 0)) <= 0) continue block103;
                        ((ks)object3).d((short)175);
                        int n27 = ((ks)object3).c((short)106, 0);
                        long l = ((ks)object3).a((short)157, 0L);
                        ((kw)object4).c.a(n26, n27, l);
                        break;
                    }
                    case 116: {
                        super.z((ks)object3);
                        break;
                    }
                    case 113: {
                        super.y((ks)object3);
                        break;
                    }
                    case 114: {
                        n5 = ((ks)object3).a((short)152, (byte)-1);
                        int n28 = ((ks)object3).c((short)106, 0);
                        cw.a("[processListMarketProducts]catid == " + n5 + "qty ==" + n28);
                        ((kw)object4).c.a(n5, n28, super.v((ks)object3));
                        break;
                    }
                    case 115: {
                        super.A((ks)object3);
                        break;
                    }
                    case 132: {
                        super.E((ks)object3);
                        break;
                    }
                    case 133: {
                        String string = ((ks)object3).d((short)1);
                        ((kw)object4).c.x(string);
                    }
                }
            }
            catch (Throwable throwable) {
                object2 = throwable;
                throwable.printStackTrace();
            }
        }
    }

    private lf a(ks ks2, int n, int n2) {
        int n3;
        int n4;
        int n5;
        n = ks2.a((short)15, 0, -1, (byte)-1);
        lf lf2 = new lf(n);
        new lf(n).b = ks2.b(0);
        lf2.c = ks2.d((short)26, 0, -1);
        lf2.f = ks2.a((short)16, 0, -1, (byte)0);
        lf2.g = ks2.a((short)15, 0, -1, (byte)0);
        lf2.G = ks2.a((short)27, 0, -1, 0);
        lf2.e = ks2.a((short)24, 0, -1, (byte)0);
        lf2.H = ks2.a((short)43, 0, -1, 0);
        lf2.O = ks2.a((short)108, 0, -1, 0);
        lf2.P = ks2.a((short)109, 0, -1, 0);
        lf2.s = ks2.a((short)17, 0, -1, 0);
        lf2.r = ks2.a((short)47, 0, -1, 1);
        lf2.u = ks2.a((short)18, 0, -1, 0);
        lf2.t = ks2.a((short)48, 0, -1, 1);
        lf2.w = ks2.a((short)45, 0, -1, 0);
        lf2.v = ks2.a((short)49, 0, -1, 1);
        lf2.U = ks2.d((short)151);
        if (lf2.U == null) {
            lf2.U = "Ch\u01b0a c\u00f3";
        }
        lf2.T = "Ch\u01b0a c\u00f3";
        if (lf2.S == null) {
            lf2.S = lf2.G > 100 && lf2.G <= 200 ? "\u0110\u1ea1i Hi\u1ec7p" : (lf2.G > 200 ? "Chi\u1ebfn V\u01b0\u01a1ng" : "H\u00e0o Ki\u1ec7t");
        }
        lf2.ad = ks2.c((short)160, 0);
        n2 = ks2.a((short)64, 0, -1);
        lf2.E = new lt[n2];
        if (n2 > 0) {
            n5 = ks2.b((short)64, 0, -1);
            n4 = 0;
            while (n4 < n2) {
                n3 = ks2.a((short)64, n5);
                lf2.E[n4] = new lt(ks2.a(n5, -1));
                n5 = n3;
                ++n4;
            }
        }
        lf2.D = new lj[ks2.a((short)83, 0, -1)];
        n5 = ks2.a((short)83, 0);
        n4 = 0;
        while (n4 < lf2.D.length) {
            n3 = ks2.a((short)83, n5);
            lf2.D[n4] = kw.a(ks2, n5, n3, false);
            n5 = n3;
            ++n4;
        }
        n4 = ks2.a((short)90, 0, -1);
        n5 = ks2.a((short)90, 0);
        n3 = 0;
        while (n3 < n4) {
            n2 = ks2.a((short)90, n5);
            int n6 = ks2.a(n5, 0);
            byte by = ks2.a((short)91, n5, n2, (byte)0);
            di di2 = new di(n6);
            int n7 = ks2.a((short)93, n5, n2, 0);
            byte[] byArray = ks2.c((short)95, n5, n2);
            di2.d = new dj(n7, byArray);
            di2.f = new dj[]{di2.d};
            n7 = ks2.a((short)96, n5, n2, 0);
            byte[] byArray2 = ks2.c((short)98, n5, n2);
            di2.e = new dj(n7, byArray2);
            switch (by) {
                case 0: {
                    lf2.W = di2;
                    break;
                }
                case 1: {
                    lf2.X = di2;
                    break;
                }
                case 2: {
                    lf2.Y = di2;
                }
            }
            n5 = n2;
            ++n3;
        }
        return lf2;
    }

    private lf b(ks ks2, int n, int n2) {
        try {
            int n3;
            int n4;
            int n5;
            int n6;
            int n7;
            int n8;
            byte by = ks2.a((short)15, n, n2, (byte)-1);
            lf lf2 = new lf(by);
            new lf(by).b = ks2.b(n);
            lf2.c = ks2.d((short)26, n, n2);
            lf2.Q = ks2.c((short)36, n, n2) != null;
            lf2.f = ks2.a((short)16, n, n2, (byte)0);
            lf2.g = ks2.a((short)15, n, n2, (byte)0);
            lf2.G = ks2.a((short)27, n, n2, 0);
            lf2.aa = ks2.a((short)4, n, n2, 0);
            lf2.V = ks2.a((short)19, n, n2, (byte)0);
            lf2.s = ks2.a((short)17, n, n2, 0);
            lf2.r = ks2.a((short)47, n, n2, 1);
            lf2.u = ks2.a((short)18, n, n2, 0);
            lf2.t = ks2.a((short)48, n, n2, 1);
            lf2.w = ks2.a((short)45, n, n2, 0);
            lf2.v = ks2.a((short)49, n, n2, 1);
            lf2.U = ks2.d((short)151);
            if (lf2.U == null) {
                lf2.U = "Ch\u01b0a c\u00f3";
            }
            lf2.T = "Ch\u01b0a c\u00f3";
            if (lf2.S == null) {
                lf2.S = lf2.G > 100 && lf2.G <= 200 ? "\u0110\u1ea1i Hi\u1ec7p" : (lf2.G > 200 ? "Chi\u1ebfn V\u01b0\u01a1ng" : "H\u00e0o Ki\u1ec7t");
            }
            int n9 = ks2.a((short)64, n, n2);
            lf2.E = new lt[n9];
            if (n9 > 0) {
                n8 = ks2.b((short)64, n, n2);
                n7 = 0;
                while (n7 < n9) {
                    n6 = ks2.a((short)64, n8);
                    lf2.E[n7] = new lt(ks2.a(n8, -1));
                    lf2.E[n7].b = ks2.d((short)26, n8, n6);
                    lf2.E[n7].d = ks2.d((short)66, n8, n6);
                    lf2.E[n7].f = ks2.a((short)67, n8, n6, -1);
                    lf2.E[n7].e = ks2.a((short)68, n8, n6, -1);
                    n5 = ks2.a((short)69, n8, n6);
                    lf2.E[n7].h = new String[n5];
                    n4 = ks2.b((short)69, n8, n6);
                    n3 = 0;
                    while (n3 < n5) {
                        int n10 = ks2.a((short)69, n4);
                        lf2.E[n7].h[n3] = ks2.b(n4);
                        n4 = n10;
                        ++n3;
                    }
                    n8 = n6;
                    ++n7;
                }
            }
            lf2.D = new lj[ks2.a((short)83, n, n2)];
            n8 = ks2.a((short)83, n);
            n7 = 0;
            while (n7 < lf2.D.length) {
                n6 = ks2.a((short)83, n8);
                lf2.D[n7] = kw.a(ks2, n8, n6, false);
                n8 = n6;
                ++n7;
            }
            ks ks3 = ks2;
            kw kw2 = this;
            lf2.F = kw.a(ks3, 0);
            n7 = ks2.a((short)90, n, n2);
            int n11 = ks2.a((short)90, n);
            n6 = 0;
            while (n6 < n7) {
                n5 = ks2.a((short)90, n11);
                n4 = ks2.a(n11, 0);
                n3 = ks2.a((short)91, n11, n5, (byte)0);
                di di2 = new di(n4);
                n = ks2.a((short)93, n11, n5, 0);
                byte[] byArray = ks2.c((short)95, n11, n5);
                di2.d = new dj(n, byArray);
                di2.f = new dj[]{di2.d};
                n = ks2.a((short)96, n11, n5, 0);
                byArray = ks2.c((short)98, n11, n5);
                di2.e = new dj(n, byArray);
                switch (n3) {
                    case 0: {
                        lf2.W = di2;
                        break;
                    }
                    case 1: {
                        lf2.X = di2;
                        break;
                    }
                    case 2: {
                        lf2.Y = di2;
                    }
                }
                n11 = n5;
                ++n6;
            }
            return lf2;
        }
        catch (Exception exception) {
            Exception exception2 = exception;
            exception.printStackTrace();
            MGMIDlet.f().d();
            return null;
        }
    }

    private lf c(ks ks2, int n, int n2) {
        try {
            int n3;
            int n4;
            int n5;
            lf lf2 = new lf(0);
            new lf(0).b = ks2.b(n);
            lf2.c = ks2.d((short)26, n, n2);
            lf2.Q = ks2.c((short)36, n, n2) != null;
            lf2.f = ks2.a((short)16, n, n2, (byte)0);
            lf2.g = ks2.a((short)15, n, n2, (byte)0);
            lf2.G = ks2.a((short)27, n, n2, 0);
            lf2.aa = ks2.a((short)4, n, n2, 0);
            lf2.V = ks2.a((short)19, n, n2, (byte)0);
            lf2.s = ks2.a((short)17, n, n2, 0);
            lf2.r = ks2.a((short)47, n, n2, 1);
            lf2.u = ks2.a((short)18, n, n2, 0);
            lf2.t = ks2.a((short)48, n, n2, 1);
            lf2.w = ks2.a((short)45, n, n2, 0);
            lf2.v = ks2.a((short)49, n, n2, 1);
            lf2.U = ks2.d((short)151);
            if (lf2.U == null) {
                lf2.U = "Ch\u01b0a c\u00f3";
            }
            lf2.T = "Ch\u01b0a c\u00f3";
            if (lf2.S == null) {
                lf2.S = lf2.G > 100 && lf2.G <= 200 ? "\u0110\u1ea1i Hi\u1ec7p" : (lf2.G > 200 ? "Chi\u1ebfn V\u01b0\u01a1ng" : "H\u00e0o Ki\u1ec7t");
            }
            int n6 = ks2.a((short)64, n, n2);
            lf2.E = new lt[n6];
            if (n6 > 0) {
                n5 = ks2.b((short)64, n, n2);
                n4 = 0;
                while (n4 < n6) {
                    n3 = ks2.a((short)64, n5);
                    lf2.E[n4] = new lt(ks2.a(n5, -1));
                    n5 = n3;
                    ++n4;
                }
            }
            lf2.D = new lj[ks2.a((short)83, n, n2)];
            n5 = ks2.a((short)83, n);
            n4 = 0;
            while (n4 < lf2.D.length) {
                n3 = ks2.a((short)83, n5);
                lf2.D[n4] = kw.a(ks2, n5, n3, false);
                n5 = n3;
                ++n4;
            }
            lf2.F = new lk[ks2.a((short)114, n, n2)];
            n5 = ks2.a((short)114, n);
            n4 = 0;
            while (n4 < lf2.F.length) {
                n3 = ks2.a((short)114, n5);
                n6 = ks2.a(n5, 0);
                lf2.F[n4] = new lk(n6);
                lf2.F[n4].g = ks2.a((short)106, n5, n3, 0);
                lf2.F[n4].j = ks2.a((short)4, n5, n3, 0);
                n5 = n3;
                ++n4;
            }
            n4 = ks2.a((short)90, n, n2);
            n5 = ks2.a((short)90, n);
            n3 = 0;
            while (n3 < n4) {
                n6 = ks2.a((short)90, n5);
                int n7 = ks2.a(n5, 0);
                n2 = ks2.a((short)91, n5, n6, (byte)0);
                di di2 = new di(n7);
                int n8 = ks2.a((short)93, n5, n6, 0);
                byte[] byArray = ks2.c((short)95, n5, n6);
                di2.d = new dj(n8, byArray);
                di2.f = new dj[]{di2.d};
                n8 = ks2.a((short)96, n5, n6, 0);
                byte[] byArray2 = ks2.c((short)98, n5, n6);
                di2.e = new dj(n8, byArray2);
                switch (n2) {
                    case 0: {
                        lf2.W = di2;
                        break;
                    }
                    case 1: {
                        lf2.X = di2;
                        break;
                    }
                    case 2: {
                        lf2.Y = di2;
                    }
                }
                n5 = n6;
                ++n3;
            }
            return lf2;
        }
        catch (Exception exception) {
            Exception exception2 = exception;
            exception.printStackTrace();
            MGMIDlet.f().d();
            return null;
        }
    }

    private lf a(ks ks2) {
        int n;
        int n2;
        int n3;
        int n4;
        byte by = ks2.a((short)15, 0, -1, (byte)-1);
        lf lf2 = new lf(by);
        new lf(by).b = ks2.d((short)9);
        lf2.c = ks2.d((short)26);
        lf2.g = ks2.a((short)15, (byte)0);
        lf2.f = ks2.a((short)16, (byte)0);
        lf2.G = ks2.c((short)27, 0);
        lf2.s = ks2.c((short)17, 0);
        lf2.r = ks2.c((short)47, 0);
        lf2.u = ks2.c((short)18, 0);
        lf2.t = ks2.c((short)48, 0);
        lf2.h = ks2.c((short)118, 0);
        lf2.j = ks2.c((short)119, 0);
        lf2.i = ks2.c((short)120, 0);
        lf2.k = ks2.c((short)121, 0);
        lf2.l = ks2.c((short)196, 0);
        lf2.m = ks2.c((short)197, 0);
        lf2.n = ks2.c((short)198, 0);
        lf2.o = ks2.c((short)199, 0);
        lf2.p = ks2.c((short)116, 0);
        lf2.q = ks2.c((short)115, 0);
        cw.a("[readFighterInf] addHealth " + lf2.p + " heatlPec  " + lf2.q);
        lf2.J = ks2.c((short)42, 0);
        lf2.H = ks2.c((short)43, 0);
        lf2.I = ks2.c((short)99, 10000);
        lf2.K = ks2.c((short)53, 0);
        lf2.L = ks2.c((short)76, 0);
        lf2.M = ks2.c((short)73, 0);
        lf2.N = ks2.c((short)74, 0);
        lf2.O = ks2.c((short)108, 0);
        lf2.P = ks2.c((short)109, 0);
        lf2.U = ks2.d((short)151);
        if (lf2.U == null) {
            lf2.U = "Ch\u01b0a c\u00f3";
        }
        lf2.T = "Ch\u01b0a c\u00f3";
        if (lf2.S == null) {
            lf2.S = lf2.G > 100 && lf2.G <= 200 ? "\u0110\u1ea1i Hi\u1ec7p" : (lf2.G > 200 ? "Chi\u1ebfn V\u01b0\u01a1ng" : "H\u00e0o Ki\u1ec7t");
        }
        lf2.ad = ks2.c((short)160, 0);
        lf2.ab = ks2.a((short)165, (byte)0) == 1;
        lf2.ac = ks2.a((short)166, (byte)0) == 1;
        int n5 = ks2.b((short)64);
        lf2.E = new lt[n5];
        if (n5 > 0) {
            n4 = ks2.a((short)64, 0);
            n3 = 0;
            while (n3 < n5) {
                n2 = ks2.a((short)64, n4);
                lf2.E[n3] = new lt(ks2.a(n4, -1));
                lf2.E[n3].f = ks2.a((short)67, n4, n2, -1);
                lf2.E[n3].e = ks2.a((short)68, n4, n2, -1);
                n4 = n2;
                ++n3;
            }
        }
        lf2.D = new lj[ks2.b((short)83)];
        n4 = ks2.b((short)83, 0);
        n3 = 0;
        while (n3 < lf2.D.length) {
            n2 = ks2.a((short)83, n4);
            lf2.D[n3] = kw.a(ks2, n4, n2, false);
            n4 = n2;
            ++n3;
        }
        lf2.F = this.l(ks2);
        n3 = ks2.b((short)90);
        n4 = ks2.b((short)90, 0);
        n2 = 0;
        while (n2 < n3) {
            n5 = ks2.a((short)90, n4);
            int n6 = ks2.a(n4, 0);
            n = ks2.a((short)91, n4, n5, (byte)0);
            di di2 = new di(n6);
            int n7 = ks2.a((short)93, n4, n5, 0);
            byte[] byArray = ks2.c((short)95, n4, n5);
            di2.d = new dj(n7, byArray);
            di2.f = new dj[]{di2.d};
            n7 = ks2.a((short)96, n4, n5, 0);
            byte[] byArray2 = ks2.c((short)98, n4, n5);
            di2.e = new dj(n7, byArray2);
            switch (n) {
                case 0: {
                    lf2.W = di2;
                    break;
                }
                case 1: {
                    lf2.X = di2;
                    break;
                }
                case 2: {
                    lf2.Y = di2;
                }
            }
            n4 = n5;
            ++n2;
        }
        n2 = ks2.b((short)158);
        lr[] lrArray = new lr[n2];
        n4 = ks2.b((short)158, 0);
        int n8 = 0;
        while (n8 < n2) {
            n = ks2.a((short)158, n4);
            lrArray[n8] = new lr();
            ks2.d((short)158, n4, n);
            lrArray[n8].a = ks2.a((short)4, n4, n, 0);
            lrArray[n8].b = ks2.a((short)157, n4, n, 0L);
            n4 = n;
            ++n8;
        }
        lf2.ae = lrArray;
        return lf2;
    }

    private void b(ks ks2) {
        int n;
        int n2;
        int n3;
        int n4;
        int n5;
        int n6;
        int n7;
        int n8 = ks2.c((short)23, 0);
        String string = ks2.d((short)9);
        if ((n8 & 1) != 0) {
            n7 = ks2.b((short)90);
            n6 = ks2.b((short)90, 0);
            n5 = ks2.a((short)15, (byte)0);
            n4 = ks2.a((short)16, (byte)0);
            di di2 = null;
            di di3 = null;
            di di4 = null;
            n3 = 0;
            while (n3 < n7) {
                n2 = ks2.a((short)90, n6);
                int n9 = ks2.a(n6, 0);
                n = ks2.a((short)91, n6, n2, (byte)0);
                di di5 = new di(n9);
                int n10 = ks2.a((short)93, n6, n2, 0);
                byte[] byArray = ks2.c((short)95, n6, n2);
                di5.d = new dj(n10, byArray);
                di5.f = new dj[]{di5.d};
                n10 = ks2.a((short)96, n6, n2, 0);
                byte[] byArray2 = ks2.c((short)98, n6, n2);
                di5.e = new dj(n10, byArray2);
                switch (n) {
                    case 0: {
                        di2 = di5;
                        break;
                    }
                    case 1: {
                        di3 = di5;
                        break;
                    }
                    case 2: {
                        di4 = di5;
                    }
                }
                n6 = n2;
                ++n3;
            }
            if (this.c != null) {
                this.c.a(string, (byte)n5, (byte)n4, di2, di3, di4);
            }
        }
        if ((n8 & 2) != 0) {
            n7 = ks2.c((short)27, 0);
            n6 = ks2.c((short)118, 0);
            n5 = ks2.c((short)119, 0);
            n4 = ks2.c((short)120, 0);
            int n11 = ks2.c((short)121, 0);
            int n12 = ks2.c((short)196, 0);
            int n13 = ks2.c((short)197, 0);
            n3 = ks2.c((short)198, 0);
            n2 = ks2.c((short)199, 0);
            int n14 = ks2.c((short)116, 0);
            n = ks2.c((short)115, 0);
            if (this.c != null) {
                this.c.a(string, n7, n6, n5, n4, n11, n12, n13, n3, n2, n14, n);
            }
        }
        if ((n8 & 4) != 0) {
            n7 = ks2.c((short)17, 0);
            n6 = ks2.c((short)47, 0);
            n5 = ks2.c((short)42, 0);
            n4 = ks2.c((short)73, 0);
            int n15 = ks2.c((short)74, 0);
            int n16 = ks2.c((short)43, 0);
            int n17 = ks2.c((short)99, 10000);
            if (this.c != null) {
                this.c.a(string, n7, n6, n5, n4, n15, n16, n17);
            }
        }
        if ((n8 & 8) != 0) {
            n7 = ks2.c((short)53, 0);
            n6 = ks2.c((short)76, 0);
            n5 = ks2.c((short)108, 0);
            n4 = ks2.c((short)109, 0);
            int n18 = ks2.c((short)160, 0);
            String string2 = ks2.d((short)151);
            int n19 = ks2.c((short)27, 0);
            if (string2 == null) {
                string2 = "Ch\u01b0a c\u00f3";
            }
            String string3 = "Ch\u01b0a c\u00f3";
            String string4 = n19 > 100 && n19 <= 200 ? "\u0110\u1ea1i Hi\u1ec7p" : (n19 > 200 ? "Chi\u1ebfn V\u01b0\u01a1ng" : "H\u00e0o Ki\u1ec7t");
            if (this.c != null) {
                this.c.a(string, n7, n6, n5, n4, n18, string2, string3, string4);
            }
        }
        if ((n8 & 0x10) != 0) {
            this.c.c(ks2.a((short)165, (byte)0) == 1);
            this.c.d(ks2.a((short)166, (byte)0) == 1);
        }
        if ((n8 & 0x20) != 0) {
            n7 = ks2.b((short)64);
            lt[] ltArray = new lt[n7];
            if (n7 > 0) {
                n5 = ks2.a((short)64, 0);
                n4 = 0;
                while (n4 < n7) {
                    int n20 = ks2.a((short)64, n5);
                    ltArray[n4] = new lt(ks2.a(n5, -1));
                    ltArray[n4].f = ks2.a((short)67, n5, n20, -1);
                    n5 = n20;
                    ++n4;
                }
            }
            if (this.c != null) {
                this.c.a(ltArray);
            }
        }
        if ((n8 & 0x40) != 0) {
            lj[] ljArray = new lj[ks2.b((short)83)];
            int n21 = ks2.b((short)83, 0);
            n5 = 0;
            while (n5 < ljArray.length) {
                n4 = ks2.a((short)83, n21);
                ljArray[n5] = kw.a(ks2, n21, n4, false);
                n21 = n4;
                ++n5;
            }
            if (this.c != null) {
                this.c.a(string, ljArray);
            }
        }
        if ((n8 & 0x100) != 0) {
            int n22 = ks2.b((short)158);
            lr[] lrArray = new lr[n22];
            n5 = ks2.b((short)158, 0);
            n4 = 0;
            while (n4 < n22) {
                int n23 = ks2.a((short)158, n5);
                lrArray[n4] = new lr();
                ks2.d((short)158, n5, n23);
                lrArray[n4].a = ks2.a((short)4, n5, n23, 0);
                lrArray[n4].b = ks2.a((short)157, n5, n23, 0L);
                n5 = n23;
                ++n4;
            }
            if (this.c != null) {
                this.c.a(lrArray);
            }
        }
        if (this.c != null) {
            this.c.S();
        }
    }

    private static lj a(ks ks2, int n, int n2, boolean bl) {
        Object object = ks2.b(n);
        byte by = ks2.a((short)84, n, n2, (byte)0);
        object = new lj((String)object, by);
        v0.m = ks2.a((short)4, n, n2, 0);
        ((lj)object).n = ks2.a((short)139, n, n2, -1);
        ((lj)object).i = ks2.a((short)27, n, n2, 0);
        if (bl) {
            ((lj)object).c = ks2.d((short)26, n, n2);
            ((lj)object).h = ks2.a((short)135, n, n2, -1);
            ((lj)object).e = ks2.a((short)15, n, n2, (byte)7);
            ((lj)object).g = ks2.a((short)16, n, n2, (byte)2);
            ((lj)object).l = ks2.a((short)138, n, n2, (byte)0);
            ((lj)object).o = ks2.a((short)144, n, n2, 0);
            ((lj)object).f = ks2.d((short)117, n, n2);
            ((lj)object).q = ks2.a((short)156, n, n2, (byte)-1);
            ((lj)object).r = ks2.a((short)85, n, n2, (byte)1);
            ((lj)object).j = ks2.a((short)190, n, n2, (byte)-1);
            kz kz2 = new kz();
            new kz().a = ks2.a((short)118, n, n2, 0);
            kz2.b = ks2.a((short)119, n, n2, 0);
            kz2.c = ks2.a((short)120, n, n2, 0);
            kz2.d = ks2.a((short)121, n, n2, 0);
            kz2.e = ks2.a((short)72, n, n2, 0);
            kz2.f = ks2.a((short)71, n, n2, 0);
            kz2.g = ks2.a((short)126, n, n2, 0);
            kz2.h = ks2.a((short)124, n, n2, 0);
            kz2.i = ks2.a((short)47, n, n2, 0);
            kz2.j = ks2.a((short)200, n, n2, 0);
            kz2.k = ks2.a((short)201, n, n2, 0);
            kz2.l = ks2.a((short)202, n, n2, 0);
            kz2.m = ks2.a((short)203, n, n2, 0);
            kz2.n = ks2.a((short)204, n, n2, 0);
            kz2.o = ks2.a((short)221, n, n2, 0);
            ((lj)object).p = kz2;
        }
        return object;
    }

    private void c(ks ks2) {
        byte by = ks2.a((short)12, (byte)0);
        String string = ks2.d((short)131);
        int n = ks2.c((short)41, -1);
        int n2 = ks2.c((short)13, 0);
        int[] nArray = new int[ks2.b((short)4)];
        int n3 = ks2.b((short)4, 0);
        int n4 = 0;
        while (n4 < nArray.length) {
            nArray[n4] = ks2.a(n3, 0);
            ++n3;
            ++n4;
        }
        this.c.a(by == 2, string, n, nArray, n2);
        n4 = ks2.c((short)170, -1);
        String[] stringArray = new String[ks2.b((short)1)];
        if (stringArray.length > 0) {
            int n5 = ks2.b((short)1, 0);
            n = 0;
            while (n < stringArray.length) {
                n2 = ks2.a((short)1, n5);
                stringArray[n] = ks2.b(n5);
                n5 = n2;
                ++n;
            }
        }
        this.c.a(n4, stringArray);
    }

    private void d(ks ks2) {
        try {
            jl jl2;
            byte by = ks2.a((short)12, (byte)0);
            Object object = ks2.d((short)20);
            if (by == 0) {
                jl jl3;
                int n;
                jl[] jlArray = new jl[ks2.b((short)21)];
                int n2 = ks2.b((short)21, 0);
                int n3 = 0;
                while (n3 < jlArray.length) {
                    n = ks2.a((short)21, n2);
                    jl3 = new jl();
                    new jl().c = ks2.a(n2, -1);
                    jl3.b = ks2.d((short)26, n2, n);
                    jl3.a = ks2.a((short)22, n2, n, (byte)0);
                    jl3.d = ks2.a((short)102, n2, n, 0);
                    jl3.e = ks2.a((short)103, n2, n, 0);
                    jl3.f = ks2.a((short)104, n2, n, 0);
                    jl3.g = ks2.a((short)105, n2, n, 0);
                    jl3.h = ks2.a((short)101, n2, n, (byte)0) == 1;
                    jl3.i = ks2.a((short)4, n2, n, 0);
                    jlArray[n3] = jl3;
                    n2 = n;
                    ++n3;
                }
                n3 = 0;
                while (n3 < jlArray.length - 1) {
                    n = n3 + 1;
                    while (n < jlArray.length) {
                        if (jlArray[n3].c > jlArray[n].c) {
                            jl3 = jlArray[n3];
                            jlArray[n3] = jlArray[n];
                            jlArray[n] = jl3;
                        }
                        ++n;
                    }
                    ++n3;
                }
                this.c.a((String)object, jlArray);
                return;
            }
            a a2 = new a();
            jm jm2 = new jm();
            new jm().a = object;
            jm2.b = ks2.d((short)26);
            jm2.c = ks2.c((short)41, 0);
            jm2.d = ks2.c((short)56, 0);
            jm2.e = ks2.c((short)57, 0);
            jm2.f = ks2.c((short)58, 0);
            jm2.g = ks2.c((short)59, 0);
            jm2.j = ks2.c((short)55);
            jm2.k = ks2.c((short)54);
            jm2.l = ks2.c((short)61);
            jm2.m = ks2.c((short)60, 0);
            jm2.h = ks2.c((short)63, 0);
            jm2.i = ks2.c((short)29, 0);
            a2.a(new Integer(jm2.m));
            a2.a(new Integer(jm2.h));
            a2.a(new Integer(jm2.i));
            jl[] jlArray = new jl[ks2.b((short)21)];
            int n = ks2.b((short)21, 0);
            int n4 = 0;
            while (n4 < jlArray.length) {
                int n5 = ks2.a((short)21, n);
                jl2 = new jl();
                new jl().c = ks2.a(n, -1);
                jl2.b = ks2.d((short)26, n, n5);
                jl2.a = ks2.a((short)22, n, n5, (byte)0);
                jl2.d = ks2.a((short)102, n, n5, 0);
                jl2.e = ks2.a((short)103, n, n5, 0);
                jl2.f = ks2.a((short)104, n, n5, 0);
                jl2.g = ks2.a((short)105, n, n5, 0);
                jl2.h = ks2.a((short)101, n, n5, (byte)0) == 1;
                jl2.i = ks2.a((short)4, n, n5, 0);
                if (jl2.i != 0) {
                    a2.a(new Integer(jl2.i));
                }
                jlArray[n4] = jl2;
                n = n5;
                ++n4;
            }
            n4 = 0;
            while (n4 < jlArray.length - 1) {
                int n6 = n4 + 1;
                while (n6 < jlArray.length) {
                    if (jlArray[n4].c > jlArray[n6].c) {
                        jl2 = jlArray[n4];
                        jlArray[n4] = jlArray[n6];
                        jlArray[n6] = jl2;
                    }
                    ++n6;
                }
                ++n4;
            }
            jm2.n = jlArray;
            n4 = ks2.a(ks2.b((short)6, 0), 0);
            object = new int[a2.d()];
            int n7 = 0;
            while (n7 < ((Object)object).length) {
                object[n7] = (Integer)a2.b(n7);
                ++n7;
            }
            int[] nArray = new int[ks2.b((short)154)];
            if (nArray.length > 0) {
                n = ks2.b((short)154, 0);
                int n8 = 0;
                while (n8 < nArray.length) {
                    int n9 = ks2.a((short)154, n);
                    nArray[n8] = ks2.a(n, 0);
                    n = n9;
                    ++n8;
                }
            }
            int n10 = ks2.a(ks2.b((short)6, 1), 0);
            this.c.a(jm2, (int[])object, n4, nArray, n10);
            return;
        }
        catch (OutOfMemoryError outOfMemoryError) {
            return;
        }
    }

    private final void e(ks ks2) {
        try {
            jl jl2;
            int n;
            Object object = ks2.d((short)20);
            int n2 = ks2.a((short)12, (byte)0);
            jl[] jlArray = new jl[ks2.b((short)21)];
            int n3 = ks2.b((short)21, 0);
            int n4 = 0;
            while (n4 < jlArray.length) {
                n = ks2.a((short)21, n3);
                jl2 = new jl();
                new jl().c = ks2.a(n3, -1);
                jl2.b = ks2.d((short)26, n3, n);
                jl2.a = ks2.a((short)22, n3, n, (byte)0);
                jl2.d = ks2.a((short)102, n3, n, 0);
                jl2.e = ks2.a((short)103, n3, n, 0);
                jl2.f = ks2.a((short)104, n3, n, 0);
                jl2.g = ks2.a((short)105, n3, n, 0);
                jl2.h = ks2.a((short)101, n3, n, (byte)0) == 1;
                jl2.i = ks2.a((short)4, n3, n, 0);
                jlArray[n4] = jl2;
                n3 = n;
                ++n4;
            }
            n4 = 0;
            while (n4 < jlArray.length - 1) {
                n = n4 + 1;
                while (n < jlArray.length) {
                    if (jlArray[n4].c > jlArray[n].c) {
                        jl2 = jlArray[n4];
                        jlArray[n4] = jlArray[n];
                        jlArray[n] = jl2;
                    }
                    ++n;
                }
                ++n4;
            }
            if (n2 == 0) {
                this.c.a((String)object, jlArray);
                return;
            }
            a a2 = new a();
            jm jm2 = new jm();
            new jm().a = object;
            jm2.b = ks2.d((short)26);
            jm2.c = ks2.c((short)41, 0);
            jm2.d = ks2.c((short)56, 0);
            jm2.e = ks2.c((short)57, 0);
            jm2.f = ks2.c((short)58, 0);
            jm2.g = ks2.c((short)59, 0);
            jm2.j = ks2.c((short)55);
            jm2.k = ks2.c((short)54);
            jm2.l = ks2.c((short)61);
            jm2.m = ks2.c((short)60, 0);
            jm2.h = ks2.c((short)63, 0);
            jm2.i = ks2.c((short)29, 0);
            a2.a(new Integer(jm2.m));
            a2.a(new Integer(jm2.h));
            a2.a(new Integer(jm2.i));
            jm2.n = jlArray;
            int n5 = ks2.a(ks2.b((short)6, 0), 0);
            object = new int[a2.d()];
            n2 = 0;
            while (n2 < ((Object)object).length) {
                object[n2] = (Integer)a2.b(n2);
                ++n2;
            }
            int[] nArray = new int[ks2.b((short)154)];
            if (nArray.length > 0) {
                n3 = ks2.b((short)154, 0);
                int n6 = 0;
                while (n6 < nArray.length) {
                    int n7 = ks2.a((short)154, n3);
                    nArray[n6] = ks2.a(n3, 0);
                    n3 = n7;
                    ++n6;
                }
            }
            int n8 = ks2.a(ks2.b((short)6, 1), 0);
            this.c.a(jm2, (int[])object, n5, nArray, n8);
            return;
        }
        catch (OutOfMemoryError outOfMemoryError) {
            cw.b("processMapInfo()");
            return;
        }
    }

    private void f(ks ks2) {
        Object[] objectArray;
        int n;
        int n2;
        int n3;
        int n4 = 0;
        int n5 = 0;
        int n6 = 0;
        int n7 = 0;
        int n8 = 0;
        int n9 = 0;
        int n10 = ks2.b((short)90);
        di[] diArray = new di[n10];
        byte[] byArray = new byte[n10];
        int n11 = ks2.b((short)90, 0);
        int n12 = 0;
        while (n12 < n10) {
            n3 = ks2.a((short)90, n11);
            n2 = ks2.a(n11, 0);
            byArray[n12] = ks2.a((short)91, n11, n3, (byte)0);
            di di2 = new di(n2);
            new di(n2).b = ks2.d((short)92, n11, n3);
            di2.c = ks2.a((short)16, n11, n3, (byte)0);
            n = ks2.a((short)93, n11, n3, 0);
            String string = ks2.d((short)94, n11, n3);
            objectArray = ks2.c((short)95, n11, n3);
            di2.d = new dj(n, (byte[])objectArray);
            di2.d.b = string;
            di2.e = di2.d;
            di2.f = new dj[ks2.a((short)96, n11, n3)];
            di2.f[0] = di2.d;
            n11 = ks2.a((short)96, n11);
            int n13 = 1;
            int n14 = 0;
            while (n14 < di2.f.length) {
                int n15 = ks2.a((short)96, n11);
                int n16 = ks2.a(n11, 0);
                String string2 = ks2.d((short)97, n11, n15);
                byte[] byArray2 = ks2.c((short)98, n11, n15);
                if (n16 != n) {
                    di2.f[n13] = new dj(n16, byArray2);
                    di2.f[n13].b = string2;
                    ++n13;
                }
                n11 = n15;
                ++n14;
            }
            if (di2.c == 0) {
                switch (byArray[n12]) {
                    case 0: {
                        ++n5;
                        break;
                    }
                    case 1: {
                        ++n4;
                        break;
                    }
                    case 2: {
                        ++n6;
                    }
                }
            } else {
                switch (byArray[n12]) {
                    case 0: {
                        ++n8;
                        break;
                    }
                    case 1: {
                        ++n7;
                        break;
                    }
                    case 2: {
                        ++n9;
                    }
                }
            }
            diArray[n12] = di2;
            n11 = n3;
            ++n12;
        }
        n12 = 0;
        n3 = 0;
        n2 = 0;
        int n17 = 0;
        n = 0;
        int n18 = 0;
        objectArray = new di[n4];
        di[] diArray2 = new di[n5];
        di[] diArray3 = new di[n6];
        di[] diArray4 = new di[n7];
        di[] diArray5 = new di[n8];
        di[] diArray6 = new di[n9];
        int n19 = 0;
        while (n19 < diArray.length) {
            if (diArray[n19].c == 0) {
                switch (byArray[n19]) {
                    case 0: {
                        diArray2[n3++] = diArray[n19];
                        break;
                    }
                    case 1: {
                        objectArray[n12++] = diArray[n19];
                        break;
                    }
                    case 2: {
                        diArray3[n2++] = diArray[n19];
                    }
                }
            } else {
                switch (byArray[n19]) {
                    case 0: {
                        diArray5[n++] = diArray[n19];
                        break;
                    }
                    case 1: {
                        diArray4[n17++] = diArray[n19];
                        break;
                    }
                    case 2: {
                        diArray6[n18++] = diArray[n19];
                    }
                }
            }
            ++n19;
        }
        this.c.a(diArray2, (di[])objectArray, diArray3, diArray5, diArray4, diArray6);
    }

    private void g(ks ks2) {
        String string = ks2.d((short)20);
        byte by = ks2.a((short)40, (byte)3);
        jn[] jnArray = new jn[ks2.b((short)9)];
        int n = ks2.b((short)9, 0);
        int n2 = 0;
        while (n2 < jnArray.length) {
            int n3 = ks2.a((short)9, n);
            jn jn2 = new jn();
            new jn().a = ks2.b(n);
            jn2.b = ks2.d((short)26, n, n3);
            jn2.d = ks2.a((short)27, n, n3, 0);
            jn2.c = ks2.a((short)15, n, n3, (byte)0);
            jn2.e = ks2.a((short)129, n, n3, 0);
            jn2.f = ks2.a((short)106, n, n3, 0);
            jn2.g = ks2.a((short)107, n, n3, (byte)0);
            jnArray[n2] = jn2;
            n = n3;
            ++n2;
        }
        switch (by) {
            case 0: {
                this.c.b(jnArray, string);
                return;
            }
            case 1: {
                this.c.c(jnArray, string);
                return;
            }
            case 3: {
                this.c.a(jnArray, string);
            }
        }
    }

    private void h(ks ks2) {
        nq[] nqArray = new nq[ks2.b((short)77)];
        int n = ks2.b((short)77, 0);
        int n2 = 0;
        while (n2 < nqArray.length) {
            int n3 = ks2.a((short)77, n);
            String string = ks2.b(n);
            String string2 = ks2.d((short)26, n, n3);
            nqArray[n2] = new nq(string, string2, "");
            n = n3;
            ++n2;
        }
        this.c.a(nqArray);
    }

    private void i(ks ks2) {
        String string = ks2.d((short)192);
        int n = ks2.b((short)9);
        if (n > 0) {
            dq[] dqArray = new dq[n];
            int n2 = ks2.b((short)9, 0);
            int n3 = 0;
            try {
                n3 = 0;
                while (n3 < dqArray.length) {
                    int n4 = ks2.a((short)9, n2);
                    dqArray[n3] = kw.d(ks2, n2, n4);
                    n2 = n4;
                    ++n3;
                }
                this.c.a(string, dqArray);
                return;
            }
            catch (OutOfMemoryError outOfMemoryError) {
                ks2.c = null;
                System.gc();
            }
        }
    }

    private void j(ks ks2) {
        byte by = ks2.a((short)193, (byte)0);
        int n = ks2.b((short)192, 0);
        int n2 = ks2.b((short)192);
        lp[] lpArray = new lp[n2];
        int n3 = 0;
        while (n3 < n2) {
            int n4;
            int n5 = n4 = ks2.a((short)192, n);
            int n6 = n;
            ks ks3 = ks2;
            String string = ks3.b(n6);
            String string2 = ks3.d((short)26, n6, n5);
            int n7 = ks3.a((short)106, n6, n5, 0);
            String string3 = ks3.d((short)1, n6, n5);
            byte by2 = ks3.a((short)101, n6, n5, (byte)0);
            n = ks3.a((short)143, n6, n5, (byte)0);
            lpArray[n3] = new lp(string, string2, string3, n7, by2, (byte)n);
            n = n4;
            ++n3;
        }
        if (by != 0) {
            this.c.b(lpArray);
            return;
        }
        this.c.a(lpArray);
    }

    private static dq d(ks ks2, int n, int n2) {
        dq dq2 = new dq();
        new dq().a = ks2.b(n);
        dq2.b = ks2.a((short)27, n, n2, 0);
        dq2.c = ks2.a((short)24, n, n2, (byte)0);
        dq2.d = "Ch\u01b0a c\u00f3";
        dq2.f = ks2.a((short)132, n, n2, 0);
        return dq2;
    }

    private void k(ks ks2) {
        cw.a("[processPrepareData]======================================");
        lf lf2 = null;
        lf lf3 = null;
        byte by = ks2.a((short)111, (byte)0);
        byte by2 = ks2.a((short)140, (byte)0);
        boolean bl = false;
        int n = ks2.b((short)9, 0);
        int n2 = 0;
        while (n2 < 2) {
            int n3 = ks2.a((short)9, n);
            lf lf4 = this.c(ks2, n, n3);
            if (by2 != 9) {
                if (lf4.b.equals(gr.e)) {
                    lf2 = lf4;
                    if (lf3 == null) {
                        bl = true;
                    }
                } else {
                    lf3 = lf4;
                }
            } else if (lf2 == null) {
                lf2 = lf4;
            } else {
                lf3 = lf4;
            }
            n = n3;
            ++n2;
        }
        kq.a().f = ks2.d((short)28);
        byte[] byArray = ks2.c((short)30);
        byte[] byArray2 = null;
        byte[] byArray3 = null;
        int n4 = ks2.b((short)35);
        int n5 = 0;
        while (n5 < n4) {
            byte[] byArray4 = ks2.a(ks2.b((short)35, n5));
            if (byArray2 == null) {
                byArray2 = byArray4;
            } else {
                byArray3 = byArray4;
            }
            ++n5;
        }
        n5 = ks2.a((short)70, (byte)0);
        this.c.a(lf2, lf3, bl, byArray, byArray2, byArray3, n5, by, by2);
    }

    private lk[] l(ks ks2) {
        return kw.a(ks2, 0);
    }

    private static lk[] a(ks ks2, int n) {
        lk[] lkArray = new lk[ks2.b((short)114) - n];
        n = ks2.b((short)114, n);
        int n2 = 0;
        while (n2 < lkArray.length) {
            int n3 = ks2.a((short)114, n);
            int n4 = ks2.a(n, 0);
            lkArray[n2] = new lk(n4);
            lkArray[n2].b = ks2.d((short)26, n, n3);
            lkArray[n2].d = ks2.d((short)117, n, n3);
            lkArray[n2].g = ks2.a((short)106, n, n3, 0);
            lkArray[n2].e = ks2.a((short)122, n, n3, (byte)-1);
            lkArray[n2].f = ks2.a((short)123, n, n3, (byte)-1);
            lkArray[n2].j = ks2.a((short)4, n, n3, 0);
            lkArray[n2].h = ks2.a((short)145, n, n3, 0);
            lkArray[n2].i = ks2.a((short)106, n, n3, 0);
            lkArray[n2].l = ks2.a((short)82, n, n3, -1);
            lkArray[n2].k = ks2.a((short)132, n, n3, 0L);
            lkArray[n2].m = ks2.a((short)85, n, n3, (byte)1);
            n = n3;
            ++n2;
        }
        return lkArray;
    }

    private void m(ks ks2) {
        int n;
        int n2 = ks2.c((short)41, 0);
        byte[] byArray = ks2.c((short)30);
        byte[] byArray2 = ks2.c((short)35);
        byte[] byArray3 = ks2.a(ks2.b((short)35, 1));
        lf[] lfArray = new lf[1];
        lf[] lfArray2 = new lf[1];
        int n3 = ks2.b((short)9, 0);
        int n4 = 0;
        while (n4 < 2) {
            n = ks2.a((short)9, n3);
            lf lf2 = this.b(ks2, n3, n);
            if (op.o != 9) {
                if (lf2.b.equals(gr.e)) {
                    lfArray[0] = lf2;
                } else {
                    lfArray2[0] = lf2;
                }
            } else if (lfArray[0] == null) {
                lfArray[0] = lf2;
            } else {
                lfArray2[0] = lf2;
            }
            n3 = n;
            ++n4;
        }
        int n5 = n = ks2.c((short)32) != null ? 1 : 0;
        if (this.d != null) {
            this.d.a(byArray, byArray2, byArray3, lfArray, lfArray2, n != 0, n2);
        }
    }

    private void a(ks ks2, no no2) {
        if (this.d == null) {
            return;
        }
        int n = ks2.b((short)35);
        no2.g = new byte[n][];
        int n2 = 0;
        while (n2 < n) {
            byte[] byArray = ks2.a(ks2.b((short)35, n2));
            if (byArray != null) {
                no2.g[n2] = byArray;
            }
            ++n2;
        }
        Object[] objectArray = ks2.c((short)30);
        if (objectArray != null) {
            no2.h = objectArray;
        }
        kw.b(ks2, no2);
        int n3 = ks2.b((short)37);
        if (n3 > 0) {
            Object object = new int[n3];
            objectArray = new int[n3];
            int n4 = ks2.b((short)37, 0);
            int n5 = 0;
            while (n5 < n3) {
                int n6 = ks2.a((short)37, n4);
                int n7 = ks2.a(n4, -1);
                n4 = ks2.a((short)38, n4, n6, -1);
                object[n5] = n7;
                objectArray[n5] = n4;
                n4 = n6;
                ++n5;
            }
            Object[] objectArray2 = objectArray;
            objectArray = (Object[])object;
            object = no2;
            no2.i = objectArray;
            object.j = objectArray2;
        }
        no2.F = ks2.a((short)52, (byte)0);
        no2.H = ks2.a((short)172, (byte)0);
        no2.E = ks2.a((short)133, (byte)0);
        n = ks2.c((short)32) != null ? 1 : 0;
        no2.a = ks2.d((short)62);
        objectArray = ks2.c((short)39);
        if (objectArray != null) {
            this.d.a(no2);
            no2.G = true;
            this.a(ks2, objectArray[0], no2.b);
            return;
        }
        no2.d = n;
        this.d.a(no2);
    }

    private static void b(ks ks2, no no2) {
        int n = ks2.b((short)9);
        int n2 = ks2.b((short)9, 0);
        no2.f = new nj[n];
        int n3 = 0;
        while (n3 < n) {
            int n4 = ks2.a((short)9, n2);
            String string = ks2.b(n2);
            int n5 = ks2.a((short)46, n2, n4, -1);
            byte by = ks2.a((short)19, n2, n4, (byte)0);
            int n6 = ks2.a((short)17, n2, n4, -1);
            int n7 = ks2.a((short)18, n2, n4, -1);
            n2 = ks2.a((short)45, n2, n4, -1);
            no2.f[n3] = new nj(string, n5, n6, n7, n2, by);
            n2 = n4;
            cw.a("" + no2.f[n3]);
            ++n3;
        }
    }

    private void n(ks ks2) {
        int n;
        int n2 = ks2.c((short)41, 0);
        no no2 = new no(n2, 2);
        new no(n2, 2).k = ks2.a((short)44, 0L);
        int n3 = ks2.a(ks2.b((short)64, 0), -1);
        int n4 = ks2.a(ks2.b((short)75, 0), 0);
        byte[] byArray = new byte[]{};
        byte[] byArray2 = new byte[]{};
        int n5 = ks2.b((short)50);
        if (n5 > 0) {
            byArray = new byte[n5];
            byArray2 = new byte[n5];
            int n6 = ks2.b((short)50, 0);
            int n7 = 0;
            while (n7 < n5) {
                n = ks2.a((short)50, n6);
                byArray[n7] = (byte)ks2.a(n6, -1);
                byArray2[n7] = (byte)ks2.a((short)51, n6, n, -1);
                n6 = n;
                ++n7;
            }
        }
        byte[] byArray3 = new byte[]{};
        byte[] byArray4 = new byte[]{};
        n = ks2.b((short)33);
        if (n > 0) {
            byArray3 = new byte[n];
            byArray4 = new byte[n];
            n5 = ks2.b((short)33, 0);
            int n8 = 0;
            while (n8 < n) {
                int n9 = ks2.a((short)33, n5);
                byArray3[n8] = (byte)ks2.a(n5, -1);
                byArray4[n8] = (byte)ks2.a((short)34, n5, n9, -1);
                n5 = n9;
                ++n8;
            }
        }
        byte[] byArray5 = byArray3;
        byte[] byArray6 = byArray4;
        byArray4 = byArray2;
        byArray3 = byArray;
        byte[] byArray7 = byArray6;
        byArray2 = byArray5;
        int n10 = n4;
        n4 = n3;
        no no3 = no2;
        no2.p = n4;
        no3.t = n10;
        no3.u = byArray2;
        no3.q = byArray7;
        no3.r = byArray4;
        no3.s = byArray3;
        this.a(ks2, no2);
    }

    private void a(ks object, byte by, int n) {
        no no2 = new no(n, 5);
        int n2 = ((ks)object).c((short)42, 0);
        int n3 = ((ks)object).c((short)43, 0);
        int n4 = ((ks)object).c((short)110, 0);
        int[] nArray = new int[((ks)object).b((short)73)];
        int[] nArray2 = new int[nArray.length];
        int n5 = 0;
        while (n5 < nArray2.length) {
            nArray[n5] = ((ks)object).a(((ks)object).b((short)73, n5), -1);
            nArray2[n5] = ((ks)object).a(((ks)object).b((short)74, n5), -1);
            ++n5;
        }
        lj[] ljArray = new lj[((ks)object).b((short)83)];
        int n6 = ((ks)object).b((short)83, 0);
        int n7 = 0;
        while (n7 < ljArray.length) {
            int n8 = ((ks)object).a((short)83, n6);
            ljArray[n7] = kw.a((ks)object, n6, n8, true);
            n6 = n8;
            ++n7;
        }
        lk[] lkArray = object;
        object = this;
        lk[] lkArray2 = kw.a((ks)lkArray, 0);
        if (this.d != null) {
            int n9 = n4;
            lkArray = lkArray2;
            boolean bl = false;
            n4 = n3;
            n3 = n9;
            no no3 = no2;
            no2.v = by;
            no3.C = ljArray;
            no3.x = n3;
            no3.w = n2;
            no3.y = n4;
            no3.z = 0;
            no3.A = nArray;
            no3.B = nArray2;
            no3.D = lkArray;
            this.d.a(no2);
        }
    }

    private void o(ks object) {
        Object object2 = ((ks)object).d((short)77);
        String string = ((ks)object).d((short)26);
        String string2 = ((ks)object).d((short)79);
        boolean bl = ((ks)object).a((short)100, (byte)0) == 0;
        object2 = new nq((String)object2, string, string2);
        int n = ((ks)object).b((short)80);
        if (n > 0) {
            nr[] nrArray = new nr[n];
            int n2 = ((ks)object).b((short)80, 0);
            int n3 = 0;
            while (n3 < nrArray.length) {
                int n4 = ((ks)object).a((short)80, n2);
                int n5 = ((ks)object).a(n2, -1);
                String string3 = ((ks)object).d((short)81, n2, n4);
                nrArray[n3] = new nr(n5, string3, null);
                n2 = n4;
                ++n3;
            }
            object = object2;
            ((nq)object2).d = nrArray;
        }
        this.c.a((nq)object2, bl);
    }

    private static void p(ks object) {
        Object object2 = ((ks)object).d((short)77);
        object2 = new nq((String)object2, "", "");
        int n = ((ks)object).b((short)80);
        if (n > 0) {
            nr[] nrArray = new nr[n];
            int n2 = ((ks)object).b((short)80, 0);
            int n3 = 0;
            while (n3 < nrArray.length) {
                int n4 = ((ks)object).a((short)80, n2);
                int n5 = ((ks)object).a(n2, -1);
                String string = ((ks)object).d((short)81, n2, n4);
                nrArray[n3] = new nr(n5, string, null);
                n2 = n4;
                ++n3;
            }
            object = object2;
            ((nq)object2).d = nrArray;
        }
        ns.b((nq)object2);
    }

    private void q(ks ks2) {
        Object object = ks2.d((short)77);
        String[] stringArray = ks2.d((short)26);
        String[] stringArray2 = new String[ks2.b((short)1)];
        int n = ks2.b((short)1, 0);
        int n2 = 0;
        while (n2 < stringArray2.length) {
            int n3 = ks2.a((short)1, n);
            stringArray2[n2] = ks2.b(n);
            n = n3;
            ++n2;
        }
        nq nq2 = new nq((String)object, (String)stringArray, "");
        stringArray = stringArray2;
        object = nq2;
        nq2.e = stringArray;
        ns.a(nq2);
        String string = ks2.d((short)149);
        this.c.a(string, (byte)0);
    }

    private void r(ks ks2) {
        int n = ks2.b((short)64);
        lu[] luArray = new lu[n];
        int n2 = ks2.b((short)64, 0);
        int n3 = 0;
        while (n3 < n) {
            int n4;
            int n5 = ks2.a((short)64, n2);
            luArray[n3] = new lu(ks2.a(n2, 0));
            luArray[n3].b = ks2.d((short)26, n2, n5);
            luArray[n3].d = ks2.a((short)136, n2, n5, 0);
            lv[] lvArray = new lv[ks2.a((short)67, n2, n5)];
            n2 = ks2.a((short)67, n2);
            int n6 = 0;
            while (n6 < lvArray.length) {
                n4 = ks2.a((short)67, n2);
                lvArray[n6] = new lv(luArray[n3].a);
                lvArray[n6].a = ks2.a(n2, 0);
                lvArray[n6].e = ks2.d((short)66, n2, n4);
                lvArray[n6].c = ks2.a((short)76, n2, n4, 0);
                lvArray[n6].b = ks2.a((short)135, n2, n4, 0);
                lvArray[n6].d = ks2.a((short)68, n2, n4, 0);
                n2 = n4;
                ++n6;
            }
            n6 = 0;
            while (n6 < lvArray.length) {
                n4 = n6 + 1;
                while (n4 < lvArray.length) {
                    if (lvArray[n6].a > lvArray[n4].a) {
                        lv lv2 = lvArray[n6];
                        lvArray[n6] = lvArray[n4];
                        lvArray[n4] = lv2;
                    }
                    ++n4;
                }
                ++n6;
            }
            luArray[n3].c = lvArray;
            n2 = n5;
            ++n3;
        }
        this.c.a(luArray);
    }

    private void s(ks ks2) {
        int n;
        ks2.d((short)9);
        lj[] ljArray = new lj[ks2.b((short)83)];
        if (ljArray.length > 0) {
            int n2 = ks2.b((short)83, 0);
            int n3 = 0;
            while (n3 < ljArray.length) {
                n = ks2.a((short)83, n2);
                if (n < 0) {
                    n = ks2.a((short)114, n2);
                }
                ljArray[n3] = kw.a(ks2, n2, n, true);
                n2 = n;
                ++n3;
            }
        }
        ks ks3 = ks2;
        lk[] lkArray = this;
        lkArray = kw.a(ks3, 0);
        int n4 = ks2.c((short)86, 0);
        n = ks2.c((short)145, 0);
        this.c.a(ljArray, lkArray, n4, n);
    }

    private void t(ks ks2) {
        int n = ks2.a((short)147, (byte)-1);
        switch (n) {
            case 0: {
                String string = ks2.d((short)9);
                String string2 = ks2.d((short)150);
                kq.a().h.a(string2);
                this.c.k(string);
                return;
            }
            case 1: {
                String string = ks2.d((short)9);
                String string3 = ks2.d((short)150);
                byte by = ks2.a((short)31, (byte)-1);
                if (by == 0) {
                    this.c.m(string);
                    return;
                }
                this.c.n(string);
                kq.a().g = string3;
                kq.j = 0;
                return;
            }
            case 4: {
                Object object = ks2.d((short)9);
                kq.j = ks2.c((short)41, 0);
                if (((String)object).equals(kq.a().d)) {
                    this.c.I();
                    return;
                }
                int n2 = ks2.c((short)106, -1);
                String string = ks2.d((short)83);
                if (n2 > 0) {
                    object = kw.a(ks2, ks2.b((short)83, 0), -1, true);
                    this.c.b((lj)object);
                    return;
                }
                this.c.j(string);
                return;
            }
            case 3: {
                Object object = ks2.d((short)9);
                kq.j = ks2.c((short)41, 0);
                if (((String)object).equals(kq.a().d)) {
                    this.c.I();
                    return;
                }
                ks ks3 = ks2;
                object = this;
                lk[] lkArray = kw.a(ks3, 0);
                int n3 = ks2.c((short)106, -1);
                if (n3 > 0) {
                    this.c.a(lkArray[0], n3);
                    return;
                }
                this.c.a(lkArray[0]);
                return;
            }
            case 2: {
                String string = ks2.d((short)9);
                kq.j = ks2.c((short)41, 0);
                if (string.equals(kq.a().d)) {
                    this.c.I();
                    return;
                }
                long l = ks2.a((short)132, 0L);
                this.c.l((int)l);
                return;
            }
            case 5: {
                String string = ks2.d((short)9);
                this.c.i(string);
                return;
            }
            case 6: {
                int n4 = (int)ks2.a((short)132, -1L);
                lj[] ljArray = new lj[ks2.b((short)83)];
                if (ljArray.length > 0) {
                    n = ks2.b((short)83, 0);
                    int n5 = 0;
                    while (n5 < ljArray.length) {
                        int n6 = ks2.a((short)83, n);
                        ljArray[n5] = kw.a(ks2, n, n6, true);
                        n = n6;
                        ++n5;
                    }
                }
                ks ks4 = ks2;
                lk[] lkArray = this;
                lkArray = kw.a(ks4, 0);
                this.c.a(ljArray, lkArray, n4);
                return;
            }
            case 7: {
                this.c.O();
                return;
            }
            case 8: {
                this.c.N();
                return;
            }
            case 9: {
                String string = ks2.d((short)9);
                this.c.l(string);
            }
        }
    }

    private void u(ks ks2) {
        int n = ks2.b((short)147);
        int[] nArray = new int[n];
        String[] stringArray = new String[n];
        if (nArray.length > 0) {
            int n2 = ks2.b((short)147, 0);
            int n3 = 0;
            while (n3 < nArray.length) {
                int n4 = ks2.a((short)147, n2);
                byte by = ks2.a(n2, (byte)0);
                String string = ks2.d((short)168, n2, n4);
                nArray[n3] = by;
                stringArray[n3] = string;
                n2 = n4;
                ++n3;
            }
        }
        this.c.a(nArray, stringArray);
    }

    private static kz e(ks ks2, int n, int n2) {
        kz kz2 = new kz();
        new kz().a = ks2.a((short)118, n, n2, 0);
        kz2.b = ks2.a((short)119, n, n2, 0);
        kz2.c = ks2.a((short)120, n, n2, 0);
        kz2.d = ks2.a((short)121, n, n2, 0);
        kz2.e = ks2.a((short)72, n, n2, 0);
        kz2.f = ks2.a((short)71, n, n2, 0);
        kz2.g = ks2.a((short)126, n, n2, 0);
        kz2.h = ks2.a((short)124, n, n2, 0);
        kz2.i = ks2.a((short)47, n, n2, 0);
        kz2.j = ks2.a((short)200, n, n2, 0);
        kz2.k = ks2.a((short)201, n, n2, 0);
        kz2.l = ks2.a((short)202, n, n2, 0);
        kz2.m = ks2.a((short)203, n, n2, 0);
        kz2.n = ks2.a((short)204, n, n2, 0);
        kz2.o = ks2.a((short)221, n, n2, 0);
        return kz2;
    }

    private lo[] v(ks ks2) {
        lo[] loArray = new lo[ks2.b((short)175)];
        int n = ks2.b((short)175, 0);
        int n2 = 0;
        while (n2 < loArray.length) {
            int n3 = ks2.a((short)175, n);
            lo lo2 = new lo();
            new lo().b = ks2.b(n);
            lo2.c = ks2.a((short)159, n, n3, (byte)-1);
            lo2.f = ks2.d((short)62, n, n3);
            lo2.d = ks2.a((short)145, n, n3, -1);
            lo2.g = ks2.a((short)157, n, n3, 0L);
            cw.a("[readAllProductsInMarket]=== availableTime===" + lo2.g);
            switch (lo2.c) {
                case 0: {
                    byte by = ks2.a((short)84, n, n3, (byte)0);
                    Object object = new lj("", by);
                    new lj("", by).a = ks2.a(n, 0);
                    ((lj)object).m = ks2.a((short)4, n, n3, 0);
                    ((lj)object).i = ks2.a((short)27, n, n3, 0);
                    ((lj)object).k = ks2.a((short)145, n, n3, 0);
                    ((lj)object).c = ks2.d((short)26, n, n3);
                    ((lj)object).h = ks2.a((short)135, n, n3, -1);
                    ((lj)object).e = ks2.a((short)15, n, n3, (byte)7);
                    ((lj)object).g = ks2.a((short)16, n, n3, (byte)0);
                    ((lj)object).l = ks2.a((short)138, n, n3, (byte)0);
                    ((lj)object).n = ks2.a((short)139, n, n3, 0);
                    ((lj)object).o = ks2.a((short)144, n, n3, 0);
                    ((lj)object).f = ks2.d((short)117, n, n3);
                    ((lj)object).j = ks2.a((short)190, n, n3, (byte)-1);
                    ((lj)object).r = ks2.a((short)85, n, n3, (byte)1);
                    ((lj)object).p = kw.e(ks2, n, n3);
                    lo2.e = object;
                    break;
                }
                case 1: {
                    int n4 = ks2.a((short)114, n, n3, -1);
                    Object object = new lk(n4);
                    new lk(n4).b = ks2.d((short)26, n, n3);
                    ((lb)object).d = ks2.d((short)117, n, n3);
                    ((lk)object).g = ks2.a((short)106, n, n3, 0);
                    ((lk)object).e = ks2.a((short)122, n, n3, (byte)-1);
                    ((lk)object).f = ks2.a((short)123, n, n3, (byte)-1);
                    ((lk)object).j = ks2.a((short)4, n, n3, 0);
                    ((lk)object).h = ks2.a((short)145, n, n3, 0);
                    ((lk)object).i = ks2.a((short)106, n, n3, 0);
                    ((lk)object).l = ks2.a((short)82, n, n3, -1);
                    ((lk)object).k = ks2.a((short)132, n, n3, 0L);
                    ((lk)object).m = ks2.a((short)85, n, n3, (byte)1);
                    lo2.e = object;
                    break;
                }
                case 99: {
                    int n5 = ks2.a((short)155, n, n3, -1);
                    Object object = new ls(n5);
                    new ls(n5).a = ks2.d((short)26, n, n3);
                    ((ls)object).b = ks2.d((short)1, n, n3);
                    ((ls)object).c = ks2.a((short)145, n, n3, 0);
                    lo2.e = object;
                }
            }
            loArray[n2] = lo2;
            if (cw.a()) {
                cw.a("[MARKET] Parsing " + lo2);
            }
            n = n3;
            ++n2;
        }
        return loArray;
    }

    private void w(ks loArray) {
        int n = loArray.a((short)147, (byte)-1);
        switch (n) {
            case 0: {
                ld[] ldArray = new ld[loArray.b((short)152)];
                if (ldArray.length > 0) {
                    int n2 = loArray.b((short)152, 0);
                    int n3 = 0;
                    while (n3 < ldArray.length) {
                        int n4 = loArray.a((short)152, n2);
                        byte by = loArray.a(n2, (byte)-1);
                        String string = loArray.d((short)26, n2, n4);
                        n2 = loArray.a((short)106, n2, n4, 0);
                        ldArray[n3] = new ld(by, string, n2);
                        n2 = n4;
                        ++n3;
                    }
                }
                this.c.a(ldArray);
                return;
            }
            case 1: {
                byte by = loArray.a((short)152, (byte)-1);
                ks ks2 = loArray;
                loArray = this;
                loArray = new lo[ks2.b((short)153)];
                int n5 = ks2.b((short)153, 0);
                int n6 = 0;
                while (n6 < loArray.length) {
                    int n7 = ks2.a((short)153, n5);
                    lo lo2 = new lo();
                    new lo().a = ks2.a(n5, -1);
                    lo2.c = ks2.a((short)159, n5, n7, (byte)-1);
                    lo2.d = ks2.a((short)145, n5, n7, -1);
                    switch (lo2.c) {
                        case 0: {
                            byte by2 = ks2.a((short)84, n5, n7, (byte)0);
                            Object object = new lj("", by2);
                            new lj("", by2).a = ks2.a(n5, 0);
                            ((lj)object).m = ks2.a((short)4, n5, n7, 0);
                            ((lj)object).k = ks2.a((short)145, n5, n7, 0);
                            ((lj)object).i = ks2.a((short)27, n5, n7, 0);
                            ((lj)object).c = ks2.d((short)26, n5, n7);
                            ((lj)object).h = ks2.a((short)135, n5, n7, -1);
                            ((lj)object).e = ks2.a((short)15, n5, n7, (byte)7);
                            ((lj)object).g = ks2.a((short)16, n5, n7, (byte)0);
                            ((lj)object).l = ks2.a((short)138, n5, n7, (byte)0);
                            ((lj)object).o = ks2.a((short)144, n5, n7, 0);
                            ((lj)object).f = ks2.d((short)117, n5, n7);
                            ((lj)object).j = ks2.a((short)190, n5, n7, (byte)-1);
                            ((lj)object).r = ks2.a((short)85, n5, n7, (byte)1);
                            ((lj)object).p = kw.e(ks2, n5, n7);
                            lo2.e = object;
                            break;
                        }
                        case 1: {
                            int n8 = ks2.a((short)114, n5, n7, -1);
                            Object object = new lk(n8);
                            new lk(n8).b = ks2.d((short)26, n5, n7);
                            ((lb)object).d = ks2.d((short)117, n5, n7);
                            ((lk)object).g = ks2.a((short)106, n5, n7, 0);
                            ((lk)object).e = ks2.a((short)122, n5, n7, (byte)-1);
                            ((lk)object).f = ks2.a((short)123, n5, n7, (byte)-1);
                            ((lk)object).j = ks2.a((short)4, n5, n7, 0);
                            ((lk)object).h = ks2.a((short)145, n5, n7, 0);
                            ((lk)object).i = ks2.a((short)106, n5, n7, 0);
                            ((lk)object).l = ks2.a((short)82, n5, n7, -1);
                            ((lk)object).k = ks2.a((short)132, n5, n7, 0L);
                            ((lk)object).m = ks2.a((short)85, n5, n7, (byte)1);
                            lo2.e = object;
                            break;
                        }
                        case 99: {
                            int n9 = ks2.a((short)155, n5, n7, -1);
                            Object object = new ls(n9);
                            new ls(n9).a = ks2.d((short)26, n5, n7);
                            ((ls)object).b = ks2.d((short)1, n5, n7);
                            ((ls)object).c = ks2.a((short)145, n5, n7, 0);
                            lo2.e = object;
                        }
                    }
                    loArray[n6] = lo2;
                    n5 = n7;
                    ++n6;
                }
                this.c.a((int)by, loArray);
                return;
            }
            case 2: {
                int n10;
                int[] nArray;
                Object[] objectArray;
                n = loArray.b((short)114);
                if (n > 0) {
                    objectArray = new int[n];
                    nArray = new int[n];
                    n10 = 0;
                    while (n10 < n) {
                        objectArray[n10] = loArray.a(loArray.b((short)114, n10), -1);
                        nArray[n10] = loArray.a(loArray.b((short)106, n10), -1);
                        ++n10;
                    }
                    this.c.a((int[])objectArray, nArray);
                }
                if ((n = loArray.b((short)83)) <= 0) break;
                objectArray = new String[n];
                nArray = new int[n];
                n10 = 0;
                while (n10 < n) {
                    objectArray[n10] = (int)loArray.b(loArray.b((short)83, n10));
                    nArray[n10] = loArray.a(loArray.b((short)146, n10), -1);
                    ++n10;
                }
                this.c.a((String[])objectArray, nArray);
            }
        }
    }

    private void x(ks ks2) {
        int n;
        int n2;
        int n3 = ks2.b((short)9);
        dk[] dkArray = new dk[n3];
        if (n3 > 0) {
            n2 = ks2.b((short)9, 0);
            n = 0;
            while (n < dkArray.length) {
                n3 = ks2.a((short)9, n2);
                int n4 = ks2.a((short)148, n2, n3, 0);
                String string = ks2.d((short)151, n2, n3);
                String string2 = ks2.b(n2);
                String string3 = ks2.d((short)1, n2, n3);
                dkArray[n] = new dk(n4, string, string2, string3);
                n2 = n3;
                ++n;
            }
        }
        n2 = 0;
        while (n2 < dkArray.length - 1) {
            n = n2 + 1;
            while (n < dkArray.length) {
                if (dkArray[n2].a > dkArray[n].a) {
                    dk dk2 = dkArray[n2];
                    dkArray[n2] = dkArray[n];
                    dkArray[n] = dk2;
                }
                ++n;
            }
            ++n2;
        }
        this.c.a(dkArray);
    }

    private void y(ks object) {
        String string = ((ks)object).d((short)175);
        lj[] ljArray = new lj[((ks)object).b((short)83)];
        if (ljArray.length > 0) {
            int n = ((ks)object).b((short)83, 0);
            int n2 = 0;
            while (n2 < ljArray.length) {
                int n3 = ((ks)object).a((short)83, n);
                if (n3 < 0) {
                    n3 = ((ks)object).a((short)114, n);
                }
                ljArray[n2] = kw.a((ks)object, n, n3, true);
                n = n3;
                ++n2;
            }
        }
        lk[] lkArray = object;
        object = this;
        lkArray = kw.a((ks)lkArray, 0);
        this.c.a(string, ljArray, lkArray);
    }

    private void z(ks ks2) {
        ld[] ldArray = new ld[ks2.b((short)152)];
        if (ldArray.length > 0) {
            int n = ks2.b((short)152, 0);
            int n2 = 0;
            while (n2 < ldArray.length) {
                int n3 = ks2.a((short)152, n);
                byte by = ks2.a(n, (byte)-1);
                String string = ks2.d((short)26, n, n3);
                n = ks2.a((short)106, n, n3, 0);
                ldArray[n2] = new ld(by, string, n);
                n = n3;
                ++n2;
            }
        }
        this.c.b(ldArray);
    }

    private void A(ks object) {
        String string = ((ks)object).d((short)175);
        lj[] ljArray = new lj[((ks)object).b((short)83)];
        if (ljArray.length > 0) {
            int n = ((ks)object).b((short)83, 0);
            int n2 = 0;
            while (n2 < ljArray.length) {
                int n3 = ((ks)object).a((short)83, n);
                if (n3 < 0) {
                    n3 = ((ks)object).a((short)114, n);
                }
                ljArray[n2] = kw.a((ks)object, n, n3, true);
                n = n3;
                ++n2;
            }
        }
        lk[] lkArray = object;
        object = this;
        lkArray = kw.a((ks)lkArray, 0);
        this.c.b(string, ljArray, lkArray);
    }

    private void B(ks object) {
        String string = ((ks)object).d((short)186);
        byte by = ((ks)object).a((short)189, (byte)-1);
        lj[] ljArray = new lj[((ks)object).b((short)83)];
        lk[] lkArray = new lk[114];
        if (ljArray.length > 0) {
            int n = ((ks)object).b((short)83, 0);
            int n2 = 0;
            while (n2 < ljArray.length) {
                int n3 = ((ks)object).a((short)83, n);
                ljArray[n2] = kw.a((ks)object, n, n3, true);
                n = n3;
                ++n2;
            }
        }
        if (lkArray.length > 0) {
            lkArray = object;
            object = this;
            lkArray = kw.a((ks)lkArray, 0);
        }
        this.c.a(string, ljArray, lkArray, by);
    }

    private void C(ks object) {
        String string = ((ks)object).d((short)186);
        byte by = ((ks)object).a((short)189, (byte)-1);
        lj[] ljArray = new lj[((ks)object).b((short)83)];
        lk[] lkArray = new lk[114];
        if (ljArray.length > 0) {
            int n = ((ks)object).b((short)83, 0);
            int n2 = 0;
            while (n2 < ljArray.length) {
                int n3 = ((ks)object).a((short)83, n);
                ljArray[n2] = kw.a((ks)object, n, n3, true);
                n = n3;
                ++n2;
            }
        }
        if (lkArray.length > 0) {
            lkArray = object;
            object = this;
            lkArray = kw.a((ks)lkArray, 0);
        }
        this.c.b(string, ljArray, lkArray, by);
    }

    private void D(ks ks2) {
        int n;
        ks2.d((short)9);
        int n2 = ks2.b((short)83);
        String[] stringArray = new String[n2];
        int[] nArray = new int[n2];
        int[] nArray2 = new int[n2];
        n2 = ks2.b((short)114);
        int[] nArray3 = new int[n2];
        int[] nArray4 = new int[n2];
        int n3 = ks2.b((short)83, 0);
        int n4 = 0;
        while (n4 < stringArray.length) {
            n = ks2.a((short)83, n3);
            stringArray[n4] = ks2.b(n3);
            nArray[n4] = ks2.a((short)139, n3, n, 0);
            nArray2[n4] = ks2.a((short)144, n3, n, 0);
            n3 = n;
            ++n4;
        }
        n3 = ks2.b((short)114, 0);
        n4 = 0;
        while (n4 < nArray3.length) {
            n = ks2.a((short)114, n3);
            nArray3[n4] = ks2.a(n3, 0);
            nArray4[n4] = ks2.a((short)106, n3, n, 0);
            n3 = n;
            ++n4;
        }
        if (this.c != null) {
            this.c.a(stringArray, nArray, nArray2, nArray3, nArray4);
        }
    }

    private void E(ks object) {
        String string = ((ks)object).d((short)182);
        String[] stringArray = ((ks)object).d((short)183);
        object = ((ks)object).d((short)1);
        stringArray = l.b((String)stringArray, ";");
        String[] stringArray2 = null;
        String[] stringArray3 = null;
        if (stringArray != null && stringArray.length > 0 && stringArray.length % 2 == 0) {
            stringArray2 = new String[stringArray.length / 2];
            stringArray3 = new String[stringArray.length / 2];
            int n = 0;
            while (n < stringArray.length) {
                stringArray3[n / 2] = stringArray[n];
                stringArray2[n / 2] = stringArray[n + 1];
                n += 2;
            }
        }
        this.c.b((String)object, string, stringArray3, stringArray2);
    }

    public final void a() {
        this.g = true;
        if (this.f != null) {
            kt kt2 = this.f;
            try {
                kt2.a.close();
            }
            catch (Throwable throwable) {}
            this.f = null;
        }
    }
}
