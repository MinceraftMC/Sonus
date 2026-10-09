package dev.minceraft.sonus.svc.protocol;


import dev.minceraft.sonus.common.protocol.adapter.UdpSonusAdapter;
import dev.minceraft.sonus.common.protocol.udp.AbstractMagicUdpCodec;
import dev.minceraft.sonus.svc.protocol.voice.SvcVoicePacket;
import io.leangen.geantyref.TypeToken;

public class SvcUdpMagicCodec extends AbstractMagicUdpCodec<SvcVoicePacket> {

    public static final byte MAGIC_BYTE = (byte) 0b11111111;

    public SvcUdpMagicCodec(UdpSonusAdapter adapter) {
        super(adapter, MAGIC_BYTE, new TypeToken<SvcVoicePacket>() {});
    }
}
