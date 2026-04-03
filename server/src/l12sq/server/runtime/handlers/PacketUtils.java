package l12sq.server.runtime.handlers;

import java.nio.ByteBuffer;
import java.util.Map;

public final class PacketUtils {
    private PacketUtils() {}

    public static String firstNonBlank(String... values) {
        for (String value : values) {
            if (value != null && !value.trim().isEmpty()) {
                return value.trim();
            }
        }
        return "";
    }

    public static String safeUsername(String username) {
        return firstNonBlank(username, "player");
    }

    public static int tagInt(Map<Integer, byte[]> tags, int tagId, int defaultValue) {
        byte[] value = tags.get(tagId);
        if (value == null || value.length == 0) {
            return defaultValue;
        }
        if (value.length == 1) {
            return value[0];
        }
        if (value.length >= 4) {
            return ByteBuffer.wrap(value, 0, 4).getInt();
        }
        return defaultValue;
    }
}
