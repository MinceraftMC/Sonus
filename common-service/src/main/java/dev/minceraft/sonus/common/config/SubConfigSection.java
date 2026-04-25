package dev.minceraft.sonus.common.config;

import com.google.common.collect.ClassToInstanceMap;
import com.google.common.collect.ImmutableClassToInstanceMap;
import org.jspecify.annotations.NullMarked;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;

import java.util.List;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Collectors;

@NullMarked
public final class SubConfigSection {

    public static final SubConfigSection EMPTY = new SubConfigSection(List.of(), List.of());

    private final List<ISubConfig> configs;
    private final ClassToInstanceMap<ISubConfig> configMap;

    public SubConfigSection(List<Template<?>> templates, List<ISubConfig> configs) {
        this.configs = List.copyOf(configs);

        // track which template classes are/aren't used
        Set<Class<?>> classes = templates.stream()
                .map(Template::clazz).collect(Collectors.toSet());

        ImmutableClassToInstanceMap.Builder<ISubConfig> configsByClassBuilder = ImmutableClassToInstanceMap.builder();
        for (ISubConfig config : configs) {
            @SuppressWarnings("unchecked") // ignore
            Class<ISubConfig> clazz = (Class<ISubConfig>) config.getClass();
            if (!classes.remove(clazz)) {
                throw new IllegalStateException("Can't find template for " + clazz);
            }
            configsByClassBuilder.put(clazz, config);
        }
        // add default configs based on templates
        for (Template<?> template : templates) {
            if (classes.contains(template.clazz())) {
                // still missing, construct default
                template.putDefaultInBuilder(configsByClassBuilder);
            }
        }
        this.configMap = configsByClassBuilder.build();
    }

    public <T extends ISubConfig> T getConfig(Class<T> clazz) {
        ISubConfig config = this.configMap.getInstance(clazz);
        if (config == null) {
            throw new IllegalArgumentException("Can't find config for " + clazz);
        } else if (!clazz.isInstance(config)) {
            throw new IllegalArgumentException("Incorrect config: " + config.getClass()
                    + " is not an instance of " + clazz);
        }
        return clazz.cast(config);
    }

    public List<ISubConfig> getConfigs() {
        return this.configs;
    }

    public record Template<T extends ISubConfig>(
            String id,
            Class<T> clazz,
            Constructor<T> constructor
    ) {

        public T getOrConstruct(ConfigurationNode node) throws SerializationException {
            return node.get(this.clazz, (Supplier<T>) this.constructor::construct);
        }

        private void putDefaultInBuilder(ImmutableClassToInstanceMap.Builder<ISubConfig> builder) {
            builder.put(this.clazz, this.constructor.construct());
        }
    }

    public interface Constructor<T extends ISubConfig> {

        T construct();
    }
}
