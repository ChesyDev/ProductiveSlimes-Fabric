package com.chesy.productiveslimes.datagen;

import com.chesy.productiveslimes.item.ModItems;
import net.fabricmc.fabric.api.client.datagen.v1.provider.FabricModelProvider;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.minecraft.client.data.*;
import net.minecraft.item.Item;
import net.minecraft.util.Identifier;

import java.util.Optional;

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

        itemModelGenerator.register(ModItems.ENERGY_SLIME_BALL, Models.GENERATED);
        dnaItem(itemModelGenerator, ModItems.SLIME_DNA);
        itemModelGenerator.register(ModItems.SLIMEBALL_FRAGMENT, Models.GENERATED);
    }


    private static void dnaItem(ItemModelGenerator itemModelGenerator, Item item) {
        Model model = new Model(
                Optional.of(Identifier.ofVanilla("item/generated")),
                Optional.empty(),
                TextureKey.of("layer0")
        );
        itemModelGenerator.register(item, model);
    }
}
