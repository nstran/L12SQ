package l12sq.server.runtime;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.function.Supplier;

final class ExternalAssetLoader {
    private static final Path ASSET_ROOT = Path.of("assets");

    private ExternalAssetLoader() {
    }

    static byte[] loadBytes(String relativePath, Supplier<byte[]> fallbackSupplier) {
        Path assetPath = ASSET_ROOT.resolve(relativePath).normalize();
        if (Files.isRegularFile(assetPath)) {
            try {
                return Files.readAllBytes(assetPath);
            } catch (IOException exception) {
                throw new IllegalStateException("Unable to read asset file: " + assetPath, exception);
            }
        }
        return fallbackSupplier.get();
    }

    static Path resolve(String relativePath) {
        return ASSET_ROOT.resolve(relativePath).normalize();
    }
}
