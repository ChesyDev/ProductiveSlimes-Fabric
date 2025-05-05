package com.chesy.productiveslimes.enchantment;

import com.chesy.productiveslimes.ProductiveSlimes;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;

public final class ModEnchantments {
    public static final RegistryKey<Enchantment> GLIDING = register("gliding");

    private static RegistryKey<Enchantment> register(String id) {
        return RegistryKey.of(RegistryKeys.ENCHANTMENT, Identifier.of(ProductiveSlimes.MODID, id));
    }

    public static void initialize() {
    }
}
