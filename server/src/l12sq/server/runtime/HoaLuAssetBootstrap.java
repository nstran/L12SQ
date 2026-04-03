package l12sq.server.runtime;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public final class HoaLuAssetBootstrap {
    private HoaLuAssetBootstrap() {
    }

    public static void main(String[] args) throws Exception {
        Path assetDir = ExternalAssetLoader.resolve("maps/hoalu");
        Files.createDirectories(assetDir);

        writeIfMissing(assetDir.resolve("background.png"), InstallResourceCatalog.resourceById(InstallResourceCatalog.MAP_HOA_LU_BACKGROUND_ID));
        writeIfMissing(assetDir.resolve("overlay.png"), InstallResourceCatalog.resourceById(InstallResourceCatalog.MAP_HOA_LU_OVERLAY_ID));
        writeIfMissing(assetDir.resolve("tileset.png"), InstallResourceCatalog.resourceById(InstallResourceCatalog.MAP_HOA_LU_TILESET_ID));
        writeTextIfMissing(HoaLuMapSpec.defaultPath(), HoaLuMapSpec.defaultJson());

        System.out.println(assetDir.toAbsolutePath());
        System.out.println(HoaLuMapSpec.defaultPath().toAbsolutePath());
    }

    private static void writeIfMissing(Path path, byte[] bytes) throws IOException {
        if (Files.exists(path)) {
            return;
        }
        Files.write(path, bytes);
    }

    private static void writeTextIfMissing(Path path, String text) throws IOException {
        if (Files.exists(path)) {
            return;
        }
        Files.createDirectories(path.getParent());
        Files.writeString(path, text, StandardCharsets.UTF_8);
    }
}
