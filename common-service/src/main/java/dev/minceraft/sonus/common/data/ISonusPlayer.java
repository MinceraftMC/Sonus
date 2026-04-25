package dev.minceraft.sonus.common.data;

import dev.minceraft.sonus.api.service.ISonusServicePlayer;
import dev.minceraft.sonus.common.IAudioSource;
import dev.minceraft.sonus.common.adapter.SonusAdapter;
import dev.minceraft.sonus.common.audio.SonusAudio;
import dev.minceraft.sonus.common.util.GameProfile;
import io.netty.buffer.ByteBuf;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.util.TriState;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.List;

@NullMarked
public interface ISonusPlayer extends IAudioSource, ISonusServicePlayer {

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

    @Nullable
    SonusAdapter getAdapter();

    boolean setAdapter(@Nullable SonusAdapter adapter);

    void setConnected(boolean connected);

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
}
