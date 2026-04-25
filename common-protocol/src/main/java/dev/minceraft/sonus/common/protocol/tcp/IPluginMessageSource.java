package dev.minceraft.sonus.common.protocol.tcp;
// Created by booky10 in Sonus (02:27 16.11.2025)

import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.UUID;

@NullMarked
public interface IPluginMessageSource {

    UUID getPlayerId();

    @Nullable UUID getServerId();

    interface Player {
    }

    interface Server {
    }
}
