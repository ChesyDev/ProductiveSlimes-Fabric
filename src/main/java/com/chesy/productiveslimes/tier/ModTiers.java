package com.chesy.productiveslimes.tier;

import com.chesy.productiveslimes.ProductiveSlimes;
import com.chesy.productiveslimes.block.custom.SlimeBlock;
import com.chesy.productiveslimes.entity.BaseSlime;
import com.chesy.productiveslimes.item.custom.BucketItem;
import com.chesy.productiveslimes.item.custom.DnaItem;
import com.chesy.productiveslimes.item.custom.SlimeballItem;
import com.chesy.productiveslimes.item.custom.SpawnEggItem;
import net.minecraft.block.FluidBlock;
import net.minecraft.entity.EntityType;
import net.minecraft.fluid.FlowableFluid;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ModTiers {
    public static final Map<Tier , ModTier> TIERS = new HashMap<>();
    private static final Map<String , ModTier> REGISTERED_TIERS = new HashMap<>();
    private static final Map<Identifier, SlimeBlock> registeredBlock = new HashMap<>();
    private static final Map<Identifier, SlimeballItem> registeredSlimeballItem = new HashMap<>();
    private static final Map<Identifier, DnaItem> registeredDnaItem = new HashMap<>();
    private static final Map<Identifier, SpawnEggItem> registeredSpawnEggItem = new HashMap<>();
    private static final Map<Identifier, EntityType<BaseSlime>> registeredSlimes = new HashMap<>();
    private static final Map<Identifier, FluidBlock> registeredLiquidBlock = new HashMap<>();
    private static final Map<Identifier, BucketItem> registeredBucketItem = new HashMap<>();
    private static final Map<Identifier, FlowableFluid> registeredSource = new HashMap<>();
    private static final Map<Identifier, FlowableFluid> registeredFlow = new HashMap<>();

    public static void init(){
        TIERS.put(Tier.DIRT, new ModTier("dirt", 0xFF866043, 10, 1500, "minecraft:dirt", "minecraft:dirt", 2, "minecraft:dirt", "productiveslimes:slime_dna", "productiveslimes:slime_dna", 0.75f));
        TIERS.put(Tier.STONE, new ModTier("stone", 0xFF4a4545, 11, 1700, "minecraft:stone", "minecraft:stone", 2, "minecraft:stone", "productiveslimes:dirt_slime_dna", "productiveslimes:dirt_slime_dna", 0.7f));
        TIERS.put(Tier.IRON, new ModTier("iron", 0xFF898c8a, 2, 3000, "minecraft:iron_block", "minecraft:iron_ingot", 1, "minecraft:iron_block", "productiveslimes:copper_slime_dna", "productiveslimes:copper_slime_dna", 0.6f));
        TIERS.put(Tier.COPPER, new ModTier("copper", 0xFF6a3e15, 26, 2500, "minecraft:copper_block", "minecraft:copper_ingot", 1, "minecraft:copper_block", "productiveslimes:coal_slime_dna", "productiveslimes:coal_slime_dna", 0.6f));
        TIERS.put(Tier.GOLD, new ModTier("gold", 0xFFa5953f, 30, 3200, "minecraft:gold_block", "minecraft:gold_ingot", 1, "minecraft:gold_block", "productiveslimes:iron_slime_dna", "productiveslimes:iron_slime_dna", 0.5f));
        TIERS.put(Tier.DIAMOND, new ModTier("diamond", 0xFF178f9c, 31, 4000, "minecraft:diamond_block", "minecraft:diamond", 1, "minecraft:diamond_block", "productiveslimes:gold_slime_dna", "productiveslimes:gold_slime_dna", 0.4f));
        TIERS.put(Tier.NETHERITE, new ModTier("netherite", 0xFF4c2b2b, 26, 5000, "minecraft:netherite_block", "minecraft:netherite_ingot", 1, "minecraft:netherite_block", "productiveslimes:diamond_slime_dna", "productiveslimes:diamond_slime_dna", 0.3f));
        TIERS.put(Tier.LAPIS, new ModTier("lapis", 0xFF1c41ba, 32, 2500, "minecraft:lapis_block", "minecraft:lapis_lazuli", 2, "minecraft:lapis_block", "productiveslimes:iron_slime_dna", "productiveslimes:iron_slime_dna", 0.6f));
        TIERS.put(Tier.REDSTONE, new ModTier("redstone", 0xFFa10505, 28, 2700, "minecraft:redstone_block", "minecraft:redstone", 2, "minecraft:redstone_block", "productiveslimes:gold_slime_dna", "productiveslimes:gold_slime_dna", 0.6f));
        TIERS.put(Tier.OAK, new ModTier("oak", 0xFFa69d6f, 26, 1500, "minecraft:oak_log", "minecraft:oak_log", 2, "minecraft:oak_planks", "productiveslimes:dirt_slime_dna", "productiveslimes:dirt_slime_dna", 0.7f));
        TIERS.put(Tier.SAND, new ModTier("sand", 0xFFf7f7c6, 2, 1500, "minecraft:sand", "minecraft:sand", 2, "minecraft:sand", "productiveslimes:dirt_slime_dna", "productiveslimes:dirt_slime_dna", 0.7f));
        TIERS.put(Tier.ANDESITE, new ModTier("andesite", 0xFF9d9e9a, 11, 1500, "minecraft:andesite", "minecraft:andesite", 2, "minecraft:andesite", "productiveslimes:stone_slime_dna", "productiveslimes:stone_slime_dna", 0.7f));
        TIERS.put(Tier.SNOW, new ModTier("snow", 0xFFf2fcfc, 8, 1800, "minecraft:snow_block", "minecraft:snow_block", 2, "minecraft:snow", "productiveslimes:slime_dna", "productiveslimes:slime_dna", 0.65f));
        TIERS.put(Tier.ICE, new ModTier("ice", 0xFF89b1fc, 5, 1800, "minecraft:ice", "minecraft:ice", 2, "minecraft:ice", "productiveslimes:snow_slime_dna", "productiveslimes:snow_slime_dna", 0.6f));
        TIERS.put(Tier.MUD, new ModTier("mud", 0xFF363339, 10, 1500, "minecraft:mud", "minecraft:mud", 2, "minecraft:mud", "productiveslimes:dirt_slime_dna", "productiveslimes:dirt_slime_dna", 0.8f));
        TIERS.put(Tier.CLAY, new ModTier("clay", 0xFF9ca2ac, 9, 1500, "minecraft:clay", "minecraft:clay", 2, "minecraft:clay", "productiveslimes:mud_slime_dna", "productiveslimes:mud_slime_dna", 0.75f));
        TIERS.put(Tier.RED_SAND, new ModTier("red_sand", 0xFFbb6520, 28, 1500, "minecraft:red_sand", "minecraft:red_sand", 2, "minecraft:red_sand", "productiveslimes:sand_slime_dna", "productiveslimes:sand_slime_dna", 0.7f));
        TIERS.put(Tier.MOSS, new ModTier("moss", 0xFF4a6029, 1, 1500, "minecraft:moss_block", "minecraft:moss_block", 2, "minecraft:moss_block", "productiveslimes:dirt_slime_dna", "productiveslimes:dirt_slime_dna", 0.7f));
        TIERS.put(Tier.DEEPSLATE, new ModTier("deepslate", 0xFF3c3c42, 59, 1500, "minecraft:deepslate", "minecraft:deepslate", 2, "minecraft:deepslate", "productiveslimes:stone_slime_dna", "productiveslimes:stone_slime_dna", 0.7f));
        TIERS.put(Tier.GRANITE, new ModTier("granite", 0xFF835949, 28, 1500, "minecraft:granite", "minecraft:granite", 2, "minecraft:granite", "productiveslimes:stone_slime_dna", "productiveslimes:stone_slime_dna", 0.7f));
        TIERS.put(Tier.DIORITE, new ModTier("diorite", 0xFFadacad, 36, 1500, "minecraft:diorite", "minecraft:diorite", 2, "minecraft:diorite", "productiveslimes:stone_slime_dna", "productiveslimes:stone_slime_dna", 0.7f));
        TIERS.put(Tier.CALCITE, new ModTier("calcite", 0xFFe9e9e3, 36, 1500, "minecraft:calcite", "minecraft:calcite", 2, "minecraft:calcite", "productiveslimes:stone_slime_dna", "productiveslimes:stone_slime_dna", 0.7f));
        TIERS.put(Tier.TUFF, new ModTier("tuff", 0xFF55564c, 11, 1500, "minecraft:tuff", "minecraft:tuff", 2, "minecraft:tuff", "productiveslimes:stone_slime_dna", "productiveslimes:stone_slime_dna", 0.7f));
        TIERS.put(Tier.DRIPSTONE, new ModTier("dripstone", 0xFF806155, 26, 1500, "minecraft:dripstone_block", "minecraft:dripstone_block", 2, "minecraft:dripstone_block", "productiveslimes:stone_slime_dna", "productiveslimes:stone_slime_dna", 0.6f));
        TIERS.put(Tier.NETHERRACK, new ModTier("netherrack", 0xFF763535, 35, 1500, "minecraft:netherrack", "minecraft:netherrack", 2, "minecraft:netherrack", "productiveslimes:stone_slime_dna", "productiveslimes:stone_slime_dna", 0.7f));
        TIERS.put(Tier.PRISMARINE, new ModTier("prismarine", 0xFF529584, 17, 3000, "minecraft:prismarine_shard", "minecraft:prismarine_shard", 2, "minecraft:prismarine_shard", "productiveslimes:sand_slime_dna", "productiveslimes:sand_slime_dna", 0.5f));
        TIERS.put(Tier.MAGMA, new ModTier("magma", 0xFF561f1f, 4, 2500, "minecraft:magma_block", "minecraft:magma_block", 2, "minecraft:magma_block", "productiveslimes:netherite_slime_dna", "productiveslimes:netherite_slime_dna", 0.5f));
        TIERS.put(Tier.OBSIDIAN, new ModTier("obsidian", 0xFF030106, 29, 3500, "minecraft:obsidian", "minecraft:obsidian", 2, "minecraft:obsidian", "productiveslimes:deepslate_slime_dna", "productiveslimes:deepslate_slime_dna", 0.45f));
        TIERS.put(Tier.SOUL_SAND, new ModTier("soul_sand", 0xFF413127, 26, 2000, "minecraft:soul_sand", "minecraft:soul_sand", 2, "minecraft:soul_sand", "productiveslimes:sand_slime_dna", "productiveslimes:netherrack_slime_dna", 0.7f));
        TIERS.put(Tier.SOUL_SOIL, new ModTier("soul_soil", 0xFF392b23, 26, 2000, "minecraft:soul_soil", "minecraft:soul_soil", 2, "minecraft:soul_soil", "productiveslimes:dirt_slime_dna", "productiveslimes:netherrack_slime_dna", 0.7f));
        TIERS.put(Tier.BLACKSTONE, new ModTier("blackstone", 0xFF201819, 59, 1500, "minecraft:blackstone", "minecraft:blackstone", 2, "minecraft:blackstone", "productiveslimes:stone_slime_dna", "productiveslimes:netherrack_slime_dna", 0.7f));
        TIERS.put(Tier.BASALT, new ModTier("basalt", 0xFF565456, 59, 1500, "minecraft:basalt", "minecraft:basalt", 2, "minecraft:basalt", "productiveslimes:stone_slime_dna", "productiveslimes:netherrack_slime_dna", 0.7f));
        TIERS.put(Tier.QUARTZ, new ModTier("quartz", 0xFFe4ddd3, 14, 3200, "minecraft:quartz_block", "minecraft:quartz_block", 2, "minecraft:quartz", "productiveslimes:iron_slime_dna", "productiveslimes:netherrack_slime_dna", 0.55f));
        TIERS.put(Tier.GLOWSTONE, new ModTier("glowstone", 0xFF784e27, 18, 3000, "minecraft:glowstone", "minecraft:glowstone", 2, "minecraft:glowstone_dust", "productiveslimes:gold_slime_dna", "productiveslimes:netherrack_slime_dna", 0.5f));
        TIERS.put(Tier.END_STONE, new ModTier("end_stone", 0xFFcece8e, 2, 2000, "minecraft:end_stone", "minecraft:end_stone", 2, "minecraft:end_stone", "productiveslimes:deepslate_slime_dna", "productiveslimes:netherrack_slime_dna", 0.6f));
        TIERS.put(Tier.AMETHYST, new ModTier("amethyst", 0xFF6b4da5, 20, 3000, "minecraft:amethyst_shard", "minecraft:amethyst_shard", 2, "minecraft:amethyst_shard", "productiveslimes:calcite_slime_dna", "productiveslimes:glowstone_slime_dna", 0.4f));
        TIERS.put(Tier.BROWN_MUSHROOM, new ModTier("brown_mushroom", 0xFF967251, 26, 3500, "minecraft:brown_mushroom_block", "minecraft:brown_mushroom", 2, "minecraft:brown_mushroom", "productiveslimes:mud_slime_dna", "productiveslimes:cactus_slime_dna", 0.3f));
        TIERS.put(Tier.RED_MUSHROOM, new ModTier("red_mushroom", 0xFFc02624, 28, 3500, "minecraft:red_mushroom_block", "minecraft:red_mushroom", 2, "minecraft:red_mushroom", "productiveslimes:mud_slime_dna", "productiveslimes:cactus_slime_dna", 0.3f));
        TIERS.put(Tier.CACTUS, new ModTier("cactus", 0xFF476d21, 27, 2000, "minecraft:cactus", "minecraft:cactus", 2, "minecraft:cactus", "productiveslimes:sand_slime_dna", "productiveslimes:slime_dna", 0.6f));
        TIERS.put(Tier.COAL, new ModTier("coal", 0xFF3b3d3b, 29, 1800, "minecraft:coal_block", "minecraft:coal", 2, "minecraft:coal_block", "productiveslimes:stone_slime_dna", "productiveslimes:stone_slime_dna", 0.65f));
        TIERS.put(Tier.GRAVEL, new ModTier("gravel", 0xFF4a444b, 21, 1500, "minecraft:gravel", "minecraft:gravel", 2, "minecraft:gravel", "productiveslimes:sand_slime_dna", "productiveslimes:stone_slime_dna", 0.6f));
        TIERS.put(Tier.OAK_LEAVES, new ModTier("oak_leaves", 0xFF48b518, 27, 1500, "minecraft:oak_leaves", "minecraft:oak_leaves", 2, "minecraft:oak_leaves", "productiveslimes:dirt_slime_dna", "productiveslimes:slime_dna", 0.7f));

        TIERS.forEach((tier, modTiers) -> REGISTERED_TIERS.put(tier.toString(), modTiers));
    }

    public static void addRegisteredBlock(String name, SlimeBlock block){
        registeredBlock.put(Identifier.of(ProductiveSlimes.MODID, name + "_slime_block"), block);
    }

    public static void addRegisteredSlimeballItem(String name, SlimeballItem item){
        registeredSlimeballItem.put(Identifier.of(ProductiveSlimes.MODID, name + "_slimeball"), item);
    }

    public static void addRegisteredDnaItem(String name, DnaItem item) {
        registeredDnaItem.put(Identifier.of(ProductiveSlimes.MODID, name + "_slime_dna"), item);
    }

    public static void addRegisteredSpawnEggItem(String name, SpawnEggItem item) {
        registeredSpawnEggItem.put(Identifier.of(ProductiveSlimes.MODID, name + "_slime_spawn_egg"), item);
    }

    public static void addRegisteredSlime(String name, EntityType<BaseSlime> slime) {
        registeredSlimes.put(Identifier.of(ProductiveSlimes.MODID, name + "_slime"), slime);
    }

    public static void addRegisteredLiquidBlock(String name, FluidBlock liquidBlock){
        registeredLiquidBlock.put(Identifier.of(ProductiveSlimes.MODID, "molten_" + name + "_block"), liquidBlock);
    }

    public static void addRegisteredBucketItem(String name, BucketItem bucketItem){
        registeredBucketItem.put(Identifier.of(ProductiveSlimes.MODID, "molten_" + name + "_bucket"), bucketItem);
    }

    public static void addRegisteredSource(String name, FlowableFluid source){
        registeredSource.put(Identifier.of(ProductiveSlimes.MODID, "source_molten_" + name), source);
    }

    public static void addRegisteredFlow(String name, FlowableFluid flow){
        registeredFlow.put(Identifier.of(ProductiveSlimes.MODID, "flowing_molten_" + name), flow);
    }

    public static ModTier getTierByName(Tier tier){
        return TIERS.get(tier);
    }

    public static SlimeBlock getBlockByName(String name){
        return registeredBlock.get(Identifier.of(ProductiveSlimes.MODID, name + "_slime_block"));
    }

    public static SlimeballItem getSlimeballItemByName(String name){
        return registeredSlimeballItem.get(Identifier.of(ProductiveSlimes.MODID, name + "_slimeball"));
    }

    public static DnaItem getDnaItemByName(String name){
        return registeredDnaItem.get(Identifier.of(ProductiveSlimes.MODID, name + "_slime_dna"));
    }

    public static SpawnEggItem getSpawnEggItemByName(String name){
        return registeredSpawnEggItem.get(Identifier.of(ProductiveSlimes.MODID, name + "_slime_spawn_egg"));
    }

    public static EntityType<BaseSlime> getEntityByName(String name){
        return registeredSlimes.get(Identifier.of(ProductiveSlimes.MODID, name + "_slime"));
    }

    public static FluidBlock getLiquidBlockByName(String name){
        return registeredLiquidBlock.get(Identifier.of(ProductiveSlimes.MODID, "molten_" + name + "_block"));
    }

    public static BucketItem getBucketItemByName(String name){
        return registeredBucketItem.get(Identifier.of(ProductiveSlimes.MODID, "molten_" + name + "_bucket"));
    }

    public static FlowableFluid getSourceByName(String name){
        return registeredSource.get(Identifier.of(ProductiveSlimes.MODID, "source_molten_" + name));
    }

    public static FlowableFluid getFlowByName(String name){
        return registeredFlow.get(Identifier.of(ProductiveSlimes.MODID, "flowing_molten_" + name));
    }

    public static void addRegisteredTier(String key, ModTier value){
        REGISTERED_TIERS.put(key, value);
    }
    public static List<ModTier> getRegisteredTiers(){
        return REGISTERED_TIERS.values().stream().sorted(Comparator.comparing(ModTier::name)).toList();
    }

    public static Item getItemByKey(String key){
        return Registries.ITEM.get(new Identifier(key));
    }
}
