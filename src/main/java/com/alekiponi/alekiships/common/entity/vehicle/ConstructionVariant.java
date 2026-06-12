package com.alekiponi.alekiships.common.entity.vehicle;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.resources.RegistryFileCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.storage.loot.LootTable;

public interface ConstructionVariant<E extends Enum<E> & ConstructionInput.ConstructionStage<E>> {

    static <T extends ConstructionVariant<E>, E extends Enum<E> & ConstructionInput.ConstructionStage<E>> RecordCodecBuilder<T, Holder<ConstructionInput<E>>> getConstructionInputField(
            final ResourceKey<Registry<ConstructionInput<E>>> inputRegistryKey,
            final Codec<ConstructionInput<E>> inputCodec) {
        return RegistryFileCodec.create(inputRegistryKey, inputCodec)
                .fieldOf("construction_input")
                .forGetter(ConstructionVariant::getConstructionInput);
    }

    /**
     * Simple helper for the Loot table resource key codec field
     */
    static <T extends ConstructionVariant<?>> RecordCodecBuilder<T, ResourceKey<LootTable>> lootTableField() {
        return ResourceKey.codec(Registries.LOOT_TABLE)
                .fieldOf("loot_table")
                .forGetter(ConstructionVariant::getLootTable);
    }

    static <T extends ConstructionVariant<?>> RecordCodecBuilder<T, Component> nameField() {
        return ComponentSerialization.CODEC.fieldOf("name").forGetter(ConstructionVariant::getName);
    }

    Component getName();

    Holder<ConstructionInput<E>> getConstructionInput();

    /**
     * @return The loot table used by this boat
     */
    ResourceKey<LootTable> getLootTable();
}