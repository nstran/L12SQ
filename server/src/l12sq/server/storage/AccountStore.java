package l12sq.server.storage;

import java.io.IOException;

public interface AccountStore {
    int count();

    boolean exists(String username);

    String passwordHashOf(String username);

    boolean register(String username, String passwordHash) throws IOException;
}
