package com.alekiponi.alekiships.common.entity.vehicle;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;

import com.alekiponi.alekiships.common.recipe.entity.EntityResult;
import com.alekiponi.alekiships.common.recipe.entity.SimpleResult;

import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.block.Blocks;

import net.neoforged.neoforge.common.crafting.SizedIngredient;
import net.neoforged.neoforge.common.util.NeoForgeExtraCodecs;
import net.neoforged.neoforge.network.codec.NeoForgeStreamCodecs;

import java.text.MessageFormat;
import java.util.Map;
import java.util.Objects;
import java.util.function.Supplier;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Range;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Singular;
import lombok.ToString;

/**
 * A Datapack object representing a collection of construction inputs.
 * These consist of discrete stages represented by {@link ProgressStage}.
 * When completed it spawns {@link #constructedEntity}, plays {@link #assembleSound} and creates
 * particles using {@link #assembleParticle}.
 *
 * @param <E> An enum representing the construction stages
 */
@Builder
@ToString
@EqualsAndHashCode
public final class ConstructionInput<E extends Enum<E> & ConstructionInput.ConstructionStage<E>> {

    @SuppressWarnings({"rawtypes", "unchecked"})
    private static final ConstructionInput<?> EMPTY = new ConstructionInput(SoundEvents.EMPTY,
            new BlockParticleOption(ParticleTypes.BLOCK, Blocks.STONE.defaultBlockState()),
            new SimpleResult(EntityType.PIG), Map.of());
    public final SoundEvent assembleSound;
    public final ParticleOptions assembleParticle;
    public final EntityResult constructedEntity;
    @Singular
    private final Map<E, ProgressStage> stages;

    /**
     * A helper to create a {@link Codec} for {@link ConstructionInput}
     *
     * @param stageCodec     The codec for the stages
     * @param stagesSupplier The supplier for all the stages
     * @param <E>            An enum representing the construction stages
     */
    public static <E extends Enum<E> & ConstructionStage<E>> Codec<ConstructionInput<E>> codec(
            final StringRepresentable.StringRepresentableCodec<E> stageCodec, final Supplier<E[]> stagesSupplier) {
        return RecordCodecBuilder.create(instance -> instance.group(
                        ConstructionInput.stagesCodec(stageCodec, stagesSupplier)
                                .fieldOf("stages")
                                .forGetter(constructionInput -> constructionInput.stages),
                        EntityResult.CODEC.fieldOf("constructed_entity")
                                .forGetter(constructionInput -> constructionInput.constructedEntity),
                        BuiltInRegistries.SOUND_EVENT.byNameCodec()
                                .fieldOf("assemble_sound")
                                .forGetter(constructionInput -> constructionInput.assembleSound),
                        ParticleTypes.CODEC.fieldOf("assemble_particle")
                                .forGetter(constructionInput -> constructionInput.assembleParticle))
                .apply(instance, ConstructionInput::of));
    }

    public static <E extends Enum<E> & ConstructionStage<E>> StreamCodec<RegistryFriendlyByteBuf, ConstructionInput<E>> streamCodec(
            final Class<E> enumClass) {
        return StreamCodec.composite(
                ByteBufCodecs.map(Object2ObjectOpenHashMap::new, NeoForgeStreamCodecs.enumCodec(enumClass),
                        ProgressStage.STREAM_CODEC), constructionInput -> constructionInput.stages,
                EntityResult.STREAM_CODEC, constructionInput -> constructionInput.constructedEntity,
                ByteBufCodecs.registry(Registries.SOUND_EVENT), constructionInput -> constructionInput.assembleSound,
                ParticleTypes.STREAM_CODEC, constructionInput -> constructionInput.assembleParticle,
                ConstructionInput::of);
    }

    private static <E extends Enum<E> & ConstructionStage<E>> Codec<Map<E, ProgressStage>> stagesCodec(
            final StringRepresentable.StringRepresentableCodec<E> stageCodec, final Supplier<E[]> stagesSupplier) {
        final var stages = stagesSupplier.get();
        return Codec.simpleMap(stageCodec, ProgressStage.CODEC, StringRepresentable.keys(stages)).validate(map -> {
            for (final E constructionStage : stages) {
                final var stage = map.get(constructionStage);
                if (constructionStage == constructionStage.end()) {
                    if (stage == null) continue;
                    return DataResult.error(() -> MessageFormat.format("End stage {0} cannot be present",
                            constructionStage.end().getSerializedName()));
                }
                if (stage == null) {
                    return DataResult.error(() -> MessageFormat.format("Required stage {0} not present",
                            constructionStage.getSerializedName()));
                }
            }

            return DataResult.success(map);
        }).codec();
    }

    public static <E extends Enum<E> & ConstructionStage<E>> ConstructionInput<E> of(
            @Singular final Map<E, ProgressStage> stages, final EntityResult constructedEntity,
            final SoundEvent assembleSound, final ParticleOptions assembleParticle) {
        return stages.isEmpty() ? empty() : new ConstructionInput<>(assembleSound, assembleParticle, constructedEntity,
                stages);
    }

    @SuppressWarnings("unchecked")
    public static <E extends Enum<E> & ConstructionStage<E>> ConstructionInput<E> empty() {
        return ((ConstructionInput<E>) EMPTY);
    }

    private void validate(final E constructionStage) {
        Objects.requireNonNull(constructionStage, "Construction Stage cannot be null");
        assert constructionStage != constructionStage.end() : MessageFormat.format(
                "End Construction Stage {0} cannot have values", constructionStage.end().getSerializedName());
    }

    /**
     * @return A sized ingredient representing what it takes to complete the provided stage
     */
    public SizedIngredient getIngredient(final E constructionStage) {
        this.validate(constructionStage);
        return this.stages.get(constructionStage).ingredient;
    }

    /**
     * @return The progress sound
     */
    public SoundEvent getProgressSound(final E constructionStage) {
        this.validate(constructionStage);
        return this.stages.get(constructionStage).progressSound;
    }

    /**
     * @return The switch sound
     */
    public SoundEvent getSwitchSound(final E constructionStage) {
        this.validate(constructionStage);
        return this.stages.get(constructionStage).switchSound;
    }

    /**
     * @return The switch particle
     */
    public ParticleOptions getSwitchParticle(final E constructionStage) {
        this.validate(constructionStage);
        return this.stages.get(constructionStage).switchParticle;
    }

    /**
     *
     * @param <E>
     */
    public interface ConstructionStage<E extends Enum<E> & ConstructionStage<E>> extends StringRepresentable, Comparable<E> {
        /**
         * @return The next stage
         */
        E next();

        /**
         * @return The end stage
         */
        E end();
    }

    public record ConstructionState<E extends Enum<E> & ConstructionStage<E>>(E inputStage,
            @Range(from = 0, to = Integer.MAX_VALUE) int remainingInputs) {

        public static <E extends Enum<E> & ConstructionStage<E>> Codec<ConstructionState<E>> codec(
                final Codec<E> stageCodec, final E startingStage) {
            return RecordCodecBuilder.create(instance -> instance.group(
                            stageCodec.optionalFieldOf("input_stage", startingStage).forGetter(ConstructionState::inputStage),
                            Codec.INT.optionalFieldOf("remaining_input_count", 0).forGetter(ConstructionState::remainingInputs))
                    .apply(instance, ConstructionState::new));
        }

        @Contract("_ -> new")
        public static <E extends Enum<E> & ConstructionStage<E>> StreamCodec<FriendlyByteBuf, ConstructionState<E>> streamCodec(
                final Class<E> clazz) {
            return StreamCodec.composite(NeoForgeStreamCodecs.enumCodec(clazz), ConstructionState::inputStage,
                    ByteBufCodecs.VAR_INT, ConstructionState::remainingInputs, ConstructionState::new);
        }

        @Contract("_ -> new")
        public ConstructionState<E> withRemaining(final int remaining) {
            return new ConstructionState<>(this.inputStage, remaining);
        }

        public ConstructionState<E> nextStateOf(final ConstructionInput<E> constructionInput) {
            final var nextStage = this.inputStage.next();
            return new ConstructionState<>(nextStage,
                    nextStage != nextStage.end() ? constructionInput.getIngredient(nextStage).count() : -1);
        }
    }

    @SuppressWarnings("cast") // Lombok code gen has redundant casts
    public static class ConstructionInputBuilder<E extends Enum<E> & ConstructionStage<E>> {}

    /**
     * @param ingredient     An ingredient
     * @param progressSound  The progress sound
     * @param switchSound    The switch sound
     * @param switchParticle The switch particle
     */
    public record ProgressStage(SizedIngredient ingredient, SoundEvent progressSound, SoundEvent switchSound,
            ParticleOptions switchParticle) {

        public static final Codec<ProgressStage> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                        NeoForgeExtraCodecs.withAlternative(SizedIngredient.FLAT_CODEC, SizedIngredient.NESTED_CODEC)
                                .fieldOf("input")
                                .forGetter(ProgressStage::ingredient), BuiltInRegistries.SOUND_EVENT.byNameCodec()
                                .fieldOf("progress_sound")
                                .forGetter(ProgressStage::progressSound), BuiltInRegistries.SOUND_EVENT.byNameCodec()
                                .fieldOf("switch_sound")
                                .forGetter(ProgressStage::switchSound),
                        ParticleTypes.CODEC.fieldOf("switch_particle").forGetter(ProgressStage::switchParticle))
                .apply(instance, ProgressStage::new));

        public static final StreamCodec<RegistryFriendlyByteBuf, ProgressStage> STREAM_CODEC = StreamCodec.composite(
                SizedIngredient.STREAM_CODEC, ProgressStage::ingredient, ByteBufCodecs.registry(Registries.SOUND_EVENT),
                ProgressStage::progressSound, ByteBufCodecs.registry(Registries.SOUND_EVENT),
                ProgressStage::switchSound, ParticleTypes.STREAM_CODEC, ProgressStage::switchParticle,
                ProgressStage::new);
    }
}