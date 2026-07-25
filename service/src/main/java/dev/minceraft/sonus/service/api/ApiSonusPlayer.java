package dev.minceraft.sonus.service.api;

import dev.minceraft.sonus.api.service.ISonusServicePlayer;
import dev.minceraft.sonus.api.service.audio.ISonusAudio;
import dev.minceraft.sonus.api.service.rooms.ISonusRoom;
import dev.minceraft.sonus.common.participant.builtin.ISonusPlayer;
import dev.minceraft.sonus.service.api.audio.ApiAudio;
import dev.minceraft.sonus.service.api.participant.ApiSonusListenerAndSource;
import net.kyori.adventure.text.Component;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.Locale;

@NullMarked
public class ApiSonusPlayer extends ApiSonusListenerAndSource<ISonusPlayer> implements ISonusServicePlayer {

    public ApiSonusPlayer(ISonusPlayer delegate) {
        super(delegate);
    }

    @Override
    public String getName(@Nullable ISonusServicePlayer viewer) {
        return this.delegate.getName();
    }

    @Override
    public long getLastKeepAlive() {
        return this.delegate.getLastKeepAlive();
    }

    @Override
    public @Nullable String getTeam() {
        return this.delegate.getTeam();
    }

    @Override
    public boolean canAccessRoom(ISonusRoom room, @Nullable String password) {
        return this.delegate.canAccessRoom(((ApiRoom) room).getDelegate(), password);
    }

    @Override
    public void joinRoom(ISonusRoom room) {
        this.delegate.joinRoom(((ApiRoom) room).getDelegate());
    }

    @Override
    public void leaveRoom(ISonusRoom room) {
        this.delegate.joinRoom(((ApiRoom) room).getDelegate());
    }

    @Override
    public @Nullable ISonusRoom getServerRoom() {
        return new ApiRoom(this.delegate.getServerRoom());
    }

    @Override
    public void setServerRoom(@Nullable ISonusRoom room) {
        this.delegate.setServerRoom(room == null ? null : ((ApiRoom) room).getDelegate());
    }

    @Override
    public @Nullable ISonusRoom getPrimaryRoom() {
        return new ApiRoom(this.delegate.getPrimaryRoom());
    }

    @Override
    public void setPrimaryRoom(@Nullable ISonusRoom room) {
        this.delegate.setPrimaryRoom(room == null ? null : ((ApiRoom) room).getDelegate());
    }

    @Override
    public boolean isMuted() {
        return this.delegate.isMuted();
    }

    @Override
    public void setMuted(boolean mute, boolean ignorePermission) {
        this.delegate.setMuted(mute, ignorePermission);
    }

    @Override
    public boolean isDeafened() {
        return this.delegate.isDeafened();
    }

    @Override
    public void setDeafened(boolean deafened) {
        this.delegate.setDeafened(deafened);
    }

    @Override
    public boolean isConnected() {
        return this.delegate.isConnected();
    }

    @Override
    public boolean isVoiceActive() {
        return this.delegate.isVoiceActive();
    }

    @Override
    public void handleAudioInput(ISonusAudio audio) {
        this.delegate.handleAudioInput(((ApiAudio) audio).getDelegate());
    }

    @Override
    public void handleAudioInputEnd(long sequence) {
        this.delegate.handleAudioInputEnd(sequence);
    }

    @Override
    public boolean canSee(ISonusServicePlayer target) {
        return this.delegate.canSee(((ApiSonusPlayer) target).getDelegate());
    }

    @Override
    public boolean canReceive(ISonusServicePlayer target) {
        return this.delegate.canReceive(((ApiSonusPlayer) target).getDelegate());
    }

    @Override
    public Locale getLocale() {
        return this.delegate.getLocale();
    }

    @Override
    public Component renderComponent(Component component, Locale locale) {
        return this.delegate.renderComponent(component, locale);
    }

    @Override
    public String renderPlainComponent(Component component, Locale locale) {
        return this.delegate.renderPlainComponent(component, locale);
    }

    @Override
    public void sendMessage(Component message) {
        this.delegate.sendMessage(message);
    }

    @Override
    public boolean isOnline() {
        return this.delegate.isOnline();
    }

    @Override
    public void disconnect() {
        this.delegate.disconnect();
    }
}
