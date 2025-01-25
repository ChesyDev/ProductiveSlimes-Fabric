package com.chesy.productiveslimes.worldgen.tree;

import com.chesy.productiveslimes.ProductiveSlimes;
import com.chesy.productiveslimes.worldgen.ModConfiguredFeatures;
import net.minecraft.block.SaplingGenerator;

import java.util.Optional;

public class ModTreeGrowers {
    public static final SaplingGenerator SLIMY = new SaplingGenerator(ProductiveSlimes.MODID + ":slimy", Optional.empty(),
            Optional.of(ModConfiguredFeatures.SLIMY_TREE), Optional.empty());
}
