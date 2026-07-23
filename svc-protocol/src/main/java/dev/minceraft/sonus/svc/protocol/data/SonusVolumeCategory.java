package dev.minceraft.sonus.svc.protocol.data;

import dev.minceraft.sonus.common.audio.AudioCategory;
import dev.minceraft.sonus.common.protocol.util.DataTypeUtil;
import dev.minceraft.sonus.common.protocol.util.Utf8String;
import dev.minceraft.sonus.svc.protocol.SvcPacketContext;
import io.netty.buffer.ByteBuf;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.Contract;
import org.jspecify.annotations.Nullable;

import java.util.UUID;
import java.util.function.Function;

public class SonusVolumeCategory {

    private final String id;
    private final String name;
    private final @Nullable String description;
    private final int @Nullable [][] icon;

    public SonusVolumeCategory(
            String id, String name,
            @Nullable String description,
            int @Nullable [][] icon
    ) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.icon = icon;
    }

    public SonusVolumeCategory(AudioCategory category, Function<Component, String> renderer) {
        this.id = generateId(category.getUniqueId());
        this.name = renderer.apply(category.getName());
        Component description = category.getDescription();
        this.description = description != null ? renderer.apply(description) : null;
        this.icon = null;
    }

    public SonusVolumeCategory(ByteBuf buf, SvcPacketContext ctx) {
        this.id = Utf8String.read(buf, 16);
        this.name = Utf8String.read(buf, 16);
        if (ctx.version() >= 19) {
            DataTypeUtil.readNullable(buf, Utf8String::read); // discard name translation key
        }
        this.description = DataTypeUtil.readNullable(buf, Utf8String::read);
        if (ctx.version() >= 19) {
            DataTypeUtil.readNullable(buf, Utf8String::read); // discard description translation key
        }
        this.icon = DataTypeUtil.readNullable(buf, SonusVolumeCategory::readIconBytes);
    }

    private static final char[] CHARSET = "abcdefghijklmnopqrstuvwxyz_".toCharArray();
    private static final int BASE = CHARSET.length;

    @Contract("null -> null; !null -> !null")
    public static @Nullable String generateId(@Nullable UUID uniqueId) {
        if (uniqueId == null) {
            return null;
        }
        long l = uniqueId.getMostSignificantBits() ^ uniqueId.getLeastSignificantBits();
        // just do a constant two chars per byte, much easier
        StringBuilder builder = new StringBuilder(Long.BYTES * 2);
        for (int i = 0; i < 8; i++) {
            int b = (int) (l & 0xFF);
            l >>>= 8L;
            builder.append(CHARSET[b / BASE]).append(CHARSET[b % BASE]);
        }
        return builder.toString();
    }

    private static int[][] readIconBytes(ByteBuf buf) {
        int[][] icon = new int[16][16];
        for (int x = 0; x < icon.length; x++) {
            for (int y = 0; y < icon.length; y++) {
                icon[x][y] = buf.readInt();
            }
        }
        return icon;
    }

    private static void writeIconBytes(ByteBuf buf, int[][] icon) {
        if (icon.length != 16) {
            throw new IllegalStateException("Icon is not 16x16");
        }
        for (int[] ints : icon) {
            for (int y = 0; y < icon.length; y++) {
                buf.writeInt(ints[y]);
            }
        }
    }

    public String getId() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }

    public @Nullable String getDescription() {
        return this.description;
    }

    public int[] @Nullable [] getIcon() {
        return this.icon;
    }

    public void encode(ByteBuf buf, SvcPacketContext ctx) {
        Utf8String.write(buf, this.id);
        Utf8String.write(buf, this.name);
        if (ctx.version() >= 19) {
            buf.writeBoolean(false); // name translation key
        }
        DataTypeUtil.writeNullable(buf, this.description, Utf8String::write);
        if (ctx.version() >= 19) {
            buf.writeBoolean(false); // description translation key
        }
        DataTypeUtil.writeNullableIf(buf, this.icon, i -> i[0] != null, SonusVolumeCategory::writeIconBytes);
    }
}
