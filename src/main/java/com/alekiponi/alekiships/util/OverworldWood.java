package com.alekiponi.alekiships.util;

import com.alekiponi.alekiships.common.entity.vehicle.ConstructionSloopVariant;
import com.alekiponi.alekiships.common.entity.vehicle.ConstructionSloopVariants;
import com.alekiponi.alekiships.common.entity.vehicle.RowboatVariant;
import com.alekiponi.alekiships.common.entity.vehicle.RowboatVariants;

import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;

import java.util.Locale;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.Accessors;

/**
 * An enum for all the vanilla Overworld woods to assist in registration and data generation
 */
@AllArgsConstructor
public enum OverworldWood implements Wood {
    OAK(Items.OAK_PLANKS, BoatMaterials.OAK, FrameMaterial.OAK, RowboatVariants.OAK, ConstructionSloopVariants.OAK),
    SPRUCE(Items.SPRUCE_PLANKS, BoatMaterials.SPRUCE, FrameMaterial.SPRUCE, RowboatVariants.SPRUCE,
            ConstructionSloopVariants.SPRUCE),
    BIRCH(Items.BIRCH_PLANKS, BoatMaterials.BIRCH, FrameMaterial.BIRCH, RowboatVariants.BIRCH,
            ConstructionSloopVariants.BIRCH),
    ACACIA(Items.ACACIA_PLANKS, BoatMaterials.ACACIA, FrameMaterial.ACACIA, RowboatVariants.ACACIA,
            ConstructionSloopVariants.ACACIA),
    CHERRY(Items.CHERRY_PLANKS, BoatMaterials.CHERRY, FrameMaterial.CHERRY, RowboatVariants.CHERRY,
            ConstructionSloopVariants.CHERRY),
    JUNGLE(Items.JUNGLE_PLANKS, BoatMaterials.JUNGLE, FrameMaterial.JUNGLE, RowboatVariants.JUNGLE,
            ConstructionSloopVariants.JUNGLE),
    DARK_OAK(Items.DARK_OAK_PLANKS, BoatMaterials.DARK_OAK, FrameMaterial.DARK_OAK, RowboatVariants.DARK_OAK,
            ConstructionSloopVariants.DARK_OAK),
    MANGROVE(Items.MANGROVE_PLANKS, BoatMaterials.MANGROVE, FrameMaterial.MANGROVE, RowboatVariants.MANGROVE,
            ConstructionSloopVariants.MANGROVE),
    BAMBOO(Items.BAMBOO_PLANKS, BoatMaterials.BAMBOO, FrameMaterial.BAMBOO, RowboatVariants.BAMBOO,
            ConstructionSloopVariants.BAMBOO);

    @Getter
    private final Item plankItem;
    @Getter
    @Accessors(fluent = true)
    private final ResourceKey<BoatMaterial> boatMaterialKey;
    @Getter
    @Accessors(fluent = true)
    private final ResourceKey<FrameMaterial> frameMaterialKey;
    @Getter
    @Accessors(fluent = true)
    private final ResourceKey<RowboatVariant> rowboatKey;
    @Getter
    @Accessors(fluent = true)
    private final ResourceKey<ConstructionSloopVariant> sloopConstructionKey;

    @Override
    public String getSerializedName() {
        return this.name().toLowerCase(Locale.ROOT);
    }
}