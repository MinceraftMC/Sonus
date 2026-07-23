package dev.minceraft.sonus.service.adapter;
// Created by booky10 in Sonus (02:23 17.07.2025)

import dev.minceraft.sonus.common.protocol.adapter.UdpSonusAdapter;
import dev.minceraft.sonus.service.IAudioSource;
import dev.minceraft.sonus.service.ISonusService;
import dev.minceraft.sonus.service.audio.AudioCategory;
import dev.minceraft.sonus.service.audio.SonusAudio;
import dev.minceraft.sonus.service.data.ISonusPlayer;
import dev.minceraft.sonus.service.data.WorldRotatedVec3d;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.UUID;

@NullMarked
public interface SonusAdapter {

    default void load(ISonusService service) {
    }

    void init(ISonusService service);

    default void shutdown(ISonusService service) {
    }

    void sendStaticAudio(ISonusPlayer player, IAudioSource source, SonusAudio audio);

    void sendSpatialAudio(ISonusPlayer player, IAudioSource source, SonusAudio audio, WorldRotatedVec3d pos);

    void sendSpatialAudio(ISonusPlayer player, IAudioSource source, SonusAudio audio);

    void sendAudioEnd(ISonusPlayer player, IAudioSource source, long sequence);

    void registerCategory(ISonusPlayer player, AudioCategory category);

    void unregisterCategory(ISonusPlayer player, UUID categoryId);

    void sendKeepAlive(ISonusPlayer player, long currentTime);

    default @Nullable UdpSonusAdapter getUdpAdapter() {
        return null;
    }

    AdapterInfo getAdapterInfo();
}
