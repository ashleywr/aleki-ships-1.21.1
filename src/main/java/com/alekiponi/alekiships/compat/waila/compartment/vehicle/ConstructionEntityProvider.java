package com.alekiponi.alekiships.compat.waila.compartment.vehicle;

import snownee.jade.api.EntityAccessor;
import snownee.jade.api.IEntityComponentProvider;
import snownee.jade.api.ITooltip;
import snownee.jade.api.config.IPluginConfig;

import com.alekiponi.alekiships.AlekiShips;
import com.alekiponi.alekiships.common.entity.vehiclehelper.ConstructionEntity;
import com.alekiponi.alekiships.util.CommonHelper;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public enum ConstructionEntityProvider implements IEntityComponentProvider {
    INSTANCE;

    public static final String INPUTS_REMAINING_KEY = AlekiShips.lang("jade", "construction_entity.remaining_inputs");
    public static final String CURRENT_STAGE_KEY = AlekiShips.lang("jade", "construction_entity.current_stage");
    public static final String NEXT_STAGE_KEY = AlekiShips.lang("jade", "construction_entity.next_stage");
    private static final ResourceLocation NAME = AlekiShips.location("construction_entity");

    @Override
    public void appendTooltip(final ITooltip iTooltip, final EntityAccessor entityAccessor,
            final IPluginConfig iPluginConfig) {
        // This looks a bit weird because EntityAccessor#getEntity returns PartEntity#getParent
        if (!(entityAccessor.getRawEntity() instanceof final ConstructionEntity constructionEntity)) return;

        final var constructionState = constructionEntity.getParent().getConstructionState();
        iTooltip.add(Component.translatable(INPUTS_REMAINING_KEY, constructionState.remainingInputs()));
        iTooltip.add(
                Component.translatable(CURRENT_STAGE_KEY, CommonHelper.translateEnum(constructionState.inputStage())));
        iTooltip.add(Component.translatable(NEXT_STAGE_KEY,
                CommonHelper.translateEnum(constructionState.inputStage().next())));
    }

    @Override
    public ResourceLocation getUid() {
        return NAME;
    }
}