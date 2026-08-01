package dev.minceraft.sonus.api.service.event;

import dev.minceraft.sonus.api.service.participant.builtin.ISonusServicePlayer;
import dev.minceraft.sonus.api.service.rooms.ISonusRoom;
import net.kyori.adventure.key.Key;

import java.util.Set;
import java.util.UUID;

public interface ISonusEvents {

    default void onPlayerSwitchBackend(UUID playerId) {
    }

    default void onPlayerDisconnect(ISonusServicePlayer player) {
    }

    default void onPlayerQuit(ISonusServicePlayer player) {
    }

    default void onPlayerNickUpdate(ISonusServicePlayer player, UUID previousNick) {
    }

    default void onPlayerStateUpdate(ISonusServicePlayer player, boolean globalUpdate) {
    }

    default void onChannelRegistered(UUID playerId, Set<Key> channel) {
    }

    default void onPrimaryRoomJoined(ISonusServicePlayer player, ISonusRoom room) {
    }

    default void onPrimaryRoomLeaved(ISonusServicePlayer player, ISonusRoom room) {
    }

    default void onGroupCreate(ISonusRoom room) {
    }

    default void onGroupRemove(ISonusRoom room) {
    }

    default void onConnectionState(ISonusServicePlayer player) {
    }
}
