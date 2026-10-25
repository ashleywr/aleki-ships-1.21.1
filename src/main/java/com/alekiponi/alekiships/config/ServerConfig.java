package com.alekiponi.alekiships.config;

import com.alekiponi.alekiships.AlekiShips;

import net.neoforged.neoforge.common.ModConfigSpec;

public final class ServerConfig extends SpecHoldingConfig {

    public final ModConfigSpec.BooleanValue windAffectsBoatsWithNoAnchor;
    public final ModConfigSpec.BooleanValue windDrift;
    public final ModConfigSpec.BooleanValue eagerEntityMultiblockValidation;

    ServerConfig(final ModConfigSpec.Builder builder) {
        this.windAffectsBoatsWithNoAnchor = builder.comment("When true, boats with no anchor will drift in the wind")
                .translation(lang("windAffectsBoatsWithNoAnchor"))
                .define("windAffectsBoatsWithNoAnchor", false);
        this.windDrift = builder.comment("When true, wind applies passive drift and turning force to boats")
                .translation(lang("windDrift"))
                .define("windDrift", true);
        this.eagerEntityMultiblockValidation = builder.comment(
                        "When true entity multiblocks will be eagerly checked whenever a block is placed")
                .translation(lang("eagerEntityMultiblockValidation"))
                .define("eagerEntityMultiblockValidation", true);
    }

    private static String lang(final String key) {
        return AlekiShips.lang("config", "server" + "." + key);
    }
}
