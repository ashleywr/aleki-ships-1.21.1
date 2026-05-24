package com.alekiponi.alekiships.common.entity;

import com.alekiponi.alekiships.AlekiShips;
import com.alekiponi.alekiships.common.AlekiShipsRegistries;
import com.alekiponi.alekiships.common.entity.vehicle.ConstructionInput;
import com.alekiponi.alekiships.common.entity.vehicle.SloopUnderConstructionEntity;
import com.alekiponi.alekiships.common.entity.vehicle.SloopVariant;
import com.alekiponi.alekiships.common.entity.vehicle.SloopVariants;
import com.alekiponi.alekiships.common.item.AlekiShipsItems;
import com.alekiponi.alekiships.common.particles.ConstructionParticleOption;
import com.alekiponi.alekiships.common.recipe.entity.SloopResult;

import net.minecraft.core.Holder;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;

import lombok.Builder;

public final class SloopConstructionInputs {

    public static final ResourceKey<ConstructionInput<SloopUnderConstructionEntity.SloopConstructionStage>> OAK = createKey("oak");
    public static final ResourceKey<ConstructionInput<SloopUnderConstructionEntity.SloopConstructionStage>> SPRUCE = createKey("spruce");
    public static final ResourceKey<ConstructionInput<SloopUnderConstructionEntity.SloopConstructionStage>> BIRCH = createKey("birch");
    public static final ResourceKey<ConstructionInput<SloopUnderConstructionEntity.SloopConstructionStage>> ACACIA = createKey("acacia");
    public static final ResourceKey<ConstructionInput<SloopUnderConstructionEntity.SloopConstructionStage>> CHERRY = createKey("cherry");
    public static final ResourceKey<ConstructionInput<SloopUnderConstructionEntity.SloopConstructionStage>> JUNGLE = createKey("jungle");
    public static final ResourceKey<ConstructionInput<SloopUnderConstructionEntity.SloopConstructionStage>> DARK_OAK = createKey("dark_oak");
    public static final ResourceKey<ConstructionInput<SloopUnderConstructionEntity.SloopConstructionStage>> CRIMSON = createKey("crimson");
    public static final ResourceKey<ConstructionInput<SloopUnderConstructionEntity.SloopConstructionStage>> WARPED = createKey("warped");
    public static final ResourceKey<ConstructionInput<SloopUnderConstructionEntity.SloopConstructionStage>> MANGROVE = createKey("mangrove");
    public static final ResourceKey<ConstructionInput<SloopUnderConstructionEntity.SloopConstructionStage>> BAMBOO = createKey("bamboo");

    public static final int KEEL_LOGS = 8;
    public static final int DECK_PLANKS = 20;
    public static final int BOWSPRIT_LOGS = 6;
    public static final int MAST_LOGS = 12;
    public static final int BOOM_LOGS = 8;
    public static final int MAINSAIL_WOOL = 16;
    public static final int JIBSAIL_WOOL = 8;
    public static final int STERN_RAILINGS = 8;
    public static final int BOW_RAILINGS = 8;
    public static final int RIGGING_CABLES = 8;

    /**
     * The total amount of stripped logs it takes to asemble the sloop from the construction phase
     */
    public static final int TOTAL_STRIPPED_LOGS = KEEL_LOGS + BOWSPRIT_LOGS + MAST_LOGS + BOOM_LOGS;
    /**
     * The total amount of planks it takes to assemble the sloop from the construction phase
     */
    public static final int TOTAL_PLANKS = DECK_PLANKS;
    /**
     * The total amount of railings it takes to assemble the sloop from the construction phase
     */
    public static final int TOTAL_RAILINGS = STERN_RAILINGS + BOW_RAILINGS;
    /**
     * The total amount of wool it takes to assemble the sloop from the construction phase
     */
    public static final float TOTAL_WOOL = MAINSAIL_WOOL + JIBSAIL_WOOL;

    private static ResourceKey<ConstructionInput<SloopUnderConstructionEntity.SloopConstructionStage>> createKey(final String name) {
        return ResourceKey.create(AlekiShipsRegistries.CONSTRUCTION_SLOOP_INPUT, AlekiShips.location(name));
    }

    public static SloopConstructionInput.SloopConstructionInputBuilder standardSloop(final ParticleOptions particle,
            final Item strippedLogItem, final Item deckItem, final Item sailItem, final Item railingItem,
            final SoundEvent stageProgressSound, final SoundEvent stageSwitchSound) {
        return SloopConstructionInput.builder()
                .keel(strippedLogItem, stageProgressSound, stageSwitchSound, particle)
                .deck(deckItem, stageProgressSound, stageSwitchSound, particle)
                .bowsprit(strippedLogItem, stageProgressSound, stageSwitchSound, particle)
                .mast(strippedLogItem, stageProgressSound, stageSwitchSound, particle)
                .boom(strippedLogItem, stageProgressSound, stageSwitchSound, particle)
                .mainsail(sailItem, SoundEvents.WOOL_PLACE, SoundEvents.WOOL_BREAK, particle)
                .jibsail(sailItem, SoundEvents.WOOL_PLACE, SoundEvents.WOOL_BREAK, particle)
                .railingStern(railingItem, stageProgressSound, stageSwitchSound, particle)
                .railingBow(railingItem, stageProgressSound, stageSwitchSound, particle)
                .anchor(AlekiShipsItems.ANCHOR, SoundEvents.METAL_PLACE, stageSwitchSound, particle)
                .rigging(Items.LEAD, SoundEvents.LEASH_KNOT_PLACE, stageSwitchSound, particle);
    }

    public static void bootstrapOverworld(final BootstrapContext<ConstructionInput<SloopUnderConstructionEntity.SloopConstructionStage>> context) {
        final var sloopVariantLookup = context.lookup(AlekiShipsRegistries.SLOOP_VARIANT);
        context.register(OAK, standardSloopBuilder().particle(getParticle("oak_planks"))
                .strippedLogItem(Items.STRIPPED_OAK_LOG)
                .deckItem(Items.OAK_PLANKS)
                .sailItem(Items.WHITE_WOOL)
                .railingItem(Items.OAK_FENCE)
                .stageProgressSound(SoundEvents.WOOD_PLACE)
                .stageSwitchSound(SoundEvents.WOOD_BREAK)
                .sloopVariant(sloopVariantLookup.getOrThrow(SloopVariants.OAK))
                .build());

        context.register(SPRUCE, standardSloopBuilder().particle(getParticle("spruce_planks"))
                .strippedLogItem(Items.STRIPPED_SPRUCE_LOG)
                .deckItem(Items.SPRUCE_PLANKS)
                .sailItem(Items.WHITE_WOOL)
                .railingItem(Items.SPRUCE_FENCE)
                .stageProgressSound(SoundEvents.WOOD_PLACE)
                .stageSwitchSound(SoundEvents.WOOD_BREAK)
                .sloopVariant(sloopVariantLookup.getOrThrow(SloopVariants.SPRUCE))
                .build());

        context.register(BIRCH, standardSloopBuilder().particle(getParticle("birch_planks"))
                .strippedLogItem(Items.STRIPPED_BIRCH_LOG)
                .deckItem(Items.BIRCH_PLANKS)
                .sailItem(Items.WHITE_WOOL)
                .railingItem(Items.BIRCH_FENCE)
                .stageProgressSound(SoundEvents.WOOD_PLACE)
                .stageSwitchSound(SoundEvents.WOOD_BREAK)
                .sloopVariant(sloopVariantLookup.getOrThrow(SloopVariants.BIRCH))
                .build());

        context.register(ACACIA, standardSloopBuilder().particle(getParticle("acacia_planks"))
                .strippedLogItem(Items.STRIPPED_ACACIA_LOG)
                .deckItem(Items.ACACIA_PLANKS)
                .sailItem(Items.WHITE_WOOL)
                .railingItem(Items.ACACIA_FENCE)
                .stageProgressSound(SoundEvents.WOOD_PLACE)
                .stageSwitchSound(SoundEvents.WOOD_BREAK)
                .sloopVariant(sloopVariantLookup.getOrThrow(SloopVariants.ACACIA))
                .build());

        context.register(CHERRY, standardSloopBuilder().particle(getParticle("cherry_planks"))
                .strippedLogItem(Items.STRIPPED_CHERRY_LOG)
                .deckItem(Items.CHERRY_PLANKS)
                .sailItem(Items.WHITE_WOOL)
                .railingItem(Items.CHERRY_FENCE)
                .stageProgressSound(SoundEvents.WOOD_PLACE)
                .stageSwitchSound(SoundEvents.WOOD_BREAK)
                .sloopVariant(sloopVariantLookup.getOrThrow(SloopVariants.CHERRY))
                .build());

        context.register(JUNGLE, standardSloopBuilder().particle(getParticle("jungle_planks"))
                .strippedLogItem(Items.STRIPPED_JUNGLE_LOG)
                .deckItem(Items.JUNGLE_PLANKS)
                .sailItem(Items.WHITE_WOOL)
                .railingItem(Items.JUNGLE_FENCE)
                .stageProgressSound(SoundEvents.WOOD_PLACE)
                .stageSwitchSound(SoundEvents.WOOD_BREAK)
                .sloopVariant(sloopVariantLookup.getOrThrow(SloopVariants.JUNGLE))
                .build());

        context.register(DARK_OAK, standardSloopBuilder().particle(getParticle("dark_oak_planks"))
                .strippedLogItem(Items.STRIPPED_DARK_OAK_LOG)
                .deckItem(Items.DARK_OAK_PLANKS)
                .sailItem(Items.WHITE_WOOL)
                .railingItem(Items.DARK_OAK_FENCE)
                .stageProgressSound(SoundEvents.WOOD_PLACE)
                .stageSwitchSound(SoundEvents.WOOD_BREAK)
                .sloopVariant(sloopVariantLookup.getOrThrow(SloopVariants.DARK_OAK))
                .build());

        context.register(MANGROVE, standardSloopBuilder().particle(getParticle("mangrove_planks"))
                .strippedLogItem(Items.STRIPPED_MANGROVE_LOG)
                .deckItem(Items.MANGROVE_PLANKS)
                .sailItem(Items.WHITE_WOOL)
                .railingItem(Items.MANGROVE_FENCE)
                .stageProgressSound(SoundEvents.WOOD_PLACE)
                .stageSwitchSound(SoundEvents.WOOD_BREAK)
                .sloopVariant(sloopVariantLookup.getOrThrow(SloopVariants.MANGROVE))
                .build());

        context.register(BAMBOO, standardSloopBuilder().particle(getParticle("bamboo_planks"))
                .strippedLogItem(Items.STRIPPED_BAMBOO_BLOCK)
                .deckItem(Items.BAMBOO_PLANKS)
                .sailItem(Items.WHITE_WOOL)
                .railingItem(Items.BAMBOO_FENCE)
                .stageProgressSound(SoundEvents.WOOD_PLACE)
                .stageSwitchSound(SoundEvents.WOOD_BREAK)
                .sloopVariant(sloopVariantLookup.getOrThrow(SloopVariants.BAMBOO))
                .build());
    }

    public static void bootstrapNether(final BootstrapContext<ConstructionInput<SloopUnderConstructionEntity.SloopConstructionStage>> context) {
        final var sloopVariantLookup = context.lookup(AlekiShipsRegistries.SLOOP_VARIANT);
        context.register(CRIMSON, standardSloopBuilder().particle(getParticle("crimson_planks"))
                .strippedLogItem(Items.STRIPPED_CRIMSON_STEM)
                .deckItem(Items.CRIMSON_PLANKS)
                .sailItem(Items.WHITE_WOOL)
                .railingItem(Items.CRIMSON_FENCE)
                .stageProgressSound(SoundEvents.NETHER_WOOD_PLACE)
                .stageSwitchSound(SoundEvents.NETHER_WOOD_BREAK)
                .sloopVariant(sloopVariantLookup.getOrThrow(SloopVariants.CRIMSON))
                .build());

        context.register(WARPED, standardSloopBuilder().particle(getParticle("warped_planks"))
                .strippedLogItem(Items.STRIPPED_WARPED_STEM)
                .deckItem(Items.WARPED_PLANKS)
                .sailItem(Items.WHITE_WOOL)
                .railingItem(Items.WARPED_FENCE)
                .stageProgressSound(SoundEvents.WOOD_PLACE)
                .stageSwitchSound(SoundEvents.WOOD_BREAK)
                .sloopVariant(sloopVariantLookup.getOrThrow(SloopVariants.WARPED))
                .build());
    }

    private static ConstructionParticleOption getParticle(final String texture) {
        return ConstructionParticleOption.of(ResourceLocation.withDefaultNamespace("block/" + texture));
    }

    @SuppressWarnings("unused") // Builder method
    @Builder(builderMethodName = "standardSloopBuilder", builderClassName = "StandardSloopConstructionInputBuilder")
    private static ConstructionInput<SloopUnderConstructionEntity.SloopConstructionStage> constructionSloopInput(final ParticleOptions particle,
            final Item strippedLogItem, final Item deckItem, final Item sailItem, final Item railingItem,
            final SoundEvent stageProgressSound, final SoundEvent stageSwitchSound,
            final Holder<SloopVariant> sloopVariant) {

        return standardSloop(particle, strippedLogItem, deckItem, sailItem, railingItem, stageProgressSound,
                stageSwitchSound).constructedEntity(new SloopResult(sloopVariant))
                .assembleSound(SoundEvents.WOOD_BREAK)
                .assembleParticle(particle)
                .build();
    }
}