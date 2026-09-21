package dev.minceraft.sonus.service;
// Created by booky10 in Sonus (01:08 10.08.2025)

import dev.minceraft.sonus.common.config.ConfigHolder;
import dev.minceraft.sonus.common.adapter.config.ISonusConfig;
import dev.minceraft.sonus.common.config.ISubConfig;
import dev.minceraft.sonus.common.config.SubConfigSection;
import dev.minceraft.sonus.common.protocol.codec.OpusCodec;
import org.jspecify.annotations.NullMarked;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;

import java.net.InetSocketAddress;
import java.util.List;

@NullMarked
@ConfigSerializable
public class SonusConfig implements ISonusConfig {

    private InetSocketAddress bind = new InetSocketAddress("0.0.0.0", 9982);
    private InetSocketAddress host = new InetSocketAddress("127.0.0.1", 9982);

    private OpusCodec opusCodec = OpusCodec.VOIP;
    private int mtuSize = 1412;
    private double voiceChatRange = 32.0;
    private boolean allowRecordings = false;
    private int keepAliveMs = 1000;
    private int keepAliveTimeoutMs = 30000;
    private boolean autoGainControl = true;
    private int cleanupTaskIntervalMs = 60000;
    private SubConfigSection adapterConfigs = SubConfigSection.EMPTY;

    public static SonusConfig createWithTemplates(ConfigHolder<?, ?> holder) {
        SonusConfig config = new SonusConfig();
        // create default adapter configs with specified config templates
        config.adapterConfigs = new SubConfigSection(holder.getConfigTemplates(), List.of());
        return config;
    }

    @Override
    public InetSocketAddress getBind() {
        return this.bind;
    }

    @Override
    public InetSocketAddress getHost() {
        return this.host;
    }

    @Override
    public int getMtuSize() {
        return this.mtuSize;
    }

    @Override
    public OpusCodec getOpusCodec() {
        return this.opusCodec;
    }

    @Override
    public double getVoiceChatRange() {
        return this.voiceChatRange;
    }

    @Override
    public boolean allowRecordings() {
        return this.allowRecordings;
    }

    @Override
    public int getKeepAliveMs() {
        return this.keepAliveMs;
    }

    @Override
    public int getKeepAliveTimeoutMs() {
        return this.keepAliveTimeoutMs;
    }

    @Override
    public boolean agcEnabled() {
        return this.autoGainControl;
    }

    @Override
    public int getCleanupTaskIntervalMs() {
        return this.cleanupTaskIntervalMs;
    }

    @Override
    public <T extends ISubConfig> T getSubConfig(Class<T> configClass) {
        return this.adapterConfigs.getConfig(configClass);
    }
}
