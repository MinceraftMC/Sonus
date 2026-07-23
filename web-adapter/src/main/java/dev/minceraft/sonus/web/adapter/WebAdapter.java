package dev.minceraft.sonus.web.adapter;
// Created by booky10 in Sonus (02:19 10.08.2025)

import dev.minceraft.sonus.common.IAudioSource;
import dev.minceraft.sonus.common.ISonusService;
import dev.minceraft.sonus.common.adapter.AdapterInfo;
import dev.minceraft.sonus.common.adapter.SonusAdapter;
import dev.minceraft.sonus.common.audio.AudioCategory;
import dev.minceraft.sonus.common.audio.SonusAudio;
import dev.minceraft.sonus.common.data.ISonusPlayer;
import dev.minceraft.sonus.common.data.Vec3d;
import dev.minceraft.sonus.common.data.WorldRotatedVec3d;
import dev.minceraft.sonus.web.adapter.config.WebConfig;
import dev.minceraft.sonus.web.adapter.connection.WebSocketConnection;
import dev.minceraft.sonus.web.adapter.rtc.RtcHandler;
import dev.minceraft.sonus.web.adapter.rtc.RtcManager;
import dev.minceraft.sonus.web.adapter.util.AudioSpatialToStereoUtil;
import dev.minceraft.sonus.web.protocol.packets.clientbound.CategoryAddPacket;
import dev.minceraft.sonus.web.protocol.packets.clientbound.CategoryRemovePacket;
import dev.minceraft.sonus.web.protocol.packets.commonbound.KeepAlivePacket;
import dev.minceraft.sonus.web.protocol.packets.servicebound.VolumePacket.VolumeType;
import org.checkerframework.checker.nullness.qual.MonotonicNonNull;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.UUID;

@NullMarked
public class WebAdapter implements SonusAdapter {

    private final WebSessionManager sessions = new WebSessionManager(this);
    private final WebServer server = new WebServer(this);
    private @MonotonicNonNull ISonusService service;
    private @MonotonicNonNull AdapterInfo adapterInfo;
    private @MonotonicNonNull RtcManager webrtc;

    @Override
    public void load(ISonusService service) {
        this.service = service;
        service.getConfigHolder().registerConfigTemplate("web", WebConfig.class, WebConfig::new);
    }

    private AdapterInfo buildAdapterInfo() {
        return new AdapterInfo("web", this.service.getConfig().getSubConfig(WebConfig.class).enabled);
    }

    @Override
    public void init(ISonusService service) {
        this.server.openSocket();
        this.webrtc = new RtcManager(service.getOpusNatives(),
                () -> service.getConfig().getSubConfig(WebConfig.class));
        this.service.getEventManager().registerListener(new WebSonusListener(this));
    }

    @Override
    public void shutdown(ISonusService service) {
        try (RtcManager ignoredWebrtc = this.webrtc) {
            this.server.shutdown();
        }
    }

    private void sendAudio(ISonusPlayer player, IAudioSource source, SonusAudio audio, @Nullable Vec3d pos) {
        RtcHandler rtcHandler = this.webrtc.getPeer(player.getUniqueId());
        if (rtcHandler == null || !rtcHandler.isConnected()) {
            return;
        }
        WebSocketConnection connection = rtcHandler.getSignalConnection();
        // as we mix webrtc audio on the server, we have
        // to calculate voice activity on the server too
        if (source instanceof ISonusPlayer) {
            connection.setVoiceActive(source.getSenderId(player), true);
        }

        short[] leftData = audio.pcm();
        short[] rightData = leftData;
        // transform from spatial to stereo
        if (pos != null && player.getPosition() != null) {
            rightData = new short[leftData.length]; // create new
            AudioSpatialToStereoUtil.process(
                    leftData, pos, player.getPosition(),
                    leftData, rightData);
        }

        // apply source-specific volumes server-side
        float volume = connection.getVolume(VolumeType.PLAYER, source.getSenderId(player));
        UUID categoryId = source.getCategoryId();
        if (categoryId != null) {
            volume *= connection.getVolume(VolumeType.CATEGORY, categoryId);
        }

        // append to audio mixer queue
        UUID channelId = source.getSenderId(player);
        rtcHandler.queueAudio(channelId, leftData, rightData, volume);
    }

    @Override
    public void sendStaticAudio(ISonusPlayer player, IAudioSource source, SonusAudio audio) {
        this.sendAudio(player, source, audio, null);
    }

    @Override
    public void sendSpatialAudio(ISonusPlayer player, IAudioSource source, SonusAudio audio, Vec3d pos) {
        this.sendAudio(player, source, audio, pos);
    }

    @Override
    public void sendSpatialAudio(ISonusPlayer player, IAudioSource source, SonusAudio audio) {
        WorldRotatedVec3d sourcePos = source.getPosition();
        // we can only support spatial audio with source position
        if (sourcePos != null) {
            this.sendAudio(player, source, audio, sourcePos);
        }
    }

    @Override
    public void sendAudioEnd(ISonusPlayer player, IAudioSource source, long sequence) {
        WebSocketConnection connection = this.sessions.getConnection(player.getUniqueId());
        if (connection != null) {
            connection.setVoiceActive(source.getSenderId(player), false);
        }
    }

    @Override
    public void registerCategory(ISonusPlayer player, AudioCategory category) {
        WebSocketConnection connection = this.sessions.getConnection(player.getUniqueId());
        if (connection != null) {
            CategoryAddPacket packet = new CategoryAddPacket();
            packet.setCategory(category);
            connection.sendPacket(packet);
        }
    }

    @Override
    public void unregisterCategory(ISonusPlayer player, UUID categoryId) {
        WebSocketConnection connection = this.sessions.getConnection(player.getUniqueId());
        if (connection != null) {
            CategoryRemovePacket packet = new CategoryRemovePacket();
            packet.setCategoryId(categoryId);
            connection.sendPacket(packet);
        }
    }

    @Override
    public void sendKeepAlive(ISonusPlayer player, long currentTime) {
        WebSocketConnection connection = this.sessions.getConnection(player.getUniqueId());
        if (connection != null) {
            KeepAlivePacket packet = new KeepAlivePacket();
            packet.setId(currentTime);
            connection.sendPacket(packet);
        }
    }

    @Override
    public AdapterInfo getAdapterInfo() {
        if (this.adapterInfo == null) {
            this.adapterInfo = this.buildAdapterInfo();
        }
        return this.adapterInfo;
    }

    public ISonusService getService() {
        return this.service;
    }

    public WebSessionManager getSessions() {
        return this.sessions;
    }

    public RtcManager getWebRtc() {
        return this.webrtc;
    }
}
