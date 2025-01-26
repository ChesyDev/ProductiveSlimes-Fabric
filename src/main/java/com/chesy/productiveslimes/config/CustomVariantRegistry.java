package com.chesy.productiveslimes.config;

import com.chesy.productiveslimes.ProductiveSlimes;
import com.chesy.productiveslimes.block.custom.SlimeBlock;
import com.chesy.productiveslimes.config.fluid.CustomDynamicFluid;
import com.chesy.productiveslimes.entity.BaseSlime;
import com.chesy.productiveslimes.item.custom.BucketItem;
import com.chesy.productiveslimes.item.custom.DnaItem;
import com.chesy.productiveslimes.item.custom.SlimeballItem;
import com.chesy.productiveslimes.item.custom.SpawnEggItem;
import com.chesy.productiveslimes.util.MixinAdditionMethod;
import com.chesy.productiveslimes.util.CustomVariantDataPack;
import com.chesy.productiveslimes.util.CustomVariantResourcePack;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mojang.logging.LogUtils;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.FluidBlock;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.fluid.FlowableFluid;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.resource.*;
import net.minecraft.server.MinecraftServer;
import net.minecraft.text.Text;
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

    private static Map<Identifier, Item> registeredItems = new HashMap<>();
    private static Map<Identifier, Item> registeredDnaItems = new HashMap<>();
    private static Map<Identifier, Item> registeredSpawnEggItems = new HashMap<>();
    private static Map<Identifier, Block> registeredBlocks = new HashMap<>();
    private static Map<Identifier, EntityType<BaseSlime>> registeredSlimes = new HashMap<>();
    private static final Map<Identifier, FluidBlock> registeredLiquidBlock = new HashMap<>();
    private static final Map<Identifier, BucketItem> registeredBucketItem = new HashMap<>();
    private static final Map<Identifier, FlowableFluid> registeredSource = new HashMap<>();
    private static final Map<Identifier, FlowableFluid> registeredFlow = new HashMap<>();
    private static Map<String, byte[]> resourceData = new HashMap<>();
    private static Map<String, byte[]> dataPackResources = new HashMap<>();

    public static void initialize(){
        createDefaultConfig();
        loadVariants();

        generateResourcePackInMemory();
        generateDataPackInMemory();
    }

    public static void handleResourcePack(){
        CustomVariantResourcePack resourcePack = new CustomVariantResourcePack(resourceData);
        ResourcePackManager packManager = MinecraftClient.getInstance().getResourcePackManager();

        ResourcePackProfile pack = ResourcePackProfile.create(
                resourcePack.getInfo(),
                new ResourcePackProfile.PackFactory() {
                    @Override
                    public ResourcePack open(ResourcePackInfo location) {
                        return resourcePack;
                    }

                    @Override
                    public ResourcePack openWithOverlays(ResourcePackInfo location, ResourcePackProfile.Metadata metadata) {
                        return resourcePack;
                    }
                },
                ResourceType.CLIENT_RESOURCES,
                new ResourcePackPosition(true, ResourcePackProfile.InsertionPosition.TOP, true)
        );

        if (packManager instanceof MixinAdditionMethod mixinAdditionMethod){
            mixinAdditionMethod.addPackFinder((consumer) -> {
                consumer.accept(pack);
            });
        }
        MinecraftClient.getInstance().reloadResources();
    }

    public static void handleDatapack(MinecraftServer server) {
        CustomVariantDataPack dataPack = new CustomVariantDataPack(dataPackResources);
        ResourcePackProfile pack = ResourcePackProfile.create(
                new ResourcePackInfo("productiveslimes_datapack", Text.literal("In Memory Pack"),
                        new ResourcePackSource() {
                            @Override
                            public Text decorate(Text name) {
                                return Text.literal("In Memory Pack");
                            }

                            @Override
                            public boolean canBeEnabledLater() {
                                return true;
                            }
                        }, Optional.empty()),
                new ResourcePackProfile.PackFactory() {
                    @Override
                    public ResourcePack open(ResourcePackInfo location) {
                        return dataPack;
                    }

                    @Override
                    public ResourcePack openWithOverlays(ResourcePackInfo location, ResourcePackProfile.Metadata metadata) {
                        return dataPack;
                    }
                },
                ResourceType.SERVER_DATA,
                new ResourcePackPosition(true, ResourcePackProfile.InsertionPosition.TOP, true)
        );

        // Add your pack to the pack repository
        ResourcePackManager dataManager = server.getDataPackManager();
        if (dataManager instanceof MixinAdditionMethod mixinAdditionMethod){
            mixinAdditionMethod.addPackFinder((consumer) -> {
                consumer.accept(pack);
            });
        }
        // Reload data packs to include your new pack
        List<ResourcePackProfile> packs = new ArrayList<>(server.getDataPackManager().getEnabledProfiles());
        packs.add(pack);
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
                    new CustomVariant("birch", "#FFa69d6f", 5, 1500, "minecraft:birch_log", "minecraft:birch_log", 2, "productiveslimes:oak_slime_dna", "productiveslimes:oak_slime_dna", "minecraft:birch_log", 0.75)
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
        Item item = Registry.register(Registries.ITEM, itemId, new SpawnEggItem(getSlimeForVariant(variant.name()), variant.getColor(), variant.getColor(), new Item.Settings().registryKey(RegistryKey.of(RegistryKeys.ITEM, itemId))));

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
            String modelPath = "assets/productiveslimes/models/block/" + id + ".json";
            String bucketModelPath = "assets/productiveslimes/models/item/molten_" + variants.name() + "_bucket.json";
            String slimeBlockModelPath = "assets/productiveslimes/models/item/" + variants.name() + "_slime_block.json";
            String dnaModelPath = "assets/productiveslimes/models/item/" + variants.name() + "_slime_dna.json";
            String spawnEggModelPath = "assets/productiveslimes/models/item/" + variants.name() + "_slime_spawn_egg.json";
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
                    "    \"model\": \"productiveslimes:item/" + variants.name() + "_slime_spawn_egg\",\n" +
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

            String spawnEggModelContent = "{\n" +
                    "  \"parent\": \"minecraft:item/template_spawn_egg\"\n" +
                    "}";

            resourceData.put(blockstatePath, blockstateContent.getBytes(StandardCharsets.UTF_8));
            resourceData.put(modelPath, blockModelContent.getBytes(StandardCharsets.UTF_8));
            resourceData.put(bucketModelPath, bucketModelContent.getBytes(StandardCharsets.UTF_8));
            resourceData.put(slimeBlockModelPath, slimeBlockModelContent.getBytes(StandardCharsets.UTF_8));
            resourceData.put(dnaModelPath, dnaModelContent.getBytes(StandardCharsets.UTF_8));
            resourceData.put(spawnEggModelPath, spawnEggModelContent.getBytes(StandardCharsets.UTF_8));
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
            String lootTable = "{\n" +
                    "  \"type\": \"minecraft:block\",\n" +
                    "  \"pools\": [\n" +
                    "    {\n" +
                    "      \"bonus_rolls\": 0.0,\n" +
                    "      \"conditions\": [\n" +
                    "        {\n" +
                    "          \"condition\": \"minecraft:survives_explosion\"\n" +
                    "        }\n" +
                    "      ],\n" +
                    "      \"entries\": [\n" +
                    "        {\n" +
                    "          \"type\": \"minecraft:item\",\n" +
                    "          \"name\": \"productiveslimes:" + variants.name() + "_slime_block\"\n" +
                    "        }\n" +
                    "      ],\n" +
                    "      \"rolls\": 1.0\n" +
                    "    }\n" +
                    "  ],\n" +
                    "  \"random_sequence\": \"productiveslimes:blocks/" + variants.name() + "_slime_block\"\n" +
                    "}";
            dataPackResources.put(lootTablePath, lootTable.getBytes(StandardCharsets.UTF_8));
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

        String recipe = "{\n" +
                "  \"type\": \"minecraft:crafting_shaped\",\n" +
                "  \"category\": \"building\",\n" +
                "  \"key\": {\n" +
                "    \"A\": \"productiveslimes:" + name + "_slimeball\"\n" +
                "  },\n" +
                "  \"pattern\": [\n" +
                "    \"AAA\",\n" +
                "    \"AAA\",\n" +
                "    \"AAA\"\n" +
                "  ],\n" +
                "  \"result\": {\n" +
                "    \"count\": 1,\n" +
                "    \"id\": \"productiveslimes:" + name + "_slime_block\"\n" +
                "  }\n" +
                "}";

        dataPackResources.put(recipePath, recipe.getBytes(StandardCharsets.UTF_8));
    }

    private static void slimeBlockToSlimeball(String name){
        String recipePath = "data/minecraft/recipe/" + name + "_slime_block_to_ball.json";

        String recipe = "{\n" +
                "  \"type\": \"minecraft:crafting_shapeless\",\n" +
                "  \"category\": \"misc\",\n" +
                "  \"ingredients\": [\n" +
                "    \"productiveslimes:" + name + "_slime_block\"\n" +
                "  ],\n" +
                "  \"result\": {\n" +
                "    \"count\": 9,\n" +
                "    \"id\": \"productiveslimes:" + name + "_slimeball\"\n" +
                "  }\n" +
                "}";

        dataPackResources.put(recipePath, recipe.getBytes(StandardCharsets.UTF_8));
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

        String recipe = "{\n" +
                "  \"type\": \"productiveslimes:melting\",\n" +
                "  \"energy\": 200,\n" +
                "  \"ingredients\": [\n" +
                "      \"productiveslimes:" + name + "_slime_block\"\n" +
                "  ],\n" +
                "  \"inputCount\": 2,\n" +
                "  \"output\": [\n" +
                "    {\n" +
                "      \"count\": 5,\n" +
                "      \"id\": \"productiveslimes:molten_" + name + "_bucket\"\n" +
                "    }\n" +
                "  ]\n" +
                "}";

        dataPackResources.put(recipePath, recipe.getBytes(StandardCharsets.UTF_8));
    }

    private static void meltingRecipeBall(String name){
        String recipePath = "data/productiveslimes/recipe/melting/" + name + "_slimeball_melting.json";

        String recipe = "{\n" +
                "  \"type\": \"productiveslimes:melting\",\n" +
                "  \"energy\": 200,\n" +
                "  \"ingredients\": [\n" +
                "      \"productiveslimes:" + name + "_slimeball\"\n" +
                "  ],\n" +
                "  \"inputCount\": 4,\n" +
                "  \"output\": [\n" +
                "    {\n" +
                "      \"count\": 1,\n" +
                "      \"id\": \"productiveslimes:molten_" + name + "_bucket\"\n" +
                "    }\n" +
                "  ]\n" +
                "}";

        dataPackResources.put(recipePath, recipe.getBytes(StandardCharsets.UTF_8));
    }

    private static void solidingRecipe(CustomVariant variant){
        String recipePath = "data/productiveslimes/recipe/soliding/molten_" + variant.name() + "_bucket_soliding.json";

        String recipe = "{\n" +
                "  \"type\": \"productiveslimes:soliding\",\n" +
                "  \"energy\": 200,\n" +
                "  \"ingredients\": [\n" +
                "      \"productiveslimes:molten_" + variant.name() + "_bucket\"\n" +
                "  ],\n" +
                "  \"inputCount\": 1,\n" +
                "  \"output\": [\n" +
                "    {\n" +
                "      \"count\": " + variant.solidingOutputCount() + ",\n" +
                "      \"id\": \"" + variant.solidingOutput() + "\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"count\": 1,\n" +
                "      \"id\": \"minecraft:bucket\"\n" +
                "    }\n" +
                "  ]\n" +
                "}";

        dataPackResources.put(recipePath, recipe.getBytes(StandardCharsets.UTF_8));
    }

    private static void dnaExtracting(CustomVariant variant){
        String recipePath = "data/productiveslimes/recipe/dna_extracting/" + variant.name() + "_slimeball_dna_extracting.json";

        String recipe = "{\n" +
                "  \"type\": \"productiveslimes:dna_extracting\",\n" +
                "  \"energy\": 400,\n" +
                "  \"ingredients\": [\n" +
                "    \"productiveslimes:" + variant.name() + "_slimeball\"\n" +
                "  ],\n" +
                "  \"inputCount\": 1,\n" +
                "  \"output\": [\n" +
                "    {\n" +
                "      \"count\": 1,\n" +
                "      \"id\": \"productiveslimes:" + variant.name() + "_slime_dna\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"count\": 1,\n" +
                "      \"id\": \"minecraft:slime_ball\"\n" +
                "    }\n" +
                "  ],\n" +
                "  \"outputChance\": " + variant.dnaOutputChance() + "\n" +
                "}";

        dataPackResources.put(recipePath, recipe.getBytes(StandardCharsets.UTF_8));
    }

    private static void dnaSynthesizingSelf(CustomVariant variant){
        String recipePath = "data/productiveslimes/recipe/dna_synthesizing/" + variant.name() + "_slime_spawn_egg_synthesizing_self.json";

        String recipe = "{\n" +
                "  \"type\": \"productiveslimes:dna_synthesizing\",\n" +
                "  \"energy\": 600,\n" +
                "  \"ingredients\": [\n" +
                "      \"productiveslimes:" + variant.name() + "_slime_dna\",\n" +
                "      \"productiveslimes:" + variant.name() + "_slime_dna\",\n" +
                "      \"" + variant.synthesizingInputItem() + "\"\n" +
                "  ],\n" +
                "  \"inputCount\": 2,\n" +
                "  \"output\": [\n" +
                "    {\n" +
                "      \"count\": 1,\n" +
                "      \"id\": \"productiveslimes:" + variant.name() + "_slime_spawn_egg\"\n" +
                "    }\n" +
                "  ]\n" +
                "}";

        dataPackResources.put(recipePath, recipe.getBytes(StandardCharsets.UTF_8));
    }

    private static void dnaSynthesizing(CustomVariant variant){
        String recipePath = "data/productiveslimes/recipe/dna_synthesizer/" + variant.name() + "_slime_spawn_egg_synthesizing.json";

        String recipe = "{\n" +
                "  \"type\": \"productiveslimes:dna_synthesizing\",\n" +
                "  \"energy\": 600,\n" +
                "  \"ingredients\": [\n" +
                "  \"" + variant.synthesizingInputDna1() + "\",\n" +
                "  \"" + variant.synthesizingInputDna2() + "\",\n" +
                "  \"" + variant.synthesizingInputItem() + "\"\n" +
                "  ],\n" +
                "  \"inputCount\": 4,\n" +
                "  \"output\": [\n" +
                "    {\n" +
                "      \"count\": 1,\n" +
                "      \"id\": \"productiveslimes:" + variant.name() + "_slime_spawn_egg\"\n" +
                "    }\n" +
                "  ]\n" +
                "}";

        dataPackResources.put(recipePath, recipe.getBytes(StandardCharsets.UTF_8));
    }
}
