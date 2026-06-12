package com.alekiponi.alekiships.common.entity.vehicle;

import com.mojang.serialization.Codec;

import com.alekiponi.alekiships.common.AlekiShipsRegistries;
import com.alekiponi.alekiships.network.AlekiShipsEntityDataSerializers;
import com.alekiponi.alekiships.util.AlekiShipsExtraCodecs;
import com.alekiponi.alekiships.util.MutableVec3i;
import com.alekiponi.alekiships.util.advancements.AlekiShipsAdvancements;

import net.minecraft.core.Holder;
import net.minecraft.core.Vec3i;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class SloopUnderConstructionEntity extends AbstractUnderConstructionEntity<ConstructionSloopVariant, SloopUnderConstructionEntity.SloopConstructionStage> {

    public static final String SLOOP_CONSTRUCTION_TYPE_KEY = "sloop_construction_type";

    public static final Codec<ConstructionInput.ConstructionState<SloopConstructionStage>> CODEC = ConstructionInput.ConstructionState.codec(
            SloopConstructionStage.ENUM_CODEC, SloopConstructionStage.START);

    private static final EntityDataAccessor<ConstructionInput.ConstructionState<SloopConstructionStage>> DATA_ID_CONSTRUCTION_STATE = SynchedEntityData.defineId(
            SloopUnderConstructionEntity.class, AlekiShipsEntityDataSerializers.SLOOP_CONSTRUCTION_STATE);
    private static final EntityDataAccessor<Holder<ConstructionSloopVariant>> DATA_ID_SLOOP_CONSTRUCTION_VARIANT = SynchedEntityData.defineId(
            SloopUnderConstructionEntity.class, AlekiShipsEntityDataSerializers.SLOOP_CONSTRUCTION_VARIANT);

    public SloopUnderConstructionEntity(final EntityType<? extends SloopUnderConstructionEntity> entityType,
            final Level level) {
        super(entityType, level, SloopConstructionStage.SIZE, CODEC);
    }

    @Override
    protected void defineSynchedData(final SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        final var registry = this.registryAccess().registryOrThrow(AlekiShipsRegistries.CONSTRUCTION_SLOOP_VARIANT);
        final var constructionType = registry.getHolder(ConstructionSloopVariants.DEFAULT)
                .or(registry::getAny)
                .orElseThrow();
        builder.define(DATA_ID_SLOOP_CONSTRUCTION_VARIANT, constructionType);
        final var constructionInput = constructionType.value().getConstructionInput();
        builder.define(DATA_ID_CONSTRUCTION_STATE,
                new ConstructionInput.ConstructionState<>(SloopConstructionStage.START,
                        constructionInput.value().getIngredient(SloopConstructionStage.START).count()));
    }

    @Override
    public void onSyncedDataUpdated(final EntityDataAccessor<?> key) {
        super.onSyncedDataUpdated(key);
        if (DATA_ID_SLOOP_CONSTRUCTION_VARIANT.equals(key)) {
            this.constructionContents.clear();
        }
        if (DATA_ID_CONSTRUCTION_STATE.equals(key)) {
            this.requiredItems.invalidate();
        }
    }

    @Override
    protected Vec3 getConstructionEntityPosition(final SloopConstructionStage stage) {
        final var yOffset = this.isRemoved() ? 0.01F : 0;
        return switch (stage) {
            case KEEL -> new Vec3(0, yOffset, 0);
            case DECK -> new Vec3(-1.0f, 0.5 + yOffset, 0);
            case BOWSPRIT -> new Vec3(4.0f, 0.7 + yOffset, 0);
            case MAST -> new Vec3(2.0f, 2 + yOffset, 0);
            case BOOM -> new Vec3(1.8f, 2.25 + yOffset, 0);
            case MAINSAIL -> new Vec3(1.5f, 2.5 + yOffset, 0);
            case JIBSAIl -> new Vec3(3.0f, 0.5 + yOffset, 0);
            case RAILINGS_STERN -> new Vec3(-3.0f, 0.8 + yOffset, 0);
            case RAILINGS_BOW -> new Vec3(4.0f, 0.8 + yOffset, 0);
            case ANCHOR -> new Vec3(2.52f, 0.7 + yOffset, -1.34f);
            case RIGGING -> new Vec3(1.5f, 1.5 + yOffset, 0);
            case COMPLETE -> Vec3.ZERO;
        };
    }

    @Override
    public float getDamageThreshold() {
        return 40;
    }

    @Override
    protected void progressConstructionStage(final ConstructionInput<SloopConstructionStage> constructionInput,
            final SloopConstructionStage previousStage, final SloopConstructionStage nextStage) {
        this.playSound(constructionInput.getSwitchSound(previousStage));

        final var switchParticle = constructionInput.getSwitchParticle(previousStage);
        for (final var localPos : MutableVec3i.betweenClosed(Vec3i.ZERO, new Vec3i(7, 0, 4))) {
            localPos.add(-3, 0, -2);
            final var position = this.position().add(this.positionLocally(Vec3.atLowerCornerOf(localPos)));
            this.addConstructionParticles(position, switchParticle);
        }
    }

    private void addConstructionParticles(final Vec3 pos, final ParticleOptions particleOptions) {
        final var forward = Vec3.directionFromRotation(0, this.getYRot());
        final var right = forward.cross(new Vec3(0, 1, 0));
        // 12 particles
        for (final var vec3i : MutableVec3i.betweenClosed(0, 0, 0, 2, 2, 2)) {
            final var offset = this.positionLocally(new Vec3(-0.5, -0.5, -0.5))
                    .add(forward.scale(vec3i.getX() / 2D))
                    .add(right.scale(vec3i.getZ() / 2D))
                    .add(0, vec3i.getY() / 2D, 0);
            final var realPos = pos.add(offset);
            final var speed = offset.reverse();
            this.level()
                    .addParticle(particleOptions, realPos.x(), realPos.y(), realPos.z(), speed.x(), speed.y(),
                            speed.z());
        }
    }

    @Override
    protected void finalizeConstruction(final Player player,
            final ConstructionInput<SloopConstructionStage> constructionInput) {
        constructionInput.constructedEntity.createEntity(this.level()).ifPresent(entity -> {
            entity.setYRot(this.getYRot());
            entity.setPos(this.position());
            this.level().addFreshEntity(entity);
            if (entity instanceof SloopEntity && player instanceof ServerPlayer serverPlayer) {
                AlekiShipsAdvancements.SLOOP_COMPLETED.trigger(serverPlayer);
            }
        });

        for (final var localPos : MutableVec3i.betweenClosed(Vec3i.ZERO, new Vec3i(7, 11, 4))) {
            localPos.add(-3, 0, -2);
            final var position = this.position().add(this.positionLocally(Vec3.atLowerCornerOf(localPos)));
            this.addConstructionParticles(position, constructionInput.assembleParticle);
        }

        this.kill();
    }

    @Override
    protected void readAdditionalSaveData(final CompoundTag compoundTag) {
        final var ops = this.registryAccess().createSerializationContext(NbtOps.INSTANCE);
        AlekiShipsExtraCodecs.read(ConstructionSloopVariant.CODEC, ops, compoundTag.get(SLOOP_CONSTRUCTION_TYPE_KEY))
                .ifPresent(this::setVariant);
        // The variant must be set before the construction state is read
        super.readAdditionalSaveData(compoundTag);
    }

    @Override
    protected void addAdditionalSaveData(final CompoundTag compoundTag) {
        super.addAdditionalSaveData(compoundTag);
        final var ops = this.registryAccess().createSerializationContext(NbtOps.INSTANCE);
        AlekiShipsExtraCodecs.write(ConstructionSloopVariant.CODEC, ops, this.getVariant())
                .ifPresent(tag -> compoundTag.put(SLOOP_CONSTRUCTION_TYPE_KEY, tag));
    }

    @Override
    public final ConstructionInput.ConstructionState<SloopConstructionStage> getConstructionState() {
        return this.entityData.get(DATA_ID_CONSTRUCTION_STATE);
    }

    @Override
    public final void setConstructionState(
            final ConstructionInput.ConstructionState<SloopConstructionStage> constructionState) {
        this.entityData.set(DATA_ID_CONSTRUCTION_STATE, constructionState);
    }

    @Override
    public final Holder<ConstructionSloopVariant> getVariant() {
        return this.entityData.get(DATA_ID_SLOOP_CONSTRUCTION_VARIANT);
    }

    @Override
    public final void setVariant(final Holder<ConstructionSloopVariant> variant) {
        this.entityData.set(DATA_ID_SLOOP_CONSTRUCTION_VARIANT, variant);
        final var ingredient = variant.value()
                .getConstructionInput()
                .value()
                .getIngredient(SloopConstructionStage.START);
        this.setConstructionState(
                new ConstructionInput.ConstructionState<>(SloopConstructionStage.START, ingredient.count()));
    }

    public ResourceLocation getTexture() {
        return this.getVariant().value().texture();
    }

    public enum SloopConstructionStage implements ConstructionInput.ConstructionStage<SloopConstructionStage> {
        KEEL("keel"),
        DECK("deck"),
        BOWSPRIT("bowsprit"),
        MAST("mast"),
        BOOM("boom"),
        MAINSAIL("mainsail"),
        JIBSAIl("jibsail"),
        RAILINGS_STERN("stern_railings"),
        RAILINGS_BOW("bow_railings"),
        ANCHOR("anchor"),
        RIGGING("rigging"),
        COMPLETE("complete");

        public static final SloopConstructionStage START = KEEL;

        public static final int SIZE = SloopConstructionStage.values().length - 1;

        public static final StringRepresentableCodec<SloopConstructionStage> ENUM_CODEC = StringRepresentable.fromEnum(
                SloopConstructionStage::values);

        public static final Codec<ConstructionInput<SloopConstructionStage>> INPUT_CODEC = ConstructionInput.codec(
                ENUM_CODEC, SloopConstructionStage::values);

        public static final StreamCodec<RegistryFriendlyByteBuf, ConstructionInput<SloopConstructionStage>> INPUT_STREAM_CODEC = ConstructionInput.streamCodec(
                SloopConstructionStage.class);

        private final String name;

        SloopConstructionStage(final String name) {
            this.name = name;
        }

        @Override
        public String getSerializedName() {
            return this.name;
        }

        @Override
        public SloopConstructionStage next() {
            return switch (this) {
                case KEEL -> DECK;
                case DECK -> BOWSPRIT;
                case BOWSPRIT -> MAST;
                case MAST -> BOOM;
                case BOOM -> MAINSAIL;
                case MAINSAIL -> JIBSAIl;
                case JIBSAIl -> RAILINGS_STERN;
                case RAILINGS_STERN -> RAILINGS_BOW;
                case RAILINGS_BOW -> ANCHOR;
                case ANCHOR -> RIGGING;
                case RIGGING -> COMPLETE;
                case COMPLETE -> throw new UnsupportedOperationException("Complete is the final construction stage");
            };
        }

        @Override
        public SloopConstructionStage end() {
            return COMPLETE;
        }
    }
}