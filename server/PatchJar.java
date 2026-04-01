import java.io.*;
import java.util.*;
import java.util.jar.*;

/**
 * PatchJar - Replaces server IP strings inside .class constant pools.
 * Handles variable-length string replacement properly.
 */
public class PatchJar {

    // IPs to replace (from eh.class and km.class)
    private static final Map<String, String> REPLACEMENTS = new LinkedHashMap<>();
    static {
        // Auth IPs (eh.z)
        REPLACEMENTS.put("210.211.116.129", "127.0.0.1");
        REPLACEMENTS.put("222.255.121.164", "127.0.0.1");
        REPLACEMENTS.put("210.211.116.131", "127.0.0.1");
        REPLACEMENTS.put("123.30.108.85",   "127.0.0.1");
        REPLACEMENTS.put("210.211.116.132", "127.0.0.1");
        REPLACEMENTS.put("123.30.108.163",  "127.0.0.1");
        REPLACEMENTS.put("210.211.116.134", "127.0.0.1");
        REPLACEMENTS.put("123.30.108.168",  "127.0.0.1");
        REPLACEMENTS.put("210.211.116.139", "127.0.0.1");
        REPLACEMENTS.put("123.30.108.233",  "127.0.0.1");
        REPLACEMENTS.put("210.211.116.175", "127.0.0.1");
        REPLACEMENTS.put("ocs.ola.vn",      "127.0.0.1");
        // Game IPs (km.a)
        // Auth port (dv.class stores as String)
        REPLACEMENTS.put("1236", "7236");
        // Game IPs (km.a)
        REPLACEMENTS.put("210.211.116.155", "127.0.0.1");
        REPLACEMENTS.put("210.211.116.156", "127.0.0.1");
        REPLACEMENTS.put("210.211.116.157", "127.0.0.1");
        REPLACEMENTS.put("210.211.116.158", "127.0.0.1");
    }

    public static void main(String[] args) throws Exception {
        String srcJar = args.length > 0 ? args[0] : "d:\\L12SQ\\loan-12-su-quan.jar";
        String dstJar = args.length > 1 ? args[1] : "d:\\L12SQ\\loan-12-su-quan-offline.jar";

        System.out.println("=== L12SQ JAR Patcher ===");
        System.out.println("Source: " + srcJar);
        System.out.println("Output: " + dstJar);

        int totalPatches = 0;
        try (JarInputStream jis = new JarInputStream(new FileInputStream(srcJar));
             JarOutputStream jos = new JarOutputStream(new FileOutputStream(dstJar))) {

            JarEntry entry;
            while ((entry = jis.getNextJarEntry()) != null) {
                byte[] data = readAll(jis);

                if (entry.getName().endsWith(".class")) {
                    // Binary patch game port 1238 -> 7238 in kq.class
                    if (entry.getName().equals("kq.class")) {
                        data = patchGamePort(data, 1238, 7238);
                    }
                    int patches = countPatches(data);
                    if (patches > 0) {
                        System.out.println("  Patching: " + entry.getName() + " (" + patches + " strings)");
                        data = patchClassBytes(data);
                        totalPatches += patches;
                    }
                }
                jos.putNextEntry(new JarEntry(entry.getName()));
                jos.write(data);
                jos.closeEntry();
            }
        }
        System.out.println("\nDone! Total patches: " + totalPatches);
        System.out.println("Output JAR: " + dstJar);
    }

    private static int countPatches(byte[] data) {
        int count = 0;
        for (String ip : REPLACEMENTS.keySet()) {
            byte[] pat = ip.getBytes(java.nio.charset.StandardCharsets.UTF_8);
            if (indexOf(data, pat, 0) >= 0) count++;
        }
        return count;
    }

    /** Patch all IP strings in the class file constant pool */
    private static byte[] patchClassBytes(byte[] orig) throws Exception {
        ByteArrayOutputStream out = new ByteArrayOutputStream(orig.length + 256);
        DataInputStream dis = new DataInputStream(new ByteArrayInputStream(orig));

        // Magic + version
        out.write(readBytes(dis, 8));

        // Constant pool
        int cpCount = dis.readUnsignedShort();
        writeShort(out, cpCount);

        for (int i = 1; i < cpCount; i++) {
            int tag = dis.readUnsignedByte();
            out.write(tag);

            switch (tag) {
                case 1: { // CONSTANT_Utf8
                    int len = dis.readUnsignedShort();
                    byte[] str = readBytes(dis, len);
                    String s = new String(str, "UTF-8");

                    if (REPLACEMENTS.containsKey(s)) {
                        byte[] rep = REPLACEMENTS.get(s).getBytes("UTF-8");
                        writeShort(out, rep.length);
                        out.write(rep);
                    } else {
                        writeShort(out, len);
                        out.write(str);
                    }
                    break;
                }
                case 3: case 4: case 9: case 10: case 11: case 12: case 17: case 18:
                    out.write(readBytes(dis, 4)); break;
                case 5: case 6:
                    out.write(readBytes(dis, 8)); i++; break;
                case 7: case 8: case 16: case 19: case 20:
                    out.write(readBytes(dis, 2)); break;
                case 15:
                    out.write(readBytes(dis, 3)); break;
                default:
                    throw new RuntimeException("Unknown CP tag: " + tag + " at index " + i);
            }
        }

        // Copy rest of class file unchanged
        byte[] rest = dis.readAllBytes();
        out.write(rest);
        return out.toByteArray();
    }

    private static byte[] readBytes(DataInputStream dis, int n) throws IOException {
        byte[] b = new byte[n];
        dis.readFully(b);
        return b;
    }

    private static void writeShort(OutputStream os, int v) throws IOException {
        os.write((v >> 8) & 0xFF);
        os.write(v & 0xFF);
    }

    private static byte[] readAll(InputStream is) throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        byte[] buf = new byte[4096];
        int n;
        while ((n = is.read(buf)) > 0) bos.write(buf, 0, n);
        return bos.toByteArray();
    }

    private static int indexOf(byte[] data, byte[] pattern, int from) {
        outer:
        for (int i = from; i <= data.length - pattern.length; i++) {
            for (int j = 0; j < pattern.length; j++) {
                if (data[i + j] != pattern[j]) continue outer;
            }
            return i;
        }
        return -1;
    }

    /** Patch integer constant (sipush instruction) for port number in bytecode */
    private static byte[] patchGamePort(byte[] data, int oldPort, int newPort) {
        // sipush = 0x11, followed by 2-byte big-endian short
        byte[] pattern = new byte[]{0x11, (byte)((oldPort >> 8) & 0xFF), (byte)(oldPort & 0xFF)};
        byte[] replacement = new byte[]{0x11, (byte)((newPort >> 8) & 0xFF), (byte)(newPort & 0xFF)};
        int count = 0;
        for (int i = 0; i <= data.length - 3; i++) {
            if (data[i] == pattern[0] && data[i+1] == pattern[1] && data[i+2] == pattern[2]) {
                data[i+1] = replacement[1];
                data[i+2] = replacement[2];
                count++;
            }
        }
        if (count > 0) System.out.println("  Patching: kq.class (game port " + oldPort + " -> " + newPort + ", " + count + "x)");
        return data;
    }
}
