package com.alekiponi.alekiships.network;

import com.alekiponi.alekiships.AlekiShips;
import com.alekiponi.alekiships.common.entity.EntityInput;
import com.alekiponi.alekiships.common.entity.SloopConstructionState;
import com.alekiponi.alekiships.common.entity.compartment.vanilla.ChestCompartmentData;
import com.alekiponi.alekiships.common.entity.vehicle.ConstructionSloopVariant;
import com.alekiponi.alekiships.common.entity.vehicle.RowboatVariant;
import com.alekiponi.alekiships.common.entity.vehicle.SloopVariant;
import com.alekiponi.alekiships.common.entity.vehiclehelper.MastEntity;
import com.alekiponi.alekiships.common.recipe.util.ItemStackProvider;
import com.alekiponi.alekiships.wind.Wind;

import net.minecraft.core.Holder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializer;
import net.minecraft.world.item.DyeColor;

import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.Optional;
import java.util.function.Supplier;
import lombok.AllArgsConstructor;

public final class AlekiShipsEntityDataSerializers {

    public static final DeferredRegister<EntityDataSerializer<?>> ENTITY_DATA_SERIALIZERS = DeferredRegister.create(
            NeoForgeRegistries.ENTITY_DATA_SERIALIZERS, AlekiShips.MOD_ID);

    public static final DeferredEntityDataSerializer<DyeColor> DYE_COLOR = register("dye_color",
            () -> EntityDataSerializer.forValueType(DyeColor.STREAM_CODEC));

    public static final DeferredEntityDataSerializer<Wind> WIND = register("wind",
            () -> EntityDataSerializer.forValueType(Wind.STREAM_CODEC));

    public static final DeferredEntityDataSerializer<EntityInput.EntityInputState> ENTITY_INPUT_STATE = register(
            "entity_input_state", () -> EntityDataSerializer.forValueType(EntityInput.EntityInputState.STREAM_CODEC));

    public static final DeferredEntityDataSerializer<SloopConstructionState> SLOOP_CONSTRUCTION_STATE = register(
            "sloop_construction_state", () -> EntityDataSerializer.forValueType(SloopConstructionState.STREAM_CODEC));

    public static final DeferredEntityDataSerializer<ChestCompartmentData> CHEST_COMPARTMENT_DATA = register(
            "chest_compartment_data", () -> EntityDataSerializer.forValueType(ChestCompartmentData.STREAM_CODEC));

    public static final DeferredEntityDataSerializer<Holder<SloopVariant>> SLOOP_VARIANT = register("sloop_variant",
            () -> EntityDataSerializer.forValueType(SloopVariant.STREAM_CODEC));

    public static final DeferredEntityDataSerializer<Holder<RowboatVariant>> ROWBOAT_VARIANT = register(
            "rowboat_variant", () -> EntityDataSerializer.forValueType(RowboatVariant.STREAM_CODEC));

    public static final DeferredEntityDataSerializer<Holder<ConstructionSloopVariant>> SLOOP_CONSTRUCTION_VARIANT = register(
            "sloop_construction_variant",
            () -> EntityDataSerializer.forValueType(ConstructionSloopVariant.STREAM_CODEC));

    public static final DeferredEntityDataSerializer<Optional<DyeColor>> OPTIONAL_DYE_COLOR = register(
            "optional_dye_color",
            () -> EntityDataSerializer.forValueType(ByteBufCodecs.optional(DyeColor.STREAM_CODEC)));

    public static final DeferredEntityDataSerializer<ItemStackProvider> ITEM_STACK_PROVIDER = register(
            "item_stack_provider", () -> EntityDataSerializer.forValueType(ItemStackProvider.STREAM_CODEC));

    public static final DeferredEntityDataSerializer<Optional<MastEntity.MastBanner>> MAST_BANNER = register(
            "mast_banner",
            () -> EntityDataSerializer.forValueType(ByteBufCodecs.optional(MastEntity.MastBanner.STREAM_CODEC)));

    private static <T> DeferredEntityDataSerializer<T> register(final String name,
            final Supplier<EntityDataSerializer<T>> supplier) {
        return new DeferredEntityDataSerializer<>(ENTITY_DATA_SERIALIZERS.register(name, supplier));
    }

    @AllArgsConstructor
    public static final class DeferredEntityDataSerializer<T> implements EntityDataSerializer<T> {

        private final DeferredHolder<EntityDataSerializer<?>, EntityDataSerializer<T>> serializer;

        @Override
        public StreamCodec<? super RegistryFriendlyByteBuf, T> codec() {
            return this.serializer.get().codec();
        }

        @Override
        public EntityDataAccessor<T> createAccessor(final int id) {
            return new EntityDataAccessor<>(id, this.serializer.get());
        }

        @Override
        public T copy(final T value) {
            return this.serializer.get().copy(value);
        }
    }
}