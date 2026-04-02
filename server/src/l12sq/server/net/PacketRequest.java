package l12sq.server.net;

import java.util.Map;

public record PacketRequest(
        int subCount,
        int payloadLength,
        int command,
        byte[] payload,
        Map<Integer, byte[]> tags) {
}
