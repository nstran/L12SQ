package l12sq.server.runtime.handlers;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Map;

import l12sq.server.net.PacketRequest;
import l12sq.server.net.TlvCodec;
import l12sq.server.runtime.GameSession;
import l12sq.server.runtime.ProfilePackets;
import l12sq.server.runtime.ServerLog;
import l12sq.server.runtime.TagPacketBuilder;
import l12sq.server.runtime.WorldPackets;
import l12sq.server.storage.CharacterStore;

public final class CharacterHandler {
    private final CharacterStore characterStore;

    public CharacterHandler(CharacterStore characterStore) {
        this.characterStore = characterStore;
    }

    public void handle(String channel, PacketRequest request, DataOutputStream dos, GameSession session) throws IOException {
        switch (request.command()) {
            case 8 -> handleCreateCharacterRequest(request, dos, session);
            case 9 -> handleProfileSync(request.tags(), dos, session);
            case 30 -> handleNoCharacterBootstrap(request.tags(), dos, session);
        }
    }

    private void handleProfileSync(Map<Integer, byte[]> tags, DataOutputStream dos, GameSession session) throws IOException {
        if (!session.authenticated()) {
            java.io.ByteArrayOutputStream payload = new java.io.ByteArrayOutputStream();
            TlvCodec.writeTag(payload, 1, "Ban chua dang nhap vao game.");
            TlvCodec.sendPacket(dos, 0, payload.toByteArray(), 1);
            return;
        }

        String username = PacketUtils.firstNonBlank(TlvCodec.tagString(tags, 9), session.username(), "player");
        session.setUsername(username);

        CharacterStore.CharacterData characterData = characterStore.load(username);
        if (characterData != null) {
            TagPacketBuilder profile = ProfilePackets.buildCharacterProfile(characterData);
            ServerLog.info("[GAME] Sending CHARACTER profile for " + username);
            TlvCodec.sendPacket(dos, 9, profile.payload(), profile.count());
            session.setCurrentMapName("M99");
            session.setCurrentRoomId(0);
            session.setSceneReady(false);
            WorldPackets.sendMapJoin(dos, username, session.currentMapName(), session.currentRoomId());
            session.setAwaitingCharacterCreation(false);
            session.setCreateCharacterOptionsSent(false);
            return;
        }

        TagPacketBuilder profile = ProfilePackets.buildStartProfile(username);
        ServerLog.info("[GAME] Sending START profile for " + username);
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
            ServerLog.info("[GAME] Ignore bootstrap CMD 30 while waiting create-char for "
                    + session.username() + " classFlag=" + classFlag);
            return;
        }

        int classFlag = tags.containsKey(15) && tags.get(15).length > 0 ? tags.get(15)[0] & 0xFF : -1;
        ServerLog.info("[GAME] Ack no-char bootstrap for " + session.username() + " classFlag=" + classFlag);
        TlvCodec.sendEmpty(dos, 30);
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
        ServerLog.info("[GAME] Created character for " + session.username()
                + " gender=" + selection.gender()
                + " element=" + selection.element()
                + " hair=" + selection.hairOptionId() + "/" + selection.hairColorId()
                + " face=" + selection.faceOptionId()
                + " skin=" + selection.skinOptionId() + "/" + selection.skinColorId());

        session.setAwaitingCharacterCreation(false);
        session.setCurrentMapName("M99");
        session.setCurrentRoomId(0);
        session.setSceneReady(false);
        TagPacketBuilder profile = ProfilePackets.buildCharacterProfile(characterData);
        TlvCodec.sendPacket(dos, 9, profile.payload(), profile.count());
        WorldPackets.sendMapJoin(dos, session.username(), session.currentMapName(), session.currentRoomId());
    }

    public static void sendCreateCharacterOptions(DataOutputStream dos, String username) throws IOException {
        TagPacketBuilder options = new TagPacketBuilder();
        ProfilePackets.appendCreateCharacterOptions(options);

        ServerLog.info("[GAME] Sending create-character options (CMD 8) for " + username);
        TlvCodec.sendPacket(dos, 8, options.payload(), options.count());
    }
}
