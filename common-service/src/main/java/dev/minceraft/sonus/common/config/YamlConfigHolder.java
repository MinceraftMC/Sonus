package dev.minceraft.sonus.common.config;

import org.jspecify.annotations.NullMarked;
import org.spongepowered.configurate.yaml.NodeStyle;
import org.spongepowered.configurate.yaml.YamlConfigurationLoader;

import java.nio.file.Path;
import java.util.function.Function;
import java.util.function.Supplier;

@NullMarked
public class YamlConfigHolder<T> extends ConfigHolder<T, YamlConfigurationLoader> {

    public YamlConfigHolder(Class<T> clazz, Supplier<T> def, Path path) {
        this(clazz, __ -> def.get(), path);
    }

    public YamlConfigHolder(Class<T> clazz, Function<ConfigHolder<T, YamlConfigurationLoader>, T> def, Path path) {
        super(clazz, def, path, () -> YamlConfigurationLoader.builder()
                .nodeStyle(NodeStyle.BLOCK).indent(2));
    }
}
