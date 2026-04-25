package dev.minceraft.sonus.common.rooms.options;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import dev.minceraft.sonus.common.IAudioSource;
import dev.minceraft.sonus.common.data.ISonusPlayer;
import dev.minceraft.sonus.common.protocol.util.DataTypeUtil;
import dev.minceraft.sonus.common.protocol.util.Utf8String;
import dev.minceraft.sonus.common.protocol.util.VarInt;
import io.netty.buffer.ByteBuf;
import org.jspecify.annotations.Nullable;

import java.util.UUID;

public final class RoomDefinition {

    public static final String NULL_TEAM = "@null";
    public static final String FALLBACK_TEAM = "@fallback";

    private final Table<UUID, UUID, RelationState> staticOverrides = HashBasedTable.create();
    private final Table<String, String, RelationState> teamOverrides = HashBasedTable.create();
    private RelationState defaultState = RelationState.SPATIAL;
    private boolean dirty = true;

    public static RoomDefinition read(ByteBuf buf, DataTypeUtil types) {
        RoomDefinition def = new RoomDefinition();
        def.defaultState = RelationState.read(buf);
        def.staticOverrides.putAll(types.readTable(buf, DataTypeUtil::readUniqueId,
                DataTypeUtil::readUniqueId, RelationState::read));
        def.teamOverrides.putAll(types.readTable(buf, Utf8String::read, Utf8String::read, RelationState::read));
        return def;
    }

    public static void write(ByteBuf buf, DataTypeUtil types, RoomDefinition def) {
        RelationState.write(buf, def.defaultState);
        types.writeTable(buf, def.staticOverrides, DataTypeUtil::writeUniqueId,
                DataTypeUtil::writeUniqueId, RelationState::write);
        types.writeTable(buf, def.teamOverrides, Utf8String::write, Utf8String::write, RelationState::write);
    }

    public RoomDefinition setStatic(UUID sender, UUID receiver, @Nullable RelationState state) {
        if (state != null) {
            this.staticOverrides.put(sender, receiver, state);
        } else {
            this.staticOverrides.remove(sender, receiver);
        }
        this.markDirty();
        return this;
    }

    public RoomDefinition setTeam(String senderTeam, String receiverTeam, @Nullable RelationState state) {
        if (state != null) {
            this.teamOverrides.put(senderTeam, receiverTeam, state);
        } else {
            this.teamOverrides.remove(senderTeam, receiverTeam);
        }
        this.markDirty();
        return this;
    }

    public RelationState getDefault() {
        return this.defaultState;
    }

    public RoomDefinition setDefault(RelationState state) {
        this.defaultState = state;
        return this;
    }

    public RelationState getState(IAudioSource sender, ISonusPlayer receiver) {
        if (sender.getSenderId(receiver).equals(receiver.getUniqueId())) {
            return RelationState.HIDE;
        }
        // check for static relation
        RelationState staticState = this.staticOverrides.get(sender.getSenderId(receiver), receiver.getUniqueId());
        if (staticState != null) {
            return staticState;
        }
        // check for team relation
        if (sender instanceof ISonusPlayer playerSender) {
            String senderTeam = playerSender.getTeam();
            if (senderTeam == null) {
                senderTeam = NULL_TEAM;
            }
            String receiverTeam = receiver.getTeam();
            if (receiverTeam == null) {
                receiverTeam = NULL_TEAM;
            }
            RelationState teamState = this.teamOverrides.get(senderTeam, receiverTeam);
            if (teamState != null) {
                return teamState;
            }
            teamState = this.teamOverrides.get(senderTeam, FALLBACK_TEAM);
            if (teamState != null) {
                return teamState;
            }
        }
        // return default
        return this.defaultState;
    }

    private void markDirty() {
        this.dirty = true;
    }

    public boolean checkDirty() {
        if (this.dirty) {
            this.dirty = false;
            return true;
        }
        return false;
    }

    public Table<String, String, RelationState> getTeamOverrides() {
        return this.teamOverrides;
    }

    public Table<UUID, UUID, RelationState> getStaticOverrides() {
        return this.staticOverrides;
    }

    public enum RelationState {

        STATIC,
        SPATIAL,
        SPATIAL_NORMALIZED,
        HIDE,
        ;

        private static final RelationState[] STATES = values();

        public static RelationState read(ByteBuf buf) {
            return STATES[VarInt.read(buf)];
        }

        public static void write(ByteBuf buf, RelationState state) {
            VarInt.write(buf, state.ordinal());
        }
    }
}
