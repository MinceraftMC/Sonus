package dev.minceraft.sonus.service.rooms;
// Created by booky10 in Sonus (02:18 17.07.2025)

import dev.minceraft.sonus.service.SonusService;
import dev.minceraft.sonus.common.audio.SonusAudio;
import dev.minceraft.sonus.common.participant.builtin.ISonusPlayer;
import dev.minceraft.sonus.common.participant.IAudioSource;
import org.jetbrains.annotations.ApiStatus;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@NullMarked
public abstract class AbstractRoom implements dev.minceraft.sonus.common.participant.builtin.IRoom {

    protected final SonusService service;
    protected final UUID roomId;
    protected String name;

    protected final Map<UUID, ISonusPlayer> members = new ConcurrentHashMap<>();

    protected dev.minceraft.sonus.common.participant.builtin.RoomAudioType roomAudioType = dev.minceraft.sonus.common.participant.builtin.RoomAudioType.OPEN;
    protected @Nullable String password;

    public AbstractRoom(SonusService service, UUID roomId) {
        this.service = service;
        this.roomId = roomId;
        this.name = "room_" + this.roomId.toString().substring(0, 8);
    }

    @Override
    public final void sendAudio(@Nullable IAudioSource source, SonusAudio audio) {
        IAudioSource realSource = Objects.requireNonNullElse(source, this);
        this.sendAudio0(realSource, audio);
    }

    @Override
    public void sendAudioEnd(@Nullable IAudioSource source, long sequence) {
        IAudioSource realSource = Objects.requireNonNullElse(source, this);
        this.sendAudioEnd0(realSource, sequence);
    }

    @ApiStatus.OverrideOnly
    protected abstract void sendAudio0(IAudioSource source, SonusAudio audio);

    @ApiStatus.OverrideOnly
    protected abstract void sendAudioEnd0(IAudioSource source, long sequence);

    @Override
    public boolean addMember(ISonusPlayer player) {
        boolean success = this.members.putIfAbsent(player.getUniqueId(), player) == null;
        if (success) {
            for (ISonusPlayer value : this.members.values()) {
                if (value != player) {
                    value.ensureTabListed(player);
                    player.ensureTabListed(value);
                }
            }
        }
        return success;
    }

    @Override
    public boolean removeMember(ISonusPlayer player) {
        return this.members.remove(player.getUniqueId()) != null;
    }

    @Override
    public UUID getUniqueId(@Nullable ISonusPlayer viewer) {
        return this.roomId;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    @Nullable
    public String getPassword() {
        return this.password;
    }

    @Override
    public void setPassword(@Nullable String password) {
        this.password = password;
    }

    @Override
    public dev.minceraft.sonus.common.participant.builtin.RoomAudioType getRoomAudioType() {
        return this.roomAudioType;
    }

    public void setRoomAudioType(dev.minceraft.sonus.common.participant.builtin.RoomAudioType type) {
        this.roomAudioType = Objects.requireNonNull(type);
    }

    @Override
    public Set<ISonusPlayer> getMembers() {
        return Set.copyOf(this.members.values());
    }

    @Override
    public boolean isMember(ISonusPlayer player) {
        return this.members.containsKey(player.getUniqueId());
    }
}
