package dev.minceraft.sonus.plasmo.protocol.tcp.clientbound;


import dev.minceraft.sonus.common.util.codec.DataTypeUtil;
import dev.minceraft.sonus.common.util.codec.Utf8String;
import dev.minceraft.sonus.plasmo.protocol.tcp.TcpHandler;
import dev.minceraft.sonus.plasmo.protocol.tcp.TcpPlasmoPacket;
import io.netty.buffer.ByteBuf;
import org.checkerframework.checker.nullness.qual.MonotonicNonNull;
import org.jspecify.annotations.NullMarked;

import java.util.HashMap;
import java.util.Map;

@NullMarked
public class LanguagePacket extends TcpPlasmoPacket<LanguagePacket> {

    private @MonotonicNonNull String language;
    private @MonotonicNonNull Map<String, String> languageMap;

    public LanguagePacket() {
    }

    @Override
    public void encode(ByteBuf buf) {
        Utf8String.writeUnsignedShort(buf, this.language);
        DataTypeUtil.INT.writeMap(buf, this.languageMap, Utf8String::writeUnsignedShort, Utf8String::writeUnsignedShort);
    }

    @Override
    public void decode(ByteBuf buf) {
        this.language = Utf8String.readUnsignedShort(buf);
        this.languageMap = DataTypeUtil.INT.readMap(buf, Utf8String::readUnsignedShort, Utf8String::readUnsignedShort, HashMap::new);
    }

    @Override
    public void handle(TcpHandler handler) {
        handler.handleLanguagePacket(this);
    }

    public String getLanguage() {
        return this.language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public Map<String, String> getLanguageMap() {
        return this.languageMap;
    }

    public void setLanguageMap(Map<String, String> languageMap) {
        this.languageMap = languageMap;
    }
}
