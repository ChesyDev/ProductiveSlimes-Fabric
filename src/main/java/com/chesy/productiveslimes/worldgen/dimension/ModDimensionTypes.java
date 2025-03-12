package com.chesy.productiveslimes.worldgen.dimension;

import com.chesy.productiveslimes.ProductiveSlimes;
import net.minecraft.registry.Registerable;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.intprovider.UniformIntProvider;
import net.minecraft.world.dimension.DimensionType;

import java.util.OptionalLong;

public class ModDimensionTypes {
    public static final RegistryKey<DimensionType> SLIMY_WORLD = RegistryKey.of(RegistryKeys.DIMENSION_TYPE, Identifier.of(ProductiveSlimes.MODID, "slimy_world"));

    public static void boostrap(Registerable<DimensionType> context){
        context.register(SLIMY_WORLD, slimyWorld(context));
    }

    public static DimensionType slimyWorld(Registerable<DimensionType> bootstrap){
        return new DimensionType(
                OptionalLong.of(6000),
                true,
                false,
                false,
                true,
                1.0,
                true,
                true,
                -64,
                384,
                256,
                BlockTags.INFINIBURN_OVERWORLD,
                Identifier.of("minecraft:overworld"),
                0,
                new DimensionType.MonsterSettings(
                        false,
                        false,
                        UniformIntProvider.create(0, 0),
                        0
                )
        );
    }
}
