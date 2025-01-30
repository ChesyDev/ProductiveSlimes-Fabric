package com.chesy.productiveslimes.item;

import com.chesy.productiveslimes.ProductiveSlimes;
import com.chesy.productiveslimes.block.ModBlocks;
import com.chesy.productiveslimes.config.CustomVariant;
import com.chesy.productiveslimes.config.CustomVariantRegistry;
import com.chesy.productiveslimes.entity.renderer.BaseSlimeRenderer;
import com.chesy.productiveslimes.tier.ModTiers;
import com.chesy.productiveslimes.tier.Tier;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.render.fluid.v1.FluidRenderHandlerRegistry;
import net.fabricmc.fabric.api.client.render.fluid.v1.SimpleFluidRenderHandler;
import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.block.Block;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.lang.reflect.Field;

public class ModItemGroups {
    public static final ItemGroup PRODUCTIVE_SLIME_TAB = Registry.register(Registries.ITEM_GROUP,
            Identifier.of(ProductiveSlimes.MODID, "productive_slimes"),
            FabricItemGroup.builder().icon(() -> new ItemStack(Items.SLIME_BLOCK))
                    .displayName(Text.translatable("creativetab.productiveslimes"))
                    .entries((displayContext, entries) -> {
                        entries.add(ModItems.GUIDEBOOK);
                        entries.add(ModItems.ENERGY_MULTIPLIER_UPGRADE);
                        entries.add(ModItems.SLIME_NEST_SPEED_UPGRADE_1);
                        entries.add(ModItems.SLIME_NEST_SPEED_UPGRADE_2);
                        entries.add(ModItems.SLIMEBALL_FRAGMENT);

                        for (Field field : ModBlocks.class.getFields()) {
                            try {
                                // Ensure the field is a Supplier of Block (for blocks)
                                if (Block.class.isAssignableFrom(field.getType())) {
                                    Block block = (Block) field.get(null);
                                    if (block != null) entries.add(block);
                                }
                            } catch (IllegalAccessException e) {
                                e.printStackTrace();
                            }
                        }

                        for(Tier tier : Tier.values()) {
                            entries.add(ModTiers.getBlockByName(ModTiers.getTierByName(tier).name()).asItem());
                        }
                        for (CustomVariant variant : CustomVariantRegistry.getLoadedTiers()){
                            String name = variant.name();
                            entries.add(CustomVariantRegistry.getSlimeBlockForVariant(name));
                        }

                        entries.add(ProductiveSlimes.ENERGY_SLIME_BALL);
                        for (Tier tier : Tier.values()){
                            entries.add(ModTiers.getSlimeballItemByName(ModTiers.getTierByName(tier).name()).asItem());
                        }
                        for (CustomVariant variant : CustomVariantRegistry.getLoadedTiers()){
                            String name = variant.name();
                            entries.add(CustomVariantRegistry.getSlimeballItemForVariant(name));
                        }

                        entries.add(ModItems.SLIME_DNA);
                        for (Tier tier : Tier.values()){
                            entries.add(ModTiers.getDnaItemByName(ModTiers.getTierByName(tier).name()).asItem());
                        }
                        for (CustomVariant variant : CustomVariantRegistry.getLoadedTiers()){
                            String name = variant.name();
                            entries.add(CustomVariantRegistry.getDnaItemForVariant(name));
                        }

                        for (Tier tier : Tier.values()){
                            entries.add(ModTiers.getBucketItemByName(ModTiers.getTierByName(tier).name()).asItem());
                        }
                        for (CustomVariant variant : CustomVariantRegistry.getLoadedTiers()){
                            String name = variant.name();
                            entries.add(CustomVariantRegistry.getBucketItemForVariant(name));
                        }

                        entries.add(ModItems.ENERGY_SLIME_SPAWN_EGG);
                        for (Tier tier : Tier.values()){
                            entries.add(ModTiers.getSpawnEggItemByName(ModTiers.getTierByName(tier).name()).asItem());
                        }
                        for (CustomVariant variant : CustomVariantRegistry.getLoadedTiers()){
                            String name = variant.name();
                            entries.add(CustomVariantRegistry.getSpawnEggItemForVariant(name));
                        }
                    }).build());

    public static void initialize() {

    }
}
