package dev.minceraft.sonus.api.service.event;

import dev.minceraft.sonus.api.service.audio.ISonusAudio;
import dev.minceraft.sonus.api.service.participant.ISonusSource;
import dev.minceraft.sonus.api.service.participant.builtin.ISonusServicePlayer;
import dev.minceraft.sonus.api.service.rooms.ISonusRoom;
import net.kyori.adventure.key.Key;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

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

    /**
     * Called when a player will receive an audio frame. You can cancel the audio frame by returning null.
     *
     * @param receiver the player who will receive the audio frame
     * @param source   the source of the audio frame
     * @param audio    the audio frame
     * @return the audio frame to send to the player, or null to cancel the audio frame
     */
    @Nullable
    default ISonusAudio onPlayerOutputAudio(ISonusServicePlayer receiver, ISonusSource source, ISonusAudio audio) {
        return audio;
    }

    /**
     * Called when a player sends an audio frame. You can cancel the audio frame by returning null.
     * <p>
     * Note: This is called before internal processing, if you want to modify the audio after internal processing,
     * use {@link #onPlayerInputPostAudio(ISonusServicePlayer, ISonusAudio)} instead.
     *
     * @param sender the player who sent the audio frame
     * @param audio  the audio frame
     * @return the audio frame to send to the server, or null to cancel the audio frame
     */
    @Nullable
    default ISonusAudio onPlayerInputAudio(ISonusServicePlayer sender, ISonusAudio audio) {
        return audio;
    }

    /**
     * Called when a player sends an audio frame after internal processing. You can cancel the audio frame by returning null.
     * <p>
     * Note: This is called after internal processing, if you want to modify the audio before internal processing,
     * use {@link #onPlayerInputAudio(ISonusServicePlayer, ISonusAudio)} instead.
     *
     * @param sender the player who sent the audio frame
     * @param audio  the audio frame
     * @return the audio frame to send to the server, or null to cancel the audio frame
     */
    @Nullable
    default ISonusAudio onPlayerInputPostAudio(ISonusServicePlayer sender, ISonusAudio audio) {
        return audio;
    }
}
