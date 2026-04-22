package com.alekiponi.alekiships.common.compartment;

import com.mojang.serialization.MapCodec;

import com.alekiponi.alekiships.AlekiShips;
import com.alekiponi.alekiships.common.AlekiShipsRegistries;
import com.alekiponi.alekiships.common.compartment.vanilla.*;
import com.alekiponi.alekiships.common.entity.AlekiShipsEntities;
import com.alekiponi.alekiships.common.entity.compartment.AbstractCompartmentEntity;
import com.alekiponi.alekiships.common.entity.compartment.BlockCompartment;
import com.alekiponi.alekiships.common.entity.compartment.CompartmentCloneable;
import com.alekiponi.alekiships.common.entity.compartment.SimpleBlockMenuCompartmentEntity;
import com.alekiponi.alekiships.common.entity.compartment.vanilla.BrewingStandCompartmentEntity;
import com.alekiponi.alekiships.common.entity.compartment.vanilla.EnderChestCompartmentEntity;
import com.alekiponi.alekiships.common.entity.compartment.vanilla.JukeboxCompartmentEntity;
import com.alekiponi.alekiships.common.entity.compartment.vanilla.NoteBlockCompartmentEntity;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.level.block.Blocks;

import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class AlekiShipsCompartmentPlaceableSerializers {

    public static final DeferredRegister<CompartmentPlaceableSerializer<?>> COMPARTMENT_PLACEABLE_SERIALIZERS = DeferredRegister.create(
            AlekiShipsRegistries.COMPARTMENT_PLACEABLE_SERIALIZER, AlekiShips.MOD_ID);

    public static final DeferredHolder<CompartmentPlaceableSerializer<?>, CompartmentPlaceableSerializer<BlockCompartmentPlaceable>> BLOCK = register(
            "block", BlockCompartmentPlaceable.CODEC, BlockCompartmentPlaceable.STREAM_CODEC);

    public static final DeferredHolder<CompartmentPlaceableSerializer<?>, CompartmentPlaceableSerializer<BarrelCompartmentPlaceable>> BARREL = register(
            "barrel", BarrelCompartmentPlaceable.CODEC, BarrelCompartmentPlaceable.STREAM_CODEC);

    public static final DeferredHolder<CompartmentPlaceableSerializer<?>, CompartmentPlaceableSerializer<ChestCompartmentPlaceable>> CHEST = register(
            "chest", ChestCompartmentPlaceable.CODEC, ChestCompartmentPlaceable.STREAM_CODEC);

    public static final DeferredHolder<CompartmentPlaceableSerializer<?>, CompartmentPlaceableSerializer<ShulkerBoxCompartmentPlaceable>> SHULKER_BOX = register(
            "shulker_box", ShulkerBoxCompartmentPlaceable.CODEC, ShulkerBoxCompartmentPlaceable.STREAM_CODEC);

    public static final DeferredHolder<CompartmentPlaceableSerializer<?>, CompartmentPlaceableSerializer<FurnaceCompartmentPlaceable>> FURNACE = register(
            "furnace", FurnaceCompartmentPlaceable.CODEC, FurnaceCompartmentPlaceable.STREAM_CODEC);

    public static final DeferredHolder<CompartmentPlaceableSerializer<?>, CompartmentPlaceableSerializer<BlastFurnaceCompartmentPlaceable>> BLAST_FURNACE = register(
            "blast_furnace", BlastFurnaceCompartmentPlaceable.CODEC, BlastFurnaceCompartmentPlaceable.STREAM_CODEC);

    public static final DeferredHolder<CompartmentPlaceableSerializer<?>, CompartmentPlaceableSerializer<SmokerCompartmentPlaceable>> SMOKER = register(
            "smoker", SmokerCompartmentPlaceable.CODEC, SmokerCompartmentPlaceable.STREAM_CODEC);

    public static final DeferredHolder<CompartmentPlaceableSerializer<?>, CompartmentPlaceableSerializer<CraftingTableCompartmentPlaceable>> CRAFTING_TABLE = register(
            "crafting_table", CraftingTableCompartmentPlaceable.CODEC, CraftingTableCompartmentPlaceable.STREAM_CODEC);

    public static final DirectCompartmentPlaceable<EnderChestCompartmentEntity> ENDER_CHEST = register("ender_chest",
            DirectCompartmentType.simple(AlekiShipsEntities.ENDER_CHEST_COMPARTMENT_ENTITY));

    public static final DirectCompartmentPlaceable<BrewingStandCompartmentEntity> BREWING_STAND = register(
            "brewing_stand", DirectCompartmentType.postInit(AlekiShipsEntities.BREWING_STAND_COMPARTMENT_ENTITY,
                    BlockCompartment.staticFactory(BrewingStandCompartmentEntity::new, Blocks.BREWING_STAND),
                    CompartmentCloneable::initialize));

    public static final DirectCompartmentPlaceable<SimpleBlockMenuCompartmentEntity> STONECUTTER = register(
            "stonecutter", DirectCompartmentType.of(AlekiShipsEntities.STONECUTTER_COMPARTMENT_ENTITY,
                    BlockCompartment.staticFactory(SimpleBlockMenuCompartmentEntity.directCompartmentFactory(
                            SimpleBlockMenuCompartmentEntity.STONECUTTER), Blocks.STONECUTTER)));

    public static final DirectCompartmentPlaceable<SimpleBlockMenuCompartmentEntity> CARTOGRAPHY_TABLE = register(
            "cartography_table", DirectCompartmentType.of(AlekiShipsEntities.CARTOGRAPHY_TABLE_COMPARTMENT_ENTITY,
                    BlockCompartment.staticFactory(SimpleBlockMenuCompartmentEntity.directCompartmentFactory(
                            SimpleBlockMenuCompartmentEntity.CARTOGRAPHY_TABLE), Blocks.CARTOGRAPHY_TABLE)));

    public static final DirectCompartmentPlaceable<SimpleBlockMenuCompartmentEntity> SMITHING_TABLE = register(
            "smithing_table", DirectCompartmentType.of(AlekiShipsEntities.SMITHING_TABLE_COMPARTMENT_ENTITY,
                    BlockCompartment.staticFactory(SimpleBlockMenuCompartmentEntity.directCompartmentFactory(
                            SimpleBlockMenuCompartmentEntity.SMITHING_TABLE), Blocks.SMITHING_TABLE)));

    public static final DirectCompartmentPlaceable<SimpleBlockMenuCompartmentEntity> GRINDSTONE = register("grindstone",
            DirectCompartmentType.of(AlekiShipsEntities.GRINDSTONE_COMPARTMENT_ENTITY, BlockCompartment.staticFactory(
                    SimpleBlockMenuCompartmentEntity.directCompartmentFactory(
                            SimpleBlockMenuCompartmentEntity.GRINDSTONE), Blocks.GRINDSTONE)));

    public static final DirectCompartmentPlaceable<SimpleBlockMenuCompartmentEntity> LOOM = register("loom",
            DirectCompartmentType.of(AlekiShipsEntities.LOOM_COMPARTMENT_ENTITY, BlockCompartment.staticFactory(
                    SimpleBlockMenuCompartmentEntity.directCompartmentFactory(SimpleBlockMenuCompartmentEntity.LOOM),
                    Blocks.LOOM)));

    public static final DirectCompartmentPlaceable<NoteBlockCompartmentEntity> NOTE_BLOCK = register("note_block",
            DirectCompartmentType.of(AlekiShipsEntities.NOTE_BLOCK_COMPARTMENT_ENTITY,
                    BlockCompartment.staticFactory(NoteBlockCompartmentEntity::new, Blocks.NOTE_BLOCK)));

    public static final DirectCompartmentPlaceable<JukeboxCompartmentEntity> JUKEBOX = register("jukebox",
            DirectCompartmentType.postInit(AlekiShipsEntities.JUKEBOX_COMPARTMENT_ENTITY,
                    BlockCompartment.staticFactory(JukeboxCompartmentEntity::new, Blocks.JUKEBOX),
                    CompartmentCloneable::initialize));

    private static <R extends CompartmentPlaceable<?>> DeferredHolder<CompartmentPlaceableSerializer<?>, CompartmentPlaceableSerializer<R>> register(
            final String name, final MapCodec<R> codec,
            final StreamCodec<? super RegistryFriendlyByteBuf, R> streamCodec) {
        return COMPARTMENT_PLACEABLE_SERIALIZERS.register(name, () -> new CompartmentPlaceableSerializer<>() {
            @Override
            public MapCodec<R> codec() {
                return codec;
            }

            @Override
            public StreamCodec<? super RegistryFriendlyByteBuf, R> streamCodec() {
                return streamCodec;
            }
        });
    }

    private static <E extends AbstractCompartmentEntity> DirectCompartmentPlaceable<E> register(final String name,
            final DirectCompartmentType<E> directCompartmentType) {
        final var eSimpleCompartmentPlaceable = new DirectCompartmentPlaceable<>(directCompartmentType);
        COMPARTMENT_PLACEABLE_SERIALIZERS.register(name, eSimpleCompartmentPlaceable::getSerializer);
        return eSimpleCompartmentPlaceable;
    }
}