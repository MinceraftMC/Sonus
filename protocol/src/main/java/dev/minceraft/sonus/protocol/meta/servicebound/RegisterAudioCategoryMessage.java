package dev.minceraft.sonus.protocol.meta.servicebound;
// Created by booky10 in Sonus (00:51 17.11.2025)

import dev.minceraft.sonus.common.protocol.audio.AudioCategory;
import dev.minceraft.sonus.protocol.meta.IMetaHandler;
import dev.minceraft.sonus.protocol.meta.IMetaMessage;
import io.netty.buffer.ByteBuf;
import org.checkerframework.checker.nullness.qual.MonotonicNonNull;
import org.jspecify.annotations.NullMarked;

@NullMarked
public class RegisterAudioCategoryMessage implements IMetaMessage {

    private @MonotonicNonNull AudioCategory category;

    @Override
    public void encode(ByteBuf buf) {
        AudioCategory.encode(buf, this.category);
    }

    @Override
    public void decode(ByteBuf buf) {
        this.category = AudioCategory.decode(buf);
    }

    @Override
    public void handle(IMetaHandler handler) {
        handler.handleRegisterAudioCategory(this);
    }

    public AudioCategory getCategory() {
        return this.category;
    }

    public void setCategory(AudioCategory category) {
        this.category = category;
    }
}
