package dev.minceraft.sonus.web.pion.ipc.pipeline;
// Created by booky10 in Sonus (9:30 PM 06.03.2026)

import dev.minceraft.sonus.common.util.codec.QuietCodecException;
import dev.minceraft.sonus.common.util.codec.VarInt;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import org.jspecify.annotations.NullMarked;

import java.util.List;

@NullMarked
public class FrameDecoder extends ByteToMessageDecoder {

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) {
        int ri = in.readerIndex();
        try {
            int length = VarInt.read(in);
            if (in.isReadable(length)) {
                out.add(in.readRetainedSlice(length));
                ri = -1; // don't reset
            }
        } catch (QuietCodecException ignored) {
        } finally {
            // reset
            if (ri != -1) {
                in.readerIndex(ri);
            }
        }
    }
}
