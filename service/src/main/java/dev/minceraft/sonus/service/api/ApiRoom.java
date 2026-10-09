package dev.minceraft.sonus.service.api;

import dev.minceraft.sonus.api.service.participant.builtin.ISonusServicePlayer;
import dev.minceraft.sonus.api.service.rooms.ISonusRoom;
import dev.minceraft.sonus.api.service.rooms.RoomAudioType;
import dev.minceraft.sonus.common.participant.builtin.ISonusPlayer;
import dev.minceraft.sonus.common.participant.builtin.IRoom;
import dev.minceraft.sonus.service.api.participant.ApiSonusSource;
import dev.minceraft.sonus.service.api.participant.builtin.ApiSonusPlayer;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

@NullMarked
public class ApiRoom extends ApiSonusSource<IRoom> implements ISonusRoom {

    public ApiRoom(IRoom delegate) {
        super(delegate);
    }

    @Override
    public String getName() {
        return this.delegate.getName();
    }

    @Override
    public void setName(String name) {
        this.delegate.setName(name);
    }

    @Override
    public @Nullable String getPassword() {
        return this.delegate.getPassword();
    }

    @Override
    public void setPassword(@Nullable String password) {
        this.delegate.setPassword(password);
    }

    @Override
    public Set<? extends ISonusServicePlayer> getMembers() {
        Set<ISonusServicePlayer> players = new HashSet<>();
        for (ISonusPlayer member : this.delegate.getMembers()) {
            players.add(new ApiSonusPlayer(member));
        }
        return Collections.unmodifiableSet(players);
    }

    @Override
    public boolean addMember(ISonusServicePlayer player) {
        return this.delegate.addMember(((ApiSonusPlayer) player).getDelegate());
    }

    @Override
    public boolean removeMember(ISonusServicePlayer player) {
        return this.delegate.removeMember(((ApiSonusPlayer) player).getDelegate());
    }

    @Override
    public boolean isMember(ISonusServicePlayer player) {
        return this.delegate.isMember(((ApiSonusPlayer) player).getDelegate());
    }

    @Override
    public RoomAudioType getRoomAudioType() {
        return switch (this.delegate.getRoomAudioType()) {
            case OPEN -> RoomAudioType.OPEN;
            case NORMAL -> RoomAudioType.NORMAL;
            case ISOLATED -> RoomAudioType.ISOLATED;
        };
    }

    @Override
    public void setRoomAudioType(RoomAudioType type) {
        this.delegate.setRoomAudioType(switch (type) {
            case OPEN -> dev.minceraft.sonus.common.participant.builtin.RoomAudioType.OPEN;
            case NORMAL -> dev.minceraft.sonus.common.participant.builtin.RoomAudioType.NORMAL;
            case ISOLATED -> dev.minceraft.sonus.common.participant.builtin.RoomAudioType.ISOLATED;
        });
    }

    @Override
    public boolean isVisible() {
        return this.delegate.isVisible();
    }
}
