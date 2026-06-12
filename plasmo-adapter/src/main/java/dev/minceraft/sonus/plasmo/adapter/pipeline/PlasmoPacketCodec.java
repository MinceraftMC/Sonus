package dev.minceraft.sonus.plasmo.adapter.pipeline;

import dev.minceraft.sonus.plasmo.adapter.PlasmoUdpPipelineNode;
import dev.minceraft.sonus.plasmo.protocol.PlasmoUdpMagicCodec;
import dev.minceraft.sonus.plasmo.protocol.udp.UdpPlasmoPacket;
import dev.minceraft.sonus.plasmo.protocol.udp.UdpPlasmoRegistry;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;

import java.util.List;

public class PlasmoPacketCodec extends PlasmoUdpPipelineNode<ByteBuf, UdpPlasmoPacket<?>> {

    public PlasmoPacketCodec(PlasmoUdpMagicCodec plasmoCodec) {
        super(plasmoCodec);
    }

    @Override
    public void encode(ChannelHandlerContext ctx, UdpPlasmoPacket<?> msg, List<Object> out, PlasmoUdpContext adapterCtx) throws Exception {
        ByteBuf buffer = ctx.alloc().buffer();
        UdpPlasmoRegistry.REGISTRY.encode(buffer, msg);
        out.add(buffer);
    }

    @Override
    public void decode(ChannelHandlerContext ctx, ByteBuf msg, List<Object> out, PlasmoUdpContext adapterCtx) throws Exception {
        try {
            UdpPlasmoPacket<?> read = UdpPlasmoRegistry.REGISTRY.decode(msg);
            if (read != null) {
                out.add(read);
            }
        } finally {
            msg.release();
        }
    }
}
