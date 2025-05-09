package com.chesy.productiveslimes.config;

import com.chesy.productiveslimes.ProductiveSlimes;
import com.chesy.productiveslimes.block.custom.SlimeBlock;
import com.chesy.productiveslimes.config.fluid.CustomDynamicFluid;
import com.chesy.productiveslimes.entity.BaseSlime;
import com.chesy.productiveslimes.item.custom.BucketItem;
import com.chesy.productiveslimes.item.custom.DnaItem;
import com.chesy.productiveslimes.item.custom.SlimeballItem;
import com.chesy.productiveslimes.item.custom.SpawnEggItem;
import com.chesy.productiveslimes.tier.ModTier;
import com.chesy.productiveslimes.tier.ModTiers;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.mojang.logging.LogUtils;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.FluidBlock;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.fluid.FlowableFluid;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.ColorHelper;
import org.slf4j.Logger;

import java.io.*;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

public class CustomVariantRegistry {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final String CONFIG_PATH = "config/productiveslimes/variants.json";
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    private static List<CustomVariant> loadedVariants = new ArrayList<>();

    private static final Map<Identifier, Item> registeredItems = new HashMap<>();
    private static final Map<Identifier, Item> registeredDnaItems = new HashMap<>();
    private static final Map<Identifier, Item> registeredSpawnEggItems = new HashMap<>();
    private static final Map<Identifier, Block> registeredBlocks = new HashMap<>();
    private static final Map<Identifier, EntityType<BaseSlime>> registeredSlimes = new HashMap<>();
    private static final Map<Identifier, FluidBlock> registeredLiquidBlock = new HashMap<>();
    private static final Map<Identifier, BucketItem> registeredBucketItem = new HashMap<>();
    private static final Map<Identifier, FlowableFluid> registeredSource = new HashMap<>();
    private static final Map<Identifier, FlowableFluid> registeredFlow = new HashMap<>();
    public static Map<String, byte[]> resourceData = new HashMap<>();
    public static Map<String, byte[]> dataPackResources = new HashMap<>();

    public static void initialize(){
        createDefaultConfig();
        loadVariants();

        generateResourcePackInMemory();
        generateDataPackInMemory();
    }

    public static List<CustomVariant> getLoadedTiers() {
        return loadedVariants;
    }

    public static Item getSlimeballItemForVariant(String variantName){
        return registeredItems.get(Identifier.of(ProductiveSlimes.MODID, variantName + "_slimeball"));
    }

    public static Item getDnaItemForVariant(String variantName){
        return registeredDnaItems.get(Identifier.of(ProductiveSlimes.MODID, variantName + "_slime_dna"));
    }

    public static Item getSpawnEggItemForVariant(String variantName){
        return registeredSpawnEggItems.get(Identifier.of(ProductiveSlimes.MODID, variantName + "_slime_spawn_egg"));
    }

    public static Block getSlimeBlockForVariant(String variantName){
        return registeredBlocks.get(Identifier.of(ProductiveSlimes.MODID, variantName + "_slime_block"));
    }

    public static EntityType<BaseSlime> getSlimeForVariant(String variantName){
        return registeredSlimes.get(Identifier.of(ProductiveSlimes.MODID, variantName + "_slime"));
    }

    public static FlowableFluid getSourceFluidForVariant(String variantName){
        return registeredSource.get(Identifier.of(ProductiveSlimes.MODID, "still_" + variantName));
    }

    public static FlowableFluid getFlowingFluidForVariant(String variantName){
        return registeredFlow.get(Identifier.of(ProductiveSlimes.MODID, "flowing_" + variantName));
    }

    public static FluidBlock getLiquidBlockForVariant(String variantName){
        return registeredLiquidBlock.get(Identifier.of(ProductiveSlimes.MODID, "molten_" + variantName + "_block"));
    }

    public static BucketItem getBucketItemForVariant(String variantName){
        return registeredBucketItem.get(Identifier.of(ProductiveSlimes.MODID, "molten_" + variantName + "_bucket"));
    }

    private static void createDefaultConfig() {
        File configFile = new File(CONFIG_PATH);
        if (!configFile.exists()) {
            List<CustomVariant> defaultTiers = List.of(
                    new CustomVariant("birch", "#FFa69d6f", 5, 1500, "minecraft:birch_log", 250 * 81,"minecraft:birch_log", "productiveslimes:oak_slime_dna", "productiveslimes:oak_slime_dna", "minecraft:birch_log", 0.75)
            );

            try {
                configFile.getParentFile().mkdirs();

                try (FileWriter writer = new FileWriter(configFile)) {
                    GSON.toJson(defaultTiers, writer);
                }
            } catch (IOException e) {
                LOGGER.error("Failed to create default tier config", e);
            }
        }
    }

    private static void loadVariants() {
        File configFile = new File(CONFIG_PATH);
        if (configFile.exists()) {
            try (FileReader reader = new FileReader(configFile)) {
                Type listType = new TypeToken<List<CustomVariant>>(){}.getType();
                List<CustomVariant> tiers = GSON.fromJson(reader, listType);
                loadedVariants = validateTiers(tiers);

                for (CustomVariant variant : loadedVariants){
                    registerSlimeballItem(variant);
                    registerDnaItem(variant);
                    registerSlimeBlock(variant);
                    registerSlime(variant);
                    registerSpawnEggItem(variant);
                    registerFluid(variant);

                    ModTier registerTier = new ModTier(variant.name(), variant.getColor(), variant.mapColorId(), variant.cooldown(), variant.growthItem(), variant.solidingInputAmount(), variant.solidingOutput(), variant.synthesizingInputItem(), variant.synthesizingInputDna1(), variant.synthesizingInputDna2(), (float) variant.dnaOutputChance());
                    ModTiers.addRegisteredTier(variant.name(), registerTier);
                }

                LOGGER.info("Loaded " + loadedVariants.size() + " custom tiers");
            } catch (IOException e) {
                LOGGER.error("Failed to load tier config", e);
            }
        }
    }

    private static List<CustomVariant> validateTiers(List<CustomVariant> tiers) {
        return tiers.stream()
                .filter(tier -> {
                    // Validate name (no spaces, special characters, etc.)
                    if (!tier.name().matches("^[a-z0-9_]+$")) {
                        LOGGER.error("Invalid name format for tier: " + tier.name());
                        return false;
                    }
                    return true;
                })
                .collect(Collectors.toList());
    }

    private static void registerSlimeballItem(CustomVariant variant){
        String itemName = variant.name() + "_slimeball";
        Identifier itemId = Identifier.of(ProductiveSlimes.MODID, itemName);
        RegistryKey<Item> key = RegistryKey.of(RegistryKeys.ITEM, itemId);
        Item item = Registry.register(Registries.ITEM, itemId, new SlimeballItem(variant.getColor(), new Item.Settings().registryKey(key)));

        registeredItems.put(itemId, item);
    }

    private static void registerDnaItem(CustomVariant variant){
        String itemName = variant.name() + "_slime_dna";
        Identifier itemId = Identifier.of(ProductiveSlimes.MODID, itemName);
        RegistryKey<Item> key = RegistryKey.of(RegistryKeys.ITEM, itemId);
        Item item = Registry.register(Registries.ITEM, itemId, new DnaItem(variant.getColor(), new Item.Settings().registryKey(key)));

        registeredDnaItems.put(itemId, item);
    }

    private static void registerSlimeBlock(CustomVariant variant){
        String blockName = variant.name() + "_slime_block";
        Identifier blockId = Identifier.of(ProductiveSlimes.MODID, blockName);
        RegistryKey<Block> key = RegistryKey.of(RegistryKeys.BLOCK, blockId);
        RegistryKey<Item> itemKey = RegistryKey.of(RegistryKeys.ITEM, blockId);
        Block block = Registry.register(Registries.BLOCK, blockId, new SlimeBlock(AbstractBlock.Settings.copy(Blocks.SLIME_BLOCK).registryKey(key), variant.getColor()));
        Registry.register(Registries.ITEM, blockId, new BlockItem(block, new Item.Settings().useBlockPrefixedTranslationKey().registryKey(itemKey)));

        registeredBlocks.put(blockId, block);
    }

    private static void registerSlime(CustomVariant variant){
        String slimeName = variant.name() + "_slime";
        Identifier slimeId = Identifier.of(ProductiveSlimes.MODID, slimeName);

        EntityType<BaseSlime> slime = Registry.register(Registries.ENTITY_TYPE, slimeId, EntityType.Builder.<BaseSlime>create(
                (pEntityType, pLevel) -> new BaseSlime(pEntityType, pLevel, variant.cooldown(), variant.getColor(), getSlimeballItemForVariant(variant.name()), Registries.ITEM.get(Identifier.of(variant.growthItem()))),
                SpawnGroup.CREATURE).build(RegistryKey.of(RegistryKeys.ENTITY_TYPE, slimeId)));

        registeredSlimes.put(slimeId, slime);
    }

    private static void registerSpawnEggItem(CustomVariant variant){
        String itemName = variant.name() + "_slime_spawn_egg";
        Identifier itemId = Identifier.of(ProductiveSlimes.MODID, itemName);
        Item item = Registry.register(Registries.ITEM, itemId, new SpawnEggItem(getSlimeForVariant(variant.name()), variant.getColor(), new Item.Settings().registryKey(RegistryKey.of(RegistryKeys.ITEM, itemId))));

        registeredSpawnEggItems.put(itemId, item);
    }

    private static void registerFluid(CustomVariant variants) {
        FlowableFluid STILL_DYNAMIC_FLUID = Registry.register(Registries.FLUID,
                Identifier.of(ProductiveSlimes.MODID, "still_" + variants.name()), new CustomDynamicFluid.Still(variants.name()));
        FlowableFluid FLOWING_DYNAMIC_FLUID = Registry.register(Registries.FLUID,
                Identifier.of(ProductiveSlimes.MODID, "flowing_"  + variants.name()), new CustomDynamicFluid.Flowing(variants.name()));

        registeredSource.put(Identifier.of(ProductiveSlimes.MODID, "still_" + variants.name()), STILL_DYNAMIC_FLUID);
        registeredFlow.put(Identifier.of(ProductiveSlimes.MODID, "flowing_"  + variants.name()), FLOWING_DYNAMIC_FLUID);

        FluidBlock DYNAMIC_FLUID_BLOCK = Registry.register(Registries.BLOCK, Identifier.of(ProductiveSlimes.MODID, "molten_" + variants.name() + "_block"),
                new FluidBlock(getSourceFluidForVariant(variants.name()), AbstractBlock.Settings.copy(Blocks.WATER).registryKey(RegistryKey.of(RegistryKeys.BLOCK, Identifier.of(ProductiveSlimes.MODID, "molten_" + variants.name() + "_block")))) {
                });
        BucketItem DYNAMIC_FLUID_BUCKET = Registry.register(Registries.ITEM, Identifier.of(ProductiveSlimes.MODID, "molten_" + variants.name() + "_bucket"),
                new BucketItem(getSourceFluidForVariant(variants.name()), new Item.Settings().maxCount(64).registryKey(RegistryKey.of(RegistryKeys.ITEM, Identifier.of(ProductiveSlimes.MODID, "molten_" + variants.name() + "_bucket"))), variants.getColor()));

        registeredLiquidBlock.put(Identifier.of(ProductiveSlimes.MODID, "molten_" + variants.name() + "_block"), DYNAMIC_FLUID_BLOCK);
        registeredBucketItem.put(Identifier.of(ProductiveSlimes.MODID, "molten_" + variants.name() + "_bucket"), DYNAMIC_FLUID_BUCKET);
    }

    private static void generateResourcePackInMemory(){
        Map<String, String> langJson = new HashMap<>();

        // Prepare pack.mcmeta content
        String packMcmetaContent = "{\n" +
                "  \"pack\": {\n" +
                "    \"pack_format\": 48,\n" +
                "    \"description\": \"Custom variants resources\"\n" +
                "  }\n" +
                "}";

        resourceData.put("pack.mcmeta", packMcmetaContent.getBytes(StandardCharsets.UTF_8));

        for (CustomVariant variants : getLoadedTiers()){
            String id = variants.name() + "_slime_block";


            String blockstatePath = "assets/productiveslimes/blockstates/" + id + ".json";
            String moltenBlockstatePath = "assets/productiveslimes/blockstates/molten_" + variants.name() + "_block.json";
            String modelPath = "assets/productiveslimes/models/block/" + id + ".json";
            String bucketModelPath = "assets/productiveslimes/models/item/molten_" + variants.name() + "_bucket.json";
            String slimeBlockModelPath = "assets/productiveslimes/models/item/" + variants.name() + "_slime_block.json";
            String dnaModelPath = "assets/productiveslimes/models/item/" + variants.name() + "_slime_dna.json";
            String slimeballModelPath = "assets/productiveslimes/models/item/" + variants.name() + "_slimeball.json";

            String itemsBucketPath = "assets/productiveslimes/items/molten_" + variants.name() + "_bucket.json";
            String itemsSlimeBlockPath = "assets/productiveslimes/items/" + variants.name() + "_slime_block.json";
            String itemsDnaPath = "assets/productiveslimes/items/" + variants.name() + "_slime_dna.json";
            String itemsSpawnEggPath = "assets/productiveslimes/items/" + variants.name() + "_slime_spawn_egg.json";
            String itemsSlimeballPath = "assets/productiveslimes/items/" + variants.name() + "_slimeball.json";

            // Generate formatted name
            String formattedName = Arrays.stream(variants.name().split("_")).map(word -> word.substring(0, 1).toUpperCase() + word.substring(1)).collect(Collectors.joining(" "));

            langJson.put("block.productiveslimes." + variants.name() + "_slime_block", formattedName + " Slime Block");
            langJson.put("item.productiveslimes." + variants.name()  + "_slime_spawn_egg", formattedName + " Slime Spawn Egg");
            langJson.put("item.productiveslimes." + variants.name()  + "_slimeball", formattedName + " Slimeball");
            langJson.put("item.productiveslimes." + variants.name()  + "_slime_dna", formattedName + " Slime DNA");
            langJson.put("entity.productiveslimes." + variants.name()  + "_slime", formattedName + " Slime");
            langJson.put("block.productiveslimes." + "molten_" + variants.name() + "_block", "Molten " + formattedName);
            langJson.put("item.productiveslimes." + "molten_" + variants.name() + "_bucket", "Molten " + formattedName + " Bucket");
            langJson.put("fluid_type.productiveslimes." + variants.name(), "Molten " + formattedName);

            String blockstateContent = "{\n" +
                    "  \"variants\": {\n" +
                    "    \"\": {\n" +
                    "      \"model\": \"productiveslimes:block/"+ id + "\"\n" +
                    "    }\n" +
                    "  }\n" +
                    "}";

            String moltenBlockstateContent = "{\n" +
                    "  \"variants\": {\n" +
                    "    \"\": {\n" +
                    "      \"model\": \"minecraft:block/water\"\n" +
                    "    }\n" +
                    "  }\n" +
                    "}";

            String bucketModelContent = "{\n" +
                    "  \"parent\": \"minecraft:item/generated\",\n" +
                    "  \"textures\": {\n" +
                    "    \"layer0\": \"productiveslimes:item/bucket\",\n" +
                    "    \"layer1\": \"productiveslimes:item/bucket_fluid\"\n" +
                    "  }\n" +
                    "}";

            String dnaModelContent = "{\n" +
                    "  \"parent\": \"minecraft:item/generated\",\n" +
                    "  \"textures\": {\n" +
                    "    \"layer0\": \"productiveslimes:item/template_dna\"\n" +
                    "  }\n" +
                    "}";

            String slimeballModelContent = "{\n" +
                    "  \"parent\": \"minecraft:item/generated\",\n" +
                    "  \"textures\": {\n" +
                    "    \"layer0\": \"productiveslimes:item/template_slimeball\"\n" +
                    "  }\n" +
                    "}";

            String itemsBucketContent = "{\n" +
                    "  \"model\": {\n" +
                    "    \"type\": \"minecraft:model\",\n" +
                    "    \"model\": \"productiveslimes:item/molten_" + variants.name() + "_bucket\",\n" +
                    "    \"tints\": [\n" +
                    "      {\n" +
                    "        \"type\": \"minecraft:constant\",\n" +
                    "        \"value\": -1\n" +
                    "      },\n" +
                    "      {\n" +
                    "        \"type\": \"minecraft:constant\",\n" +
                    "        \"value\": " + ColorHelper.fullAlpha(variants.getColor()) + "\n" +
                    "      }\n" +
                    "    ]\n" +
                    "  }\n" +
                    "}";

            String itemsSlimeBlockContent = "{\n" +
                    "  \"model\": {\n" +
                    "    \"type\": \"minecraft:model\",\n" +
                    "    \"model\": \"productiveslimes:item/" + variants.name() + "_slime_block\",\n" +
                    "    \"tints\": [\n" +
                    "      {\n" +
                    "        \"type\": \"minecraft:constant\",\n" +
                    "        \"value\": " + ColorHelper.fullAlpha(variants.getColor()) + "\n" +
                    "      }\n" +
                    "    ]\n" +
                    "  }\n" +
                    "}";

            String itemsDnaContent = "{\n" +
                    "  \"model\": {\n" +
                    "    \"type\": \"minecraft:model\",\n" +
                    "    \"model\": \"productiveslimes:item/" + variants.name() + "_slime_dna\",\n" +
                    "    \"tints\": [\n" +
                    "      {\n" +
                    "        \"type\": \"minecraft:constant\",\n" +
                    "        \"value\": " + ColorHelper.fullAlpha(variants.getColor()) + "\n" +
                    "      }\n" +
                    "    ]\n" +
                    "  }\n" +
                    "}";

            String itemsSpawnEggContent = "{\n" +
                    "  \"model\": {\n" +
                    "    \"type\": \"minecraft:model\",\n" +
                    "    \"model\": \"productiveslimes:item/template_slime_spawn_egg\",\n" +
                    "    \"tints\": [\n" +
                    "      {\n" +
                    "        \"type\": \"minecraft:constant\",\n" +
                    "        \"value\": " + ColorHelper.fullAlpha(variants.getColor()) + "\n" +
                    "      },\n" +
                    "      {\n" +
                    "        \"type\": \"minecraft:constant\",\n" +
                    "        \"value\": " + ColorHelper.fullAlpha(variants.getColor()) + "\n" +
                    "      }\n" +
                    "    ]\n" +
                    "  }\n" +
                    "}";

            String itemsSlimeballContent = "{\n" +
                    "  \"model\": {\n" +
                    "    \"type\": \"minecraft:model\",\n" +
                    "    \"model\": \"productiveslimes:item/" + variants.name() + "_slimeball\",\n" +
                    "    \"tints\": [\n" +
                    "      {\n" +
                    "        \"type\": \"minecraft:constant\",\n" +
                    "        \"value\": " + ColorHelper.fullAlpha(variants.getColor()) + "\n" +
                    "      }\n" +
                    "    ]\n" +
                    "  }\n" +
                    "}";

            String blockModelContent = "{\n" +
                    "  \"parent\": \"productiveslimes:block/template_slime_block\"\n" +
                    "}";

            String slimeBlockModelContent = "{\n" +
                    "  \"parent\": \"productiveslimes:block/template_slime_block\"\n" +
                    "}";

            resourceData.put(blockstatePath, blockstateContent.getBytes(StandardCharsets.UTF_8));
            resourceData.put(moltenBlockstatePath, moltenBlockstateContent.getBytes(StandardCharsets.UTF_8));
            resourceData.put(modelPath, blockModelContent.getBytes(StandardCharsets.UTF_8));
            resourceData.put(bucketModelPath, bucketModelContent.getBytes(StandardCharsets.UTF_8));
            resourceData.put(slimeBlockModelPath, slimeBlockModelContent.getBytes(StandardCharsets.UTF_8));
            resourceData.put(dnaModelPath, dnaModelContent.getBytes(StandardCharsets.UTF_8));
            resourceData.put(slimeballModelPath, slimeballModelContent.getBytes(StandardCharsets.UTF_8));
            resourceData.put(itemsBucketPath, itemsBucketContent.getBytes(StandardCharsets.UTF_8));
            resourceData.put(itemsSlimeBlockPath, itemsSlimeBlockContent.getBytes(StandardCharsets.UTF_8));
            resourceData.put(itemsDnaPath, itemsDnaContent.getBytes(StandardCharsets.UTF_8));
            resourceData.put(itemsSpawnEggPath, itemsSpawnEggContent.getBytes(StandardCharsets.UTF_8));
            resourceData.put(itemsSlimeballPath, itemsSlimeballContent.getBytes(StandardCharsets.UTF_8));
        }

        // Convert langJson map to JSON string
        String langJsonContent = new GsonBuilder().setPrettyPrinting().create().toJson(langJson);

        // Add language file to resource data
        String langFilePath = "assets/productiveslimes/lang/en_us.json";
        resourceData.put(langFilePath, langJsonContent.getBytes(StandardCharsets.UTF_8));
    }

    private static void generateDataPackInMemory() {
        dataPackResources.clear();
        addPackMcmeta();
        generateSlimeballTag();
        generateWaterTag();
        generateDnaTag();
        generateSlimeBlockLootTable();
        generateCraftingRecipe();
        generateModRecipe();
    }

    private static void addPackMcmeta() {
        String packMcmetaContent = "{\n" +
                "  \"pack\": {\n" +
                "    \"pack_format\": 57,\n" + // Adjust pack_format according to Minecraft version
                "    \"description\": \"Productive Slimes Generated Data Pack\"\n" +
                "  }\n" +
                "}";
        dataPackResources.put("pack.mcmeta", packMcmetaContent.getBytes(StandardCharsets.UTF_8));
    }

    private static void generateWaterTag(){
        List<String> itemIds = new ArrayList<>();

        for (CustomVariant variants : getLoadedTiers()){
            itemIds.add("productiveslimes:still_" + variants.name());
            itemIds.add("productiveslimes:flowing_" + variants.name());
        }

        Map<String, Object> tagJson = new HashMap<>();
        tagJson.put("replace", false);
        tagJson.put("values", itemIds);

        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String jsonContent = gson.toJson(tagJson);

        String tagPath = "data/minecraft/tags/fluid/water.json";
        dataPackResources.put(tagPath, jsonContent.getBytes(StandardCharsets.UTF_8));
    }

    private static void generateSlimeballTag(){
        List<String> itemIds = new ArrayList<>();

        for (CustomVariant variants : getLoadedTiers()){
            itemIds.add("productiveslimes:" + variants.name() + "_slimeball");
        }

        Map<String, Object> tagJson = new HashMap<>();
        tagJson.put("replace", false);
        tagJson.put("values", itemIds);

        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String jsonContent = gson.toJson(tagJson);

        String tagPath = "data/c/tags/item/slime_balls.json";
        dataPackResources.put(tagPath, jsonContent.getBytes(StandardCharsets.UTF_8));
    }

    private static void generateDnaTag(){
        List<String> itemIds = new ArrayList<>();

        for (CustomVariant variants : getLoadedTiers()){
            itemIds.add("productiveslimes:" + variants.name() + "_slime_dna");
        }

        Map<String, Object> tagJson = new HashMap<>();
        tagJson.put("replace", false);
        tagJson.put("values", itemIds);

        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String jsonContent = gson.toJson(tagJson);

        String tagPath = "data/productiveslimes/tags/item/dna_item.json";
        dataPackResources.put(tagPath, jsonContent.getBytes(StandardCharsets.UTF_8));
    }

    private static void generateSlimeBlockLootTable(){
        for (CustomVariant variants : getLoadedTiers()){
            String lootTablePath = "data/productiveslimes/loot_table/blocks/" + variants.name() + "_slime_block.json";
            JsonObject lootTableObj = new JsonObject();
            lootTableObj.addProperty("type", "minecraft:block");

            JsonArray poolsArray = new JsonArray();
            JsonObject poolObj = new JsonObject();

            poolObj.addProperty("bonus_rolls", 0.0);

            JsonArray conditionsArray = new JsonArray();
            JsonObject conditionObj = new JsonObject();
            conditionObj.addProperty("condition", "minecraft:survives_explosion");
            conditionsArray.add(conditionObj);
            poolObj.add("conditions", conditionsArray);

            JsonArray entriesArray = new JsonArray();
            JsonObject entryObj = new JsonObject();
            entryObj.addProperty("type", "minecraft:item");
            entryObj.addProperty("name", "productiveslimes:" + variants.name() + "_slime_block");
            entriesArray.add(entryObj);
            poolObj.add("entries", entriesArray);

            poolObj.addProperty("rolls", 1.0);

            poolsArray.add(poolObj);
            lootTableObj.add("pools", poolsArray);

            lootTableObj.addProperty("random_sequence", "productiveslimes:blocks/" + variants.name() + "_slime_block");

            String lootTableJson = new GsonBuilder().setPrettyPrinting().create().toJson(lootTableObj);
            dataPackResources.put(lootTablePath, lootTableJson.getBytes(StandardCharsets.UTF_8));
        }
    }

    public static void generateCraftingRecipe(){
        for (CustomVariant variant : loadedVariants){
            slimeballToSlimeBlock(variant.name());
            slimeBlockToSlimeball(variant.name());
        }
    }

    private static void slimeballToSlimeBlock(String name){
        String recipePath = "data/minecraft/recipe/" + name + "_slimeball_to_block.json";

        JsonObject recipeObject = new JsonObject();
        recipeObject.addProperty("type", "minecraft:crafting_shaped");
        recipeObject.addProperty("category", "building");

        JsonObject keyObject = new JsonObject();
        keyObject.addProperty("A", "productiveslimes:" + name + "_slimeball");
        recipeObject.add("key", keyObject);

        JsonArray patternArray = new JsonArray();
        patternArray.add("AAA");
        patternArray.add("AAA");
        patternArray.add("AAA");
        recipeObject.add("pattern", patternArray);

        JsonObject resultObject = new JsonObject();
        resultObject.addProperty("count", 1);
        resultObject.addProperty("id", "productiveslimes:" + name + "_slime_block");
        recipeObject.add("result", resultObject);

        String recipeJson = new GsonBuilder().setPrettyPrinting().create().toJson(recipeObject);
        dataPackResources.put(recipePath, recipeJson.getBytes(StandardCharsets.UTF_8));
    }

    private static void slimeBlockToSlimeball(String name){
        String recipePath = "data/minecraft/recipe/" + name + "_slime_block_to_ball.json";

        JsonObject recipeObject = new JsonObject();
        recipeObject.addProperty("type", "minecraft:crafting_shapeless");
        recipeObject.addProperty("category", "misc");

        JsonArray ingredients = new JsonArray();
        ingredients.add("productiveslimes:" + name + "_slime_block");
        recipeObject.add("ingredients", ingredients);

        JsonObject result = new JsonObject();
        result.addProperty("count", 9);
        result.addProperty("id", "productiveslimes:" + name + "_slimeball");
        recipeObject.add("result", result);

        String recipeJson = new GsonBuilder().setPrettyPrinting().create().toJson(recipeObject);
        dataPackResources.put(recipePath, recipeJson.getBytes(StandardCharsets.UTF_8));
    }

    private static void generateModRecipe(){
        for (CustomVariant variant : loadedVariants){
            meltingRecipeBlock(variant.name());
            meltingRecipeBall(variant.name());
            solidingRecipe(variant);
            dnaExtracting(variant);
            dnaSynthesizingSelf(variant);
            dnaSynthesizing(variant);
        }
    }

    private static void meltingRecipeBlock(String name){
        String recipePath = "data/productiveslimes/recipe/melting/" + name + "_slime_block_melting.json";

        JsonObject recipeObj = new JsonObject();
        recipeObj.addProperty("type", "productiveslimes:melting");
        recipeObj.addProperty("energy", 200);

        JsonObject ingredientsObj = new JsonObject();
        ingredientsObj.addProperty("count", 1);
        ingredientsObj.addProperty("ingredient", "productiveslimes:" + name + "_slime_block");
        recipeObj.add("ingredients", ingredientsObj);

        JsonObject outputObj = new JsonObject();
        outputObj.addProperty("amount", 182250);
        outputObj.addProperty("id", "productiveslimes:still_" + name);
        recipeObj.add("output", outputObj);

        String recipeJson = new GsonBuilder().setPrettyPrinting().create().toJson(recipeObj);
        dataPackResources.put(recipePath, recipeJson.getBytes(StandardCharsets.UTF_8));
    }

    private static void meltingRecipeBall(String name){
        String recipePath = "data/productiveslimes/recipe/melting/" + name + "_slimeball_melting.json";

        JsonObject recipeObj = new JsonObject();
        recipeObj.addProperty("type", "productiveslimes:melting");
        recipeObj.addProperty("energy", 200);

        JsonObject ingredientsObj = new JsonObject();
        ingredientsObj.addProperty("count", 1);
        ingredientsObj.addProperty("ingredient", "productiveslimes:" + name + "_slimeball");
        recipeObj.add("ingredients", ingredientsObj);

        JsonObject outputObj = new JsonObject();
        outputObj.addProperty("amount", 20250);
        outputObj.addProperty("id", "productiveslimes:still_" + name);
        recipeObj.add("output", outputObj);

        String recipeJson = new GsonBuilder().setPrettyPrinting().create().toJson(recipeObj);
        dataPackResources.put(recipePath, recipeJson.getBytes(StandardCharsets.UTF_8));
    }

    private static void solidingRecipe(CustomVariant variant){
        String recipePath = "data/productiveslimes/recipe/soliding/molten_" + variant.name() + "_bucket_soliding.json";

        JsonObject recipeObj = new JsonObject();
        recipeObj.addProperty("type", "productiveslimes:soliding");
        recipeObj.addProperty("energy", 200);

        JsonObject ingredientsObj = new JsonObject();
        ingredientsObj.addProperty("amount", variant.solidingInputAmount());
        ingredientsObj.addProperty("id", "productiveslimes:still_" + variant.name());
        recipeObj.add("ingredients", ingredientsObj);

        JsonObject outputObj = new JsonObject();
        outputObj.addProperty("count", 1);
        outputObj.addProperty("id", variant.solidingOutput());

        JsonArray outputArray = new JsonArray();
        outputArray.add(outputObj);

        recipeObj.add("output", outputArray);

        String recipeJson = new GsonBuilder().setPrettyPrinting().create().toJson(recipeObj);
        dataPackResources.put(recipePath, recipeJson.getBytes(StandardCharsets.UTF_8));
    }

    private static void dnaExtracting(CustomVariant variant){
        String recipePath = "data/productiveslimes/recipe/dna_extracting/" + variant.name() + "_slimeball_dna_extracting.json";

        JsonObject recipeObj = new JsonObject();
        recipeObj.addProperty("type", "productiveslimes:dna_extracting");
        recipeObj.addProperty("energy", 400);

        JsonArray ingredientsArray = new JsonArray();
        ingredientsArray.add("productiveslimes:" + variant.name() + "_slimeball");
        recipeObj.add("ingredients", ingredientsArray);

        recipeObj.addProperty("inputCount", 1);

        JsonArray outputArray = new JsonArray();

        JsonObject output1 = new JsonObject();
        output1.addProperty("count", 1);
        output1.addProperty("id", "productiveslimes:" + variant.name() + "_slime_dna");
        outputArray.add(output1);

        JsonObject output2 = new JsonObject();
        output2.addProperty("count", 1);
        output2.addProperty("id", "minecraft:slime_ball");
        outputArray.add(output2);

        recipeObj.add("output", outputArray);

        recipeObj.addProperty("outputChance", variant.dnaOutputChance());

        String recipeJson = new GsonBuilder().setPrettyPrinting().create().toJson(recipeObj);
        dataPackResources.put(recipePath, recipeJson.getBytes(StandardCharsets.UTF_8));
    }

    private static void dnaSynthesizingSelf(CustomVariant variant){
        String recipePath = "data/productiveslimes/recipe/dna_synthesizing/" + variant.name() + "_slime_spawn_egg_synthesizing_self.json";

        JsonObject recipeObj = new JsonObject();
        recipeObj.addProperty("type", "productiveslimes:dna_synthesizing");
        recipeObj.addProperty("energy", 600);

        JsonArray ingredientsArray = new JsonArray();

        JsonObject ingredient1 = new JsonObject();
        ingredient1.addProperty("count", 1);
        ingredient1.addProperty("ingredient", "productiveslimes:" + variant.name() + "_slime_dna");
        ingredientsArray.add(ingredient1);

        JsonObject ingredient2 = new JsonObject();
        ingredient2.addProperty("count", 1);
        ingredient2.addProperty("ingredient", "productiveslimes:" + variant.name() + "_slime_dna");
        ingredientsArray.add(ingredient2);

        JsonObject ingredient3 = new JsonObject();
        ingredient3.addProperty("count", 2);
        ingredient3.addProperty("ingredient", variant.synthesizingInputItem());
        ingredientsArray.add(ingredient3);

        recipeObj.add("ingredients", ingredientsArray);

        JsonArray outputArray = new JsonArray();
        JsonObject outputObj = new JsonObject();
        outputObj.addProperty("count", 1);
        outputObj.addProperty("id", "productiveslimes:" + variant.name() + "_slime_spawn_egg");
        outputArray.add(outputObj);

        recipeObj.add("output", outputArray);

        String recipeJson = new GsonBuilder().setPrettyPrinting().create().toJson(recipeObj);
        dataPackResources.put(recipePath, recipeJson.getBytes(StandardCharsets.UTF_8));
    }

    private static void dnaSynthesizing(CustomVariant variant){
        String recipePath = "data/productiveslimes/recipe/dna_synthesizing/" + variant.name() + "_slime_spawn_egg_synthesizing.json";

        JsonObject recipeObj = new JsonObject();
        recipeObj.addProperty("type", "productiveslimes:dna_synthesizing");
        recipeObj.addProperty("energy", 600);

        JsonArray ingredientsArray = new JsonArray();

        JsonObject ingredient1 = new JsonObject();
        ingredient1.addProperty("count", 1);
        ingredient1.addProperty("ingredient", variant.synthesizingInputDna1());
        ingredientsArray.add(ingredient1);

        JsonObject ingredient2 = new JsonObject();
        ingredient2.addProperty("count", 1);
        ingredient2.addProperty("ingredient", variant.synthesizingInputDna2());
        ingredientsArray.add(ingredient2);

        JsonObject ingredient3 = new JsonObject();
        ingredient3.addProperty("count", 4);
        ingredient3.addProperty("ingredient", variant.synthesizingInputItem());
        ingredientsArray.add(ingredient3);

        recipeObj.add("ingredients", ingredientsArray);

        JsonArray outputArray = new JsonArray();
        JsonObject outputObj = new JsonObject();
        outputObj.addProperty("count", 1);
        outputObj.addProperty("id", "productiveslimes:" + variant.name() + "_slime_spawn_egg");
        outputArray.add(outputObj);
        recipeObj.add("output", outputArray);

        String recipeJson = new GsonBuilder().setPrettyPrinting().create().toJson(recipeObj);
        dataPackResources.put(recipePath, recipeJson.getBytes(StandardCharsets.UTF_8));
    }
}
