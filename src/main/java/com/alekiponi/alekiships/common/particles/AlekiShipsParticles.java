package com.alekiponi.alekiships.common.particles;

import com.mojang.serialization.MapCodec;

import com.alekiponi.alekiships.AlekiShips;

import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Function;

public final class AlekiShipsParticles {
    public static final DeferredRegister<ParticleType<?>> PARTICLE_TYPES = DeferredRegister.create(
            Registries.PARTICLE_TYPE, AlekiShips.MOD_ID);

    public static final DeferredHolder<ParticleType<?>, ParticleType<ConstructionParticleOption>> CONSTRUCTION = register(
            "construction", false, ConstructionParticleOption::codec, ConstructionParticleOption::streamCodec);

    private static DeferredHolder<ParticleType<?>, SimpleParticleType> simple(final String name,
            final boolean overrideLimitter) {
        return PARTICLE_TYPES.register(name, () -> new SimpleParticleType(overrideLimitter));
    }

    @SuppressWarnings({"Convert2Diamond", "SameParameterValue"})
    private static <T extends ParticleOptions> DeferredHolder<ParticleType<?>, ParticleType<T>> register(
            final String name, final boolean overrideLimitter, final Function<ParticleType<T>, MapCodec<T>> codecGetter,
            final Function<ParticleType<T>, StreamCodec<? super RegistryFriendlyByteBuf, T>> streamCodecGetter) {
        return PARTICLE_TYPES.register(name, () -> new ParticleType<T>(overrideLimitter) {
            @Override
            public MapCodec<T> codec() {
                return codecGetter.apply(this);
            }

            @Override
            public StreamCodec<? super RegistryFriendlyByteBuf, T> streamCodec() {
                return streamCodecGetter.apply(this);
            }
        });
    }
}