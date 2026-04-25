package dev.minceraft.sonus.api.service.audio;
// Created by booky10 in Sonus (00:41 17.11.2025)

import net.kyori.adventure.text.Component;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.UUID;

@NullMarked
public record AudioCategory(UUID uniqueId, Component name, @Nullable Component description) {

}
