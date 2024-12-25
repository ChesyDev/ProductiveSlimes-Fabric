package com.chesy.productiveslimes.block;

import com.chesy.productiveslimes.ProductiveSlimes;
import com.chesy.productiveslimes.block.custom.DnaExtratorBlock;
import com.chesy.productiveslimes.block.custom.DnaSynthesizerBlock;
import com.chesy.productiveslimes.block.custom.MeltingStationBlock;
import com.chesy.productiveslimes.block.custom.SolidingStationBlock;
import com.chesy.productiveslimes.item.ModItemGroups;
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

    public static final Block SOLIDING_STATION = registerBlock("soliding_station", new SolidingStationBlock(AbstractBlock.Settings.create()
            .registryKey(RegistryKey.of(RegistryKeys.BLOCK, Identifier.of(ProductiveSlimes.MOD_ID, "soliding_station")))));

    public static final Block DNA_EXTRACTOR = registerBlock("dna_extractor", new DnaExtratorBlock(AbstractBlock.Settings.create()
            .registryKey(RegistryKey.of(RegistryKeys.BLOCK, Identifier.of(ProductiveSlimes.MOD_ID, "dna_extractor")))));

    public static final Block DNA_SYNTHESIZER = registerBlock("dna_synthesizer", new DnaSynthesizerBlock(AbstractBlock.Settings.create()
            .registryKey(RegistryKey.of(RegistryKeys.BLOCK, Identifier.of(ProductiveSlimes.MOD_ID, "dna_synthesizer")))));

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

    }
}
