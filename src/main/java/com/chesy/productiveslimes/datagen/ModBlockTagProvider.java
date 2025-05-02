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
        valueLookupBuilder(BlockTags.DIRT)
                .add(ModBlocks.SLIMY_GRASS_BLOCK)
                .add(ModBlocks.SLIMY_DIRT);

        valueLookupBuilder(BlockTags.STONE_ORE_REPLACEABLES)
                .add(ModBlocks.SLIMY_STONE);

        valueLookupBuilder(BlockTags.DEEPSLATE_ORE_REPLACEABLES)
                .add(ModBlocks.SLIMY_DEEPSLATE);

        valueLookupBuilder(BlockTags.LOGS_THAT_BURN)
                .add(ModBlocks.SLIMY_LOG)
                .add(ModBlocks.SLIMY_WOOD)
                .add(ModBlocks.STRIPPED_SLIMY_LOG)
                .add(ModBlocks.STRIPPED_SLIMY_WOOD);

        valueLookupBuilder(BlockTags.PLANKS)
                .add(ModBlocks.SLIMY_PLANKS);

        valueLookupBuilder(BlockTags.SAPLINGS)
                .add(ModBlocks.SLIMY_SAPLING);

        valueLookupBuilder(BlockTags.LEAVES)
                .add(ModBlocks.SLIMY_LEAVES);

        valueLookupBuilder(BlockTags.LOGS)
                .add(ModBlocks.SLIMY_LOG)
                .add(ModBlocks.SLIMY_WOOD)
                .add(ModBlocks.STRIPPED_SLIMY_LOG)
                .add(ModBlocks.STRIPPED_SLIMY_WOOD);

        valueLookupBuilder(ModTags.Blocks.SLIMY_LOGS)
                .add(ModBlocks.SLIMY_LOG)
                .add(ModBlocks.SLIMY_WOOD)
                .add(ModBlocks.STRIPPED_SLIMY_LOG)
                .add(ModBlocks.STRIPPED_SLIMY_WOOD);

        valueLookupBuilder(BlockTags.FENCES)
                .add(ModBlocks.SLIMY_FENCE);

        valueLookupBuilder(BlockTags.FENCE_GATES)
                .add(ModBlocks.SLIMY_FENCE_GATE);

        valueLookupBuilder(BlockTags.WOODEN_DOORS)
                .add(ModBlocks.SLIMY_DOOR);

        valueLookupBuilder(BlockTags.WOODEN_TRAPDOORS)
                .add(ModBlocks.SLIMY_TRAPDOOR);

        valueLookupBuilder(BlockTags.WALLS)
                .add(ModBlocks.SLIMY_COBBLESTONE_WALL)
                .add(ModBlocks.SLIMY_COBBLED_DEEPSLATE_WALL);

        valueLookupBuilder(BlockTags.PICKAXE_MINEABLE)
                .add(ModBlocks.MELTING_STATION)
                .add(ModBlocks.SOLIDING_STATION)
                .add(ModBlocks.ENERGY_GENERATOR)
                .add(ModBlocks.DNA_EXTRACTOR)
                .add(ModBlocks.DNA_SYNTHESIZER)
                .add(ModBlocks.FLUID_TANK)
                .add(ModBlocks.SLIME_SQUEEZER)
                .add(ModBlocks.CABLE)
                .add(ModBlocks.SLIME_NEST)
                .add(ModBlocks.SLIMEBALL_COLLECTOR)
                .add(ModBlocks.SLIMY_STONE)
                .add(ModBlocks.SLIMY_DEEPSLATE)
                .add(ModBlocks.SLIMY_COBBLESTONE)
                .add(ModBlocks.SLIMY_COBBLED_DEEPSLATE)
                .add(ModBlocks.SLIMY_COBBLESTONE_SLAB)
                .add(ModBlocks.SLIMY_COBBLED_DEEPSLATE_SLAB)
                .add(ModBlocks.SLIMY_COBBLESTONE_STAIRS)
                .add(ModBlocks.SLIMY_COBBLED_DEEPSLATE_STAIRS)
                .add(ModBlocks.SLIMY_COBBLESTONE_WALL)
                .add(ModBlocks.SLIMY_COBBLED_DEEPSLATE_WALL)
                .add(ModBlocks.SLIMY_STONE_STAIRS)
                .add(ModBlocks.SLIMY_STONE_SLAB)
                .add(ModBlocks.SLIMY_STONE_BUTTON)
                .add(ModBlocks.SLIMY_STONE_PRESSURE_PLATE);

        valueLookupBuilder(BlockTags.AXE_MINEABLE)
                .add(ModBlocks.SLIMY_LOG)
                .add(ModBlocks.SLIMY_WOOD)
                .add(ModBlocks.STRIPPED_SLIMY_LOG)
                .add(ModBlocks.STRIPPED_SLIMY_WOOD)
                .add(ModBlocks.SLIMY_PLANKS)
                .add(ModBlocks.SLIMY_FENCE)
                .add(ModBlocks.SLIMY_FENCE_GATE)
                .add(ModBlocks.SLIMY_PRESSURE_PLATE)
                .add(ModBlocks.SLIMY_BUTTON)
                .add(ModBlocks.SLIMY_DOOR)
                .add(ModBlocks.SLIMY_TRAPDOOR)
                .add(ModBlocks.SLIMY_STAIRS)
                .add(ModBlocks.SLIMY_SLAB);

        valueLookupBuilder(BlockTags.SHOVEL_MINEABLE)
                .add(ModBlocks.SLIMY_GRASS_BLOCK)
                .add(ModBlocks.SLIMY_DIRT);

        valueLookupBuilder(BlockTags.NEEDS_STONE_TOOL)
                .add(ModBlocks.MELTING_STATION)
                .add(ModBlocks.SOLIDING_STATION)
                .add(ModBlocks.DNA_EXTRACTOR)
                .add(ModBlocks.DNA_SYNTHESIZER)
                .add(ModBlocks.ENERGY_GENERATOR)
                .add(ModBlocks.FLUID_TANK)
                .add(ModBlocks.SLIME_SQUEEZER)
                .add(ModBlocks.CABLE)
                .add(ModBlocks.SLIME_NEST)
                .add(ModBlocks.SLIMEBALL_COLLECTOR);
    }
}
