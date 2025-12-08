package com.chesy.productiveslimes.worldgen.biome;

import net.minecraft.world.biome.source.util.MultiNoiseUtil;

public class BiomeRegister {
    public static void init() {
        OverworldBiomeInjector.registerBiome(
                ModBiomes.SLIMY_LAND,
                new MultiNoiseUtil.NoiseHypercube(
                        MultiNoiseUtil.ParameterRange.of(0.1F, 0.8F),
                        MultiNoiseUtil.ParameterRange.of(0.2F, 0.7F),
                        MultiNoiseUtil.ParameterRange.of(-0.19F, 0.4F),
                        MultiNoiseUtil.ParameterRange.of(-0.22F, 0.55F),
                        MultiNoiseUtil.ParameterRange.of(0.0F, 0.0F),
                        MultiNoiseUtil.ParameterRange.of(-0.56F, 0.56F),
                        0L
                ),
                7
        );
    }
}