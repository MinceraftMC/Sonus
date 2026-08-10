package dev.minceraft.sonus.service.rooms;
// Created by booky10 in Sonus (19:28 15.11.2025)

import dev.minceraft.sonus.common.participant.builtin.RoomDefinition;
import dev.minceraft.sonus.service.SonusService;
import dev.minceraft.sonus.service.platform.IServer;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.Set;
import java.util.UUID;

@NullMarked
public class ServerRoom extends DefinedRoom {

    public ServerRoom(SonusService service, IServer server, RoomDefinition definition) {
        super(service, server.getUniqueId(), definition);
    }

    @Override
    public boolean checkDiscarded(@Nullable Set<UUID> serverIds) {
        return serverIds != null && !serverIds.contains(this.roomId);
    }
}
