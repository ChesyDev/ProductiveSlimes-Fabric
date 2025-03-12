package com.chesy.productiveslimes.worldgen.dimension;

import com.chesy.productiveslimes.ProductiveSlimes;
import com.chesy.productiveslimes.worldgen.biome.ModBiomes;
import com.chesy.productiveslimes.worldgen.noise.ModNoiseSettings;
import com.mojang.datafixers.util.Pair;
import net.minecraft.registry.Registerable;
import net.minecraft.registry.RegistryEntryLookup;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.source.MultiNoiseBiomeSource;
import net.minecraft.world.biome.source.util.MultiNoiseUtil;
import net.minecraft.world.dimension.DimensionOptions;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.gen.chunk.ChunkGeneratorSettings;
import net.minecraft.world.gen.chunk.NoiseChunkGenerator;

import java.util.List;

public class ModDimensions {
    public static final RegistryKey<World> SLIMY_WORLD = RegistryKey.of(RegistryKeys.WORLD, Identifier.of(ProductiveSlimes.MODID, "slimy_world"));
    public static final RegistryKey<DimensionOptions> SLIMY_WORLD_STEM = RegistryKey.of(RegistryKeys.DIMENSION, Identifier.of(ProductiveSlimes.MODID, "slimy_world"));

    public static void boostrap(Registerable<DimensionOptions> context){
        context.register(SLIMY_WORLD_STEM, slimyWorld(context));
    }

    public static DimensionOptions slimyWorld(Registerable<DimensionOptions> context){
        RegistryEntryLookup<Biome> biomeRegistry = context.getRegistryLookup(RegistryKeys.BIOME);
        RegistryEntryLookup<DimensionType> dimTypes = context.getRegistryLookup(RegistryKeys.DIMENSION_TYPE);
        RegistryEntryLookup<ChunkGeneratorSettings> noiseGenSettings = context.getRegistryLookup(RegistryKeys.CHUNK_GENERATOR_SETTINGS);

        NoiseChunkGenerator wrappedChunkGenerator = new NoiseChunkGenerator(
                MultiNoiseBiomeSource.create(
                        new MultiNoiseUtil.Entries<>(
                                List.of(
                                        Pair.of(
                                                new MultiNoiseUtil.NoiseHypercube(
                                                        MultiNoiseUtil.ParameterRange.of(-1.0F, 1.0F),
                                                        MultiNoiseUtil.ParameterRange.of(-1.0F, 1.0F),
                                                        MultiNoiseUtil.ParameterRange.of(-1.0F, -0.1F),
                                                        MultiNoiseUtil.ParameterRange.of(-1.0F, 1.0F),
                                                        MultiNoiseUtil.ParameterRange.of(0.0F),
                                                        MultiNoiseUtil.ParameterRange.of(-1.0F, 1.0F),
                                                        0L
                                                ),
                                                biomeRegistry.getOrThrow(ModBiomes.SLIMY_OCEAN)
                                        ),
                                        Pair.of(
                                                new MultiNoiseUtil.NoiseHypercube(
                                                        MultiNoiseUtil.ParameterRange.of(-1.0F, 1.0F),
                                                        MultiNoiseUtil.ParameterRange.of(-1.0F, 1.0F),
                                                        MultiNoiseUtil.ParameterRange.of(0.1F, 1.0F),
                                                        MultiNoiseUtil.ParameterRange.of(-1.0F, 1.0F),
                                                        MultiNoiseUtil.ParameterRange.of(0.0F),
                                                        MultiNoiseUtil.ParameterRange.of(-1.0F, 1.0F),
                                                        0L
                                                ),
                                                biomeRegistry.getOrThrow(ModBiomes.SLIMY_LAND)
                                        )
                                )
                        )
                ),
                noiseGenSettings.getOrThrow(ModNoiseSettings.SLIMY_WORLD)
        );

        return new DimensionOptions(dimTypes.getOrThrow(ModDimensionTypes.SLIMY_WORLD), wrappedChunkGenerator);
    }
}
