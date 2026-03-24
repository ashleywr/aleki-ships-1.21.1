package com.alekiponi.alekiships.data.advancements;

import mod.traister101.datagenutils.data.AdvancementSubProvider;
import mod.traister101.datagenutils.data.util.AdvancementBuilder;
import mod.traister101.datagenutils.data.util.AdvancementOutput;
import mod.traister101.datagenutils.data.util.SimpleDisplayInfo;

import com.alekiponi.alekiships.AlekiShips;
import com.alekiponi.alekiships.common.item.AlekiShipsItems;
import com.alekiponi.alekiships.util.advancements.AlekiShipsAdvancements;

import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.core.HolderLookup;
import net.minecraft.world.item.Items;

import static net.minecraft.data.advancements.AdvancementSubProvider.createPlaceholder;

public class AlekiShipsAdvancementGenerator implements AdvancementSubProvider {

    private static final AdvancementHolder SMELT_IRON = createPlaceholder("story/smelt_iron");

    @Override
    public void generate(final AdvancementOutput output, final HolderLookup.Provider registries) {
        AdvancementBuilder.childOf(SMELT_IRON)
                .display(SimpleDisplayInfo.builder()
                        .icon(AlekiShipsItems.CANNON)
                        .title("Incoming Cannon Event")
                        .description("Craft a cannon")
                        .task()
                        .toast()
                        .announce())
                .hasItems("has_cannon", AlekiShipsItems.CANNON)
                .save(output, AlekiShips.location("cannon"));

        final var rowboatCompleted = AdvancementBuilder.childOf(SMELT_IRON)
                .display(SimpleDisplayInfo.builder()
                        .icon(AlekiShipsItems.ROWBOAT_ICON_ONLY)
                        .title("The Montlake Cut")
                        .description("Build a Rowboat")
                        .task()
                        .toast()
                        .announce())
                .addCriterion("rowboat", AlekiShipsAdvancements.ROWBOAT_COMPLETED.criterion())
                .save(output, AlekiShips.location("rowboat_completed"));

        AdvancementBuilder.childOf(rowboatCompleted)
                .display(SimpleDisplayInfo.builder()
                        .icon(Items.BARREL)
                        .title("Lost at Sea")
                        .description("Ride a barrel")
                        .task()
                        .toast()
                        .announce()
                        .hidden())
                .addCriterion("barrel", AlekiShipsAdvancements.RIDE_BARREL.criterion())
                .save(output, AlekiShips.location("ride_barrel"));

        AdvancementBuilder.childOf(rowboatCompleted)
                .display(SimpleDisplayInfo.builder()
                        .icon(AlekiShipsItems.CANNONBALL)
                        .title("Master and Commander")
                        .description("Fire a new broadside")
                        .challenge()
                        .toast()
                        .announce()
                        .hidden())
                .addCriterion("broadside", AlekiShipsAdvancements.FULL_BROADSIDE.criterion())
                .save(output, AlekiShips.location("full_broadside"));

        AdvancementBuilder.childOf(rowboatCompleted)
                .display(SimpleDisplayInfo.builder()
                        .icon(Items.ARMOR_STAND)
                        .title("Wilson!!!!!!!")
                        .description("Place an inanimate companion on a boat")
                        .challenge()
                        .toast()
                        .announce()
                        .hidden())
                .addCriterion("armorstand", AlekiShipsAdvancements.ARMOR_STAND_ON_BOAT.criterion())
                .save(output, AlekiShips.location("armor_stand_on_boat"));

        final var sloopCompleted = AdvancementBuilder.childOf(rowboatCompleted)
                .display(SimpleDisplayInfo.builder()
                        .icon(AlekiShipsItems.SLOOP_ICON_ONLY)
                        .title("Yesler's Wharf")
                        .description("Build a Sloop")
                        .task()
                        .toast()
                        .announce())
                .addCriterion("sloop", AlekiShipsAdvancements.SLOOP_COMPLETED.criterion())
                .save(output, AlekiShips.location("sloop_completed"));

        AdvancementBuilder.childOf(sloopCompleted)
                .display(SimpleDisplayInfo.builder()
                        .icon(Items.SKELETON_SKULL)
                        .title("The best pirate I've ever seen")
                        .description("Name a black ship The Black Pearl")
                        .challenge()
                        .toast()
                        .announce()
                        .hidden())
                .addCriterion("dye", AlekiShipsAdvancements.DYE_SHIP_BLACK.criterion())
                .save(output, AlekiShips.location("dye_ship_black"));
    }
}