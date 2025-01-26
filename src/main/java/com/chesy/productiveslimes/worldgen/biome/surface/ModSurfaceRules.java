package com.chesy.productiveslimes.worldgen.biome.surface;

import com.chesy.productiveslimes.block.ModBlocks;
import com.chesy.productiveslimes.worldgen.biome.ModBiomes;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.VerticalSurfaceType;
import net.minecraft.world.gen.YOffset;
import net.minecraft.world.gen.surfacebuilder.MaterialRules;

public class ModSurfaceRules {
    private static final MaterialRules.MaterialRule SLIMY_GRASS_BLOCK = makeStateRule(ModBlocks.SLIMY_GRASS_BLOCK);
    private static final MaterialRules.MaterialRule SLIMY_DIRT = makeStateRule(ModBlocks.SLIMY_DIRT);
    private static final MaterialRules.MaterialRule BEDROCK = makeStateRule(Blocks.BEDROCK);
    private static final MaterialRules.MaterialRule SLIMY_STONE = makeStateRule(ModBlocks.SLIMY_STONE);
    private static final MaterialRules.MaterialRule SLIMY_DEEPSLATE = makeStateRule(ModBlocks.SLIMY_DEEPSLATE);

    public static MaterialRules.MaterialRule makeRules() {
        return MaterialRules.sequence(
                MaterialRules.condition(
                        MaterialRules.verticalGradient(
                                "minecraft:bedrock_floor",
                                YOffset.aboveBottom(0),
                                YOffset.aboveBottom(5)
                        ),
                        BEDROCK
                ),
                MaterialRules.condition(
                        MaterialRules.biome(ModBiomes.SLIMY_LAND),
                        MaterialRules.sequence(
                                MaterialRules.condition(
                                        MaterialRules.surface(),
                                        MaterialRules.sequence(
                                                MaterialRules.condition(
                                                        MaterialRules.stoneDepth(0, false, VerticalSurfaceType.FLOOR),
                                                        SLIMY_GRASS_BLOCK
                                                ),
                                                MaterialRules.condition(
                                                        MaterialRules.stoneDepth(1, true, 5, VerticalSurfaceType.FLOOR),
                                                        SLIMY_DIRT
                                                )
                                        )
                                ),
                                MaterialRules.condition(
                                        MaterialRules.aboveYWithStoneDepth(YOffset.fixed(0), 5),
                                        MaterialRules.condition(
                                                MaterialRules.stoneDepth(5, true, 80, VerticalSurfaceType.FLOOR),
                                                SLIMY_STONE
                                        )
                                ),
                                MaterialRules.condition(
                                        MaterialRules.stoneDepth(80, true, 256, VerticalSurfaceType.FLOOR),
                                        SLIMY_DEEPSLATE
                                )
                        )
                )
        );

    }

    private static MaterialRules.MaterialRule makeStateRule(Block block) {
        return MaterialRules.block(block.getDefaultState());
    }
}
