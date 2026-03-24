package com.alekiponi.alekiships.data.providers;

import mod.traister101.datagenutils.data.EnhancedAdvancementProvider;

import com.alekiponi.alekiships.data.advancements.AlekiShipsAdvancementGenerator;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;

import net.neoforged.neoforge.common.data.ExistingFileHelper;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public final class AlekiShipsAdvancementsProvider {

    public static EnhancedAdvancementProvider create(final PackOutput packOutput,
            final CompletableFuture<HolderLookup.Provider> registries, final ExistingFileHelper existingFileHelper) {
        return new EnhancedAdvancementProvider(packOutput, registries, existingFileHelper,
                List.of(new AlekiShipsAdvancementGenerator()));
    }
}