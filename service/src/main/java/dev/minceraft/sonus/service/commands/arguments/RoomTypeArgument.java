package dev.minceraft.sonus.service.commands.arguments;


import dev.minceraft.sonus.common.participant.builtin.RoomAudioType;
import org.jspecify.annotations.NullMarked;

@NullMarked
public final class RoomTypeArgument extends EnumArgument<RoomAudioType> {

    public static final RoomTypeArgument INSTANCE = new RoomTypeArgument();

    private RoomTypeArgument() {
        super(RoomAudioType.class);
    }
}
