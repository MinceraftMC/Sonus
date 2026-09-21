package dev.minceraft.sonus.service.server;
// Created by booky10 in Sonus (01:05 17.11.2025)

import dev.minceraft.sonus.common.adapter.data.ISonusServer;
import dev.minceraft.sonus.service.SonusService;
import dev.minceraft.sonus.service.platform.IServer;
import net.kyori.adventure.text.Component;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.UUID;

@NullMarked
public final class SonusServer implements ISonusServer {

    private final SonusService service;
    private final IServer platform;

    public SonusServer(SonusService service, IServer platform) {
        this.service = service;
        this.platform = platform;
    }

    @Override
    public UUID getUniqueId() {
        return this.platform.getUniqueId();
    }

    @Override
    public Component getName() {
        return this.platform.getName();
    }

    @Override
    public @Nullable String getType() {
        return this.platform.getType();
    }
}
