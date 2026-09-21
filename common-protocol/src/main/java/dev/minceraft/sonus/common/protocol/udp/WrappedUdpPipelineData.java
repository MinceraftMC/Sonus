package dev.minceraft.sonus.common.protocol.udp;

import org.jspecify.annotations.NullMarked;

@NullMarked
public record WrappedUdpPipelineData(
        UdpBasedContext<?> context,
        AbstractMagicUdpCodec<?> codec,
        Object data
) {

    public WrappedUdpPipelineData(UdpBasedContext<?> context, AbstractMagicUdpCodec<?> codec, Object data) {
        this.context = context;
        this.codec = codec;
        this.data = data;
    }

    @SuppressWarnings("unchecked")
    public <T> T unwrap() {
        return (T) this.data;
    }

    public <T> T unwrapAndRecycle() {
        T packet = this.unwrap();
        this.context.recycle();
        return packet;
    }

    public WrappedUdpPipelineData withPacket(Object packet) {
        return new WrappedUdpPipelineData(this.context, this.codec, packet);
    }

    @Override
    public String toString() {
        return "WrappedUdpPipelineData{" +
                "context=" + context +
                ", codec=" + codec +
                ", data=" + data +
                '}';
    }
}

