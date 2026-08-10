package dev.minceraft.sonus.api.service.event;

import dev.minceraft.sonus.api.service.participant.builtin.ISonusServicePlayer;
import dev.minceraft.sonus.api.service.rooms.ISonusRoom;
import net.kyori.adventure.key.Key;
import org.jspecify.annotations.NullMarked;

import java.util.Set;
import java.util.UUID;

/**
 * The sonus event interface
 */
@NullMarked
public interface ISonusEvents {

    /**
     * Called when a player switches backend
     *
     * @param playerId the id of the player
     */
    default void onPlayerSwitchBackend(UUID playerId) {
    }

    /**
     * Called when the player disconnects from sonus
     *
     * @param player the player
     */
    default void onPlayerDisconnect(ISonusServicePlayer player) {
    }

    /**
     * Called when the player quits from the network
     *
     * @param player the quiting player
     */
    default void onPlayerQuit(ISonusServicePlayer player) {
    }

    /**
     * Called when a nick system triggered a nick update
     *
     * @param player       the target player
     * @param previousNick previous nicked id
     */
    default void onPlayerNickUpdate(ISonusServicePlayer player, UUID previousNick) {
    }

    /**
     * Called when the sonus state is updated.
     *
     * @param player       the target player
     * @param globalUpdate internally used for specific updates
     */
    default void onPlayerStateUpdate(ISonusServicePlayer player, boolean globalUpdate) {
    }

    /**
     * Called when a player has registered plugin message channels
     *
     * @param playerId the target players' id
     * @param channel  set of channels
     */
    default void onChannelRegistered(UUID playerId, Set<Key> channel) {
    }

    /**
     * Called when a player joins a primary room.
     *
     * @param player the player
     * @param room   the target room
     */
    default void onPrimaryRoomJoined(ISonusServicePlayer player, ISonusRoom room) {
    }

    /**
     * Called when a player leaved a primary room
     *
     * @param player the player
     * @param room   the previous room
     */
    default void onPrimaryRoomLeaved(ISonusServicePlayer player, ISonusRoom room) {
    }

    /**
     * Called when a room has been created
     *
     * @param room the created room
     */
    default void onGroupCreate(ISonusRoom room) {
    }

    /**
     * Called when a room has been deleted
     *
     * @param room the deleted room
     */
    default void onGroupRemove(ISonusRoom room) {
    }

    /**
     * Called when a player has a valid audio connection
     *
     * @param player the player
     */
    default void onConnectionState(ISonusServicePlayer player) {
    }
}
