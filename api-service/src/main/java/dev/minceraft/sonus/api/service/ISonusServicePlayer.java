package dev.minceraft.sonus.api.service;

import dev.minceraft.sonus.api.service.audio.ISonusAudio;
import dev.minceraft.sonus.api.service.participant.ISonusListener;
import dev.minceraft.sonus.api.service.participant.ISonusSource;
import dev.minceraft.sonus.api.service.rooms.IRoom;
import net.kyori.adventure.text.Component;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.Locale;

@NullMarked
public interface ISonusServicePlayer extends ISonusSource, ISonusListener {

    String getName(@Nullable ISonusServicePlayer viewer);

    default String getName() {
        return this.getName(null);
    }

    long getLastKeepAlive();

    @Nullable
    String getTeam();

    boolean canAccessRoom(IRoom room, @Nullable String password);

    void joinRoom(IRoom room);

    void leaveRoom(IRoom room);

    @Nullable
    IRoom getServerRoom();

    void setServerRoom(@Nullable IRoom room);

    @Nullable
    IRoom getPrimaryRoom();

    void setPrimaryRoom(@Nullable IRoom room);

    boolean isMuted();

    default void setMuted(boolean muted) {
        this.setMuted(muted, false);
    }

    void setMuted(boolean mute, boolean ignorePermission);

    boolean isDeafened();

    void setDeafened(boolean deafened);

    boolean isConnected();

    boolean isVoiceActive();

    void handleAudioInput(ISonusAudio audio);

    void handleAudioInputEnd(long sequence);

    boolean canSee(ISonusServicePlayer target);

    boolean canReceive(ISonusServicePlayer target);

    Locale getLocale();

    Component renderComponent(Component component, Locale locale);

    default Component renderComponent(Component component) {
        return this.renderComponent(component, this.getLocale());
    }

    String renderPlainComponent(Component component, Locale locale);

    default String renderPlainComponent(Component component) {
        return this.renderPlainComponent(component, this.getLocale());
    }

    void sendMessage(Component message);

    boolean isOnline();

    void disconnect();
}
