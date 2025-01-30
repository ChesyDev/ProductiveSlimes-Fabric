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
import com.chesy.productiveslimes.util.FluidTankTint;
import com.chesy.productiveslimes.util.SlimeItemTint;
import com.chesy.productiveslimes.util.property.*;
import net.fabricmc.fabric.api.client.datagen.v1.provider.FabricModelProvider;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.minecraft.block.Block;
import net.minecraft.client.data.*;
import net.minecraft.client.render.item.model.BasicItemModel;
import net.minecraft.client.render.item.model.SpecialItemModel;
import net.minecraft.client.render.item.tint.ConstantTintSource;
import net.minecraft.client.render.item.tint.TintSource;
import net.minecraft.item.Item;
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
        BlockStateModelGenerator.BlockTexturePool slimyWoodSet = blockStateModelGenerator.registerCubeAllModelTexturePool(ModBlocks.SLIMY_PLANKS);
        BlockStateModelGenerator.BlockTexturePool slimyStoneSet = blockStateModelGenerator.registerCubeAllModelTexturePool(ModBlocks.SLIMY_STONE);
        BlockStateModelGenerator.BlockTexturePool slimyCobblestoneSet = blockStateModelGenerator.registerCubeAllModelTexturePool(ModBlocks.SLIMY_COBBLESTONE);
        BlockStateModelGenerator.BlockTexturePool slimyCobbledDeepslateSet = blockStateModelGenerator.registerCubeAllModelTexturePool(ModBlocks.SLIMY_COBBLED_DEEPSLATE);

        registerNorthDefaultHorizontalRotationInverted(blockStateModelGenerator, ModBlocks.MELTING_STATION);
        registerNorthDefaultHorizontalRotationInverted(blockStateModelGenerator, ModBlocks.SOLIDING_STATION);
        registerNorthDefaultHorizontalRotationInverted(blockStateModelGenerator, ModBlocks.DNA_EXTRACTOR);
        registerNorthDefaultHorizontalRotationInverted(blockStateModelGenerator, ModBlocks.DNA_SYNTHESIZER);
        registerNorthDefaultHorizontalRotationInverted(blockStateModelGenerator, ModBlocks.ENERGY_GENERATOR);
        registerNorthDefaultHorizontalRotationInverted(blockStateModelGenerator, ModBlocks.SLIMEBALL_COLLECTOR);
        registerNorthDefaultHorizontalRotationInverted(blockStateModelGenerator, ModBlocks.SLIME_NEST);
        blockStateModelGenerator.registerNorthDefaultHorizontalRotation(ModBlocks.SLIME_SQUEEZER);
        blockStateModelGenerator.itemModelOutput.accept(ModBlocks.SLIME_SQUEEZER.asItem(), new BasicItemModel.Unbaked(itemLocation("slime_squeezer"), Collections.emptyList()));
        simpleBlockWithExistingModel(blockStateModelGenerator, ModBlocks.SQUEEZER);

        fluidTank(blockStateModelGenerator, ModBlocks.FLUID_TANK);

        simpleBlockWithExistingModel(blockStateModelGenerator, ModBlocks.SLIMY_GRASS_BLOCK);

        blockStateModelGenerator.registerSimpleCubeAll(ModBlocks.SLIMY_DIRT);
        blockStateModelGenerator.registerSimpleCubeAll(ModBlocks.SLIMY_DEEPSLATE);

        logBlock(blockStateModelGenerator, ModBlocks.SLIMY_LOG, ModBlocks.SLIMY_WOOD);
        logBlock(blockStateModelGenerator, ModBlocks.STRIPPED_SLIMY_LOG, ModBlocks.STRIPPED_SLIMY_WOOD);

        slimyStoneSet.slab(ModBlocks.SLIMY_STONE_SLAB);
        slimyStoneSet.stairs(ModBlocks.SLIMY_STONE_STAIRS);
        slimyStoneSet.pressurePlate(ModBlocks.SLIMY_STONE_PRESSURE_PLATE);
        slimyStoneSet.button(ModBlocks.SLIMY_STONE_BUTTON);

        slimyCobblestoneSet.slab(ModBlocks.SLIMY_COBBLESTONE_SLAB);
        slimyCobblestoneSet.stairs(ModBlocks.SLIMY_COBBLESTONE_STAIRS);
        slimyCobblestoneSet.wall(ModBlocks.SLIMY_COBBLESTONE_WALL);

        slimyCobbledDeepslateSet.slab(ModBlocks.SLIMY_COBBLED_DEEPSLATE_SLAB);
        slimyCobbledDeepslateSet.stairs(ModBlocks.SLIMY_COBBLED_DEEPSLATE_STAIRS);
        slimyCobbledDeepslateSet.wall(ModBlocks.SLIMY_COBBLED_DEEPSLATE_WALL);

        slimyWoodSet.slab(ModBlocks.SLIMY_SLAB);
        slimyWoodSet.stairs(ModBlocks.SLIMY_STAIRS);
        slimyWoodSet.pressurePlate(ModBlocks.SLIMY_PRESSURE_PLATE);
        slimyWoodSet.button(ModBlocks.SLIMY_BUTTON);
        slimyWoodSet.fence(ModBlocks.SLIMY_FENCE);
        slimyWoodSet.fenceGate(ModBlocks.SLIMY_FENCE_GATE);

        leavesBlock(blockStateModelGenerator, ModBlocks.SLIMY_LEAVES);
        saplingBlock(blockStateModelGenerator, ModBlocks.SLIMY_SAPLING);

        blockStateModelGenerator.registerTrapdoor(ModBlocks.SLIMY_TRAPDOOR);
        blockStateModelGenerator.registerDoor(ModBlocks.SLIMY_DOOR);

        //Slime Blocks
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
        itemModelGenerator.registerSpawnEgg(ModItems.ENERGY_SLIME_SPAWN_EGG, ModItems.ENERGY_SLIME_SPAWN_EGG.getBg(), ModItems.ENERGY_SLIME_SPAWN_EGG.getFg());

        slimeItem(itemModelGenerator, ModItems.SLIME_ITEM);

        for (Tier tier : Tier.values()){
            ModTier tiers = ModTiers.getTierByName(tier);

            slimeballItem(itemModelGenerator, ModTiers.getSlimeballItemByName(tiers.name()));
            bucketItem(itemModelGenerator, ModTiers.getBucketItemByName(tiers.name()));
            dnaItem(itemModelGenerator, ModTiers.getDnaItemByName(tiers.name()));
            SpawnEggItem spawnEggItem = ModTiers.getSpawnEggItemByName(tiers.name());
            itemModelGenerator.registerSpawnEgg(spawnEggItem, spawnEggItem.getBg(), spawnEggItem.getFg());
        }
    }

    private void simpleBlockWithExistingModel(BlockStateModelGenerator blockModels, Block block){
        blockModels.blockStateCollector.accept(VariantsBlockStateSupplier.create(block, BlockStateVariant.create().put(VariantSettings.MODEL, blockLocation(getBlockName(block)))));
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
        blockModels.registerTintedBlockAndItem(block, TexturedModel.LEAVES, -1);
    }

    private void saplingBlock(BlockStateModelGenerator blockModels, Block block){
        blockModels.registerTintableCrossBlockState(block, BlockStateModelGenerator.CrossType.NOT_TINTED);
    }

    private void dnaItem(ItemModelGenerator itemModelGenerator, DnaItem item) {
        Identifier identifier = Identifier.of(ProductiveSlimes.MODID,"item/template_dna");
        Identifier model = Models.GENERATED.upload(item, TextureMap.of(TextureKey.LAYER0, identifier), itemModelGenerator.modelCollector);

        itemModelGenerator.output.accept(item, new BasicItemModel.Unbaked(model, Collections.singletonList(new ConstantTintSource(item.getColor()))));
    }

    private void slimeballItem(ItemModelGenerator itemModelGenerator, SlimeballItem item) {
        Identifier identifier = Identifier.of(ProductiveSlimes.MODID,"item/template_slimeball");
        Identifier model = Models.GENERATED.upload(item, TextureMap.of(TextureKey.LAYER0, identifier), itemModelGenerator.modelCollector);

        itemModelGenerator.output.accept(item, new BasicItemModel.Unbaked(model, Collections.singletonList(new ConstantTintSource(item.getColor()))));
    }

    private void bucketItem(ItemModelGenerator itemModelGenerator, BucketItem item){
        TextureMap textures = new TextureMap()
                .put(TextureKey.LAYER0, Identifier.of(ProductiveSlimes.MODID, "item/bucket"))
                .put(TextureKey.LAYER1, Identifier.of(ProductiveSlimes.MODID, "item/bucket_fluid"));

        Identifier model = Models.GENERATED_TWO_LAYERS.upload(item, textures, itemModelGenerator.modelCollector);

        itemModelGenerator.output.accept(item, new BasicItemModel.Unbaked(model, List.of(new ConstantTintSource(-1),new ConstantTintSource(item.getColor()))));
    }

    private void fluidTank(BlockStateModelGenerator blockModels, Block block){
        registerNorthDefaultHorizontalRotationInverted(blockModels, block);

        Identifier fluidTankEmpty = itemLocation("fluid_tank/fluid_tank_empty");
        Identifier fluidTank3k = itemLocation("fluid_tank/fluid_tank_1");
        Identifier fluidTank6k = itemLocation("fluid_tank/fluid_tank_2");
        Identifier fluidTank9k = itemLocation("fluid_tank/fluid_tank_3");
        Identifier fluidTank12k = itemLocation("fluid_tank/fluid_tank_4");
        Identifier fluidTank15k = itemLocation("fluid_tank/fluid_tank_5");
        Identifier fluidTank18k = itemLocation("fluid_tank/fluid_tank_6");
        Identifier fluidTank21k = itemLocation("fluid_tank/fluid_tank_7");
        Identifier fluidTank24k = itemLocation("fluid_tank/fluid_tank_8");
        Identifier fluidTank27k = itemLocation("fluid_tank/fluid_tank_9");
        Identifier fluidTank30k = itemLocation("fluid_tank/fluid_tank_10");
        Identifier fluidTank33k = itemLocation("fluid_tank/fluid_tank_11");
        Identifier fluidTank36k = itemLocation("fluid_tank/fluid_tank_12");
        Identifier fluidTank40k = itemLocation("fluid_tank/fluid_tank_13");
        Identifier fluidTank45k = itemLocation("fluid_tank/fluid_tank_14");
        Identifier fluidTankFull = itemLocation("fluid_tank/fluid_tank_full");

        List<TintSource> tintSources = List.of(ItemModels.constantTintSource(-1), new FluidTankTint(16777215));

        blockModels.itemModelOutput.accept(block.asItem(), ItemModels.condition(
                new FluidTankProperty(),
                new BasicItemModel.Unbaked(fluidTankEmpty, tintSources),
                ItemModels.condition(
                        new FluidTankProperty3k(),
                        new BasicItemModel.Unbaked(fluidTank3k, tintSources),
                        ItemModels.condition(
                                new FluidTankProperty6k(),
                                new BasicItemModel.Unbaked(fluidTank6k, tintSources),
                                ItemModels.condition(
                                        new FluidTankProperty9k(),
                                        new BasicItemModel.Unbaked(fluidTank9k, tintSources),
                                        ItemModels.condition(
                                                new FluidTankProperty12k(),
                                                new BasicItemModel.Unbaked(fluidTank12k, tintSources),
                                                ItemModels.condition(
                                                        new FluidTankProperty15k(),
                                                        new BasicItemModel.Unbaked(fluidTank15k, tintSources),
                                                        ItemModels.condition(
                                                                new FluidTankProperty18k(),
                                                                new BasicItemModel.Unbaked(fluidTank18k, tintSources),
                                                                ItemModels.condition(
                                                                        new FluidTankProperty21k(),
                                                                        new BasicItemModel.Unbaked(fluidTank21k, tintSources),
                                                                        ItemModels.condition(
                                                                                new FluidTankProperty24k(),
                                                                                new BasicItemModel.Unbaked(fluidTank24k, tintSources),
                                                                                ItemModels.condition(
                                                                                        new FluidTankProperty27k(),
                                                                                        new BasicItemModel.Unbaked(fluidTank27k, tintSources),
                                                                                        ItemModels.condition(
                                                                                                new FluidTankProperty30k(),
                                                                                                new BasicItemModel.Unbaked(fluidTank30k, tintSources),
                                                                                                ItemModels.condition(
                                                                                                        new FluidTankProperty33k(),
                                                                                                        new BasicItemModel.Unbaked(fluidTank33k, tintSources),
                                                                                                        ItemModels.condition(
                                                                                                                new FluidTankProperty36k(),
                                                                                                                new BasicItemModel.Unbaked(fluidTank36k, tintSources),
                                                                                                                ItemModels.condition(
                                                                                                                        new FluidTankProperty40k(),
                                                                                                                        new BasicItemModel.Unbaked(fluidTank40k, tintSources),
                                                                                                                        ItemModels.condition(
                                                                                                                                new FluidTankProperty45k(),
                                                                                                                                new BasicItemModel.Unbaked(fluidTank45k, tintSources),
                                                                                                                                new BasicItemModel.Unbaked(fluidTankFull, tintSources)
                                                                                                                        )
                                                                                                                )
                                                                                                        )
                                                                                                )
                                                                                        )
                                                                                )
                                                                        )
                                                                )
                                                        )
                                                )
                                        )
                                )
                        )
                )
        ));
    }

    public BlockStateVariantMap createNorthDefaultHorizontalRotationStatesInverted() {
        return BlockStateVariantMap.create(Properties.HORIZONTAL_FACING)
                .register(Direction.EAST, BlockStateVariant.create().put(VariantSettings.Y, VariantSettings.Rotation.R270))
                .register(Direction.NORTH, BlockStateVariant.create().put(VariantSettings.Y, VariantSettings.Rotation.R180))
                .register(Direction.WEST, BlockStateVariant.create().put(VariantSettings.Y, VariantSettings.Rotation.R90))
                .register(Direction.SOUTH, BlockStateVariant.create());
    }

    private void slimeBlock(BlockStateModelGenerator blockStateModelGenerator, SlimeBlock block){
        blockStateModelGenerator.blockStateCollector
                .accept(VariantsBlockStateSupplier.create(block, BlockStateVariant.create().put(VariantSettings.MODEL, blockLocation("template_slime_block")))
                );
        blockStateModelGenerator.registerTintedItemModel(block, blockLocation("template_slime_block"), ItemModels.constantTintSource(block.getColor()));
    }

    public final void registerNorthDefaultHorizontalRotationInverted(BlockStateModelGenerator blockStateModelGenerator, Block block) {
        blockStateModelGenerator.blockStateCollector
                .accept(
                        VariantsBlockStateSupplier.create(block, BlockStateVariant.create().put(VariantSettings.MODEL, ModelIds.getBlockModelId(block)))
                                .coordinate(createNorthDefaultHorizontalRotationStatesInverted())
                );
    }

    private void slimeItem(ItemModelGenerator itemModels, Item item){
        Identifier model = itemLocation("slime_item");
        itemModels.output.accept(item, new BasicItemModel.Unbaked(model, List.of(new SlimeItemTint(-1), new SlimeItemTint(-1))));
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
