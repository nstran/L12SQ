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
        HoaLuMapSpec mapSpec = HoaLuMapSpec.load();
        TagPacketBuilder builder = new TagPacketBuilder();
        builder.byteTag(12, 0);
        builder.stringTag(20, mapName);
        for (int roomId = 1; roomId <= mapSpec.roomCount(); roomId++) {
            HoaLuMapSpec.RoomEntry roomEntry = mapSpec.room(roomId).roomEntry();
            appendEntry(builder, roomEntry.roomId(), roomEntry.label(), 0, roomEntry.centerX(), roomEntry.centerY(), roomEntry.width(), roomEntry.height(), true, 0);
        }
        System.out.println("[GAME] Sending room list CMD 11 map=" + mapName + " count=" + mapSpec.roomCount());
        TlvCodec.sendPacket(dos, 11, builder.payload(), builder.count());
    }

    static void sendMapInfo(DataOutputStream dos, String mapName, int roomId) throws IOException {
        HoaLuMapSpec mapSpec = HoaLuMapSpec.load();
        HoaLuMapSpec.RoomView room = mapSpec.room(roomId);
        HoaLuMapSpec.RoomEntry roomEntry = room.roomEntry();
        byte[] groundLayer = repeatedByteArray(room.width() * room.height(), 0);
        byte[] decorationLayer = repeatedByteArray(room.width() * room.height(), 0);
        byte[] triggerLayer = room.logicLayer();

        TagPacketBuilder builder = new TagPacketBuilder();
        builder.byteTag(12, 1);
        builder.stringTag(20, mapSpec.mapName());
        builder.stringTag(26, room.label());
        builder.intTag(41, 5120);
        builder.intTag(56, room.width());
        builder.intTag(57, room.height());
        builder.intTag(58, room.tileSize());
        builder.intTag(59, room.tileSize());
        builder.rawTag(55, groundLayer);
        builder.rawTag(54, decorationLayer);
        builder.rawTag(61, triggerLayer);
        builder.intTag(60, InstallResourceCatalog.MAP_HOA_LU_TILESET_ID);
        builder.intTag(63, InstallResourceCatalog.roomBackgroundId(room.roomId()));
        builder.intTag(29, InstallResourceCatalog.roomOverlayId(room.roomId()));
        appendEntry(
                builder,
                roomEntry.roomId(),
                roomEntry.label(),
                1,
                roomEntry.centerX(),
                roomEntry.centerY(),
                roomEntry.width(),
                roomEntry.height(),
                true,
                0);
        builder.intTag(6, 0);
        builder.intTag(6, 0);
        System.out.println("[GAME] Sending map info CMD 11 map=" + mapName + " room=" + room.roomId() + " resources=3 rooms=1");
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
        System.out.println("[GAME] Sending scene actors CMD 43 map=" + mapName + " viewer=" + username + " count=0");
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
            int actorPalette,
            int x,
            int y) {
        builder.stringTag(9, actorId);
        builder.stringTag(26, label);
        builder.intTag(27, actorKind);
        builder.byteTag(15, actorVariant);
        builder.intTag(129, actorPower);
        builder.intTag(106, actorCopies);
        builder.byteTag(107, actorPalette);
        builder.intTag(102, x);
        builder.intTag(103, y);
    }

    private static byte[] repeatedByteArray(int size, int value) {
        byte[] bytes = new byte[size];
        for (int i = 0; i < bytes.length; i++) {
            bytes[i] = (byte) (value & 0xFF);
        }
        return bytes;
    }

}
