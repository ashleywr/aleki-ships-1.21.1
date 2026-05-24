package com.alekiponi.alekiships.common.entity;

import com.alekiponi.alekiships.common.entity.vehicle.ConstructionInput;
import com.alekiponi.alekiships.common.entity.vehicle.ConstructionInput.ProgressStage;
import com.alekiponi.alekiships.common.entity.vehicle.SloopUnderConstructionEntity;
import com.alekiponi.alekiships.common.recipe.entity.EntityResult;

import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.level.ItemLike;

import net.neoforged.neoforge.common.crafting.SizedIngredient;

public final class SloopConstructionInput {

    public static SloopConstructionInputBuilder builder() {
        return new SloopConstructionInputBuilder();
    }

    public static final class SloopConstructionInputBuilder {

        private final ConstructionInput.ConstructionInputBuilder<SloopUnderConstructionEntity.SloopConstructionStage> builder = ConstructionInput.<SloopUnderConstructionEntity.SloopConstructionStage>builder();

        public SloopConstructionInputBuilder keel(final ItemLike strippedLogItem, final SoundEvent progressSound,
                final SoundEvent switchSound, final ParticleOptions switchParticle) {
            return this.stage(SloopUnderConstructionEntity.SloopConstructionStage.KEEL,
                    new ProgressStage(SizedIngredient.of(strippedLogItem, SloopConstructionInputs.KEEL_LOGS),
                            progressSound, switchSound, switchParticle));
        }

        public SloopConstructionInputBuilder deck(final ItemLike deckItem, final SoundEvent progressSound,
                final SoundEvent switchSound, final ParticleOptions switchParticle) {
            return stage(SloopUnderConstructionEntity.SloopConstructionStage.DECK,
                    new ProgressStage(SizedIngredient.of(deckItem, SloopConstructionInputs.DECK_PLANKS), progressSound,
                            switchSound, switchParticle));
        }

        public SloopConstructionInputBuilder bowsprit(final ItemLike strippedLogItem, final SoundEvent progressSound,
                final SoundEvent switchSound, final ParticleOptions switchParticle) {
            return stage(SloopUnderConstructionEntity.SloopConstructionStage.BOWSPRIT,
                    new ProgressStage(SizedIngredient.of(strippedLogItem, SloopConstructionInputs.BOWSPRIT_LOGS),
                            progressSound, switchSound, switchParticle));
        }

        public SloopConstructionInputBuilder mast(final ItemLike strippedLogItem, final SoundEvent progressSound,
                final SoundEvent switchSound, final ParticleOptions switchParticle) {
            return stage(SloopUnderConstructionEntity.SloopConstructionStage.MAST,
                    new ProgressStage(SizedIngredient.of(strippedLogItem, SloopConstructionInputs.MAST_LOGS),
                            progressSound, switchSound, switchParticle));
        }

        public SloopConstructionInputBuilder boom(final ItemLike strippedLogItem, final SoundEvent progressSound,
                final SoundEvent switchSound, final ParticleOptions switchParticle) {
            return stage(SloopUnderConstructionEntity.SloopConstructionStage.BOOM,
                    new ProgressStage(SizedIngredient.of(strippedLogItem, SloopConstructionInputs.BOOM_LOGS),
                            progressSound, switchSound, switchParticle));
        }

        public SloopConstructionInputBuilder mainsail(final ItemLike woolItem, final SoundEvent progressSound,
                final SoundEvent switchSound, final ParticleOptions switchParticle) {
            return stage(SloopUnderConstructionEntity.SloopConstructionStage.MAINSAIL,
                    new ProgressStage(SizedIngredient.of(woolItem, SloopConstructionInputs.MAINSAIL_WOOL),
                            progressSound, switchSound, switchParticle));
        }

        public SloopConstructionInputBuilder jibsail(final ItemLike woolItem, final SoundEvent progressSound,
                final SoundEvent switchSound, final ParticleOptions switchParticle) {
            return stage(SloopUnderConstructionEntity.SloopConstructionStage.JIBSAIl,
                    new ProgressStage(SizedIngredient.of(woolItem, SloopConstructionInputs.JIBSAIL_WOOL), progressSound,
                            switchSound, switchParticle));
        }

        public SloopConstructionInputBuilder railingStern(final ItemLike railingItem, final SoundEvent progressSound,
                final SoundEvent switchSound, final ParticleOptions switchParticle) {
            return stage(SloopUnderConstructionEntity.SloopConstructionStage.RAILINGS_STERN,
                    new ProgressStage(SizedIngredient.of(railingItem, SloopConstructionInputs.STERN_RAILINGS),
                            progressSound, switchSound, switchParticle));
        }

        public SloopConstructionInputBuilder railingBow(final ItemLike railingItem, final SoundEvent progressSound,
                final SoundEvent switchSound, final ParticleOptions switchParticle) {
            return stage(SloopUnderConstructionEntity.SloopConstructionStage.RAILINGS_BOW,
                    new ProgressStage(SizedIngredient.of(railingItem, SloopConstructionInputs.BOW_RAILINGS),
                            progressSound, switchSound, switchParticle));
        }

        public SloopConstructionInputBuilder anchor(final ItemLike anchor, final SoundEvent progressSound,
                final SoundEvent switchSound, final ParticleOptions switchParticle) {
            return stage(SloopUnderConstructionEntity.SloopConstructionStage.ANCHOR,
                    new ProgressStage(SizedIngredient.of(anchor, 1), progressSound, switchSound, switchParticle));
        }

        public SloopConstructionInputBuilder rigging(final ItemLike riggingItem, final SoundEvent progressSound,
                final SoundEvent switchSound, final ParticleOptions switchParticle) {
            return this.stage(SloopUnderConstructionEntity.SloopConstructionStage.RIGGING,
                    new ProgressStage(SizedIngredient.of(riggingItem, SloopConstructionInputs.RIGGING_CABLES),
                            progressSound, switchSound, switchParticle));
        }

        public ConstructionInput<SloopUnderConstructionEntity.SloopConstructionStage> build() {
            return this.builder.build();
        }

        public SloopConstructionInputBuilder assembleSound(final SoundEvent assembleSound) {
            this.builder.assembleSound(assembleSound);
            return this;
        }

        public SloopConstructionInputBuilder constructedEntity(final EntityResult constructedEntity) {
            this.builder.constructedEntity(constructedEntity);
            return this;
        }

        public SloopConstructionInputBuilder assembleParticle(final ParticleOptions assembleParticle) {
            this.builder.assembleParticle(assembleParticle);
            return this;
        }

        private SloopConstructionInputBuilder stage(final SloopUnderConstructionEntity.SloopConstructionStage stageKey,
                final ProgressStage stageValue) {
            this.builder.stage(stageKey, stageValue);
            return this;
        }
    }
}