package dev.minceraft.sonus.common.util;

import java.util.List;
import java.util.UUID;

public record GameProfile(
        UUID uniqueId,
        String name,
        List<Property> properties
) {

    public static record Property(String name, String value, String signature) {
    }
}