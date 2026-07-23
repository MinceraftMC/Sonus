package dev.minceraft.sonus.service.audio;
// Created by booky10 in Sonus (00:41 17.11.2025)

import dev.minceraft.sonus.common.protocol.util.DataTypeUtil;
import io.netty.buffer.ByteBuf;
import net.kyori.adventure.text.Component;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.UUID;

@NullMarked
public class AudioCategory {

    private final UUID uniqueId;
    private final Component name;
    private final @Nullable Component description;

    public AudioCategory(UUID uniqueId, Component name, @Nullable Component description) {
        this.uniqueId = uniqueId;
        this.name = name;
        this.description = description;
    }

    public static void encode(ByteBuf buf, AudioCategory category) {
        DataTypeUtil.writeUniqueId(buf, category.uniqueId);
        DataTypeUtil.writeComponentJson(buf, category.name);
        DataTypeUtil.writeNullable(buf, category.description, DataTypeUtil::writeComponentJson);
    }

    public static AudioCategory decode(ByteBuf buf) {
        UUID uniqueId = DataTypeUtil.readUniqueId(buf);
        Component name = DataTypeUtil.readComponentJson(buf);
        Component description = DataTypeUtil.readNullable(buf, DataTypeUtil::readComponentJson);
        return new AudioCategory(uniqueId, name, description);
    }

    public UUID getUniqueId() {
        return this.uniqueId;
    }

    public Component getName() {
        return this.name;
    }

    public @Nullable Component getDescription() {
        return this.description;
    }
}
