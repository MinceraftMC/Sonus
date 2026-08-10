package dev.minceraft.sonus.common.protocol.tcp.holder;

import dev.minceraft.sonus.common.protocol.util.AbstractRecyclerPair;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.util.Recycler;
import net.kyori.adventure.key.Key;

public class PmDataHolderBuf extends AbstractRecyclerPair<PmDataHolderBuf, ByteBuf, Key> {

    private static final Recycler<PmDataHolderBuf> PAIR_RECYCLER = createRecycler(PmDataHolderBuf::new);

    private PmDataHolderBuf(Recycler.Handle<PmDataHolderBuf> handle) {
        super(handle);
    }

    public static PmDataHolderBuf newInstance(ByteBuf buf, Key channel) {
        PmDataHolderBuf holder = PAIR_RECYCLER.get();
        holder.setFirst(buf);
        holder.setSecond(channel);
        return holder;
    }

    public static PmDataHolderBuf newInstance(Key channel) {
        PmDataHolderBuf holder = PAIR_RECYCLER.get();
        holder.setFirst(Unpooled.buffer());
        holder.setSecond(channel);
        return holder;
    }

    @Override
    public void clear() {
        this.getFirst().release();
        super.clear();
    }
}
