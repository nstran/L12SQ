/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.microedition.rms.RecordStore
 */
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import javax.microedition.rms.RecordStore;

public final class pa
extends cv {
    private static y b = null;

    private static void H() {
        b = y.a("olacacher", 2048, 1024, 1, 10);
        cv.a(10240);
    }

    public static long b(int[] nArray) {
        if (nArray == null) {
            return 0L;
        }
        byte[] byArray = new byte[nArray.length << 2];
        int n = 0;
        int n2 = 0;
        while (n2 < nArray.length) {
            System.arraycopy(p.a((int)byArray[n2]), 0, byArray, n, 4);
            n += 4;
            ++n2;
        }
        e.a();
        return e.a(byArray);
    }

    public static int a(long l) {
        long l2 = 0L;
        byte[] byArray = cv.a.a(153);
        if (byArray != null) {
            l2 = p.d(byArray);
        }
        if (l2 != l) {
            pa.b(l);
            return 0;
        }
        int n = 0;
        byArray = cv.a.a(154);
        if (byArray != null) {
            n = p.c(byArray);
        }
        return n;
    }

    public static void b(long l) {
        cv.a.b(153, p.a(l));
    }

    public static void b(int n) {
        cv.a.b(154, p.a(n));
    }

    public static void j() {
        cv.a.b(153);
        cv.a.b(154);
        cv.a.a();
    }

    public static long k() {
        int n = 155;
        y y2 = cv.a;
        Object object = y2.a(155);
        Long l = object != null ? new Long(p.d(object)) : null;
        object = l;
        if (l == null) {
            object = new Long(System.currentTimeMillis());
            long l2 = object.longValue();
            if (!cv.a.c(155)) {
                cv.a.b(155, p.a(l2));
            }
        }
        return object.longValue();
    }

    public static void a(String[] stringArray) {
        if (stringArray == null) {
            return;
        }
        byte[] byArray = new byte[1000];
        int n = 0;
        System.arraycopy(p.a(stringArray.length), 0, byArray, 0, 4);
        n += 4;
        int n2 = 0;
        while (n2 < stringArray.length) {
            byte[] byArray2 = stringArray[n2].getBytes();
            System.arraycopy(p.a(byArray2.length), 0, byArray, n, 4);
            System.arraycopy(byArray2, 0, byArray, n += 4, byArray2.length);
            n += byArray2.length;
            ++n2;
        }
        cv.a.b(157, byArray, 0, n);
    }

    public static String[] l() {
        byte[] byArray = cv.a.a(157);
        if (byArray != null) {
            int n = 0;
            int n2 = p.a(byArray, 0);
            n += 4;
            String[] stringArray = new String[n2];
            int n3 = 0;
            while (n3 < stringArray.length) {
                int n4 = p.a(byArray, n);
                stringArray[n3] = new String(byArray, n += 4, n4);
                n += n4;
                ++n3;
            }
            return stringArray;
        }
        return null;
    }

    public static final void m() {
        pa.H();
        int n = 0;
        byte[] byArray = cv.a.a(121);
        if (byArray != null) {
            n = p.c(byArray);
        }
        if (n < 5) {
            try {
                String[] stringArray = RecordStore.listRecordStores();
                if (stringArray != null) {
                    int n2 = 0;
                    while (n2 < stringArray.length) {
                        j.b(stringArray[n2]);
                        ++n2;
                    }
                }
            }
            catch (Throwable throwable) {
                Throwable throwable2 = throwable;
                throwable.printStackTrace();
            }
            pa.H();
            byte[] byArray2 = p.a(5);
            cv.a.b(121, byArray2);
        }
    }

    public static boolean n() {
        return cv.a.c(151);
    }

    public static void o() {
        cv.a.b(151, new byte[]{1});
    }

    public static final String p() {
        byte[] byArray = cv.a.a(112);
        if (byArray != null) {
            return l.a(byArray);
        }
        return null;
    }

    public static final void a(String string) {
        byte[] byArray = null;
        try {
            byArray = l.c(string);
        }
        catch (UnsupportedEncodingException unsupportedEncodingException) {}
        if (byArray == null || byArray.length <= 0) {
            byArray = new byte[]{};
        }
        cv.a.b(112, byArray);
    }

    public static byte[] q() {
        byte[] byArray = b.a(Integer.MIN_VALUE);
        return byArray;
    }

    public static void b(byte[] byArray) {
        if (byArray == null) {
            return;
        }
        b.a(Integer.MIN_VALUE, byArray);
        b.a(Integer.MIN_VALUE, Long.MAX_VALUE);
        b.a();
    }

    public static void b(String object) {
        byte[] byArray = null;
        try {
            byArray = l.c((String)object);
        }
        catch (UnsupportedEncodingException unsupportedEncodingException) {
            object = unsupportedEncodingException;
            unsupportedEncodingException.printStackTrace();
        }
        if (byArray == null) {
            return;
        }
        cv.a.b(119, byArray);
    }

    public static String r() {
        byte[] byArray = cv.a.a(119);
        if (byArray == null) {
            return null;
        }
        return l.a(byArray);
    }

    public static void s() {
        b.b(-2147483647);
        b.b(-2147483646);
        cv.a.b(118);
        cv.a.b(112);
        cv.a.b(150);
        cv.a.a();
        b.a();
        gr.d = 0;
    }

    public static void b(String[] stringArray) {
        if (stringArray == null) {
            return;
        }
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream(2000);
        DataOutputStream dataOutputStream = new DataOutputStream(byteArrayOutputStream);
        Object object = null;
        try {
            dataOutputStream.writeInt(stringArray.length);
            int n = 0;
            while (n < stringArray.length) {
                object = l.c(stringArray[n]);
                dataOutputStream.writeInt(((Object)object).length);
                dataOutputStream.write((byte[])object, 0, ((Object)object).length);
                dataOutputStream.flush();
                byteArrayOutputStream.flush();
                object = byteArrayOutputStream.toByteArray();
                dataOutputStream.close();
                byteArrayOutputStream.close();
                ++n;
            }
        }
        catch (IOException iOException) {
            try {
                dataOutputStream.close();
                byteArrayOutputStream.close();
            }
            catch (IOException iOException2) {
                object = iOException2;
                iOException2.printStackTrace();
            }
            iOException.printStackTrace();
            return;
        }
        if (object == null) {
            return;
        }
        b.a(-2147483646, (byte[])object);
        b.a(-2147483646, Long.MAX_VALUE);
        b.a();
    }

    public static String[] t() {
        String[] stringArray;
        Object object = b.a(-2147483646);
        if (object == null) {
            return null;
        }
        object = new ByteArrayInputStream((byte[])object);
        DataInputStream dataInputStream = new DataInputStream((InputStream)object);
        Object var2_2 = null;
        try {
            int n = dataInputStream.readInt();
            stringArray = new String[n];
            int n2 = 0;
            while (n2 < stringArray.length) {
                byte[] byArray = new byte[dataInputStream.readInt()];
                dataInputStream.read(byArray, 0, byArray.length);
                stringArray[n2] = l.a(byArray);
                ++n2;
            }
            dataInputStream.close();
            object.close();
        }
        catch (Exception exception) {
            try {
                dataInputStream.close();
                object.close();
            }
            catch (IOException iOException) {
                IOException iOException2 = iOException;
                iOException.printStackTrace();
            }
            return null;
        }
        b.b(-2147483646);
        b.a();
        return stringArray;
    }

    public static void a(j[] jArray, int n) {
        if (jArray == null) {
            return;
        }
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream(2000);
        DataOutputStream dataOutputStream = new DataOutputStream(byteArrayOutputStream);
        Object object = null;
        try {
            int n2 = jArray.length;
            int n3 = 0;
            while (n3 < jArray.length) {
                if (jArray[n3].a() == 1) {
                    --n2;
                }
                ++n3;
            }
            dataOutputStream.writeInt(n2);
            n3 = 0;
            while (n3 < jArray.length) {
                if (jArray[n3].a() != 1) {
                    byte[] byArray = l.c(jArray[n3].b());
                    dataOutputStream.writeInt(byArray.length);
                    dataOutputStream.write(byArray, 0, byArray.length);
                    n2 = 0;
                    du[] duArray = jArray[n3].c();
                    if (duArray != null) {
                        n2 = duArray.length;
                    }
                    dataOutputStream.writeInt(n2);
                    int n4 = 0;
                    while (n4 < n2) {
                        byte[] byArray2 = l.c(duArray[n4].a());
                        dataOutputStream.writeInt(byArray2.length);
                        dataOutputStream.write(byArray2, 0, byArray2.length);
                        byArray2 = l.c(duArray[n4].b());
                        if (byArray2 == null) {
                            dataOutputStream.writeInt(0);
                        } else {
                            dataOutputStream.writeInt(byArray2.length);
                            dataOutputStream.write(byArray2, 0, byArray2.length);
                        }
                        ++n4;
                    }
                }
                ++n3;
            }
            dataOutputStream.flush();
            byteArrayOutputStream.flush();
            dataOutputStream.close();
            object = byteArrayOutputStream.toByteArray();
            byteArrayOutputStream.close();
        }
        catch (IOException iOException) {
            try {
                dataOutputStream.close();
                byteArrayOutputStream.close();
            }
            catch (IOException iOException2) {
                object = iOException2;
                iOException2.printStackTrace();
            }
            object = null;
            iOException.printStackTrace();
        }
        if (object == null) {
            return;
        }
        b.a(-2147483647, (byte[])object);
        b.a(-2147483647, Long.MAX_VALUE);
        b.a();
        int n5 = n;
        byte[] byArray = p.a(n5);
        cv.a.b(118, byArray);
    }

    public static j[] u() {
        j[] jArray;
        Object object = b.a(-2147483647);
        if (object == null) {
            return null;
        }
        object = new ByteArrayInputStream((byte[])object);
        DataInputStream dataInputStream = new DataInputStream((InputStream)object);
        Object var2_2 = null;
        try {
            int n = dataInputStream.readInt();
            jArray = new j[n];
            int n2 = 0;
            while (n2 < jArray.length) {
                byte[] byArray = new byte[dataInputStream.readInt()];
                dataInputStream.read(byArray, 0, byArray.length);
                jArray[n2] = new j(l.a(byArray));
                du[] duArray = new du[dataInputStream.readInt()];
                int n3 = 0;
                while (n3 < duArray.length) {
                    duArray[n3] = new du();
                    byArray = new byte[dataInputStream.readInt()];
                    dataInputStream.read(byArray, 0, byArray.length);
                    duArray[n3].a(l.a(byArray));
                    byArray = new byte[dataInputStream.readInt()];
                    if (byArray.length > 0) {
                        dataInputStream.read(byArray, 0, byArray.length);
                        duArray[n3].b(l.a(byArray));
                    } else {
                        duArray[n3].b(duArray[n3].a());
                    }
                    ++n3;
                }
                jArray[n2].a(duArray);
                jArray[n2].a((short)0);
                dataInputStream.close();
                object.close();
                ++n2;
            }
        }
        catch (IOException iOException) {
            try {
                dataInputStream.close();
                object.close();
            }
            catch (IOException iOException2) {
                IOException iOException3 = iOException2;
                iOException2.printStackTrace();
            }
            iOException.printStackTrace();
            return null;
        }
        return jArray;
    }

    public static int v() {
        byte[] byArray = cv.a.a(118);
        if (byArray == null) {
            return gr.d;
        }
        return p.c(byArray);
    }

    public static final boolean c(int n) {
        if (n < -2147483643) {
            n += 5;
        }
        return b.c(n);
    }

    public static final void a(byte[] byArray, String object) {
        byte[] byArray2;
        if (byArray == null) {
            return;
        }
        int n = ((String)object).hashCode();
        if (n < -2147483643) {
            n += 5;
        }
        try {
            byArray2 = l.c((String)object);
        }
        catch (UnsupportedEncodingException unsupportedEncodingException) {
            byArray2 = ((String)object).getBytes();
        }
        object = new byte[byArray.length + byArray2.length + 4];
        System.arraycopy(p.a(byArray2.length), 0, object, 0, 4);
        System.arraycopy(byArray2, 0, object, 4, byArray2.length);
        System.arraycopy(byArray, 0, object, 4 + byArray2.length, byArray.length);
        b.b(n, (byte[])object);
    }

    public static final byte[] d(int n) {
        if (n < -2147483643) {
            n += 5;
        }
        byte[] byArray = b.a(n);
        b.e();
        return byArray;
    }

    public static final int w() {
        byte[] byArray = cv.a.a(113);
        if (byArray == null) {
            return gr.a;
        }
        return p.c(byArray);
    }

    public static final void e(int n) {
        byte[] byArray = p.a(n);
        cv.a.b(113, byArray);
    }

    public static final int x() {
        byte[] byArray = cv.a.a(120);
        if (byArray == null) {
            return 0;
        }
        return p.c(byArray);
    }

    public static final void f(int n) {
        byte[] byArray = p.a(n);
        cv.a.b(120, byArray);
    }

    public static final int y() {
        byte[] byArray = cv.a.a(117);
        if (byArray == null) {
            return gr.b;
        }
        return p.c(byArray);
    }

    public static final void g(int n) {
        byte[] byArray = p.a(n);
        cv.a.b(117, byArray);
    }

    public static final void z() {
        byte[] byArray = new byte[]{1};
        cv.a.b(114, byArray);
    }

    public static final void A() {
        byte[] byArray = new byte[3];
        byArray[1] = 17;
        byte[] byArray2 = byArray;
        cv.a.b(152, byArray2);
    }

    public static final boolean B() {
        if (cv.a.c(152)) {
            byte[] byArray = cv.a.a(152);
            return byArray[0] == 0 && byArray[1] == 17 && byArray[2] == 0;
        }
        return false;
    }

    public static final void c(long l2) {
        byte[] byArray = p.a(l2);
        cv.a.b(160, byArray);
    }

    public static void h(int n) {
        byte[] byArray = p.a((long)n);
        cv.a.b(162, byArray);
    }

    public static final int C() {
        byte[] byArray;
        if (cv.a.c(162) && (byArray = cv.a.a(162)) != null) {
            return p.c(byArray);
        }
        return -1;
    }

    public static final boolean d(long l2) {
        byte[] byArray;
        if (cv.a.c(160) && (byArray = cv.a.a(160)) != null) {
            long l3 = p.d(byArray);
            return l3 == l2;
        }
        return false;
    }

    public static final boolean D() {
        return cv.a.c(114);
    }

    public static String[][] E() {
        String[][] stringArray = null;
        if (cv.a.c(115)) {
            byte[] byArray = cv.a.a(115);
            int n = 0;
            int n2 = p.a(byArray, 0);
            stringArray = new String[n2][2];
            n += 4;
            int n3 = 0;
            while (n3 < n2) {
                int n4 = p.a(byArray, n);
                stringArray[n3][0] = l.a(byArray, n += 4, n4);
                n += n4;
                n4 = p.a(byArray, n);
                stringArray[n3][1] = l.a(byArray, n += 4, n4);
                n += n4;
                ++n3;
            }
        }
        return stringArray;
    }

    public static String[][] a(String[][] stringArray, String object, String string) {
        int n;
        String string2 = string = string == null ? "" : string;
        if (((String)object).length() > 15) {
            object = String.valueOf(((String)object).substring(0, 12)) + "...";
        }
        if (stringArray == null) {
            stringArray = "#sq".equals(string) ? new String[][]{{object, string}, {"#ola", "#ola"}} : ("#ola".equals(string) ? new String[][]{{object, string}, {"#sq", "#sq"}} : new String[][]{{object, string}, {"#sq", "#sq"}, {"#ola", "#ola"}});
        } else {
            boolean bl = false;
            int n2 = 0;
            while (n2 < stringArray.length) {
                if (stringArray[n2][1].equals(string)) {
                    n = n2 - 1;
                    while (n >= 0) {
                        stringArray[n + 1] = stringArray[n];
                        --n;
                    }
                    stringArray[0] = new String[]{object, string};
                    bl = true;
                    break;
                }
                ++n2;
            }
            if (!bl) {
                String[][] stringArray2 = new String[stringArray.length + 1][2];
                n = 1;
                while (n < stringArray2.length) {
                    stringArray2[n] = stringArray[n - 1];
                    ++n;
                }
                stringArray = stringArray2;
                stringArray2[0] = new String[]{object, string};
            }
        }
        if (stringArray.length > 5) {
            String[][] stringArray3 = new String[stringArray.length - 1][2];
            int n3 = 0;
            while (n3 < stringArray3.length) {
                stringArray3[n3] = stringArray[n3];
                ++n3;
            }
            stringArray = stringArray3;
        }
        byte[] byArray = new byte[1000];
        int n4 = 0;
        System.arraycopy(p.a(stringArray.length), 0, byArray, 0, 4);
        n4 += 4;
        n = 0;
        while (n < stringArray.length) {
            byte[] byArray2 = pa.a(stringArray[n][0], stringArray[n][1]);
            object = byArray2;
            System.arraycopy(byArray2, 0, byArray, n4, ((Object)object).length);
            n4 += ((Object)object).length;
            ++n;
        }
        cv.a.b(115, byArray, 0, n4);
        return stringArray;
    }

    private static byte[] a(String object, String object2) {
        byte[] byArray = null;
        try {
            object = l.c((String)object);
            byte[] byArray2 = l.c((String)object2);
            object2 = byArray2;
            if (byArray2 == null) {
                object2 = new byte[0];
            }
            byArray = new byte[((Object)object).length + ((Object)object2).length + 8];
            System.arraycopy(p.a(((Object)object).length), 0, byArray, 0, 4);
            System.arraycopy(object, 0, byArray, 4, ((Object)object).length);
            int n = 4 + ((Object)object).length;
            System.arraycopy(p.a(((Object)object2).length), 0, byArray, n, 4);
            System.arraycopy(object2, 0, byArray, n += 4, ((Object)object2).length);
        }
        catch (Throwable throwable) {
            object = throwable;
            throwable.printStackTrace();
            return null;
        }
        return byArray;
    }

    public static final int F() {
        byte[] byArray = cv.a.a(116);
        if (byArray == null) {
            return 0;
        }
        return p.c(byArray);
    }

    public static final void i(int n) {
        byte[] byArray = p.a(n);
        cv.a.b(116, byArray);
    }

    public static final void b(boolean bl) {
        byte[] byArray = new byte[]{(byte)(bl ? 1 : 0)};
        cv.a.a(150, byArray);
        if (gr.q != bl) {
            gr.q = !bl;
        }
        cv.a.a();
    }

    public static final int G() {
        byte[] byArray = cv.a.a(150);
        if (byArray == null) {
            return 0;
        }
        return byArray[0];
    }
}
