package dev.minceraft.sonus.common.config.serializer;

import dev.minceraft.sonus.common.config.ISubConfig;
import dev.minceraft.sonus.common.config.SubConfigSection;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.serialize.TypeSerializer;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

@NullMarked
public final class SubConfigSerializer implements TypeSerializer<SubConfigSection> {

    private final List<SubConfigSection.Template<?>> templates;

    public SubConfigSerializer(List<SubConfigSection.Template<?>> templates) {
        this.templates = List.copyOf(templates);
    }

    @Override
    public SubConfigSection deserialize(Type type, ConfigurationNode node) throws SerializationException {
        List<ISubConfig> adapterConfigs = new ArrayList<>(this.templates.size());
        for (SubConfigSection.Template<?> template : this.templates) {
            ConfigurationNode child = node.node(template.id());
            adapterConfigs.add(template.getOrConstruct(child));
        }
        return new SubConfigSection(this.templates, adapterConfigs);
    }

    @Override
    public void serialize(Type type, @Nullable SubConfigSection obj, ConfigurationNode node) throws SerializationException {
        if (obj == null) {
            node.set(null);
            return;
        }
        for (SubConfigSection.Template<?> template : this.templates) {
            ISubConfig config = obj.getConfig(template.clazz());
            node.node(template.id()).set(template.clazz(), config);
        }
    }
}
