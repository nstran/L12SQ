package l12sq.server.net;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.LinkedHashMap;
import java.util.Map;

public final class TlvCodec {
    private TlvCodec() {
    }

    public static PacketRequest readAuthRequest(DataInputStream dis) throws IOException {
        try {
            int packetType = dis.readUnsignedByte();
            dis.readNBytes(4);
            int payloadLength = dis.readInt();
            int command = dis.readUnsignedByte();
            byte[] payload = dis.readNBytes(Math.max(0, payloadLength));
            return new PacketRequest(packetType, payloadLength, command, payload, parseTags(payload));
        } catch (EOFException eof) {
            throw eof;
        }
    }

    public static PacketRequest readGameRequest(DataInputStream dis) throws IOException {
        try {
            int totalLength = dis.readInt();
            int packetType = dis.readUnsignedByte();
            dis.readNBytes(4);
            int command = dis.readUnsignedByte();
            int payloadLength = Math.max(0, totalLength - 6);
            byte[] payload = dis.readNBytes(payloadLength);
            return new PacketRequest(packetType, payloadLength, command, payload, parseTags(payload));
        } catch (EOFException eof) {
            throw eof;
        }
    }

    public static Map<Integer, byte[]> parseTags(byte[] payload) {
        Map<Integer, byte[]> tags = new LinkedHashMap<>();
        ByteBuffer buffer = ByteBuffer.wrap(payload);
        while (buffer.remaining() >= 5) {
            int tagId = buffer.get() & 0xFF;
            int length = buffer.getInt();
            if (length < 0 || buffer.remaining() < length) {
                break;
            }
            byte[] value = new byte[length];
            buffer.get(value);
            tags.put(tagId, value);
        }
        return tags;
    }

    public static void sendEmpty(DataOutputStream dos, int command) throws IOException {
        dos.writeShort(0);
        dos.writeInt(0);
        dos.writeByte(command);
        dos.flush();
    }

    public static void sendSingleTag(DataOutputStream dos, int command, byte[] tag) throws IOException {
        dos.writeShort(1);
        dos.writeInt(tag.length);
        dos.writeByte(command);
        dos.write(tag);
        dos.flush();
    }

    public static void sendPacket(DataOutputStream dos, int command, byte[] payload, int subCount) throws IOException {
        dos.writeShort(subCount);
        dos.writeInt(payload.length);
        dos.writeByte(command);
        dos.write(payload);
        dos.flush();
    }

    public static byte[] makeTag(int tagId, byte[] data) {
        byte[] out = new byte[1 + 4 + data.length];
        out[0] = (byte) (tagId & 0xFF);
        out[1] = (byte) ((data.length >> 24) & 0xFF);
        out[2] = (byte) ((data.length >> 16) & 0xFF);
        out[3] = (byte) ((data.length >> 8) & 0xFF);
        out[4] = (byte) (data.length & 0xFF);
        System.arraycopy(data, 0, out, 5, data.length);
        return out;
    }

    public static byte[] makeTag(int tagId, String value) {
        return makeTag(tagId, value.getBytes(StandardCharsets.UTF_8));
    }

    public static void writeTag(ByteArrayOutputStream output, int tagId, byte[] data) {
        output.write(tagId & 0xFF);
        output.write((data.length >> 24) & 0xFF);
        output.write((data.length >> 16) & 0xFF);
        output.write((data.length >> 8) & 0xFF);
        output.write(data.length & 0xFF);
        output.writeBytes(data);
    }

    public static void writeTag(ByteArrayOutputStream output, int tagId, String value) {
        writeTag(output, tagId, value.getBytes(StandardCharsets.UTF_8));
    }

    public static byte[] randomSalt(int size) {
        byte[] salt = new byte[size];
        new SecureRandom().nextBytes(salt);
        return salt;
    }

    public static String tagString(Map<Integer, byte[]> tags, int tagId) {
        byte[] value = tags.get(tagId);
        return value == null ? null : new String(value, StandardCharsets.UTF_8);
    }

    public static String tagHex(Map<Integer, byte[]> tags, int tagId) {
        byte[] value = tags.get(tagId);
        if (value == null) {
            return null;
        }
        StringBuilder builder = new StringBuilder(value.length * 2);
        for (byte item : value) {
            builder.append(String.format("%02X", item));
        }
        return builder.toString();
    }
}
