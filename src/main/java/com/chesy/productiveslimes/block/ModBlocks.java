package com.chesy.productiveslimes.block;

import com.chesy.productiveslimes.ProductiveSlimes;
import com.chesy.productiveslimes.block.custom.MeltingStationBlock;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModBlocks {
    public static final Block MELTING_STATION = registerBlock("melting_station", new MeltingStationBlock(AbstractBlock.Settings.create()));

    public static Block registerBlock(String name, Block block){
        System.out.println("registerBlock called");
        Identifier id = Identifier.of(ProductiveSlimes.MOD_ID, name);
        System.out.println("Block Identifier Created");
        System.out.println("Registering Block: " + id.toString());
        registerItem(name, new BlockItem(block, new Item.Settings()));
        return Registry.register(Registries.BLOCK, id, block);
    }

    public static Item registerItem(String name, Item item) {
        Identifier itemID = Identifier.of(ProductiveSlimes.MOD_ID, name);
        System.out.println("Registering BlockItem: " + itemID.toString());
        return Registry.register(Registries.ITEM, itemID, item);
    }

    public static void initialize() {
        System.out.println("initializing ModBlocks");
    }
}
