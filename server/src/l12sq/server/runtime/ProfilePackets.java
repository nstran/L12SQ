package l12sq.server.runtime;

import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import l12sq.server.storage.CharacterStore;

final class ProfilePackets {
    private ProfilePackets() {
    }

    static TagPacketBuilder buildCharacterProfile(CharacterStore.CharacterData characterData) {
        TagPacketBuilder profile = buildBaseProfile(characterData.username());
        profile.byteTag(15, characterData.element());
        profile.byteTag(16, characterData.gender());
        appendCmd9AppearanceEntry(profile, characterData.hairOptionId(), 0, characterData.hairOptionId() + 99, characterData.hairColorId());
        appendCmd9AppearanceEntry(profile, characterData.faceOptionId(), 1, characterData.faceOptionId() + 99, characterData.faceOptionId() + 99);
        appendCmd9AppearanceEntry(profile, characterData.skinOptionId(), 2, characterData.skinOptionId() + 99, characterData.skinColorId());
        return profile;
    }

    static TagPacketBuilder buildStartProfile(String username) {
        TagPacketBuilder profile = buildBaseProfile(username);
        appendCmd9AppearanceEntry(profile, 79800, 0, 79899, 79899);
        appendCmd9AppearanceEntry(profile, 79900, 1, 79999, 79999);
        appendCmd9AppearanceEntry(profile, 89900, 2, 89999, 89999);
        return profile;
    }

    static TagPacketBuilder buildNoCharacterProfile(String username) {
        TagPacketBuilder profile = new TagPacketBuilder();

        profile.intTag(23, 31);
        profile.stringTag(9, username);
        profile.byteTag(15, 0);
        profile.byteTag(16, 0);

        appendCmd9AppearanceEntry(profile, 79800, 0, 79899, 79899);
        appendCmd9AppearanceEntry(profile, 79900, 1, 79999, 79999);
        appendCmd9AppearanceEntry(profile, 89900, 2, 89999, 89999);

        profile.intTag(27, 1);
        profile.intTag(118, 10);
        profile.intTag(119, 10);
        profile.intTag(120, 10);
        profile.intTag(121, 10);
        profile.intTag(196, 0);
        profile.intTag(197, 0);
        profile.intTag(198, 0);
        profile.intTag(199, 0);
        profile.intTag(116, 0);
        profile.intTag(115, 100);

        profile.intTag(17, 1000);
        profile.intTag(47, 1000);
        profile.intTag(42, 50);
        profile.intTag(73, 0);
        profile.intTag(74, 0);
        profile.intTag(43, 30);
        profile.intTag(99, 1000);

        profile.intTag(53, 0);
        profile.intTag(76, 0);
        profile.intTag(108, 5);
        profile.intTag(109, 5);
        profile.intTag(160, 0);
        profile.stringTag(151, "");

        profile.byteTag(165, 0);
        profile.byteTag(166, 0);
        return profile;
    }

    static void appendCreateCharacterOptions(TagPacketBuilder options) {
        appendCmd8AppearanceEntry(options, 79800, 0, 0, "Nam Toc 1", 79899, "Mau Toc Nam 1");
        appendCmd8AppearanceEntry(options, 79900, 1, 0, "Nam Mat 1", 79999, "Nam Mat 1");
        appendCmd8AppearanceEntry(options, 89900, 2, 0, "Nam Da 1", 89999, "Mau Da Nam 1");
        appendCmd8AppearanceEntry(options, 79900, 0, 1, "Nu Toc 1", 79999, "Mau Toc Nu 1");
        appendCmd8AppearanceEntry(options, 79800, 1, 1, "Nu Mat 1", 79899, "Nu Mat 1");
        appendCmd8AppearanceEntry(options, 89900, 2, 1, "Nu Da 1", 89999, "Mau Da Nu 1");
    }

    static CreateCharacterSelection parseCreateCharacterSelection(byte[] payload) {
        List<TagEntry> entries = parseTagEntries(payload);
        int gender = 0;
        int element = 1;
        int[] optionIds = new int[3];
        int[] variantIds = new int[3];
        int optionIndex = 0;
        int variantIndex = 0;

        for (TagEntry entry : entries) {
            if (entry.tagId() == 16 && entry.value().length > 0) {
                gender = entry.value()[0] & 0xFF;
            } else if (entry.tagId() == 15 && entry.value().length > 0) {
                element = entry.value()[0] & 0xFF;
            } else if (entry.tagId() == 90 && entry.value().length >= 4 && optionIndex < optionIds.length) {
                optionIds[optionIndex++] = ByteBuffer.wrap(entry.value(), 0, 4).getInt();
            } else if (entry.tagId() == 96 && entry.value().length >= 4 && variantIndex < variantIds.length) {
                variantIds[variantIndex++] = ByteBuffer.wrap(entry.value(), 0, 4).getInt();
            }
        }

        return new CreateCharacterSelection(
                gender,
                element,
                fallback(optionIds, 0, 79800),
                fallback(variantIds, 0, 79899),
                fallback(optionIds, 1, gender == 0 ? 79900 : 79800),
                fallback(optionIds, 2, 89900),
                fallback(variantIds, 2, 89999));
    }

    private static TagPacketBuilder buildBaseProfile(String username) {
        TagPacketBuilder profile = new TagPacketBuilder();
        profile.byteTag(134, 1);
        profile.stringTag(9, username);
        profile.stringTag(26, "Tan Thu");
        profile.byteTag(15, 1);
        profile.byteTag(16, 0);
        profile.intTag(27, 1);
        profile.intTag(17, 1000);
        profile.intTag(47, 1000);
        profile.intTag(18, 500);
        profile.intTag(48, 500);
        profile.intTag(118, 10);
        profile.intTag(119, 10);
        profile.intTag(120, 10);
        profile.intTag(121, 10);
        profile.intTag(196, 0);
        profile.intTag(197, 0);
        profile.intTag(198, 0);
        profile.intTag(199, 0);
        profile.intTag(116, 0);
        profile.intTag(115, 100);
        profile.intTag(42, 50);
        profile.intTag(43, 30);
        profile.intTag(99, 1000);
        profile.intTag(53, 0);
        profile.intTag(76, 0);
        profile.intTag(73, 0);
        profile.intTag(74, 0);
        profile.intTag(108, 5);
        profile.intTag(109, 5);
        profile.stringTag(151, "");
        profile.intTag(160, 0);
        profile.byteTag(165, 0);
        profile.byteTag(166, 0);
        profile.longTag(132, 0L);
        return profile;
    }

    private static void appendCmd9AppearanceEntry(TagPacketBuilder builder, int optionId, int category, int spriteId, int alternateSpriteId) {
        builder.rawTag(90, TagPacketBuilder.intBytes(optionId));
        builder.byteTag(91, category);
        builder.intTag(93, spriteId);
        builder.rawTag(95, TagPacketBuilder.intArrayBytes(spriteId));
        builder.rawTag(96, TagPacketBuilder.intBytes(alternateSpriteId));
        builder.rawTag(98, TagPacketBuilder.intArrayBytes(alternateSpriteId));
    }

    private static void appendCmd8AppearanceEntry(
            TagPacketBuilder builder,
            int optionId,
            int category,
            int gender,
            String name,
            int spriteId,
            String spriteName
    ) {
        builder.rawTag(90, TagPacketBuilder.intBytes(optionId));
        builder.byteTag(91, category);
        builder.stringTag(92, name);
        builder.byteTag(16, gender);
        builder.intTag(93, spriteId);
        builder.stringTag(94, spriteName);
        builder.rawTag(95, TagPacketBuilder.intArrayBytes(spriteId));
        builder.rawTag(96, TagPacketBuilder.intBytes(spriteId));
        builder.stringTag(97, spriteName);
        builder.rawTag(98, TagPacketBuilder.intArrayBytes(spriteId));
    }

    private static List<TagEntry> parseTagEntries(byte[] payload) {
        ByteBuffer buffer = ByteBuffer.wrap(payload);
        ArrayList<TagEntry> entries = new ArrayList<>();
        while (buffer.remaining() >= 5) {
            int tagId = buffer.get() & 0xFF;
            int length = buffer.getInt();
            if (length < 0 || buffer.remaining() < length) {
                break;
            }
            byte[] value = new byte[length];
            buffer.get(value);
            entries.add(new TagEntry(tagId, value));
        }
        return entries;
    }

    private static int fallback(int[] values, int index, int defaultValue) {
        return index < values.length && values[index] != 0 ? values[index] : defaultValue;
    }

    private record TagEntry(int tagId, byte[] value) {
    }

    record CreateCharacterSelection(
            int gender,
            int element,
            int hairOptionId,
            int hairColorId,
            int faceOptionId,
            int skinOptionId,
            int skinColorId) {
    }
}
