package com.alekiponi.alekiships.mixins.common;

import com.llamalad7.mixinextras.sugar.Local;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.alekiponi.alekiships.common.entity.compartment.vanilla.AbstractFurnaceCompartmentEntity;
import com.alekiponi.alekiships.common.entity.compartment.vanilla.FurnaceCompartmentEntity;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.inventory.FurnaceResultSlot;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

@Mixin(FurnaceResultSlot.class)
public abstract class FurnaceResultSlotMixin extends Slot {

    public FurnaceResultSlotMixin(final Container pContainer, final int pSlot, final int pX, final int pY) {
        super(pContainer, pSlot, pX, pY);
    }

    /**
     * @reason Our {@link FurnaceCompartmentEntity} and children need to reward XP like vanilla furnaces.
     * This mixin is to do exactly that.
     * @author Traister101
     */
    @Inject(method = "checkTakeAchievements", at = @At(value = "FIELD", target = "Lnet/minecraft/world/inventory/FurnaceResultSlot;container:Lnet/minecraft/world/Container;", opcode = Opcodes.GETFIELD))
    private void alekiships$rewardAbstractFurnaceCompartmentXP(final ItemStack pStack, final CallbackInfo ci,
            @Local final ServerPlayer serverPlayer) {
        if (this.container instanceof AbstractFurnaceCompartmentEntity furnaceCompartment) {
            furnaceCompartment.awardUsedRecipesAndPopExperience(serverPlayer);
        }
    }
}