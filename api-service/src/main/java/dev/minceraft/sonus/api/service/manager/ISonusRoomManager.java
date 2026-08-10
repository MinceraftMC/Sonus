package dev.minceraft.sonus.api.service.manager;

import dev.minceraft.sonus.api.service.rooms.ISonusRoom;
import dev.minceraft.sonus.api.service.rooms.RoomAudioType;
import org.jetbrains.annotations.Unmodifiable;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.Collection;
import java.util.UUID;

/**
 * The sonus service room manager
 */
@NullMarked
public interface ISonusRoomManager {

    /**
     * Creates a static voice room.
     * <p>
     * A persist room will be not automatically deleted, after the last player left the room.
     *
     * @param name      the name of the room
     * @param password  the password, leave it blank for no authentication
     * @param audioType isolation type, see {@link RoomAudioType}
     * @param persist   whether persist or not
     * @return the created room
     */
    ISonusRoom createStatic(String name, @Nullable String password, RoomAudioType audioType, boolean persist);

    /**
     * Gets the {@link ISonusRoom} from an id
     *
     * @param id the id
     * @return the room
     */
    @Nullable
    ISonusRoom getRoom(UUID id);

    /**
     * Removes the target room, and kicks out all players
     *
     * @param room the target room
     * @return if the deletion was successfully. Some rooms cannot be deleted or have already been deleted.
     */
    boolean removeRoom(ISonusRoom room);

    /**
     * Get all the rooms
     *
     * @return the rooms
     */
    @Unmodifiable
    Collection<? extends ISonusRoom> getRooms();
}
