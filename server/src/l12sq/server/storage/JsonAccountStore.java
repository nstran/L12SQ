package l12sq.server.storage;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class JsonAccountStore implements AccountStore {
    private static final Pattern ENTRY_PATTERN = Pattern.compile("\"([^\"]+)\"\\s*:\\s*\"([^\"]*)\"");

    private final Path file;
    private final Map<String, String> accounts = new LinkedHashMap<>();

    public JsonAccountStore(Path file) throws IOException {
        this.file = file;
        load();
    }

    @Override
    public synchronized int count() {
        return accounts.size();
    }

    @Override
    public synchronized boolean exists(String username) {
        return accounts.containsKey(normalize(username));
    }

    @Override
    public synchronized String passwordHashOf(String username) {
        return accounts.get(normalize(username));
    }

    @Override
    public synchronized boolean register(String username, String passwordHash) throws IOException {
        String key = normalize(username);
        if (key.isEmpty() || accounts.containsKey(key)) {
            return false;
        }
        accounts.put(key, passwordHash);
        save();
        return true;
    }

    private void load() throws IOException {
        accounts.clear();
        if (!Files.exists(file)) {
            Files.createDirectories(file.getParent());
            Files.writeString(file, "{\n}\n", StandardCharsets.UTF_8);
            return;
        }
        String content = Files.readString(file, StandardCharsets.UTF_8);
        Matcher matcher = ENTRY_PATTERN.matcher(content);
        while (matcher.find()) {
            accounts.put(normalize(matcher.group(1)), matcher.group(2));
        }
    }

    private void save() throws IOException {
        StringBuilder builder = new StringBuilder();
        builder.append("{\n");
        int index = 0;
        for (Map.Entry<String, String> entry : accounts.entrySet()) {
            builder.append("  \"")
                    .append(escape(entry.getKey()))
                    .append("\": \"")
                    .append(escape(entry.getValue()))
                    .append("\"");
            if (index < accounts.size() - 1) {
                builder.append(",");
            }
            builder.append("\n");
            index++;
        }
        builder.append("}\n");
        Files.writeString(file, builder.toString(), StandardCharsets.UTF_8);
    }

    private static String normalize(String username) {
        return username == null ? "" : username.trim().toLowerCase();
    }

    private static String escape(String value) {
        return value.replace("\\", "\\\\").replace("\"", "\\\"");
    }
}
