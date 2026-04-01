import java.io.*;
import java.net.*;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;

/**
 * Loan 12 Su Quan - Offline Server v2.0
 * JSON-backed account & character storage.
 * Auth Port: 7236 | Game Port: 7238
 */
public class OfflineServer {
    private static final int AUTH_PORT = 7236;
    private static final int GAME_PORT = 7238;

    private static final char[] HEX_ARRAY = "0123456789ABCDEF".toCharArray();

    // JSON data files
    private static final File DATA_DIR   = new File("data");
    private static final File CHARS_DIR  = new File(DATA_DIR, "chars");
    private static final File ACCOUNTS_F = new File(DATA_DIR, "accounts.json");

    // In-memory maps loaded from JSON
    // accounts: { "username" -> "password_plain" }
    private static final java.util.concurrent.ConcurrentHashMap<String, String> ACCOUNTS = new java.util.concurrent.ConcurrentHashMap<>();
    // charExists: set of nicks that have a character saved
    private static final java.util.Set<String> CHAR_SAVED = Collections.synchronizedSet(new HashSet<>());

    // ===== MAIN =====
    public static void main(String[] args) {
        DATA_DIR.mkdirs();
        CHARS_DIR.mkdirs();
        loadAccounts();
        System.out.println("=============================================");
        System.out.println("  LOAN 12 SU QUAN - OFFLINE SERVER v2.0");
        System.out.println("  Auth Port : " + AUTH_PORT);
        System.out.println("  Game Port : " + GAME_PORT);
        System.out.println("  Accounts  : " + ACCOUNTS.size());
        System.out.println("=============================================");
        new Thread(() -> listen(AUTH_PORT, "AUTH"), "auth-listener").start();
        new Thread(() -> listen(GAME_PORT, "GAME"), "game-listener").start();
    }

    // ===== LISTEN =====
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

    // ===== CONNECTION STATE (per-thread via thread-local or closure) =====
    // We track the logged-in nick per connection via a simple wrapper
    private static void handleClient(Socket socket, String serverType) {
        final String[] sessionNick = {null}; // mutable closure trick
        try (DataInputStream dis = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
             DataOutputStream dos = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()))) {

            System.out.println("\n[" + serverType + "] Client connected from " + socket.getInetAddress());

            while (true) {
                int totalLen = dis.readInt();
                int type = dis.readUnsignedByte();
                byte[] magic = new byte[4];
                dis.readFully(magic);
                int cmd = dis.readUnsignedByte();

                int payloadLen = totalLen - 6;
                byte[] payload = new byte[Math.max(0, payloadLen)];
                if (payloadLen > 0) dis.readFully(payload);

                Map<Integer, byte[]> tags = parseTLV(payload);

                System.out.printf("[%s] << CMD %d (0x%02X) len=%d tags=%d%n",
                        serverType, cmd, cmd, totalLen, tags.size());
                for (var e : tags.entrySet()) {
                    System.out.printf("       Tag %d = %s%n", e.getKey(), fmt(e.getValue()));
                }

                processCmd(dos, cmd, tags, serverType, sessionNick);
            }
        } catch (EOFException e) {
            System.out.println("[" + serverType + "] Client disconnected.");
        } catch (IOException e) {
            System.out.println("[" + serverType + "] Error: " + e.getMessage());
        }
    }

    // ===== DISPATCHER =====
    private static void processCmd(DataOutputStream dos, int cmd, Map<Integer, byte[]> tags,
                                   String sType, String[] sessionNick) throws IOException {
        switch (cmd) {
            case 5:   handleVersion(dos); break;
            case 2:   handleLoginReq(dos); break;
            case 3:   handleLoginReq(dos); break;
            case 4:   handleAuth(dos, tags, sType, sessionNick); break;
            case 1:   handleKeepAlive(dos); break;
            case 9:   handleProfileSync(dos, tags, sessionNick); break;
            case 10:  handleStatUpdate(dos, tags, sessionNick); break;
            case 11:  handleRoomList(dos, tags); break;
            case 15:  handleMapInfo(dos, tags); break;
            case 6:   handleResource(dos, tags); break;
            case 29:  handlePlayerInfo(dos, tags); break;
            case 30:  handleClassFlow(dos, tags, sessionNick); break;
            case 37:  handleFriendList(dos, tags); break;
            case 51:  handleInventory(dos, tags); break;
            case 16:  handleChat(dos, tags); break;
            case 25:  handleMsg(dos, tags); break;
            case 42:  handleBootstrapRequest(dos, tags, sessionNick); break;
            case 43:  send(dos, 43); break;
            case 84:  handleEquipSelect(dos, tags); break;
            case 96:  handleEquipView(dos, tags); break;
            case 97:  handleEquipUpgrade(dos, tags); break;
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

    // ========== AUTH HANDLERS ==========

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

    private static void handleAuth(DataOutputStream dos, Map<Integer, byte[]> tags,
                                   String sType, String[] sessionNick) throws IOException {
        String nick = tagStr(tags, 9);
        String pass = tagStr(tags, 10); // password tag (tag 10 on auth submission)

        System.out.println("       >> Auth submit from: " + nick);

        if (sType.equals("AUTH")) {
            // STRICT OFFLINE AUTH: Account MUST exist in accounts.json
            if (nick != null && ACCOUNTS.containsKey(nick.toLowerCase())) {
                String savedPassHex = ACCOUNTS.get(nick.toLowerCase());
                String loginPassHex = tagHex(tags, 10);
                
                System.out.println("       >> [AUTH] Nick: " + nick);
                System.out.println("       >> [AUTH] Saved Hash: " + savedPassHex);
                System.out.println("       >> [AUTH] Login Hash: " + loginPassHex);

                // For offline, we accept if password matches stored hash (registration hash)
                // OR we allow bypass if we haven't implemented the salt algo yet, 
                // but for now let's try to match exactly (some clients send fixed hash in registration/login)
                if (loginPassHex == null || loginPassHex.equalsIgnoreCase(savedPassHex) || savedPassHex.equals("OFFLINE")) {
                    sessionNick[0] = nick; // PRESERVE CASE
                    send(dos, 1);  // auth success
                    send(dos, 2, tag(3, "127.0.0.1")); // redirect to game port
                    System.out.println("       >> AUTH SUCCESS for " + nick);
                } else {
                    // Password mismatch
                    ByteArrayOutputStream tlv = new ByteArrayOutputStream();
                    wTag(tlv, 1, "Sai mat khau! Vui long kiem tra lai.");
                    sendPacket(dos, 0, tlv.toByteArray(), 1);
                    System.out.println("       >> AUTH FAILED: Wrong password for " + nick);
                }
            } else {
                // Account doesn't exist
                ByteArrayOutputStream tlv = new ByteArrayOutputStream();
                wTag(tlv, 1, "Tai khoan '" + nick + "' chua duoc dang ky.");
                sendPacket(dos, 0, tlv.toByteArray(), 1);
                System.out.println("       >> AUTH FAILED: Account not found: " + nick);
            }
        } else {
            // Game port auth phase 2 - always allow if nickname is known
            if (nick != null) sessionNick[0] = nick.toLowerCase();
            send(dos, 4);
            System.out.println("       >> GAME AUTH OK, session=" + sessionNick[0]);
        }
    }

    private static void handleKeepAlive(DataOutputStream dos) throws IOException {
        send(dos, 1);
    }

    // ========== REGISTRATION ==========

    /** CMD 130 - Send captcha image + salt */
    private static void handleRegCaptcha(DataOutputStream dos) throws IOException {
        System.out.println("       >> Sending registration captcha (1234)");
        byte[] captchaImg = generateCaptchaPng("1234");
        byte[] salt = new byte[16];
        new Random().nextBytes(salt);
        ByteArrayOutputStream tlv = new ByteArrayOutputStream();
        int count = 0;
        wTag(tlv, 176, captchaImg); count++;
        wTag(tlv, 2, salt);         count++;
        sendPacket(dos, 130, tlv.toByteArray(), count);
        System.out.println("       >> Captcha sent (" + captchaImg.length + " bytes)");
    }

    /** CMD 131 - Registration submit -> save account to JSON */
    private static void handleRegSubmit(DataOutputStream dos, Map<Integer, byte[]> tags) throws IOException {
        String nick = tagStr(tags, 9);
        String passHex = tagHex(tags, 10); // Capture pass as HEX to prevent corruption

        System.out.println("       >> Registration submit: nick=" + nick + " passHex=" + passHex);

        if (nick == null || nick.isEmpty()) {
            ByteArrayOutputStream tlv = new ByteArrayOutputStream();
            wTag(tlv, 1, "Ten dang nhap khong hop le."); 
            sendPacket(dos, 131, tlv.toByteArray(), 1);
            return;
        }

        String key = nick.toLowerCase();
        // Save account
        ACCOUNTS.put(key, passHex != null ? passHex : "OFFLINE");
        saveAccounts();

        ByteArrayOutputStream tlv = new ByteArrayOutputStream();
        int count = 0;
        wTag(tlv, 1, "Dang ky thanh cong! Hay dang nhap."); count++;
        sendPacket(dos, 131, tlv.toByteArray(), count);
        System.out.println("       >> Registration SUCCESS for: " + nick);
    }

    // ========== PROFILE / CHARACTER ==========

    private static void handleProfileSync(DataOutputStream dos, Map<Integer, byte[]> tags, String[] sessionNick) throws IOException {
        String nick = tagStr(tags, 9);
        if (nick == null) nick = sessionNick[0];
        if (nick == null) nick = "guest";
        int profileType = tagByte(tags, 134, -1);
        System.out.println("       >> Profile sync for: " + nick + " type=" + profileType);
        if (profileType > 0) {
            File charFile = new File(CHARS_DIR, nick.toLowerCase() + ".json");
            if (charFile.exists()) {
                sendFullProfileFromData(dos, nick, loadJson(charFile));
            } else {
                sendEmptyProfile(dos, nick);
            }
        } else {
            int bitmask = tagInt(tags, 23, 0);
            sendPartialProfile(dos, nick, bitmask);
        }
    }

    /** CMD 42 - Login bootstrap after CMD 30. */
    private static void handleBootstrapRequest(DataOutputStream dos, Map<Integer, byte[]> tags, String[] sessionNick) throws IOException {
        String nick = tagStr(tags, 9);
        if (nick == null) nick = sessionNick[0];
        if (nick == null) nick = "guest";
        System.out.println("       >> Bootstrap request (CMD 42) for: " + nick);

        File charFile = new File(CHARS_DIR, nick.toLowerCase() + ".json");
        if (charFile.exists()) {
            sendBootstrapData(dos, true);
        } else {
            System.out.println("       >> No character yet, bootstrap stays in create-char flow");
            sendBootstrapData(dos, false);
        }
    }

    /** CMD 30 - Existing character: class bootstrap. New character: save selection first. */
    private static void handleClassFlow(DataOutputStream dos, Map<Integer, byte[]> tags, String[] sessionNick) throws IOException {
        int classId = tagByte(tags, 15, 1);
        String nick = sessionNick[0] != null ? sessionNick[0] : "guest";
        File charFile = new File(CHARS_DIR, nick.toLowerCase() + ".json");

        if (charFile.exists()) {
            System.out.println("       >> Class bootstrap for existing char: classId=" + classId + " nick=" + nick);
            sendClassBootstrap(dos, classId);
            return;
        }

        if (classId <= 0) {
            System.out.println("       >> Create-char screen bootstrap only for nick=" + nick + " classId=" + classId);
            sendClassBootstrap(dos, classId);
            return;
        }

        System.out.println("       >> Character preview only: classId=" + classId + " nick=" + nick);

        // Build character data
        Map<String, String> charData = new LinkedHashMap<>();
        charData.put("nick", nick);
        charData.put("class", String.valueOf(classId));
        charData.put("level", "1");
        charData.put("hp", "1000");
        charData.put("hpMax", "1000");
        charData.put("mp", "500");
        charData.put("mpMax", "500");
        charData.put("str", "10");
        charData.put("agi", "10");
        charData.put("mag", "10");
        charData.put("vit", "10");
        charData.put("gold", "10000");
        charData.put("honor", "0");
        charData.put("attack", "50");
        charData.put("defense", "30");
        charData.put("speed", "1000");

        // Do not persist yet. The client sends bootstrap/class packets before the player
        // actually confirms entering the world, so we only return a preview profile here.
        sendClassBootstrap(dos, classId);
        sendFullProfileFromData(dos, nick, charData);
    }

    /** CMD 15 & CMD 11 - Send map and room info to enter the world */
    private static void sendWorldEntry(DataOutputStream dos) throws IOException {
        System.out.println("       >> Sending World Entry (Map & Room)");
        // Map Info (CMD 15)
        ByteArrayOutputStream mTLV = new ByteArrayOutputStream();
        // M99 is handled specially by the client and avoids the RMS map-cache path
        // that currently crashes on our minimal offline packets.
        wTag(mTLV, 20, "M99");
        wTag(mTLV, 12, (byte) 0);
        sendPacket(dos, 15, mTLV.toByteArray(), 2);

        // Room List (CMD 11)
        ByteArrayOutputStream rTLV = new ByteArrayOutputStream();
        wTag(rTLV, 20, "M99");
        wTag(rTLV, 12, (byte) 0);
        sendPacket(dos, 11, rTLV.toByteArray(), 2);
    }

    private static void sendClassBootstrap(DataOutputStream dos, int classId) throws IOException {
        System.out.println("       >> Sending class bootstrap (CMD 30), classId=" + classId);
        sendPacket(dos, 30, new byte[0], 0);
    }

    private static void sendBootstrapData(DataOutputStream dos, boolean enterWorld) throws IOException {
        ByteArrayOutputStream tlv = new ByteArrayOutputStream();
        int count = 0;
        wTag(tlv, 86, 0);  count++;
        wTag(tlv, 145, 0); count++;
        sendPacket(dos, 42, tlv.toByteArray(), count);
        System.out.println("       >> Bootstrap data sent (CMD 42)");
        if (enterWorld) {
            sendWorldEntry(dos);
        }
    }

    // ========== PROFILE BUILDERS ==========

    private static void sendEmptyProfile(DataOutputStream dos, String nick) throws IOException {
        System.out.println("       >> Sending EMPTY profile (no char yet) for " + nick);
        ByteArrayOutputStream tlv = new ByteArrayOutputStream(512);
        int count = 0;
        wTag(tlv, 134, (byte) 1); count++;
        wTag(tlv, 9, nick);       count++;
        wTag(tlv, 26, "Tan Thu"); count++;
        // The client still builds a full lf/gr.j object for the create/start screen,
        // so we must provide a valid default class and base stats even before saving a char file.
        wTag(tlv, 15, (byte) 1);  count++;
        wTag(tlv, 16, (byte) 0);  count++;
        wTag(tlv, 27, 1);         count++;
        wTag(tlv, 17, 1000);      count++;
        wTag(tlv, 47, 1000);      count++;
        wTag(tlv, 18, 500);       count++;
        wTag(tlv, 48, 500);       count++;
        wTag(tlv, 118, 10);       count++;
        wTag(tlv, 119, 10);       count++;
        wTag(tlv, 120, 10);       count++;
        wTag(tlv, 121, 10);       count++;
        wTag(tlv, 196, 0);        count++;
        wTag(tlv, 197, 0);        count++;
        wTag(tlv, 198, 0);        count++;
        wTag(tlv, 199, 0);        count++;
        wTag(tlv, 116, 0);        count++;
        wTag(tlv, 115, 100);      count++;
        wTag(tlv, 42, 50);        count++;
        wTag(tlv, 43, 30);        count++;
        wTag(tlv, 99, 1000);      count++;
        wTag(tlv, 53, 0);         count++;
        wTag(tlv, 76, 0);         count++;
        wTag(tlv, 73, 0);         count++;
        wTag(tlv, 74, 0);         count++;
        wTag(tlv, 108, 5);        count++;
        wTag(tlv, 109, 5);        count++;
        wTag(tlv, 151, "");       count++;
        wTag(tlv, 160, 0);        count++;
        wTag(tlv, 165, (byte) 0); count++;
        wTag(tlv, 166, (byte) 0); count++;
        wTag(tlv, 132, 0L);       count++;
        sendPacket(dos, 9, tlv.toByteArray(), count);
    }

    private static void sendFullProfileFromData(DataOutputStream dos, String nick, Map<String, String> d) throws IOException {
        System.out.println("       >> Sending profile from JSON data for " + nick);
        ByteArrayOutputStream tlv = new ByteArrayOutputStream(512);
        int count = 0;

        int classId = parseInt(d, "class", 1);
        int level   = parseInt(d, "level", 1);
        int hp      = parseInt(d, "hp", 1000);
        int hpMax   = parseInt(d, "hpMax", 1000);
        int mp      = parseInt(d, "mp", 500);
        int mpMax   = parseInt(d, "mpMax", 500);
        int str     = parseInt(d, "str", 10);
        int agi     = parseInt(d, "agi", 10);
        int mag     = parseInt(d, "mag", 10);
        int vit     = parseInt(d, "vit", 10);
        int gold    = parseInt(d, "gold", 10000);
        int honor   = parseInt(d, "honor", 0);
        int atk     = parseInt(d, "attack", 50);
        int def     = parseInt(d, "defense", 30);
        int spd     = parseInt(d, "speed", 1000);

        wTag(tlv, 134, (byte) 1);   count++;
        wTag(tlv, 9,   nick);       count++;
        wTag(tlv, 26,  "Su Quan");  count++;
        wTag(tlv, 15,  (byte) classId); count++;
        wTag(tlv, 16,  (byte) 0);   count++;
        wTag(tlv, 27,  level);      count++;
        wTag(tlv, 17,  hp);         count++;
        wTag(tlv, 47,  hpMax);      count++;
        wTag(tlv, 18,  mp);         count++;
        wTag(tlv, 48,  mpMax);      count++;
        wTag(tlv, 118, str);        count++;
        wTag(tlv, 119, agi);        count++;
        wTag(tlv, 120, mag);        count++;
        wTag(tlv, 121, vit);        count++;
        wTag(tlv, 196, 0);          count++;
        wTag(tlv, 197, 0);          count++;
        wTag(tlv, 198, 0);          count++;
        wTag(tlv, 199, 0);          count++;
        wTag(tlv, 116, 0);          count++;
        wTag(tlv, 115, 100);        count++;
        wTag(tlv, 42,  atk);        count++;
        wTag(tlv, 43,  def);        count++;
        wTag(tlv, 99,  spd);        count++;
        wTag(tlv, 53,  0);          count++;
        wTag(tlv, 76,  0);          count++;
        wTag(tlv, 73,  0);          count++;
        wTag(tlv, 74,  0);          count++;
        wTag(tlv, 108, 5);          count++;
        wTag(tlv, 109, 5);          count++;
        wTag(tlv, 151, "");         count++;
        wTag(tlv, 160, honor);      count++;
        wTag(tlv, 165, (byte) 0);   count++;
        wTag(tlv, 166, (byte) 0);   count++;
        wTag(tlv, 132, (long) gold); count++;

        sendPacket(dos, 9, tlv.toByteArray(), count);
        System.out.println("       >> Profile sent (" + count + " tags) level=" + level + " class=" + classId);
    }

    private static void sendFullProfile(DataOutputStream dos, String nick) throws IOException {
        // Used for generic/non-nicked profile requests
        File charFile = new File(CHARS_DIR, nick.toLowerCase() + ".json");
        if (charFile.exists()) {
            sendFullProfileFromData(dos, nick, loadJson(charFile));
        } else {
            // Send a generic max-stat profile to keep the game happy
            System.out.println("       >> Sending GENERIC profile for " + nick);
            ByteArrayOutputStream tlv = new ByteArrayOutputStream(512);
            int count = 0;
            wTag(tlv, 134, (byte) 1);    count++;
            wTag(tlv, 9,   nick);        count++;
            wTag(tlv, 26,  "Su Quan");   count++;
            wTag(tlv, 15,  (byte) 1);    count++;
            wTag(tlv, 16,  (byte) 0);    count++;
            wTag(tlv, 27,  50);          count++;
            wTag(tlv, 17,  5000);        count++;
            wTag(tlv, 47,  5000);        count++;
            wTag(tlv, 18,  3000);        count++;
            wTag(tlv, 48,  3000);        count++;
            wTag(tlv, 118, 100);         count++;
            wTag(tlv, 119, 100);         count++;
            wTag(tlv, 120, 100);         count++;
            wTag(tlv, 121, 100);         count++;
            wTag(tlv, 196, 50);          count++;
            wTag(tlv, 197, 50);          count++;
            wTag(tlv, 198, 50);          count++;
            wTag(tlv, 199, 50);          count++;
            wTag(tlv, 116, 500);         count++;
            wTag(tlv, 115, 100);         count++;
            wTag(tlv, 42,  500);         count++;
            wTag(tlv, 43,  300);         count++;
            wTag(tlv, 99,  10000);       count++;
            wTag(tlv, 53,  100);         count++;
            wTag(tlv, 76,  50);          count++;
            wTag(tlv, 73,  50);          count++;
            wTag(tlv, 74,  50);          count++;
            wTag(tlv, 108, 10);          count++;
            wTag(tlv, 109, 10);          count++;
            wTag(tlv, 151, "Offline");   count++;
            wTag(tlv, 160, 1000);        count++;
            wTag(tlv, 165, (byte) 0);    count++;
            wTag(tlv, 166, (byte) 0);    count++;
            wTag(tlv, 132, 999999L);     count++;
            sendPacket(dos, 9, tlv.toByteArray(), count);
            System.out.println("       >> Generic profile sent (" + count + " tags)");
        }
    }

    private static void sendPartialProfile(DataOutputStream dos, String nick, int bitmask) throws IOException {
        ByteArrayOutputStream tlv = new ByteArrayOutputStream(256);
        int count = 0;
        wTag(tlv, 23, bitmask > 0 ? bitmask : 0xFF); count++;
        wTag(tlv, 9,  nick); count++;
        wTag(tlv, 27, 1);    count++;
        wTag(tlv, 17, 1000); count++; wTag(tlv, 47, 1000); count++;
        wTag(tlv, 18, 500);  count++; wTag(tlv, 48, 500);  count++;
        wTag(tlv, 42, 50);   count++; wTag(tlv, 43, 30);   count++;
        sendPacket(dos, 9, tlv.toByteArray(), count);
    }

    // ========== OTHER GAME HANDLERS ==========

    private static void handleStatUpdate(DataOutputStream dos, Map<Integer, byte[]> tags, String[] sessionNick) throws IOException {
        String nick = sessionNick[0] != null ? sessionNick[0] : "guest";
        System.out.println("       >> Stat update acknowledged");
        sendFullProfile(dos, nick);
        send(dos, 27);
    }

    private static void handleRoomList(DataOutputStream dos, Map<Integer, byte[]> tags) throws IOException {
        String zone = tagStr(tags, 20);
        System.out.println("       >> Room list for zone: " + zone);
        ByteArrayOutputStream tlv = new ByteArrayOutputStream();
        int count = 0;
        wTag(tlv, 20, "M99"); count++;
        wTag(tlv, 12, (byte) 0); count++;
        sendPacket(dos, 11, tlv.toByteArray(), count);
    }

    private static void handleMapInfo(DataOutputStream dos, Map<Integer, byte[]> tags) throws IOException {
        System.out.println("       >> Map info request");
        ByteArrayOutputStream tlv = new ByteArrayOutputStream();
        int count = 0;
        wTag(tlv, 20, "M99"); count++;
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
        wTag(tlv, 9, nick != null ? nick : "guest"); count++;
        wTag(tlv, 20, "Online"); count++;
        wTag(tlv, 21, 0); count++;
        sendPacket(dos, 29, tlv.toByteArray(), count);
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

    // ========== CAPTCHA ==========

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

    // ========== JSON PERSISTENCE (no external libs) ==========

    private static void loadAccounts() {
        if (!ACCOUNTS_F.exists()) return;
        try {
            Map<String, String> data = loadJson(ACCOUNTS_F);
            ACCOUNTS.putAll(data);
            System.out.println("[DB] Loaded " + ACCOUNTS.size() + " accounts.");
        } catch (Exception e) {
            System.err.println("[DB] Failed to load accounts: " + e.getMessage());
        }
    }

    private static synchronized void saveAccounts() {
        try {
            saveJson(ACCOUNTS_F, new LinkedHashMap<>(ACCOUNTS));
            System.out.println("[DB] Accounts saved (" + ACCOUNTS.size() + ")");
        } catch (Exception e) {
            System.err.println("[DB] Failed to save accounts: " + e.getMessage());
        }
    }

    /** Minimal JSON save: writes a flat {"key":"value",...} map */
    private static synchronized void saveJson(File file, Map<String, String> data) throws IOException {
        StringBuilder sb = new StringBuilder("{\n");
        int i = 0;
        for (Map.Entry<String, String> e : data.entrySet()) {
            sb.append("  \"").append(jsonEsc(e.getKey())).append("\": \"")
              .append(jsonEsc(e.getValue())).append("\"");
            if (++i < data.size()) sb.append(",");
            sb.append("\n");
        }
        sb.append("}");
        Files.writeString(file.toPath(), sb.toString(), StandardCharsets.UTF_8);
    }

    /** Minimal JSON load: reads a flat {"key":"value",...} map */
    private static Map<String, String> loadJson(File file) throws IOException {
        String raw = Files.readString(file.toPath(), StandardCharsets.UTF_8).trim();
        Map<String, String> map = new LinkedHashMap<>();
        // Strip outer { }
        if (raw.startsWith("{")) raw = raw.substring(1);
        if (raw.endsWith("}")) raw = raw.substring(0, raw.length() - 1);
        // Simple key:value pair splitter
        int pos = 0;
        while (pos < raw.length()) {
            // find opening quote of key
            int ks = raw.indexOf('"', pos);
            if (ks < 0) break;
            int ke = raw.indexOf('"', ks + 1);
            if (ke < 0) break;
            String key = raw.substring(ks + 1, ke);
            // find colon then opening quote of value
            int colon = raw.indexOf(':', ke + 1);
            if (colon < 0) break;
            int vs = raw.indexOf('"', colon + 1);
            if (vs < 0) break;
            int ve = vs + 1;
            StringBuilder valSb = new StringBuilder();
            while (ve < raw.length()) {
                char c = raw.charAt(ve);
                if (c == '\\' && ve + 1 < raw.length()) {
                    valSb.append(raw.charAt(ve + 1));
                    ve += 2;
                } else if (c == '"') {
                    break;
                } else {
                    valSb.append(c);
                    ve++;
                }
            }
            map.put(key, valSb.toString());
            pos = ve + 1;
        }
        return map;
    }

    private static String jsonEsc(String s) {
        return s.replace("\\", "\\\\").replace("\"", "\\\"");
    }

    private static int parseInt(Map<String, String> d, String key, int def) {
        try { return Integer.parseInt(d.getOrDefault(key, String.valueOf(def))); }
        catch (NumberFormatException e) { return def; }
    }

    // ========== PROTOCOL HELPERS ==========

    private static void send(DataOutputStream dos, int cmd) throws IOException {
        dos.writeShort(0);
        dos.writeInt(0);
        dos.writeByte(cmd);
        dos.flush();
    }

    private static void send(DataOutputStream dos, int cmd, byte[] singleTag) throws IOException {
        dos.writeShort(1);
        dos.writeInt(singleTag.length);
        dos.writeByte(cmd);
        dos.write(singleTag);
        dos.flush();
    }

    private static void sendPacket(DataOutputStream dos, int cmd, byte[] payload, int subCount) throws IOException {
        dos.writeShort(subCount);
        dos.writeInt(payload.length);
        dos.writeByte(cmd);
        dos.write(payload);
        dos.flush();
    }

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

    private static String tagHex(Map<Integer, byte[]> tags, int id) {
        byte[] v = tags.get(id);
        return v != null ? bytesToHex(v) : null;
    }

    private static String bytesToHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        for (int j = 0; j < bytes.length; j++) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = HEX_ARRAY[v >>> 4];
            hexChars[j * 2 + 1] = HEX_ARRAY[v & 0x0F];
        }
        return new String(hexChars);
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
            boolean printable = true;
            for (byte b : data) {
                if (b < 0x20 && b != 0x0A && b != 0x0D) { printable = false; break; }
            }
            if (printable && data.length > 0)
                return "\"" + new String(data, StandardCharsets.UTF_8) + "\"";
        }
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
