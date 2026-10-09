package dev.minceraft.sonus.common.adapter.service;

import dev.minceraft.sonus.common.audio.SonusAudio;
import dev.minceraft.sonus.common.data.SonusPlayerState;
import dev.minceraft.sonus.common.participant.IAudioSource;
import dev.minceraft.sonus.common.participant.builtin.IRoom;
import dev.minceraft.sonus.common.participant.builtin.ISonusPlayer;
import net.kyori.adventure.key.Key;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

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

    @Nullable
    default SonusAudio onPlayerOutputAudio(ISonusPlayer receiver, IAudioSource source, SonusAudio audio) {
        return audio;
    }

    @Nullable
    default SonusAudio onPlayerInputAudio(ISonusPlayer sender, SonusAudio audio){
        return audio;
    }

    @Nullable
    default SonusAudio onPlayerInputPostAudio(ISonusPlayer sender, SonusAudio audio){
        return audio;
    }
}
