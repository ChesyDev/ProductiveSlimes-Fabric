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
        getOrCreateTagBuilder(BlockTags.DIRT)
                .add(ModBlocks.SLIMY_GRASS_BLOCK)
                .add(ModBlocks.SLIMY_DIRT);

        getOrCreateTagBuilder(BlockTags.STONE_ORE_REPLACEABLES)
                .add(ModBlocks.SLIMY_STONE);

        getOrCreateTagBuilder(BlockTags.DEEPSLATE_ORE_REPLACEABLES)
                .add(ModBlocks.SLIMY_DEEPSLATE);

        getOrCreateTagBuilder(BlockTags.LOGS_THAT_BURN)
                .add(ModBlocks.SLIMY_LOG)
                .add(ModBlocks.SLIMY_WOOD)
                .add(ModBlocks.STRIPPED_SLIMY_LOG)
                .add(ModBlocks.STRIPPED_SLIMY_WOOD);

        getOrCreateTagBuilder(BlockTags.PLANKS)
                .add(ModBlocks.SLIMY_PLANKS);

        getOrCreateTagBuilder(BlockTags.SAPLINGS)
                .add(ModBlocks.SLIMY_SAPLING);

        getOrCreateTagBuilder(BlockTags.LEAVES)
                .add(ModBlocks.SLIMY_LEAVES);

        getOrCreateTagBuilder(BlockTags.LOGS)
                .add(ModBlocks.SLIMY_LOG)
                .add(ModBlocks.SLIMY_WOOD)
                .add(ModBlocks.STRIPPED_SLIMY_LOG)
                .add(ModBlocks.STRIPPED_SLIMY_WOOD);

        getOrCreateTagBuilder(ModTags.Blocks.SLIMY_LOGS)
                .add(ModBlocks.SLIMY_LOG)
                .add(ModBlocks.SLIMY_WOOD)
                .add(ModBlocks.STRIPPED_SLIMY_LOG)
                .add(ModBlocks.STRIPPED_SLIMY_WOOD);

        getOrCreateTagBuilder(BlockTags.FENCES)
                .add(ModBlocks.SLIMY_FENCE);

        getOrCreateTagBuilder(BlockTags.FENCE_GATES)
                .add(ModBlocks.SLIMY_FENCE_GATE);

        getOrCreateTagBuilder(BlockTags.WOODEN_DOORS)
                .add(ModBlocks.SLIMY_DOOR);

        getOrCreateTagBuilder(BlockTags.WOODEN_TRAPDOORS)
                .add(ModBlocks.SLIMY_TRAPDOOR);

        getOrCreateTagBuilder(BlockTags.WALLS)
                .add(ModBlocks.SLIMY_COBBLESTONE_WALL)
                .add(ModBlocks.SLIMY_COBBLED_DEEPSLATE_WALL);

        getOrCreateTagBuilder(BlockTags.PICKAXE_MINEABLE)
                .add(ModBlocks.MELTING_STATION)
                .add(ModBlocks.SOLIDING_STATION)
                .add(ModBlocks.ENERGY_GENERATOR)
                .add(ModBlocks.DNA_EXTRACTOR)
                .add(ModBlocks.DNA_SYNTHESIZER)
                .add(ModBlocks.FLUID_TANK)
                .add(ModBlocks.SLIME_SQUEEZER)
                .add(ModBlocks.CABLE)
                .add(ModBlocks.PIPE)
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

        getOrCreateTagBuilder(BlockTags.AXE_MINEABLE)
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

        getOrCreateTagBuilder(BlockTags.SHOVEL_MINEABLE)
                .add(ModBlocks.SLIMY_GRASS_BLOCK)
                .add(ModBlocks.SLIMY_DIRT);

        getOrCreateTagBuilder(BlockTags.NEEDS_STONE_TOOL)
                .add(ModBlocks.MELTING_STATION)
                .add(ModBlocks.SOLIDING_STATION)
                .add(ModBlocks.DNA_EXTRACTOR)
                .add(ModBlocks.DNA_SYNTHESIZER)
                .add(ModBlocks.ENERGY_GENERATOR)
                .add(ModBlocks.FLUID_TANK)
                .add(ModBlocks.SLIME_SQUEEZER)
                .add(ModBlocks.CABLE)
                .add(ModBlocks.PIPE)
                .add(ModBlocks.SLIME_NEST)
                .add(ModBlocks.SLIMEBALL_COLLECTOR);
    }
}
