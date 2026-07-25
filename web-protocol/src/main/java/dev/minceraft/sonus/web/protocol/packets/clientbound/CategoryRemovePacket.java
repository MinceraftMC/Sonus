package dev.minceraft.sonus.web.protocol.packets.clientbound;
// Created by booky10 in Sonus (20:33 28.11.2025)

import dev.minceraft.sonus.common.util.codec.DataTypeUtil;
import dev.minceraft.sonus.web.protocol.WsPacketContext;
import dev.minceraft.sonus.web.protocol.packets.IWebSocketHandler;
import dev.minceraft.sonus.web.protocol.packets.WebSocketPacket;
import io.netty.buffer.ByteBuf;
import org.checkerframework.checker.nullness.qual.MonotonicNonNull;
import org.jspecify.annotations.NullMarked;

import java.util.UUID;

@NullMarked
public class CategoryRemovePacket extends WebSocketPacket {

    private @MonotonicNonNull UUID categoryId;

    public CategoryRemovePacket(UUID categoryId) {
        this.categoryId = categoryId;
    }

    public CategoryRemovePacket() {
    }

    @Override
    public void encode(ByteBuf buf, WsPacketContext context) {
        DataTypeUtil.writeUniqueId(buf, this.categoryId);
    }

    @Override
    public void decode(ByteBuf buf, WsPacketContext context) {
        this.categoryId = DataTypeUtil.readUniqueId(buf);
    }

    @Override
    public void handle(IWebSocketHandler handler) {
        handler.handleCategoryRemove(this);
    }

    public UUID getCategoryId() {
        return this.categoryId;
    }

    public void setCategoryId(UUID categoryId) {
        this.categoryId = categoryId;
    }
}
