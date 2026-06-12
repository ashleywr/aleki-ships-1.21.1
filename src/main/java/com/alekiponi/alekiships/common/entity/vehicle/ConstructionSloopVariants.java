package com.alekiponi.alekiships.common.entity.vehicle;

import com.alekiponi.alekiships.AlekiShips;
import com.alekiponi.alekiships.common.AlekiShipsRegistries;
import com.alekiponi.alekiships.common.block.AlekiShipsBlocks;
import com.alekiponi.alekiships.common.entity.SloopConstructionInputs;

import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;

public final class ConstructionSloopVariants {

    public static final ResourceKey<ConstructionSloopVariant> OAK = createKey("oak");
    public static final ResourceKey<ConstructionSloopVariant> SPRUCE = createKey("spruce");
    public static final ResourceKey<ConstructionSloopVariant> BIRCH = createKey("birch");
    public static final ResourceKey<ConstructionSloopVariant> ACACIA = createKey("acacia");
    public static final ResourceKey<ConstructionSloopVariant> CHERRY = createKey("cherry");
    public static final ResourceKey<ConstructionSloopVariant> JUNGLE = createKey("jungle");
    public static final ResourceKey<ConstructionSloopVariant> DARK_OAK = createKey("dark_oak");
    public static final ResourceKey<ConstructionSloopVariant> CRIMSON = createKey("crimson");
    public static final ResourceKey<ConstructionSloopVariant> WARPED = createKey("warped");
    public static final ResourceKey<ConstructionSloopVariant> MANGROVE = createKey("mangrove");
    public static final ResourceKey<ConstructionSloopVariant> BAMBOO = createKey("bamboo");
    public static final ResourceKey<ConstructionSloopVariant> DEFAULT = OAK;

    private static ResourceKey<ConstructionSloopVariant> createKey(final String name) {
        return ResourceKey.create(AlekiShipsRegistries.CONSTRUCTION_SLOOP_VARIANT, AlekiShips.location(name));
    }

    public static void bootstrapOverworld(final BootstrapContext<ConstructionSloopVariant> context) {
        final var constructionInputLookup = context.lookup(AlekiShipsRegistries.CONSTRUCTION_SLOOP_INPUT);

        context.register(OAK, ConstructionSloopVariant.builder()
                .texture(getTexture("oak"))
                .constructionInput(constructionInputLookup.getOrThrow(SloopConstructionInputs.OAK))
                .name(getName("oak"))
                .lootTable(Loot.OAK)
                .build());

        context.register(SPRUCE, ConstructionSloopVariant.builder()
                .texture(getTexture("spruce"))
                .constructionInput(constructionInputLookup.getOrThrow(SloopConstructionInputs.SPRUCE))
                .name(getName("spruce"))
                .lootTable(Loot.SPRUCE)
                .build());

        context.register(BIRCH, ConstructionSloopVariant.builder()
                .texture(getTexture("birch"))
                .constructionInput(constructionInputLookup.getOrThrow(SloopConstructionInputs.BIRCH))
                .name(getName("birch"))
                .lootTable(Loot.BIRCH)
                .build());

        context.register(ACACIA, ConstructionSloopVariant.builder()
                .texture(getTexture("acacia"))
                .constructionInput(constructionInputLookup.getOrThrow(SloopConstructionInputs.ACACIA))
                .name(getName("acacia"))
                .lootTable(Loot.ACACIA)
                .build());

        context.register(CHERRY, ConstructionSloopVariant.builder()
                .texture(getTexture("cherry"))
                .constructionInput(constructionInputLookup.getOrThrow(SloopConstructionInputs.CHERRY))
                .name(getName("cherry"))
                .lootTable(Loot.CHERRY)
                .build());

        context.register(JUNGLE, ConstructionSloopVariant.builder()
                .texture(getTexture("jungle"))
                .constructionInput(constructionInputLookup.getOrThrow(SloopConstructionInputs.JUNGLE))
                .name(getName("jungle"))
                .lootTable(Loot.JUNGLE)
                .build());

        context.register(DARK_OAK, ConstructionSloopVariant.builder()
                .texture(getTexture("dark_oak"))
                .constructionInput(constructionInputLookup.getOrThrow(SloopConstructionInputs.DARK_OAK))
                .name(getName("dark_oak"))
                .lootTable(Loot.DARK_OAK)
                .build());

        context.register(MANGROVE, ConstructionSloopVariant.builder()
                .texture(getTexture("mangrove"))
                .constructionInput(constructionInputLookup.getOrThrow(SloopConstructionInputs.MANGROVE))
                .name(getName("mangrove"))
                .lootTable(Loot.MANGROVE)
                .build());

        context.register(BAMBOO, ConstructionSloopVariant.builder()
                .texture(getTexture("bamboo"))
                .constructionInput(constructionInputLookup.getOrThrow(SloopConstructionInputs.BAMBOO))
                .name(getName("bamboo"))
                .lootTable(Loot.BAMBOO)
                .build());
    }

    public static void bootstrapNether(final BootstrapContext<ConstructionSloopVariant> context) {
        final var constructionInputLookup = context.lookup(AlekiShipsRegistries.CONSTRUCTION_SLOOP_INPUT);

        context.register(CRIMSON, ConstructionSloopVariant.builder()
                .texture(getTexture("crimson"))
                .constructionInput(constructionInputLookup.getOrThrow(SloopConstructionInputs.CRIMSON))
                .name(getName("crimson"))
                .lootTable(Loot.CRIMSON)
                .build());

        context.register(WARPED, ConstructionSloopVariant.builder()
                .texture(getTexture("warped"))
                .constructionInput(constructionInputLookup.getOrThrow(SloopConstructionInputs.WARPED))
                .name(getName("warped"))
                .lootTable(Loot.WARPED)
                .build());
    }

    private static ResourceLocation getTexture(final String wood) {
        return AlekiShips.location("entity/watercraft/sloop_construction/" + wood);
    }

    private static Component getName(final String name) {
        return Component.translatable(ConstructionSloopVariant.getDescriptionId(AlekiShips.location(name)));
    }

    public static final class Loot {
        public static final ResourceKey<LootTable> OAK = createKey("oak");
        public static final ResourceKey<LootTable> SPRUCE = createKey("spruce");
        public static final ResourceKey<LootTable> BIRCH = createKey("birch");
        public static final ResourceKey<LootTable> ACACIA = createKey("acacia");
        public static final ResourceKey<LootTable> CHERRY = createKey("cherry");
        public static final ResourceKey<LootTable> JUNGLE = createKey("jungle");
        public static final ResourceKey<LootTable> DARK_OAK = createKey("dark_oak");
        public static final ResourceKey<LootTable> CRIMSON = createKey("crimson");
        public static final ResourceKey<LootTable> WARPED = createKey("warped");
        public static final ResourceKey<LootTable> MANGROVE = createKey("mangrove");
        public static final ResourceKey<LootTable> BAMBOO = createKey("bamboo");

        private static ResourceKey<LootTable> createKey(final String name) {
            return ResourceKey.create(Registries.LOOT_TABLE,
                    AlekiShips.location("entities/construction/sloop/" + name));
        }

        public static LootTable.Builder createConstructionSloopLootTable(final ItemLike angledBoatFrame,
                final ItemLike flatBoatFrame, final ItemLike plankItem) {
            final var builder = LootTable.lootTable();

            builder.withPool(LootPool.lootPool()
                    .add(LootItem.lootTableItem(angledBoatFrame)
                            .apply(SetItemCountFunction.setCount(ConstantValue.exactly(18))))
                    .add(LootItem.lootTableItem(flatBoatFrame)
                            .apply(SetItemCountFunction.setCount(ConstantValue.exactly(6))))
                    .add(LootItem.lootTableItem(plankItem)
                            .apply(SetItemCountFunction.setCount(
                                    ConstantValue.exactly(SloopVariants.TOTAL_STRUCTURE_PLANKS))))
                    .add(LootItem.lootTableItem(AlekiShipsBlocks.CLEAT)
                            .apply(SetItemCountFunction.setCount(ConstantValue.exactly(4)))));

            return builder;
        }
    }
}