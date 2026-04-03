package l12sq.server.runtime;

import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;
import l12sq.server.net.TlvCodec;

public final class TagPacketBuilder {
    private final ByteArrayOutputStream payload = new ByteArrayOutputStream();
    private int count;

    public TagPacketBuilder() {
    }

    public void stringTag(int tagId, String value) {
        TlvCodec.writeTag(payload, tagId, value);
        count++;
    }

    public void byteTag(int tagId, int value) {
        TlvCodec.writeTag(payload, tagId, new byte[]{(byte) (value & 0xFF)});
        count++;
    }

    public void intTag(int tagId, int value) {
        TlvCodec.writeTag(payload, tagId, intBytes(value));
        count++;
    }

    public void longTag(int tagId, long value) {
        TlvCodec.writeTag(payload, tagId, longBytes(value));
        count++;
    }

    public void rawTag(int tagId, byte[] value) {
        TlvCodec.writeTag(payload, tagId, value);
        count++;
    }

    public byte[] payload() {
        return payload.toByteArray();
    }

    public int count() {
        return count;
    }

    public static byte[] intBytes(int value) {
        return ByteBuffer.allocate(4).putInt(value).array();
    }

    public static byte[] longBytes(long value) {
        return ByteBuffer.allocate(8).putLong(value).array();
    }

    public static byte[] intArrayBytes(int... values) {
        ByteBuffer buffer = ByteBuffer.allocate(values.length * 4);
        for (int value : values) {
            buffer.putInt(value);
        }
        return buffer.array();
    }
}
