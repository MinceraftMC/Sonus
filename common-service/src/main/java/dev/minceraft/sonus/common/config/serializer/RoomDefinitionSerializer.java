package dev.minceraft.sonus.common.config.serializer;

import dev.minceraft.sonus.common.rooms.options.RoomDefinition;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.serialize.TypeSerializer;

import java.lang.reflect.Type;
import java.util.Map;
import java.util.UUID;

public final class RoomDefinitionSerializer implements TypeSerializer<RoomDefinition> {

    public static final RoomDefinitionSerializer INSTANCE = new RoomDefinitionSerializer();

    private RoomDefinitionSerializer() {
    }

    @Override
    public RoomDefinition deserialize(Type type, ConfigurationNode node) throws SerializationException {
        if (node.virtual()) {
            return null;
        }
        RoomDefinition roomDefinition = new RoomDefinition();

        roomDefinition.setDefault(RoomDefinition.RelationState.valueOf(node.node("default-state").getString()));

        ConfigurationNode staticOverridesNode = node.node("static-overrides");
        for (Map.Entry<Object, ? extends ConfigurationNode> entry : staticOverridesNode.childrenMap().entrySet()) {
            UUID sender = UUID.fromString(entry.getKey().toString());
            ConfigurationNode columnNode = entry.getValue();
            for (Map.Entry<Object, ? extends ConfigurationNode> innerEntry : columnNode.childrenMap().entrySet()) {
                UUID receiver = UUID.fromString(innerEntry.getKey().toString());
                RoomDefinition.RelationState state = RoomDefinition.RelationState.valueOf(innerEntry.getValue().getString());
                roomDefinition.setStatic(sender, receiver, state);
            }
        }

        ConfigurationNode teamOverridesNode = node.node("team-overrides");
        for (Map.Entry<Object, ? extends ConfigurationNode> entry : teamOverridesNode.childrenMap().entrySet()) {
            String senderTeam = entry.getKey().toString();
            ConfigurationNode columnNode = entry.getValue();
            for (Map.Entry<Object, ? extends ConfigurationNode> innerEntry : columnNode.childrenMap().entrySet()) {
                String receiverTeam = innerEntry.getKey().toString();
                RoomDefinition.RelationState state = RoomDefinition.RelationState.valueOf(innerEntry.getValue().getString());
                roomDefinition.setTeam(senderTeam, receiverTeam, state);
            }
        }

        return roomDefinition;
    }

    @Override
    public void serialize(Type type, @Nullable RoomDefinition obj, ConfigurationNode node) throws SerializationException {
        if (obj == null) {
            node.set(null);
            return;
        }
        node.node("default-state").set(obj.getDefault());

        ConfigurationNode staticOverridesNode = node.node("static-overrides");
        for (Map.Entry<UUID, Map<UUID, RoomDefinition.RelationState>> entry : obj.getStaticOverrides().rowMap().entrySet()) {
            ConfigurationNode columnNode = staticOverridesNode.node(entry.getKey().toString());
            for (Map.Entry<UUID, RoomDefinition.RelationState> innerEntry : entry.getValue().entrySet()) {
                columnNode.node(innerEntry.getKey().toString()).set(innerEntry.getValue().name());
            }
        }

        ConfigurationNode teamOverridesNode = node.node("team-overrides");
        for (Map.Entry<String, Map<String, RoomDefinition.RelationState>> entry : obj.getTeamOverrides().rowMap().entrySet()) {
            ConfigurationNode columnNode = teamOverridesNode.node(entry.getKey());
            for (Map.Entry<String, RoomDefinition.RelationState> innerEntry : entry.getValue().entrySet()) {
                columnNode.node(innerEntry.getKey()).set(innerEntry.getValue().name());
            }
        }
    }
}
