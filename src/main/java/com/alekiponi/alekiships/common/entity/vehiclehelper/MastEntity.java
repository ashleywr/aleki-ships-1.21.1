package com.alekiponi.alekiships.common.entity.vehiclehelper;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import com.alekiponi.alekiships.network.AlekiShipsEntityDataSerializers;
import com.alekiponi.alekiships.util.AlekiShipsExtraCodecs;

import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BannerItem;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BannerBlock;
import net.minecraft.world.level.block.entity.BannerPatternLayers;
import net.minecraft.world.phys.Vec3;

import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.items.ItemHandlerHelper;

import java.util.Optional;

public class MastEntity extends AbstractPassthroughHelper {

    public static final String BANNER_PATTERN_KEY = "banner_pattern";
    private static final EntityDataAccessor<Optional<MastBanner>> DATA_ID_BANNER = SynchedEntityData.defineId(
            MastEntity.class, AlekiShipsEntityDataSerializers.MAST_BANNER);

    public MastEntity(EntityType<?> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    @Override
    public void tick() {
        super.tick();

        if (this.level().isClientSide()) {

            final var playersToMoveWithMast = this.level()
                    .getEntities(this, this.getBoundingBox(), EntitySelector.NO_SPECTATORS);

            // Client-side correction keeps the local player from clipping through a moving mast; full movement
            // propagation remains intentionally owned by the vehicle/passenger system.
            for (final var entity : playersToMoveWithMast) {
                if ((entity instanceof LocalPlayer player)) {
                    Vec3 vehicleMovement = this.getRootVehicle().getDeltaMovement();
                    player.setPos(new Vec3(this.position().x + 0.3f, player.position().y, this.position().z + 0.3f));
                    if (player.input.jumping) {
                        player.setDeltaMovement(player.getDeltaMovement().multiply(1, 0, 1).add(0, 0.1, 0));
                    } else if (player.input.shiftKeyDown) {
                        player.setDeltaMovement(player.getDeltaMovement().multiply(1, 0, 1).add(0, 0, 0));
                    } else {
                        player.setDeltaMovement(player.getDeltaMovement().multiply(1, 0, 1).add(0, -0.1, 0));
                    }

                }
                if (entity instanceof Player player) {
                    player.resetFallDistance();
                }

            }

        }

    }

    @Override
    public InteractionResult interact(final Player player, final InteractionHand hand) {
        final var heldStack = player.getItemInHand(hand);
        {
            final var result = tryInsertBanner(player, heldStack);
            if (result.consumesAction()) {
                if (!player.hasInfiniteMaterials()) heldStack.shrink(1);
                return result;
            }
        }
        if (this.hasBanner() && heldStack.is(Tags.Items.TOOLS_SHEAR)) {
            heldStack.hurtAndBreak(1, player, LivingEntity.getSlotForHand(hand));
            this.getBanner()
                    .map(MastBanner::createBannerStack)
                    .ifPresent(stack -> ItemHandlerHelper.giveItemToPlayer(player, stack));
            this.removeBanner();
            this.level()
                    .playSound(null, this, SoundEvents.SHEEP_SHEAR, SoundSource.BLOCKS, 1.5F,
                            this.level().getRandom().nextFloat() * 0.1F + 0.9F);
            return InteractionResult.SUCCESS;
        }
        return super.interact(player, hand);
    }

    private InteractionResult tryInsertBanner(final Player player, final ItemStack heldStack) {
        final var newBanner = MastBanner.fromStack(heldStack);
        if (newBanner.isEmpty()) return InteractionResult.PASS;
        if (this.getBanner().equals(newBanner)) return InteractionResult.PASS;

        this.getBanner()
                .map(MastBanner::createBannerStack)
                .ifPresent(stack -> ItemHandlerHelper.giveItemToPlayer(player, stack));
        this.setBanner(newBanner.get());
        this.level()
                .playSound(player, this, SoundEvents.WOOL_PLACE, SoundSource.BLOCKS, 1.5F,
                        this.level().getRandom().nextFloat() * 0.1F + 0.9F);
        return InteractionResult.SUCCESS;
    }

    public boolean hasBanner() {
        return this.getBanner().isPresent();
    }

    public Optional<MastBanner> getBanner() {
        return this.entityData.get(DATA_ID_BANNER);
    }

    public void setBanner(final MastBanner banner) {
        this.entityData.set(DATA_ID_BANNER, Optional.of(banner));
    }

    public void removeBanner() {
        this.entityData.set(DATA_ID_BANNER, Optional.empty());
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        builder.define(DATA_ID_BANNER, Optional.empty());
    }

    @Override
    protected void addAdditionalSaveData(final CompoundTag compound) {
        final var ops = this.registryAccess().createSerializationContext(NbtOps.INSTANCE);
        AlekiShipsExtraCodecs.writeNullable(MastBanner.CODEC, ops, this.getBanner().orElse(null))
                .ifPresent(tag -> compound.put(BANNER_PATTERN_KEY, tag));
    }

    @Override
    protected void readAdditionalSaveData(final CompoundTag compound) {
        final var ops = this.registryAccess().createSerializationContext(NbtOps.INSTANCE);
        AlekiShipsExtraCodecs.read(MastBanner.CODEC, ops, compound.get(BANNER_PATTERN_KEY)).ifPresent(this::setBanner);
    }

    public record MastBanner(BannerPatternLayers patternLayers, DyeColor baseColor) {
        public static final Codec<MastBanner> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                BannerPatternLayers.CODEC.fieldOf("pattern").forGetter(MastBanner::patternLayers),
                DyeColor.CODEC.fieldOf("color").forGetter(MastBanner::baseColor)).apply(instance, MastBanner::new));

        public static final StreamCodec<RegistryFriendlyByteBuf, MastBanner> STREAM_CODEC = StreamCodec.composite(
                BannerPatternLayers.STREAM_CODEC, MastBanner::patternLayers, DyeColor.STREAM_CODEC,
                MastBanner::baseColor, MastBanner::new);

        /**
         * Get an optional {@link MastBanner} from an {@link ItemStack}
         *
         * @param stack The stack
         *
         * @return An optional {@link MastBanner}
         */
        public static Optional<MastBanner> fromStack(final ItemStack stack) {
            if (!(stack.getItem() instanceof BannerItem bannerItem)) return Optional.empty();
            final var bannerPattern = stack.getOrDefault(DataComponents.BANNER_PATTERNS, BannerPatternLayers.EMPTY);
            return Optional.of(new MastBanner(bannerPattern, bannerItem.getColor()));
        }

        /**
         * @return An {@link ItemStack} of a banner with the same pattern and base color
         */
        public ItemStack createBannerStack() {
            final var bannerItem = BannerBlock.byColor(this.baseColor).asItem();
            final var stack = new ItemStack(bannerItem);
            stack.set(DataComponents.BANNER_PATTERNS, this.patternLayers);
            return stack;
        }
    }
}
