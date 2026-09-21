package dev.minceraft.sonus.plasmo.protocol.tcp.data;

import dev.minceraft.sonus.common.util.codec.DataTypeUtil;
import dev.minceraft.sonus.common.util.codec.Utf8String;
import io.netty.buffer.ByteBuf;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.Nullable;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class VoiceActivation {

    private final UUID id;
    private final String name;
    private final String translation;
    private final String icon;
    private final List<Integer> distances;
    private final int defaultDistance;
    private final boolean proximity;
    private final boolean transitive;
    private final boolean stereoSupported;
    private final @Nullable CodecInfo encoderInfo;
    private final int weight;

    public VoiceActivation(ByteBuf buf) {
        this.name = Utf8String.readUnsignedShort(buf);
        this.id = generateId(this.name);
        this.translation = Utf8String.readUnsignedShort(buf);
        this.icon = Utf8String.readUnsignedShort(buf);
        this.distances = DataTypeUtil.INT.readCollection(buf, ByteBuf::readInt, ArrayList::new);
        this.defaultDistance = buf.readInt();
        this.proximity = buf.readBoolean();
        this.stereoSupported = buf.readBoolean();
        this.transitive = buf.readBoolean();
        this.encoderInfo = DataTypeUtil.readNullable(buf, CodecInfo::new);
        this.weight = buf.readInt();
    }

    public VoiceActivation(
            String name, String translation, String icon, List<Integer> distances,
            int defaultDistance, boolean proximity, boolean stereoSupported, boolean transitive,
            @Nullable CodecInfo encoderInfo, int weight) {
        this.id = generateId(name);
        this.name = name;
        this.translation = translation;
        this.icon = icon;
        this.distances = distances;
        this.defaultDistance = defaultDistance;
        this.proximity = proximity;
        this.stereoSupported = stereoSupported;
        this.transitive = transitive;
        this.encoderInfo = encoderInfo;
        this.weight = weight;
    }

    public static UUID generateId(@NotNull String name) {
        return UUID.nameUUIDFromBytes((name + "_activation").getBytes(StandardCharsets.UTF_8));
    }

    public void write(ByteBuf buf) {
        Utf8String.writeUnsignedShort(buf, this.name);
        Utf8String.writeUnsignedShort(buf, this.translation);
        Utf8String.writeUnsignedShort(buf, this.icon);
        DataTypeUtil.INT.writeCollection(buf, this.distances, ByteBuf::writeInt);
        buf.writeInt(this.defaultDistance);
        buf.writeBoolean(this.proximity);
        buf.writeBoolean(this.stereoSupported);
        buf.writeBoolean(this.transitive);
        DataTypeUtil.writeNullable(buf, this.encoderInfo, (b, e) -> e.write(b));
        buf.writeInt(this.weight);
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

    public List<Integer> getDistances() {
        return this.distances;
    }

    public int getDefaultDistance() {
        return this.defaultDistance;
    }

    public boolean isProximity() {
        return this.proximity;
    }

    public boolean isTransitive() {
        return this.transitive;
    }

    public boolean isStereoSupported() {
        return this.stereoSupported;
    }

    public @Nullable CodecInfo getEncoderInfo() {
        return this.encoderInfo;
    }

    public int getWeight() {
        return this.weight;
    }

    @Override
    public String toString() {
        return "VoiceActivation{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", translation='" + translation + '\'' +
                ", icon='" + icon + '\'' +
                ", distances=" + distances +
                ", defaultDistance=" + defaultDistance +
                ", proximity=" + proximity +
                ", transitive=" + transitive +
                ", stereoSupported=" + stereoSupported +
                ", encoderInfo=" + encoderInfo +
                ", weight=" + weight +
                '}';
    }
}
