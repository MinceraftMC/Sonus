package dev.minceraft.sonus.plasmo.protocol.tcp.data;


import dev.minceraft.sonus.common.util.codec.DataTypeUtil;
import io.netty.buffer.ByteBuf;

public class CaptureInfo {

    private final int sampleRate;
    private final int mtuSize;
    private final CodecInfo codecInfo;

    public CaptureInfo(ByteBuf buf) {
        this.sampleRate = buf.readInt();
        this.mtuSize = buf.readInt();
        this.codecInfo = DataTypeUtil.readNullable(buf, CodecInfo::new);
    }

    public CaptureInfo(int sampleRate, int mtuSize, CodecInfo codecInfo) {
        this.sampleRate = sampleRate;
        this.mtuSize = mtuSize;
        this.codecInfo = codecInfo;
    }

    public void write(ByteBuf buf) {
        buf.writeInt(this.sampleRate);
        buf.writeInt(this.mtuSize);
        DataTypeUtil.writeNullable(buf, this.codecInfo, (b, c) -> c.write(b));
    }

    public int getSampleRate() {
        return this.sampleRate;
    }

    public int getMtuSize() {
        return this.mtuSize;
    }

    public CodecInfo getCodecInfo() {
        return this.codecInfo;
    }

    @Override
    public String toString() {
        return "CaptureInfo{" +
                "sampleRate=" + sampleRate +
                ", mtuSize=" + mtuSize +
                ", codecInfo=" + codecInfo +
                '}';
    }
}
