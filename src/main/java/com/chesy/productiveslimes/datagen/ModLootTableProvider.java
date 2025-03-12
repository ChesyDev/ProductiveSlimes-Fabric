package com.chesy.productiveslimes.datagen;

import com.chesy.productiveslimes.block.ModBlocks;
import com.chesy.productiveslimes.tier.ModTier;
import com.chesy.productiveslimes.tier.ModTiers;
import com.chesy.productiveslimes.tier.Tier;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricBlockLootTableProvider;
import net.minecraft.registry.RegistryWrapper;

import java.util.concurrent.CompletableFuture;

public class ModLootTableProvider extends FabricBlockLootTableProvider {
    public ModLootTableProvider(FabricDataOutput dataOutput, CompletableFuture<RegistryWrapper.WrapperLookup> registryLookup) {
        super(dataOutput, registryLookup);
    }

    @Override
    public void generate() {
        addDrop(ModBlocks.MELTING_STATION);
        addDrop(ModBlocks.SLIME_SQUEEZER);
        addDrop(ModBlocks.ENERGY_GENERATOR);
        addDrop(ModBlocks.CABLE);
        addDrop(ModBlocks.PIPE);
        addDrop(ModBlocks.DNA_EXTRACTOR);
        addDrop(ModBlocks.DNA_SYNTHESIZER);
        addDrop(ModBlocks.FLUID_TANK);
        addDrop(ModBlocks.SLIME_SQUEEZER);
        addDrop(ModBlocks.SLIME_NEST);
        addDrop(ModBlocks.SLIMEBALL_COLLECTOR);
        addDrop(ModBlocks.SLIMY_PORTAL_FRAME);

        addDrop(ModBlocks.SLIMY_GRASS_BLOCK, block -> drops(block, ModBlocks.SLIMY_DIRT));
        addDrop(ModBlocks.SLIMY_DIRT);
        addDrop(ModBlocks.SLIMY_STONE, block -> drops(block, ModBlocks.SLIMY_COBBLESTONE));
        addDrop(ModBlocks.SLIMY_DEEPSLATE, block -> drops(block, ModBlocks.SLIMY_COBBLED_DEEPSLATE));
        addDrop(ModBlocks.SLIMY_COBBLESTONE);
        addDrop(ModBlocks.SLIMY_COBBLED_DEEPSLATE);

        addDrop(ModBlocks.SLIMY_LOG);
        addDrop(ModBlocks.SLIMY_WOOD);
        addDrop(ModBlocks.STRIPPED_SLIMY_LOG);
        addDrop(ModBlocks.STRIPPED_SLIMY_WOOD);
        addDrop(ModBlocks.SLIMY_PLANKS);
        addDrop(ModBlocks.SLIMY_SAPLING);
        addDrop(ModBlocks.SLIMY_LEAVES, block -> leavesDrops(block, ModBlocks.SLIMY_SAPLING, SAPLING_DROP_CHANCE));
        addDrop(ModBlocks.SLIMY_STAIRS);
        addDrop(ModBlocks.SLIMY_SLAB, block -> slabDrops(ModBlocks.SLIMY_SLAB));
        addDrop(ModBlocks.SLIMY_PRESSURE_PLATE);
        addDrop(ModBlocks.SLIMY_BUTTON);
        addDrop(ModBlocks.SLIMY_FENCE);
        addDrop(ModBlocks.SLIMY_FENCE_GATE);
        addDrop(ModBlocks.SLIMY_TRAPDOOR);
        addDrop(ModBlocks.SLIMY_DOOR, block -> doorDrops(ModBlocks.SLIMY_DOOR));
        addDrop(ModBlocks.SLIMY_STONE_STAIRS);
        addDrop(ModBlocks.SLIMY_STONE_SLAB, block -> slabDrops(ModBlocks.SLIMY_STONE_SLAB));
        addDrop(ModBlocks.SLIMY_STONE_PRESSURE_PLATE);
        addDrop(ModBlocks.SLIMY_STONE_BUTTON);
        addDrop(ModBlocks.SLIMY_COBBLESTONE_STAIRS);
        addDrop(ModBlocks.SLIMY_COBBLESTONE_SLAB, block -> slabDrops(ModBlocks.SLIMY_COBBLESTONE_SLAB));
        addDrop(ModBlocks.SLIMY_COBBLESTONE_WALL);
        addDrop(ModBlocks.SLIMY_COBBLED_DEEPSLATE_STAIRS);
        addDrop(ModBlocks.SLIMY_COBBLED_DEEPSLATE_SLAB, block -> slabDrops(ModBlocks.SLIMY_COBBLED_DEEPSLATE_SLAB));
        addDrop(ModBlocks.SLIMY_COBBLED_DEEPSLATE_WALL);

        addDrop(ModBlocks.ENERGY_SLIME_BLOCK);

        for (Tier tier : Tier.values()){
            ModTier tiers = ModTiers.getTierByName(tier);
            addDrop(ModTiers.getBlockByName(tiers.name()));
        }
    }
}
