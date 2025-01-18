package com.chesy.productiveslimes.datagen;

import com.chesy.productiveslimes.tier.ModTiers;
import com.chesy.productiveslimes.tier.Tier;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.fluid.Fluid;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.tag.FluidTags;

import java.util.concurrent.CompletableFuture;

public class ModFluidTagProvider extends FabricTagProvider.FluidTagProvider {
    public ModFluidTagProvider(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture) {
        super(output, registriesFuture);
    }

    @Override
    protected void configure(RegistryWrapper.WrapperLookup wrapperLookup) {
        FabricTagProvider<Fluid>.FabricTagBuilder tag =  getOrCreateTagBuilder(FluidTags.WATER);

        for (Tier tier : Tier.values()){
            tag.add(ModTiers.getSourceByName(ModTiers.getTierByName(tier).name()));
            tag.add(ModTiers.getFlowByName(ModTiers.getTierByName(tier).name()));
        }
    }
}