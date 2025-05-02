package com.chesy.productiveslimes.datagen;

import com.chesy.productiveslimes.ProductiveSlimes;
import com.chesy.productiveslimes.block.ModBlocks;
import com.chesy.productiveslimes.item.ModItems;
import com.chesy.productiveslimes.tier.ModTier;
import com.chesy.productiveslimes.tier.ModTiers;
import com.chesy.productiveslimes.tier.Tier;
import com.chesy.productiveslimes.util.ModTags;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.fabricmc.fabric.api.tag.convention.v2.ConventionalItemTags;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.tag.ItemTags;

import java.util.concurrent.CompletableFuture;

public class ModItemTagProvider extends FabricTagProvider.ItemTagProvider {
    public ModItemTagProvider(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> completableFuture) {
        super(output, completableFuture);
    }

    @Override
    protected void configure(RegistryWrapper.WrapperLookup wrapperLookup) {
        var slimeballTag = getTagBuilder(ConventionalItemTags.SLIME_BALLS);

        slimeballTag.add(ProductiveSlimes.ENERGY_SLIME_BALL.getRegistryEntry().registryKey().getValue());

        for (Tier tier : Tier.values()){
            ModTier tiers = ModTiers.getTierByName(tier);
            slimeballTag.add(ModTiers.getSlimeballItemByName(tiers.name()).getRegistryEntry().registryKey().getValue());
        }

        var dnaTag = getTagBuilder(ModTags.Items.DNA_ITEM);
        dnaTag.add(ModItems.SLIME_DNA.getRegistryEntry().registryKey().getValue());

        for (Tier tier : Tier.values()){
            ModTier tiers = ModTiers.getTierByName(tier);
            dnaTag.add(ModTiers.getDnaItemByName(tiers.name()).getRegistryEntry().registryKey().getValue());
        }

        getTagBuilder(ModTags.Items.TRANSFORMABLE_ITEMS)
                .add(ModItems.GUIDEBOOK.getRegistryEntry().registryKey().getValue())
                .add(ModItems.SLIMEBALL_FRAGMENT.getRegistryEntry().registryKey().getValue())
                .add(ProductiveSlimes.ENERGY_SLIME_BALL.getRegistryEntry().registryKey().getValue())
                .add(ModItems.ENERGY_MULTIPLIER_UPGRADE.getRegistryEntry().registryKey().getValue());

        getTagBuilder(ItemTags.LOGS_THAT_BURN)
                .add(ModBlocks.SLIMY_LOG.asItem().getRegistryEntry().registryKey().getValue())
                .add(ModBlocks.SLIMY_WOOD.asItem().getRegistryEntry().registryKey().getValue())
                .add(ModBlocks.STRIPPED_SLIMY_LOG.asItem().getRegistryEntry().registryKey().getValue())
                .add(ModBlocks.STRIPPED_SLIMY_WOOD.asItem().getRegistryEntry().registryKey().getValue());

        getTagBuilder(ItemTags.PLANKS)
                .add(ModBlocks.SLIMY_PLANKS.asItem().getRegistryEntry().registryKey().getValue());

        getTagBuilder(ModTags.Items.SLIMY_LOG)
                .add(ModBlocks.SLIMY_LOG.asItem().getRegistryEntry().registryKey().getValue())
                .add(ModBlocks.STRIPPED_SLIMY_LOG.asItem().getRegistryEntry().registryKey().getValue())
                .add(ModBlocks.SLIMY_WOOD.asItem().getRegistryEntry().registryKey().getValue())
                .add(ModBlocks.STRIPPED_SLIMY_WOOD.asItem().getRegistryEntry().registryKey().getValue());
    }
}
