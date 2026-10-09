package dev.minceraft.sonus.plasmo.protocol;


import dev.minceraft.sonus.common.protocol.adapter.UdpSonusAdapter;
import dev.minceraft.sonus.common.protocol.udp.AbstractMagicUdpCodec;
import dev.minceraft.sonus.plasmo.protocol.udp.UdpPlasmoPacket;
import io.leangen.geantyref.TypeToken;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

public class PlasmoUdpMagicCodec extends AbstractMagicUdpCodec<UdpPlasmoPacket<?>> {

    public static final int MAGIC_NUMBER = 0x4e9004e9;
    public static final byte MAGIC_BYTE = (byte) ((MAGIC_NUMBER >> 24) & 0xFF);
    public static final int MAGIC_BYTE_REST = MAGIC_NUMBER & 0xFFFFFF; // The last 3 bytes of the magic number

    public PlasmoUdpMagicCodec(UdpSonusAdapter adapter) {
        super(adapter, MAGIC_BYTE, new TypeToken<UdpPlasmoPacket<?>>() {});
    }

    @Override
    protected ByteBuf allocateMagicBuffer() {
        ByteBuf buffer = Unpooled.buffer(4, 4);
        buffer.writeInt(MAGIC_NUMBER);
        return buffer;
    }

    @Override
    public void clearRestMagicByte(ByteBuf buf) {
        int i = buf.readUnsignedMedium(); // Read the next 3 bytes as a medium (24 bits)
        if (i != MAGIC_BYTE_REST) {
            throw new IllegalStateException("Invalid magic byte rest: " + i);
        }
    }
}
