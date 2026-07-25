package dev.minceraft.sonus.plasmo.protocol.version;

import dev.minceraft.sonus.common.version.SemanticVersion;
import org.jspecify.annotations.NullMarked;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@NullMarked
public final class VersionManager {

    public static final SemanticVersion MIN_VERSION = SemanticVersion.of("2.0.0");

    private static final Logger LOGGER = LoggerFactory.getLogger("Sonus");

    private VersionManager() {
    }

    public static boolean isSupported(SemanticVersion version) {
        // Currently, plasmo voice protocol supports only versions 2.0.0 and newer. There aren't any upper limits yet.
        return version.isEqualOrNewerThan(MIN_VERSION);
    }

    public static void logSupportedVersions() {
        LOGGER.info("Supported Plasmo protocol versions: {} and newer", MIN_VERSION.asShortPrettyString());
    }
}
