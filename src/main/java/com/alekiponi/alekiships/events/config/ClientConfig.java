package com.alekiponi.alekiships.events.config;

import com.alekiponi.alekiships.AlekiShips;
import com.alekiponi.alekiships.client.IngameOverlays;
import com.alekiponi.alekiships.util.CommonHelper;

import net.minecraft.network.chat.Component;

import net.neoforged.neoforge.common.ModConfigSpec;
import net.neoforged.neoforge.common.TranslatableEnum;

public final class ClientConfig extends SpecHoldingConfig {

    public final ModConfigSpec.EnumValue<RudderSchemes> rudderControlScheme;
    public final ModConfigSpec.EnumValue<IngameOverlays.DebugDisplayUnits> debugDisplayUnits;

    ClientConfig(final ModConfigSpec.Builder builder) {
        this.rudderControlScheme = builder.comment("Change how the rudder behaves on boats that have them")
                .translation(lang("tillerControlScheme"))
                .defineEnum("tillerControlScheme", RudderSchemes.RETURN_TO_CENTER);
        this.debugDisplayUnits = builder.comment("Change the debug display units for debug displays")
                .translation(lang("debugDisplayUnits"))
                .defineEnum("debugDisplayUnits", IngameOverlays.DebugDisplayUnits.KILOMETRES_PER_HOUR);
    }

    private static String lang(final String key) {
        return AlekiShips.lang("config", "client" + "." + key);
    }

    public enum RudderSchemes implements TranslatableEnum {
        RETURN_TO_CENTER,
        STAY_IN_PLACE;

        @Override
        public Component getTranslatedName() {
            return CommonHelper.translateEnum(this);
        }
    }
}