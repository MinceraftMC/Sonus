package dev.minceraft.sonus.svc.adapter.pipeline;

import dev.minceraft.sonus.common.util.codec.DataTypeUtil;
import dev.minceraft.sonus.svc.adapter.SvcProtocolAdapter;
import dev.minceraft.sonus.svc.adapter.SvcUdpPipelineNode;
import dev.minceraft.sonus.svc.adapter.connection.SvcConnection;
import dev.minceraft.sonus.svc.protocol.SvcUdpMagicCodec;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import org.jspecify.annotations.NullMarked;

import java.util.List;
import java.util.UUID;

@NullMarked
@ChannelHandler.Sharable
public class SvcPlayerMarkerCodec extends SvcUdpPipelineNode<ByteBuf, ByteBuf> {

    private final SvcProtocolAdapter adapter;

    public SvcPlayerMarkerCodec(SvcUdpMagicCodec svcCodec, SvcProtocolAdapter adapter) {
        super(svcCodec);
        this.adapter = adapter;
    }

    @Override
    public void encode(ChannelHandlerContext ctx, ByteBuf msg, List<Object> out, SvcUdpContext svcCtx) {
        out.add(msg); // Only used for serverbound packets, so we just pass it through
    }

    @Override
    public void decode(ChannelHandlerContext ctx, ByteBuf msg, List<Object> out, SvcUdpContext svcCtx) {
        UUID playerId = DataTypeUtil.readUniqueId(msg);
        SvcConnection connection = this.adapter.getAdapter().getSessions().getConnection(playerId);
        if (connection != null) {
            svcCtx.connection = connection;
            out.add(msg.slice());
        } else {
            msg.release();
        }
    }
}
