package dev.minceraft.sonus.common.service;

import dev.minceraft.sonus.common.data.ISonusPlayer;
import dev.minceraft.sonus.common.data.SonusPlayerState;
import dev.minceraft.sonus.common.rooms.IRoom;
import net.kyori.adventure.key.Key;
import org.jspecify.annotations.NullMarked;

import java.util.Set;
import java.util.UUID;

@NullMarked
public interface ISonusServiceEvents {

    default void onPlayerSwitchBackend(UUID playerId) {
    }

    default void onPlayerDisconnect(ISonusPlayer player) {
    }

    default void onPlayerQuit(ISonusPlayer player) {
    }

    default void onPlayerNickUpdate(ISonusPlayer player, UUID previousNick) {
    }

    default void onPlayerStateUpdate(ISonusPlayer player, boolean globalUpdate) {
    }

    default void onChannelRegistered(UUID playerId, Set<Key> channel) {
    }

    default void onPrimaryRoomJoined(ISonusPlayer player, IRoom room) {
    }

    default void onPrimaryRoomLeaved(ISonusPlayer player, IRoom room) {
    }

    default void onGroupCreate(IRoom room) {
    }

    default void onGroupRemove(IRoom room) {
    }

    default void onConnectionState(ISonusPlayer player) {
    }

    default void onPlayerVisibilityStateUpdate(ISonusPlayer player, ISonusPlayer target, SonusPlayerState state) {
    }
}
