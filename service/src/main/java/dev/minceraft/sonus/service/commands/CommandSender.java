package dev.minceraft.sonus.service.commands;

import dev.minceraft.sonus.service.data.ISonusPlayer;
import net.kyori.adventure.text.Component;

import java.util.UUID;

public interface CommandSender {

    boolean hasPermission(String permission, boolean defaultValue);

    default boolean hasPermission(String permission) {
        return hasPermission(permission, false);
    }

    void sendMessage(Component component);

    String getNameFor(ISonusPlayer target);

    UUID getUniqueIdFor(ISonusPlayer target);
}
