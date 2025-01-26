package com.chesy.productiveslimes.event;

import com.chesy.productiveslimes.ProductiveSlimes;
import com.chesy.productiveslimes.item.ModItems;
import com.chesy.productiveslimes.tier.ModTiers;
import com.chesy.productiveslimes.villager.ModVillagers;
import net.fabricmc.fabric.api.object.builder.v1.trade.TradeOfferHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.village.TradeOffer;
import net.minecraft.village.TradedItem;

public class ModVillagerTrade {
    public static void init(){
        TradeOfferHelper.registerVillagerOffers(ModVillagers.SCIENTIST, 1, factories -> {
            factories.add((entity, random) -> new TradeOffer(
                    new TradedItem(ModTiers.getSlimeballItemByName("dirt"), 10),
                    new ItemStack(Items.EMERALD, 1), 8, 2, 0.05f
            ));
            factories.add((entity, random) -> new TradeOffer(
                    new TradedItem(ModTiers.getSlimeballItemByName("stone"), 10),
                    new ItemStack(Items.EMERALD, 1), 8, 2, 0.05f
            ));
            factories.add((entity, random) -> new TradeOffer(
                    new TradedItem(Items.SLIME_BALL, 20),
                    new ItemStack(Items.EMERALD, 1), 8, 2, 0.05f
            ));
            factories.add((entity, random) -> new TradeOffer(
                    new TradedItem(Items.EMERALD, 1),
                    new ItemStack(ModTiers.getSlimeballItemByName("copper"), 4), 8, 1, 0.05f
            ));
        });

        TradeOfferHelper.registerVillagerOffers(ModVillagers.SCIENTIST, 2, factories -> {
            factories.add((entity, random) -> new TradeOffer(
                    new TradedItem(Items.EMERALD, 1),
                    new ItemStack(ProductiveSlimes.ENERGY_SLIME_BALL, 4), 4, 10, 0.05f
            ));
            factories.add((entity, random) -> new TradeOffer(
                    new TradedItem(Items.EMERALD, 1),
                    new ItemStack(ModTiers.getSlimeballItemByName("iron"), 4), 4, 10, 0.05f
            ));
            factories.add((entity, random) -> new TradeOffer(
                    new TradedItem(Items.EMERALD, 1),
                    new ItemStack(ModTiers.getSlimeballItemByName("stone"), 6), 4, 10, 0.05f
            ));
        });

        TradeOfferHelper.registerVillagerOffers(ModVillagers.SCIENTIST, 3, factories -> {
            factories.add((entity, random) -> new TradeOffer(
                    new TradedItem(ModTiers.getBucketItemByName("dirt"), 16),
                    new ItemStack(Items.EMERALD, 1), 4, 15, 0.05f
            ));
            factories.add((entity, random) -> new TradeOffer(
                    new TradedItem(ModTiers.getBucketItemByName("stone"), 12),
                    new ItemStack(Items.EMERALD, 1), 4, 15, 0.05f
            ));
            factories.add((entity, random) -> new TradeOffer(
                    new TradedItem(Items.EMERALD, 20),
                    new ItemStack(ModItems.ENERGY_SLIME_SPAWN_EGG, 1), 4, 15, 0.05f
            ));
        });

        TradeOfferHelper.registerVillagerOffers(ModVillagers.SCIENTIST, 4, factories -> {
            factories.add((entity, random) -> new TradeOffer(
                    new TradedItem(Items.EMERALD, 32),
                    new ItemStack(ModTiers.getDnaItemByName("iron"), 1), 4, 20, 0.05f
            ));
            factories.add((entity, random) -> new TradeOffer(
                    new TradedItem(Items.EMERALD, 28),
                    new ItemStack(ModTiers.getDnaItemByName("gold"), 1), 4, 20, 0.05f
            ));
            factories.add((entity, random) -> new TradeOffer(
                    new TradedItem(Items.EMERALD, 48),
                    new ItemStack(ModTiers.getDnaItemByName("diamond"), 1), 4, 20, 0.05f
            ));
        });

        TradeOfferHelper.registerVillagerOffers(ModVillagers.SCIENTIST, 5, factories -> {
            factories.add((entity, random) -> new TradeOffer(
                    new TradedItem(Items.EMERALD, 64),
                    new ItemStack(ModTiers.getSpawnEggItemByName("diamond"), 1), 2, 30, 0.05f
            ));
            factories.add((entity, random) -> new TradeOffer(
                    new TradedItem(Items.EMERALD, 40),
                    new ItemStack(ModTiers.getSpawnEggItemByName("gold"), 1), 2, 30, 0.05f
            ));
            factories.add((entity, random) -> new TradeOffer(
                    new TradedItem(Items.EMERALD, 48),
                    new ItemStack(ModTiers.getSpawnEggItemByName("iron"), 1), 2, 30, 0.05f
            ));
            factories.add((entity, random) -> new TradeOffer(
                    new TradedItem(Items.EMERALD, 32),
                    new ItemStack(ModTiers.getSpawnEggItemByName("copper"), 1), 2, 30, 0.05f
            ));
            factories.add((entity, random) -> new TradeOffer(
                    new TradedItem(ModTiers.getSpawnEggItemByName("dirt"), 1),
                    new ItemStack(Items.EMERALD, 12), 2, 30, 0.05f
            ));
        });
    }
}
