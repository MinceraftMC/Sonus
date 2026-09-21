package dev.minceraft.sonus.protocol.meta.servicebound;
// Created by booky10 in Sonus (01:15 17.07.2025)

import com.google.common.collect.Table;
import dev.minceraft.sonus.common.data.SonusPlayerState;
import dev.minceraft.sonus.common.data.WorldRotatedVec3d;
import dev.minceraft.sonus.common.util.codec.DataTypeUtil;
import dev.minceraft.sonus.common.util.codec.Utf8String;
import dev.minceraft.sonus.protocol.meta.IMetaHandler;
import dev.minceraft.sonus.protocol.meta.IMetaMessage;
import io.netty.buffer.ByteBuf;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.Map;
import java.util.UUID;

@NullMarked
public class BackendTickMessage implements IMetaMessage {

    private @Nullable Map<UUID, WorldRotatedVec3d> positions;
    private @Nullable Table<UUID, UUID, SonusPlayerState> perPlayerStates;
    private @Nullable Map<UUID, @Nullable String> teams;

    public BackendTickMessage() {
    }

    @Override
    public void decode(ByteBuf buf) {
        this.positions = DataTypeUtil.readNullable(buf, buffer ->
                DataTypeUtil.VAR_INT.readMap(buffer, DataTypeUtil::readUniqueId, WorldRotatedVec3d::read));
        this.perPlayerStates = DataTypeUtil.readNullable(buf, buffer -> DataTypeUtil.VAR_INT.readTable(buffer,
                DataTypeUtil::readUniqueId, DataTypeUtil::readUniqueId, dev.minceraft.sonus.common.data.SonusPlayerState::read));
        this.teams = DataTypeUtil.readNullable(buf, buffer ->
                DataTypeUtil.VAR_INT.readMap(buffer, DataTypeUtil::readUniqueId, buff ->
                        DataTypeUtil.readNullable(buff, Utf8String::read)));
    }

    @Override
    public void encode(ByteBuf buf) {
        DataTypeUtil.writeNullable(buf, this.positions, (buffer, positions) ->
                DataTypeUtil.VAR_INT.writeMap(buffer, positions, DataTypeUtil::writeUniqueId, WorldRotatedVec3d::write));
        DataTypeUtil.writeNullable(buf, this.perPlayerStates, (buffer, states) ->
                DataTypeUtil.VAR_INT.writeTable(buf, states, DataTypeUtil::writeUniqueId, DataTypeUtil::writeUniqueId, SonusPlayerState::write));
        DataTypeUtil.writeNullable(buf, this.teams, (buffer, teams) ->
                DataTypeUtil.VAR_INT.writeMap(buffer, teams, DataTypeUtil::writeUniqueId, (buff, string) ->
                        DataTypeUtil.writeNullable(buff, string, Utf8String::write)));
    }

    @Override
    public void handle(IMetaHandler handler) {
        handler.handleBackendTick(this);
    }

    public @Nullable Map<UUID, WorldRotatedVec3d> getPositions() {
        return this.positions;
    }

    public void setPositions(@Nullable Map<UUID, WorldRotatedVec3d> positions) {
        this.positions = positions;
    }

    public @Nullable Table<UUID, UUID, SonusPlayerState> getPerPlayerStates() {
        return this.perPlayerStates;
    }

    public void setPerPlayerStates(@Nullable Table<UUID, UUID, SonusPlayerState> perPlayerStates) {
        this.perPlayerStates = perPlayerStates;
    }

    @Nullable
    public Map<UUID, @Nullable String> getTeams() {
        return this.teams;
    }

    public void setTeams(@Nullable Map<UUID, @Nullable String> teams) {
        this.teams = teams;
    }
}
