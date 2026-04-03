import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Enumeration;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;
import java.util.jar.JarFile;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;

public final class PatchClientHost {
    private static final String[] SOURCE_HOSTS = {
            "210.211.116.129",
            "222.255.121.164",
            "210.211.116.131",
            "123.30.108.85",
            "210.211.116.132",
            "123.30.108.163",
            "210.211.116.134",
            "123.30.108.168",
            "210.211.116.139",
            "123.30.108.233",
            "210.211.116.175",
            "ocs.ola.vn",
            "210.211.116.155",
            "210.211.116.156",
            "210.211.116.157",
            "210.211.116.158"
    };
    private static final Path DEFAULT_APP_SETTINGS = Paths.get("appsettings.properties");

    private PatchClientHost() {
    }

    public static void main(String[] args) throws Exception {
        String sourceJar = args.length > 0 ? args[0] : "e:\\L12SQ\\loan-12-su-quan.jar";
        String outputJar = args.length > 1 ? args[1] : "e:\\L12SQ\\loan-12-su-quan-local.jar";
        String targetHost = args.length > 2 ? args[2] : configuredHost();
        Map<String, String> hostReplacements = buildHostReplacements(targetHost);

        int patchedEntries = 0;

        try (JarFile jarFile = new JarFile(sourceJar);
             JarOutputStream jos = new JarOutputStream(new FileOutputStream(outputJar))) {

            Enumeration<JarEntry> entries = jarFile.entries();
            while (entries.hasMoreElements()) {
                JarEntry entry = entries.nextElement();
                byte[] data = readAll(jarFile, entry);
                byte[] patched = data;

                if (entry.getName().endsWith(".class")) {
                    byte[] candidate = patchClassUtf8Constants(data, hostReplacements);
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
        System.out.println("Target host: " + targetHost);
    }

    private static byte[] patchClassUtf8Constants(byte[] original, Map<String, String> hostReplacements) throws IOException {
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
                    String replacement = hostReplacements.get(value);
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

    private static Map<String, String> buildHostReplacements(String targetHost) {
        Map<String, String> replacements = new LinkedHashMap<>();
        for (String sourceHost : SOURCE_HOSTS) {
            replacements.put(sourceHost, targetHost);
        }
        return replacements;
    }

    private static String configuredHost() {
        Properties properties = new Properties();
        if (Files.exists(DEFAULT_APP_SETTINGS)) {
            try (InputStream input = Files.newInputStream(DEFAULT_APP_SETTINGS)) {
                properties.load(input);
                String value = properties.getProperty("server.advertisedHost");
                if (value != null && !value.trim().isEmpty()) {
                    return value.trim();
                }
            } catch (IOException exception) {
                System.err.println("Unable to read appsettings.properties: " + exception.getMessage());
            }
        }
        return "127.0.0.1";
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
