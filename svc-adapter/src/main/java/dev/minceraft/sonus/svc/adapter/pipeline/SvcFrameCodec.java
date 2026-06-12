package dev.minceraft.sonus.svc.adapter.pipeline;


import dev.minceraft.sonus.common.protocol.util.QuietCodecException;
import dev.minceraft.sonus.common.protocol.util.VarInt;
import dev.minceraft.sonus.svc.adapter.SvcUdpPipelineNode;
import dev.minceraft.sonus.svc.protocol.SvcUdpMagicCodec;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import org.jspecify.annotations.NullMarked;

import java.util.List;

// one of the most useless part's of SVC's protocol... Maybe for lost bytes? But hey then you could also use checksums
@NullMarked
@ChannelHandler.Sharable
public final class SvcFrameCodec extends SvcUdpPipelineNode<ByteBuf, ByteBuf> {

    public SvcFrameCodec(SvcUdpMagicCodec svcCodec) {
        super(svcCodec);
    }

    @Override
    public void encode(ChannelHandlerContext ctx, ByteBuf msg, List<Object> out, SvcUdpContext svcCtx) {
        out.add(ctx.alloc().compositeBuffer(2)
                .addComponent(true, VarInt.buffer(ctx.alloc(), msg.readableBytes()))
                .addComponent(true, msg));
    }

    @Override
    public void decode(ChannelHandlerContext ctx, ByteBuf msg, List<Object> out, SvcUdpContext svcCtx) {
        int size = VarInt.read(msg);
        if (msg.readableBytes() != size) {
            int readable = msg.readableBytes();
            msg.release();
            throw new QuietCodecException("Received invalid readable byte count: " + readable + " != " + size);
        }
        out.add(msg);
    }
}
