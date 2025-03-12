package com.chesy.productiveslimes.worldgen.biome.surface;

import com.chesy.productiveslimes.block.ModBlocks;
import com.chesy.productiveslimes.worldgen.biome.ModBiomes;
import com.google.common.collect.ImmutableList;
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

    public static MaterialRules.MaterialRule makeRules(boolean aboveGround, boolean bedrockRoof, boolean bedrockFloor) {
        MaterialRules.MaterialCondition waterBlockCheck = MaterialRules.water(0, 0);

        // Define common surface rules for both biomes
        MaterialRules.MaterialRule MaterialRulesForBiome = MaterialRules.sequence(
                MaterialRules.condition(
                        MaterialRules.surface(),
                        MaterialRules.sequence(
                                MaterialRules.condition(
                                        MaterialRules.stoneDepth(0, false, VerticalSurfaceType.FLOOR),
                                        MaterialRules.sequence(MaterialRules.condition(waterBlockCheck, SLIMY_GRASS_BLOCK), SLIMY_DIRT)
                                ),
                                MaterialRules.condition(
                                        MaterialRules.stoneDepth(1, true, 5, VerticalSurfaceType.FLOOR),
                                        SLIMY_DIRT
                                )
                        )
                )
        );

        MaterialRules.MaterialRule biomeRules = MaterialRules.sequence(
                MaterialRules.condition(
                        MaterialRules.biome(ModBiomes.SLIMY_LAND),
                        MaterialRulesForBiome
                ),
                MaterialRules.condition(
                        MaterialRules.biome(ModBiomes.SLIMY_OCEAN),
                        MaterialRulesForBiome
                )
        );

        // Build the final rules with bedrock and deepslate transitions
        ImmutableList.Builder<MaterialRules.MaterialRule> builder = ImmutableList.builder();
        if (bedrockRoof) {
            builder.add(
                    MaterialRules.condition(
                            MaterialRules.not(MaterialRules.verticalGradient("bedrock_roof", YOffset.belowTop(5), YOffset.getTop())),
                            BEDROCK
                    )
            );
        }
        if (bedrockFloor) {
            builder.add(
                    MaterialRules.condition(
                            MaterialRules.verticalGradient("bedrock_floor", YOffset.getBottom(), YOffset.aboveBottom(5)),
                            BEDROCK
                    )
            );
        }

        // Add the biome-specific rules
        builder.add(biomeRules);

        // Add deepslate transition below y=0
        builder.add(
                MaterialRules.condition(
                        MaterialRules.verticalGradient("deepslate", YOffset.fixed(0), YOffset.fixed(8)),
                        SLIMY_DEEPSLATE
                )
        );

        return MaterialRules.sequence(builder.build().toArray(MaterialRules.MaterialRule[]::new));
    }

    private static MaterialRules.MaterialRule makeStateRule(Block block) {
        return MaterialRules.block(block.getDefaultState());
    }
}
