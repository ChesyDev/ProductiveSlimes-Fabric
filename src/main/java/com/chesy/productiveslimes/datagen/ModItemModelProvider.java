package com.chesy.productiveslimes.datagen;

import com.chesy.productiveslimes.ProductiveSlimes;
import com.chesy.productiveslimes.item.ModItems;
import com.chesy.productiveslimes.item.custom.DnaItem;
import com.chesy.productiveslimes.item.custom.SlimeballItem;
import com.chesy.productiveslimes.item.custom.SpawnEggItem;
import net.fabricmc.fabric.api.client.datagen.v1.provider.FabricModelProvider;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.minecraft.client.data.*;
import net.minecraft.client.render.item.model.BasicItemModel;
import net.minecraft.client.render.item.tint.ConstantTintSource;
import net.minecraft.client.render.item.tint.TintSource;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;

import java.util.*;

public class ModItemModelProvider extends FabricModelProvider {

    public ModItemModelProvider(FabricDataOutput output) {
        super(output);
    }

    @Override
    public void generateBlockStateModels(BlockStateModelGenerator blockStateModelGenerator) {

    }

    @Override
    public void generateItemModels(ItemModelGenerator itemModelGenerator) {
        itemModelGenerator.register(ModItems.GUIDEBOOK, Models.GENERATED);
        itemModelGenerator.register(ModItems.ENERGY_MULTIPLIER_UPGRADE, Models.GENERATED);
        itemModelGenerator.register(ModItems.SLIMEBALL_FRAGMENT, Models.GENERATED);

        slimeballItem(itemModelGenerator, ModItems.ENERGY_SLIME_BALL);
        dnaItem(itemModelGenerator, ModItems.SLIME_DNA);

        itemModelGenerator.registerSpawnEgg(ModItems.ENERGY_SLIME_SPAWN_EGG, ModItems.ENERGY_SLIME_SPAWN_EGG.getBg(), ModItems.ENERGY_SLIME_SPAWN_EGG.getFg());
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
}
