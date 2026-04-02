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
import java.util.Map;
import l12sq.server.auth.AuthService;
import l12sq.server.auth.CaptchaFactory;
import l12sq.server.config.ServerConfig;
import l12sq.server.net.PacketRequest;
import l12sq.server.net.TlvCodec;
import l12sq.server.storage.AccountStore;

public final class GameServer {
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
        try (DataInputStream dis = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
             DataOutputStream dos = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()))) {

            System.out.println("[" + channel + "] Client connected from " + socket.getInetAddress());
            while (true) {
                PacketRequest request = "AUTH".equals(channel)
                        ? TlvCodec.readAuthRequest(dis)
                        : TlvCodec.readGameRequest(dis);
                logRequest(channel, request);
                route(channel, request, dos);
            }
        } catch (EOFException eof) {
            System.out.println("[" + channel + "] Client disconnected.");
        } catch (IOException exception) {
            System.out.println("[" + channel + "] Error: " + exception.getMessage());
        }
    }

    private void route(String channel, PacketRequest request, DataOutputStream dos) throws IOException {
        switch (request.command()) {
            case 5 -> TlvCodec.sendEmpty(dos, 5);
            case 2, 3 -> handleSalt(dos);
            case 130 -> handleCaptcha(dos);
            case 131 -> handleRegistration(request.tags(), dos);
            case 4 -> handleAuth(channel, request.tags(), dos);
            case 1 -> TlvCodec.sendEmpty(dos, 1);
            default -> System.out.println("[" + channel + "] [WARN] Unhandled CMD " + request.command());
        }
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

    private void handleAuth(String channel, Map<Integer, byte[]> tags, DataOutputStream dos) throws IOException {
        String username = TlvCodec.tagString(tags, 9);
        String passwordHex = TlvCodec.tagHex(tags, 10);

        if ("AUTH".equals(channel)) {
            if (authService.canLogin(username, passwordHex)) {
                TlvCodec.sendEmpty(dos, 1);
                TlvCodec.sendSingleTag(dos, 2, TlvCodec.makeTag(3, config.advertisedHost()));
                return;
            }

            ByteArrayOutputStream payload = new ByteArrayOutputStream();
            TlvCodec.writeTag(payload, 1, "Dang nhap that bai.");
            TlvCodec.sendPacket(dos, 0, payload.toByteArray(), 1);
            return;
        }

        TlvCodec.sendEmpty(dos, 4);
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
}
