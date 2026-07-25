package dev.minceraft.sonus.plasmo.adapter.config;

import dev.minceraft.sonus.common.config.ISubConfig;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;

import java.util.UUID;

@ConfigSerializable
public class PlasmoConfig implements ISubConfig {

    public boolean enabled = true;

    public UUID serverId = UUID.randomUUID();
}
