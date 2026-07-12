package dev.minceraft.sonus.svc.adapter.pipeline;

import dev.minceraft.sonus.svc.adapter.SvcUdpPipelineNode;
import dev.minceraft.sonus.svc.protocol.SvcUdpMagicCodec;
import dev.minceraft.sonus.svc.protocol.registries.SvcVoicePacketRegistry;
import dev.minceraft.sonus.svc.protocol.voice.SvcVoicePacket;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;

import java.util.List;

@ChannelHandler.Sharable
public class SvcPacketCodec extends SvcUdpPipelineNode<ByteBuf, SvcVoicePacket> {

    public SvcPacketCodec(SvcUdpMagicCodec svcCodec) {
        super(svcCodec);
    }

    @Override
    public void encode(ChannelHandlerContext ctx, SvcVoicePacket msg, List<Object> out, SvcUdpContext svcCtx) {
        ByteBuf buf = ctx.alloc().buffer();
        try {
            SvcVoicePacketRegistry.REGISTRY.encode(buf, msg, svcCtx.connection.getContext());
            out.add(buf.retain());
        } finally {
            buf.release();
        }
    }

    @Override
    public void decode(ChannelHandlerContext ctx, ByteBuf msg, List<Object> out, SvcUdpContext svcCtx) {
        try {
            SvcVoicePacket packet = SvcVoicePacketRegistry.REGISTRY.decode(msg, svcCtx.connection.getContext());
            if (packet != null) {
                out.add(packet);
            }
        } finally {
            msg.release();
        }
    }
}
