package dev.minceraft.sonus.web.pion.ipc.pipeline;
// Created by booky10 in Sonus (9:30 PM 06.03.2026)

import dev.minceraft.sonus.common.util.codec.VarInt;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;
import org.jspecify.annotations.NullMarked;

import java.util.List;

@NullMarked
@ChannelHandler.Sharable
public class FrameEncoder extends MessageToMessageEncoder<ByteBuf> {

    public static final FrameEncoder ENCODER = new FrameEncoder();

    private FrameEncoder() {
    }

    @Override
    protected void encode(ChannelHandlerContext ctx, ByteBuf msg, List<Object> out) {
        out.add(ctx.alloc().compositeBuffer(2)
                .addComponent(true, VarInt.buffer(ctx.alloc(), msg.readableBytes()))
                .addComponent(true, msg.retain()));
    }
}
