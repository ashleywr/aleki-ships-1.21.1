package com.alekiponi.alekiships.data.providers.tags;

import mod.traister101.datagenutils.data.tags.EnhancedBlockTagsProvider;

import com.alekiponi.alekiships.AlekiShips;
import com.alekiponi.alekiships.common.block.AlekiShipsBlocks;
import com.alekiponi.alekiships.util.AlekiShipsTags;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Blocks;

import net.neoforged.neoforge.common.data.ExistingFileHelper;

import java.util.concurrent.CompletableFuture;
import org.jetbrains.annotations.Nullable;

public class AlekiShipsBlockTagsProvider extends EnhancedBlockTagsProvider {

    public AlekiShipsBlockTagsProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider,
            @Nullable ExistingFileHelper existingFileHelper) {
        super(output, lookupProvider, AlekiShips.MOD_ID, existingFileHelper);
    }

    @Override
    protected void addTags(final HolderLookup.Provider provider) {
        this.tag(AlekiShipsTags.Blocks.PLANTS_THAT_GET_MOWED)
                .name("Plants That Get Mowed")
                .add(Blocks.KELP)
                .add(Blocks.LILY_PAD);

        //Ice types
        this.tag(AlekiShipsTags.Blocks.BREAKABLE_ICE)
                .name("Breakable Ice")
                .add(Blocks.ICE)
                .add(Blocks.FROSTED_ICE)
                .add(Blocks.PACKED_ICE)
                .add(Blocks.BLUE_ICE);

        // Vanilla mining tags
        this.tag(BlockTags.MINEABLE_WITH_AXE)
                .add(AlekiShipsBlocks.WOODEN_BOAT_FRAME_ANGLED.get(), AlekiShipsBlocks.WOODEN_BOAT_FRAME_FLAT.get());

        this.tag(BlockTags.MINEABLE_WITH_PICKAXE).add(AlekiShipsBlocks.OARLOCK.get(), AlekiShipsBlocks.CLEAT.get());
    }
}