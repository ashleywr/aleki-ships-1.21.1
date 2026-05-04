package com.alekiponi.alekiships.mixins.common.accessors;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.world.Container;
import net.minecraft.world.inventory.ShulkerBoxMenu;

@Mixin(ShulkerBoxMenu.class)
public interface ShulkerBoxMenuAccessor {

    @Accessor("container")
    Container alekiships$getContainer();
}