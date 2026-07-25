package dev.minceraft.sonus.service.api;

import dev.minceraft.sonus.api.service.ISonusServicePlayer;
import dev.minceraft.sonus.api.service.audio.ISonusAudio;
import dev.minceraft.sonus.api.service.participant.ISonusSource;
import dev.minceraft.sonus.api.service.rooms.ISonusRoom;
import dev.minceraft.sonus.api.service.rooms.RoomAudioType;
import dev.minceraft.sonus.api.service.util.Vec3d;
import dev.minceraft.sonus.service.data.ISonusPlayer;
import dev.minceraft.sonus.service.rooms.IRoom;
import org.jspecify.annotations.Nullable;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class ApiRoom extends ApiDelegation<IRoom> implements ISonusRoom {

    public ApiRoom(IRoom delegate) {
        super(delegate);
    }

    @Override
    public String getName() {
        return this.delegate.getName();
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
            case OPEN -> dev.minceraft.sonus.service.rooms.RoomAudioType.OPEN;
            case NORMAL -> dev.minceraft.sonus.service.rooms.RoomAudioType.NORMAL;
            case ISOLATED -> dev.minceraft.sonus.service.rooms.RoomAudioType.ISOLATED;
        });
    }

    @Override
    public void sendStaticAudio(ISonusSource source, ISonusAudio audio) {

    }

    @Override
    public void sendStaticAudioEnd(ISonusSource source, long sequence) {

    }

    @Override
    public void sendSpatialAudio(ISonusSource source, ISonusAudio audio, Vec3d position) {

    }

    @Override
    public void sendSpatialAudio(ISonusSource source, ISonusAudio audio) {

    }

    @Override
    public void sendSpatialNormedAudio(ISonusSource source, ISonusAudio audio) {

    }

    @Override
    public void sendSpatialAudioEnd(ISonusSource source, long sequence) {

    }

    @Override
    public UUID getUniqueId(@Nullable ISonusServicePlayer viewer) {
        return null;
    }
}
