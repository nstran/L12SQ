package l12sq.server.runtime.handlers;

import java.io.DataOutputStream;
import java.io.IOException;

import l12sq.server.auth.AuthService;
import l12sq.server.config.ServerConfig;
import l12sq.server.net.PacketRequest;
import l12sq.server.net.TlvCodec;
import l12sq.server.runtime.GameSession;
import l12sq.server.runtime.ServerLog;
import l12sq.server.storage.CharacterStore;

public final class PacketRouter {
    private final AuthHandler authHandler;
    private final ResourceHandler resourceHandler;
    private final CharacterHandler characterHandler;
    private final WorldMapHandler worldMapHandler;

    public PacketRouter(ServerConfig config, AuthService authService, CharacterStore characterStore) {
        this.authHandler = new AuthHandler(config, authService);
        this.resourceHandler = new ResourceHandler();
        this.characterHandler = new CharacterHandler(characterStore);
        this.worldMapHandler = new WorldMapHandler();
    }

    public void route(String channel, PacketRequest request, DataOutputStream dos, GameSession session) throws IOException {
        switch (request.command()) {
            case 2, 3, 130, 131, 4 -> authHandler.handle(channel, request, dos, session);
            case 5, 6 -> resourceHandler.handle(channel, request, dos, session);
            case 8, 9, 30 -> characterHandler.handle(channel, request, dos, session);
            case 11, 13, 14, 20, 29, 42, 43, 44 -> worldMapHandler.handle(channel, request, dos, session);
            case 1 -> TlvCodec.sendEmpty(dos, 1);
            default -> ServerLog.info("[" + channel + "] [WARN] Unhandled CMD " + request.command());
        }
    }
}
