import java.io.*;
import java.net.*;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;

/**
 * Loan 12 Su Quan - Offline Server v1.0
 * Handles both Auth (port 2236) and Game (port 1238) connections.
 * Protocol: Binary TLV over TCP socket.
 */
public class OfflineServer {
    private static final int AUTH_PORT = 7236;
    private static final int GAME_PORT = 7238;

    public static void main(String[] args) {
        System.out.println("=============================================");
        System.out.println("  LOAN 12 SU QUAN - OFFLINE SERVER v1.0");
        System.out.println("  Auth Port: " + AUTH_PORT);
        System.out.println("  Game Port: " + GAME_PORT);
        System.out.println("=============================================");

        new Thread(() -> listen(AUTH_PORT, "AUTH"), "auth-listener").start();
        new Thread(() -> listen(GAME_PORT, "GAME"), "game-listener").start();
    }

    private static void listen(int port, String name) {
        try (ServerSocket ss = new ServerSocket(port)) {
            System.out.println("[" + name + "] Listening on port " + port + "...");
            while (true) {
                Socket c = ss.accept();
                c.setTcpNoDelay(true);
                new Thread(() -> handleClient(c, name), name + "-client").start();
            }
        } catch (IOException e) {
            System.err.println("[" + name + "] FATAL: " + e.getMessage());
        }
    }

    // ========== CLIENT HANDLER ==========

    private static void handleClient(Socket socket, String serverType) {
        try (DataInputStream dis = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
             DataOutputStream dos = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()))) {

            System.out.println("\n[" + serverType + "] Client connected from " + socket.getInetAddress());

            while (true) {
                // --- Read client packet ---
                // Format: [4-int totalLen][1-byte type][4-byte magic][1-byte cmd][payload]
                int totalLen = dis.readInt();
                int type = dis.readUnsignedByte();
                byte[] magic = new byte[4];
                dis.readFully(magic);
                int cmd = dis.readUnsignedByte();

                int payloadLen = totalLen - 6; // subtract type(1) + magic(4) + cmd(1)
                byte[] payload = new byte[Math.max(0, payloadLen)];
                if (payloadLen > 0) dis.readFully(payload);

                // Parse TLV tags from payload
                Map<Integer, byte[]> tags = parseTLV(payload);

                System.out.printf("[%s] << CMD %d (0x%02X) len=%d tags=%d%n",
                        serverType, cmd, cmd, totalLen, tags.size());
                for (var e : tags.entrySet()) {
                    System.out.printf("       Tag %d = %s%n", e.getKey(), fmt(e.getValue()));
                }

                // --- Handle command ---
                processCmd(dos, cmd, tags, serverType);
            }
        } catch (EOFException e) {
            System.out.println("[" + serverType + "] Client disconnected.");
        } catch (IOException e) {
            System.out.println("[" + serverType + "] Error: " + e.getMessage());
        }
    }

    // ========== COMMAND DISPATCHER ==========

    private static void processCmd(DataOutputStream dos, int cmd, Map<Integer, byte[]> tags, String sType) throws IOException {
        switch (cmd) {
            case 5:  handleVersion(dos); break;
            case 2:  handleLoginReq(dos); break;
            case 3:  handleLoginReq(dos); break; // re-auth on game port
            case 4:  handleAuth(dos, tags, sType); break;
            case 1:  handleKeepAlive(dos); break;
            case 9:  handleProfileSync(dos, tags); break;
            case 10: handleStatUpdate(dos, tags); break;
            case 11: handleRoomList(dos, tags); break;
            case 15: handleMapInfo(dos, tags); break;
            case 6:  handleResource(dos, tags); break;
            case 29: handlePlayerInfo(dos, tags); break;
            case 30: handleClassSelect(dos, tags); break;
            case 37: handleFriendList(dos, tags); break;
            case 51: handleInventory(dos, tags); break;
            case 16: handleChat(dos, tags); break;
            case 25: handleMsg(dos, tags); break;
            case 42: handleProfileRequest(dos, tags); break;
            case 43: send(dos, 43); break; // leave room OK
            case 84: handleEquipSelect(dos, tags); break;
            case 96: handleEquipView(dos, tags); break;
            case 97: handleEquipUpgrade(dos, tags); break;
            case 100: handleEquipOp(dos, tags); break;
            case 112: handleMarket(dos, tags); break;
            case 114: handleMarketList(dos, tags); break;
            case 129: handleGuild(dos, tags); break;
            case 130: handleRegCaptcha(dos); break;
            case 131: handleRegSubmit(dos, tags); break;
            case 132: send(dos, 132); break;
            case 133: send(dos, 133); break;
            default:
                System.out.printf("       [WARN] Unhandled CMD %d%n", cmd);
                break;
        }
    }

    // ========== COMMAND HANDLERS ==========

    private static void handleVersion(DataOutputStream dos) throws IOException {
        System.out.println("       >> Version OK");
        send(dos, 5);
    }

    private static void handleLoginReq(DataOutputStream dos) throws IOException {
        System.out.println("       >> Sending Salt challenge");
        byte[] salt = new byte[16];
        new Random().nextBytes(salt);
        send(dos, 3, tag(2, salt));
    }

    private static void handleAuth(DataOutputStream dos, Map<Integer, byte[]> tags, String sType) throws IOException {
        String nick = tagStr(tags, 9);
        System.out.println("       >> Auth submit from: " + nick);

        if (sType.equals("AUTH")) {
            // Phase 1: Auth success + redirect to game port
            send(dos, 1);  // success
            send(dos, 2, tag(3, "127.0.0.1")); // redirect IP only
            System.out.println("       >> AUTH OK -> Redirect to GAME:" + GAME_PORT);
        } else {
            // Phase 2: Game auth success
            send(dos, 4);  // game auth OK -> triggers client c.R()
            System.out.println("       >> GAME AUTH OK");
        }
    }

    private static void handleKeepAlive(DataOutputStream dos) throws IOException {
        send(dos, 1);
    }

    private static void handleProfileSync(DataOutputStream dos, Map<Integer, byte[]> tags) throws IOException {
        String nick = tagStr(tags, 9);
        if (nick == null) nick = "admin";
        int profileType = tagByte(tags, 134, -1);
        System.out.println("       >> Profile sync for: " + nick + " type=" + profileType);

        if (profileType > 0) {
            // Full profile (readFighterInf)
            sendFullProfile(dos, nick);
        } else {
            // Partial update via bitmask
            int bitmask = tagInt(tags, 23, 0);
            System.out.println("       >> Partial sync bitmask=" + bitmask);
            sendPartialProfile(dos, nick, bitmask);
        }
    }

    private static void sendFullProfile(DataOutputStream dos, String nick) throws IOException {
        System.out.println("       >> Sending FULL profile for " + nick);
        ByteArrayOutputStream tlv = new ByteArrayOutputStream(512);
        int count = 0;

        // Tag 134: profile type (must be > 0 for full read)
        wTag(tlv, 134, (byte) 1); count++;
        // Basic info
        wTag(tlv, 9,  nick);         count++; // nick
        wTag(tlv, 26, "Su Quan");    count++; // desc
        wTag(tlv, 15, (byte) 1);     count++; // class/he
        wTag(tlv, 16, (byte) 0);     count++; // gender
        wTag(tlv, 27, 50);           count++; // level
        // HP / Mana / Power
        wTag(tlv, 17, 5000);  count++; // hp current
        wTag(tlv, 47, 5000);  count++; // hp max
        wTag(tlv, 18, 3000);  count++; // mana current
        wTag(tlv, 48, 3000);  count++; // mana max
        // Base stats
        wTag(tlv, 118, 100); count++; // strength
        wTag(tlv, 119, 100); count++; // agility
        wTag(tlv, 120, 100); count++; // magic
        wTag(tlv, 121, 100); count++; // vitality
        // Bonus stats
        wTag(tlv, 196, 50);  count++; // addStr
        wTag(tlv, 197, 50);  count++; // addAgi
        wTag(tlv, 198, 50);  count++; // addMag
        wTag(tlv, 199, 50);  count++; // addVit
        // Combat stats
        wTag(tlv, 116, 500); count++; // addHP
        wTag(tlv, 115, 100); count++; // hpPercent
        wTag(tlv, 42,  500); count++; // attack (J)
        wTag(tlv, 43,  300); count++; // defense (H)
        wTag(tlv, 99,  10000); count++; // speed (I)
        wTag(tlv, 53,  100); count++; // K stat
        wTag(tlv, 76,  50);  count++; // L stat
        wTag(tlv, 73,  50);  count++; // M stat
        wTag(tlv, 74,  50);  count++; // N stat
        wTag(tlv, 108, 10);  count++; // crit rate
        wTag(tlv, 109, 10);  count++; // dodge rate
        // Social
        wTag(tlv, 151, "Offline");  count++; // guild
        wTag(tlv, 160, 1000);       count++; // honor
        wTag(tlv, 165, (byte) 0);   count++; // flag ab
        wTag(tlv, 166, (byte) 0);   count++; // flag ac
        // Empty arrays: skills(64), equip(83), items(114), pets(90), quests(158)
        // Not including them = count of 0 = empty arrays

        sendPacket(dos, 9, tlv.toByteArray(), count);
        System.out.println("       >> Full profile sent (" + count + " tags)");
    }

    private static void sendPartialProfile(DataOutputStream dos, String nick, int bitmask) throws IOException {
        ByteArrayOutputStream tlv = new ByteArrayOutputStream(256);
        int count = 0;
        wTag(tlv, 23, bitmask > 0 ? bitmask : 0xFF); count++;
        wTag(tlv, 9, nick); count++;
        wTag(tlv, 27, 50);  count++; // level
        wTag(tlv, 17, 5000); count++; wTag(tlv, 47, 5000); count++;
        wTag(tlv, 18, 3000); count++; wTag(tlv, 48, 3000); count++;
        wTag(tlv, 118, 100); count++; wTag(tlv, 119, 100); count++;
        wTag(tlv, 120, 100); count++; wTag(tlv, 121, 100); count++;
        wTag(tlv, 42, 500);  count++; wTag(tlv, 43, 300);  count++;
        wTag(tlv, 99, 10000); count++;
        sendPacket(dos, 9, tlv.toByteArray(), count);
    }

    private static void handleStatUpdate(DataOutputStream dos, Map<Integer, byte[]> tags) throws IOException {
        System.out.println("       >> Stat update acknowledged");
        sendFullProfile(dos, "admin");
        send(dos, 27); // triggers c.S()
    }

    private static void handleRoomList(DataOutputStream dos, Map<Integer, byte[]> tags) throws IOException {
        String zone = tagStr(tags, 20);
        System.out.println("       >> Room list for zone: " + zone);
        // Send empty room list
        ByteArrayOutputStream tlv = new ByteArrayOutputStream();
        int count = 0;
        wTag(tlv, 20, zone != null ? zone : "offline"); count++;
        wTag(tlv, 12, (byte) 0); count++; // type
        sendPacket(dos, 11, tlv.toByteArray(), count);
    }

    private static void handleMapInfo(DataOutputStream dos, Map<Integer, byte[]> tags) throws IOException {
        System.out.println("       >> Map info request");
        ByteArrayOutputStream tlv = new ByteArrayOutputStream();
        int count = 0;
        wTag(tlv, 20, "offline"); count++;
        wTag(tlv, 12, (byte) 0); count++;
        sendPacket(dos, 15, tlv.toByteArray(), count);
    }

    private static void handleResource(DataOutputStream dos, Map<Integer, byte[]> tags) throws IOException {
        int resId = tagInt(tags, 4, 0);
        System.out.println("       >> Resource request: " + resId);
        ByteArrayOutputStream tlv = new ByteArrayOutputStream();
        int count = 0;
        wTag(tlv, 4, resId); count++;
        wTag(tlv, 5, 0); count++;
        wTag(tlv, 6, 0); count++;
        sendPacket(dos, 6, tlv.toByteArray(), count);
    }

    private static void handlePlayerInfo(DataOutputStream dos, Map<Integer, byte[]> tags) throws IOException {
        String nick = tagStr(tags, 9);
        System.out.println("       >> Player info for: " + nick);
        ByteArrayOutputStream tlv = new ByteArrayOutputStream();
        int count = 0;
        wTag(tlv, 9, nick != null ? nick : "admin"); count++;
        wTag(tlv, 20, "Online"); count++;
        wTag(tlv, 21, 0); count++;
        sendPacket(dos, 29, tlv.toByteArray(), count);
    }

    private static void handleClassSelect(DataOutputStream dos, Map<Integer, byte[]> tags) throws IOException {
        System.out.println("       >> Class select");
        send(dos, 30);
    }

    private static void handleFriendList(DataOutputStream dos, Map<Integer, byte[]> tags) throws IOException {
        System.out.println("       >> Friend list");
        ByteArrayOutputStream tlv = new ByteArrayOutputStream();
        int count = 0;
        wTag(tlv, 89, (byte) 0); count++;
        sendPacket(dos, 37, tlv.toByteArray(), count);
    }

    private static void handleInventory(DataOutputStream dos, Map<Integer, byte[]> tags) throws IOException {
        System.out.println("       >> Inventory request");
        ByteArrayOutputStream tlv = new ByteArrayOutputStream();
        int count = 0;
        wTag(tlv, 114, 0); count++;
        wTag(tlv, 1, ""); count++;
        sendPacket(dos, 51, tlv.toByteArray(), count);
    }

    private static void handleChat(DataOutputStream dos, Map<Integer, byte[]> tags) throws IOException {
        String who = tagStr(tags, 9);
        String msg = tagStr(tags, 1);
        System.out.println("       >> Chat [" + who + "]: " + msg);
    }

    private static void handleMsg(DataOutputStream dos, Map<Integer, byte[]> tags) throws IOException {
        System.out.println("       >> Message command");
    }

    private static void handleEquipSelect(DataOutputStream dos, Map<Integer, byte[]> tags) throws IOException {
        int id = tagInt(tags, 114, 0);
        System.out.println("       >> Equip select: " + id);
        ByteArrayOutputStream tlv = new ByteArrayOutputStream();
        int count = 0;
        wTag(tlv, 114, id); count++;
        wTag(tlv, 106, 0);  count++;
        sendPacket(dos, 84, tlv.toByteArray(), count);
    }

    private static void handleEquipView(DataOutputStream dos, Map<Integer, byte[]> tags) throws IOException {
        System.out.println("       >> Equip view");
        ByteArrayOutputStream tlv = new ByteArrayOutputStream();
        int count = 0;
        wTag(tlv, 186, "0"); count++;
        wTag(tlv, 83, ""); count++;
        wTag(tlv, 1, "Khong co trang bi"); count++;
        sendPacket(dos, 96, tlv.toByteArray(), count);
    }

    private static void handleEquipUpgrade(DataOutputStream dos, Map<Integer, byte[]> tags) throws IOException {
        System.out.println("       >> Equip upgrade");
        ByteArrayOutputStream tlv = new ByteArrayOutputStream();
        int count = 0;
        wTag(tlv, 186, tagStr(tags, 186) != null ? tagStr(tags, 186) : "0"); count++;
        wTag(tlv, 187, (byte) 0); count++;
        wTag(tlv, 1, "Offline mode"); count++;
        wTag(tlv, 188, (byte) 0);  count++;
        sendPacket(dos, 97, tlv.toByteArray(), count);
    }

    private static void handleEquipOp(DataOutputStream dos, Map<Integer, byte[]> tags) throws IOException {
        System.out.println("       >> Equipment operation");
        ByteArrayOutputStream tlv = new ByteArrayOutputStream();
        int count = 0;
        wTag(tlv, 186, "0"); count++;
        wTag(tlv, 187, (byte) 0); count++;
        wTag(tlv, 1, "OK"); count++;
        wTag(tlv, 132, 999999L); count++;
        wTag(tlv, 188, (byte) 1); count++;
        sendPacket(dos, 100, tlv.toByteArray(), count);
    }

    private static void handleMarket(DataOutputStream dos, Map<Integer, byte[]> tags) throws IOException {
        System.out.println("       >> Market");
        send(dos, 112);
    }

    private static void handleMarketList(DataOutputStream dos, Map<Integer, byte[]> tags) throws IOException {
        System.out.println("       >> Market list");
        ByteArrayOutputStream tlv = new ByteArrayOutputStream();
        int count = 0;
        wTag(tlv, 152, (byte) 0); count++;
        wTag(tlv, 106, 0); count++;
        sendPacket(dos, 114, tlv.toByteArray(), count);
    }

    private static void handleGuild(DataOutputStream dos, Map<Integer, byte[]> tags) throws IOException {
        System.out.println("       >> Guild info");
        ByteArrayOutputStream tlv = new ByteArrayOutputStream();
        int count = 0;
        wTag(tlv, 147, (byte) 0); count++;
        wTag(tlv, 162, "Offline Guild"); count++;
        sendPacket(dos, 129, tlv.toByteArray(), count);
    }

    private static void handleProfileRequest(DataOutputStream dos, Map<Integer, byte[]> tags) throws IOException {
        String nick = tagStr(tags, 9);
        System.out.println("       >> Profile request (CMD 42) for: " + nick);
        sendFullProfile(dos, nick != null ? nick : "admin");
    }

    /** CMD 130 - Send captcha image + salt for registration */
    private static void handleRegCaptcha(DataOutputStream dos) throws IOException {
        System.out.println("       >> Sending registration captcha");
        // Generate a simple captcha image with text "1234"
        byte[] captchaImg = generateCaptchaPng("1234");
        byte[] salt = new byte[16];
        new Random().nextBytes(salt);

        ByteArrayOutputStream tlv = new ByteArrayOutputStream();
        int count = 0;
        wTag(tlv, 176, captchaImg); count++;  // captcha image
        wTag(tlv, 2, salt);         count++;  // salt
        sendPacket(dos, 130, tlv.toByteArray(), count);
        System.out.println("       >> Captcha sent (" + captchaImg.length + " bytes)");
    }

    /** CMD 131 - Registration submit → always success */
    private static void handleRegSubmit(DataOutputStream dos, Map<Integer, byte[]> tags) throws IOException {
        String nick = tagStr(tags, 9);
        System.out.println("       >> Registration submit for: " + nick);
        ByteArrayOutputStream tlv = new ByteArrayOutputStream();
        int count = 0;
        wTag(tlv, 1, "Dang ky thanh cong! Hay dang nhap."); count++;
        sendPacket(dos, 131, tlv.toByteArray(), count);
        System.out.println("       >> Registration SUCCESS");
    }

    /** Generate a simple PNG captcha image */
    private static byte[] generateCaptchaPng(String text) {
        try {
            BufferedImage img = new BufferedImage(80, 30, BufferedImage.TYPE_INT_RGB);
            Graphics2D g = img.createGraphics();
            g.setColor(Color.WHITE);
            g.fillRect(0, 0, 80, 30);
            g.setColor(Color.BLACK);
            g.setFont(new Font("Arial", Font.BOLD, 18));
            g.drawString(text, 15, 22);
            g.dispose();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(img, "png", baos);
            return baos.toByteArray();
        } catch (Exception e) {
            // Fallback: return a minimal 1x1 PNG
            return new byte[]{
                (byte)0x89,0x50,0x4E,0x47,0x0D,0x0A,0x1A,0x0A,
                0x00,0x00,0x00,0x0D,0x49,0x48,0x44,0x52,
                0x00,0x00,0x00,0x01,0x00,0x00,0x00,0x01,
                0x08,0x02,0x00,0x00,0x00,(byte)0x90,0x77,0x53,
                (byte)0xDE,0x00,0x00,0x00,0x0C,0x49,0x44,0x41,
                0x54,0x08,(byte)0xD7,0x63,(byte)0xF8,(byte)0xCF,(byte)0xC0,0x00,
                0x00,0x00,0x02,0x00,0x01,(byte)0xE2,0x21,(byte)0xBC,
                0x33,0x00,0x00,0x00,0x00,0x49,0x45,0x4E,
                0x44,(byte)0xAE,0x42,0x60,(byte)0x82
            };
        }
    }

    // ========== PROTOCOL HELPERS ==========

    /** Send an empty response (no tags) */
    private static void send(DataOutputStream dos, int cmd) throws IOException {
        dos.writeShort(0);      // 0 sub-packets
        dos.writeInt(0);        // 0 payload length
        dos.writeByte(cmd);     // command
        dos.flush();
    }

    /** Send response with a single tag */
    private static void send(DataOutputStream dos, int cmd, byte[] singleTag) throws IOException {
        dos.writeShort(1);
        dos.writeInt(singleTag.length);
        dos.writeByte(cmd);
        dos.write(singleTag);
        dos.flush();
    }

    /** Send response with pre-built payload */
    private static void sendPacket(DataOutputStream dos, int cmd, byte[] payload, int subCount) throws IOException {
        dos.writeShort(subCount);
        dos.writeInt(payload.length);
        dos.writeByte(cmd);
        dos.write(payload);
        dos.flush();
    }

    /** Build a single TLV tag as byte array */
    private static byte[] tag(int tagId, byte[] data) {
        byte[] out = new byte[1 + 4 + data.length];
        out[0] = (byte)(tagId & 0xFF);
        out[1] = (byte)((data.length >> 24) & 0xFF);
        out[2] = (byte)((data.length >> 16) & 0xFF);
        out[3] = (byte)((data.length >> 8) & 0xFF);
        out[4] = (byte)(data.length & 0xFF);
        System.arraycopy(data, 0, out, 5, data.length);
        return out;
    }

    private static byte[] tag(int tagId, String value) {
        return tag(tagId, value.getBytes(StandardCharsets.UTF_8));
    }

    /** Write a tag to a stream */
    private static void wTag(OutputStream os, int tagId, byte[] data) throws IOException {
        os.write(tagId & 0xFF);
        os.write((data.length >> 24) & 0xFF);
        os.write((data.length >> 16) & 0xFF);
        os.write((data.length >> 8) & 0xFF);
        os.write(data.length & 0xFF);
        os.write(data);
    }

    private static void wTag(OutputStream os, int tagId, String value) throws IOException {
        wTag(os, tagId, value.getBytes(StandardCharsets.UTF_8));
    }

    private static void wTag(OutputStream os, int tagId, int value) throws IOException {
        wTag(os, tagId, ByteBuffer.allocate(4).putInt(value).array());
    }

    private static void wTag(OutputStream os, int tagId, long value) throws IOException {
        wTag(os, tagId, ByteBuffer.allocate(8).putLong(value).array());
    }

    private static void wTag(OutputStream os, int tagId, byte value) throws IOException {
        wTag(os, tagId, new byte[]{value});
    }

    // ========== PARSING HELPERS ==========

    private static Map<Integer, byte[]> parseTLV(byte[] data) {
        Map<Integer, byte[]> map = new LinkedHashMap<>();
        ByteBuffer bb = ByteBuffer.wrap(data);
        while (bb.hasRemaining()) {
            if (bb.remaining() < 5) break;
            int t = bb.get() & 0xFF;
            int len = bb.getInt();
            if (bb.remaining() < len || len < 0) break;
            byte[] val = new byte[len];
            bb.get(val);
            map.put(t, val);
        }
        return map;
    }

    private static String tagStr(Map<Integer, byte[]> tags, int id) {
        byte[] v = tags.get(id);
        return v != null ? new String(v, StandardCharsets.UTF_8) : null;
    }

    private static int tagInt(Map<Integer, byte[]> tags, int id, int def) {
        byte[] v = tags.get(id);
        if (v == null || v.length < 4) return def;
        return ByteBuffer.wrap(v).getInt();
    }

    private static int tagByte(Map<Integer, byte[]> tags, int id, int def) {
        byte[] v = tags.get(id);
        if (v == null || v.length < 1) return def;
        return v[0] & 0xFF;
    }

    private static String fmt(byte[] data) {
        if (data.length <= 32) {
            // Try as string
            boolean printable = true;
            for (byte b : data) {
                if (b < 0x20 && b != 0x0A && b != 0x0D) { printable = false; break; }
            }
            if (printable && data.length > 0)
                return "\"" + new String(data, StandardCharsets.UTF_8) + "\"";
        }
        // Hex dump
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < Math.min(data.length, 16); i++) {
            if (i > 0) sb.append(' ');
            sb.append(String.format("%02X", data[i]));
        }
        if (data.length > 16) sb.append("...");
        sb.append("] (").append(data.length).append("b)");
        return sb.toString();
    }
}
