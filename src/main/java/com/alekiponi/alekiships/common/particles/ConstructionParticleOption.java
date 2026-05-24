package com.alekiponi.alekiships.common.particles;

import com.mojang.serialization.MapCodec;

import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public class ConstructionParticleOption implements ParticleOptions {

    private final ParticleType<ConstructionParticleOption> type;
    @Getter
    private final ResourceLocation texture;

    public static MapCodec<ConstructionParticleOption> codec(
            final ParticleType<ConstructionParticleOption> particleType) {
        return ResourceLocation.CODEC.xmap(texture -> new ConstructionParticleOption(particleType, texture),
                ConstructionParticleOption::getTexture).fieldOf("texture");
    }

    public static StreamCodec<? super RegistryFriendlyByteBuf, ConstructionParticleOption> streamCodec(
            final ParticleType<ConstructionParticleOption> particleType) {
        return ResourceLocation.STREAM_CODEC.map(texture -> new ConstructionParticleOption(particleType, texture),
                ConstructionParticleOption::getTexture);
    }

    public static ConstructionParticleOption of(final ResourceLocation texture) {
        return new ConstructionParticleOption(AlekiShipsParticles.CONSTRUCTION.get(), texture);
    }

    @Override
    public ParticleType<ConstructionParticleOption> getType() {
        return this.type;
    }
}