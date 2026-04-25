package dev.minceraft.sonus.common.config;

import dev.minceraft.sonus.common.protocol.codec.OpusCodec;

import java.net.InetSocketAddress;

public interface ISonusConfig {

    InetSocketAddress getBind();

    InetSocketAddress getHost();

    OpusCodec getOpusCodec();

    int getMtuSize();

    double getVoiceChatRange();

    boolean allowRecordings();

    int getKeepAliveMs();

    int getKeepAliveTimeoutMs();

    boolean agcEnabled();

    int getCleanupTaskIntervalMs();

    <T extends ISubConfig> T getSubConfig(Class<T> configClass);
}
