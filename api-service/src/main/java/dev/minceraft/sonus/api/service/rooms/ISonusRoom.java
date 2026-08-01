package dev.minceraft.sonus.api.service.rooms;

import dev.minceraft.sonus.api.service.participant.builtin.ISonusServicePlayer;
import dev.minceraft.sonus.api.service.participant.ISonusSource;
import org.jetbrains.annotations.Unmodifiable;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.Set;

@NullMarked
public interface ISonusRoom extends ISonusSource {

    String getName();

    @Nullable
    String getPassword();

    void setPassword(@Nullable String password);

    @Unmodifiable
    Set<? extends ISonusServicePlayer> getMembers();

    boolean addMember(ISonusServicePlayer player);

    boolean removeMember(ISonusServicePlayer player);

    boolean isMember(ISonusServicePlayer player);

    RoomAudioType getRoomAudioType();

    void setRoomAudioType(RoomAudioType type);

    default boolean isVisible() {
        return false;
    }
}
