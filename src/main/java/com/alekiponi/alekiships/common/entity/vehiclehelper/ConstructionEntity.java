package com.alekiponi.alekiships.common.entity.vehiclehelper;

import com.alekiponi.alekiships.common.entity.AlekiShipsEntities;
import com.alekiponi.alekiships.common.entity.vehicle.AbstractUnderConstructionEntity;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import net.neoforged.neoforge.entity.PartEntity;

public class ConstructionEntity extends PartEntity<AbstractUnderConstructionEntity<?, ?>> {

    private final EntityDimensions size = EntityDimensions.scalable(1, 1);

    public ConstructionEntity(final AbstractUnderConstructionEntity<?, ?> parent) {
        super(parent);
        this.refreshDimensions();
    }

    public float getSpin(final float partialTicks) {
        return (this.getParent().tickCount + partialTicks) / 20F;
    }

    @Override
    protected void defineSynchedData(final SynchedEntityData.Builder builder) {}

    @Override
    protected void readAdditionalSaveData(final CompoundTag compound) {}

    @Override
    protected void addAdditionalSaveData(final CompoundTag compound) {}

    @Override
    public EntityType<?> getType() {
        return AlekiShipsEntities.CONSTRUCTION_ENTITY.get();
    }

    @Override
    public EntityDimensions getDimensions(final Pose pose) {
        return this.size;
    }

    /**
     * @return A potentially empty array of accepted ItemStack inputs
     */
    public ItemStack[] getRequiredItems() {
        return this.getParent().getRequiredItems();
    }

    @Override
    public InteractionResult interact(final Player player, final InteractionHand hand) {
        return this.getParent().interactFromConstructionEntity(player, hand);
    }

    @Override
    public boolean hurt(final DamageSource source, final float amount) {
        return this.getParent().hurt(source, amount);
    }

    @Override
    public boolean isPickable() {
        return !this.isRemoved();
    }

    @Override
    public boolean shouldBeSaved() {
        return false;
    }
}