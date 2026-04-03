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
    static final int INSTALL_PACKAGE_VERSION = 13;
    static final int MAP_HOA_LU_BACKGROUND_ID = 31000;
    static final int MAP_HOA_LU_OVERLAY_ID = 31001;
    static final int MAP_HOA_LU_TILESET_ID = 31002;
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
        resources.put(MAP_HOA_LU_BACKGROUND_ID, sceneBackgroundBytes());
        resources.put(MAP_HOA_LU_OVERLAY_ID, sceneOverlayBytes());
        resources.put(MAP_HOA_LU_TILESET_ID, tileAtlasBytes());
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

    private static byte[] sceneBackgroundBytes() {
        BufferedImage image = new BufferedImage(240, 160, BufferedImage.TYPE_BYTE_INDEXED);
        Graphics2D graphics = image.createGraphics();
        graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        graphics.setPaint(new java.awt.GradientPaint(0, 0, new Color(120, 214, 248), 0, 112, new Color(203, 246, 255)));
        graphics.fillRect(0, 0, image.getWidth(), image.getHeight());

        graphics.setPaint(new java.awt.GradientPaint(0, 90, new Color(112, 215, 243), 0, 160, new Color(55, 170, 218)));
        graphics.fillRect(0, 90, image.getWidth(), 70);
        graphics.setColor(new Color(240, 255, 255, 86));
        graphics.fillRect(0, 88, image.getWidth(), 4);

        drawCloudSwirl(graphics, 24, 18, 48, 18);
        drawCloudSwirl(graphics, 92, 22, 58, 20);
        drawCloudSwirl(graphics, 165, 28, 42, 16);

        drawDistantIsland(graphics, 24, 104, 52, 18);
        drawDistantIsland(graphics, 150, 98, 44, 14);
        drawPagodaIsland(graphics, 154, 56, 46, 42);
        drawPagodaIsland(graphics, 56, 62, 36, 32);
        drawFloatingSpire(graphics, 188, 44, 26, 40);

        graphics.setColor(new Color(231, 243, 198, 80));
        graphics.fillOval(132, 44, 56, 20);
        graphics.dispose();
        return writePng(image, "scene background");
    }

    private static byte[] sceneOverlayBytes() {
        BufferedImage image = new BufferedImage(240, 160, BufferedImage.TYPE_BYTE_INDEXED);
        Graphics2D graphics = image.createGraphics();
        graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        graphics.setPaint(new java.awt.GradientPaint(0, 0, new Color(120, 214, 248), 0, 112, new Color(203, 246, 255)));
        graphics.fillRect(0, 0, image.getWidth(), image.getHeight());
        graphics.setPaint(new java.awt.GradientPaint(0, 90, new Color(112, 215, 243), 0, 160, new Color(55, 170, 218)));
        graphics.fillRect(0, 90, image.getWidth(), 70);
        graphics.setColor(new Color(240, 255, 255, 72));
        graphics.fillRect(0, 88, image.getWidth(), 4);

        // Build a connected playable silhouette: bottom floor, left ramp, mid ledge attached on the right.
        drawCliffMass(graphics, 0, 124, 240, 36);
        drawSlopeRise(graphics, 0, 84, 104, 124);
        drawGrassLedge(graphics, 108, 106, 84);
        drawVerticalCliffEdge(graphics, 188, 106, 52, 18);

        drawRoundCanopyTree(graphics, 20, 70);
        drawRoundCanopyTree(graphics, 166, 90);
        drawPalmTree(graphics, 205, 78);
        drawFlowerBush(graphics, 92, 119);
        drawFlowerBush(graphics, 138, 100);

        graphics.dispose();
        return writePng(image, "scene overlay");
    }

    private static byte[] tileAtlasBytes() {
        final int tileSize = 32;
        BufferedImage image = new BufferedImage(tileSize * 2, tileSize * 2, BufferedImage.TYPE_BYTE_INDEXED);
        Graphics2D graphics = image.createGraphics();
        graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);

        drawGrassTile(graphics, 0, 0, tileSize);
        drawCliffTile(graphics, tileSize, 0, tileSize);
        drawWaterTile(graphics, 0, tileSize, tileSize);
        drawWoodTile(graphics, tileSize, tileSize, tileSize);

        graphics.dispose();
        return writePng(image, "tile atlas");
    }

    private static void drawTile(Graphics2D graphics, int x, int y, int tileSize, Color base, Color accent) {
        graphics.setColor(base);
        graphics.fillRect(x, y, tileSize, tileSize);
        graphics.setColor(accent);
        for (int row = 0; row < tileSize; row += 8) {
            graphics.fillRect(x, y + row, tileSize, 2);
        }
        for (int col = 0; col < tileSize; col += 8) {
            graphics.fillRect(x + col, y, 2, tileSize);
        }
    }

    private static void drawGrassTile(Graphics2D graphics, int x, int y, int tileSize) {
        graphics.setColor(new Color(150, 220, 85));
        graphics.fillRect(x, y, tileSize, 6);
        graphics.setColor(new Color(84, 168, 58));
        for (int i = 0; i < tileSize; i += 4) {
            graphics.fillRect(x + i, y + 3 + (i % 2), 3, 4);
        }
        graphics.setColor(new Color(210, 184, 139));
        graphics.fillRect(x, y + 6, tileSize, tileSize - 6);
        graphics.setColor(new Color(184, 156, 116));
        for (int row = y + 10; row < y + tileSize; row += 6) {
            graphics.fillOval(x + 2, row, tileSize - 4, 4);
        }
    }

    private static void drawCliffTile(Graphics2D graphics, int x, int y, int tileSize) {
        drawGrassTile(graphics, x, y, tileSize);
        graphics.setColor(new Color(159, 131, 96));
        graphics.fillRect(x, y + 12, tileSize, tileSize - 12);
    }

    private static void drawWaterTile(Graphics2D graphics, int x, int y, int tileSize) {
        graphics.setColor(new Color(84, 101, 112));
        graphics.fillRect(x, y, tileSize, tileSize);
        graphics.setColor(new Color(107, 124, 136));
        for (int row = 2; row < tileSize; row += 6) {
            graphics.fillRect(x, y + row, tileSize, 2);
        }
        graphics.setColor(new Color(220, 229, 233));
        graphics.fillOval(x + 4, y + 8, 12, 3);
        graphics.fillOval(x + 15, y + 18, 10, 3);
    }

    private static void drawWoodTile(Graphics2D graphics, int x, int y, int tileSize) {
        graphics.setColor(new Color(174, 108, 70));
        graphics.fillRect(x, y, tileSize, tileSize);
        graphics.setColor(new Color(132, 82, 53));
        for (int row = 0; row < tileSize; row += 6) {
            graphics.fillRect(x, y + row, tileSize, 1);
        }
        graphics.fillRect(x + 10, y, 2, tileSize);
        graphics.fillRect(x + 21, y, 2, tileSize);
    }

    private static byte[] spriteSheetBytes(SpriteLayer layer, int groupId, int frameCount) {
        final int frameWidth = 24;
        final int frameHeight = 32;
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
            drawSpriteFrame(graphics, layer, x, bob, frameWidth, frameHeight);
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

    private static byte[] writePng(BufferedImage image, String label) {
        try {
            ByteArrayOutputStream output = new ByteArrayOutputStream();
            ImageIO.write(image, "png", output);
            return output.toByteArray();
        } catch (IOException exception) {
            throw new IllegalStateException("Unable to render " + label, exception);
        }
    }

    private static void drawSpriteFrame(Graphics2D graphics, SpriteLayer layer, int x, int bob, int frameWidth, int frameHeight) {
        int headX = x + 7;
        int headY = 2 + bob;
        int bodyX = x + 6;
        int bodyY = 12 + bob;
        int legY = bodyY + 12;

        switch (layer) {
            case BODY -> {
                graphics.setColor(new Color(241, 203, 170));
                graphics.fillRect(headX + 1, headY + 3, 8, 7);
                graphics.fillRect(x + 5, bodyY + 7, 3, 7);
                graphics.fillRect(x + 16, bodyY + 7, 3, 7);

                graphics.setColor(new Color(185, 118, 54));
                graphics.fillRect(x + 8, legY, 3, 6);
                graphics.fillRect(x + 13, legY, 3, 6);

                graphics.setColor(new Color(205, 138, 78));
                graphics.fillRect(bodyX + 1, bodyY + 1, 10, 11);

                graphics.setColor(new Color(93, 58, 32));
                graphics.fillRect(bodyX + 3, bodyY + 2, 6, 4);

                graphics.setColor(new Color(232, 176, 84));
                graphics.fillRect(bodyX, bodyY + 2, 1, 9);
                graphics.fillRect(bodyX + 11, bodyY + 2, 1, 9);
                graphics.fillRect(bodyX + 3, bodyY + 9, 6, 2);

                graphics.setColor(new Color(110, 68, 30));
                graphics.drawRect(bodyX + 1, bodyY + 1, 9, 10);

                graphics.setColor(new Color(88, 54, 114));
                graphics.fillRect(x + 7, frameHeight - 4, 4, 2);
                graphics.fillRect(x + 13, frameHeight - 4, 4, 2);
            }
            case HAIR -> {
                graphics.setColor(new Color(156, 78, 188));
                graphics.fillRect(headX + 1, headY, 9, 3);
                graphics.fillRect(headX, headY + 3, 11, 6);
                graphics.fillRect(headX, headY + 8, 2, 4);
                graphics.fillRect(headX + 9, headY + 8, 2, 4);
                graphics.fillRect(headX + 6, headY - 4, 4, 5);
                graphics.fillRect(headX + 7, headY - 6, 3, 2);

                graphics.setColor(new Color(108, 46, 135));
                graphics.drawRect(headX, headY + 2, 10, 7);
                graphics.drawRect(headX + 6, headY - 4, 3, 4);
            }
            case FACE -> {
                graphics.setColor(new Color(42, 24, 18));
                graphics.fillRect(headX + 3, headY + 6, 1, 1);
                graphics.fillRect(headX + 7, headY + 6, 1, 1);
                graphics.fillRect(headX + 4, headY + 9, 3, 1);

                graphics.setColor(new Color(255, 178, 194));
                graphics.fillRect(headX + 2, headY + 7, 1, 1);
                graphics.fillRect(headX + 8, headY + 7, 1, 1);
            }
            case SKIN -> {
                graphics.setColor(new Color(255, 223, 186, 120));
                graphics.fillRect(headX + 1, headY + 3, 8, 7);
                graphics.fillRect(x + 5, bodyY + 7, 3, 7);
                graphics.fillRect(x + 16, bodyY + 7, 3, 7);
            }
        }
    }

    private static void drawCliffMass(Graphics2D graphics, int x, int y, int width, int height) {
        fillEarthTexture(graphics, x, y, width, height);
        drawGrassEdge(graphics, x, y, width);
    }

    private static void drawSlopeRise(Graphics2D graphics, int x, int topY, int width, int bottomY) {
        for (int step = 0; step < width; step += 6) {
            int y = topY + (step * (bottomY - topY)) / Math.max(1, width - 6);
            fillEarthTexture(graphics, x + step, y, 8, 160 - y);
        }
        for (int step = 0; step < width; step += 3) {
            int y = topY + (step * (bottomY - topY)) / Math.max(1, width - 3);
            graphics.setColor(new Color(113, 193, 58));
            graphics.fillRect(x + step, y, 4, 3);
            graphics.setColor(new Color(64, 120, 34));
            graphics.fillRect(x + step, y + 3, 1, 4);
        }
    }

    private static void drawGrassLedge(Graphics2D graphics, int x, int y, int width) {
        graphics.setColor(new Color(95, 72, 43, 54));
        graphics.fillRect(x + 2, y + 5, width - 4, 1);
        drawGrassEdge(graphics, x, y, width);
    }

    private static void drawVerticalCliffEdge(Graphics2D graphics, int x, int y, int width, int height) {
        fillEarthTexture(graphics, x, y, width, height);
        graphics.setColor(new Color(121, 97, 60));
        graphics.fillRect(x, y, 2, height);
    }

    private static void drawTreeSilhouette(Graphics2D graphics, int x, int y) {
        graphics.setColor(new Color(88, 63, 39));
        graphics.fillRect(x + 12, y + 18, 5, 22);
        graphics.fillRect(x + 15, y + 22, 4, 13);
        graphics.setColor(new Color(66, 113, 47));
        graphics.fillOval(x, y + 10, 22, 18);
        graphics.fillOval(x + 11, y + 1, 22, 21);
        graphics.fillOval(x + 24, y + 10, 20, 17);
        graphics.setColor(new Color(86, 141, 59));
        graphics.fillOval(x + 4, y + 13, 15, 11);
        graphics.fillOval(x + 15, y + 5, 15, 13);
        graphics.fillOval(x + 28, y + 12, 12, 10);
    }

    private static void fillEarthTexture(Graphics2D graphics, int x, int y, int width, int height) {
        graphics.setColor(new Color(244, 222, 171));
        graphics.fillRect(x, y, width, height);
        graphics.setColor(new Color(227, 196, 142));
        for (int row = y + 4; row < y + height; row += 11) {
            for (int col = x + ((row / 11) % 2 == 0 ? 1 : 10); col < x + width + 12; col += 20) {
                graphics.drawLine(col, row + 4, col + 6, row + 1);
                graphics.drawLine(col + 6, row + 1, col + 12, row + 4);
                graphics.drawLine(col, row + 4, col + 6, row + 8);
                graphics.drawLine(col + 6, row + 8, col + 12, row + 4);
            }
        }
        graphics.setColor(new Color(253, 238, 196));
        for (int row = y + 4; row < y + height; row += 11) {
            for (int col = x + 2 + ((row / 11) % 2 == 0 ? 1 : 10); col < x + width + 12; col += 20) {
                graphics.drawLine(col + 1, row + 4, col + 5, row + 2);
            }
        }
    }

    private static void drawGrassEdge(Graphics2D graphics, int x, int y, int width) {
        graphics.setColor(new Color(132, 221, 63));
        graphics.fillRect(x, y, width, 5);
        graphics.setColor(new Color(224, 255, 139));
        graphics.fillRect(x, y, width, 1);
        graphics.setColor(new Color(73, 136, 39));
        graphics.fillRect(x, y + 5, width, 1);
        graphics.setColor(new Color(58, 119, 38));
        for (int i = x; i < x + width; i += 4) {
            int blade = ((i / 4) % 3);
            graphics.fillRect(i, y + 2 + blade, 2, 4 + (blade % 2));
        }
    }

    private static void drawCloudSwirl(Graphics2D graphics, int x, int y, int width, int height) {
        graphics.setColor(new Color(246, 251, 254, 170));
        graphics.fillOval(x, y + 3, width / 2, height - 4);
        graphics.fillOval(x + width / 4, y, width / 2, height);
        graphics.fillOval(x + width / 2, y + 4, width / 3, height - 5);
    }

    private static void drawDistantIsland(Graphics2D graphics, int x, int y, int width, int height) {
        graphics.setColor(new Color(216, 207, 236, 140));
        graphics.fillOval(x, y + 8, width, height);
        graphics.setColor(new Color(196, 248, 126, 120));
        graphics.fillRect(x + 4, y + 6, width - 8, 3);
    }

    private static void drawPagodaIsland(Graphics2D graphics, int x, int y, int width, int height) {
        graphics.setColor(new Color(228, 241, 214, 180));
        graphics.fillOval(x + 6, y + height - 10, width - 12, 10);
        graphics.setColor(new Color(230, 219, 178, 200));
        graphics.fillPolygon(
                new int[] {x + width / 2, x + 10, x + width - 10},
                new int[] {y + 8, y + height - 8, y + height - 8},
                3);
        graphics.setColor(new Color(153, 110, 76, 200));
        graphics.fillRect(x + width / 2 - 3, y + height - 14, 6, 10);
        graphics.setColor(new Color(202, 126, 104, 220));
        graphics.fillPolygon(
                new int[] {x + width / 2, x + width / 2 - 10, x + width / 2 + 10},
                new int[] {y, y + 7, y + 7},
                3);
        graphics.setColor(new Color(168, 210, 93, 190));
        graphics.fillRect(x + 8, y + height - 12, width - 16, 3);
    }

    private static void drawFloatingSpire(Graphics2D graphics, int x, int y, int width, int height) {
        graphics.setColor(new Color(230, 220, 176, 195));
        graphics.fillPolygon(
                new int[] {x + width / 2, x + 4, x + width - 4},
                new int[] {y, y + height - 12, y + height - 12},
                3);
        graphics.setColor(new Color(148, 214, 89, 205));
        graphics.fillRect(x + 5, y, width - 10, 4);
        graphics.setColor(new Color(74, 193, 235, 210));
        graphics.fillRect(x + width / 2 - 2, y + 2, 4, height - 6);
        graphics.setColor(new Color(63, 151, 195, 180));
        graphics.fillRect(x + width / 2 - 1, y + 3, 2, height - 6);
    }

    private static void drawTropicalTree(Graphics2D graphics, int x, int y) {
        graphics.setColor(new Color(155, 99, 44));
        graphics.fillRect(x + 12, y + 22, 9, 34);
        graphics.setColor(new Color(180, 119, 56));
        graphics.fillRect(x + 14, y + 24, 2, 28);
        graphics.fillRect(x + 18, y + 28, 2, 22);
        graphics.setColor(new Color(100, 171, 48));
        graphics.fillOval(x, y + 8, 24, 22);
        graphics.fillOval(x + 12, y, 24, 24);
        graphics.fillOval(x + 24, y + 8, 24, 22);
        graphics.setColor(new Color(146, 219, 78));
        graphics.fillOval(x + 4, y + 12, 14, 11);
        graphics.fillOval(x + 16, y + 5, 14, 13);
        graphics.fillOval(x + 28, y + 11, 13, 10);
    }

    private static void drawRoundCanopyTree(Graphics2D graphics, int x, int y) {
        graphics.setColor(new Color(132, 82, 42));
        graphics.fillRect(x + 10, y + 24, 8, 30);
        graphics.setColor(new Color(92, 156, 54));
        graphics.fillOval(x, y + 10, 20, 16);
        graphics.fillOval(x + 10, y + 2, 22, 18);
        graphics.fillOval(x + 24, y + 11, 20, 15);
        graphics.setColor(new Color(142, 212, 78));
        graphics.fillOval(x + 4, y + 13, 13, 10);
        graphics.fillOval(x + 16, y + 6, 13, 11);
        graphics.fillOval(x + 28, y + 14, 10, 8);
    }

    private static void drawPalmTree(Graphics2D graphics, int x, int y) {
        graphics.setColor(new Color(155, 99, 44));
        graphics.fillRect(x + 8, y + 18, 4, 28);
        graphics.setColor(new Color(88, 160, 58));
        graphics.fillOval(x, y + 2, 10, 26);
        graphics.fillOval(x + 6, y, 12, 28);
        graphics.fillOval(x + 12, y + 3, 10, 24);
    }

    private static void drawTropicalLeaves(Graphics2D graphics, int x, int y) {
        graphics.setColor(new Color(74, 149, 56));
        graphics.fillOval(x, y + 10, 18, 34);
        graphics.fillOval(x + 10, y + 2, 20, 40);
        graphics.fillOval(x + 24, y + 10, 18, 34);
        graphics.setColor(new Color(174, 157, 58));
        graphics.fillOval(x + 24, y + 30, 14, 18);
        graphics.fillOval(x + 18, y + 26, 12, 16);
    }

    private static void drawFlowerBush(Graphics2D graphics, int x, int y) {
        graphics.setColor(new Color(76, 153, 58));
        graphics.fillOval(x, y + 4, 18, 10);
        graphics.fillOval(x + 10, y, 16, 12);
        graphics.fillOval(x + 22, y + 4, 18, 10);
        graphics.setColor(new Color(255, 84, 145));
        graphics.fillOval(x + 6, y + 6, 4, 4);
        graphics.fillOval(x + 16, y + 3, 4, 4);
        graphics.fillOval(x + 25, y + 7, 4, 4);
    }

    private enum SpriteLayer {
        BODY,
        HAIR,
        FACE,
        SKIN
    }
}
