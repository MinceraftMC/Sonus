package dev.minceraft.sonus.api.service.manager;

import dev.minceraft.sonus.api.service.rooms.ISonusRoom;
import dev.minceraft.sonus.api.service.rooms.RoomAudioType;
import org.jetbrains.annotations.Unmodifiable;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.Collection;
import java.util.UUID;

@NullMarked
public interface ISonusRoomManager {

    ISonusRoom createStatic(String name, @Nullable String password, RoomAudioType audioType, boolean persist);

    @Nullable
    ISonusRoom getRoom(UUID id);

    boolean removeRoom(ISonusRoom room);

    @Unmodifiable
    Collection<? extends ISonusRoom> getRooms();
}
