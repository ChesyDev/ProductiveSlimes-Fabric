package com.chesy.productiveslimes.worldgen.tree;

import com.chesy.productiveslimes.ProductiveSlimes;
import com.chesy.productiveslimes.worldgen.ModConfiguredFeatures;
import net.minecraft.block.sapling.SaplingGenerator;
import net.minecraft.registry.RegistryKey;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class ModTreeGrowers extends SaplingGenerator{
    @Nullable
    @Override
    protected RegistryKey<ConfiguredFeature<?, ?>> getTreeFeature(Random random, boolean bees) {
        return ModConfiguredFeatures.SLIMY_TREE;
    }
}
