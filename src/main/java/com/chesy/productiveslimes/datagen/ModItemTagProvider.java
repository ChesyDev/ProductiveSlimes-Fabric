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
        var slimeballTag = getOrCreateTagBuilder(ConventionalItemTags.SLIME_BALLS);

        slimeballTag.add(ProductiveSlimes.ENERGY_SLIME_BALL);

        for (Tier tier : Tier.values()){
            ModTier tiers = ModTiers.getTierByName(tier);
            slimeballTag.add(ModTiers.getSlimeballItemByName(tiers.name()));
        }

        var dnaTag = getOrCreateTagBuilder(ModTags.Items.DNA_ITEM);

        for (Tier tier : Tier.values()){
            ModTier tiers = ModTiers.getTierByName(tier);
            dnaTag.add(ModTiers.getDnaItemByName(tiers.name()));
        }

        getOrCreateTagBuilder(ModTags.Items.TRANSFORMABLE_ITEMS)
                .add(ModItems.GUIDEBOOK)
                .add(ModItems.SLIMEBALL_FRAGMENT)
                .add(ProductiveSlimes.ENERGY_SLIME_BALL)
                .add(ModItems.ENERGY_MULTIPLIER_UPGRADE);

        getOrCreateTagBuilder(ItemTags.LOGS_THAT_BURN)
                .add(ModBlocks.SLIMY_LOG.asItem())
                .add(ModBlocks.SLIMY_WOOD.asItem())
                .add(ModBlocks.STRIPPED_SLIMY_LOG.asItem())
                .add(ModBlocks.STRIPPED_SLIMY_WOOD.asItem());

        getOrCreateTagBuilder(ItemTags.PLANKS)
                .add(ModBlocks.SLIMY_PLANKS.asItem());

        getOrCreateTagBuilder(ModTags.Items.SLIMY_LOG)
                .add(ModBlocks.SLIMY_LOG.asItem())
                .add(ModBlocks.STRIPPED_SLIMY_LOG.asItem())
                .add(ModBlocks.SLIMY_WOOD.asItem())
                .add(ModBlocks.STRIPPED_SLIMY_WOOD.asItem());
    }
}
