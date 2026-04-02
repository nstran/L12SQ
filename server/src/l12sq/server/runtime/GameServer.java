package l12sq.server.runtime;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Base64;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import l12sq.server.auth.AuthService;
import l12sq.server.auth.CaptchaFactory;
import l12sq.server.config.ServerConfig;
import l12sq.server.net.PacketRequest;
import l12sq.server.net.TlvCodec;
import l12sq.server.storage.AccountStore;

public final class GameServer {
    private static final int INSTALL_PACKAGE_VERSION = 3;
    private static final int[] METADATA_FRAME_COUNTS = {2, 6, 4, 4, 4, 4, 3};
    private static final byte[] PLACEHOLDER_PNG = Base64.getDecoder().decode(
            "iVBORw0KGgoAAAANSUhEUgAAAAwAAAAMCAYAAABWdVznAAAAAXNSR0IArs4c6QAAAARnQU1BAACxjwv8YQUAAAAJcEhZcwAADsMAAA7DAcdvqGQAAAAYSURBVChTY/h/4PR/UvCoBmLwCNRw+j8Awcz9IBccOeIAAAAASUVORK5CYII=");
    private static final Map<Integer, byte[]> INSTALL_RESOURCES = createInstallResources();
    private static final List<Integer> STARTUP_INSTALL_RESOURCE_IDS = Arrays.asList(
            30099,
            79899,
            79999,
            89999,
            99000, 99001, 99002, 99003, 99004, 99005, 99006,
            700000, 700001, 700002, 700003, 700004, 700005, 700006,
            700010, 700011, 700012, 700013, 700014, 700015, 700016,
            700020, 700021, 700022, 700023, 700024, 700025, 700026
    );
    private static final List<Integer> CREATE_CHAR_RESOURCE_IDS = Arrays.asList(
            79899,
            79999,
            89999,
            99000, 99001, 99002, 99003, 99004, 99005, 99006,
            700000, 700001, 700002, 700003, 700004, 700005, 700006,
            700010, 700011, 700012, 700013, 700014, 700015, 700016,
            700020, 700021, 700022, 700023, 700024, 700025, 700026
    );

    private final ServerConfig config;
    private final AuthService authService;

    public GameServer(ServerConfig config, AccountStore accountStore) {
        this.config = config;
        this.authService = new AuthService(accountStore);
    }

    public void start() {
        System.out.println("=============================================");
        System.out.println("  L12SQ ONLINE SERVER");
        System.out.println("  Host      : " + config.advertisedHost());
        System.out.println("  Auth Port : " + config.authPort());
        System.out.println("  Game Port : " + config.gamePort());
        System.out.println("  Accounts  : " + authService.accountCount());
        System.out.println("  Scope     : register/login/auth");
        System.out.println("=============================================");

        new Thread(() -> listen(config.authPort(), "AUTH"), "auth-listener").start();
        new Thread(() -> listen(config.gamePort(), "GAME"), "game-listener").start();
    }

    private void listen(int port, String channel) {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("[" + channel + "] Listening on port " + port + "...");
            while (true) {
                Socket socket = serverSocket.accept();
                socket.setTcpNoDelay(true);
                new Thread(() -> handleClient(socket, channel), channel + "-client").start();
            }
        } catch (IOException exception) {
            System.err.println("[" + channel + "] FATAL: " + exception.getMessage());
        }
    }

    private void handleClient(Socket socket, String channel) {
        SessionContext session = new SessionContext(channel);
        try (DataInputStream dis = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
             DataOutputStream dos = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()))) {

            System.out.println("[" + channel + "] Client connected from " + socket.getInetAddress());
            while (true) {
                PacketRequest request = "AUTH".equals(channel)
                        ? TlvCodec.readAuthRequest(dis)
                        : TlvCodec.readGameRequest(dis);
                logRequest(channel, request);
                route(channel, request, dos, session);
            }
        } catch (EOFException eof) {
            System.out.println("[" + channel + "] Client disconnected.");
        } catch (IOException exception) {
            System.out.println("[" + channel + "] Error: " + exception.getMessage());
        }
    }

    private void route(String channel, PacketRequest request, DataOutputStream dos, SessionContext session) throws IOException {
        switch (request.command()) {
            case 5 -> handleVersionCheck(channel, dos, session);
            case 2, 3 -> handleSalt(dos);
            case 130 -> handleCaptcha(dos);
            case 131 -> handleRegistration(request.tags(), dos);
            case 4 -> handleAuth(channel, request.tags(), dos, session);
            case 6 -> handleInstallResourceRequest(request.tags(), dos, session);
            case 8 -> handleCreateCharacterRequest(request, dos, session);
            case 9 -> handleProfileSync(request.tags(), dos, session);
            case 30 -> handleNoCharacterBootstrap(request.tags(), dos, session);
            case 42 -> handleStartButton(request.tags(), dos, session);
            case 1 -> TlvCodec.sendEmpty(dos, 1);
            default -> System.out.println("[" + channel + "] [WARN] Unhandled CMD " + request.command());
        }
    }

    private void handleVersionCheck(String channel, DataOutputStream dos, SessionContext session) throws IOException {
        if (!"GAME".equals(channel) || session.installManifestSent) {
            TlvCodec.sendEmpty(dos, 5);
            return;
        }

        TagPacketBuilder builder = new TagPacketBuilder();
        builder.byteTag(12, 0);
        builder.stringTag(131, "");
        builder.intTag(41, totalInstallBytes(STARTUP_INSTALL_RESOURCE_IDS));
        builder.intTag(13, INSTALL_PACKAGE_VERSION);
        for (int resourceId : STARTUP_INSTALL_RESOURCE_IDS) {
            builder.intTag(4, resourceId);
        }

        session.installManifestSent = true;
        System.out.println("[GAME] Sending install manifest via CMD 5 resources=" + STARTUP_INSTALL_RESOURCE_IDS.size());
        TlvCodec.sendPacket(dos, 5, builder.payload(), builder.count());
    }

    private void handleSalt(DataOutputStream dos) throws IOException {
        TlvCodec.sendSingleTag(dos, 3, TlvCodec.makeTag(2, TlvCodec.randomSalt(16)));
    }

    private void handleCaptcha(DataOutputStream dos) throws IOException {
        ByteArrayOutputStream payload = new ByteArrayOutputStream();
        TlvCodec.writeTag(payload, 176, CaptchaFactory.renderPng("1234"));
        TlvCodec.writeTag(payload, 2, TlvCodec.randomSalt(16));
        TlvCodec.sendPacket(dos, 130, payload.toByteArray(), 2);
    }

    private void handleRegistration(Map<Integer, byte[]> tags, DataOutputStream dos) throws IOException {
        String username = TlvCodec.tagString(tags, 9);
        String passwordHex = TlvCodec.tagHex(tags, 10);
        AuthService.RegistrationResult result = authService.register(username, passwordHex);

        ByteArrayOutputStream payload = new ByteArrayOutputStream();
        TlvCodec.writeTag(payload, 1, result.message());
        TlvCodec.sendPacket(dos, 131, payload.toByteArray(), 1);
    }

    private void handleAuth(String channel, Map<Integer, byte[]> tags, DataOutputStream dos, SessionContext session) throws IOException {
        String username = TlvCodec.tagString(tags, 9);
        String passwordHex = TlvCodec.tagHex(tags, 10);

        if (authService.canLogin(username, passwordHex)) {
            session.username = safeUsername(username);
            session.authenticated = true;

            if ("AUTH".equals(channel)) {
                TlvCodec.sendEmpty(dos, 1);
                TlvCodec.sendSingleTag(dos, 2, TlvCodec.makeTag(3, config.advertisedHost()));
                return;
            }

            TlvCodec.sendEmpty(dos, 4);
            return;
        }

        ByteArrayOutputStream payload = new ByteArrayOutputStream();
        TlvCodec.writeTag(payload, 1, "Dang nhap that bai.");
        TlvCodec.sendPacket(dos, 0, payload.toByteArray(), 1);
    }

    private void handleProfileSync(Map<Integer, byte[]> tags, DataOutputStream dos, SessionContext session) throws IOException {
        if (!session.authenticated) {
            ByteArrayOutputStream payload = new ByteArrayOutputStream();
            TlvCodec.writeTag(payload, 1, "Ban chua dang nhap vao game.");
            TlvCodec.sendPacket(dos, 0, payload.toByteArray(), 1);
            return;
        }

        String username = firstNonBlank(TlvCodec.tagString(tags, 9), session.username, "player");
        session.username = username;

        TagPacketBuilder profile = buildStartProfile(username);
        System.out.println("[GAME] Sending START profile for " + username);
        TlvCodec.sendPacket(dos, 9, profile.payload(), profile.count());
        session.awaitingCharacterCreation = true;
        session.createCharacterOptionsSent = false;
        session.preloadedResources.clear();
        session.pendingResourceAnnouncements.clear();
        session.activeInstallResourceId = null;
        sendCreateCharacterOptions(dos, session.username);
        session.createCharacterOptionsSent = true;
    }

    private void handleNoCharacterBootstrap(Map<Integer, byte[]> tags, DataOutputStream dos, SessionContext session) throws IOException {
        if (!session.authenticated) {
            TlvCodec.sendEmpty(dos, 30);
            return;
        }
        if (session.awaitingCharacterCreation) {
            int classFlag = tags.containsKey(15) && tags.get(15).length > 0 ? tags.get(15)[0] & 0xFF : -1;
            System.out.println("[GAME] Ignore bootstrap CMD 30 while waiting create-char for "
                    + session.username + " classFlag=" + classFlag);
            return;
        }

        int classFlag = tags.containsKey(15) && tags.get(15).length > 0 ? tags.get(15)[0] & 0xFF : -1;
        System.out.println("[GAME] Ack no-char bootstrap for " + session.username + " classFlag=" + classFlag);
        TlvCodec.sendEmpty(dos, 30);
    }

    private void handleStartButton(Map<Integer, byte[]> tags, DataOutputStream dos, SessionContext session) throws IOException {
        if (!session.authenticated) {
            TlvCodec.sendEmpty(dos, 42);
            return;
        }
        if (session.awaitingCharacterCreation) {
            System.out.println("[GAME] Ignore start button CMD 42 while waiting create-char for " + session.username);
            return;
        }

        System.out.println("[GAME] Ack start button for " + session.username + " tags=" + tags.size());
        TlvCodec.sendEmpty(dos, 42);
    }

    private void handleCreateCharacterRequest(PacketRequest request, DataOutputStream dos, SessionContext session) throws IOException {
        if (!session.authenticated) {
            TlvCodec.sendEmpty(dos, 8);
            return;
        }

        System.out.println("[GAME] Received create-character request for " + session.username
                + " payload=" + request.payloadLength() + " tags=" + request.tags().size());
        session.awaitingCharacterCreation = false;
        TlvCodec.sendEmpty(dos, 8);
    }

    private void handleInstallResourceRequest(Map<Integer, byte[]> tags, DataOutputStream dos, SessionContext session) throws IOException {
        int resourceId = tagInt(tags, 4, -1);
        int chunkIndex = tagInt(tags, 7, -1);
        if (resourceId < 0) {
            System.out.println("[GAME] [WARN] CMD 6 missing resource id");
            return;
        }

        byte[] resource = INSTALL_RESOURCES.get(resourceId);
        if (resource == null) {
            System.out.println("[GAME] [WARN] Unknown install resource " + resourceId + " chunk=" + chunkIndex);
            return;
        }

        if (chunkIndex < 0) {
            sendInstallResourceAnnouncement(dos, resourceId, resource.length, 1);
            return;
        }

        sendInstallResourceChunk(dos, resourceId, chunkIndex, resource);
        if (chunkIndex == 0) {
            session.preloadedResources.add(resourceId);
            session.pendingResourceAnnouncements.remove(resourceId);
            if (session.activeInstallResourceId != null && session.activeInstallResourceId == resourceId) {
                session.activeInstallResourceId = null;
            }
            announceNextCreateCharacterResource(dos, session);
        }
    }

    private void maybeSendCreateCharacterOptions(DataOutputStream dos, SessionContext session) throws IOException {
        if (!session.awaitingCharacterCreation || session.createCharacterOptionsSent) {
            return;
        }
        if (!session.preloadedResources.containsAll(CREATE_CHAR_RESOURCE_IDS)) {
            return;
        }

        sendCreateCharacterOptions(dos, session.username);
        session.createCharacterOptionsSent = true;
    }

    private void announceNextCreateCharacterResource(DataOutputStream dos, SessionContext session) throws IOException {
        if (session.activeInstallResourceId != null) {
            return;
        }

        for (int resourceId : session.pendingResourceAnnouncements) {
            if (session.preloadedResources.contains(resourceId)) {
                continue;
            }
            byte[] resource = INSTALL_RESOURCES.get(resourceId);
            if (resource == null) {
                continue;
            }
            session.activeInstallResourceId = resourceId;
            sendInstallResourceAnnouncement(dos, resourceId, resource.length, 1);
            return;
        }

        maybeSendCreateCharacterOptions(dos, session);
    }

    private void sendCreateCharacterOptions(DataOutputStream dos, String username) throws IOException {
        TagPacketBuilder options = new TagPacketBuilder();
        appendCmd8AppearanceEntry(options, 79800, 0, 0, "Nam Mat 1", 79899, "Nam Mat 1");
        appendCmd8AppearanceEntry(options, 79900, 1, 0, "Nam Toc 1", 79999, "Nam Toc 1");
        appendCmd8AppearanceEntry(options, 89900, 2, 0, "Nam Da 1", 89999, "Nam Da 1");
        appendCmd8AppearanceEntry(options, 79900, 0, 1, "Nu Mat 1", 79999, "Nu Mat 1");
        appendCmd8AppearanceEntry(options, 79900, 1, 1, "Nu Toc 1", 79999, "Nu Toc 1");
        appendCmd8AppearanceEntry(options, 89900, 2, 1, "Nu Da 1", 89999, "Nu Da 1");

        System.out.println("[GAME] Sending create-character options (CMD 8) for " + username);
        TlvCodec.sendPacket(dos, 8, options.payload(), options.count());
    }

    private static TagPacketBuilder buildStartProfile(String username) {
        TagPacketBuilder profile = new TagPacketBuilder();
        profile.byteTag(134, 1);
        profile.stringTag(9, username);
        profile.stringTag(26, "Tan Thu");
        profile.byteTag(15, 1);
        profile.byteTag(16, 0);
        profile.intTag(27, 1);
        profile.intTag(17, 1000);
        profile.intTag(47, 1000);
        profile.intTag(18, 500);
        profile.intTag(48, 500);
        profile.intTag(118, 10);
        profile.intTag(119, 10);
        profile.intTag(120, 10);
        profile.intTag(121, 10);
        profile.intTag(196, 0);
        profile.intTag(197, 0);
        profile.intTag(198, 0);
        profile.intTag(199, 0);
        profile.intTag(116, 0);
        profile.intTag(115, 100);
        profile.intTag(42, 50);
        profile.intTag(43, 30);
        profile.intTag(99, 1000);
        profile.intTag(53, 0);
        profile.intTag(76, 0);
        profile.intTag(73, 0);
        profile.intTag(74, 0);
        profile.intTag(108, 5);
        profile.intTag(109, 5);
        profile.stringTag(151, "");
        profile.intTag(160, 0);
        profile.byteTag(165, 0);
        profile.byteTag(166, 0);
        profile.longTag(132, 0L);
        appendCmd9AppearanceEntry(profile, 79800, 0, 79899, 79899);
        appendCmd9AppearanceEntry(profile, 79900, 1, 79999, 79999);
        appendCmd9AppearanceEntry(profile, 89900, 2, 89999, 89999);
        return profile;
    }

    private static TagPacketBuilder buildNoCharacterProfile(String username) {
        TagPacketBuilder profile = new TagPacketBuilder();

        profile.intTag(23, 31);
        profile.stringTag(9, username);
        profile.byteTag(15, 0);
        profile.byteTag(16, 0);

        // Seed a default preview for the no-character branch parsed by kw.b(ks).
        appendCmd9AppearanceEntry(profile, 79800, 0, 79899, 79899);
        appendCmd9AppearanceEntry(profile, 79900, 1, 79999, 79999);
        appendCmd9AppearanceEntry(profile, 89900, 2, 89999, 89999);

        profile.intTag(27, 1);
        profile.intTag(118, 10);
        profile.intTag(119, 10);
        profile.intTag(120, 10);
        profile.intTag(121, 10);
        profile.intTag(196, 0);
        profile.intTag(197, 0);
        profile.intTag(198, 0);
        profile.intTag(199, 0);
        profile.intTag(116, 0);
        profile.intTag(115, 100);

        profile.intTag(17, 1000);
        profile.intTag(47, 1000);
        profile.intTag(42, 50);
        profile.intTag(73, 0);
        profile.intTag(74, 0);
        profile.intTag(43, 30);
        profile.intTag(99, 1000);

        profile.intTag(53, 0);
        profile.intTag(76, 0);
        profile.intTag(108, 5);
        profile.intTag(109, 5);
        profile.intTag(160, 0);
        profile.stringTag(151, "");

        profile.byteTag(165, 0);
        profile.byteTag(166, 0);
        return profile;
    }

    private static void appendCmd9AppearanceEntry(TagPacketBuilder builder, int optionId, int category, int spriteId, int alternateSpriteId) {
        builder.rawTag(90, intBytes(optionId));
        builder.byteTag(91, category);
        builder.intTag(93, spriteId);
        builder.rawTag(95, intArrayBytes(spriteId));
        builder.rawTag(96, intBytes(alternateSpriteId));
        builder.rawTag(98, intArrayBytes(alternateSpriteId));
    }

    private static void appendCmd8AppearanceEntry(
            TagPacketBuilder builder,
            int optionId,
            int category,
            int gender,
            String name,
            int spriteId,
            String spriteName
    ) {
        builder.rawTag(90, intBytes(optionId));
        builder.byteTag(91, category);
        builder.stringTag(92, name);
        builder.byteTag(16, gender);
        builder.intTag(93, spriteId);
        builder.stringTag(94, spriteName);
        builder.rawTag(95, intArrayBytes(spriteId));
        builder.rawTag(96, intBytes(spriteId));
        builder.stringTag(97, spriteName);
        builder.rawTag(98, intArrayBytes(spriteId));
    }

    private static void sendInstallResourceAnnouncement(DataOutputStream dos, int resourceId, int totalBytes, int totalChunks) throws IOException {
        TagPacketBuilder builder = new TagPacketBuilder();
        builder.intTag(4, resourceId);
        builder.intTag(6, totalBytes);
        builder.intTag(5, totalChunks);
        System.out.println("[GAME] >> CMD 6 announce resource=" + resourceId + " bytes=" + totalBytes + " chunks=" + totalChunks);
        TlvCodec.sendPacket(dos, 6, builder.payload(), builder.count());
    }

    private static void sendInstallResourceChunk(DataOutputStream dos, int resourceId, int chunkIndex, byte[] chunkBytes) throws IOException {
        TagPacketBuilder builder = new TagPacketBuilder();
        builder.intTag(4, resourceId);
        builder.intTag(7, chunkIndex);
        builder.rawTag(8, chunkBytes);
        System.out.println("[GAME] >> CMD 6 chunk resource=" + resourceId + " chunk=" + chunkIndex + " bytes=" + chunkBytes.length);
        TlvCodec.sendPacket(dos, 6, builder.payload(), builder.count());
    }

    private static String safeUsername(String username) {
        return firstNonBlank(username, "player");
    }

    private static String firstNonBlank(String... values) {
        for (String value : values) {
            if (value != null && !value.trim().isEmpty()) {
                return value.trim();
            }
        }
        return "";
    }

    private static int tagInt(Map<Integer, byte[]> tags, int tagId, int defaultValue) {
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

    private static int totalInstallBytes(List<Integer> resourceIds) {
        int total = 0;
        for (int resourceId : resourceIds) {
            byte[] resource = INSTALL_RESOURCES.get(resourceId);
            if (resource != null) {
                total += resource.length;
            }
        }
        return total;
    }

    private static void logRequest(String channel, PacketRequest request) {
        System.out.printf("[%s] << CMD %d len=%d tags=%d%n",
                channel,
                request.command(),
                request.payloadLength(),
                request.tags().size());
        for (Map.Entry<Integer, byte[]> entry : request.tags().entrySet()) {
            int size = entry.getValue() == null ? 0 : entry.getValue().length;
            System.out.printf("       Tag %d (%db)%n", entry.getKey(), size);
        }
    }

    private static byte[] intBytes(int value) {
        return ByteBuffer.allocate(4).putInt(value).array();
    }

    private static byte[] longBytes(long value) {
        return ByteBuffer.allocate(8).putLong(value).array();
    }

    private static byte[] intArrayBytes(int... values) {
        ByteBuffer buffer = ByteBuffer.allocate(values.length * 4);
        for (int value : values) {
            buffer.putInt(value);
        }
        return buffer.array();
    }

    private static final class SessionContext {
        private final String channel;
        private boolean authenticated;
        private boolean awaitingCharacterCreation;
        private boolean createCharacterOptionsSent;
        private String username;
        private final Set<Integer> pendingResourceAnnouncements = new LinkedHashSet<>();
        private final Set<Integer> preloadedResources = new LinkedHashSet<>();
        private Integer activeInstallResourceId;
        private boolean installManifestSent;

        private SessionContext(String channel) {
            this.channel = channel;
        }
    }

    private static final class TagPacketBuilder {
        private final ByteArrayOutputStream payload = new ByteArrayOutputStream();
        private int count;

        private void stringTag(int tagId, String value) {
            TlvCodec.writeTag(payload, tagId, value);
            count++;
        }

        private void byteTag(int tagId, int value) {
            TlvCodec.writeTag(payload, tagId, new byte[]{(byte) (value & 0xFF)});
            count++;
        }

        private void intTag(int tagId, int value) {
            TlvCodec.writeTag(payload, tagId, intBytes(value));
            count++;
        }

        private void longTag(int tagId, long value) {
            TlvCodec.writeTag(payload, tagId, longBytes(value));
            count++;
        }

        private void rawTag(int tagId, byte[] value) {
            TlvCodec.writeTag(payload, tagId, value);
            count++;
        }

        private byte[] payload() {
            return payload.toByteArray();
        }

        private int count() {
            return count;
        }
    }

    private static Map<Integer, byte[]> createInstallResources() {
        Map<Integer, byte[]> resources = new LinkedHashMap<>();
        resources.put(30099, PLACEHOLDER_PNG);
        resources.put(79899, metadataBytes(700000));
        resources.put(79999, metadataBytes(700010));
        resources.put(89999, metadataBytes(700020));
        addPlaceholderRange(resources, 99000, 7);
        addPlaceholderRange(resources, 700000, 7);
        addPlaceholderRange(resources, 700010, 7);
        addPlaceholderRange(resources, 700020, 7);
        return resources;
    }

    private static byte[] framedImageBytes(int frameCount) {
        ByteBuffer buffer = ByteBuffer.allocate(4 + PLACEHOLDER_PNG.length);
        buffer.putInt(frameCount);
        buffer.put(PLACEHOLDER_PNG);
        return buffer.array();
    }

    private static byte[] metadataBytes(int imageBaseId) {
        try {
            ByteArrayOutputStream output = new ByteArrayOutputStream();
            DataOutputStream dos = new DataOutputStream(output);
            dos.writeByte(1);
            dos.writeInt(imageBaseId);
            dos.writeByte(METADATA_FRAME_COUNTS.length);
            for (int groupId = 0; groupId < METADATA_FRAME_COUNTS.length; groupId++) {
                int frameCount = METADATA_FRAME_COUNTS[groupId];
                dos.writeByte(groupId);
                dos.writeByte(frameCount);
                dos.writeByte(frameCount);
                for (int frameIndex = 0; frameIndex < frameCount; frameIndex++) {
                    dos.writeByte(frameIndex);
                    dos.writeShort(0);
                    dos.writeShort(0);
                }
            }
            dos.flush();
            return output.toByteArray();
        } catch (IOException exception) {
            throw new IllegalStateException("Unable to build create-character metadata", exception);
        }
    }

    private static void addPlaceholderRange(Map<Integer, byte[]> resources, int startId, int count) {
        for (int offset = 0; offset < count; offset++) {
            resources.put(startId + offset, PLACEHOLDER_PNG);
        }
    }
}
