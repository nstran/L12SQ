package l12sq.server.runtime.handlers;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Map;

import l12sq.server.net.PacketRequest;
import l12sq.server.net.TlvCodec;
import l12sq.server.runtime.GameSession;
import l12sq.server.runtime.InstallResourceCatalog;
import l12sq.server.runtime.ServerLog;
import l12sq.server.runtime.TagPacketBuilder;

public final class ResourceHandler {
    private static final int INSTALL_RESOURCE_CHUNK_SIZE = 32 * 1024;

    public void handle(String channel, PacketRequest request, DataOutputStream dos, GameSession session) throws IOException {
        switch (request.command()) {
            case 5 -> handleVersionCheck(channel, dos, session);
            case 6 -> handleInstallResourceRequest(request.tags(), dos, session);
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
        ServerLog.info("[GAME] Sending install manifest via CMD 5 resources=" + InstallResourceCatalog.STARTUP_INSTALL_RESOURCE_IDS.size());
        TlvCodec.sendPacket(dos, 5, builder.payload(), builder.count());
    }

    private void handleInstallResourceRequest(Map<Integer, byte[]> tags, DataOutputStream dos, GameSession session) throws IOException {
        int resourceId = PacketUtils.tagInt(tags, 4, -1);
        int chunkIndex = PacketUtils.tagInt(tags, 7, -1);
        if (resourceId < 0) {
            ServerLog.info("[GAME] [WARN] CMD 6 missing resource id");
            return;
        }

        byte[] resource = InstallResourceCatalog.resourceById(resourceId);
        if (resource == null) {
            ServerLog.info("[GAME] [WARN] Unknown install resource " + resourceId + " chunk=" + chunkIndex);
            return;
        }

        int totalChunks = totalChunks(resource.length);
        if (chunkIndex < 0) {
            sendInstallResourceAnnouncement(dos, resourceId, resource.length, totalChunks);
            return;
        }

        if (chunkIndex >= totalChunks) {
            ServerLog.info("[GAME] [WARN] CMD 6 invalid chunk index resource=" + resourceId
                    + " chunk=" + chunkIndex
                    + " totalChunks=" + totalChunks);
            return;
        }

        sendInstallResourceChunk(dos, resourceId, chunkIndex, resource);
        if (chunkIndex == totalChunks - 1) {
            session.preloadedResources().add(resourceId);
            session.pendingResourceAnnouncements().remove(resourceId);
            if (session.activeInstallResourceId() != null && session.activeInstallResourceId() == resourceId) {
                session.setActiveInstallResourceId(null);
            }
            announceNextCreateCharacterResource(dos, session);
        }
    }

    protected void announceNextCreateCharacterResource(DataOutputStream dos, GameSession session) throws IOException {
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
            sendInstallResourceAnnouncement(dos, resourceId, resource.length, totalChunks(resource.length));
            return;
        }

        // Delegate to CharacterHandler or statically call sendCreateCharacterOptions inside CharacterHandler
        if (!session.awaitingCharacterCreation() || session.createCharacterOptionsSent()) {
            return;
        }
        if (!session.preloadedResources().containsAll(InstallResourceCatalog.CREATE_CHAR_RESOURCE_IDS)) {
            return;
        }

        CharacterHandler.sendCreateCharacterOptions(dos, session.username());
        session.setCreateCharacterOptionsSent(true);
    }

    private static void sendInstallResourceAnnouncement(DataOutputStream dos, int resourceId, int totalBytes, int totalChunks) throws IOException {
        TagPacketBuilder builder = new TagPacketBuilder();
        builder.intTag(4, resourceId);
        builder.intTag(6, totalBytes);
        builder.intTag(5, totalChunks);
        ServerLog.info("[GAME] >> CMD 6 announce resource=" + resourceId + " bytes=" + totalBytes + " chunks=" + totalChunks);
        TlvCodec.sendPacket(dos, 6, builder.payload(), builder.count());
    }

    private static void sendInstallResourceChunk(DataOutputStream dos, int resourceId, int chunkIndex, byte[] chunkBytes) throws IOException {
        TagPacketBuilder builder = new TagPacketBuilder();
        builder.intTag(4, resourceId);
        builder.intTag(7, chunkIndex);
        byte[] payload = chunkSlice(chunkBytes, chunkIndex);
        builder.rawTag(8, payload);
        ServerLog.info("[GAME] >> CMD 6 chunk resource=" + resourceId + " chunk=" + chunkIndex + " bytes=" + payload.length);
        TlvCodec.sendPacket(dos, 6, builder.payload(), builder.count());
    }

    private static int totalChunks(int totalBytes) {
        return Math.max(1, (totalBytes + INSTALL_RESOURCE_CHUNK_SIZE - 1) / INSTALL_RESOURCE_CHUNK_SIZE);
    }

    private static byte[] chunkSlice(byte[] bytes, int chunkIndex) {
        int start = chunkIndex * INSTALL_RESOURCE_CHUNK_SIZE;
        int end = Math.min(bytes.length, start + INSTALL_RESOURCE_CHUNK_SIZE);
        int length = Math.max(0, end - start);
        byte[] slice = new byte[length];
        System.arraycopy(bytes, start, slice, 0, length);
        return slice;
    }
}
