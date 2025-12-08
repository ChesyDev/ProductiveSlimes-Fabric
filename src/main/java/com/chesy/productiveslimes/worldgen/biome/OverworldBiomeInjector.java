package com.chesy.productiveslimes.worldgen.biome;

import com.mojang.datafixers.util.Pair;
import net.minecraft.registry.RegistryKey;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.source.util.MultiNoiseUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class OverworldBiomeInjector {
    private static final List<BiomeData> CUSTOM_BIOMES = new ArrayList<>();

    public static void registerBiome(RegistryKey<Biome> biome, MultiNoiseUtil.NoiseHypercube climate, int weight) {
        CUSTOM_BIOMES.add(new BiomeData(biome, climate, weight));
    }

    public static void injectBiomes(Consumer<Pair<MultiNoiseUtil.NoiseHypercube, RegistryKey<Biome>>> consumer) {
        for (BiomeData entry : CUSTOM_BIOMES) {
            int injectionCount = Math.max(1, entry.weight() / 3);
            for (int i = 0; i < injectionCount; i++) {
                consumer.accept(Pair.of(entry.climate, entry.biome));
            }
        }
    }

    public record BiomeData(RegistryKey<Biome> biome, MultiNoiseUtil.NoiseHypercube climate, int weight) {
        @Override
        public int weight() {
            return Math.max(1, Math.min(10, weight));
        }
    }
}