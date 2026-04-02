import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Enumeration;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.jar.JarFile;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;

public final class PatchClientHost {
    private static final Map<String, String> HOST_REPLACEMENTS = new LinkedHashMap<>();

    static {
        HOST_REPLACEMENTS.put("210.211.116.129", "192.168.1.226");
        HOST_REPLACEMENTS.put("222.255.121.164", "192.168.1.226");
        HOST_REPLACEMENTS.put("210.211.116.131", "192.168.1.226");
        HOST_REPLACEMENTS.put("123.30.108.85", "192.168.1.226");
        HOST_REPLACEMENTS.put("210.211.116.132", "192.168.1.226");
        HOST_REPLACEMENTS.put("123.30.108.163", "192.168.1.226");
        HOST_REPLACEMENTS.put("210.211.116.134", "192.168.1.226");
        HOST_REPLACEMENTS.put("123.30.108.168", "192.168.1.226");
        HOST_REPLACEMENTS.put("210.211.116.139", "192.168.1.226");
        HOST_REPLACEMENTS.put("123.30.108.233", "192.168.1.226");
        HOST_REPLACEMENTS.put("210.211.116.175", "192.168.1.226");
        HOST_REPLACEMENTS.put("ocs.ola.vn", "192.168.1.226");
        HOST_REPLACEMENTS.put("210.211.116.155", "192.168.1.226");
        HOST_REPLACEMENTS.put("210.211.116.156", "192.168.1.226");
        HOST_REPLACEMENTS.put("210.211.116.157", "192.168.1.226");
        HOST_REPLACEMENTS.put("210.211.116.158", "192.168.1.226");
    }

    private PatchClientHost() {
    }

    public static void main(String[] args) throws Exception {
        String sourceJar = args.length > 0 ? args[0] : "d:\\L12SQ\\loan-12-su-quan.jar";
        String outputJar = args.length > 1 ? args[1] : "d:\\L12SQ\\loan-12-su-quan-local.jar";

        int patchedEntries = 0;

        try (JarFile jarFile = new JarFile(sourceJar);
             JarOutputStream jos = new JarOutputStream(new FileOutputStream(outputJar))) {

            Enumeration<JarEntry> entries = jarFile.entries();
            while (entries.hasMoreElements()) {
                JarEntry entry = entries.nextElement();
                byte[] data = readAll(jarFile, entry);
                byte[] patched = data;

                if (entry.getName().endsWith(".class")) {
                    byte[] candidate = patchClassUtf8Constants(data);
                    if (candidate != data) {
                        patched = candidate;
                        patchedEntries++;
                    }
                }

                JarEntry outEntry = new JarEntry(entry.getName());
                jos.putNextEntry(outEntry);
                jos.write(patched);
                jos.closeEntry();
            }
        }

        System.out.println("Patched client jar: " + outputJar);
        System.out.println("Patched class entries: " + patchedEntries);
    }

    private static byte[] patchClassUtf8Constants(byte[] original) throws IOException {
        DataInputStream input = new DataInputStream(new ByteArrayInputStream(original));
        ByteArrayOutputStream output = new ByteArrayOutputStream(original.length + 512);

        output.write(readBytes(input, 8));
        int constantPoolCount = input.readUnsignedShort();
        writeShort(output, constantPoolCount);

        boolean changed = false;

        for (int i = 1; i < constantPoolCount; i++) {
            int tag = input.readUnsignedByte();
            output.write(tag);

            switch (tag) {
                case 1 -> {
                    int length = input.readUnsignedShort();
                    byte[] textBytes = readBytes(input, length);
                    String value = new String(textBytes, StandardCharsets.UTF_8);
                    String replacement = HOST_REPLACEMENTS.get(value);
                    if (replacement != null) {
                        byte[] replacementBytes = replacement.getBytes(StandardCharsets.UTF_8);
                        writeShort(output, replacementBytes.length);
                        output.write(replacementBytes);
                        changed = true;
                    } else {
                        writeShort(output, length);
                        output.write(textBytes);
                    }
                }
                case 3, 4, 9, 10, 11, 12, 17, 18 -> output.write(readBytes(input, 4));
                case 5, 6 -> {
                    output.write(readBytes(input, 8));
                    i++;
                }
                case 7, 8, 16, 19, 20 -> output.write(readBytes(input, 2));
                case 15 -> output.write(readBytes(input, 3));
                default -> throw new IOException("Unknown constant pool tag: " + tag);
            }
        }

        output.write(input.readAllBytes());
        return changed ? output.toByteArray() : original;
    }

    private static byte[] readBytes(DataInputStream input, int count) throws IOException {
        byte[] data = new byte[count];
        input.readFully(data);
        return data;
    }

    private static void writeShort(OutputStream output, int value) throws IOException {
        output.write((value >>> 8) & 0xFF);
        output.write(value & 0xFF);
    }

    private static byte[] readAll(JarFile jarFile, JarEntry entry) throws IOException {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        try (var input = jarFile.getInputStream(entry)) {
            byte[] chunk = new byte[4096];
            int read;
            while ((read = input.read(chunk)) != -1) {
                buffer.write(chunk, 0, read);
            }
        }
        return buffer.toByteArray();
    }
}
