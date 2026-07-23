package dev.minceraft.sonus.svc.adapter;
// Created by booky10 in Sonus (02:19 10.08.2025)

import dev.minceraft.sonus.common.IAudioSource;
import dev.minceraft.sonus.common.ISonusService;
import dev.minceraft.sonus.common.adapter.AdapterInfo;
import dev.minceraft.sonus.common.adapter.SonusAdapter;
import dev.minceraft.sonus.common.adapter.UdpSonusAdapter;
import dev.minceraft.sonus.common.audio.AudioCategory;
import dev.minceraft.sonus.common.audio.SonusAudio;
import dev.minceraft.sonus.common.data.ISonusPlayer;
import dev.minceraft.sonus.common.data.Vec3d;
import dev.minceraft.sonus.svc.adapter.config.SvcConfig;
import dev.minceraft.sonus.svc.adapter.connection.SvcConnection;
import dev.minceraft.sonus.svc.protocol.data.SonusVolumeCategory;
import dev.minceraft.sonus.svc.protocol.meta.clientbound.AddCategorySvcPacket;
import dev.minceraft.sonus.svc.protocol.meta.clientbound.RemoveCategorySvcPacket;
import dev.minceraft.sonus.svc.protocol.version.VersionManager;
import dev.minceraft.sonus.svc.protocol.voice.clientbound.GroupSoundSvcPacket;
import dev.minceraft.sonus.svc.protocol.voice.clientbound.LocationSoundSvcPacket;
import dev.minceraft.sonus.svc.protocol.voice.clientbound.PlayerSoundSvcPacket;
import dev.minceraft.sonus.svc.protocol.voice.commonbound.KeepAliveSvcPacket;
import org.checkerframework.checker.nullness.qual.MonotonicNonNull;
import org.jspecify.annotations.NullMarked;

import java.util.UUID;

@NullMarked
public class SvcAdapter implements SonusAdapter {

    private static final byte[] EMPTY_BUFFER = new byte[0];

    private @MonotonicNonNull SvcSessionManager sessions;
    private @MonotonicNonNull SvcProtocolAdapter protocolAdapter;
    private @MonotonicNonNull ISonusService service;
    private @MonotonicNonNull AdapterInfo adapterInfo;

    @Override
    public void load(ISonusService service) {
        this.service = service;
        service.getConfigHolder().registerConfigTemplate("svc", SvcConfig.class, SvcConfig::new);
    }

    private AdapterInfo buildAdapterInfo() {
        return new AdapterInfo("svc", this.service.getConfig().getSubConfig(SvcConfig.class).enabled);
    }

    @Override
    public void init(ISonusService service) {
        this.sessions = new SvcSessionManager(this);
        this.protocolAdapter = new SvcProtocolAdapter(this);

        this.service.getEventManager().registerListener(new SvcSonusListener(this));

        VersionManager.logSupportedVersions();
    }

    @Override
    public void sendStaticAudio(ISonusPlayer player, IAudioSource source, SonusAudio audio) {
        SvcConnection connection = this.sessions.getConnection(player.getUniqueId());
        if (connection == null) {
            return; // no svc session found
        }

        GroupSoundSvcPacket packet = new GroupSoundSvcPacket();
        packet.setChannelId(source.getSenderId(player));
        packet.setSender(source.getSenderId(player));
        packet.setCategory(SonusVolumeCategory.generateId(source.getCategoryId()));
        packet.setData(audio.setProcessor(() -> connection.getProcessor(source.getSenderId(player))).opus());
        packet.setSequenceNumber(audio.getSequenceNumber());
        connection.sendPacket(packet);
    }

    @Override
    public void sendSpatialAudio(ISonusPlayer player, IAudioSource source, SonusAudio audio, Vec3d pos) {
        SvcConnection connection = this.sessions.getConnection(player.getUniqueId());
        if (connection == null) {
            return; // no svc session found
        }

        LocationSoundSvcPacket packet = new LocationSoundSvcPacket();
        packet.setChannelId(source.getSenderId(player));
        packet.setSender(source.getSenderId(player));
        packet.setCategory(SonusVolumeCategory.generateId(source.getCategoryId()));
        packet.setData(audio.setProcessor(() -> connection.getProcessor(source.getSenderId(player))).opus());
        packet.setSequenceNumber(audio.getSequenceNumber());
        packet.setLocation(pos);
        packet.setDistance((float) this.service.getConfig().getVoiceChatRange());
        connection.sendPacket(packet);
    }

    @Override
    public void sendSpatialAudio(ISonusPlayer player, IAudioSource source, SonusAudio audio) {
        SvcConnection connection = this.sessions.getConnection(player.getUniqueId());
        if (connection == null) {
            return; // no svc session found
        }

        PlayerSoundSvcPacket packet = new PlayerSoundSvcPacket();
        packet.setChannelId(source.getSenderId(player));
        packet.setSender(source.getSenderId(player));
        packet.setCategory(SonusVolumeCategory.generateId(source.getCategoryId()));
        packet.setData(audio.setProcessor(() -> connection.getProcessor(source.getSenderId(player))).opus());
        packet.setSequenceNumber(audio.getSequenceNumber());
        packet.setDistance((float) this.service.getConfig().getVoiceChatRange());
        connection.sendPacket(packet);
    }

    @Override
    public void sendAudioEnd(ISonusPlayer player, IAudioSource source, long sequence) {
        SvcConnection connection = this.sessions.getConnection(player.getUniqueId());
        if (connection == null) {
            return; // no svc session found
        }

        GroupSoundSvcPacket packet = new GroupSoundSvcPacket(); // Generic sound packet
        packet.setChannelId(source.getSenderId(player));
        packet.setSender(source.getSenderId(player));
        packet.setCategory(SonusVolumeCategory.generateId(source.getCategoryId()));
        packet.setData(EMPTY_BUFFER);
        packet.setSequenceNumber(sequence);
        connection.sendPacket(packet);
    }

    @Override
    public void registerCategory(ISonusPlayer player, AudioCategory category) {
        SvcConnection connection = this.sessions.getConnection(player.getUniqueId());
        if (connection == null) {
            return; // no svc session found
        }
        AddCategorySvcPacket packet = new AddCategorySvcPacket();
        packet.setCategory(new SonusVolumeCategory(category, player::renderPlainComponent));
        connection.sendPacket(packet);
    }

    @Override
    public void unregisterCategory(ISonusPlayer player, UUID categoryId) {
        SvcConnection connection = this.sessions.getConnection(player.getUniqueId());
        if (connection == null) {
            return; // no svc session found
        }
        RemoveCategorySvcPacket packet = new RemoveCategorySvcPacket();
        packet.setCategoryId(SonusVolumeCategory.generateId(categoryId));
        connection.sendPacket(packet);
    }

    @Override
    public void sendKeepAlive(ISonusPlayer player, long currentTime) {
        SvcConnection connection = this.sessions.getConnection(player.getUniqueId());
        if (connection != null) {
            connection.sendPacket(KeepAliveSvcPacket.INSTANCE);
        }
    }

    @Override
    public UdpSonusAdapter getUdpAdapter() {
        return this.protocolAdapter;
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

    public SvcSessionManager getSessions() {
        return this.sessions;
    }
}
