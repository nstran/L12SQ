package l12sq.server.storage;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class JsonCharacterStore implements CharacterStore {
    private static final Pattern STRING_PATTERN = Pattern.compile("\"([^\"]+)\"\\s*:\\s*\"([^\"]*)\"");
    private static final Pattern NUMBER_PATTERN = Pattern.compile("\"([^\"]+)\"\\s*:\\s*(-?\\d+)");

    private final Path directory;

    public JsonCharacterStore(Path directory) throws IOException {
        this.directory = directory;
        Files.createDirectories(directory);
    }

    @Override
    public boolean exists(String username) {
        return Files.exists(fileOf(username));
    }

    @Override
    public CharacterData load(String username) throws IOException {
        Path file = fileOf(username);
        if (!Files.exists(file)) {
            return null;
        }

        String content = Files.readString(file, StandardCharsets.UTF_8);
        return new CharacterData(
                stringValue(content, "username", normalize(username)),
                intValue(content, "gender", 0),
                intValue(content, "element", 1),
                intValue(content, "hairOptionId", 79800),
                intValue(content, "hairColorId", 79899),
                intValue(content, "faceOptionId", 79900),
                intValue(content, "skinOptionId", 89900),
                intValue(content, "skinColorId", 89999));
    }

    @Override
    public void save(CharacterData characterData) throws IOException {
        Path file = fileOf(characterData.username());
        Files.createDirectories(file.getParent());

        StringBuilder builder = new StringBuilder();
        builder.append("{\n");
        builder.append("  \"username\": \"").append(escape(characterData.username())).append("\",\n");
        builder.append("  \"gender\": ").append(characterData.gender()).append(",\n");
        builder.append("  \"element\": ").append(characterData.element()).append(",\n");
        builder.append("  \"hairOptionId\": ").append(characterData.hairOptionId()).append(",\n");
        builder.append("  \"hairColorId\": ").append(characterData.hairColorId()).append(",\n");
        builder.append("  \"faceOptionId\": ").append(characterData.faceOptionId()).append(",\n");
        builder.append("  \"skinOptionId\": ").append(characterData.skinOptionId()).append(",\n");
        builder.append("  \"skinColorId\": ").append(characterData.skinColorId()).append("\n");
        builder.append("}\n");

        Files.writeString(file, builder.toString(), StandardCharsets.UTF_8);
    }

    private Path fileOf(String username) {
        return directory.resolve(normalize(username) + ".json");
    }

    private static String stringValue(String content, String key, String defaultValue) {
        Matcher matcher = STRING_PATTERN.matcher(content);
        while (matcher.find()) {
            if (key.equals(matcher.group(1))) {
                return matcher.group(2);
            }
        }
        return defaultValue;
    }

    private static int intValue(String content, String key, int defaultValue) {
        Matcher matcher = NUMBER_PATTERN.matcher(content);
        while (matcher.find()) {
            if (key.equals(matcher.group(1))) {
                return Integer.parseInt(matcher.group(2));
            }
        }
        return defaultValue;
    }

    private static String normalize(String username) {
        return username == null ? "" : username.trim().toLowerCase();
    }

    private static String escape(String value) {
        return value.replace("\\", "\\\\").replace("\"", "\\\"");
    }
}
