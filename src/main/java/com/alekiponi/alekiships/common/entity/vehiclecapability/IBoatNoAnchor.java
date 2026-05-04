package com.alekiponi.alekiships.common.entity.vehiclecapability;

import com.alekiponi.alekiships.events.config.AlekiShipsConfig;

public interface IBoatNoAnchor {
    default boolean windShouldAffect() {
        return AlekiShipsConfig.SERVER.windAffectsBoatsWithNoAnchor.get();
    }

}
