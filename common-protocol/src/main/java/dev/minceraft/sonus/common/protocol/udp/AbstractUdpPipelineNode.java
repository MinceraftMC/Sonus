package dev.minceraft.sonus.common.protocol.udp;

import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import io.netty.util.Recycler;
import io.netty.util.ReferenceCountUtil;
import io.netty.util.concurrent.PromiseCombiner;
import io.netty.util.internal.TypeParameterMatcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public abstract class AbstractUdpPipelineNode<E, D, C extends UdpBasedContext<C>> extends ChannelDuplexHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger("Sonus");

    private static final Recycler<OutList> OUT_LIST_RECYCLER = new Recycler<OutList>() {
        @Override
        protected OutList newObject(Handle<OutList> handle) {
            return new OutList(handle);
        }
    };

    protected final AbstractMagicUdpCodec<?> allowedCodec;
    private final TypeParameterMatcher encodedMatcher;
    private final TypeParameterMatcher decodedMatcher;

    protected AbstractUdpPipelineNode(AbstractMagicUdpCodec<?> allowedCodec) {
        this.allowedCodec = allowedCodec;
        this.encodedMatcher = TypeParameterMatcher.find(this, AbstractUdpPipelineNode.class, "E");
        this.decodedMatcher = TypeParameterMatcher.find(this, AbstractUdpPipelineNode.class, "D");
    }

    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
        if (msg instanceof WrappedUdpPipelineData packet
                && packet.codec().equals(this.allowedCodec)
                && this.decodedMatcher.match(packet.data())
        ) {
            OutList out = OUT_LIST_RECYCLER.get();
            try {
                @SuppressWarnings("unchecked")
                D cast = (D) packet.data();
                @SuppressWarnings("unchecked")
                C context = (C) packet.context();
                this.encode(ctx, cast, out.list, context);

                out.write(ctx, promise, packet::withPacket);
            } catch (Throwable throwable) {
                LOGGER.error("Failed to encode packet: {}", msg.getClass().getSimpleName(), throwable);
            } finally {
                ReferenceCountUtil.release(msg);
                out.list.clear();
                out.handle.recycle(out);
            }
        } else {
            super.write(ctx, msg, promise);
        }
    }

    public abstract void encode(ChannelHandlerContext ctx, D msg, List<Object> out, C adapterCtx) throws Exception;

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg instanceof WrappedUdpPipelineData packet
                && packet.codec().equals(this.allowedCodec)
                && this.encodedMatcher.match(packet.data())
        ) {
            OutList out = OUT_LIST_RECYCLER.get();
            try {
                @SuppressWarnings("unchecked")
                E cast = (E) packet.data();
                @SuppressWarnings("unchecked")
                C context = (C) packet.context();
                this.decode(ctx, cast, out.list, context);

                for (Object obj : out.list) {
                    ctx.fireChannelRead(packet.withPacket(obj));
                }
            } catch (Throwable throwable) {
                LOGGER.error("Failed to decode packet: {}", msg.getClass().getSimpleName(), throwable);
            } finally {
                ReferenceCountUtil.release(msg);
                out.list.clear();
                out.handle.recycle(out);
            }
        } else {
            super.channelRead(ctx, msg);
        }
    }

    public abstract void decode(ChannelHandlerContext ctx, E msg, List<Object> out, C adapterCtx) throws Exception;

    private static final class OutList {

        private final List<Object> list = new ArrayList<>(1);
        private final Recycler.Handle<OutList> handle;

        public OutList(Recycler.Handle<OutList> handle) {
            this.handle = handle;
        }

        public void write(ChannelHandlerContext ctx, ChannelPromise promise, Function<Object, Object> transformer) {
            List<Object> list = this.list;
            int len = list.size();
            if (len == 1) {
                ctx.write(transformer.apply(list.getFirst()), promise);
                return;
            }

            // copied from MessageToMessageEncoder#writePromiseCombiner
            PromiseCombiner combiner = new PromiseCombiner(ctx.executor());
            for (int i = 0; i < len; ++i) {
                combiner.add(ctx.write(transformer.apply(list.get(i))));
            }
            combiner.finish(promise);
        }
    }
}
