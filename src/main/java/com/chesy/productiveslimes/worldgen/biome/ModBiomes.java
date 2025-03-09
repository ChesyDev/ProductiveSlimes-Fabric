package com.chesy.productiveslimes.worldgen.biome;

import com.chesy.productiveslimes.ProductiveSlimes;
import com.chesy.productiveslimes.tier.ModTiers;
import com.chesy.productiveslimes.worldgen.ModPlacedFeatures;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.registry.Registerable;
import net.minecraft.registry.RegistryEntryLookup;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.sound.BiomeMoodSound;
import net.minecraft.util.Identifier;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeEffects;
import net.minecraft.world.biome.GenerationSettings;
import net.minecraft.world.biome.SpawnSettings;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.feature.DefaultBiomeFeatures;
import net.minecraft.world.gen.feature.PlacedFeature;

public class ModBiomes {
    public static final RegistryKey<Biome> SLIMY_LAND = RegistryKey.of(RegistryKeys.BIOME, Identifier.of(ProductiveSlimes.MODID, "slimy_land"));

    public static void bootstrap(Registerable<Biome> context){
        context.register(SLIMY_LAND, slimeLand(context));
    }

    public static void globalOverworldGeneration(GenerationSettings.LookupBackedBuilder builder) {
        DefaultBiomeFeatures.addFrozenTopLayer(builder);
        DefaultBiomeFeatures.addDefaultOres(builder);
    }

    private static Biome slimeLand(Registerable<Biome> context){
        SpawnSettings.Builder spawnBuilder = new SpawnSettings.Builder();

        spawnBuilder.spawn(SpawnGroup.MONSTER, 10, new SpawnSettings.SpawnEntry(EntityType.SLIME, 1, 1));
        spawnBuilder.spawn(SpawnGroup.MONSTER, 100, new SpawnSettings.SpawnEntry(EntityType.BAT, 1, 1));
        spawnBuilder.spawn(SpawnGroup.CREATURE, 100, new SpawnSettings.SpawnEntry(ModTiers.getEntityByName("dirt"), 1, 1));
        spawnBuilder.spawn(SpawnGroup.CREATURE, 65, new SpawnSettings.SpawnEntry(ModTiers.getEntityByName("stone"), 1, 1));
        spawnBuilder.spawn(SpawnGroup.CREATURE, 10, new SpawnSettings.SpawnEntry(ModTiers.getEntityByName("iron"), 1, 1));

        RegistryEntryLookup<PlacedFeature> placedFeatureHolderGetter = context.getRegistryLookup(RegistryKeys.PLACED_FEATURE);

        GenerationSettings.LookupBackedBuilder biomeBuilder = new GenerationSettings.LookupBackedBuilder(context.getRegistryLookup(RegistryKeys.PLACED_FEATURE), context.getRegistryLookup(RegistryKeys.CONFIGURED_CARVER));

        biomeBuilder.feature(GenerationStep.Feature.VEGETAL_DECORATION, placedFeatureHolderGetter.getOrThrow(ModPlacedFeatures.SLIMY_TREE));

        biomeBuilder.feature(GenerationStep.Feature.LAKES, placedFeatureHolderGetter.getOrThrow(ModPlacedFeatures.LAKE_MOLTEN_DIRT));
        biomeBuilder.feature(GenerationStep.Feature.LAKES, placedFeatureHolderGetter.getOrThrow(ModPlacedFeatures.LAKE_MOLTEN_STONE));

        globalOverworldGeneration(biomeBuilder);

        return new Biome.Builder()
                .precipitation(true)
                .downfall(0.4f)
                .temperature(0.8f)
                .generationSettings(biomeBuilder.build())
                .spawnSettings(spawnBuilder.build())
                .effects((new BiomeEffects.Builder())
                        .fogColor(0xFFFFFF)
                        .waterColor(0x254788)
                        .waterFogColor(0x2b1b05)
                        .skyColor(0x6EB1FF)
                        .moodSound(BiomeMoodSound.CAVE).build())
                .build();
    }
}