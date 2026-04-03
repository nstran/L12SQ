package l12sq.server.runtime.handlers;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Map;

import l12sq.server.net.PacketRequest;
import l12sq.server.net.TlvCodec;
import l12sq.server.runtime.GameSession;
import l12sq.server.runtime.ServerLog;
import l12sq.server.runtime.WorldPackets;

public final class WorldMapHandler {

    public void handle(String channel, PacketRequest request, DataOutputStream dos, GameSession session) throws IOException {
        switch (request.command()) {
            case 11 -> handleWorldMapHotspotRequest(request.tags(), dos, session);
            case 13 -> handleMapSelectionRequest(request.tags(), dos, session);
            case 14, 20, 44 -> handleGameplayInput(request.command(), request.tags(), session);
            case 29 -> handleMapJoinRequest(request.tags(), dos, session);
            case 43 -> handleSceneReady(request.tags(), dos, session);
            case 42 -> handleStartButton(request.tags(), dos, session);
        }
    }

    private void handleStartButton(Map<Integer, byte[]> tags, DataOutputStream dos, GameSession session) throws IOException {
        if (!session.authenticated()) {
            TlvCodec.sendEmpty(dos, 42);
            return;
        }
        if (session.awaitingCharacterCreation()) {
            ServerLog.info("[GAME] Ignore start button CMD 42 while waiting create-char for " + session.username());
            return;
        }

        ServerLog.info("[GAME] Ack start button for " + session.username() + " tags=" + tags.size());
        TlvCodec.sendEmpty(dos, 42);
        if (session.currentMapName() != null && !session.currentMapName().isEmpty()) {
            WorldPackets.sendMapJoin(dos, session.username(), session.currentMapName(), session.currentRoomId());
        }
    }

    private void handleMapJoinRequest(Map<Integer, byte[]> tags, DataOutputStream dos, GameSession session) throws IOException {
        if (!session.authenticated()) {
            return;
        }

        String username = PacketUtils.firstNonBlank(TlvCodec.tagString(tags, 9), session.username(), "player");
        session.setUsername(username);
        if (session.currentMapName() == null || session.currentMapName().isEmpty()) {
            session.setCurrentMapName("M99");
            session.setCurrentRoomId(0);
        }

        ServerLog.info("[GAME] Respond map join request for " + session.username()
                + " -> " + session.currentMapName() + " room=" + session.currentRoomId());
        WorldPackets.sendMapJoin(dos, session.username(), session.currentMapName(), session.currentRoomId());
    }

    private void handleWorldMapHotspotRequest(Map<Integer, byte[]> tags, DataOutputStream dos, GameSession session) throws IOException {
        if (!session.authenticated()) {
            return;
        }

        String mapName = PacketUtils.firstNonBlank(TlvCodec.tagString(tags, 20), session.currentMapName(), "M99");
        int requestValue = PacketUtils.tagInt(tags, 41, 0);
        session.setCurrentMapName(mapName);
        if ("M99".equalsIgnoreCase(mapName)) {
            session.setSceneReady(false);
        }

        ServerLog.info("[GAME] Respond CMD 11 for " + session.username()
                + " map=" + mapName + " requestValue=" + requestValue);
        if ("M99".equalsIgnoreCase(mapName)) {
            WorldPackets.sendWorldMapHotspots(dos, mapName);
            return;
        }
        WorldPackets.sendMapInfo(dos, mapName, Math.max(1, session.currentRoomId()));
    }

    private void handleMapSelectionRequest(Map<Integer, byte[]> tags, DataOutputStream dos, GameSession session) throws IOException {
        if (!session.authenticated()) {
            return;
        }

        String sourceMapName = PacketUtils.firstNonBlank(TlvCodec.tagString(tags, 20), session.currentMapName(), "M99");
        int roomOrMarkerId = PacketUtils.tagInt(tags, 21, 0);
        String targetMapName = resolveSelectedMapName(sourceMapName, roomOrMarkerId);
        int targetRoomId = "M99".equalsIgnoreCase(sourceMapName) ? 1 : roomOrMarkerId;
        session.setCurrentMapName(targetMapName);
        session.setCurrentRoomId(targetRoomId);
        session.setSceneReady(false);

        ServerLog.info("[GAME] Respond CMD 13 for " + session.username()
                + " sourceMap=" + sourceMapName
                + " roomOrMarkerId=" + roomOrMarkerId
                + " -> targetMap=" + targetMapName
                + " room=" + targetRoomId);
        WorldPackets.sendMapSelectionAck(dos, targetMapName, targetRoomId, 0);
    }

    private static String resolveSelectedMapName(String sourceMapName, int roomOrMarkerId) {
        if ("M99".equalsIgnoreCase(sourceMapName)) {
            return switch (roomOrMarkerId) {
                case 0 -> "Hoa Lu";
                default -> "Hoa Lu";
            };
        }
        return sourceMapName;
    }

    private void handleSceneReady(Map<Integer, byte[]> tags, DataOutputStream dos, GameSession session) throws IOException {
        if (!session.authenticated()) {
            return;
        }

        String mapName = PacketUtils.firstNonBlank(TlvCodec.tagString(tags, 20), session.currentMapName(), "M99");
        session.setCurrentMapName(mapName);
        session.setSceneReady(true);
        ServerLog.info("[GAME] Scene ready CMD 43 for " + session.username() + " map=" + mapName);
        WorldPackets.sendSceneActors(dos, mapName, session.username());
    }

    private void handleGameplayInput(int command, Map<Integer, byte[]> tags, GameSession session) {
        if (!session.authenticated()) {
            return;
        }

        String mapName = PacketUtils.firstNonBlank(TlvCodec.tagString(tags, 20), session.currentMapName(), "M99");
        ServerLog.info("[GAME] Observed gameplay CMD " + command
                + " user=" + session.username()
                + " map=" + mapName
                + " sceneReady=" + session.sceneReady());
    }
}
