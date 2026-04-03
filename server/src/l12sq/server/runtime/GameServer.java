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
import java.util.Map;
import l12sq.server.auth.AuthService;
import l12sq.server.auth.CaptchaFactory;
import l12sq.server.config.ServerConfig;
import l12sq.server.net.PacketRequest;
import l12sq.server.net.TlvCodec;
import l12sq.server.storage.AccountStore;
import l12sq.server.storage.CharacterStore;

public final class GameServer {
    private final ServerConfig config;
    private final AuthService authService;
    private final CharacterStore characterStore;

    public GameServer(ServerConfig config, AccountStore accountStore, CharacterStore characterStore) {
        this.config = config;
        this.authService = new AuthService(accountStore);
        this.characterStore = characterStore;
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
        GameSession session = new GameSession(channel);
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

    private void route(String channel, PacketRequest request, DataOutputStream dos, GameSession session) throws IOException {
        switch (request.command()) {
            case 5 -> handleVersionCheck(channel, dos, session);
            case 2, 3 -> handleSalt(dos);
            case 130 -> handleCaptcha(dos);
            case 131 -> handleRegistration(request.tags(), dos);
            case 4 -> handleAuth(channel, request.tags(), dos, session);
            case 6 -> handleInstallResourceRequest(request.tags(), dos, session);
            case 8 -> handleCreateCharacterRequest(request, dos, session);
            case 9 -> handleProfileSync(request.tags(), dos, session);
            case 11 -> handleWorldMapHotspotRequest(request.tags(), dos, session);
            case 29 -> handleMapJoinRequest(request.tags(), dos, session);
            case 30 -> handleNoCharacterBootstrap(request.tags(), dos, session);
            case 42 -> handleStartButton(request.tags(), dos, session);
            case 1 -> TlvCodec.sendEmpty(dos, 1);
            default -> System.out.println("[" + channel + "] [WARN] Unhandled CMD " + request.command());
        }
    }

    private void handleVersionCheck(String channel, DataOutputStream dos, GameSession session) throws IOException {
        if (!"GAME".equals(channel) || session.installManifestSent()) {
            TlvCodec.sendEmpty(dos, 5);
            return;
        }

        TagPacketBuilder builder = new TagPacketBuilder();
        builder.byteTag(12, 0);
        builder.stringTag(131, "");
        builder.intTag(41, InstallResourceCatalog.totalInstallBytes(InstallResourceCatalog.STARTUP_INSTALL_RESOURCE_IDS));
        builder.intTag(13, InstallResourceCatalog.INSTALL_PACKAGE_VERSION);
        for (int resourceId : InstallResourceCatalog.STARTUP_INSTALL_RESOURCE_IDS) {
            builder.intTag(4, resourceId);
        }

        session.setInstallManifestSent(true);
        System.out.println("[GAME] Sending install manifest via CMD 5 resources=" + InstallResourceCatalog.STARTUP_INSTALL_RESOURCE_IDS.size());
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

    private void handleAuth(String channel, Map<Integer, byte[]> tags, DataOutputStream dos, GameSession session) throws IOException {
        String username = TlvCodec.tagString(tags, 9);
        String passwordHex = TlvCodec.tagHex(tags, 10);

        if (authService.canLogin(username, passwordHex)) {
            session.setUsername(safeUsername(username));
            session.setAuthenticated(true);

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

    private void handleProfileSync(Map<Integer, byte[]> tags, DataOutputStream dos, GameSession session) throws IOException {
        if (!session.authenticated()) {
            ByteArrayOutputStream payload = new ByteArrayOutputStream();
            TlvCodec.writeTag(payload, 1, "Ban chua dang nhap vao game.");
            TlvCodec.sendPacket(dos, 0, payload.toByteArray(), 1);
            return;
        }

        String username = firstNonBlank(TlvCodec.tagString(tags, 9), session.username(), "player");
        session.setUsername(username);

        CharacterStore.CharacterData characterData = characterStore.load(username);
        if (characterData != null) {
            TagPacketBuilder profile = ProfilePackets.buildCharacterProfile(characterData);
            System.out.println("[GAME] Sending CHARACTER profile for " + username);
            TlvCodec.sendPacket(dos, 9, profile.payload(), profile.count());
            session.setCurrentMapName("M99");
            session.setCurrentRoomId(0);
            WorldPackets.sendMapJoin(dos, username, session.currentMapName(), session.currentRoomId());
            session.setAwaitingCharacterCreation(false);
            session.setCreateCharacterOptionsSent(false);
            return;
        }

        TagPacketBuilder profile = ProfilePackets.buildStartProfile(username);
        System.out.println("[GAME] Sending START profile for " + username);
        TlvCodec.sendPacket(dos, 9, profile.payload(), profile.count());
        session.setAwaitingCharacterCreation(true);
        session.setCreateCharacterOptionsSent(false);
        session.preloadedResources().clear();
        session.pendingResourceAnnouncements().clear();
        session.setActiveInstallResourceId(null);
        sendCreateCharacterOptions(dos, session.username());
        session.setCreateCharacterOptionsSent(true);
    }

    private void handleNoCharacterBootstrap(Map<Integer, byte[]> tags, DataOutputStream dos, GameSession session) throws IOException {
        if (!session.authenticated()) {
            TlvCodec.sendEmpty(dos, 30);
            return;
        }
        if (session.awaitingCharacterCreation()) {
            int classFlag = tags.containsKey(15) && tags.get(15).length > 0 ? tags.get(15)[0] & 0xFF : -1;
            System.out.println("[GAME] Ignore bootstrap CMD 30 while waiting create-char for "
                    + session.username() + " classFlag=" + classFlag);
            return;
        }

        int classFlag = tags.containsKey(15) && tags.get(15).length > 0 ? tags.get(15)[0] & 0xFF : -1;
        System.out.println("[GAME] Ack no-char bootstrap for " + session.username() + " classFlag=" + classFlag);
        TlvCodec.sendEmpty(dos, 30);
    }

    private void handleStartButton(Map<Integer, byte[]> tags, DataOutputStream dos, GameSession session) throws IOException {
        if (!session.authenticated()) {
            TlvCodec.sendEmpty(dos, 42);
            return;
        }
        if (session.awaitingCharacterCreation()) {
            System.out.println("[GAME] Ignore start button CMD 42 while waiting create-char for " + session.username());
            return;
        }

        System.out.println("[GAME] Ack start button for " + session.username() + " tags=" + tags.size());
        TlvCodec.sendEmpty(dos, 42);
        if (session.currentMapName() != null && !session.currentMapName().isEmpty()) {
            WorldPackets.sendMapJoin(dos, session.username(), session.currentMapName(), session.currentRoomId());
        }
    }

    private void handleMapJoinRequest(Map<Integer, byte[]> tags, DataOutputStream dos, GameSession session) throws IOException {
        if (!session.authenticated()) {
            return;
        }

        String username = firstNonBlank(TlvCodec.tagString(tags, 9), session.username(), "player");
        session.setUsername(username);
        if (session.currentMapName() == null || session.currentMapName().isEmpty()) {
            session.setCurrentMapName("M99");
            session.setCurrentRoomId(0);
        }

        System.out.println("[GAME] Respond map join request for " + session.username()
                + " -> " + session.currentMapName() + " room=" + session.currentRoomId());
        WorldPackets.sendMapJoin(dos, session.username(), session.currentMapName(), session.currentRoomId());
    }

    private void handleWorldMapHotspotRequest(Map<Integer, byte[]> tags, DataOutputStream dos, GameSession session) throws IOException {
        if (!session.authenticated()) {
            return;
        }

        String mapName = firstNonBlank(TlvCodec.tagString(tags, 20), session.currentMapName(), "M99");
        int requestValue = tagInt(tags, 41, 0);
        session.setCurrentMapName(mapName);

        System.out.println("[GAME] Respond world map CMD 11 for " + session.username()
                + " map=" + mapName + " requestValue=" + requestValue);
        WorldPackets.sendWorldMapHotspots(dos, mapName);
    }

    private void handleCreateCharacterRequest(PacketRequest request, DataOutputStream dos, GameSession session) throws IOException {
        if (!session.authenticated()) {
            TlvCodec.sendEmpty(dos, 8);
            return;
        }

        ProfilePackets.CreateCharacterSelection selection = ProfilePackets.parseCreateCharacterSelection(request.payload());
        CharacterStore.CharacterData characterData = new CharacterStore.CharacterData(
                session.username(),
                selection.gender(),
                selection.element(),
                selection.hairOptionId(),
                selection.hairColorId(),
                selection.faceOptionId(),
                selection.skinOptionId(),
                selection.skinColorId());

        characterStore.save(characterData);
        System.out.println("[GAME] Created character for " + session.username()
                + " gender=" + selection.gender()
                + " element=" + selection.element()
                + " hair=" + selection.hairOptionId() + "/" + selection.hairColorId()
                + " face=" + selection.faceOptionId()
                + " skin=" + selection.skinOptionId() + "/" + selection.skinColorId());

        session.setAwaitingCharacterCreation(false);
        session.setCurrentMapName("M99");
        session.setCurrentRoomId(0);
        TagPacketBuilder profile = ProfilePackets.buildCharacterProfile(characterData);
        TlvCodec.sendPacket(dos, 9, profile.payload(), profile.count());
        WorldPackets.sendMapJoin(dos, session.username(), session.currentMapName(), session.currentRoomId());
    }

    private void handleInstallResourceRequest(Map<Integer, byte[]> tags, DataOutputStream dos, GameSession session) throws IOException {
        int resourceId = tagInt(tags, 4, -1);
        int chunkIndex = tagInt(tags, 7, -1);
        if (resourceId < 0) {
            System.out.println("[GAME] [WARN] CMD 6 missing resource id");
            return;
        }

        byte[] resource = InstallResourceCatalog.resourceById(resourceId);
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
            session.preloadedResources().add(resourceId);
            session.pendingResourceAnnouncements().remove(resourceId);
            if (session.activeInstallResourceId() != null && session.activeInstallResourceId() == resourceId) {
                session.setActiveInstallResourceId(null);
            }
            announceNextCreateCharacterResource(dos, session);
        }
    }

    private void maybeSendCreateCharacterOptions(DataOutputStream dos, GameSession session) throws IOException {
        if (!session.awaitingCharacterCreation() || session.createCharacterOptionsSent()) {
            return;
        }
        if (!session.preloadedResources().containsAll(InstallResourceCatalog.CREATE_CHAR_RESOURCE_IDS)) {
            return;
        }

        sendCreateCharacterOptions(dos, session.username());
        session.setCreateCharacterOptionsSent(true);
    }

    private void announceNextCreateCharacterResource(DataOutputStream dos, GameSession session) throws IOException {
        if (session.activeInstallResourceId() != null) {
            return;
        }

        for (int resourceId : session.pendingResourceAnnouncements()) {
            if (session.preloadedResources().contains(resourceId)) {
                continue;
            }
            byte[] resource = InstallResourceCatalog.resourceById(resourceId);
            if (resource == null) {
                continue;
            }
            session.setActiveInstallResourceId(resourceId);
            sendInstallResourceAnnouncement(dos, resourceId, resource.length, 1);
            return;
        }

        maybeSendCreateCharacterOptions(dos, session);
    }

    private void sendCreateCharacterOptions(DataOutputStream dos, String username) throws IOException {
        TagPacketBuilder options = new TagPacketBuilder();
        ProfilePackets.appendCreateCharacterOptions(options);

        System.out.println("[GAME] Sending create-character options (CMD 8) for " + username);
        TlvCodec.sendPacket(dos, 8, options.payload(), options.count());
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

    private static void logRequest(String channel, PacketRequest request) {
        System.out.printf("[%s] << CMD %d len=%d tags=%d%n",
                channel,
                request.command(),
                request.payloadLength(),
                request.tags().size());
        for (Map.Entry<Integer, byte[]> entry : request.tags().entrySet()) {
            int size = entry.getValue() == null ? 0 : entry.getValue().length;
            if (entry.getKey() == 20) {
                System.out.printf("       Tag %d (%db) = \"%s\"%n",
                        entry.getKey(),
                        size,
                        new String(entry.getValue()));
            } else if (size == 4) {
                System.out.printf("       Tag %d (%db) = %d%n",
                        entry.getKey(),
                        size,
                        ByteBuffer.wrap(entry.getValue(), 0, 4).getInt());
            } else {
                System.out.printf("       Tag %d (%db)%n", entry.getKey(), size);
            }
        }
    }
}
