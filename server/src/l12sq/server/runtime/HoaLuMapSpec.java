package l12sq.server.runtime;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

final class HoaLuMapSpec {
    private static final Path DEFAULT_PATH = ExternalAssetLoader.resolve("maps/hoalu/hoalu.json");
    private static final Pattern OBJECT_PATTERN = Pattern.compile("\\{(.*?)\\}", Pattern.DOTALL);
    private static final int DEFAULT_ROOM_TILE_WIDTH = 8;

    private final String mapName;
    private final int width;
    private final int height;
    private final int tileSize;
    private final RoomEntry roomEntry;
    private final List<WalkableRange> walkableRanges;
    private final List<SpawnCell> spawnCells;

    private HoaLuMapSpec(
            String mapName,
            int width,
            int height,
            int tileSize,
            RoomEntry roomEntry,
            List<WalkableRange> walkableRanges,
            List<SpawnCell> spawnCells) {
        this.mapName = mapName;
        this.width = width;
        this.height = height;
        this.tileSize = tileSize;
        this.roomEntry = roomEntry;
        this.walkableRanges = walkableRanges;
        this.spawnCells = spawnCells;
    }

    static HoaLuMapSpec load() {
        if (!Files.isRegularFile(DEFAULT_PATH)) {
            return defaultSpec();
        }
        try {
            String json = Files.readString(DEFAULT_PATH, StandardCharsets.UTF_8);
            return parse(json);
        } catch (IOException exception) {
            throw new IllegalStateException("Unable to read Hoa Lu map spec: " + DEFAULT_PATH, exception);
        }
    }

    static String defaultJson() {
        return """
                {
                  "mapName": "Hoa Lu",
                  "width": 10,
                  "height": 8,
                  "tileSize": 32,
                  "roomEntry": {
                    "roomId": 1,
                    "label": "Khu 1",
                    "centerX": 124,
                    "centerY": 132,
                    "width": 180,
                    "height": 28
                  },
                  "walkableRanges": [
                    { "row": 5, "startCol": 0, "endCol": 9 },
                    { "row": 6, "startCol": 0, "endCol": 9 },
                    { "row": 7, "startCol": 0, "endCol": 9 }
                  ],
                  "spawnCells": [
                    { "row": 5, "col": 3 },
                    { "row": 5, "col": 4 },
                    { "row": 5, "col": 5 },
                    { "row": 5, "col": 6 }
                  ]
                }
                """;
    }

    static Path defaultPath() {
        return DEFAULT_PATH;
    }

    String mapName() {
        return mapName;
    }

    int width() {
        return width;
    }

    int height() {
        return height;
    }

    int tileSize() {
        return tileSize;
    }

    RoomEntry roomEntry() {
        return roomEntry;
    }

    byte[] buildLogicLayer() {
        byte[] layer = new byte[width * height];
        for (WalkableRange range : walkableRanges) {
            for (int col = range.startCol(); col <= range.endCol(); col++) {
                setCell(layer, range.row(), col, 32);
            }
        }
        for (SpawnCell spawnCell : spawnCells) {
            setCell(layer, spawnCell.row(), spawnCell.col(), 2);
        }
        return layer;
    }

    int roomCount() {
        return Math.max(1, (width + roomTileWidth() - 1) / roomTileWidth());
    }

    int roomTileWidth() {
        return Math.min(width, DEFAULT_ROOM_TILE_WIDTH);
    }

    RoomView room(int roomId) {
        int normalizedRoomId = Math.max(1, Math.min(roomId, roomCount()));
        int startCol = (normalizedRoomId - 1) * roomTileWidth();
        int roomWidth = Math.min(roomTileWidth(), width - startCol);
        String label = "Khu " + normalizedRoomId;
        RoomEntry entry = new RoomEntry(
                normalizedRoomId,
                label,
                Math.max(80, (roomWidth * tileSize) / 2),
                Math.max(96, (height * tileSize) / 2),
                Math.max(180, roomWidth * tileSize / 2),
                32);
        return new RoomView(
                normalizedRoomId,
                label,
                startCol,
                roomWidth,
                height,
                tileSize,
                entry,
                buildLogicLayer(normalizedRoomId));
    }

    private byte[] buildLogicLayer(int roomId) {
        RoomView room = buildRoomShell(roomId);
        byte[] layer = new byte[room.width() * room.height()];
        int roomStartCol = room.startCol();
        int roomEndCol = roomStartCol + room.width() - 1;

        for (WalkableRange range : walkableRanges) {
            int localStart = Math.max(range.startCol(), roomStartCol);
            int localEnd = Math.min(range.endCol(), roomEndCol);
            if (localStart > localEnd) {
                continue;
            }
            for (int col = localStart; col <= localEnd; col++) {
                int localCol = col - roomStartCol;
                setCell(layer, room.height(), room.width(), range.row(), localCol, 32);
            }
        }

        boolean spawnPlaced = false;
        for (SpawnCell spawnCell : spawnCells) {
            if (spawnCell.col() < roomStartCol || spawnCell.col() > roomEndCol) {
                continue;
            }
            int localCol = spawnCell.col() - roomStartCol;
            setCell(layer, room.height(), room.width(), spawnCell.row(), localCol, 2);
            spawnPlaced = true;
        }

        if (!spawnPlaced) {
            int fallbackRow = Math.max(0, room.height() - 1);
            int fallbackCol = Math.min(Math.max(0, room.width() / 2), Math.max(0, room.width() - 1));
            setCell(layer, room.height(), room.width(), fallbackRow, fallbackCol, 2);
        }

        return layer;
    }

    private RoomView buildRoomShell(int roomId) {
        int normalizedRoomId = Math.max(1, Math.min(roomId, roomCount()));
        int startCol = (normalizedRoomId - 1) * roomTileWidth();
        int roomWidth = Math.min(roomTileWidth(), width - startCol);
        String label = "Khu " + normalizedRoomId;
        RoomEntry entry = new RoomEntry(
                normalizedRoomId,
                label,
                Math.max(80, (roomWidth * tileSize) / 2),
                Math.max(96, (height * tileSize) / 2),
                Math.max(180, roomWidth * tileSize / 2),
                32);
        return new RoomView(normalizedRoomId, label, startCol, roomWidth, height, tileSize, entry, new byte[0]);
    }

    private void setCell(byte[] layer, int row, int col, int value) {
        int index = row * width + col;
        if (index < 0 || index >= layer.length) {
            return;
        }
        layer[index] = (byte) (value & 0xFF);
    }

    private void setCell(byte[] layer, int rows, int cols, int row, int col, int value) {
        int index = row * cols + col;
        if (row < 0 || row >= rows || col < 0 || col >= cols || index < 0 || index >= layer.length) {
            return;
        }
        layer[index] = (byte) (value & 0xFF);
    }

    private static HoaLuMapSpec parse(String json) {
        String mapName = readString(json, "mapName", "Hoa Lu");
        int width = readInt(json, "width", 10);
        int height = readInt(json, "height", 8);
        int tileSize = readInt(json, "tileSize", 32);

        String roomSection = readSection(json, "roomEntry");
        RoomEntry roomEntry = new RoomEntry(
                readInt(roomSection, "roomId", 1),
                readString(roomSection, "label", "Khu 1"),
                readInt(roomSection, "centerX", 124),
                readInt(roomSection, "centerY", 132),
                readInt(roomSection, "width", 180),
                readInt(roomSection, "height", 28));

        List<WalkableRange> walkableRanges = new ArrayList<>();
        for (String object : readObjectArray(json, "walkableRanges")) {
            walkableRanges.add(new WalkableRange(
                    readInt(object, "row", 0),
                    readInt(object, "startCol", 0),
                    readInt(object, "endCol", 0)));
        }
        if (walkableRanges.isEmpty()) {
            return defaultSpec();
        }

        List<SpawnCell> spawnCells = new ArrayList<>();
        for (String object : readObjectArray(json, "spawnCells")) {
            spawnCells.add(new SpawnCell(
                    readInt(object, "row", 0),
                    readInt(object, "col", 0)));
        }

        return new HoaLuMapSpec(mapName, width, height, tileSize, roomEntry, walkableRanges, spawnCells);
    }

    private static HoaLuMapSpec defaultSpec() {
        return new HoaLuMapSpec(
                "Hoa Lu",
                10,
                8,
                32,
                new RoomEntry(1, "Khu 1", 124, 132, 180, 28),
                List.of(
                        new WalkableRange(5, 0, 9),
                        new WalkableRange(6, 0, 9),
                        new WalkableRange(7, 0, 9)),
                List.of(
                        new SpawnCell(5, 3),
                        new SpawnCell(5, 4),
                        new SpawnCell(5, 5),
                        new SpawnCell(5, 6)));
    }

    private static String readSection(String json, String field) {
        Matcher matcher = Pattern.compile("\"" + Pattern.quote(field) + "\"\\s*:\\s*\\{(.*?)\\}", Pattern.DOTALL).matcher(json);
        if (!matcher.find()) {
            return "";
        }
        return matcher.group(1);
    }

    private static List<String> readObjectArray(String json, String field) {
        Matcher matcher = Pattern.compile("\"" + Pattern.quote(field) + "\"\\s*:\\s*\\[(.*?)\\]", Pattern.DOTALL).matcher(json);
        if (!matcher.find()) {
            return List.of();
        }
        String body = matcher.group(1);
        List<String> objects = new ArrayList<>();
        Matcher objectMatcher = OBJECT_PATTERN.matcher(body);
        while (objectMatcher.find()) {
            objects.add(objectMatcher.group(1));
        }
        return objects;
    }

    private static int readInt(String source, String field, int fallback) {
        Matcher matcher = Pattern.compile("\"" + Pattern.quote(field) + "\"\\s*:\\s*(-?\\d+)").matcher(source);
        if (!matcher.find()) {
            return fallback;
        }
        return Integer.parseInt(matcher.group(1));
    }

    private static String readString(String source, String field, String fallback) {
        Matcher matcher = Pattern.compile("\"" + Pattern.quote(field) + "\"\\s*:\\s*\"([^\"]*)\"").matcher(source);
        if (!matcher.find()) {
            return fallback;
        }
        return matcher.group(1);
    }

    record RoomEntry(int roomId, String label, int centerX, int centerY, int width, int height) {
    }

    record WalkableRange(int row, int startCol, int endCol) {
    }

    record SpawnCell(int row, int col) {
    }

    record RoomView(
            int roomId,
            String label,
            int startCol,
            int width,
            int height,
            int tileSize,
            RoomEntry roomEntry,
            byte[] logicLayer) {
    }
}
