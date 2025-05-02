package com.chesy.productiveslimes.datagen;

import com.chesy.productiveslimes.block.ModBlocks;
import com.chesy.productiveslimes.util.ModTags;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.tag.BlockTags;

import java.util.concurrent.CompletableFuture;

public class ModBlockTagProvider extends FabricTagProvider.BlockTagProvider {
    public ModBlockTagProvider(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture) {
        super(output, registriesFuture);
    }

    @Override
    protected void configure(RegistryWrapper.WrapperLookup wrapperLookup) {
        getTagBuilder(BlockTags.DIRT)
                .add(ModBlocks.SLIMY_GRASS_BLOCK.getRegistryEntry().registryKey().getValue())
                .add(ModBlocks.SLIMY_DIRT.getRegistryEntry().registryKey().getValue());

        getTagBuilder(BlockTags.STONE_ORE_REPLACEABLES)
                .add(ModBlocks.SLIMY_STONE.getRegistryEntry().registryKey().getValue());

        getTagBuilder(BlockTags.DEEPSLATE_ORE_REPLACEABLES)
                .add(ModBlocks.SLIMY_DEEPSLATE.getRegistryEntry().registryKey().getValue());

        getTagBuilder(BlockTags.LOGS_THAT_BURN)
                .add(ModBlocks.SLIMY_LOG.getRegistryEntry().registryKey().getValue())
                .add(ModBlocks.SLIMY_WOOD.getRegistryEntry().registryKey().getValue())
                .add(ModBlocks.STRIPPED_SLIMY_LOG.getRegistryEntry().registryKey().getValue())
                .add(ModBlocks.STRIPPED_SLIMY_WOOD.getRegistryEntry().registryKey().getValue());

        getTagBuilder(BlockTags.PLANKS)
                .add(ModBlocks.SLIMY_PLANKS.getRegistryEntry().registryKey().getValue());

        getTagBuilder(BlockTags.SAPLINGS)
                .add(ModBlocks.SLIMY_SAPLING.getRegistryEntry().registryKey().getValue());

        getTagBuilder(BlockTags.LEAVES)
                .add(ModBlocks.SLIMY_LEAVES.getRegistryEntry().registryKey().getValue());

        getTagBuilder(BlockTags.LOGS)
                .add(ModBlocks.SLIMY_LOG.getRegistryEntry().registryKey().getValue())
                .add(ModBlocks.SLIMY_WOOD.getRegistryEntry().registryKey().getValue())
                .add(ModBlocks.STRIPPED_SLIMY_LOG.getRegistryEntry().registryKey().getValue())
                .add(ModBlocks.STRIPPED_SLIMY_WOOD.getRegistryEntry().registryKey().getValue());

        getTagBuilder(ModTags.Blocks.SLIMY_LOGS)
                .add(ModBlocks.SLIMY_LOG.getRegistryEntry().registryKey().getValue())
                .add(ModBlocks.SLIMY_WOOD.getRegistryEntry().registryKey().getValue())
                .add(ModBlocks.STRIPPED_SLIMY_LOG.getRegistryEntry().registryKey().getValue())
                .add(ModBlocks.STRIPPED_SLIMY_WOOD.getRegistryEntry().registryKey().getValue());

        getTagBuilder(BlockTags.FENCES)
                .add(ModBlocks.SLIMY_FENCE.getRegistryEntry().registryKey().getValue());

        getTagBuilder(BlockTags.FENCE_GATES)
                .add(ModBlocks.SLIMY_FENCE_GATE.getRegistryEntry().registryKey().getValue());

        getTagBuilder(BlockTags.WOODEN_DOORS)
                .add(ModBlocks.SLIMY_DOOR.getRegistryEntry().registryKey().getValue());

        getTagBuilder(BlockTags.WOODEN_TRAPDOORS)
                .add(ModBlocks.SLIMY_TRAPDOOR.getRegistryEntry().registryKey().getValue());

        getTagBuilder(BlockTags.WALLS)
                .add(ModBlocks.SLIMY_COBBLESTONE_WALL.getRegistryEntry().registryKey().getValue())
                .add(ModBlocks.SLIMY_COBBLED_DEEPSLATE_WALL.getRegistryEntry().registryKey().getValue());

        getTagBuilder(BlockTags.PICKAXE_MINEABLE)
                .add(ModBlocks.MELTING_STATION.getRegistryEntry().registryKey().getValue())
                .add(ModBlocks.SOLIDING_STATION.getRegistryEntry().registryKey().getValue())
                .add(ModBlocks.ENERGY_GENERATOR.getRegistryEntry().registryKey().getValue())
                .add(ModBlocks.DNA_EXTRACTOR.getRegistryEntry().registryKey().getValue())
                .add(ModBlocks.DNA_SYNTHESIZER.getRegistryEntry().registryKey().getValue())
                .add(ModBlocks.FLUID_TANK.getRegistryEntry().registryKey().getValue())
                .add(ModBlocks.SLIME_SQUEEZER.getRegistryEntry().registryKey().getValue())
                .add(ModBlocks.CABLE.getRegistryEntry().registryKey().getValue())
                .add(ModBlocks.SLIME_NEST.getRegistryEntry().registryKey().getValue())
                .add(ModBlocks.SLIMEBALL_COLLECTOR.getRegistryEntry().registryKey().getValue())
                .add(ModBlocks.SLIMY_STONE.getRegistryEntry().registryKey().getValue())
                .add(ModBlocks.SLIMY_DEEPSLATE.getRegistryEntry().registryKey().getValue())
                .add(ModBlocks.SLIMY_COBBLESTONE.getRegistryEntry().registryKey().getValue())
                .add(ModBlocks.SLIMY_COBBLED_DEEPSLATE.getRegistryEntry().registryKey().getValue())
                .add(ModBlocks.SLIMY_COBBLESTONE_SLAB.getRegistryEntry().registryKey().getValue())
                .add(ModBlocks.SLIMY_COBBLED_DEEPSLATE_SLAB.getRegistryEntry().registryKey().getValue())
                .add(ModBlocks.SLIMY_COBBLESTONE_STAIRS.getRegistryEntry().registryKey().getValue())
                .add(ModBlocks.SLIMY_COBBLED_DEEPSLATE_STAIRS.getRegistryEntry().registryKey().getValue())
                .add(ModBlocks.SLIMY_COBBLESTONE_WALL.getRegistryEntry().registryKey().getValue())
                .add(ModBlocks.SLIMY_COBBLED_DEEPSLATE_WALL.getRegistryEntry().registryKey().getValue())
                .add(ModBlocks.SLIMY_STONE_STAIRS.getRegistryEntry().registryKey().getValue())
                .add(ModBlocks.SLIMY_STONE_SLAB.getRegistryEntry().registryKey().getValue())
                .add(ModBlocks.SLIMY_STONE_BUTTON.getRegistryEntry().registryKey().getValue())
                .add(ModBlocks.SLIMY_STONE_PRESSURE_PLATE.getRegistryEntry().registryKey().getValue());

        getTagBuilder(BlockTags.AXE_MINEABLE)
                .add(ModBlocks.SLIMY_LOG.getRegistryEntry().registryKey().getValue())
                .add(ModBlocks.SLIMY_WOOD.getRegistryEntry().registryKey().getValue())
                .add(ModBlocks.STRIPPED_SLIMY_LOG.getRegistryEntry().registryKey().getValue())
                .add(ModBlocks.STRIPPED_SLIMY_WOOD.getRegistryEntry().registryKey().getValue())
                .add(ModBlocks.SLIMY_PLANKS.getRegistryEntry().registryKey().getValue())
                .add(ModBlocks.SLIMY_FENCE.getRegistryEntry().registryKey().getValue())
                .add(ModBlocks.SLIMY_FENCE_GATE.getRegistryEntry().registryKey().getValue())
                .add(ModBlocks.SLIMY_PRESSURE_PLATE.getRegistryEntry().registryKey().getValue())
                .add(ModBlocks.SLIMY_BUTTON.getRegistryEntry().registryKey().getValue())
                .add(ModBlocks.SLIMY_DOOR.getRegistryEntry().registryKey().getValue())
                .add(ModBlocks.SLIMY_TRAPDOOR.getRegistryEntry().registryKey().getValue())
                .add(ModBlocks.SLIMY_STAIRS.getRegistryEntry().registryKey().getValue())
                .add(ModBlocks.SLIMY_SLAB.getRegistryEntry().registryKey().getValue());

        getTagBuilder(BlockTags.SHOVEL_MINEABLE)
                .add(ModBlocks.SLIMY_GRASS_BLOCK.getRegistryEntry().registryKey().getValue())
                .add(ModBlocks.SLIMY_DIRT.getRegistryEntry().registryKey().getValue());

        getTagBuilder(BlockTags.NEEDS_STONE_TOOL)
                .add(ModBlocks.MELTING_STATION.getRegistryEntry().registryKey().getValue())
                .add(ModBlocks.SOLIDING_STATION.getRegistryEntry().registryKey().getValue())
                .add(ModBlocks.DNA_EXTRACTOR.getRegistryEntry().registryKey().getValue())
                .add(ModBlocks.DNA_SYNTHESIZER.getRegistryEntry().registryKey().getValue())
                .add(ModBlocks.ENERGY_GENERATOR.getRegistryEntry().registryKey().getValue())
                .add(ModBlocks.FLUID_TANK.getRegistryEntry().registryKey().getValue())
                .add(ModBlocks.SLIME_SQUEEZER.getRegistryEntry().registryKey().getValue())
                .add(ModBlocks.CABLE.getRegistryEntry().registryKey().getValue())
                .add(ModBlocks.SLIME_NEST.getRegistryEntry().registryKey().getValue())
                .add(ModBlocks.SLIMEBALL_COLLECTOR.getRegistryEntry().registryKey().getValue());
    }
}
