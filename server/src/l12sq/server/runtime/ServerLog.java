package l12sq.server.runtime;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public final class ServerLog {
    private static final Path LOG_FILE = Paths.get("server", "runtime", "server-debug.log");
    private static final DateTimeFormatter TS = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");

    private ServerLog() {
    }

    public static synchronized void info(String message) {
        String line = "[" + LocalDateTime.now().format(TS) + "] " + message;
        System.out.println(message);
        try {
            Files.createDirectories(LOG_FILE.getParent());
            Files.writeString(
                    LOG_FILE,
                    line + System.lineSeparator(),
                    StandardCharsets.UTF_8,
                    Files.exists(LOG_FILE)
                            ? new java.nio.file.OpenOption[]{
                            java.nio.file.StandardOpenOption.CREATE,
                            java.nio.file.StandardOpenOption.APPEND
                    }
                            : new java.nio.file.OpenOption[]{
                            java.nio.file.StandardOpenOption.CREATE
                    });
        } catch (IOException exception) {
            System.err.println("[LOG] " + exception.getMessage());
        }
    }
}
