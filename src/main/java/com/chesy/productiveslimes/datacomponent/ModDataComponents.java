package com.chesy.productiveslimes.datacomponent;

import com.chesy.productiveslimes.ProductiveSlimes;
import com.mojang.serialization.Codec;
import net.minecraft.component.ComponentType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModDataComponents {
    public static ComponentType<Integer> ENERGY = Registry.register(Registries.DATA_COMPONENT_TYPE, Identifier.of(ProductiveSlimes.MOD_ID, "energy"), ComponentType.<Integer>builder().codec(Codec.INT).build());

    public static void register() {

    }
}