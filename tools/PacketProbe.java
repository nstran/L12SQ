import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

public final class PacketProbe {
    public static void main(String[] args) throws Exception {
        byte[] cmd8 = buildCmd8Packet();
        byte[] cmd9 = buildCmd9Packet();

        probe("CMD8", "f", cmd8);
        probe("CMD9_FULL", "a", cmd9);
    }

    private static void probe(String label, String methodName, byte[] payload) throws Exception {
        Object ks = makeKs(payload);
        Constructor<?> ctor = Class.forName("kw").getDeclaredConstructor(java.io.InputStream.class);
        ctor.setAccessible(true);
        Object kw = ctor.newInstance(new ByteArrayInputStream(new byte[0]));

        Method method = Class.forName("kw").getDeclaredMethod(methodName, Class.forName("ks"));
        method.setAccessible(true);

        System.out.println("==== " + label + " ====");
        dumpKs((Object) ks);
        if ("CMD8".equals(label)) {
            inspectCmd8(ks);
        }
        try {
            method.invoke(kw, ks);
            System.out.println(label + " parsed OK");
        } catch (Throwable throwable) {
            Throwable cause = throwable.getCause() == null ? throwable : throwable.getCause();
            System.out.println(label + " failed: " + cause);
            cause.printStackTrace(System.out);
        }
    }

    private static Object makeKs(byte[] payload) throws Exception {
        Class<?> krClass = Class.forName("kr");
        Constructor<?> krCtor = krClass.getDeclaredConstructor(short.class, byte[].class);
        krCtor.setAccessible(true);

        ByteArrayInputStream input = new ByteArrayInputStream(payload);
        DataInputStream dis = new DataInputStream(input);
        java.util.ArrayList<Object> tags = new java.util.ArrayList<>();

        while (dis.available() >= 5) {
            short tag = (short) dis.readUnsignedByte();
            int len = dis.readInt();
            byte[] value = dis.readNBytes(len);
            tags.add(krCtor.newInstance(tag, value));
        }

        Object ks = Class.forName("ks").getDeclaredConstructor().newInstance();
        Field a = Class.forName("ks").getDeclaredField("a");
        Field b = Class.forName("ks").getDeclaredField("b");
        Field c = Class.forName("ks").getDeclaredField("c");
        a.setAccessible(true);
        b.setAccessible(true);
        c.setAccessible(true);
        a.setInt(ks, payload.length);
        b.setInt(ks, 0);
        Object array = java.lang.reflect.Array.newInstance(krClass, tags.size());
        for (int i = 0; i < tags.size(); i++) {
            java.lang.reflect.Array.set(array, i, tags.get(i));
        }
        c.set(ks, array);
        return ks;
    }

    private static void dumpKs(Object ks) throws Exception {
        Field c = Class.forName("ks").getDeclaredField("c");
        c.setAccessible(true);
        Object array = c.get(ks);
        int len = java.lang.reflect.Array.getLength(array);
        for (int i = 0; i < len; i++) {
            Object kr = java.lang.reflect.Array.get(array, i);
            Field tagField = Class.forName("kr").getDeclaredField("a");
            Field valueField = Class.forName("kr").getDeclaredField("b");
            tagField.setAccessible(true);
            valueField.setAccessible(true);
            short tag = tagField.getShort(kr);
            byte[] value = (byte[]) valueField.get(kr);
            System.out.println("tag=" + tag + " len=" + value.length);
        }
    }

    private static void inspectCmd8(Object ks) throws Exception {
        Class<?> ksClass = Class.forName("ks");
        Method firstIndex = ksClass.getDeclaredMethod("b", short.class, int.class);
        Method entryLimit = ksClass.getDeclaredMethod("a", short.class, int.class);
        Method countBetween = ksClass.getDeclaredMethod("a", short.class, int.class, int.class);
        Method intAt = ksClass.getDeclaredMethod("a", int.class, int.class);
        Method strBetween = ksClass.getDeclaredMethod("d", short.class, int.class, int.class);
        Method bytesBetween = ksClass.getDeclaredMethod("c", short.class, int.class, int.class);
        firstIndex.setAccessible(true);
        entryLimit.setAccessible(true);
        countBetween.setAccessible(true);
        intAt.setAccessible(true);
        strBetween.setAccessible(true);
        bytesBetween.setAccessible(true);

        int entryIndex = ((Integer) firstIndex.invoke(ks, (short) 90, 0)).intValue();
        int ordinal = 0;
        while (entryIndex >= 0) {
            int limit = ((Integer) entryLimit.invoke(ks, (short) 90, entryIndex)).intValue();
            int optionId = ((Integer) intAt.invoke(ks, entryIndex, 0)).intValue();
            int count96 = ((Integer) countBetween.invoke(ks, (short) 96, entryIndex, limit)).intValue();
            String name92 = (String) strBetween.invoke(ks, (short) 92, entryIndex, limit);
            byte[] bytes95 = (byte[]) bytesBetween.invoke(ks, (short) 95, entryIndex, limit);
            byte[] bytes98 = (byte[]) bytesBetween.invoke(ks, (short) 98, entryIndex, limit);
            System.out.println(
                    "entry#" + ordinal
                            + " idx=" + entryIndex
                            + " limit=" + limit
                            + " optionId=" + optionId
                            + " name=" + name92
                            + " count96=" + count96
                            + " len95=" + (bytes95 == null ? -1 : bytes95.length)
                            + " len98=" + (bytes98 == null ? -1 : bytes98.length)
            );
            entryIndex = ((Integer) firstIndex.invoke(ks, (short) 90, entryIndex + 1)).intValue();
            ordinal++;
        }
    }

    private static byte[] buildCmd8Packet() throws Exception {
        ByteArrayOutputStream payload = new ByteArrayOutputStream();
        appendCmd8Entry(payload, 79800, 0, 0, "Nam Mat 1", 79899, "Nam Mat 1");
        appendCmd8Entry(payload, 79810, 1, 0, "Nam Toc 1", 79811, "Nam Toc 1");
        appendCmd8Entry(payload, 89900, 2, 0, "Nam Da 1", 89901, "Nam Da 1");
        appendCmd8Entry(payload, 79900, 0, 1, "Nu Mat 1", 79999, "Nu Mat 1");
        appendCmd8Entry(payload, 79910, 1, 1, "Nu Toc 1", 79911, "Nu Toc 1");
        appendCmd8Entry(payload, 89910, 2, 1, "Nu Da 1", 89911, "Nu Da 1");
        return payload.toByteArray();
    }

    private static byte[] buildCmd9Packet() throws Exception {
        ByteArrayOutputStream payload = new ByteArrayOutputStream();
        writeTag(payload, 134, new byte[]{1});
        writeTag(payload, 9, "nst".getBytes("UTF-8"));
        writeTag(payload, 26, "Tan Thu".getBytes("UTF-8"));
        writeTag(payload, 15, new byte[]{1});
        writeTag(payload, 16, new byte[]{0});
        writeTag(payload, 27, intBytes(1));
        writeTag(payload, 17, intBytes(1000));
        writeTag(payload, 47, intBytes(1000));
        writeTag(payload, 18, intBytes(500));
        writeTag(payload, 48, intBytes(500));
        writeTag(payload, 118, intBytes(10));
        writeTag(payload, 119, intBytes(10));
        writeTag(payload, 120, intBytes(10));
        writeTag(payload, 121, intBytes(10));
        writeTag(payload, 196, intBytes(0));
        writeTag(payload, 197, intBytes(0));
        writeTag(payload, 198, intBytes(0));
        writeTag(payload, 199, intBytes(0));
        writeTag(payload, 116, intBytes(0));
        writeTag(payload, 115, intBytes(100));
        writeTag(payload, 42, intBytes(50));
        writeTag(payload, 43, intBytes(30));
        writeTag(payload, 99, intBytes(1000));
        writeTag(payload, 53, intBytes(0));
        writeTag(payload, 76, intBytes(0));
        writeTag(payload, 73, intBytes(0));
        writeTag(payload, 74, intBytes(0));
        writeTag(payload, 108, intBytes(5));
        writeTag(payload, 109, intBytes(5));
        writeTag(payload, 151, "".getBytes("UTF-8"));
        writeTag(payload, 160, intBytes(0));
        writeTag(payload, 165, new byte[]{0});
        writeTag(payload, 166, new byte[]{0});
        writeTag(payload, 132, longBytes(0L));
        appendCmd9Entry(payload, 79800, 0, 79899, 79801);
        appendCmd9Entry(payload, 79810, 1, 79811, 79812);
        appendCmd9Entry(payload, 89900, 2, 89901, 89902);
        return payload.toByteArray();
    }

    private static void appendCmd9Entry(ByteArrayOutputStream payload, int optionId, int category, int spriteId, int alternateSpriteId) throws Exception {
        writeTag(payload, 90, intBytes(optionId));
        writeTag(payload, 91, new byte[]{(byte) category});
        writeTag(payload, 93, intBytes(spriteId));
        writeTag(payload, 95, intArrayBytes(spriteId));
        writeTag(payload, 96, intBytes(alternateSpriteId));
        writeTag(payload, 98, intArrayBytes(alternateSpriteId));
    }

    private static void appendCmd8Entry(ByteArrayOutputStream payload, int optionId, int category, int gender, String name, int spriteId, String spriteName) throws Exception {
        writeTag(payload, 90, intBytes(optionId));
        writeTag(payload, 91, new byte[]{(byte) category});
        writeTag(payload, 92, name.getBytes("UTF-8"));
        writeTag(payload, 16, new byte[]{(byte) gender});
        writeTag(payload, 93, intBytes(spriteId));
        writeTag(payload, 94, spriteName.getBytes("UTF-8"));
        writeTag(payload, 95, intArrayBytes(spriteId));
        writeTag(payload, 96, intBytes(spriteId));
        writeTag(payload, 97, spriteName.getBytes("UTF-8"));
        writeTag(payload, 98, intArrayBytes(spriteId));
    }

    private static void writeTag(ByteArrayOutputStream output, int tag, byte[] value) throws Exception {
        output.write(tag & 0xFF);
        output.write(intBytes(value.length));
        output.write(value);
    }

    private static byte[] intBytes(int value) {
        return java.nio.ByteBuffer.allocate(4).putInt(value).array();
    }

    private static byte[] longBytes(long value) {
        return java.nio.ByteBuffer.allocate(8).putLong(value).array();
    }

    private static byte[] intArrayBytes(int... values) {
        java.nio.ByteBuffer buffer = java.nio.ByteBuffer.allocate(values.length * 4);
        for (int value : values) {
            buffer.putInt(value);
        }
        return buffer.array();
    }
}
