package l12sq.server.runtime;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import l12sq.server.net.TlvCodec;

final class WorldPackets {
    private static final int HOA_LU_MAP_WIDTH = 10;
    private static final int HOA_LU_MAP_HEIGHT = 8;

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
        byte[] groundLayer = repeatedByteArray(HOA_LU_MAP_WIDTH * HOA_LU_MAP_HEIGHT, 0);
        byte[] decorationLayer = repeatedByteArray(HOA_LU_MAP_WIDTH * HOA_LU_MAP_HEIGHT, 0);
        byte[] triggerLayer = buildHoaLuLogicLayer();

        TagPacketBuilder builder = new TagPacketBuilder();
        builder.byteTag(12, 1);
        builder.stringTag(20, mapName);
        builder.stringTag(26, "Hoa Lu");
        builder.intTag(41, 5120);
        builder.intTag(56, HOA_LU_MAP_WIDTH);
        builder.intTag(57, HOA_LU_MAP_HEIGHT);
        builder.intTag(58, 32);
        builder.intTag(59, 32);
        builder.rawTag(55, groundLayer);
        builder.rawTag(54, decorationLayer);
        builder.rawTag(61, triggerLayer);
        builder.intTag(60, InstallResourceCatalog.MAP_HOA_LU_TILESET_ID);
        builder.intTag(63, InstallResourceCatalog.MAP_HOA_LU_BACKGROUND_ID);
        builder.intTag(29, InstallResourceCatalog.MAP_HOA_LU_OVERLAY_ID);
        appendEntry(builder, 1, "Khu 1", 1, 124, 132, 180, 28, true, 0);
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

    static void sendSceneActors(DataOutputStream dos, String mapName, String username) throws IOException {
        TagPacketBuilder builder = new TagPacketBuilder();
        builder.stringTag(20, mapName);
        builder.byteTag(40, 3);
        appendSceneActor(builder, "npc_hoalu_guard", "Linh canh", 1, 0, 0, 1, 0);
        System.out.println("[GAME] Sending scene actors CMD 43 map=" + mapName + " viewer=" + username + " count=1");
        TlvCodec.sendPacket(dos, 43, builder.payload(), builder.count());
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

    private static void appendSceneActor(
            TagPacketBuilder builder,
            String actorId,
            String label,
            int actorKind,
            int actorVariant,
            int actorPower,
            int actorCopies,
            int actorPalette) {
        builder.stringTag(9, actorId);
        builder.stringTag(26, label);
        builder.intTag(27, actorKind);
        builder.byteTag(15, actorVariant);
        builder.intTag(129, actorPower);
        builder.intTag(106, actorCopies);
        builder.byteTag(107, actorPalette);
    }

    private static byte[] repeatedByteArray(int size, int value) {
        byte[] bytes = new byte[size];
        for (int i = 0; i < bytes.length; i++) {
            bytes[i] = (byte) (value & 0xFF);
        }
        return bytes;
    }

    private static byte[] buildHoaLuLogicLayer() {
        byte[] layer = repeatedByteArray(HOA_LU_MAP_WIDTH * HOA_LU_MAP_HEIGHT, 0);

        // Flat walkable floor across the lower part of the map.
        for (int row = 5; row < HOA_LU_MAP_HEIGHT; row++) {
            setFloorRange(layer, row, 0, HOA_LU_MAP_WIDTH - 1, 32);
        }

        // Spawn centered on the lower floor.
        markSpawnCell(layer, HOA_LU_MAP_WIDTH, 5, 3);
        markSpawnCell(layer, HOA_LU_MAP_WIDTH, 5, 4);
        markSpawnCell(layer, HOA_LU_MAP_WIDTH, 5, 5);
        markSpawnCell(layer, HOA_LU_MAP_WIDTH, 5, 6);
        return layer;
    }

    private static void setFloorRange(byte[] layer, int row, int startCol, int endCol, int value) {
        for (int col = startCol; col <= endCol; col++) {
            setCell(layer, HOA_LU_MAP_WIDTH, row, col, value);
        }
    }

    private static void markSpawnCell(byte[] layer, int mapWidth, int row, int col) {
        int index = row * mapWidth + col;
        if (index < 0 || index >= layer.length) {
            return;
        }
        layer[index] = 2;
    }

    private static void setCell(byte[] layer, int mapWidth, int row, int col, int value) {
        int index = row * mapWidth + col;
        if (index < 0 || index >= layer.length) {
            return;
        }
        layer[index] = (byte) (value & 0xFF);
    }
}
