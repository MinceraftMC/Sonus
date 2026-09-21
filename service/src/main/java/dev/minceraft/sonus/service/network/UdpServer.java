package dev.minceraft.sonus.service.network;
// Created by booky10 in Sonus (01:08 10.08.2025)

import dev.minceraft.sonus.common.adapter.config.ISonusConfig;
import dev.minceraft.sonus.common.protocol.udp.AbstractMagicUdpCodec;
import dev.minceraft.sonus.common.protocol.udp.IUdpServer;
import dev.minceraft.sonus.common.protocol.udp.WrappedUdpPipelineData;
import dev.minceraft.sonus.network.TransportType;
import dev.minceraft.sonus.service.SonusService;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import org.checkerframework.checker.nullness.qual.MonotonicNonNull;
import org.jspecify.annotations.NullMarked;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

@NullMarked
public class UdpServer implements IUdpServer {

    private static final Logger LOGGER = LoggerFactory.getLogger("Sonus");
    private static final TransportType TRANSPORT = TransportType.get();

    private final SonusService service;
    private final EventLoopGroup bossGroup = TRANSPORT.createGroup("Boss");
    private final MagicMessageCodec sonusCodec = new MagicMessageCodec();
    private final Map<String, ChannelHandler> handlers = new LinkedHashMap<>();

    private @MonotonicNonNull Channel channel;

    public UdpServer(SonusService service) {
        this.service = service;
    }

    public void bind() {
        ISonusConfig config = this.service.getConfig();
        ChannelFuture future = new Bootstrap()
                .option(ChannelOption.SO_REUSEADDR, true) // allow re-use
                .group(this.bossGroup)
                .channelFactory(TRANSPORT.getDatagramChannelFactory())
                .handler(new ChannelInitializer<>() {
                    @Override
                    protected void initChannel(Channel channel) {
                        channel.pipeline().addLast("magic-codec", UdpServer.this.sonusCodec);
                        UdpServer.this.handlers.forEach(channel.pipeline()::addLast);
                    }
                })
                .bind(config.getBind());

        CompletableFuture.runAsync(() -> {
            future.awaitUninterruptibly();
            this.channel = future.channel();

            Throwable error = future.cause();
            if (error != null) {
                LOGGER.error("Error while binding sonus server", error);
            } else {
                LOGGER.info("Sonus server bound on {} (host {})", config.getBind(), config.getHost());
            }
        });
    }

    public void shutdown() {
        LOGGER.info("Shutting down sonus server...");
        if (this.channel != null) {
            this.channel.close();
        }
        this.bossGroup.shutdownGracefully(1L, 5L, TimeUnit.SECONDS).awaitUninterruptibly();
    }

    @Override
    public <T> void registerCodec(AbstractMagicUdpCodec<T> codec) {
        this.sonusCodec.registerCodec(codec);
    }

    @Override
    public void registerHandler(String name, ChannelHandler handler) {
        this.handlers.put(name, handler);
    }

    @Override
    public InetSocketAddress getRemoteAddress() {
        return this.service.getConfig().getHost();
    }

    @Override
    public void sendPacket(WrappedUdpPipelineData data) {
        if (this.channel != null) {
            this.channel.writeAndFlush(data);
        } else {
            LOGGER.warn("Cannot send packet, server hasn't booted yet");
        }
    }
}
