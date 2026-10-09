package dev.minceraft.sonus.common.participant.builtin;

import dev.minceraft.sonus.common.audio.SonusAudio;
import dev.minceraft.sonus.common.participant.IAudioSource;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.Set;
import java.util.UUID;

@NullMarked
public interface IRoom extends IAudioSource {

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
}
