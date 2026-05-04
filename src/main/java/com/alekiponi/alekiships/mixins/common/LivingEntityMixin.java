package com.alekiponi.alekiships.mixins.common;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import com.alekiponi.alekiships.common.entity.vehiclehelper.MastEntity;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity {

    public LivingEntityMixin(EntityType<?> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    @ModifyReturnValue(method = "onClimbable", at = @At("TAIL"))
    private boolean alekiships$onClimbableWhenOnMasts(final boolean original) {
        if (original) return true;
        return !this.level()
                .getEntities(this, this.getBoundingBox(),
                        EntitySelector.NO_SPECTATORS.and(MastEntity.class::isInstance))
                .isEmpty();
    }
}