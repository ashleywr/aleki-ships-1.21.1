package com.alekiponi.alekiships.common.entity.vehicle;

import com.mojang.serialization.Codec;

import com.alekiponi.alekiships.common.entity.vehiclehelper.ConstructionEntity;
import com.alekiponi.alekiships.util.AlekiShipsExtraCodecs;
import com.alekiponi.alekiships.util.ItemContents;

import net.minecraft.BlockUtil;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.VariantHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import net.neoforged.neoforge.common.util.Lazy;
import net.neoforged.neoforge.entity.PartEntity;

import java.util.Arrays;
import org.jetbrains.annotations.Nullable;

public abstract class AbstractUnderConstructionEntity<T extends ConstructionVariant<E>, E extends Enum<E> & ConstructionInput.ConstructionStage<E>> extends Entity implements VariantHolder<Holder<T>> {

    public static final String CONSTRUCTION_CONTENTS_KEY = "construction_contents";
    public static final String CONSTRUCTION_STATE_NBT_KEY = "construction_state";
    protected static final EntityDataAccessor<Float> DATA_ID_DAMAGE = SynchedEntityData.defineId(
            AbstractUnderConstructionEntity.class, EntityDataSerializers.FLOAT);

    public final ConstructionEntity constructionEntity;
    /**
     * You typically shouldn't directly access this. Instead use {@link #getContentsCount(Enum)}
     */
    protected final ItemContents constructionContents;
    /**
     * Lazy as we expect only the client to ever need this
     */
    protected final Lazy<ItemStack[]> requiredItems;
    private final PartEntity<?>[] subEntities;
    private final Codec<ConstructionInput.ConstructionState<E>> stateCodec;

    protected AbstractUnderConstructionEntity(
            final EntityType<? extends AbstractUnderConstructionEntity<T, E>> entityType, final Level level,
            final int stages, final Codec<ConstructionInput.ConstructionState<E>> stateCodec) {
        super(entityType, level);
        this.blocksBuilding = true;
        this.constructionEntity = new ConstructionEntity(this);
        this.subEntities = new PartEntity<?>[]{this.constructionEntity};
        this.stateCodec = stateCodec;
        this.constructionContents = new ItemContents(stages);

        this.requiredItems = Lazy.of(() -> {
            final var constructionState = this.getConstructionState();
            if (this.isFinished()) return new ItemStack[0];

            final var ingredient = this.getConstructionInput().value().getIngredient(constructionState.inputStage());
            final int count = constructionState.remainingInputs();
            return Arrays.stream(ingredient.getItems())
                    .map(itemStack -> itemStack.copyWithCount(count))
                    .toArray(ItemStack[]::new);
        });

        this.setId(ENTITY_COUNTER.getAndAdd(this.subEntities.length + 1) + 1);
    }

    @Override
    public void setId(final int id) {
        super.setId(id);
        for (int i = 0; i < this.subEntities.length; i++) {
            this.subEntities[i].setId(id + i + 1);
        }
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        builder.define(DATA_ID_DAMAGE, 0f);
    }

    @Override
    public void tick() {
        super.tick();

        final var vec3 = this.positionLocally(
                this.getConstructionEntityPosition(this.getConstructionState().inputStage())).add(this.position());
        this.constructionEntity.setOldPosAndRot();
        this.constructionEntity.setPos(vec3);
    }

    @Override
    public InteractionResult interact(final Player player, final InteractionHand hand) {
        return InteractionResult.PASS;
    }

    public InteractionResult interactFromConstructionEntity(final Player player, final InteractionHand hand) {
        final var heldItem = player.getItemInHand(hand);

        final var previousStage = this.getConstructionState().inputStage();

        final var constructionInput = this.getConstructionInput().value();
        final var insertionResult = this.tryInsert(player, constructionInput, heldItem);
        if (!insertionResult.getResult().consumesAction()) return InteractionResult.PASS;

        player.setItemInHand(hand, insertionResult.getObject());
        this.playSound(constructionInput.getProgressSound(previousStage));

        final var currentStage = this.getConstructionState().inputStage();

        if (previousStage != currentStage) {
            this.progressConstructionStage(constructionInput, previousStage, currentStage);
        }

        if (this.isFinished()) {
            this.finalizeConstruction(player, constructionInput);
            return InteractionResult.SUCCESS;
        }

        return insertionResult.getResult();
    }

    private InteractionResultHolder<ItemStack> tryInsert(final @Nullable LivingEntity entity,
            final ConstructionInput<E> constructionInput, final ItemStack insertStack) {
        if (this.isFinished()) return InteractionResultHolder.pass(insertStack);

        final var constructionState = this.getConstructionState();
        final var stage = constructionState.inputStage();
        assert stage != stage.end();
        final var ingredient = constructionInput.getIngredient(stage);

        // We only care to match the Item, not it's size
        if (!ingredient.ingredient().test(insertStack)) return InteractionResultHolder.pass(insertStack);

        final var remainder = this.doInsert(entity, insertStack, 1);
        if (ingredient.count() > this.getContentsCount(stage)) {
            this.setConstructionState(constructionState.withRemaining(constructionState.remainingInputs() - 1));
            return InteractionResultHolder.success(remainder);
        }

        this.setConstructionState(constructionState.nextStateOf(constructionInput));
        return InteractionResultHolder.success(remainder);
    }

    private ItemStack doInsert(final @Nullable LivingEntity entity, final ItemStack insertStack,
            @SuppressWarnings("SameParameterValue") final int increment) {
        final E stage = this.getConstructionState().inputStage();
        if (entity != null && entity.hasInfiniteMaterials()) {
            this.insert(stage, insertStack.copyWithCount(increment));
            return insertStack;
        }

        final var stack = insertStack.copyWithCount(increment);
        final var remainder = this.insert(stage, stack);
        if (remainder.isEmpty()) return ItemStack.EMPTY;
        stack.shrink(remainder.getCount());
        return stack;
    }

    /**
     * Handler for switching to a new construction stage
     *
     * @param constructionInput The current {@link ConstructionInput}
     * @param previousStage     The stage we are switching from
     * @param nextStage         The next stage we are now at
     */
    protected abstract void progressConstructionStage(ConstructionInput<E> constructionInput, E previousStage,
            E nextStage);

    /**
     * Handler for finalizing the construction process
     *
     * @param player            The player finalizing the construction
     * @param constructionInput The current {@link ConstructionInput}
     */
    protected abstract void finalizeConstruction(Player player, ConstructionInput<E> constructionInput);

    @Override
    public Vec3 getRelativePortalPosition(final Direction.Axis axis, final BlockUtil.FoundRectangle portal) {
        return LivingEntity.resetForwardDirectionOfRelativePortalPosition(
                super.getRelativePortalPosition(axis, portal));
    }

    @Override
    public boolean hurt(final DamageSource damageSource, final float amount) {
        if (this.isInvulnerableTo(damageSource)) return false;

        if (this.level().isClientSide || this.isRemoved()) return true;

        this.setDamage(this.getDamage() + amount);
        this.markHurt();
        this.gameEvent(GameEvent.ENTITY_DAMAGE, damageSource.getEntity());
        final boolean instantKill = damageSource.getEntity() instanceof Player player && player.hasInfiniteMaterials() && damageSource.is(
                DamageTypes.PLAYER_ATTACK);

        if (instantKill) {
            this.setDamage(this.getDamage() + this.getDamageThreshold() / 4);
        }
        if (this.getDamage() > this.getDamageThreshold()) {
            if (this.level().getGameRules().getBoolean(GameRules.RULE_DOENTITYDROPS)) {
                if (this.level() instanceof ServerLevel serverLevel) {
                    this.dropAllDestructionLoot(damageSource, serverLevel);
                }
            }
            this.kill();
        }

        return true;
    }

    @Override
    protected Component getTypeName() {
        return Component.translatable(this.getType().getDescriptionId(), this.getVariant().value().getName());
    }

    @Override
    public boolean isPickable() {
        return !this.isRemoved();
    }

    public final Vec3 getLocalConstructionEntityPosition() {
        return this.positionLocally(this.getConstructionEntityPosition(this.getConstructionState().inputStage()));
    }

    protected abstract Vec3 getConstructionEntityPosition(E e);

    protected final Vec3 positionLocally(final float localX, final float localY, final float localZ) {
        return this.positionLocally(new Vec3(localX, localY, localZ));
    }

    protected final Vec3 positionLocally(final Vec3 vec3) {
        return vec3.yRot((float) Math.toRadians(-(this.getYRot() + 90)));
    }

    @Override
    public boolean canBeCollidedWith() {
        return true;
    }

    @Override
    public boolean isPushable() {
        return true;
    }

    public abstract float getDamageThreshold();

    @Override
    protected boolean canAddPassenger(final Entity passenger) {
        return false;
    }

    @Override
    public PartEntity<?>[] getParts() {
        return this.subEntities;
    }

    @Override
    public boolean isMultipartEntity() {
        return true;
    }

    protected void dropCustomDestructionLoot(final DamageSource damageSource) {
        this.constructionContents.forEach(this::spawnAtLocation);
    }

    public ItemStack insert(final E stage, final ItemStack insertStack) {
        return this.constructionContents.insertItem(stage.ordinal(), insertStack);
    }

    /**
     * @return The construction state
     */
    public abstract ConstructionInput.ConstructionState<E> getConstructionState();

    /**
     * Sets the current construction state
     *
     * @param constructionState The new construction state
     */
    public abstract void setConstructionState(ConstructionInput.ConstructionState<E> constructionState);

    /**
     * Drops all destruction loot when the vehicle is destroyed
     *
     * @param damageSource The cause of the Destruction
     */
    protected void dropAllDestructionLoot(final DamageSource damageSource, final ServerLevel serverLevel) {
        this.dropFromLootTable(serverLevel, damageSource);
        this.dropCustomDestructionLoot(damageSource);
    }

    /**
     * Drops the loot in the vehicle loot table. Allows for data-packable vehicle loot most helpful
     * with vehicles that should drop multiple things such as the {@link SloopEntity}
     */
    protected void dropFromLootTable(final ServerLevel serverLevel, final DamageSource damageSource) {
        final var lootParams = new LootParams.Builder(serverLevel).withParameter(LootContextParams.THIS_ENTITY, this)
                .withParameter(LootContextParams.ORIGIN, this.position())
                .withParameter(LootContextParams.DAMAGE_SOURCE, damageSource)
                .withOptionalParameter(LootContextParams.ATTACKING_ENTITY, damageSource.getEntity())
                .withOptionalParameter(LootContextParams.DIRECT_ATTACKING_ENTITY, damageSource.getDirectEntity())
                .create(LootContextParamSets.ENTITY);
        final var lootTable = serverLevel.getServer().reloadableRegistries().getLootTable(this.getLootTable());
        lootTable.getRandomItems(lootParams, this.getLootTableSeed(), this::spawnAtLocation);
    }

    @Override
    public AABB getBoundingBoxForCulling() {
        float bbRadius = this.getBbWidth() * 3 + 1;
        Vec3 startingPoint = new Vec3(this.getX() - bbRadius, this.getY() - bbRadius, this.getZ() - bbRadius);
        Vec3 endingPoint = new Vec3(this.getX() + bbRadius, this.getY() + bbRadius, this.getZ() + bbRadius);
        return new AABB(startingPoint, endingPoint);
    }

    public boolean isFinished() {
        final var stage = this.getConstructionState().inputStage();
        return stage.compareTo(stage.end()) == 0;
    }

    @Override
    public void onAboveBubbleCol(final boolean downwards) {}

    @Override
    public void onInsideBubbleColumn(final boolean downwards) {}

    @Override
    protected void readAdditionalSaveData(final CompoundTag compoundTag) {
        final var ops = this.registryAccess().createSerializationContext(NbtOps.INSTANCE);
        AlekiShipsExtraCodecs.read(this.stateCodec, ops, compoundTag.get(CONSTRUCTION_STATE_NBT_KEY))
                .ifPresent(this::setConstructionState);
        this.constructionContents.deserializeNBT(this.registryAccess(),
                compoundTag.getCompound(CONSTRUCTION_CONTENTS_KEY));
    }

    @Override
    protected void addAdditionalSaveData(final CompoundTag compoundTag) {
        final var ops = this.registryAccess().createSerializationContext(NbtOps.INSTANCE);
        AlekiShipsExtraCodecs.write(this.stateCodec, ops, this.getConstructionState())
                .ifPresent(tag -> compoundTag.put(CONSTRUCTION_STATE_NBT_KEY, tag));
        compoundTag.put(CONSTRUCTION_CONTENTS_KEY, this.constructionContents.serializeNBT(this.registryAccess()));
    }

    @Override
    protected MovementEmission getMovementEmission() {
        return MovementEmission.NONE;
    }

    public final float getDamage() {
        return this.entityData.get(DATA_ID_DAMAGE);
    }

    public final void setDamage(final float damage) {
        this.entityData.set(DATA_ID_DAMAGE, damage);
    }

    /**
     * @return A potentially empty array of accepted ItemStack inputs
     */
    public ItemStack[] getRequiredItems() {
        return this.requiredItems.get();
    }

    public int getContentsCount(final E stage) {
        return this.constructionContents.getCount(stage.ordinal());
    }

    public final Holder<ConstructionInput<E>> getConstructionInput() {
        return this.getVariant().value().getConstructionInput();
    }

    /**
     * @return The loot table that should be used
     */
    public ResourceKey<LootTable> getLootTable() {
        return this.getType().getDefaultLootTable();
    }

    /**
     * @return The seed to be used for the loot
     */
    public long getLootTableSeed() {
        return 0;
    }
}