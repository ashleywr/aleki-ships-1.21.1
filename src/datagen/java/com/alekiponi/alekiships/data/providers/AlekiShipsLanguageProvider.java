package com.alekiponi.alekiships.data.providers;

import mod.traister101.datagenutils.data.EnhancedLanguageProvider;
import mod.traister101.datagenutils.data.language.*;
import mod.traister101.datagenutils.data.util.LanguageTranslation;
import mod.traister101.datagenutils.data.waila.JadeLanguageSubProvider;

import com.alekiponi.alekiships.AlekiShips;
import com.alekiponi.alekiships.client.IngameOverlays;
import com.alekiponi.alekiships.commands.server.EntityMultiblockCommands;
import com.alekiponi.alekiships.common.AlekiShipsRegistries;
import com.alekiponi.alekiships.common.block.AlekiShipsBlocks;
import com.alekiponi.alekiships.common.entity.AlekiShipsEntities;
import com.alekiponi.alekiships.common.entity.vehicle.ConstructionSloopVariant;
import com.alekiponi.alekiships.common.item.AlekiShipsItems;
import com.alekiponi.alekiships.common.item.AlekiShipsTabs;
import com.alekiponi.alekiships.common.recipe.AlekiShipsRecipeTypes;
import com.alekiponi.alekiships.common.recipe.EntityMultiblockRecipe;
import com.alekiponi.alekiships.common.sounds.AlekiShipsJukeboxSongs;
import com.alekiponi.alekiships.compat.jei.JeiIntegration;
import com.alekiponi.alekiships.compat.jei.category.VehicleRepairMaterialCategory;
import com.alekiponi.alekiships.compat.waila.FrameBlockProvider;
import com.alekiponi.alekiships.compat.waila.compartment.*;
import com.alekiponi.alekiships.compat.waila.compartment.vehicle.ConstructionEntityProvider;
import com.alekiponi.alekiships.compat.waila.compartment.vehicle.VehicleEntityDamageProvider;
import com.alekiponi.alekiships.config.AlekiShipsConfig;
import com.alekiponi.alekiships.config.ClientConfig;
import com.alekiponi.alekiships.util.*;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.Registry;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.JukeboxSong;
import net.minecraft.world.level.block.Block;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.stream.Stream;

public class AlekiShipsLanguageProvider extends EnhancedLanguageProvider {

    public AlekiShipsLanguageProvider(final PackOutput output,
            final CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries, AlekiShips.MOD_ID, "en_us",
                List.of(new Items(), new Blocks(), new CreativeTabs(), new Entities(), new Jade(), new Config(),
                        new JukeboxSongLanguageProvider(AlekiShips.MOD_ID) {
                            @Override
                            protected void addTranslations(final LanguageOutput<ResourceKey<JukeboxSong>> output) {
                                output.add(AlekiShipsJukeboxSongs.PIRATE_CRAFTING, "Captain Thrack - Pirate Crafting");
                            }
                        }, simpleWoodLanguage(AlekiShipsRegistries.BOAT_MATERIAL, BoatMaterial::getDescriptionId,
                                Wood::boatMaterialKey),
                        simpleWoodLanguage(AlekiShipsRegistries.CONSTRUCTION_SLOOP_VARIANT,
                                ConstructionSloopVariant::getDescriptionId, Wood::sloopConstructionKey),
                        simpleWoodLanguage(AlekiShipsRegistries.FRAME_MATERIAL, FrameMaterial::getDescriptionId,
                                Wood::frameMaterialKey)));
    }

    private static <T> DynamicRegistryLanguageSubProvider<T> simpleWoodLanguage(
            final ResourceKey<Registry<T>> registryKey, final Function<ResourceLocation, String> keyFunction,
            final Function<Wood, ResourceKey<T>> resourceKey) {
        return new DynamicRegistryLanguageSubProvider<>(registryKey, keyFunction, AlekiShips.MOD_ID) {
            @Override
            protected void addTranslations(final LanguageOutput<ResourceKey<T>> output) {
                Stream.concat(Arrays.stream(OverworldWood.values()), Arrays.stream(NetherWood.values()))
                        .map(resourceKey)
                        .forEach(output::simple);
            }
        };
    }

    @Override
    protected void addTranslations() {
        this.add(IngameOverlays.EJECT_PASSENGERS_KEY, "Press %s + %s to eject");
        this.add(IngameOverlays.DEBUG_KILOMETERS_PER_HOUR_KEY, "%skm/h");
        this.add(IngameOverlays.DEBUG_MILES_PER_HOUR_KEY, "%smi/h");
        this.add(EntityMultiblockRecipe.SUCCESSFULLY_ASSEMBLED, "Successfully assembled %s");
        this.add(EntityMultiblockRecipe.FAILED_TO_ASEMBLE, "Failed to asemble %s");
        this.add(EntityMultiblockCommands.NOT_ENTITY_MULTIBLOCK_RECIPE,
                "%s is not a " + AlekiShipsRecipeTypes.ENTITY_MULTIBLOCK_RECIPE.getId());
        this.add(EntityMultiblockCommands.NO_MATCHING_STATES, "No matching states for %s");

        this.simpleEnumTranslations(ClientConfig.RudderSchemes.values());
        this.simpleEnumTranslations(IngameOverlays.DebugDisplayUnits.values());

        this.addTranslationsForJei();
    }

    private void simpleEnumTranslations(final Enum<?>[] values) {
        for (final var value : values) {
            this.add(CommonHelper.getEnumLanguageKey(value),
                    LanguageTranslation.langify(value.name().toLowerCase(Locale.ROOT)));
        }
    }

    private void addTranslationsForJei() {
        this.add(JeiIntegration.CAN_PLACE_INTO_COMPARTMENTS_KEY, "Can be placed into compartments");
        this.add(JeiIntegration.CAN_BE_USED_TO_DYE_SHIPS_SAILS_KEY, "Can be used to dye ships & sails");
        this.add(VehicleRepairMaterialCategory.REPAIR_AMOUNT_PERCENT_KEY, "Repair Amount: %s%%");
        this.add(VehicleRepairMaterialCategory.REPAIR_AMOUNT_ABSOLUTE_KEY, "Repair Amount: %s");
        this.add(VehicleRepairMaterialCategory.MINIMUM_DAMAGE_PERCENT_KEY, "Minimum Damage: %s%%");
        this.add(VehicleRepairMaterialCategory.MINIMUM_DAMAGE_ABSOLUTE_KEY, "Minimum Damage: %s");
        this.add(VehicleRepairMaterialCategory.MAXIMUM_DAMAGE_PERCENT_KEY, "Maximum Damage: %s%%");
        this.add(VehicleRepairMaterialCategory.MAXIMUM_DAMAGE_ABSOLUTE_KEY, "Maximum Damage: %s");
    }

    static class Config extends ConfigLanguageProvider {
        public Config() {super(AlekiShips.MOD_ID);}

        @Override
        protected void addTranslations() {
            this.add(AlekiShipsConfig.CLIENT.rudderControlScheme, "Tiller Control Scheme");
            this.add(AlekiShipsConfig.CLIENT.debugDisplayUnits, "Debug Display Units");
            this.add(AlekiShipsConfig.SERVER.windAffectsBoatsWithNoAnchor, "Wind Affects Boats With No Anchor");
            this.add(AlekiShipsConfig.SERVER.eagerEntityMultiblockValidation, "Eager Entity Multiblock Validation");
        }
    }

    static class Blocks extends BlockLanguageProvider {
        public Blocks() {super(AlekiShipsBlocks.BLOCKS);}

        @Override
        protected void addTranslations(final LanguageOutput<Block> output) {
            output.add(AlekiShipsBlocks.WOODEN_BOAT_FRAME_ANGLED, "Wooden Sloped Shipwright's Scaffolding");
            output.add(AlekiShipsBlocks.WOODEN_BOAT_FRAME_FLAT, "Wooden Flat Shipwright's Scaffolding");
            output.add(AlekiShipsBlocks.BOAT_FRAME_ANGLED, "Sloped Shipwright's Scaffolding");
            output.add(AlekiShipsBlocks.BOAT_FRAME_FLAT, "Flat Shipwright's Scaffolding");
            output.simple(AlekiShipsBlocks.OARLOCK);
            output.simple(AlekiShipsBlocks.CLEAT);
        }
    }

    static class Items extends ItemLanguageProvider {
        public Items() {super(AlekiShipsItems.ITEMS);}

        @Override
        protected void addTranslations(final LanguageOutput<Item> output) {
            output.simple(AlekiShipsItems.CANNON);
            output.simple(AlekiShipsItems.CANNONBALL);
            output.simple(AlekiShipsItems.ANCHOR);
            output.add(AlekiShipsItems.SLOOP_ICON_ONLY, "Sloop (ICON ONLY)");
            output.add(AlekiShipsItems.ROWBOAT_ICON_ONLY, "Rowboat (ICON ONLY)");
            output.simple(AlekiShipsItems.OAR);
            output.add(AlekiShipsItems.MUSIC_DISC_PIRATE_CRAFTING, "Music Disc");
        }
    }

    static class CreativeTabs extends CreativeTabLanguageProvider {
        protected CreativeTabs() {
            super(AlekiShipsTabs.CREATIVE_MODE_TABS,
                    CreativeTabLanguageProvider.languageKey(id -> "creativetab." + id.getPath()));
        }

        @Override
        protected void addTranslations(final LanguageOutput<CreativeModeTab> output) {
            output.add(AlekiShipsTabs.ALEKISHIPS_TAB, "aleki's Nifty Ships");
        }
    }

    static class Entities extends EntityLanguageProvider {
        public Entities() {super(AlekiShipsEntities.ENTITY_TYPES);}

        @Override
        protected void addTranslations(final LanguageOutput<EntityType<?>> output) {
            // Vehicles
            output.add(AlekiShipsEntities.ROWBOAT, "%s Rowboat");
            output.add(AlekiShipsEntities.SLOOP, "%s Sloop");
            output.add(AlekiShipsEntities.CONSTRUCTION_SLOOP, "%s Construction Sloop");

            // Misc
            output.add(AlekiShipsEntities.VEHICLE_PART, "Vehicle Part");
            output.add(AlekiShipsEntities.VEHICLE_CLEAT_ENTITY, "Cleat");
            output.add(AlekiShipsEntities.WINDLASS_SWITCH_ENTITY, "Windlass");
            output.add(AlekiShipsEntities.SAIL_SWITCH_ENTITY, "Sail");
            output.add(AlekiShipsEntities.VEHICLE_COLLIDER_ENTITY, "Vehicle");
            output.add(AlekiShipsEntities.CONSTRUCTION_ENTITY, "Construction Entity");
            output.add(AlekiShipsEntities.MAST_ENTITY, "Mast");
            output.add(AlekiShipsEntities.ANCHOR_ENTITY, "Anchor");

            output.simple(AlekiShipsEntities.CANNONBALL_ENTITY);
            output.simple(AlekiShipsEntities.CANNON_ENTITY);

            // Compartments
            output.add(AlekiShipsEntities.EMPTY_COMPARTMENT_ENTITY, "Empty Compartment");
            output.add(AlekiShipsEntities.BLOCK_COMPARTMENT_ENTITY, "Block Compartment");
            output.add(AlekiShipsEntities.BARREL_COMPARTMENT_ENTITY, "Barrel Compartment");
            output.add(AlekiShipsEntities.CHEST_COMPARTMENT_ENTITY, "Chest Compartment");
            output.add(AlekiShipsEntities.ENDER_CHEST_COMPARTMENT_ENTITY, "Ender Chest Compartment");
            output.add(AlekiShipsEntities.SHULKER_BOX_COMPARTMENT_ENTITY, "Shulker Box Compartment");
            output.add(AlekiShipsEntities.FURNACE_COMPARTMENT_ENTITY, "Furnace Compartment");
            output.add(AlekiShipsEntities.BLAST_FURNACE_COMPARTMENT_ENTITY, "Blast Furnace Compartment");
            output.add(AlekiShipsEntities.SMOKER_COMPARTMENT_ENTITY, "Smoker Compartment");
            output.add(AlekiShipsEntities.BREWING_STAND_COMPARTMENT_ENTITY, "Brewing Stand Compartment");
            output.add(AlekiShipsEntities.WORKBENCH_COMPARTMENT_ENTITY, "Workbench Compartment");
            output.add(AlekiShipsEntities.STONECUTTER_COMPARTMENT_ENTITY, "Stonecutter Compartment");
            output.add(AlekiShipsEntities.CARTOGRAPHY_TABLE_COMPARTMENT_ENTITY, "Cartography Table Compartment");
            output.add(AlekiShipsEntities.SMITHING_TABLE_COMPARTMENT_ENTITY, "Smithing Table Compartment");
            output.add(AlekiShipsEntities.GRINDSTONE_COMPARTMENT_ENTITY, "Grindstone Compartment");
            output.add(AlekiShipsEntities.LOOM_COMPARTMENT_ENTITY, "Loom Compartment");
            output.add(AlekiShipsEntities.NOTE_BLOCK_COMPARTMENT_ENTITY, "Note Block Compartment");
            output.add(AlekiShipsEntities.JUKEBOX_COMPARTMENT_ENTITY, "Jukebox Compartment");
        }
    }

    static class Jade extends JadeLanguageSubProvider {
        protected Jade() {super(AlekiShips.MOD_ID);}

        @Override
        protected void addTranslations() {
            this.provider(JukeboxCompartmentProvider.INSTANCE).name("Jukebox Compartment");
            this.provider(NoteBlockCompartmentProvider.INSTANCE).name("Note Block Compartment");
            this.provider(FurnaceCompartmentProvider.INSTANCE).name("Furnace Compartment");
            this.provider(BrewingStandCompartmentProvider.INSTANCE).name("Brewing Stand Compartment");
            this.provider(BlockCompartmentProvider.INSTANCE).name("Block Compartment");
            this.provider(ConstructionEntityProvider.INSTANCE).name("Construction Entity");
            this.provider(VehicleEntityDamageProvider.INSTANCE).name("Vehicle Entity Damage");
            this.config(FrameBlockProvider.FLAT.getUid()).name("Frame Block");
            this.config(VehicleEntityDamageProvider.DECIMAL_PLACES).name("Decimal Places");
            this.enumConfig(VehicleEntityDamageProvider.DISPLAY_TYPE, VehicleEntityDamageProvider.DisplayType::values)
                    .name("Display Type")
                    .defineValues((displayType, valueBuilder) -> {
                        switch (displayType) {
                            case DAMAGE -> valueBuilder.name("Damage")
                                    .description(
                                            "Display the damage done to vehicles as a percentage of overall health");
                            case DAMAGE_ABSOLUTE -> valueBuilder.name("Damage Absolute")
                                    .description(
                                            "Display the damage done to vehicles as an absolute value including the total damage that can be done");
                            case HEALTH -> valueBuilder.name("Health")
                                    .description("Display the health of vehicles as a percentage");
                            case HEALTH_ABSOLUTE -> valueBuilder.name("Health Absolute")
                                    .description(
                                            "Display the health of vehicles as an absolute value including the total health");
                        }
                    });

            this.add(BlockCompartmentProvider.COMPARTMENT_BLOCK_KEY, "%s Compartment");
            this.add(ConstructionEntityProvider.INPUTS_REMAINING_KEY, "Inputs remaining: %s");
            this.add(ConstructionEntityProvider.CURRENT_STAGE_KEY, "Current Stage: %s");
            this.add(ConstructionEntityProvider.NEXT_STAGE_KEY, "Next Stage: %s");
            this.add(VehicleEntityDamageProvider.DAMAGE_KEY, "Damage: %s%%");
            this.add(VehicleEntityDamageProvider.DAMAGE_ABSOLUTE_KEY, "Damage: %s Total: %s");
            this.add(VehicleEntityDamageProvider.HEALTH_KEY, "Health: %s%%");
            this.add(VehicleEntityDamageProvider.HEALTH_ABSOLUTE_KEY, "Health: %s Total: %s");
            this.add(VehicleEntityDamageProvider.WRECKED_KEY, "Wrecked!");
            this.add(FrameBlockProvider.ANGLED.key, "%s Sloped Shipwright's Scaffolding");
            this.add(FrameBlockProvider.FLAT.key, "%s Flat Shipwright's Scaffolding");
        }
    }
}