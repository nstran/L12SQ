package l12sq.server.runtime;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import l12sq.server.net.TlvCodec;

final class WorldPackets {
    private WorldPackets() {
    }

    static void sendMapJoin(DataOutputStream dos, String username, String mapName, int roomId) throws IOException {
        TagPacketBuilder builder = new TagPacketBuilder();
        builder.stringTag(9, username);
        builder.stringTag(20, mapName);
        builder.intTag(21, roomId);
        System.out.println("[GAME] Sending map join CMD 29 user=" + username + " map=" + mapName + " room=" + roomId);
        TlvCodec.sendPacket(dos, 29, builder.payload(), builder.count());
    }

    static void sendWorldMapHotspots(DataOutputStream dos, String mapName) throws IOException {
        TagPacketBuilder builder = new TagPacketBuilder();
        builder.byteTag(12, 0);
        builder.stringTag(20, mapName);
        builder.rawTag(21, worldMapMarkerPayload(1, "Hoa Lu", 0, 120, 220, 64, 40, true, 0));
        System.out.println("[GAME] Sending world map hotspots CMD 11 map=" + mapName + " count=1");
        TlvCodec.sendPacket(dos, 11, builder.payload(), builder.count());
    }

    private static byte[] worldMapMarkerPayload(
            int id,
            String label,
            int markerType,
            int centerX,
            int centerY,
            int width,
            int height,
            boolean enabled,
            int iconId) {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        output.writeBytes(TagPacketBuilder.intBytes(id));
        TlvCodec.writeTag(output, 26, label);
        TlvCodec.writeTag(output, 22, new byte[]{(byte) (markerType & 0xFF)});
        TlvCodec.writeTag(output, 102, TagPacketBuilder.intBytes(centerX));
        TlvCodec.writeTag(output, 103, TagPacketBuilder.intBytes(centerY));
        TlvCodec.writeTag(output, 104, TagPacketBuilder.intBytes(width));
        TlvCodec.writeTag(output, 105, TagPacketBuilder.intBytes(height));
        TlvCodec.writeTag(output, 101, new byte[]{(byte) (enabled ? 1 : 0)});
        TlvCodec.writeTag(output, 4, TagPacketBuilder.intBytes(iconId));
        return output.toByteArray();
    }
}
