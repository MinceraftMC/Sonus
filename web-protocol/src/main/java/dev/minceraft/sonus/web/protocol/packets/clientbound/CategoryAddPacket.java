package dev.minceraft.sonus.web.protocol.packets.clientbound;
// Created by booky10 in Sonus (20:33 28.11.2025)

import dev.minceraft.sonus.common.audio.AudioCategory;
import dev.minceraft.sonus.web.protocol.WsPacketContext;
import dev.minceraft.sonus.web.protocol.packets.IWebSocketHandler;
import dev.minceraft.sonus.web.protocol.packets.WebSocketPacket;
import io.netty.buffer.ByteBuf;
import org.checkerframework.checker.nullness.qual.MonotonicNonNull;
import org.jspecify.annotations.NullMarked;

@NullMarked
public class CategoryAddPacket extends WebSocketPacket {

    private @MonotonicNonNull AudioCategory category;

    public CategoryAddPacket(AudioCategory category) {
        this.category = category;
    }

    public CategoryAddPacket() {
    }

    @Override
    public void encode(ByteBuf buf, WsPacketContext context) {
        AudioCategory.encode(buf, this.category);
    }

    @Override
    public void decode(ByteBuf buf, WsPacketContext context) {
        this.category = AudioCategory.decode(buf);
    }

    @Override
    public void handle(IWebSocketHandler handler) {
        handler.handleCategoryAdd(this);
    }

    public AudioCategory getCategory() {
        return this.category;
    }

    public void setCategory(AudioCategory category) {
        this.category = category;
    }
}
