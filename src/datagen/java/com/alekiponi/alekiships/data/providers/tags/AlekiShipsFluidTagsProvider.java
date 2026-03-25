package com.alekiponi.alekiships.data.providers.tags;

import mod.traister101.datagenutils.data.tags.EnhancedFluidTagsProvider;

import com.alekiponi.alekiships.AlekiShips;
import com.alekiponi.alekiships.util.AlekiShipsTags;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.world.level.material.Fluids;

import net.neoforged.neoforge.common.data.ExistingFileHelper;

import java.util.concurrent.CompletableFuture;
import org.jetbrains.annotations.Nullable;

public class AlekiShipsFluidTagsProvider extends EnhancedFluidTagsProvider {

    public AlekiShipsFluidTagsProvider(final PackOutput output, final CompletableFuture<HolderLookup.Provider> provider,
            @Nullable final ExistingFileHelper existingFileHelper) {
        super(output, provider, AlekiShips.MOD_ID, existingFileHelper);
    }

    @Override
    protected void addTags(final HolderLookup.Provider provider) {
        this.tag(AlekiShipsTags.Fluids.PAINT_REMOVER).name("Paint Remover").add(Fluids.WATER, Fluids.FLOWING_WATER);
    }
}