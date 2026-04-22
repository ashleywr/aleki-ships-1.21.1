package com.alekiponi.alekiships.common.compartment;

import com.mojang.serialization.MapCodec;

import com.alekiponi.alekiships.common.entity.compartment.AbstractCompartmentEntity;
import com.alekiponi.alekiships.common.entity.compartment.vanilla.EnderChestCompartmentEntity;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import java.util.Optional;
import lombok.AllArgsConstructor;

/**
 * This type is special and is for compartments that cannot or do not fit a generic construction format. For example
 * {@link EnderChestCompartmentEntity} doesn't make sense to have any parameters so we use this class instead.
 */
@AllArgsConstructor
public final class DirectCompartmentPlaceable<C extends AbstractCompartmentEntity> implements CompartmentPlaceable<C> {

    private final CompartmentPlaceableSerializer<DirectCompartmentPlaceable<C>> serializer = new CompartmentPlaceableSerializer<>() {
        @Override
        public MapCodec<DirectCompartmentPlaceable<C>> codec() {
            return MapCodec.unit(DirectCompartmentPlaceable.this);
        }

        @Override
        public StreamCodec<? super RegistryFriendlyByteBuf, DirectCompartmentPlaceable<C>> streamCodec() {
            return StreamCodec.unit(DirectCompartmentPlaceable.this);
        }
    };

    private final DirectCompartmentType<C> directCompartmentType;

    /**
     * This type is special and creates the serializer internally. This is safe to invoke at any time and must be manually registered
     * {@inheritDoc}
     */
    @Override
    public CompartmentPlaceableSerializer<?> getSerializer() {
        return this.serializer;
    }

    @Override
    public Optional<C> createCompartment(final Level level, final ItemStack itemStack) {
        return this.directCompartmentType.create(level, itemStack);
    }
}