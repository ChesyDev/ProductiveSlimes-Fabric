package com.chesy.productiveslimes.datagen;

import com.chesy.productiveslimes.ProductiveSlimes;
import com.chesy.productiveslimes.block.ModBlocks;
import com.chesy.productiveslimes.item.ModItems;
import com.chesy.productiveslimes.item.custom.BucketItem;
import com.chesy.productiveslimes.item.custom.DnaItem;
import com.chesy.productiveslimes.item.custom.SlimeballItem;
import com.chesy.productiveslimes.item.custom.SpawnEggItem;
import com.chesy.productiveslimes.tier.ModTierLists;
import com.chesy.productiveslimes.tier.ModTiers;
import com.chesy.productiveslimes.tier.Tier;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.fabricmc.fabric.api.client.datagen.v1.provider.FabricModelProvider;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.minecraft.block.Block;
import net.minecraft.client.data.*;
import net.minecraft.client.render.item.model.BasicItemModel;
import net.minecraft.client.render.item.tint.ConstantTintSource;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.state.property.Properties;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;

import java.util.*;
import java.util.function.Consumer;

public class ModModelProvider extends FabricModelProvider {

    public ModModelProvider(FabricDataOutput output) {
        super(output);
    }

    @Override
    public void generateBlockStateModels(BlockStateModelGenerator blockStateModelGenerator) {
        registerNorthDefaultHorizontalRotationInverted(blockStateModelGenerator, ModBlocks.MELTING_STATION);
    }

    @Override
    public void generateItemModels(ItemModelGenerator itemModelGenerator) {
        itemModelGenerator.register(ModItems.GUIDEBOOK, Models.GENERATED);
        itemModelGenerator.register(ModItems.ENERGY_MULTIPLIER_UPGRADE, Models.GENERATED);
        itemModelGenerator.register(ModItems.SLIMEBALL_FRAGMENT, Models.GENERATED);

        slimeballItem(itemModelGenerator, ModItems.ENERGY_SLIME_BALL);
        dnaItem(itemModelGenerator, ModItems.SLIME_DNA);
        itemModelGenerator.registerSpawnEgg(ModItems.ENERGY_SLIME_SPAWN_EGG, ModItems.ENERGY_SLIME_SPAWN_EGG.getBg(), ModItems.ENERGY_SLIME_SPAWN_EGG.getFg());

        for (Tier tier : Tier.values()){
            ModTiers tiers = ModTierLists.getTierByName(tier);

            slimeballItem(itemModelGenerator, ModTierLists.getSlimeballItemByName(tiers.name()));
            bucketItem(itemModelGenerator, ModTierLists.getBucketItemByName(tiers.name()));
            dnaItem(itemModelGenerator, ModTierLists.getDnaItemByName(tiers.name()));
            SpawnEggItem spawnEggItem = ModTierLists.getSpawnEggItemByName(tiers.name());
            itemModelGenerator.registerSpawnEgg(spawnEggItem, spawnEggItem.getBg(), spawnEggItem.getFg());
        }
    }

    private static void dnaItem(ItemModelGenerator itemModelGenerator, DnaItem item) {
        Identifier identifier = Identifier.of(ProductiveSlimes.MOD_ID,"item/template_dna");
        Identifier model = Models.GENERATED.upload(item, TextureMap.of(TextureKey.LAYER0, identifier), itemModelGenerator.modelCollector);

        itemModelGenerator.output.accept(item, new BasicItemModel.Unbaked(model, Collections.singletonList(new ConstantTintSource(item.getColor()))));
    }

    private static void slimeballItem(ItemModelGenerator itemModelGenerator, SlimeballItem item) {
        Identifier identifier = Identifier.of(ProductiveSlimes.MOD_ID,"item/template_slimeball");
        Identifier model = Models.GENERATED.upload(item, TextureMap.of(TextureKey.LAYER0, identifier), itemModelGenerator.modelCollector);

        itemModelGenerator.output.accept(item, new BasicItemModel.Unbaked(model, Collections.singletonList(new ConstantTintSource(item.getColor()))));
    }

    private static void bucketItem(ItemModelGenerator itemModelGenerator, BucketItem item){
        TextureMap textures = new TextureMap()
                .put(TextureKey.LAYER0, Identifier.of(ProductiveSlimes.MOD_ID, "item/bucket"))
                .put(TextureKey.LAYER1, Identifier.of(ProductiveSlimes.MOD_ID, "item/bucket_fluid"));

        Identifier model = Models.GENERATED_TWO_LAYERS.upload(item, textures, itemModelGenerator.modelCollector);

        itemModelGenerator.output.accept(item, new BasicItemModel.Unbaked(model, List.of(new ConstantTintSource(-1),new ConstantTintSource(item.getColor()))));
    }

    public static BlockStateVariantMap createNorthDefaultHorizontalRotationStatesInverted() {
        return BlockStateVariantMap.create(Properties.HORIZONTAL_FACING)
                .register(Direction.EAST, BlockStateVariant.create().put(VariantSettings.Y, VariantSettings.Rotation.R270))
                .register(Direction.NORTH, BlockStateVariant.create().put(VariantSettings.Y, VariantSettings.Rotation.R180))
                .register(Direction.WEST, BlockStateVariant.create().put(VariantSettings.Y, VariantSettings.Rotation.R90))
                .register(Direction.SOUTH, BlockStateVariant.create());
    }

    public final void registerNorthDefaultHorizontalRotationInverted(BlockStateModelGenerator blockStateModelGenerator, Block block) {
        blockStateModelGenerator.blockStateCollector
                .accept(
                        VariantsBlockStateSupplier.create(block, BlockStateVariant.create().put(VariantSettings.MODEL, ModelIds.getBlockModelId(block)))
                                .coordinate(createNorthDefaultHorizontalRotationStatesInverted())
                );
    }
}
