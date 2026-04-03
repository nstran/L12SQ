package l12sq.server.runtime;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Base64;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.imageio.ImageIO;

final class InstallResourceCatalog {
    static final int INSTALL_PACKAGE_VERSION = 5;
    static final List<Integer> STARTUP_INSTALL_RESOURCE_IDS = Arrays.asList(
            30099,
            79899,
            79999,
            89999,
            99000, 99001, 99002, 99003, 99004, 99005, 99006,
            700000, 700001, 700002, 700003, 700004, 700005, 700006,
            700010, 700011, 700012, 700013, 700014, 700015, 700016,
            700020, 700021, 700022, 700023, 700024, 700025, 700026
    );
    static final List<Integer> CREATE_CHAR_RESOURCE_IDS = Arrays.asList(
            79899,
            79999,
            89999,
            99000, 99001, 99002, 99003, 99004, 99005, 99006,
            700000, 700001, 700002, 700003, 700004, 700005, 700006,
            700010, 700011, 700012, 700013, 700014, 700015, 700016,
            700020, 700021, 700022, 700023, 700024, 700025, 700026
    );

    private static final int[] METADATA_FRAME_COUNTS = {2, 6, 4, 4, 4, 4, 3};
    private static final byte[] PLACEHOLDER_PNG = Base64.getDecoder().decode(
            "iVBORw0KGgoAAAANSUhEUgAAAAwAAAAMCAYAAABWdVznAAAAAXNSR0IArs4c6QAAAARnQU1BAACxjwv8YQUAAAAJcEhZcwAADsMAAA7DAcdvqGQAAAAYSURBVChTY/h/4PR/UvCoBmLwCNRw+j8Awcz9IBccOeIAAAAASUVORK5CYII=");
    private static final Map<Integer, byte[]> INSTALL_RESOURCES = createInstallResources();

    private InstallResourceCatalog() {
    }

    static byte[] resourceById(int resourceId) {
        return INSTALL_RESOURCES.get(resourceId);
    }

    static int totalInstallBytes(List<Integer> resourceIds) {
        int total = 0;
        for (int resourceId : resourceIds) {
            byte[] resource = INSTALL_RESOURCES.get(resourceId);
            if (resource != null) {
                total += resource.length;
            }
        }
        return total;
    }

    private static Map<Integer, byte[]> createInstallResources() {
        Map<Integer, byte[]> resources = new LinkedHashMap<>();
        resources.put(30099, PLACEHOLDER_PNG);
        resources.put(79899, metadataBytes(700000));
        resources.put(79999, metadataBytes(700010));
        resources.put(89999, metadataBytes(700020));
        addSpriteRange(resources, 99000, SpriteLayer.BODY);
        addSpriteRange(resources, 700000, SpriteLayer.HAIR);
        addSpriteRange(resources, 700010, SpriteLayer.FACE);
        addSpriteRange(resources, 700020, SpriteLayer.SKIN);
        return resources;
    }

    private static byte[] metadataBytes(int imageBaseId) {
        try {
            ByteArrayOutputStream output = new ByteArrayOutputStream();
            DataOutputStream dos = new DataOutputStream(output);
            dos.writeByte(1);
            dos.writeInt(imageBaseId);
            dos.writeByte(METADATA_FRAME_COUNTS.length);
            for (int groupId = 0; groupId < METADATA_FRAME_COUNTS.length; groupId++) {
                int frameCount = METADATA_FRAME_COUNTS[groupId];
                dos.writeByte(groupId);
                dos.writeByte(frameCount);
                dos.writeByte(frameCount);
                for (int frameIndex = 0; frameIndex < frameCount; frameIndex++) {
                    dos.writeByte(frameIndex);
                    dos.writeShort(0);
                    dos.writeShort(0);
                }
            }
            dos.flush();
            return output.toByteArray();
        } catch (IOException exception) {
            throw new IllegalStateException("Unable to build create-character metadata", exception);
        }
    }

    private static void addSpriteRange(Map<Integer, byte[]> resources, int startId, SpriteLayer layer) {
        for (int groupId = 0; groupId < METADATA_FRAME_COUNTS.length; groupId++) {
            resources.put(startId + groupId, spriteSheetBytes(layer, groupId, METADATA_FRAME_COUNTS[groupId]));
        }
    }

    private static byte[] spriteSheetBytes(SpriteLayer layer, int groupId, int frameCount) {
        final int frameWidth = 16;
        final int frameHeight = 22;
        BufferedImage image = new BufferedImage(frameWidth * frameCount, frameHeight, BufferedImage.TYPE_INT_ARGB);
        Graphics2D graphics = image.createGraphics();
        graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
        graphics.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
        graphics.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_SPEED);
        graphics.setStroke(new BasicStroke(1f));
        graphics.setBackground(new Color(0, 0, 0, 0));
        graphics.clearRect(0, 0, image.getWidth(), image.getHeight());

        for (int frameIndex = 0; frameIndex < frameCount; frameIndex++) {
            int x = frameIndex * frameWidth;
            int bob = (frameIndex + groupId) % 2;
            drawSpriteFrame(graphics, layer, x, bob);
        }

        graphics.dispose();
        try {
            ByteArrayOutputStream output = new ByteArrayOutputStream();
            ImageIO.write(image, "png", output);
            return output.toByteArray();
        } catch (IOException exception) {
            throw new IllegalStateException("Unable to render sprite sheet for " + layer + " group " + groupId, exception);
        }
    }

    private static void drawSpriteFrame(Graphics2D graphics, SpriteLayer layer, int x, int bob) {
        int headX = x + 5;
        int headY = 2 + bob;
        int bodyX = x + 4;
        int bodyY = 9 + bob;

        switch (layer) {
            case BODY -> {
                graphics.setColor(new Color(255, 221, 178));
                graphics.fillRect(headX, headY, 6, 6);
                graphics.fillRect(x + 2, bodyY + 7, 2, 5);
                graphics.fillRect(x + 12, bodyY + 7, 2, 5);

                graphics.setColor(new Color(215, 120, 40));
                graphics.fillRect(bodyX, bodyY, 8, 9);

                graphics.setColor(new Color(40, 170, 70));
                graphics.fillRect(x + 3, bodyY + 16, 4, 3);
                graphics.fillRect(x + 9, bodyY + 16, 4, 3);

                graphics.setColor(new Color(110, 68, 30));
                graphics.drawRect(bodyX, bodyY, 7, 8);
            }
            case HAIR -> {
                graphics.setColor(new Color(90, 110, 180));
                graphics.fillRect(headX - 1, headY - 1, 8, 3);
                graphics.fillRect(headX - 1, headY + 1, 2, 4);
                graphics.fillRect(headX + 5, headY + 1, 2, 4);

                graphics.setColor(new Color(50, 70, 130));
                graphics.drawRect(headX - 1, headY - 1, 7, 5);
            }
            case FACE -> {
                graphics.setColor(new Color(30, 30, 30));
                graphics.fillRect(headX + 1, headY + 2, 1, 1);
                graphics.fillRect(headX + 4, headY + 2, 1, 1);
                graphics.fillRect(headX + 2, headY + 4, 2, 1);
            }
            case SKIN -> {
                graphics.setColor(new Color(255, 236, 204, 110));
                graphics.fillRect(headX, headY, 6, 6);
                graphics.fillRect(x + 2, bodyY + 7, 2, 5);
                graphics.fillRect(x + 12, bodyY + 7, 2, 5);
            }
        }
    }

    private enum SpriteLayer {
        BODY,
        HAIR,
        FACE,
        SKIN
    }
}
