package dev.minceraft.sonus.common.protocol.udp;


import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.internal.TypeParameterMatcher;

public abstract class AbstractUdpPipelineHandler<T, C extends UdpBasedContext<C>> extends SimpleChannelInboundHandler<WrappedUdpPipelineData> {

    private final AbstractMagicUdpCodec<?> codec;
    private final TypeParameterMatcher matcher;

    public AbstractUdpPipelineHandler(AbstractMagicUdpCodec<?> codec) {
        this.codec = codec;
        this.matcher = TypeParameterMatcher.find(this, AbstractUdpPipelineHandler.class, "T");
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, WrappedUdpPipelineData msg) throws Exception {
        if (msg.codec() == this.codec && this.matcher.match(msg.data())) {
            try {
                @SuppressWarnings("unchecked")
                C context = (C) msg.context();
                handle(ctx, msg.unwrap(), context);
            } finally {
                msg.context().recycle();
            }
        } else {
            ctx.fireChannelRead(msg);
        }
    }

    public abstract void handle(ChannelHandlerContext ctx, T msg, C adapterCtx);
}
