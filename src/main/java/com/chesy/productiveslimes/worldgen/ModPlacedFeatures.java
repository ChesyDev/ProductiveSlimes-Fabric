package com.chesy.productiveslimes.worldgen;

import com.chesy.productiveslimes.ProductiveSlimes;
import com.chesy.productiveslimes.block.ModBlocks;
import net.minecraft.registry.Registerable;
import net.minecraft.registry.RegistryEntryLookup;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Identifier;
import net.minecraft.world.Heightmap;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.PlacedFeature;
import net.minecraft.world.gen.feature.PlacedFeatures;
import net.minecraft.world.gen.feature.VegetationPlacedFeatures;
import net.minecraft.world.gen.placementmodifier.*;

import java.util.List;

public class ModPlacedFeatures {
    public static RegistryKey<PlacedFeature> SLIMY_TREE = registerKey("slimy_tree");
    public static RegistryKey<PlacedFeature> LAKE_MOLTEN_DIRT = registerKey("lake_molten_dirt");
    public static RegistryKey<PlacedFeature> LAKE_MOLTEN_STONE = registerKey("lake_molten_stone");

    public static void bootstrap(Registerable<PlacedFeature> context) {
        RegistryEntryLookup<ConfiguredFeature<?, ?>> configuredFeatureGetter = context.getRegistryLookup(RegistryKeys.CONFIGURED_FEATURE);

        register(context, ModPlacedFeatures.SLIMY_TREE, configuredFeatureGetter.getOrThrow(ModConfiguredFeatures.SLIMY_TREE), VegetationPlacedFeatures.treeModifiersWithWouldSurvive(PlacedFeatures.createCountExtraModifier(1, 0.005f, 1), ModBlocks.SLIMY_SAPLING));

        register(context, ModPlacedFeatures.LAKE_MOLTEN_DIRT, configuredFeatureGetter.getOrThrow(ModConfiguredFeatures.LAKE_MOLTEN_DIRT), List.of(
                RarityFilterPlacementModifier.of(200),
                SquarePlacementModifier.of(),
                HeightmapPlacementModifier.of(Heightmap.Type.WORLD_SURFACE_WG)
        ));
        register(context, ModPlacedFeatures.LAKE_MOLTEN_STONE, configuredFeatureGetter.getOrThrow(ModConfiguredFeatures.LAKE_MOLTEN_STONE), List.of(
                RarityFilterPlacementModifier.of(250),
                SquarePlacementModifier.of(),
                HeightmapPlacementModifier.of(Heightmap.Type.WORLD_SURFACE_WG)
        ));
    }

    private static RegistryKey<PlacedFeature> registerKey(String name) {
        return RegistryKey.of(RegistryKeys.PLACED_FEATURE, Identifier.of(ProductiveSlimes.MODID, name));
    }

    protected static void register(Registerable<PlacedFeature> context, RegistryKey<PlacedFeature> placedFeatureKey, RegistryEntry<ConfiguredFeature<?, ?>> configuredFeature, List<PlacementModifier> modifiers) {
        context.register(placedFeatureKey, new PlacedFeature(configuredFeature, modifiers));
    }
}
