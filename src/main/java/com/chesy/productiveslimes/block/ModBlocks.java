package com.chesy.productiveslimes.block;

import com.chesy.productiveslimes.ProductiveSlimes;
import com.chesy.productiveslimes.block.custom.*;
import com.chesy.productiveslimes.tier.ModTiers;
import com.chesy.productiveslimes.tier.ModTier;
import com.chesy.productiveslimes.tier.Tier;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;

public class ModBlocks {
    public static final Block MELTING_STATION = registerBlock("melting_station", new MeltingStationBlock(AbstractBlock.Settings.copy(Blocks.IRON_BLOCK)
            .registryKey(RegistryKey.of(RegistryKeys.BLOCK, Identifier.of(ProductiveSlimes.MOD_ID, "melting_station")))));

    public static final Block SOLIDING_STATION = registerBlock("soliding_station", new SolidingStationBlock(AbstractBlock.Settings.copy(Blocks.IRON_BLOCK)
            .registryKey(RegistryKey.of(RegistryKeys.BLOCK, Identifier.of(ProductiveSlimes.MOD_ID, "soliding_station")))));

    public static final Block DNA_EXTRACTOR = registerBlock("dna_extractor", new DnaExtratorBlock(AbstractBlock.Settings.copy(Blocks.IRON_BLOCK)
            .registryKey(RegistryKey.of(RegistryKeys.BLOCK, Identifier.of(ProductiveSlimes.MOD_ID, "dna_extractor")))));

    public static final Block DNA_SYNTHESIZER = registerBlock("dna_synthesizer", new DnaSynthesizerBlock(AbstractBlock.Settings.copy(Blocks.IRON_BLOCK)
            .registryKey(RegistryKey.of(RegistryKeys.BLOCK, Identifier.of(ProductiveSlimes.MOD_ID, "dna_synthesizer")))));

    public static final Block ENERGY_GENERATOR = registerBlock("energy_generator", new EnergyGeneratorBlock(AbstractBlock.Settings.copy(Blocks.IRON_BLOCK)
            .registryKey(RegistryKey.of(RegistryKeys.BLOCK, Identifier.of(ProductiveSlimes.MOD_ID, "energy_generator")))));

    public static final Block SLIME_SQUEEZER = registerBlock("slime_squeezer", new SlimeSqueezerBlock(AbstractBlock.Settings.copy(Blocks.IRON_BLOCK)
            .registryKey(RegistryKey.of(RegistryKeys.BLOCK, Identifier.of(ProductiveSlimes.MOD_ID, "slime_squeezer")))));

    public static final Block FLUID_TANK = registerBlock("fluid_tank", new FluidTankBlock(AbstractBlock.Settings.copy(Blocks.IRON_BLOCK)
            .registryKey(RegistryKey.of(RegistryKeys.BLOCK, Identifier.of(ProductiveSlimes.MOD_ID, "fluid_tank")))));

    public static final Block CABLE = registerBlock("cable", new CableBlock(AbstractBlock.Settings.copy(Blocks.IRON_BLOCK)
            .registryKey(RegistryKey.of(RegistryKeys.BLOCK, Identifier.of(ProductiveSlimes.MOD_ID, "cable")))));

    public static final SlimeBlock ENERGY_SLIME_BLOCK = registerSlimeBlock("energy_slime_block", 0xFFffff70);

    public static Block registerBlock(String name, Block block){
        Identifier id = Identifier.of(ProductiveSlimes.MOD_ID, name);
        registerItem(name, new BlockItem(block, new Item.Settings().registryKey(RegistryKey.of(RegistryKeys.ITEM, Identifier.of(ProductiveSlimes.MOD_ID, name)))));
        return Registry.register(Registries.BLOCK, id, block);
    }

    public static SlimeBlock registerSlimeBlock(String name, int color) {
        Identifier id = Identifier.of(ProductiveSlimes.MOD_ID, name);
        SlimeBlock block = new SlimeBlock(AbstractBlock.Settings.copy(Blocks.SLIME_BLOCK)
                .registryKey(RegistryKey.of(RegistryKeys.BLOCK, Identifier.of(ProductiveSlimes.MOD_ID, name))), color);
        registerItem(name, new BlockItem(block, new Item.Settings().registryKey(RegistryKey.of(RegistryKeys.ITEM, Identifier.of(ProductiveSlimes.MOD_ID, name)))));
        return Registry.register(Registries.BLOCK, id, block);
    }

    public static void registerTierBlocks(){
        for (Tier name : Tier.values()){
            ModTier tier = ModTiers.getTierByName(name);
            String blockName = tier.name() + "_slime_block";
            SlimeBlock registeredSlimeBlock = registerSlimeBlock(blockName, tier.color());
            ModTiers.addRegisteredBlock(tier.name(), registeredSlimeBlock);
        }
    }

    public static void registerItem(String name, Item item) {
        Identifier itemID = Identifier.of(ProductiveSlimes.MOD_ID, name);
        Registry.register(Registries.ITEM, itemID, item);
    }

    public static void initialize() {
        registerTierBlocks();
    }
}
