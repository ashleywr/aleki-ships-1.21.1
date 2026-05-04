package com.alekiponi.alekiships.common.item;

import com.alekiponi.alekiships.AlekiShips;
import com.alekiponi.alekiships.common.block.AlekiShipsBlocks;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;

import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public final class AlekiShipsTabs {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(
            Registries.CREATIVE_MODE_TAB, AlekiShips.MOD_ID);

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> ALEKISHIPS_TAB = register("alekiships_tab",
            AlekiShipsItems.ANCHOR::toStack, (parameters, output) -> {
                output.accept(AlekiShipsItems.OAR.get());
                output.accept(AlekiShipsBlocks.OARLOCK.get());
                output.accept(AlekiShipsBlocks.CLEAT.get());
                output.accept(AlekiShipsItems.ANCHOR.get());

                output.accept(AlekiShipsItems.CANNON.get());
                output.accept(AlekiShipsItems.CANNONBALL.get());

                output.accept(AlekiShipsBlocks.BOAT_FRAME_ANGLED.get());
                output.accept(AlekiShipsBlocks.BOAT_FRAME_FLAT.get());

                output.accept(AlekiShipsItems.MUSIC_DISC_PIRATE_CRAFTING.get());
            });

    private static DeferredHolder<CreativeModeTab, CreativeModeTab> register(final String name,
            final Supplier<ItemStack> icon, final CreativeModeTab.DisplayItemsGenerator displayItems) {
        return CREATIVE_MODE_TABS.register(name, registryName -> CreativeModeTab.builder()
                .icon(icon)
                .title(Component.translatable(registryName.toLanguageKey("itemGroup")))
                .displayItems(displayItems)
                .build());
    }
}