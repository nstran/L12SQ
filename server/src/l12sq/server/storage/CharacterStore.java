package l12sq.server.storage;

import java.io.IOException;

public interface CharacterStore {
    boolean exists(String username);

    CharacterData load(String username) throws IOException;

    void save(CharacterData characterData) throws IOException;

    record CharacterData(
            String username,
            int gender,
            int element,
            int hairOptionId,
            int hairColorId,
            int faceOptionId,
            int skinOptionId,
            int skinColorId) {
    }
}
