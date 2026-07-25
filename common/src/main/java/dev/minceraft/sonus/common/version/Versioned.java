package dev.minceraft.sonus.common.version;

import org.jspecify.annotations.Nullable;

public record Versioned<T>(VersionedKeyEntry<T>... versionedKeys) {

    @SafeVarargs
    public Versioned {
    }

    public static <T> Versioned<T> createSingle(T key) {
        return new Versioned<>(entry(Integer.MIN_VALUE, key));
    }

    public static <T> VersionedKeyEntry<T> entry(int version, T key) {
        return new VersionedKeyEntry<>(version, key);
    }

    @Nullable
    public T getForVersion(int version) {
        VersionedKeyEntry<T> lastEntry = null;
        for (VersionedKeyEntry<T> entry : this.versionedKeys) {
            if (entry.version < version) {
                lastEntry = entry;
            } else {
                if (entry.version == version) {
                    return entry.key;
                }
                break;
            }
        }
        return lastEntry == null ? null : lastEntry.key;
    }

    public record VersionedKeyEntry<T>(int version, T key) {

    }
}
