package dev.minceraft.sonus.api.service.rooms;

import dev.minceraft.sonus.api.service.ISonusServicePlayer;
import dev.minceraft.sonus.api.service.participant.ISonusListener;
import org.jspecify.annotations.Nullable;

import java.util.Set;

public interface IRoom extends ISonusListener {

    String getName();

    @Nullable
    String getPassword();

    void setPassword(@Nullable String password);

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
