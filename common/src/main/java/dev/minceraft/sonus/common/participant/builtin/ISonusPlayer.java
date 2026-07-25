package dev.minceraft.sonus.common.participant.builtin;

import dev.minceraft.sonus.common.audio.SonusAudio;
import dev.minceraft.sonus.common.data.GameProfile;
import dev.minceraft.sonus.common.participant.IAudioListener;
import dev.minceraft.sonus.common.participant.IAudioSource;
import dev.minceraft.sonus.common.util.ISonusAdapterDummy;
import io.netty.buffer.ByteBuf;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.util.TriState;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.List;
import java.util.Locale;

@NullMarked
public interface ISonusPlayer extends IAudioSource, IAudioListener {

    String getName(@Nullable ISonusPlayer viewer);

    default String getName() {
        return this.getName(null);
    }

    @Nullable
    String getTextureHash(@Nullable ISonusPlayer viewer);

    @Nullable
    default String getTextureHash() {
        return this.getTextureHash(null);
    }

    default GameProfile getSimpleProfile(@Nullable ISonusPlayer viewer, GameProfile.Property... properties) {
        return new GameProfile(this.getUniqueId(viewer), this.getName(viewer), List.of(properties));
    }

    default GameProfile getSimpleProfile(GameProfile.Property... properties) {
        return this.getSimpleProfile(null, properties);
    }

    void setKeepAlive(long timestamp);

    long getLastKeepAlive();

    @Nullable
    String getTeam();

    @Nullable
    ISonusAdapterDummy getAdapter();

    boolean setAdapter(@Nullable ISonusAdapterDummy adapter);

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

    void setConnected(boolean connected);

    boolean isVoiceActive();

    void setVoiceActive(boolean voiceActive);

    void handleAudioInput(SonusAudio audio);

    void handleAudioInputEnd(long sequence);

    void sendPluginMessage(Key key, ByteBuf data);

    void sendBackendPluginMessage(Key key, ByteBuf data);

    void handleConnect();

    void ensureTabListed(ISonusPlayer target);

    default void updateState() {
        this.updateState(false);
    }

    void updateState(boolean globalUpdate);

    boolean hasPermission(String permission, boolean defaultValue);

    void setPermission(String permission, TriState state);

    boolean canSee(ISonusPlayer target);

    boolean canReceive(ISonusPlayer target);

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
