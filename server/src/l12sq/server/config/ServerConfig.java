package l12sq.server.config;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

public record ServerConfig(
        String advertisedHost,
        int authPort,
        int gamePort,
        Path accountsFile,
        Path charactersDirectory) {
    private static final Path DEFAULT_CONFIG_PATH = Paths.get("appsettings.properties");

    public static ServerConfig defaults() {
        ServerConfig fallback = new ServerConfig(
                "127.0.0.1",
                1236,
                1238,
                Paths.get("server", "data", "accounts.json"),
                Paths.get("server", "data", "chars"));
        return load(DEFAULT_CONFIG_PATH, fallback);
    }

    private static ServerConfig load(Path configFile, ServerConfig fallback) {
        if (!Files.exists(configFile)) {
            return fallback;
        }

        Properties properties = new Properties();
        try (InputStream input = Files.newInputStream(configFile)) {
            properties.load(input);
        } catch (IOException exception) {
            System.err.println("[CONFIG] Unable to read " + configFile + ": " + exception.getMessage());
            return fallback;
        }

        return new ServerConfig(
                stringProperty(properties, "server.advertisedHost", fallback.advertisedHost()),
                intProperty(properties, "server.authPort", fallback.authPort()),
                intProperty(properties, "server.gamePort", fallback.gamePort()),
                Paths.get(stringProperty(properties, "storage.accountsFile", fallback.accountsFile().toString())),
                Paths.get(stringProperty(properties, "storage.charactersDirectory", fallback.charactersDirectory().toString())));
    }

    private static String stringProperty(Properties properties, String key, String defaultValue) {
        String value = properties.getProperty(key);
        return value == null || value.trim().isEmpty() ? defaultValue : value.trim();
    }

    private static int intProperty(Properties properties, String key, int defaultValue) {
        String value = properties.getProperty(key);
        if (value == null || value.trim().isEmpty()) {
            return defaultValue;
        }
        try {
            return Integer.parseInt(value.trim());
        } catch (NumberFormatException exception) {
            System.err.println("[CONFIG] Invalid integer for " + key + ": " + value + ", fallback=" + defaultValue);
            return defaultValue;
        }
    }
}
