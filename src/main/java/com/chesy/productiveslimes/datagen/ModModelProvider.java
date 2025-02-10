package com.chesy.productiveslimes.datagen;

import com.chesy.productiveslimes.ProductiveSlimes;
import com.chesy.productiveslimes.block.ModBlocks;
import com.chesy.productiveslimes.block.custom.SlimeBlock;
import com.chesy.productiveslimes.item.ModItems;
import com.chesy.productiveslimes.item.custom.BucketItem;
import com.chesy.productiveslimes.item.custom.DnaItem;
import com.chesy.productiveslimes.item.custom.SlimeballItem;
import com.chesy.productiveslimes.item.custom.SpawnEggItem;
import com.chesy.productiveslimes.tier.ModTiers;
import com.chesy.productiveslimes.tier.ModTier;
import com.chesy.productiveslimes.tier.Tier;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricModelProvider;
import net.minecraft.block.Block;
import net.minecraft.data.client.*;
import net.minecraft.registry.Registries;
import net.minecraft.state.property.Properties;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;

import java.util.*;

public class ModModelProvider extends FabricModelProvider {

    public ModModelProvider(FabricDataOutput output) {
        super(output);
    }

    @Override
    public void generateBlockStateModels(BlockStateModelGenerator blockStateModelGenerator) {
        registerNorthDefaultHorizontalRotationInverted(blockStateModelGenerator, ModBlocks.MELTING_STATION);
        registerNorthDefaultHorizontalRotationInverted(blockStateModelGenerator, ModBlocks.SOLIDING_STATION);
        registerNorthDefaultHorizontalRotationInverted(blockStateModelGenerator, ModBlocks.DNA_EXTRACTOR);
        registerNorthDefaultHorizontalRotationInverted(blockStateModelGenerator, ModBlocks.DNA_SYNTHESIZER);
        registerNorthDefaultHorizontalRotationInverted(blockStateModelGenerator, ModBlocks.ENERGY_GENERATOR);
        registerNorthDefaultHorizontalRotationInverted(blockStateModelGenerator, ModBlocks.SLIMEBALL_COLLECTOR);
        registerNorthDefaultHorizontalRotationInverted(blockStateModelGenerator, ModBlocks.SLIME_NEST);
        blockStateModelGenerator.registerNorthDefaultHorizontalRotation(ModBlocks.SLIME_SQUEEZER);
        blockStateModelGenerator.excludeFromSimpleItemModelGeneration(ModBlocks.SLIME_SQUEEZER);
        simpleBlockWithExistingModel(blockStateModelGenerator, ModBlocks.SQUEEZER);

        fluidTank(blockStateModelGenerator, ModBlocks.FLUID_TANK);

        simpleBlockWithExistingModel(blockStateModelGenerator, ModBlocks.SLIMY_GRASS_BLOCK);

        blockStateModelGenerator.registerSimpleCubeAll(ModBlocks.SLIMY_DIRT);
        blockWithSlab(blockStateModelGenerator, ModBlocks.SLIMY_STONE, ModBlocks.SLIMY_STONE_SLAB);
        blockStateModelGenerator.registerSimpleCubeAll(ModBlocks.SLIMY_DEEPSLATE);
        blockWithSlab(blockStateModelGenerator, ModBlocks.SLIMY_COBBLESTONE, ModBlocks.SLIMY_COBBLESTONE_SLAB);
        blockWithSlab(blockStateModelGenerator, ModBlocks.SLIMY_COBBLED_DEEPSLATE, ModBlocks.SLIMY_COBBLED_DEEPSLATE_SLAB);

        logBlock(blockStateModelGenerator, ModBlocks.SLIMY_LOG, ModBlocks.SLIMY_WOOD);
        logBlock(blockStateModelGenerator, ModBlocks.STRIPPED_SLIMY_LOG, ModBlocks.STRIPPED_SLIMY_WOOD);

        blockWithSlab(blockStateModelGenerator, ModBlocks.SLIMY_PLANKS, ModBlocks.SLIMY_SLAB);

        leavesBlock(blockStateModelGenerator, ModBlocks.SLIMY_LEAVES);
        saplingBlock(blockStateModelGenerator, ModBlocks.SLIMY_SAPLING);

        stairsBlock(blockStateModelGenerator, ModBlocks.SLIMY_STAIRS, ModBlocks.SLIMY_PLANKS);
        pressurePlateBlock(blockStateModelGenerator, ModBlocks.SLIMY_PRESSURE_PLATE, ModBlocks.SLIMY_PLANKS);
        buttonBlock(blockStateModelGenerator, ModBlocks.SLIMY_BUTTON, ModBlocks.SLIMY_PLANKS);
        fenceBlock(blockStateModelGenerator, ModBlocks.SLIMY_FENCE, ModBlocks.SLIMY_PLANKS);
        fenceGateBlock(blockStateModelGenerator, ModBlocks.SLIMY_FENCE_GATE, ModBlocks.SLIMY_PLANKS);
        blockStateModelGenerator.registerTrapdoor(ModBlocks.SLIMY_TRAPDOOR);
        blockStateModelGenerator.registerDoor(ModBlocks.SLIMY_DOOR);

        stairsBlock(blockStateModelGenerator, ModBlocks.SLIMY_STONE_STAIRS, ModBlocks.SLIMY_STONE);
        pressurePlateBlock(blockStateModelGenerator, ModBlocks.SLIMY_STONE_PRESSURE_PLATE, ModBlocks.SLIMY_STONE);
        buttonBlock(blockStateModelGenerator, ModBlocks.SLIMY_STONE_BUTTON, ModBlocks.SLIMY_STONE);

        stairsBlock(blockStateModelGenerator, ModBlocks.SLIMY_COBBLESTONE_STAIRS, ModBlocks.SLIMY_COBBLESTONE);
        wallBlock(blockStateModelGenerator, ModBlocks.SLIMY_COBBLESTONE_WALL, ModBlocks.SLIMY_COBBLESTONE);

        stairsBlock(blockStateModelGenerator, ModBlocks.SLIMY_COBBLED_DEEPSLATE_STAIRS, ModBlocks.SLIMY_COBBLED_DEEPSLATE);
        wallBlock(blockStateModelGenerator, ModBlocks.SLIMY_COBBLED_DEEPSLATE_WALL, ModBlocks.SLIMY_COBBLED_DEEPSLATE);

        slimeBlock(blockStateModelGenerator, ModBlocks.ENERGY_SLIME_BLOCK);

        for (Tier tier : Tier.values()){
            ModTier tiers = ModTiers.getTierByName(tier);
            slimeBlock(blockStateModelGenerator, ModTiers.getBlockByName(tiers.name()));
        }
    }

    @Override
    public void generateItemModels(ItemModelGenerator itemModelGenerator) {
        itemModelGenerator.register(ModItems.GUIDEBOOK, Models.GENERATED);
        itemModelGenerator.register(ModItems.ENERGY_MULTIPLIER_UPGRADE, Models.GENERATED);
        itemModelGenerator.register(ModItems.SLIME_NEST_SPEED_UPGRADE_1, Models.GENERATED);
        itemModelGenerator.register(ModItems.SLIME_NEST_SPEED_UPGRADE_2, Models.GENERATED);
        itemModelGenerator.register(ModItems.SLIMEBALL_FRAGMENT, Models.GENERATED);

        slimeballItem(itemModelGenerator, ProductiveSlimes.ENERGY_SLIME_BALL);
        dnaItem(itemModelGenerator, ModItems.SLIME_DNA);
        itemModelGenerator.register(ModItems.ENERGY_SLIME_SPAWN_EGG, new Model(Optional.of(Identifier.of("item/template_spawn_egg")), Optional.empty()));

        for (Tier tier : Tier.values()){
            ModTier tiers = ModTiers.getTierByName(tier);

            slimeballItem(itemModelGenerator, ModTiers.getSlimeballItemByName(tiers.name()));
            bucketItem(itemModelGenerator, ModTiers.getBucketItemByName(tiers.name()));
            dnaItem(itemModelGenerator, ModTiers.getDnaItemByName(tiers.name()));
            SpawnEggItem spawnEggItem = ModTiers.getSpawnEggItemByName(tiers.name());
            itemModelGenerator.register(spawnEggItem, new Model(Optional.of(Identifier.of("item/template_spawn_egg")), Optional.empty()));
        }
    }

    private void simpleBlockWithExistingModel(BlockStateModelGenerator blockModels, Block block){
        blockModels.blockStateCollector.accept(VariantsBlockStateSupplier.create(block, BlockStateVariant.create().put(VariantSettings.MODEL, blockLocation(getBlockName(block)))));
        blockModels.excludeFromSimpleItemModelGeneration(block);
    }

    private void blockWithSlab(BlockStateModelGenerator blockModels, Block block, Block slab){
        Identifier texture = blockLocation(getBlockName(block));
        blockModels.new BlockTexturePool(TextureMap.all(texture)
                .put(TextureKey.BOTTOM, texture)
                .put(TextureKey.TOP, texture)
                .put(TextureKey.SIDE, texture)
        ).base(block, Models.CUBE_ALL).slab(slab);
    }

    private void logBlock(BlockStateModelGenerator blockModels, Block block, Block wood){
        blockModels.registerLog(block).log(block).wood(wood);
    }

    private void leavesBlock(BlockStateModelGenerator blockModels, Block block){
        blockModels.registerSingleton(block, TexturedModel.LEAVES);
    }

    private void saplingBlock(BlockStateModelGenerator blockModels, Block block){
        blockModels.registerTintableCrossBlockState(block, BlockStateModelGenerator.TintType.NOT_TINTED);
    }

    private void pressurePlateBlock(BlockStateModelGenerator blockModels, Block block, Block materialBlock){
        blockModels.new BlockTexturePool(TextureMap.texture(materialBlock)).pressurePlate(block);
    }

    private void stairsBlock(BlockStateModelGenerator blockModels, Block block, Block materialBlock){
        Identifier texture = blockLocation(getBlockName(materialBlock));
        blockModels.new BlockTexturePool(TextureMap.texture(texture)
                .put(TextureKey.BOTTOM, texture)
                .put(TextureKey.TOP, texture)
                .put(TextureKey.SIDE, texture)
        ).stairs(block);
    }

    private void buttonBlock(BlockStateModelGenerator blockModels, Block block, Block materialBlock){
        blockModels.new BlockTexturePool(TextureMap.texture(materialBlock)).button(block);
    }

    private void fenceBlock(BlockStateModelGenerator blockModels, Block block, Block materialBlock){
        blockModels.new BlockTexturePool(TextureMap.texture(materialBlock)).fence(block);
    }

    private void fenceGateBlock(BlockStateModelGenerator blockModels, Block block, Block materialBlock){
        blockModels.new BlockTexturePool(TextureMap.texture(materialBlock)).fenceGate(block);
    }

    private void wallBlock(BlockStateModelGenerator blockModels, Block block, Block materialBlock){
        blockModels.new BlockTexturePool(TextureMap.texture(materialBlock).put(TextureKey.WALL, blockLocation(getBlockName(materialBlock)))).wall(block);
    }

    private void dnaItem(ItemModelGenerator itemModelGenerator, DnaItem item) {
        Identifier identifier = Identifier.of(ProductiveSlimes.MODID,"item/template_dna");

        itemModelGenerator.register(item, new Model(Optional.of(identifier), Optional.empty()));
    }

    private void slimeballItem(ItemModelGenerator itemModelGenerator, SlimeballItem item) {
        Identifier identifier = Identifier.of(ProductiveSlimes.MODID,"item/template_slimeball");

        itemModelGenerator.register(item, new Model(Optional.of(identifier), Optional.empty()));
    }

    private void bucketItem(ItemModelGenerator itemModelGenerator, BucketItem item){
        TextureMap textures = new TextureMap()
                .put(TextureKey.LAYER0, Identifier.of(ProductiveSlimes.MODID, "item/bucket"))
                .put(TextureKey.LAYER1, Identifier.of(ProductiveSlimes.MODID, "item/bucket_fluid"));

        Identifier model = Models.GENERATED_TWO_LAYERS.upload(Registries.ITEM.getId(item).withPrefixedPath("item/"), textures, itemModelGenerator.writer);
    }

    private void fluidTank(BlockStateModelGenerator blockModels, Block block){
        registerNorthDefaultHorizontalRotationInverted(blockModels, block);
    }

    public BlockStateVariantMap createNorthDefaultHorizontalRotationStatesInverted() {
        return BlockStateVariantMap.create(Properties.HORIZONTAL_FACING)
                .register(Direction.EAST, BlockStateVariant.create().put(VariantSettings.Y, VariantSettings.Rotation.R270))
                .register(Direction.NORTH, BlockStateVariant.create().put(VariantSettings.Y, VariantSettings.Rotation.R180))
                .register(Direction.WEST, BlockStateVariant.create().put(VariantSettings.Y, VariantSettings.Rotation.R90))
                .register(Direction.SOUTH, BlockStateVariant.create());
    }

    private void slimeBlock(BlockStateModelGenerator blockStateModelGenerator, SlimeBlock block){
        blockStateModelGenerator.registerSingleton(block, new TextureMap(), new Model(Optional.of(Identifier.of(ProductiveSlimes.MODID, "block/template_slime_block")), Optional.empty()));
    }

    public final void registerNorthDefaultHorizontalRotationInverted(BlockStateModelGenerator blockStateModelGenerator, Block block) {
        blockStateModelGenerator.blockStateCollector
                .accept(
                        VariantsBlockStateSupplier.create(block, BlockStateVariant.create().put(VariantSettings.MODEL, ModelIds.getBlockModelId(block)))
                                .coordinate(createNorthDefaultHorizontalRotationStatesInverted())
                );

        blockStateModelGenerator.excludeFromSimpleItemModelGeneration(block);
    }

    private Identifier blockLocation(String modelName){
        return Identifier.of(ProductiveSlimes.MODID, "block/" + modelName);
    }

    private Identifier itemLocation(String modelName){
        return Identifier.of(ProductiveSlimes.MODID, "item/" + modelName);
    }

    private String getBlockName(Block block){
        Identifier location = Registries.BLOCK.getId(block);
        return location.getPath();
    }
}
