package dev.minceraft.sonus.common.rooms;

import dev.minceraft.sonus.common.IAudioSource;
import dev.minceraft.sonus.common.data.ISonusPlayer;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.Set;
import java.util.UUID;

@NullMarked
public interface IRoom extends IAudioSource, dev.minceraft.sonus.api.service.rooms.IRoom {

    String getName();

    void setName(String name);

    @Nullable
    String getPassword();

    void setPassword(@Nullable String password);

    Set<ISonusPlayer> getMembers();

    default boolean checkDiscarded(@Nullable Set<UUID> serverIds) {
        return false; // never discarded by default
    }
}
