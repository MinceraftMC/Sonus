package dev.minceraft.sonus.common.service;

import dev.minceraft.sonus.common.rooms.IRoom;
import dev.minceraft.sonus.common.rooms.RoomAudioType;
import dev.minceraft.sonus.common.rooms.options.RoomDefinition;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.UUID;

@NullMarked
public interface ISonusRoomManager {

    @Nullable IRoom getRoom(UUID uniqueId);

    Collection<IRoom> getRooms();
    
    default Collection<IRoom> getRooms(Class<? extends IRoom> roomClass) {
        Collection<IRoom> rooms = new ArrayList<>();
        for (IRoom room : this.getRooms()) {
            if (roomClass.isInstance(room)) {
                rooms.add(room);
            }
        }
        return rooms;
    }

    IRoom createStaticRoom(String name, @Nullable String password, RoomAudioType audioType, boolean persist);

    boolean createRoom(IRoom room);

    boolean removeRoom(IRoom room);

    void updateRoomDefinition(UUID serverId, RoomDefinition definition);
}
