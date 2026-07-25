package dev.minceraft.sonus.common.version;

import org.jspecify.annotations.NullMarked;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@NullMarked
public record SemanticVersion(int major, int minor, int patch, String metadata) {

    private static final Pattern VERSION_PATTERN = Pattern.compile(".*((-)?(\\d+)\\.(\\d+)\\.(\\d+).*)");

    public SemanticVersion(int major, int minor, int patch, String metadata) {
        this.major = major;
        this.minor = minor;
        this.patch = patch;
        this.metadata = metadata;
    }

    public static SemanticVersion of(String version) {
        Matcher matcher = VERSION_PATTERN.matcher(version.trim());
        if (!matcher.matches()) {
            throw new IllegalArgumentException("Wrong version format. Use semantic versioning!");
        }
        int major, minor, patch;
        try {
            major = Integer.parseInt(matcher.group(3));
            minor = Integer.parseInt(matcher.group(4));
            patch = Integer.parseInt(matcher.group(5));
        } catch (NumberFormatException exception) {
            throw new IllegalArgumentException("Wrong version format. Use semantic versioning!", exception);
        }
        String metadata = "";
        if (version.contains("+")) {
            metadata = version.substring(version.indexOf("+") + 1);
        } else if (version.contains("-")) {
            metadata = version.substring(version.indexOf("-") + 1);
        }
        return new SemanticVersion(major, minor, patch, metadata);
    }

    public boolean isNewerThan(SemanticVersion other) {
        if (this.major != other.major) {
            return this.major > other.major;
        }
        if (this.minor != other.minor) {
            return this.minor > other.minor;
        }
        return this.patch > other.patch;
    }

    public boolean isOlderThan(SemanticVersion other) {
        if (this.major != other.major) {
            return this.major < other.major;
        }
        if (this.minor != other.minor) {
            return this.minor < other.minor;
        }
        return this.patch < other.patch;
    }


    public boolean isEqualTo(SemanticVersion other) {
        return this.major == other.major && this.minor == other.minor && this.patch == other.patch;
    }

    public boolean isEqualOrNewerThan(SemanticVersion other) {
        return isEqualTo(other) || isNewerThan(other);
    }

    public boolean isEqualOrOlderThan(SemanticVersion other) {
        return isEqualTo(other) || isOlderThan(other);
    }

    public String asShortPrettyString() {
        return String.format("%d.%d.%d", this.major, this.minor, this.patch);
    }

    @Override
    public String toString() {
        return "SematicVersion{" +
                "major=" + this.major +
                ", minor=" + this.minor +
                ", patch=" + this.patch +
                ", metadata='" + this.metadata + '\'' +
                '}';
    }
}
