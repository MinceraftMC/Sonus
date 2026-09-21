package dev.minceraft.sonus.service.api.manager;

import dev.minceraft.sonus.api.service.rooms.ISonusRoom;
import dev.minceraft.sonus.api.service.rooms.RoomAudioType;
import dev.minceraft.sonus.common.adapter.service.ISonusRoomManager;
import dev.minceraft.sonus.common.participant.builtin.IRoom;
import dev.minceraft.sonus.service.api.ApiDelegation;
import dev.minceraft.sonus.service.api.ApiRoom;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@NullMarked
public class ApiRoomManager extends ApiDelegation<ISonusRoomManager> implements dev.minceraft.sonus.api.service.manager.ISonusRoomManager {

    public ApiRoomManager(ISonusRoomManager delegate) {
        super(delegate);
    }

    @Override
    public ISonusRoom createStatic(String name, @Nullable String password, RoomAudioType audioType, boolean persist) {
        dev.minceraft.sonus.common.participant.builtin.RoomAudioType type = switch (audioType) {
            case NORMAL -> dev.minceraft.sonus.common.participant.builtin.RoomAudioType.NORMAL;
            case OPEN -> dev.minceraft.sonus.common.participant.builtin.RoomAudioType.OPEN;
            case ISOLATED -> dev.minceraft.sonus.common.participant.builtin.RoomAudioType.ISOLATED;
        };

        return new ApiRoom(this.delegate.createStaticRoom(name, password, type, persist));
    }

    @Override
    public @Nullable ISonusRoom getRoom(UUID id) {
        IRoom room = this.delegate.getRoom(id);
        if (room == null) {
            return null;
        }
        return new ApiRoom(room);
    }

    @Override
    public boolean removeRoom(ISonusRoom room) {
        return this.delegate.removeRoom(((ApiRoom) room).getDelegate());
    }

    @Override
    public Collection<? extends ISonusRoom> getRooms() {
        Set<ApiRoom> rooms = new HashSet<>();
        for (IRoom room : this.delegate.getRooms()) {
            rooms.add(new ApiRoom(room));
        }

        return Set.copyOf(rooms);
    }
}
