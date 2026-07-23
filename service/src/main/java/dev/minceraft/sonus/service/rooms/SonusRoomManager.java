package dev.minceraft.sonus.service.rooms;

import dev.minceraft.sonus.common.rooms.RoomAudioType;
import dev.minceraft.sonus.service.rooms.options.RoomDefinition;
import dev.minceraft.sonus.service.service.ISonusRoomManager;
import dev.minceraft.sonus.service.SonusService;
import dev.minceraft.sonus.service.platform.IServer;
import net.kyori.adventure.text.logger.slf4j.ComponentLogger;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

@NullMarked
public class SonusRoomManager implements ISonusRoomManager {

    private static final ComponentLogger LOGGER = ComponentLogger.logger("Sonus");

    public final SonusService service;
    private final Map<UUID, IRoom> rooms = new ConcurrentHashMap<>();

    public SonusRoomManager(SonusService service) {
        this.service = service;
    }

    public void init() {
        this.service.getScheduler().schedule(this::tick, 0, 250L, TimeUnit.MILLISECONDS);
    }

    private void tick() {
        Set<IServer> servers = this.service.getPlatform().getServers();
        Set<UUID> serverIds = new HashSet<>(servers.size());
        for (IServer server : servers) {
            // construct room if it doesn't exist already
            this.rooms.computeIfAbsent(server.getUniqueId(), serverId -> {
                LOGGER.info("Creating room for server {} ({})...", server.getName(), serverId);
                return new ServerRoom(this.service, server, new RoomDefinition());
            });
            serverIds.add(server.getUniqueId());
        }
        // remove discarded rooms
        this.rooms.values().removeIf(room -> {
            if (room.checkDiscarded(serverIds)) {
                // broadcast removal
                this.service.getEventManager().onGroupRemove(room);
                return true;
            }
            return false;
        });
    }

    @Override
    public @Nullable IRoom getRoom(UUID uniqueId) {
        return this.rooms.get(uniqueId);
    }

    @Override
    public Collection<IRoom> getRooms() {
        return this.rooms.values();
    }

    @Override
    public IRoom createStaticRoom(String name, @Nullable String password, RoomAudioType audioType, boolean persist) {
        StaticRoom room = persist
                ? new StaticRoom(this.service, UUID.randomUUID())
                : new TransientStaticRoom(this.service, UUID.randomUUID());
        room.setName(name);
        room.setPassword(password);
        room.setRoomAudioType(audioType);
        this.createRoom(room);
        return room;
    }

    @Override
    public boolean createRoom(IRoom room) {
        if (this.rooms.putIfAbsent(room.getId(), room) == null) {
            // broadcast room creation
            this.service.getEventManager().onGroupCreate(room);
            return true;
        }
        return false;
    }

    @Override
    public boolean removeRoom(IRoom room) {
        if (this.rooms.remove(room.getId()) != null) {
            // broadcast room removal
            this.service.getEventManager().onGroupRemove(room);
            return true;
        }
        return false;
    }

    @Override
    public void updateRoomDefinition(UUID serverId, RoomDefinition definition) {
        if (this.rooms.get(serverId) instanceof DefinedRoom room) {
            room.updateDefinition(definition);
        }
    }
}
