package com.chesy.productiveslimes.block;

import com.chesy.productiveslimes.ProductiveSlimes;
import com.chesy.productiveslimes.block.custom.MeltingStationBlock;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;

public class ModBlocks {
    public static final Block MELTING_STATION = registerBlock("melting_station", new MeltingStationBlock(AbstractBlock.Settings.create()
            .registryKey(RegistryKey.of(RegistryKeys.BLOCK, Identifier.of(ProductiveSlimes.MOD_ID, "melting_station")))));

    public static Block registerBlock(String name, Block block){
        Identifier id = Identifier.of(ProductiveSlimes.MOD_ID, name);
        registerItem(name, new BlockItem(block, new Item.Settings().registryKey(RegistryKey.of(RegistryKeys.ITEM, Identifier.of(ProductiveSlimes.MOD_ID, name)))));
        return Registry.register(Registries.BLOCK, id, block);
    }

    public static void registerItem(String name, Item item) {
        Identifier itemID = Identifier.of(ProductiveSlimes.MOD_ID, name);
        Registry.register(Registries.ITEM, itemID, item);
    }

    public static void initialize() {
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.INGREDIENTS).register(entries -> {
            entries.add(MELTING_STATION);
        });
    }
}
