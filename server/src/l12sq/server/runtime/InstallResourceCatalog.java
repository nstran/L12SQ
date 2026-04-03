package l12sq.server.runtime;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.Stroke;
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
    static final int INSTALL_PACKAGE_VERSION = 16;
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
    private static final Color SKY_TOP = new Color(105, 211, 233);
    private static final Color SKY_BOTTOM = new Color(156, 224, 220);
    private static final Color WATER_TOP = new Color(103, 210, 232);
    private static final Color WATER_BOTTOM = new Color(1, 159, 205);
    private static final Color CLOUD = new Color(219, 238, 211, 170);
    private static final Color EARTH_BASE = new Color(239, 211, 157);
    private static final Color EARTH_MID = new Color(237, 203, 118);
    private static final Color EARTH_SHADE = new Color(226, 177, 114);
    private static final Color EARTH_HIGHLIGHT = new Color(253, 238, 196);
    private static final Color GRASS_LIGHT = new Color(159, 212, 90);
    private static final Color GRASS_BRIGHT = new Color(216, 238, 212);
    private static final Color GRASS_MID = new Color(89, 160, 51);
    private static final Color GRASS_DARK = new Color(36, 109, 42);
    private static final Color FOLIAGE_LIGHT = new Color(101, 161, 87);
    private static final Color FOLIAGE_MID = new Color(94, 161, 52);
    private static final Color FOLIAGE_DARK = new Color(36, 109, 42);

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
        graphics.setPaint(new java.awt.GradientPaint(0, 0, SKY_TOP, 0, 106, SKY_BOTTOM));
        graphics.fillRect(0, 0, image.getWidth(), image.getHeight());

        graphics.setPaint(new java.awt.GradientPaint(0, 94, WATER_TOP, 0, 160, WATER_BOTTOM));
        graphics.fillRect(0, 92, image.getWidth(), 68);
        graphics.setColor(new Color(CLOUD.getRed(), CLOUD.getGreen(), CLOUD.getBlue(), 138));
        graphics.fillRect(0, 90, image.getWidth(), 4);

        drawCloudSwirl(graphics, 18, 18, 40, 15);
        drawCloudSwirl(graphics, 74, 18, 54, 18);
        drawCloudSwirl(graphics, 148, 20, 52, 17);
        drawSkyCurlBand(graphics, 104, 26, 92, 28);

        drawDistantIsland(graphics, 18, 108, 54, 16);
        drawDistantIsland(graphics, 150, 104, 60, 18);
        drawPagodaIsland(graphics, 26, 58, 46, 34);
        drawPagodaIsland(graphics, 178, 70, 38, 28);
        drawFloatingSpire(graphics, 144, 44, 30, 50);
        drawFloatingSpire(graphics, 112, 66, 22, 32);
        drawFloatingSpire(graphics, 192, 62, 18, 24);
        drawGoldenRoof(graphics, 76, 84, 38, 18, 110);

        graphics.setColor(new Color(216, 238, 212, 72));
        graphics.fillOval(114, 44, 64, 18);
        drawSoftWaterBands(graphics, 0, 108, 240, 46);
        graphics.dispose();
        return writePng(image, "scene background");
    }

    private static byte[] sceneOverlayBytes() {
        BufferedImage image = new BufferedImage(240, 160, BufferedImage.TYPE_BYTE_INDEXED);
        Graphics2D graphics = image.createGraphics();
        graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        graphics.setPaint(new java.awt.GradientPaint(0, 0, SKY_TOP, 0, 106, SKY_BOTTOM));
        graphics.fillRect(0, 0, image.getWidth(), image.getHeight());
        graphics.setPaint(new java.awt.GradientPaint(0, 94, WATER_TOP, 0, 160, WATER_BOTTOM));
        graphics.fillRect(0, 92, image.getWidth(), 68);
        graphics.setColor(new Color(CLOUD.getRed(), CLOUD.getGreen(), CLOUD.getBlue(), 130));
        graphics.fillRect(0, 90, image.getWidth(), 4);
        drawSoftWaterBands(graphics, 0, 108, 240, 46);

        drawGoldenRoof(graphics, 70, 84, 40, 18, 255);

        // Hanging island ceiling, close to the reference video.
        int[] topIslandX = {0, 240, 240, 220, 198, 178, 160, 140, 120, 96, 72, 46, 20, 0};
        int[] topIslandY = {0, 0, 52, 54, 88, 96, 74, 82, 96, 88, 74, 66, 56, 54};
        drawConnectedTerrain(graphics, topIslandX, topIslandY);
        drawTerrainMoss(graphics, 118, 66, 10, 18);
        drawTerrainMoss(graphics, 150, 82, 12, 8);

        // Bottom walkable floor, flat and continuous.
        int[] bottomFloorX = {0, 240, 240, 0};
        int[] bottomFloorY = {134, 134, 160, 160};
        drawConnectedTerrain(graphics, bottomFloorX, bottomFloorY);
        drawGrassContour(graphics, new int[] {0, 240}, new int[] {134, 134});

        drawBananaPlant(graphics, 4, 78);
        drawFlowerBush(graphics, 102, 124);
        drawFlowerBush(graphics, 132, 124);
        drawRopeLadder(graphics, 228, 12, 116);

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
        graphics.setColor(GRASS_LIGHT);
        graphics.fillRect(x, y, tileSize, 6);
        graphics.setColor(GRASS_MID);
        for (int i = 0; i < tileSize; i += 4) {
            graphics.fillRect(x + i, y + 3 + (i % 2), 3, 4);
        }
        graphics.setColor(EARTH_BASE);
        graphics.fillRect(x, y + 6, tileSize, tileSize - 6);
        graphics.setColor(EARTH_SHADE);
        for (int row = y + 10; row < y + tileSize; row += 6) {
            graphics.fillOval(x + 2, row, tileSize - 4, 4);
        }
    }

    private static void drawCliffTile(Graphics2D graphics, int x, int y, int tileSize) {
        drawGrassTile(graphics, x, y, tileSize);
        graphics.setColor(EARTH_SHADE);
        graphics.fillRect(x, y + 12, tileSize, tileSize - 12);
    }

    private static void drawWaterTile(Graphics2D graphics, int x, int y, int tileSize) {
        graphics.setColor(WATER_BOTTOM);
        graphics.fillRect(x, y, tileSize, tileSize);
        graphics.setColor(WATER_TOP);
        for (int row = 2; row < tileSize; row += 6) {
            graphics.fillRect(x, y + row, tileSize, 2);
        }
        graphics.setColor(new Color(CLOUD.getRed(), CLOUD.getGreen(), CLOUD.getBlue(), 200));
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
        graphics.setColor(EARTH_BASE);
        graphics.fillRect(x, y, width, height);
        graphics.setColor(EARTH_MID);
        for (int row = y + 4; row < y + height; row += 11) {
            for (int col = x + ((row / 11) % 2 == 0 ? 1 : 10); col < x + width + 12; col += 20) {
                graphics.drawLine(col, row + 4, col + 6, row + 1);
                graphics.drawLine(col + 6, row + 1, col + 12, row + 4);
                graphics.drawLine(col, row + 4, col + 6, row + 8);
                graphics.drawLine(col + 6, row + 8, col + 12, row + 4);
            }
        }
        graphics.setColor(EARTH_HIGHLIGHT);
        for (int row = y + 4; row < y + height; row += 11) {
            for (int col = x + 2 + ((row / 11) % 2 == 0 ? 1 : 10); col < x + width + 12; col += 20) {
                graphics.drawLine(col + 1, row + 4, col + 5, row + 2);
            }
        }
        graphics.setColor(new Color(EARTH_SHADE.getRed(), EARTH_SHADE.getGreen(), EARTH_SHADE.getBlue(), 110));
        for (int row = y + 8; row < y + height; row += 22) {
            graphics.drawLine(x, row, x + width, row + 2);
        }
    }

    private static void drawGrassEdge(Graphics2D graphics, int x, int y, int width) {
        graphics.setColor(GRASS_LIGHT);
        graphics.fillRect(x, y, width, 5);
        graphics.setColor(GRASS_BRIGHT);
        graphics.fillRect(x, y, width, 1);
        graphics.setColor(GRASS_DARK);
        graphics.fillRect(x, y + 5, width, 1);
        graphics.setColor(GRASS_DARK);
        for (int i = x; i < x + width; i += 4) {
            int blade = ((i / 4) % 3);
            graphics.fillRect(i, y + 2 + blade, 2, 4 + (blade % 2));
        }
    }

    private static void drawCloudSwirl(Graphics2D graphics, int x, int y, int width, int height) {
        graphics.setColor(CLOUD);
        graphics.fillOval(x, y + 3, width / 2, height - 4);
        graphics.fillOval(x + width / 4, y, width / 2, height);
        graphics.fillOval(x + width / 2, y + 4, width / 3, height - 5);
    }

    private static void drawSkyCurlBand(Graphics2D graphics, int x, int y, int width, int height) {
        graphics.setColor(new Color(CLOUD.getRed(), CLOUD.getGreen(), CLOUD.getBlue(), 120));
        for (int offset = 0; offset < width; offset += 14) {
            graphics.drawArc(x + offset, y + ((offset / 14) % 2) * 4, 10, height, 210, 120);
        }
    }

    private static void drawDistantIsland(Graphics2D graphics, int x, int y, int width, int height) {
        graphics.setColor(new Color(216, 238, 212, 125));
        graphics.fillOval(x, y + 8, width, height);
        graphics.setColor(new Color(GRASS_BRIGHT.getRed(), GRASS_BRIGHT.getGreen(), GRASS_BRIGHT.getBlue(), 110));
        graphics.fillRect(x + 4, y + 6, width - 8, 3);
    }

    private static void drawPagodaIsland(Graphics2D graphics, int x, int y, int width, int height) {
        graphics.setColor(new Color(216, 238, 212, 180));
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
        graphics.setColor(new Color(GRASS_LIGHT.getRed(), GRASS_LIGHT.getGreen(), GRASS_LIGHT.getBlue(), 190));
        graphics.fillRect(x + 8, y + height - 12, width - 16, 3);
    }

    private static void drawFloatingSpire(Graphics2D graphics, int x, int y, int width, int height) {
        graphics.setColor(new Color(230, 220, 176, 195));
        graphics.fillPolygon(
                new int[] {x + width / 2, x + 4, x + width - 4},
                new int[] {y, y + height - 12, y + height - 12},
                3);
        graphics.setColor(new Color(GRASS_LIGHT.getRed(), GRASS_LIGHT.getGreen(), GRASS_LIGHT.getBlue(), 205));
        graphics.fillRect(x + 5, y, width - 10, 4);
        graphics.setColor(new Color(74, 193, 235, 210));
        graphics.fillRect(x + width / 2 - 2, y + 2, 4, height - 6);
        graphics.setColor(new Color(63, 151, 195, 180));
        graphics.fillRect(x + width / 2 - 1, y + 3, 2, height - 6);
    }

    private static void drawGoldenRoof(Graphics2D graphics, int x, int y, int width, int height, int alpha) {
        graphics.setColor(new Color(176, 110, 87, alpha));
        graphics.fillPolygon(
                new int[] {x, x + width / 2, x + width},
                new int[] {y + height - 4, y, y + height - 4},
                3);
        graphics.setColor(new Color(235, 194, 78, alpha));
        graphics.fillPolygon(
                new int[] {x + 3, x + width / 2, x + width - 3},
                new int[] {y + height - 6, y + 2, y + height - 6},
                3);
        graphics.setColor(new Color(180, 128, 74, alpha));
        graphics.fillRect(x + width / 2 - 5, y + height - 4, 10, 9);
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

    private static void drawBananaPlant(Graphics2D graphics, int x, int y) {
        graphics.setColor(new Color(143, 98, 46));
        graphics.fillRect(x + 22, y + 30, 8, 30);
        graphics.setColor(FOLIAGE_MID);
        graphics.fillOval(x, y + 6, 22, 40);
        graphics.fillOval(x + 12, y, 24, 44);
        graphics.fillOval(x + 28, y + 8, 20, 38);
        graphics.setColor(new Color(208, 178, 69));
        graphics.fillOval(x + 28, y + 22, 10, 14);
        graphics.fillOval(x + 34, y + 26, 10, 14);
        graphics.fillOval(x + 38, y + 30, 10, 13);
    }

    private static void drawRopeLadder(Graphics2D graphics, int x, int y, int height) {
        graphics.setColor(new Color(108, 72, 39));
        graphics.fillRect(x, y, 3, height);
        graphics.fillRect(x + 9, y, 3, height);
        graphics.setColor(new Color(150, 106, 61));
        for (int row = y + 6; row < y + height; row += 10) {
            graphics.fillRect(x + 1, row, 10, 3);
        }
    }

    private static void drawHeroTree(Graphics2D graphics, int x, int y) {
        graphics.setColor(new Color(154, 100, 47));
        graphics.fillRect(x + 18, y + 26, 12, 44);
        graphics.fillRect(x + 14, y + 40, 6, 20);
        graphics.fillRect(x + 28, y + 34, 6, 18);
        graphics.setColor(new Color(179, 121, 61));
        graphics.fillRect(x + 21, y + 28, 2, 38);
        graphics.fillRect(x + 25, y + 31, 2, 30);
        graphics.setColor(FOLIAGE_MID);
        graphics.fillOval(x, y + 10, 28, 22);
        graphics.fillOval(x + 14, y, 30, 24);
        graphics.fillOval(x + 30, y + 9, 26, 21);
        graphics.fillOval(x + 12, y + 14, 34, 20);
        graphics.setColor(GRASS_LIGHT);
        graphics.fillOval(x + 5, y + 14, 16, 12);
        graphics.fillOval(x + 21, y + 5, 16, 14);
        graphics.fillOval(x + 36, y + 15, 13, 11);
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
        graphics.setColor(FOLIAGE_MID);
        graphics.fillOval(x, y + 4, 18, 10);
        graphics.fillOval(x + 10, y, 16, 12);
        graphics.fillOval(x + 22, y + 4, 18, 10);
        graphics.setColor(new Color(255, 84, 145));
        graphics.fillOval(x + 6, y + 6, 4, 4);
        graphics.fillOval(x + 16, y + 3, 4, 4);
        graphics.fillOval(x + 25, y + 7, 4, 4);
    }

    private static void drawSoftWaterBands(Graphics2D graphics, int x, int y, int width, int height) {
        graphics.setColor(new Color(CLOUD.getRed(), CLOUD.getGreen(), CLOUD.getBlue(), 52));
        for (int row = y + 2; row < y + height; row += 12) {
            graphics.fillRect(x, row, width, 1);
        }
        graphics.setColor(new Color(WATER_TOP.getRed(), WATER_TOP.getGreen(), WATER_TOP.getBlue(), 38));
        for (int row = y + 8; row < y + height; row += 12) {
            graphics.fillRect(x, row, width, 1);
        }
    }

    private static void drawConnectedTerrain(Graphics2D graphics, int[] xs, int[] ys) {
        Polygon polygon = new Polygon(xs, ys, xs.length);
        Shape previousClip = graphics.getClip();
        graphics.setClip(polygon);
        fillEarthTexture(graphics, polygon.getBounds().x, polygon.getBounds().y, polygon.getBounds().width, polygon.getBounds().height);
        graphics.setClip(previousClip);
    }

    private static void drawGrassContour(Graphics2D graphics, int[] xs, int[] ys) {
        Stroke previousStroke = graphics.getStroke();

        graphics.setStroke(new BasicStroke(7f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        graphics.setColor(GRASS_BRIGHT);
        graphics.drawPolyline(xs, ys, xs.length);

        graphics.setStroke(new BasicStroke(4f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        graphics.setColor(GRASS_LIGHT);
        graphics.drawPolyline(xs, ys, xs.length);

        graphics.setStroke(new BasicStroke(1.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        graphics.setColor(GRASS_DARK);
        graphics.drawPolyline(xs, ys, xs.length);

        drawGrassTufts(graphics, 0, 60, 64, 60, 5);
        drawGrassTufts(graphics, 64, 60, 138, 124, 4);
        drawGrassTufts(graphics, 138, 124, 240, 124, 5);

        graphics.setStroke(previousStroke);
    }

    private static void drawGrassTufts(Graphics2D graphics, int x1, int y1, int x2, int y2, int spacing) {
        double dx = x2 - x1;
        double dy = y2 - y1;
        double length = Math.max(1.0d, Math.sqrt(dx * dx + dy * dy));
        double nx = -dy / length;
        double ny = dx / length;
        if (ny > 0) {
            nx = -nx;
            ny = -ny;
        }
        graphics.setColor(new Color(73, 136, 39));
        for (double step = 0; step <= length; step += spacing) {
            double t = step / length;
            int px = (int) Math.round(x1 + dx * t);
            int py = (int) Math.round(y1 + dy * t);
            int blade = 5 + ((int) step % 3);
            int tx = (int) Math.round(px + nx * blade);
            int ty = (int) Math.round(py + ny * blade);
            graphics.drawLine(px, py, tx, ty);
        }
    }

    private static void drawTerrainMoss(Graphics2D graphics, int x, int y, int width, int height) {
        graphics.setColor(FOLIAGE_LIGHT);
        graphics.fillOval(x, y, width, height);
        graphics.setColor(FOLIAGE_DARK);
        graphics.fillOval(x + 2, y + 1, width - 4, height - 2);
    }

    private enum SpriteLayer {
        BODY,
        HAIR,
        FACE,
        SKIN
    }
}
