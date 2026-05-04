package com.alekiponi.alekiships.events.config;

import net.neoforged.neoforge.common.ModConfigSpec;

import java.util.Objects;
import org.jetbrains.annotations.Nullable;

public sealed class SpecHoldingConfig permits ClientConfig, ServerConfig {

    private @Nullable ModConfigSpec spec;

    final void updateSpec(final ModConfigSpec spec) {
        this.spec = spec;
    }

    public final ModConfigSpec spec() {
        return Objects.requireNonNull(this.spec);
    }
}