package l12sq.server.runtime;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.function.Supplier;

final class ExternalAssetLoader {
    private static final List<Path> ASSET_ROOTS = List.of(
            Path.of("server", "assets"),
            Path.of("assets"));

    private ExternalAssetLoader() {
    }

    static byte[] loadBytes(String relativePath, Supplier<byte[]> fallbackSupplier) {
        for (Path root : ASSET_ROOTS) {
            Path assetPath = root.resolve(relativePath).normalize();
            if (Files.isRegularFile(assetPath)) {
                try {
                    return Files.readAllBytes(assetPath);
                } catch (IOException exception) {
                    throw new IllegalStateException("Unable to read asset file: " + assetPath, exception);
                }
            }
        }
        return fallbackSupplier.get();
    }

    static Path resolve(String relativePath) {
        for (Path root : ASSET_ROOTS) {
            Path candidate = root.resolve(relativePath).normalize();
            Path parent = candidate.getParent();
            if (parent != null && Files.exists(parent)) {
                return candidate;
            }
        }
        return ASSET_ROOTS.get(0).resolve(relativePath).normalize();
    }
}
