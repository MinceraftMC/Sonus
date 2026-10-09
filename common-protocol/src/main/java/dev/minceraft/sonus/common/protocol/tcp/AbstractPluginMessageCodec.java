package dev.minceraft.sonus.common.protocol.tcp;

import io.netty.buffer.ByteBuf;
import net.kyori.adventure.key.Key;

import java.util.Set;

public abstract class AbstractPluginMessageCodec {

    private final Set<Key> supportedChannels;

    public AbstractPluginMessageCodec(Set<Key> supportedChannels) {
        this.supportedChannels = supportedChannels;
    }

    public Set<Key> getSupportedChannels() {
        return this.supportedChannels;
    }

    public abstract void handle(ByteBuf packet, Key channel, IPluginMessageSource source);
}
