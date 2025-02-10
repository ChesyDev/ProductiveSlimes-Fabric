package com.chesy.productiveslimes.block;

import com.chesy.productiveslimes.ProductiveSlimes;
import com.chesy.productiveslimes.block.custom.*;
import com.chesy.productiveslimes.block.custom.SlimeBlock;
import com.chesy.productiveslimes.tier.ModTiers;
import com.chesy.productiveslimes.tier.ModTier;
import com.chesy.productiveslimes.tier.Tier;
import com.chesy.productiveslimes.worldgen.tree.ModTreeGrowers;
import net.minecraft.block.*;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;

public class ModBlocks {
    public static final Block MELTING_STATION = registerBlock("melting_station", new MeltingStationBlock(AbstractBlock.Settings.copy(Blocks.IRON_BLOCK).nonOpaque()));

    public static final Block SOLIDING_STATION = registerBlock("soliding_station", new SolidingStationBlock(AbstractBlock.Settings.copy(Blocks.IRON_BLOCK).nonOpaque()));

    public static final Block DNA_EXTRACTOR = registerBlock("dna_extractor", new DnaExtratorBlock(AbstractBlock.Settings.copy(Blocks.IRON_BLOCK).nonOpaque()));

    public static final Block DNA_SYNTHESIZER = registerBlock("dna_synthesizer", new DnaSynthesizerBlock(AbstractBlock.Settings.copy(Blocks.IRON_BLOCK).nonOpaque()));

    public static final Block ENERGY_GENERATOR = registerBlock("energy_generator", new EnergyGeneratorBlock(AbstractBlock.Settings.copy(Blocks.IRON_BLOCK).nonOpaque()));

    public static final Block SLIME_SQUEEZER = registerBlock("slime_squeezer", new SlimeSqueezerBlock(AbstractBlock.Settings.copy(Blocks.IRON_BLOCK).nonOpaque()));

    public static final Block FLUID_TANK = registerBlock("fluid_tank", new FluidTankBlock(AbstractBlock.Settings.copy(Blocks.IRON_BLOCK).nonOpaque()));

    public static final Block CABLE = registerBlock("cable", new CableBlock(AbstractBlock.Settings.copy(Blocks.IRON_BLOCK).nonOpaque()));
    public static final Block SQUEEZER = registerBlock("squeezer", new SqueezerBlock(AbstractBlock.Settings.copy(Blocks.IRON_BLOCK).nonOpaque()));
    public static final Block SLIMEBALL_COLLECTOR = registerBlock("slimeball_collector", new SlimeballCollectorBlock(AbstractBlock.Settings.copy(Blocks.IRON_BLOCK).nonOpaque()));
    public static final Block SLIME_NEST = registerBlock("slime_nest", new SlimeNestBlock(AbstractBlock.Settings.copy(Blocks.IRON_BLOCK).nonOpaque()));

    public static final Block SLIMY_GRASS_BLOCK = registerBlock("slimy_grass_block", new SlimyDirt(AbstractBlock.Settings.copy(Blocks.GRASS_BLOCK)));
    public static final Block SLIMY_DIRT = registerBlock("slimy_dirt", new SlimyDirt(AbstractBlock.Settings.copy(Blocks.DIRT)));
    public static final Block SLIMY_STONE = registerBlock("slimy_stone", new SlimyBlock(AbstractBlock.Settings.copy(Blocks.STONE)));
    public static final Block SLIMY_DEEPSLATE = registerBlock("slimy_deepslate", new SlimyBlock(AbstractBlock.Settings.copy(Blocks.DEEPSLATE)));
    public static final Block SLIMY_COBBLESTONE = registerBlock("slimy_cobblestone", new SlimyBlock(AbstractBlock.Settings.copy(Blocks.COBBLESTONE)));
    public static final Block SLIMY_COBBLED_DEEPSLATE = registerBlock("slimy_cobbled_deepslate", new SlimyBlock(AbstractBlock.Settings.copy(Blocks.COBBLED_DEEPSLATE)));

    // Slimy Wood Set
    public static final PillarBlock SLIMY_LOG = registerBlock("slimy_log", new ModPillarBlock(AbstractBlock.Settings.copy(Blocks.OAK_LOG)));
    public static final PillarBlock STRIPPED_SLIMY_LOG = registerBlock("stripped_slimy_log", new ModPillarBlock(AbstractBlock.Settings.copy(Blocks.STRIPPED_OAK_LOG)));
    public static final PillarBlock SLIMY_WOOD = registerBlock("slimy_wood", new ModPillarBlock(AbstractBlock.Settings.copy(Blocks.OAK_WOOD)));
    public static final PillarBlock STRIPPED_SLIMY_WOOD = registerBlock("stripped_slimy_wood", new ModPillarBlock(AbstractBlock.Settings.copy(Blocks.STRIPPED_OAK_WOOD)));
    public static final Block SLIMY_PLANKS = registerBlock("slimy_planks", new SlimeBlock(AbstractBlock.Settings.copy(Blocks.OAK_PLANKS)));
    public static final LeavesBlock SLIMY_LEAVES = registerBlock("slimy_leaves", new ModLeavesBlock(AbstractBlock.Settings.copy(Blocks.OAK_LEAVES)));
    public static final SaplingBlock SLIMY_SAPLING = registerBlock("slimy_sapling", new ModSaplingBlock(ModTreeGrowers.SLIMY, AbstractBlock.Settings.copy(Blocks.OAK_SAPLING)));

    // Slimy Wood
    public static final SlabBlock SLIMY_SLAB = registerBlock("slimy_slab", new ModSlabBlock(AbstractBlock.Settings.copy(Blocks.OAK_SLAB)));
    public static final StairsBlock SLIMY_STAIRS = registerBlock("slimy_stairs", new ModStairBlock(ModBlocks.SLIMY_PLANKS.getDefaultState(), AbstractBlock.Settings.copy(Blocks.OAK_STAIRS)));
    public static final PressurePlateBlock SLIMY_PRESSURE_PLATE = registerBlock("slimy_pressure_plate", new ModPressurePlateBlock(BlockSetType.OAK, AbstractBlock.Settings.copy(Blocks.OAK_PRESSURE_PLATE)));
    public static final ButtonBlock SLIMY_BUTTON = registerBlock("slimy_button", new ModButtonBlock(BlockSetType.OAK, 20, AbstractBlock.Settings.copy(Blocks.OAK_BUTTON)));
    public static final FenceBlock SLIMY_FENCE = registerBlock("slimy_fence", new ModFenceBlock(AbstractBlock.Settings.copy(Blocks.OAK_FENCE)));
    public static final FenceGateBlock SLIMY_FENCE_GATE = registerBlock("slimy_fence_gate", new ModFenceGateBlock(WoodType.OAK, AbstractBlock.Settings.copy(Blocks.OAK_FENCE_GATE)));
    public static final DoorBlock SLIMY_DOOR = registerBlock("slimy_door", new ModDoorBlock(BlockSetType.OAK, AbstractBlock.Settings.copy(Blocks.OAK_DOOR)));
    public static final TrapdoorBlock SLIMY_TRAPDOOR = registerBlock("slimy_trapdoor", new ModTrapDoorBlock(BlockSetType.OAK, AbstractBlock.Settings.copy(Blocks.OAK_TRAPDOOR)));

    // Stone
    public static final StairsBlock SLIMY_STONE_STAIRS = registerBlock("slimy_stone_stairs", new ModStairBlock(ModBlocks.SLIMY_STONE.getDefaultState(), AbstractBlock.Settings.copy(Blocks.STONE_STAIRS)));
    public static final SlabBlock SLIMY_STONE_SLAB = registerBlock("slimy_stone_slab", new ModSlabBlock(AbstractBlock.Settings.copy(Blocks.STONE_SLAB)));
    public static final PressurePlateBlock SLIMY_STONE_PRESSURE_PLATE = registerBlock("slimy_stone_pressure_plate", new ModPressurePlateBlock(BlockSetType.STONE, AbstractBlock.Settings.copy(Blocks.STONE_PRESSURE_PLATE)));
    public static final ButtonBlock SLIMY_STONE_BUTTON = registerBlock("slimy_stone_button", new ModButtonBlock(BlockSetType.STONE, 20, AbstractBlock.Settings.copy(Blocks.STONE_BUTTON)));

    // Cobblestone
    public static final StairsBlock SLIMY_COBBLESTONE_STAIRS = registerBlock("slimy_cobblestone_stairs", new ModStairBlock(ModBlocks.SLIMY_COBBLESTONE.getDefaultState(), AbstractBlock.Settings.copy(Blocks.COBBLESTONE_STAIRS)));
    public static final SlabBlock SLIMY_COBBLESTONE_SLAB = registerBlock("slimy_cobblestone_slab", new ModSlabBlock(AbstractBlock.Settings.copy(Blocks.COBBLESTONE_SLAB)));
    public static final WallBlock SLIMY_COBBLESTONE_WALL = registerBlock("slimy_cobblestone_wall", new ModWallBlock(AbstractBlock.Settings.copy(Blocks.COBBLESTONE_WALL)));

    // Cobbled Deepslate
    public static final StairsBlock SLIMY_COBBLED_DEEPSLATE_STAIRS = registerBlock("slimy_cobbled_deepslate_stairs", new ModStairBlock(ModBlocks.SLIMY_COBBLED_DEEPSLATE.getDefaultState(), AbstractBlock.Settings.copy(Blocks.COBBLED_DEEPSLATE_STAIRS)));
    public static final SlabBlock SLIMY_COBBLED_DEEPSLATE_SLAB = registerBlock("slimy_cobbled_deepslate_slab", new ModSlabBlock(AbstractBlock.Settings.copy(Blocks.COBBLED_DEEPSLATE_SLAB)));
    public static final WallBlock SLIMY_COBBLED_DEEPSLATE_WALL = registerBlock("slimy_cobbled_deepslate_wall", new ModWallBlock(AbstractBlock.Settings.copy(Blocks.COBBLED_DEEPSLATE_WALL)));

    public static final SlimeBlock ENERGY_SLIME_BLOCK = registerSlimeBlock("energy_slime_block", 0xFFffff70);

    public static <T extends Block> T registerBlock(String name, T block){
        Identifier id = Identifier.of(ProductiveSlimes.MODID, name);
        registerItem(name, new BlockItem(block, new Item.Settings()));
        return Registry.register(Registries.BLOCK, id, block);
    }

    public static SlimeBlock registerSlimeBlock(String name, int color) {
        Identifier id = Identifier.of(ProductiveSlimes.MODID, name);
        SlimeBlock block = new SlimeBlock(AbstractBlock.Settings.copy(Blocks.SLIME_BLOCK), color);
        registerItem(name, new BlockItem(block, new Item.Settings()));
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
        Identifier itemID = Identifier.of(ProductiveSlimes.MODID, name);
        Registry.register(Registries.ITEM, itemID, item);
    }

    public static void initialize() {
        registerTierBlocks();
    }
}
