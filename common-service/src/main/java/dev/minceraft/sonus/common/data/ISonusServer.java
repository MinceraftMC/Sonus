package dev.minceraft.sonus.common.data;
// Created by booky10 in Sonus (2:52 PM 04.12.2025)

import net.kyori.adventure.text.Component;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.UUID;

@NullMarked
public interface ISonusServer {

    UUID getUniqueId();

    Component getName();

    @Nullable String getType();
}
