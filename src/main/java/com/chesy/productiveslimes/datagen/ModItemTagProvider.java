package com.chesy.productiveslimes.datagen;

import com.chesy.productiveslimes.item.ModItems;
import com.chesy.productiveslimes.util.ModTags;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.registry.RegistryWrapper;

import java.util.concurrent.CompletableFuture;

public class ModItemTagProvider extends FabricTagProvider.ItemTagProvider {
    public ModItemTagProvider(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> completableFuture) {
        super(output, completableFuture);
    }

    @Override
    protected void configure(RegistryWrapper.WrapperLookup wrapperLookup) {
        getOrCreateTagBuilder(ModTags.Items.TRANSFORMABLE_ITEMS)
                .add(ModItems.GUIDEBOOK)
                .add(ModItems.SLIMEBALL_FRAGMENT)
                .add(ModItems.ENERGY_SLIME_BALL)
                .add(ModItems.ENERGY_MULTIPLIER_UPGRADE);
        getOrCreateTagBuilder(ModTags.Items.DNA_ITEM)
                .add(ModItems.SLIME_DNA);
    }
}
