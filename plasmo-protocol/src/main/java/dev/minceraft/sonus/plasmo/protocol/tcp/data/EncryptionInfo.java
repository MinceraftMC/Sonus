package dev.minceraft.sonus.plasmo.protocol.tcp.data;


import dev.minceraft.sonus.common.util.codec.DataTypeUtil;
import dev.minceraft.sonus.common.util.codec.Utf8String;
import io.netty.buffer.ByteBuf;

import java.util.Arrays;

public class EncryptionInfo {

    private final String algorithm;
    private final byte[] data;

    public EncryptionInfo(String algorithm, byte[] data) {
        this.algorithm = algorithm;
        this.data = data;
    }

    public EncryptionInfo(ByteBuf buf) {
        this.algorithm = Utf8String.readUnsignedShort(buf);
        this.data = DataTypeUtil.INT.readByteArray(buf);
    }

    public void write(ByteBuf buf) {
        Utf8String.writeUnsignedShort(buf, this.algorithm);
        DataTypeUtil.INT.writeByteArray(buf, this.data);
    }

    public String getAlgorithm() {
        return this.algorithm;
    }

    public byte[] getData() {
        return this.data;
    }

    @Override
    public String toString() {
        return "EncryptionInfo{" +
                "algorithm='" + algorithm + '\'' +
                ", data=" + Arrays.toString(data) +
                '}';
    }
}
