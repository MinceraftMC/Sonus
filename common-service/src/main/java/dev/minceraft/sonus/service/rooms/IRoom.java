package dev.minceraft.sonus.service.rooms;

import dev.minceraft.sonus.service.IAudioSource;
import dev.minceraft.sonus.service.audio.SonusAudio;
import dev.minceraft.sonus.service.data.ISonusPlayer;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.Set;
import java.util.UUID;

@NullMarked
public interface IRoom extends IAudioSource {

    UUID getId();

    String getName();

    void setName(String name);

    @Nullable
    String getPassword();

    void setPassword(@Nullable String password);

    Set<ISonusPlayer> getMembers();

    boolean addMember(ISonusPlayer player);

    boolean removeMember(ISonusPlayer player);

    boolean isMember(ISonusPlayer player);

    RoomAudioType getRoomAudioType();

    void setRoomAudioType(RoomAudioType type);

    void sendAudio(@Nullable IAudioSource source, SonusAudio audio);

    void sendAudioEnd(@Nullable IAudioSource source, long sequence);

    default boolean checkDiscarded(@Nullable Set<UUID> serverIds) {
        return false; // never discarded by default
    }

    default boolean isVisible() {
        return false; // don't show rooms in list by default
    }

    @Override
    default UUID getSenderId(ISonusPlayer viewer) {
        return this.getId();
    }
}
