package l12sq.server.runtime.handlers;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Map;

import l12sq.server.auth.AuthService;
import l12sq.server.auth.CaptchaFactory;
import l12sq.server.config.ServerConfig;
import l12sq.server.net.PacketRequest;
import l12sq.server.net.TlvCodec;
import l12sq.server.runtime.GameSession;

public final class AuthHandler {
    private final ServerConfig config;
    private final AuthService authService;

    public AuthHandler(ServerConfig config, AuthService authService) {
        this.config = config;
        this.authService = authService;
    }

    public void handle(String channel, PacketRequest request, DataOutputStream dos, GameSession session) throws IOException {
        switch (request.command()) {
            case 2, 3 -> handleSalt(dos);
            case 130 -> handleCaptcha(dos);
            case 131 -> handleRegistration(request.tags(), dos);
            case 4 -> handleAuth(channel, request.tags(), dos, session);
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

    private void handleAuth(String channel, Map<Integer, byte[]> tags, DataOutputStream dos, GameSession session) throws IOException {
        String username = TlvCodec.tagString(tags, 9);
        String passwordHex = TlvCodec.tagHex(tags, 10);

        if (authService.canLogin(username, passwordHex)) {
            session.setUsername(PacketUtils.safeUsername(username));
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
}
