package l12sq.server.runtime;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.util.Map;
import l12sq.server.auth.AuthService;
import l12sq.server.config.ServerConfig;
import l12sq.server.net.PacketRequest;
import l12sq.server.net.TlvCodec;
import l12sq.server.runtime.handlers.PacketRouter;
import l12sq.server.storage.AccountStore;
import l12sq.server.storage.CharacterStore;

public final class GameServer {
    private final ServerConfig config;
    private final AuthService authService;
    private final CharacterStore characterStore;
    private final PacketRouter packetRouter;

    public GameServer(ServerConfig config, AccountStore accountStore, CharacterStore characterStore) {
        this.config = config;
        this.authService = new AuthService(accountStore);
        this.characterStore = characterStore;
        this.packetRouter = new PacketRouter(this.config, this.authService, this.characterStore);
    }

    public void start() {
        ServerLog.info("=============================================");
        ServerLog.info("  L12SQ ONLINE SERVER");
        ServerLog.info("  Host      : " + config.advertisedHost());
        ServerLog.info("  Auth Port : " + config.authPort());
        ServerLog.info("  Game Port : " + config.gamePort());
        ServerLog.info("  Accounts  : " + authService.accountCount());
        ServerLog.info("  Scope     : register/login/auth");
        ServerLog.info("=============================================");

        new Thread(() -> listen(config.authPort(), "AUTH"), "auth-listener").start();
        new Thread(() -> listen(config.gamePort(), "GAME"), "game-listener").start();
    }

    private void listen(int port, String channel) {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            ServerLog.info("[" + channel + "] Listening on port " + port + "...");
            while (true) {
                Socket socket = serverSocket.accept();
                socket.setTcpNoDelay(true);
                new Thread(() -> handleClient(socket, channel), channel + "-client").start();
            }
        } catch (IOException exception) {
            ServerLog.info("[" + channel + "] FATAL: " + exception.getMessage());
        }
    }

    private void handleClient(Socket socket, String channel) {
        GameSession session = new GameSession(channel);
        try (DataInputStream dis = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
             DataOutputStream dos = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()))) {

            ServerLog.info("[" + channel + "] Client connected from " + socket.getInetAddress());
            while (true) {
                PacketRequest request = "AUTH".equals(channel)
                        ? TlvCodec.readAuthRequest(dis)
                        : TlvCodec.readGameRequest(dis);
                logRequest(channel, request);
                packetRouter.route(channel, request, dos, session);
            }
        } catch (EOFException eof) {
            ServerLog.info("[" + channel + "] Client disconnected.");
        } catch (IOException exception) {
            ServerLog.info("[" + channel + "] Error: " + exception.getMessage());
        }
    }

    private static void logRequest(String channel, PacketRequest request) {
        ServerLog.info(String.format("[%s] << CMD %d len=%d tags=%d",
                channel,
                request.command(),
                request.payloadLength(),
                request.tags().size()));
        for (Map.Entry<Integer, byte[]> entry : request.tags().entrySet()) {
            int size = entry.getValue() == null ? 0 : entry.getValue().length;
            if (entry.getKey() == 20) {
                ServerLog.info(String.format("       Tag %d (%db) = \"%s\"",
                        entry.getKey(),
                        size,
                        new String(entry.getValue())));
            } else if (size == 4) {
                ServerLog.info(String.format("       Tag %d (%db) = %d",
                        entry.getKey(),
                        size,
                        ByteBuffer.wrap(entry.getValue(), 0, 4).getInt()));
            } else {
                ServerLog.info(String.format("       Tag %d (%db)", entry.getKey(), size));
            }
        }
    }
}
