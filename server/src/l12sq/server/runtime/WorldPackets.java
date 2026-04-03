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
        appendEntry(builder, 0, "Hoa Lu", 0, 120, 220, 64, 40, true, 0);
        System.out.println("[GAME] Sending world map hotspots CMD 11 map=" + mapName + " count=1");
        TlvCodec.sendPacket(dos, 11, builder.payload(), builder.count());
    }

    static void sendMapRoomList(DataOutputStream dos, String mapName) throws IOException {
        TagPacketBuilder builder = new TagPacketBuilder();
        builder.byteTag(12, 0);
        builder.stringTag(20, mapName);
        appendEntry(builder, 1, "Khu 1", 0, 96, 112, 48, 32, true, 0);
        appendEntry(builder, 2, "Khu 2", 0, 144, 112, 48, 32, true, 0);
        System.out.println("[GAME] Sending room list CMD 11 map=" + mapName + " count=2");
        TlvCodec.sendPacket(dos, 11, builder.payload(), builder.count());
    }

    static void sendMapInfo(DataOutputStream dos, String mapName) throws IOException {
        byte[] groundLayer = repeatedByteArray(80, 0);
        byte[] decorationLayer = repeatedByteArray(80, 0);
        byte[] triggerLayer = repeatedByteArray(80, 0);

        TagPacketBuilder builder = new TagPacketBuilder();
        builder.byteTag(12, 1);
        builder.stringTag(20, mapName);
        builder.stringTag(26, "Hoa Lu");
        builder.intTag(41, 5120);
        builder.intTag(56, 10);
        builder.intTag(57, 8);
        builder.intTag(58, 32);
        builder.intTag(59, 32);
        builder.rawTag(55, groundLayer);
        builder.rawTag(54, decorationLayer);
        builder.rawTag(61, triggerLayer);
        builder.intTag(60, InstallResourceCatalog.MAP_HOA_LU_TILESET_ID);
        builder.intTag(63, InstallResourceCatalog.MAP_HOA_LU_BACKGROUND_ID);
        builder.intTag(29, InstallResourceCatalog.MAP_HOA_LU_OVERLAY_ID);
        appendEntry(builder, 1, "Khu 1", 1, 96, 112, 48, 32, true, 0);
        builder.intTag(6, 0);
        builder.intTag(6, 0);
        System.out.println("[GAME] Sending map info CMD 11 map=" + mapName + " resources=3 rooms=1");
        TlvCodec.sendPacket(dos, 11, builder.payload(), builder.count());
    }

    static void sendMapSelectionAck(DataOutputStream dos, String mapName, int roomOrMarkerId, int selectionType) throws IOException {
        TagPacketBuilder builder = new TagPacketBuilder();
        builder.stringTag(20, mapName);
        builder.intTag(21, roomOrMarkerId);
        builder.byteTag(22, selectionType);
        System.out.println("[GAME] Sending selection ack CMD 13 map=" + mapName
                + " roomOrMarkerId=" + roomOrMarkerId
                + " selectionType=" + selectionType);
        TlvCodec.sendPacket(dos, 13, builder.payload(), builder.count());
    }

    private static void appendEntry(
            TagPacketBuilder builder,
            int id,
            String label,
            int markerType,
            int centerX,
            int centerY,
            int width,
            int height,
            boolean enabled,
            int iconId) {
        builder.intTag(21, id);
        builder.stringTag(26, label);
        builder.byteTag(22, markerType);
        builder.intTag(102, centerX);
        builder.intTag(103, centerY);
        builder.intTag(104, width);
        builder.intTag(105, height);
        builder.byteTag(101, enabled ? 1 : 0);
        builder.intTag(4, iconId);
    }

    private static byte[] repeatedByteArray(int size, int value) {
        byte[] bytes = new byte[size];
        for (int i = 0; i < bytes.length; i++) {
            bytes[i] = (byte) (value & 0xFF);
        }
        return bytes;
    }
}
