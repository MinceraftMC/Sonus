package dev.minceraft.sonus.api.service.rooms;

import dev.minceraft.sonus.api.service.participant.ISonusSource;
import dev.minceraft.sonus.api.service.participant.builtin.ISonusServicePlayer;
import org.jetbrains.annotations.Unmodifiable;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.Set;

/**
 * The Sonus room
 */
@NullMarked
public interface ISonusRoom extends ISonusSource {

    /**
     * Gets the displayname of the room
     *
     * @return the displayname
     */
    String getName();

    /**
     * Sets the displayname of the room.
     * <strong>WARNING: Connected players will not receive a room name update</strong>
     *
     * @param name the new name
     */
    void setName(String name);

    /**
     * Gets the password of the room. If none set, no authentification is required.
     *
     * @return the password
     */
    @Nullable
    String getPassword();

    /**
     * Sets the password of the room. If none set, no authentication is required
     *
     * @param password
     */
    void setPassword(@Nullable String password);

    /**
     * Get all members of the room
     *
     * @return the members
     */
    @Unmodifiable
    Set<? extends ISonusServicePlayer> getMembers();

    /**
     * Adds the player to the room
     *
     * @param player the target player
     * @return whether the join was successfully or not
     */
    boolean addMember(ISonusServicePlayer player);

    /**
     * Removes the player from the room
     *
     * @param player the target player
     * @return whether the removal was successfully or not. Maybe already left or was never in there...
     */
    boolean removeMember(ISonusServicePlayer player);

    /**
     * Checks if the player is member of this room
     *
     * @param player the target player
     * @return whether member or not
     */
    boolean isMember(ISonusServicePlayer player);

    /**
     * Gets the isolation type of this room. See {@link RoomAudioType}
     *
     * @return the isolation type
     */
    RoomAudioType getRoomAudioType();

    /**
     * Sets the isolation type of this room. See {@link RoomAudioType}
     *
     * @param type the isolation type
     */
    void setRoomAudioType(RoomAudioType type);

    /**
     * If this room is displayed in the client's room list or not
     *
     * @return whether displayed or not
     */
    boolean isVisible();
}
