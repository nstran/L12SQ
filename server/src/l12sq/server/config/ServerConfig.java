package l12sq.server.config;

import java.nio.file.Path;
import java.nio.file.Paths;

public record ServerConfig(
        String advertisedHost,
        int authPort,
        int gamePort,
        Path accountsFile) {

    public static ServerConfig defaults() {
        return new ServerConfig(
                "192.168.1.226",
                1236,
                1238,
                Paths.get("d:\\L12SQ\\server\\data\\accounts.json"));
    }
}
