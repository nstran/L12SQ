package l12sq.server.runtime;

import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import javax.imageio.ImageIO;

public final class HoaLuPreviewExporter {
    private HoaLuPreviewExporter() {
    }

    public static void main(String[] args) throws Exception {
        BufferedImage background = readResource(InstallResourceCatalog.MAP_HOA_LU_BACKGROUND_ID);
        BufferedImage overlay = readResource(InstallResourceCatalog.MAP_HOA_LU_OVERLAY_ID);

        BufferedImage composite = new BufferedImage(background.getWidth(), background.getHeight(), BufferedImage.TYPE_INT_ARGB);
        Graphics2D graphics = composite.createGraphics();
        graphics.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
        graphics.drawImage(background, 0, 0, null);
        graphics.drawImage(overlay, 0, 0, null);
        graphics.dispose();

        Path outDir = Path.of("ref", "prototypes");
        Files.createDirectories(outDir);

        Path basePath = outDir.resolve("hoalu_review_v20_base.png");
        Path previewPath = outDir.resolve("hoalu_review_v20_preview.png");

        ImageIO.write(composite, "png", basePath.toFile());

        BufferedImage scaled = new BufferedImage(composite.getWidth() * 4, composite.getHeight() * 4, BufferedImage.TYPE_INT_ARGB);
        Graphics2D scaledGraphics = scaled.createGraphics();
        scaledGraphics.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
        scaledGraphics.drawImage(composite, 0, 0, scaled.getWidth(), scaled.getHeight(), null);
        scaledGraphics.dispose();
        ImageIO.write(scaled, "png", previewPath.toFile());

        System.out.println(basePath.toAbsolutePath());
        System.out.println(previewPath.toAbsolutePath());
    }

    private static BufferedImage readResource(int id) throws IOException {
        byte[] bytes = InstallResourceCatalog.resourceById(id);
        if (bytes == null) {
            throw new IOException("Missing resource " + id);
        }
        BufferedImage image = ImageIO.read(new ByteArrayInputStream(bytes));
        if (image == null) {
            throw new IOException("Invalid PNG resource " + id);
        }
        return image;
    }
}
