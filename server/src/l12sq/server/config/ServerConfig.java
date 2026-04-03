package l12sq.server.config;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Properties;

public record ServerConfig(
        String advertisedHost,
        int authPort,
        int gamePort,
        Path accountsFile,
        Path charactersDirectory) {
    private static final Path DEFAULT_CONFIG_PATH = Paths.get("appsettings.properties");
    private static final List<Path> CONFIG_CANDIDATES = List.of(
            Paths.get("appsettings.properties"),
            Paths.get("..", "appsettings.properties"));

    public static ServerConfig defaults() {
        Path configPath = locateConfigPath();
        Path baseDir = configPath != null
                ? absoluteParent(configPath)
                : Paths.get("").toAbsolutePath().normalize();

        ServerConfig fallback = new ServerConfig(
                "127.0.0.1",
                1236,
                1238,
                defaultAccountsFile(baseDir),
                defaultCharactersDirectory(baseDir));
        return configPath == null ? fallback : load(configPath, fallback);
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

        Path configBaseDir = absoluteParent(configFile);

        return new ServerConfig(
                stringProperty(properties, "server.advertisedHost", fallback.advertisedHost()),
                intProperty(properties, "server.authPort", fallback.authPort()),
                intProperty(properties, "server.gamePort", fallback.gamePort()),
                resolvePath(configBaseDir, stringProperty(properties, "storage.accountsFile", fallback.accountsFile().toString())),
                resolvePath(configBaseDir, stringProperty(properties, "storage.charactersDirectory", fallback.charactersDirectory().toString())));
    }

    private static Path locateConfigPath() {
        for (Path candidate : CONFIG_CANDIDATES) {
            if (Files.exists(candidate)) {
                return candidate;
            }
        }
        return Files.exists(DEFAULT_CONFIG_PATH) ? DEFAULT_CONFIG_PATH : null;
    }

    private static Path absoluteParent(Path path) {
        Path absolute = path.toAbsolutePath().normalize();
        Path parent = absolute.getParent();
        return parent == null ? absolute : parent;
    }

    private static Path defaultAccountsFile(Path baseDir) {
        Path serverData = baseDir.resolve("server").resolve("data").resolve("accounts.json").normalize();
        if (Files.exists(serverData)) {
            return serverData;
        }
        return baseDir.resolve("data").resolve("accounts.json").normalize();
    }

    private static Path defaultCharactersDirectory(Path baseDir) {
        Path serverChars = baseDir.resolve("server").resolve("data").resolve("chars").normalize();
        if (Files.exists(serverChars)) {
            return serverChars;
        }
        return baseDir.resolve("data").resolve("chars").normalize();
    }

    private static Path resolvePath(Path baseDir, String value) {
        Path path = Paths.get(value);
        if (path.isAbsolute()) {
            return path.normalize();
        }
        return baseDir.resolve(path).normalize();
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
