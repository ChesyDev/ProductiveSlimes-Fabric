package com.chesy.productiveslimes.block;

import com.chesy.productiveslimes.ProductiveSlimes;
import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModBlocks {

    public static void initialize() {}
    public static Block register(Block block, String name, boolean shouldRegisterItem){
        Identifier id = Identifier.of(ProductiveSlimes.MOD_ID, name);

        if(shouldRegisterItem) {
            BlockItem blockItem = new BlockItem(block, new Item.Settings());
            Registry.register(Registries.ITEM, id, blockItem);
        }

        return Registry.register(Registries.BLOCK, id, block);
    }
}