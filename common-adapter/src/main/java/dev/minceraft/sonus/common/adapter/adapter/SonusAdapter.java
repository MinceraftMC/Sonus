package dev.minceraft.sonus.common.adapter.adapter;
// Created by booky10 in Sonus (02:23 17.07.2025)

import dev.minceraft.sonus.common.data.Vec3d;
import dev.minceraft.sonus.common.participant.IAudioSource;
import dev.minceraft.sonus.common.protocol.adapter.UdpSonusAdapter;
import dev.minceraft.sonus.common.adapter.ISonusService;
import dev.minceraft.sonus.common.protocol.audio.AudioCategory;
import dev.minceraft.sonus.common.audio.SonusAudio;
import dev.minceraft.sonus.common.participant.builtin.ISonusPlayer;
import dev.minceraft.sonus.common.data.WorldRotatedVec3d;
import dev.minceraft.sonus.common.util.ISonusAdapterDummy;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.UUID;

@NullMarked
public interface SonusAdapter extends ISonusAdapterDummy {

    default void load(ISonusService service) {
    }

    void init(ISonusService service);

    default void shutdown(ISonusService service) {
    }

    void sendStaticAudio(ISonusPlayer player, IAudioSource source, SonusAudio audio);

    void sendSpatialAudio(ISonusPlayer player, IAudioSource source, SonusAudio audio, Vec3d pos);

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
