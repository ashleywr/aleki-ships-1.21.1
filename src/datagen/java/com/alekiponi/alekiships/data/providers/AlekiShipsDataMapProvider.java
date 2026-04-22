package com.alekiponi.alekiships.data.providers;

import com.alekiponi.alekiships.common.AlekiShipsDataMaps;
import com.alekiponi.alekiships.common.AlekiShipsRegistries;
import com.alekiponi.alekiships.common.block.AlekiShipsBlocks;
import com.alekiponi.alekiships.common.compartment.AlekiShipsCompartmentPlaceableSerializers;
import com.alekiponi.alekiships.common.compartment.vanilla.*;
import com.alekiponi.alekiships.common.entity.compartment.vanilla.ChestCompartmentData;
import com.alekiponi.alekiships.data.util.DataMapBuilderExtensions;
import com.alekiponi.alekiships.util.BoatFrame;
import com.alekiponi.alekiships.util.OverworldWood;
import com.alekiponi.alekiships.wind.SimpleWindModel;

import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.BarrelBlock;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.ShulkerBoxBlock;
import net.minecraft.world.level.dimension.BuiltinDimensionTypes;

import net.neoforged.neoforge.common.data.DataMapProvider;

import java.util.concurrent.CompletableFuture;
import lombok.experimental.ExtensionMethod;

@ExtensionMethod(DataMapBuilderExtensions.class)
public class AlekiShipsDataMapProvider extends DataMapProvider {

    public AlekiShipsDataMapProvider(final PackOutput packOutput,
            final CompletableFuture<HolderLookup.Provider> lookupProvider) {
        super(packOutput, lookupProvider);
    }

    @Override
    protected void gather(final HolderLookup.Provider provider) {
        this.gatherCompartmentPlaceable();
        this.gatherWindModels();
        {
            final var angledFrame = this.builder(AlekiShipsDataMaps.ANGLED_BOAT_FRAME);
            final var flatFrame = this.builder(AlekiShipsDataMaps.FLAT_BOAT_FRAME);
            final var frameMaterials = provider.lookupOrThrow(AlekiShipsRegistries.FRAME_MATERIAL);
            for (final var wood : OverworldWood.values()) {
                final var frameMaterial = frameMaterials.getOrThrow(wood.frameMaterialKey());
                angledFrame.add(wood.getPlankItem(),
                        new BoatFrame(AlekiShipsBlocks.WOODEN_BOAT_FRAME_ANGLED.get(), frameMaterial));
                flatFrame.add(wood.getPlankItem(),
                        new BoatFrame(AlekiShipsBlocks.WOODEN_BOAT_FRAME_FLAT.get(), frameMaterial));
            }
        }
    }

    private void gatherCompartmentPlaceable() {
        final var builder = this.builder(AlekiShipsDataMaps.COMPARTMENT_PLACEABLE);

        builder.add(Items.BARREL, BarrelCompartmentPlaceable.of(
                Blocks.BARREL.defaultBlockState().setValue(BarrelBlock.FACING, Direction.UP)));
        builder.add(Items.CHEST, ChestCompartmentPlaceable.of(ChestCompartmentData.VANILLA_CHEST_NORMAL));
        builder.add(Items.TRAPPED_CHEST, ChestCompartmentPlaceable.of(ChestCompartmentData.builder()
                .texture(ResourceLocation.withDefaultNamespace("entity/chest/trapped"))
                .build()));
        builder.add(Items.ENDER_CHEST, AlekiShipsCompartmentPlaceableSerializers.ENDER_CHEST);

        builder.add(Items.SHULKER_BOX, ShulkerBoxCompartmentPlaceable.of());
        for (final var color : DyeColor.values()) {
            builder.add(ShulkerBoxBlock.getBlockByColor(color).asItem(), ShulkerBoxCompartmentPlaceable.of(color));
        }

        builder.add(Items.FURNACE, FurnaceCompartmentPlaceable.of(Blocks.FURNACE.defaultBlockState()));
        builder.add(Items.BLAST_FURNACE, BlastFurnaceCompartmentPlaceable.of(Blocks.BLAST_FURNACE.defaultBlockState()));
        builder.add(Items.SMOKER, SmokerCompartmentPlaceable.of(Blocks.SMOKER.defaultBlockState()));

        builder.add(Items.BREWING_STAND, AlekiShipsCompartmentPlaceableSerializers.BREWING_STAND);

        builder.add(Items.CRAFTING_TABLE,
                CraftingTableCompartmentPlaceable.of(Blocks.CRAFTING_TABLE.defaultBlockState()));
        builder.add(Items.STONECUTTER, AlekiShipsCompartmentPlaceableSerializers.STONECUTTER);
        builder.add(Items.CARTOGRAPHY_TABLE, AlekiShipsCompartmentPlaceableSerializers.CARTOGRAPHY_TABLE);
        builder.add(Items.SMITHING_TABLE, AlekiShipsCompartmentPlaceableSerializers.SMITHING_TABLE);
        builder.add(Items.GRINDSTONE, AlekiShipsCompartmentPlaceableSerializers.GRINDSTONE);
        builder.add(Items.LOOM, AlekiShipsCompartmentPlaceableSerializers.LOOM);

        builder.add(Items.NOTE_BLOCK, AlekiShipsCompartmentPlaceableSerializers.NOTE_BLOCK);
        builder.add(Items.JUKEBOX, AlekiShipsCompartmentPlaceableSerializers.JUKEBOX);
    }

    private void gatherWindModels() {
        final var builder = this.builder(AlekiShipsDataMaps.WIND_MODEL);
        builder.add(BuiltinDimensionTypes.OVERWORLD, SimpleWindModel.INSTANCE);
    }
}