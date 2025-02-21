package com.chesy.productiveslimes.worldgen;

import com.chesy.productiveslimes.ProductiveSlimes;
import com.chesy.productiveslimes.block.ModBlocks;
import com.chesy.productiveslimes.tier.ModTiers;
import com.chesy.productiveslimes.tier.Tier;
import net.minecraft.registry.Registerable;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.intprovider.ConstantIntProvider;
import net.minecraft.world.gen.feature.*;
import net.minecraft.world.gen.feature.size.TwoLayersFeatureSize;
import net.minecraft.world.gen.foliage.LargeOakFoliagePlacer;
import net.minecraft.world.gen.stateprovider.BlockStateProvider;
import net.minecraft.world.gen.trunk.LargeOakTrunkPlacer;

public class ModConfiguredFeatures {
    public static final RegistryKey<ConfiguredFeature<?,?>> SLIMY_TREE = registerKey("slimy_tree");
    public static final RegistryKey<ConfiguredFeature<?,?>> LAKE_MOLTEN_DIRT = registerKey("lake_molten_dirt");
    public static final RegistryKey<ConfiguredFeature<?,?>> LAKE_MOLTEN_STONE = registerKey("lake_molten_stone");

    public static void bootstrap(Registerable<ConfiguredFeature<?, ?>> context){
        register(context, ModConfiguredFeatures.SLIMY_TREE, Feature.TREE, new TreeFeatureConfig.Builder(
                BlockStateProvider.of(ModBlocks.SLIMY_LOG),
                new LargeOakTrunkPlacer(4, 4, 3),
                BlockStateProvider.of(ModBlocks.SLIMY_LEAVES),
                new LargeOakFoliagePlacer(ConstantIntProvider.create(2), ConstantIntProvider.create(3), 3),
                new TwoLayersFeatureSize(1, 0, 2)
        ).dirtProvider(BlockStateProvider.of(ModBlocks.SLIMY_DIRT)).build());

        register(context, ModConfiguredFeatures.LAKE_MOLTEN_DIRT, Feature.LAKE, new LakeFeature.Config(BlockStateProvider.of(ModTiers.getLiquidBlockByName(Tier.DIRT.getTierName()).getDefaultState()), BlockStateProvider.of(ModBlocks.SLIMY_DIRT.getDefaultState())));
        register(context, ModConfiguredFeatures.LAKE_MOLTEN_STONE, Feature.LAKE, new LakeFeature.Config(BlockStateProvider.of(ModTiers.getLiquidBlockByName(Tier.STONE.getTierName()).getDefaultState()), BlockStateProvider.of(ModBlocks.SLIMY_DIRT.getDefaultState())));
    }

    private static RegistryKey<ConfiguredFeature<?,?>> registerKey(String name){
        return RegistryKey.of(RegistryKeys.CONFIGURED_FEATURE, Identifier.of(ProductiveSlimes.MODID, name));
    }

    private static <FC extends FeatureConfig, F extends Feature<FC>> void register(Registerable<ConfiguredFeature<?, ?>> context, RegistryKey<ConfiguredFeature<?, ?>> configuredFeatureKey, F feature, FC configuration)
    {
        context.register(configuredFeatureKey, new ConfiguredFeature<>(feature, configuration));
    }
}
