package com.chesy.productiveslimes.fluid;

import com.chesy.productiveslimes.ProductiveSlimes;
import com.chesy.productiveslimes.item.custom.BucketItem;
import com.chesy.productiveslimes.tier.ModTiers;
import com.chesy.productiveslimes.tier.Tier;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Blocks;
import net.minecraft.block.FluidBlock;
import net.minecraft.fluid.FlowableFluid;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;

public class ModFluids {
    public static void register() {
        for (Tier tier : Tier.values()){
            FlowableFluid STILL_DYNAMIC_FLUID = Registry.register(Registries.FLUID,
                    Identifier.of(ProductiveSlimes.MODID, "still_" + ModTiers.getTierByName(tier).name()), new DynamicFluid.Still(ModTiers.getTierByName(tier).name()));
            FlowableFluid FLOWING_DYNAMIC_FLUID = Registry.register(Registries.FLUID,
                    Identifier.of(ProductiveSlimes.MODID, "flowing_"  + ModTiers.getTierByName(tier).name()), new DynamicFluid.Flowing(ModTiers.getTierByName(tier).name()));

            ModTiers.addRegisteredSource(ModTiers.getTierByName(tier).name(), STILL_DYNAMIC_FLUID);
            ModTiers.addRegisteredFlow(ModTiers.getTierByName(tier).name(), FLOWING_DYNAMIC_FLUID);

            FluidBlock DYNAMIC_FLUID_BLOCK = Registry.register(Registries.BLOCK, Identifier.of(ProductiveSlimes.MODID, "molten_" + ModTiers.getTierByName(tier).name() + "_block"),
                    new FluidBlock(ModTiers.getSourceByName(ModTiers.getTierByName(tier).name()), AbstractBlock.Settings.copy(Blocks.WATER).registryKey(RegistryKey.of(RegistryKeys.BLOCK, Identifier.of(ProductiveSlimes.MODID, "molten_" + ModTiers.getTierByName(tier).name() + "_block")))) {
                    });
            BucketItem DYNAMIC_FLUID_BUCKET = Registry.register(Registries.ITEM, Identifier.of(ProductiveSlimes.MODID, "molten_" + ModTiers.getTierByName(tier).name() + "_bucket"),
                    new BucketItem(ModTiers.getSourceByName(ModTiers.getTierByName(tier).name()), new Item.Settings().maxCount(64).registryKey(RegistryKey.of(RegistryKeys.ITEM, Identifier.of(ProductiveSlimes.MODID, "molten_" + ModTiers.getTierByName(tier).name() + "_bucket"))), ModTiers.getTierByName(tier).color()));

            ModTiers.addRegisteredLiquidBlock(ModTiers.getTierByName(tier).name(), DYNAMIC_FLUID_BLOCK);
            ModTiers.addRegisteredBucketItem(ModTiers.getTierByName(tier).name(), DYNAMIC_FLUID_BUCKET);
        }
    }
}
