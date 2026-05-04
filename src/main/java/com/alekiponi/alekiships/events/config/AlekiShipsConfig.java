package com.alekiponi.alekiships.events.config;

import net.neoforged.neoforge.common.ModConfigSpec;

import java.util.function.Function;

public final class AlekiShipsConfig {

    public static final ServerConfig SERVER = register(ServerConfig::new);
    public static final ClientConfig CLIENT = register(ClientConfig::new);

    private static <C extends SpecHoldingConfig> C register(final Function<ModConfigSpec.Builder, C> factory) {
        final var pair = new ModConfigSpec.Builder().configure(factory);
        pair.getLeft().updateSpec(pair.getRight());
        return pair.getLeft();
    }
}