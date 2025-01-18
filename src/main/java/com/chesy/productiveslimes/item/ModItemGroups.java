package com.chesy.productiveslimes.item;

import com.chesy.productiveslimes.ProductiveSlimes;
import com.chesy.productiveslimes.block.ModBlocks;
import com.chesy.productiveslimes.tier.ModTiers;
import com.chesy.productiveslimes.tier.Tier;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class ModItemGroups {
    public static final ItemGroup PRODUCTIVE_SLIME_TAB = Registry.register(Registries.ITEM_GROUP,
            Identifier.of(ProductiveSlimes.MOD_ID, "productive_slimes"),
            FabricItemGroup.builder().icon(() -> new ItemStack(Items.SLIME_BLOCK))
                    .displayName(Text.translatable("creativetab.productiveslimes"))
                    .entries((displayContext, entries) -> {
                        entries.add(ModItems.ENERGY_MULTIPLIER_UPGRADE);
                        entries.add(ModItems.SLIMEBALL_FRAGMENT);
                        entries.add(ModBlocks.MELTING_STATION);
                        entries.add(ModBlocks.SOLIDING_STATION);
                        entries.add(ModBlocks.DNA_EXTRACTOR);
                        entries.add(ModBlocks.DNA_SYNTHESIZER);
                        entries.add(ModBlocks.ENERGY_GENERATOR);
                        entries.add(ModBlocks.SLIME_SQUEEZER);
                        entries.add(ModBlocks.FLUID_TANK);
                        entries.add(ModBlocks.CABLE);

                        entries.add(ModBlocks.ENERGY_SLIME_BLOCK);
                        for(Tier tier : Tier.values()) {
                            entries.add(ModTiers.getBlockByName(ModTiers.getTierByName(tier).name()).asItem());
                        }

                        entries.add(ProductiveSlimes.ENERGY_SLIME_BALL);
                        for (Tier tier : Tier.values()){
                            entries.add(ModTiers.getSlimeballItemByName(ModTiers.getTierByName(tier).name()).asItem());
                        }

                        entries.add(ModItems.SLIME_DNA);
                        for (Tier tier : Tier.values()){
                            entries.add(ModTiers.getDnaItemByName(ModTiers.getTierByName(tier).name()).asItem());
                        }

                        for (Tier tier : Tier.values()){
                            entries.add(ModTiers.getBucketItemByName(ModTiers.getTierByName(tier).name()).asItem());
                        }

                        entries.add(ModItems.ENERGY_SLIME_SPAWN_EGG);
                        for (Tier tier : Tier.values()){
                            entries.add(ModTiers.getSpawnEggItemByName(ModTiers.getTierByName(tier).name()).asItem());
                        }
                    }).build());

    public static void initialize() {

    }
}
