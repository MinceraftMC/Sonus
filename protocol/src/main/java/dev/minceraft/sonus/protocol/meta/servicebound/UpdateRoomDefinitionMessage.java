package dev.minceraft.sonus.protocol.meta.servicebound;


import dev.minceraft.sonus.common.participant.builtin.RoomDefinition;
import dev.minceraft.sonus.common.util.codec.DataTypeUtil;
import dev.minceraft.sonus.protocol.meta.IMetaHandler;
import dev.minceraft.sonus.protocol.meta.IMetaMessage;
import io.netty.buffer.ByteBuf;
import org.checkerframework.checker.nullness.qual.MonotonicNonNull;
import org.jspecify.annotations.NullMarked;

@NullMarked
public class UpdateRoomDefinitionMessage implements IMetaMessage {

    private @MonotonicNonNull RoomDefinition definition;

    @Override
    public void encode(ByteBuf buf) {
        RoomDefinition.write(buf, DataTypeUtil.VAR_INT, this.definition);
    }

    @Override
    public void decode(ByteBuf buf) {
        this.definition = RoomDefinition.read(buf, DataTypeUtil.VAR_INT);
    }

    @Override
    public void handle(IMetaHandler handler) {
        handler.handleUpdateRoomDefinition(this);
    }

    public RoomDefinition getDefinition() {
        return this.definition;
    }

    public void setDefinition(RoomDefinition definition){
        this.definition = definition;
    }
}
