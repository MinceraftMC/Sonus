package dev.minceraft.sonus.plasmo.protocol.tcp.data;

import dev.minceraft.sonus.common.protocol.util.DataTypeUtil;
import dev.minceraft.sonus.common.protocol.util.Utf8String;
import dev.minceraft.sonus.common.protocol.util.GameProfile;
import io.netty.buffer.ByteBuf;
import org.jetbrains.annotations.NotNull;

import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class VoiceSourceLine {

    private final UUID id;
    private final String name;
    private final String translation;
    private final String icon;
    private final double defaultVolume;
    private final int weight;
    private final Set<GameProfile> players;

    public VoiceSourceLine(ByteBuf buf) {
        this.name = Utf8String.readUnsignedShort(buf);
        this.id = generateId(this.name);
        this.translation = Utf8String.readUnsignedShort(buf);
        this.icon = Utf8String.readUnsignedShort(buf);
        this.defaultVolume = buf.readDouble();
        this.weight = buf.readInt();
        this.players = DataTypeUtil.readIfOrElse(buf, b -> DataTypeUtil.INT.readCollection(b, _b -> DataTypeUtil.INT.readGameProfile(_b, Utf8String::readUnsignedShort), HashSet::new), HashSet::new);
    }

    public VoiceSourceLine(String name, String translation, String icon, double defaultVolume, int weight, Set<GameProfile> players) {
        this.id = generateId(name);
        this.name = name;
        this.translation = translation;
        this.icon = icon;
        this.defaultVolume = defaultVolume;
        this.weight = weight;
        this.players = players;
    }

    public static UUID generateId(@NotNull String name) {
        return UUID.nameUUIDFromBytes((name + "_line").getBytes(StandardCharsets.UTF_8));
    }

    public void write(ByteBuf buf) {
        Utf8String.writeUnsignedShort(buf, this.name);
        Utf8String.writeUnsignedShort(buf, this.translation);
        Utf8String.writeUnsignedShort(buf, this.icon);
        buf.writeDouble(this.defaultVolume);
        buf.writeInt(this.weight);
        DataTypeUtil.writeIf(buf, !this.players.isEmpty(), b -> DataTypeUtil.INT.writeCollection(b, this.players,
                (_b, profile) -> DataTypeUtil.INT.writeGameProfile(_b, profile, Utf8String::writeUnsignedShort)));
    }

    public UUID getId() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }

    public String getTranslation() {
        return this.translation;
    }

    public String getIcon() {
        return this.icon;
    }

    public double getDefaultVolume() {
        return this.defaultVolume;
    }

    public int getWeight() {
        return this.weight;
    }

    public Set<GameProfile> getPlayers() {
        return this.players;
    }

    @Override
    public String toString() {
        return "VoiceSourceLine{" + "id=" + id + ", name='" + name + '\'' + ", translation='" + translation + '\'' + ", icon='" + icon + '\'' + ", defaultVolume=" + defaultVolume + ", weight=" + weight + ", players=" + players + '}';
    }
}
