package com.chesy.productiveslimes.datagen;

import com.chesy.productiveslimes.tier.ModTierLists;
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
            tag.add(ModTierLists.getSourceByName(ModTierLists.getTierByName(tier).name()));
            tag.add(ModTierLists.getFlowByName(ModTierLists.getTierByName(tier).name()));
        }
    }
}