package com.chesy.productiveslimes.datagen;

import com.chesy.productiveslimes.ProductiveSlimes;
import com.chesy.productiveslimes.block.ModBlocks;
import com.chesy.productiveslimes.datagen.builder.*;
import com.chesy.productiveslimes.item.ModItems;
import com.chesy.productiveslimes.tier.ModTiers;
import com.chesy.productiveslimes.tier.ModTier;
import com.chesy.productiveslimes.tier.Tier;
import com.chesy.productiveslimes.util.ModTags;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.fabricmc.fabric.api.tag.convention.v2.ConventionalItemTags;
import net.minecraft.advancement.AdvancementCriterion;
import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.advancement.criterion.InventoryChangedCriterion;
import net.minecraft.data.server.recipe.*;
import net.minecraft.item.Item;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.predicate.NumberRange;
import net.minecraft.predicate.item.ItemPredicate;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.book.RecipeCategory;
import net.minecraft.registry.RegistryEntryLookup;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public class ModRecipeProvider extends FabricRecipeProvider {
    public ModRecipeProvider(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture) {
        super(output, registriesFuture);
    }

    @Override
    public void generate(RecipeExporter exporter) {
        //Override vanilla recipes
        ShapelessRecipeJsonBuilder.create(RecipeCategory.MISC, Items.STICKY_PISTON, 1)
                .input(ConventionalItemTags.SLIME_BALLS)
                .input(Items.PISTON)
                .criterion(getHasName(Items.SLIME_BALL), has(Items.PISTON))
                .offerTo(exporter);

        ShapelessRecipeJsonBuilder.create(RecipeCategory.MISC, Items.MAGMA_CREAM, 1)
                .input(ConventionalItemTags.SLIME_BALLS)
                .input(Items.BLAZE_POWDER)
                .criterion(getHasName(Items.SLIME_BALL), has(Items.BLAZE_POWDER))
                .offerTo(exporter);

        ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, Items.LEAD, 2)
                .pattern("AA ")
                .pattern("AB ")
                .pattern("  A")
                .input('A', Items.STRING)
                .input('B', ConventionalItemTags.SLIME_BALLS)
                .criterion(getHasName(Items.DEEPSLATE), has(Items.LAVA_BUCKET))
                .offerTo(exporter);

        //Mod Recipe
        ShapedRecipeJsonBuilder.create(RecipeCategory.BUILDING_BLOCKS, ModBlocks.MELTING_STATION, 1)
                .pattern("AAA")
                .pattern("ABA")
                .pattern("AAA")
                .input('A', Items.DEEPSLATE)
                .input('B', Items.LAVA_BUCKET)
                .criterion(getHasName(Items.DEEPSLATE), has(Items.LAVA_BUCKET))
                .offerTo(exporter);

        ShapedRecipeJsonBuilder.create(RecipeCategory.BUILDING_BLOCKS, ModBlocks.SOLIDING_STATION, 1)
                .pattern("AAA")
                .pattern("ABA")
                .pattern("AAA")
                .input('A', Items.DEEPSLATE)
                .input('B', Items.WATER_BUCKET)
                .criterion(getHasName(Items.DEEPSLATE), has(Items.WATER_BUCKET))
                .offerTo(exporter);

        ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, ModItems.ENERGY_SLIME_SPAWN_EGG, 1)
                .pattern("CAC")
                .pattern("ABA")
                .pattern("CAC")
                .input('A', Items.SLIME_BALL)
                .input('B', Items.EGG)
                .input('C', Items.REDSTONE)
                .criterion(getHasName(Items.SLIME_BALL), has(Items.REDSTONE))
                .offerTo(exporter);

        ShapedRecipeJsonBuilder.create(RecipeCategory.BUILDING_BLOCKS, ModBlocks.ENERGY_GENERATOR, 1)
                .pattern("CAC")
                .pattern("ABA")
                .pattern("CAC")
                .input('A', ProductiveSlimes.ENERGY_SLIME_BALL)
                .input('B', Items.COPPER_BLOCK)
                .input('C', Items.REDSTONE)
                .criterion(getHasName(Items.SLIME_BALL), has(Items.REDSTONE))
                .offerTo(exporter);

        ShapedRecipeJsonBuilder.create(RecipeCategory.BUILDING_BLOCKS, ModBlocks.CABLE, 8)
                .pattern(" A ")
                .pattern("ABA")
                .pattern(" A ")
                .input('A', Items.REDSTONE)
                .input('B', Items.COPPER_INGOT)
                .criterion(getHasName(Items.COPPER_INGOT), has(Items.REDSTONE))
                .offerTo(exporter);

        ShapedRecipeJsonBuilder.create(RecipeCategory.BUILDING_BLOCKS, ModBlocks.DNA_EXTRACTOR, 1)
                .pattern("AAA")
                .pattern("ACA")
                .pattern("ABA")
                .input('A', Items.IRON_INGOT)
                .input('B', ProductiveSlimes.ENERGY_SLIME_BALL)
                .input('C', Items.GLASS)
                .criterion(getHasName(Items.IRON_INGOT), has(Items.GLASS))
                .offerTo(exporter);

        ShapedRecipeJsonBuilder.create(RecipeCategory.BUILDING_BLOCKS, ModBlocks.DNA_SYNTHESIZER, 1)
                .pattern("AAA")
                .pattern("CCC")
                .pattern("ABA")
                .input('A', Items.IRON_INGOT)
                .input('B', ProductiveSlimes.ENERGY_SLIME_BALL)
                .input('C', Items.GLASS)
                .criterion(getHasName(Items.IRON_INGOT), has(Items.GLASS))
                .offerTo(exporter);

        ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, ModItems.ENERGY_MULTIPLIER_UPGRADE, 1)
                .pattern("ABA")
                .pattern("BCB")
                .pattern("ABA")
                .input('A', Items.IRON_INGOT)
                .input('B', ProductiveSlimes.ENERGY_SLIME_BALL)
                .input('C', Items.BLUE_WOOL)
                .criterion(getHasName(Items.COPPER_INGOT), has(Items.REDSTONE))
                .offerTo(exporter);

        ShapedRecipeJsonBuilder.create(RecipeCategory.BUILDING_BLOCKS, ModBlocks.FLUID_TANK, 1)
                .pattern("AAA")
                .pattern("BCB")
                .pattern("AAA")
                .input('A', Items.IRON_INGOT)
                .input('B', Items.GLASS)
                .input('C', Items.BUCKET)
                .criterion(getHasName(Items.IRON_INGOT), has(Items.GLASS))
                .offerTo(exporter);

        ShapelessRecipeJsonBuilder.create(RecipeCategory.MISC, ModItems.GUIDEBOOK, 1)
                .input(Items.BOOK)
                .input(ConventionalItemTags.SLIME_BALLS)
                .criterion(getHasName(Items.BOOK), has(Items.SLIME_BALL))
                .offerTo(exporter);

        ShapelessRecipeJsonBuilder.create(RecipeCategory.MISC, ModItems.SLIMEBALL_FRAGMENT, 4)
                .input(Items.SLIME_BALL)
                .criterion(getHasName(Items.SLIME_BALL), has(Items.SLIME_BALL))
                .offerTo(exporter);

        ShapedRecipeJsonBuilder.create(RecipeCategory.BUILDING_BLOCKS, Items.SLIME_BALL, 1)
                .pattern("AA ")
                .pattern("AA ")
                .pattern("   ")
                .input('A', ModItems.SLIMEBALL_FRAGMENT)
                .criterion(getHasName(ModItems.SLIMEBALL_FRAGMENT), has(ModItems.SLIMEBALL_FRAGMENT))
                .offerTo(exporter, "slimeball_from_fragment");

        ShapedRecipeJsonBuilder.create(RecipeCategory.BUILDING_BLOCKS, ModBlocks.SQUEEZER, 1)
                .pattern(" A ")
                .pattern(" A ")
                .pattern("AAA")
                .input('A', ModBlocks.SLIMY_PLANKS)
                .criterion(getHasName(ModBlocks.SLIMY_PLANKS), has(ModBlocks.SLIMY_PLANKS))
                .offerTo(exporter);

        ShapedRecipeJsonBuilder.create(RecipeCategory.BUILDING_BLOCKS, ModBlocks.SLIME_SQUEEZER, 1)
                .pattern("BAB")
                .pattern("C  ")
                .pattern("BBB")
                .input('A', ModBlocks.SQUEEZER)
                .input('B', ModBlocks.SLIMY_STONE)
                .input('C', ProductiveSlimes.ENERGY_SLIME_BALL)
                .criterion(getHasName(ModBlocks.SQUEEZER), has(ModBlocks.SQUEEZER))
                .offerTo(exporter);

        ShapedRecipeJsonBuilder.create(RecipeCategory.BUILDING_BLOCKS, ModBlocks.SLIME_NEST, 1)
                .pattern("BBB")
                .pattern("BCB")
                .pattern("AAA")
                .input('A', ModBlocks.SLIMY_GRASS_BLOCK)
                .input('B', Items.GLASS_PANE)
                .input('C', ConventionalItemTags.SLIME_BALLS)
                .criterion(getHasName(ModBlocks.SLIMY_GRASS_BLOCK), has(Items.GLASS_PANE))
                .offerTo(exporter);

        ShapedRecipeJsonBuilder.create(RecipeCategory.BUILDING_BLOCKS, ModBlocks.SLIMEBALL_COLLECTOR, 1)
                .pattern("ABA")
                .pattern("BCB")
                .pattern("ABA")
                .input('A', Items.IRON_INGOT)
                .input('B', Items.HOPPER)
                .input('C', ConventionalItemTags.CHESTS)
                .criterion(getHasName(Items.HOPPER), has(ConventionalItemTags.CHESTS))
                .offerTo(exporter);

        ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, ModItems.SLIME_NEST_SPEED_UPGRADE_1, 1)
                .pattern("ABA")
                .pattern("BCB")
                .pattern("ABA")
                .input('A', Items.REDSTONE_BLOCK)
                .input('B', ModTiers.getBlockByName(Tier.IRON.getTierName()))
                .input('C', ConventionalItemTags.IRON_INGOTS)
                .criterion(getHasName(Items.REDSTONE_BLOCK), has(ModTiers.getBlockByName(Tier.IRON.getTierName())))
                .offerTo(exporter);

        ShapedRecipeJsonBuilder.create(RecipeCategory.BUILDING_BLOCKS, ModItems.SLIME_NEST_SPEED_UPGRADE_2, 1)
                .pattern("ABA")
                .pattern("BCB")
                .pattern("ABA")
                .input('A', ModItems.SLIME_NEST_SPEED_UPGRADE_1)
                .input('B', ModTiers.getBlockByName(Tier.GOLD.getTierName()))
                .input('C', ConventionalItemTags.GOLD_INGOTS)
                .criterion(getHasName(ModItems.SLIME_NEST_SPEED_UPGRADE_1), has(ModTiers.getBlockByName(Tier.GOLD.getTierName())))
                .offerTo(exporter);

        offerPlanksRecipe(exporter, ModBlocks.SLIMY_PLANKS, ModTags.Items.SLIMY_LOG, 4);
        offerBarkBlockRecipe(exporter, ModBlocks.SLIMY_WOOD, ModBlocks.SLIMY_LOG);
        offerBarkBlockRecipe(exporter, ModBlocks.STRIPPED_SLIMY_WOOD, ModBlocks.STRIPPED_SLIMY_LOG);
        createStairsRecipe(ModBlocks.SLIMY_STAIRS, Ingredient.ofItems(ModBlocks.SLIMY_PLANKS)).group("slimy").criterion("has_slimy", has(ModBlocks.SLIMY_PLANKS)).offerTo(exporter);
        offerSlabRecipe(exporter, RecipeCategory.BUILDING_BLOCKS, ModBlocks.SLIMY_SLAB, ModBlocks.SLIMY_PLANKS);
        createButtonRecipe(exporter, ModBlocks.SLIMY_BUTTON, Ingredient.ofItems(ModBlocks.SLIMY_PLANKS));
        offerPressurePlateRecipe(exporter, ModBlocks.SLIMY_PRESSURE_PLATE, ModBlocks.SLIMY_PLANKS);
        createFenceRecipe(ModBlocks.SLIMY_FENCE, Ingredient.ofItems(ModBlocks.SLIMY_PLANKS)).group("slimy").criterion("has_slimy", has(ModBlocks.SLIMY_PLANKS)).offerTo(exporter);
        createFenceGateRecipe(ModBlocks.SLIMY_FENCE_GATE, Ingredient.ofItems(ModBlocks.SLIMY_PLANKS)).group("slimy").criterion("has_slimy", has(ModBlocks.SLIMY_PLANKS)).offerTo(exporter);
        createDoorRecipe(ModBlocks.SLIMY_DOOR, Ingredient.ofItems(ModBlocks.SLIMY_PLANKS)).group("slimy").criterion("has_slimy", has(ModBlocks.SLIMY_PLANKS)).offerTo(exporter);
        createTrapdoorRecipe(ModBlocks.SLIMY_TRAPDOOR, Ingredient.ofItems(ModBlocks.SLIMY_PLANKS)).group("slimy").criterion("has_slimy", has(ModBlocks.SLIMY_PLANKS)).offerTo(exporter);
        createStairsRecipe(ModBlocks.SLIMY_STONE_STAIRS, Ingredient.ofItems(ModBlocks.SLIMY_STONE)).group("slimy_stone").criterion("has_slimy_stone", has(ModBlocks.SLIMY_STONE)).offerTo(exporter);
        offerSlabRecipe(exporter, RecipeCategory.BUILDING_BLOCKS, ModBlocks.SLIMY_STONE_SLAB, ModBlocks.SLIMY_STONE);
        createButtonRecipe(exporter, ModBlocks.SLIMY_STONE_BUTTON, Ingredient.ofItems(ModBlocks.SLIMY_STONE));
        offerPressurePlateRecipe(exporter, ModBlocks.SLIMY_STONE_PRESSURE_PLATE, ModBlocks.SLIMY_STONE);
        createStairsRecipe(ModBlocks.SLIMY_COBBLESTONE_STAIRS, Ingredient.ofItems(ModBlocks.SLIMY_COBBLESTONE)).group("slimy_cobblestone").criterion("has_slimy_cobblestone", has(ModBlocks.SLIMY_COBBLESTONE)).offerTo(exporter);
        offerSlabRecipe(exporter, RecipeCategory.BUILDING_BLOCKS, ModBlocks.SLIMY_COBBLESTONE_SLAB, ModBlocks.SLIMY_COBBLESTONE);
        offerWallRecipe(exporter, RecipeCategory.BUILDING_BLOCKS, ModBlocks.SLIMY_COBBLESTONE_WALL, ModBlocks.SLIMY_COBBLESTONE);
        createStairsRecipe(ModBlocks.SLIMY_COBBLED_DEEPSLATE_STAIRS, Ingredient.ofItems(ModBlocks.SLIMY_COBBLED_DEEPSLATE)).group("slimy_cobbled_deepslate").criterion("has_slimy_cobbled_deepslate", has(ModBlocks.SLIMY_COBBLED_DEEPSLATE)).offerTo(exporter);
        offerSlabRecipe(exporter, RecipeCategory.BUILDING_BLOCKS, ModBlocks.SLIMY_COBBLED_DEEPSLATE_SLAB, ModBlocks.SLIMY_COBBLED_DEEPSLATE);
        offerWallRecipe(exporter, RecipeCategory.BUILDING_BLOCKS, ModBlocks.SLIMY_COBBLED_DEEPSLATE_WALL, ModBlocks.SLIMY_COBBLED_DEEPSLATE);

        smeltingRecipe(exporter, ModBlocks.SLIMY_COBBLESTONE, ModBlocks.SLIMY_STONE, 0.1f, 200);
        smeltingRecipe(exporter, ModBlocks.SLIMY_COBBLED_DEEPSLATE, ModBlocks.SLIMY_DEEPSLATE, 0.1f, 200);

        //Slime Ball Recipe
        slimeBlockToSlimeBall(exporter, ModBlocks.ENERGY_SLIME_BLOCK, ProductiveSlimes.ENERGY_SLIME_BALL);
        slimeBallToSlimeBlock(exporter, ProductiveSlimes.ENERGY_SLIME_BALL, ModBlocks.ENERGY_SLIME_BLOCK);

        dnaExtractingRecipe(exporter, Items.SLIME_BALL, ModItems.SLIME_DNA, 1, 0.9f);

        for (Tier tier : Tier.values()) {
            ModTier tiers = ModTiers.getTierByName(tier);
            slimeBlockToSlimeBall(exporter, ModTiers.getBlockByName(tiers.name()), ModTiers.getSlimeballItemByName(tiers.name()));
            slimeBallToSlimeBlock(exporter, ModTiers.getSlimeballItemByName(tiers.name()), ModTiers.getBlockByName(tiers.name()));

            meltingRecipe(exporter, ModTiers.getBlockByName(tiers.name()).asItem(), ModTiers.getBucketItemByName(tiers.name()), 2, 5);
            meltingRecipe(exporter, ModTiers.getSlimeballItemByName(tiers.name()), ModTiers.getBucketItemByName(tiers.name()), 4, 1);

            solidingRecipe(exporter, ModTiers.getBucketItemByName(tiers.name()), ModTiers.getItemByKey(tiers.solidingOutputKey()), 1, tiers.solidingOutputAmount());

            dnaExtractingRecipe(exporter, ModTiers.getSlimeballItemByName(tiers.name()), ModTiers.getDnaItemByName(tiers.name()), 1, tiers.dnaOutputChance());

            dnaSynthesizingSelfRecipe(exporter, ModTiers.getSpawnEggItemByName(tiers.name()), 2, ModTiers.getDnaItemByName(tiers.name()), ModTiers.getDnaItemByName(tiers.name()), ModTiers.getItemByKey(tiers.synthesizingInputItemKey()));
            dnaSynthesizingRecipe(exporter, ModTiers.getSpawnEggItemByName(tiers.name()), 4, ModTiers.getItemByKey(tiers.synthesizingInputDnaKey1()), ModTiers.getItemByKey(tiers.synthesizingInputDnaKey2()), ModTiers.getItemByKey(tiers.synthesizingInputItemKey()));
        }

        squeezingRecipe(exporter, ModBlocks.SLIMY_DIRT, new ItemStack(Items.DIRT, 1), new ItemStack(ModItems.SLIMEBALL_FRAGMENT, 1));
        squeezingRecipe(exporter, ModBlocks.SLIMY_GRASS_BLOCK, new ItemStack(Items.GRASS_BLOCK, 1), new ItemStack(ModItems.SLIMEBALL_FRAGMENT, 1));
        squeezingRecipe(exporter, ModBlocks.SLIMY_STONE, new ItemStack(Items.STONE, 1), new ItemStack(ModItems.SLIMEBALL_FRAGMENT, 1));
        squeezingRecipe(exporter, ModBlocks.SLIMY_DEEPSLATE, new ItemStack(Items.DEEPSLATE, 1), new ItemStack(ModItems.SLIMEBALL_FRAGMENT, 1));
        squeezingRecipe(exporter, ModBlocks.SLIMY_COBBLESTONE, new ItemStack(Items.COBBLESTONE, 1), new ItemStack(ModItems.SLIMEBALL_FRAGMENT, 1));
        squeezingRecipe(exporter, ModBlocks.SLIMY_COBBLED_DEEPSLATE, new ItemStack(Items.COBBLED_DEEPSLATE, 1), new ItemStack(ModItems.SLIMEBALL_FRAGMENT, 1));
    }

    private void slimeBlockToSlimeBall(RecipeExporter pRecipeOutput, ItemConvertible pSlimeBlock, ItemConvertible pSlimeBall) {
        ShapelessRecipeJsonBuilder.create(RecipeCategory.MISC, pSlimeBall, 9)
                .input(pSlimeBlock)
                .criterion(getHasName(pSlimeBlock), has(pSlimeBlock))
                .offerTo(pRecipeOutput, "slimeball/" + getItemName(pSlimeBall) + "_from_" + getItemName(pSlimeBlock));
    }

    private void slimeBallToSlimeBlock(RecipeExporter pRecipeOutput, ItemConvertible pSlimeBall, ItemConvertible pSlimeBlock) {
        ShapedRecipeJsonBuilder.create(RecipeCategory.BUILDING_BLOCKS, pSlimeBlock, 1)
                .pattern("AAA")
                .pattern("AAA")
                .pattern("AAA")
                .input('A', pSlimeBall)
                .criterion(getHasName(pSlimeBall), has(pSlimeBall))
                .offerTo(pRecipeOutput, "slime_block/" + getItemName(pSlimeBlock) + "_from_" + getItemName(pSlimeBall));
    }

    private void meltingRecipe(RecipeExporter pRecipeOutput, Item pIngredient, Item pResult, int pInputCount, int outputCount) {
        MeltingRecipeBuilder.meltingRecipe()
                .addIngredient(Ingredient.ofItems(pIngredient))
                .setInputCount(pInputCount)
                .addOutput(new ItemStack(pResult, outputCount))
                .setEnergy(200)
                .criterion(getHasName(pIngredient), has(pIngredient))
                .offerTo(pRecipeOutput, Identifier.of(ProductiveSlimes.MODID, "melting/" + getItemName(pIngredient) + "_melting").toString());
    }

    private void solidingRecipe(RecipeExporter pRecipeOutput, Item pIngredient, Item pResult, int pInputCount, int outputCount) {
        SolidingRecipeBuilder.solidingRecipe()
                .addIngredient(Ingredient.ofItems(pIngredient))
                .setInputCount(pInputCount)
                .addOutput(new ItemStack(pResult, outputCount))
                .addOutput(new ItemStack(Items.BUCKET, pInputCount))
                .setEnergy(200)
                .criterion(getHasName(pIngredient), has(pIngredient))
                .offerTo(pRecipeOutput, Identifier.of(ProductiveSlimes.MODID, "soliding/" + getItemName(pIngredient) + "_soliding").toString());
    }

    private void dnaExtractingRecipe(RecipeExporter pRecipeOutput, ItemConvertible pIngredient, ItemConvertible pResult, int outputCount, float outputChance) {
        var recipeBuilder = DnaExtractingRecipeBuilder.dnaExtractingRecipe()
                .addIngredient(Ingredient.ofItems(pIngredient))
                .setInputCount(1)
                .addOutput(new ItemStack(pResult, outputCount));

        if (pIngredient != Items.SLIME_BALL) {
            recipeBuilder.addOutput(new ItemStack(Items.SLIME_BALL, 1));
        }

        recipeBuilder.setEnergy(400)
                .setOutputChance(outputChance)
                .criterion(getHasName(pIngredient), has(pIngredient))
                .offerTo(pRecipeOutput, Identifier.of(ProductiveSlimes.MODID, "dna_extracting/" + getItemName(pIngredient) + "_dna_extracting").toString());

    }

    private void dnaSynthesizingSelfRecipe(RecipeExporter pRecipeOutput, ItemConvertible pResult, int inputCount, ItemConvertible... pIngredient) {
        var recipeBuilder = DnaSynthesizingRecipeBuilder.dnaSynthesizingRecipe();

        if (pIngredient.length != 3) {
            throw new IllegalArgumentException("Only accepts 3 ingredients.");
        }

        for (var ingredient : pIngredient) {
            recipeBuilder.addIngredient(Ingredient.ofItems(ingredient));
        }

        recipeBuilder
                .addOutput(new ItemStack(pResult, 1))
                .setInputCount(inputCount)
                .setEnergy(600)
                .criterion(getHasName(Items.EGG), has(Items.EGG))
                .offerTo(pRecipeOutput, Identifier.of(ProductiveSlimes.MODID, "dna_synthesizing/" + getItemName(pResult) + "_dna_synthesizing_self").toString());

    }

    private void dnaSynthesizingRecipe(RecipeExporter pRecipeOutput, ItemConvertible pResult, int inputCount, ItemConvertible... pIngredient) {
        var recipeBuilder = DnaSynthesizingRecipeBuilder.dnaSynthesizingRecipe();

        if (pIngredient.length != 3) {
            throw new IllegalArgumentException("Only accepts 3 ingredients.");
        }

        for (var ingredient : pIngredient) {
            recipeBuilder.addIngredient(Ingredient.ofItems(ingredient));
        }

        recipeBuilder
                .addOutput(new ItemStack(pResult, 1))
                .setInputCount(inputCount)
                .setEnergy(600)
                .criterion(getHasName(Items.EGG), has(Items.EGG))
                .offerTo(pRecipeOutput, Identifier.of(ProductiveSlimes.MODID, "dna_synthesizing/" + getItemName(pResult) + "_dna_synthesizing").toString());

    }

    private void squeezingRecipe(RecipeExporter pRecipeOutput, ItemConvertible pIngredient, ItemStack pResult1, ItemStack pResult2) {
        SqueezingRecipeBuilder.squeezingRecipe()
                .addIngredient(Ingredient.ofItems(pIngredient))
                .addOutput(pResult1)
                .addOutput(pResult2)
                .setEnergy(300)
                .criterion(getHasName(pIngredient), has(pIngredient))
                .offerTo(pRecipeOutput, Identifier.of(ProductiveSlimes.MODID, "squeezing/" + getItemName(pIngredient) + "_squeezing").toString());
    }

    private void smeltingRecipe(RecipeExporter pRecipeOutput, ItemConvertible pIngredient, ItemConvertible pResult, float pExperience, int pCookingTime) {
        CookingRecipeJsonBuilder.createSmelting(Ingredient.ofItems(pIngredient), RecipeCategory.BUILDING_BLOCKS, pResult, pExperience, pCookingTime).criterion(getHasName(pIngredient), has(pIngredient)).offerTo(pRecipeOutput);
    }

    public void createButtonRecipe(RecipeExporter pRecipeOutput, ItemConvertible output, Ingredient input) {
        ShapelessRecipeJsonBuilder.create(RecipeCategory.REDSTONE, output)
                .input(input)
                .criterion(getHasName(output), has(input.getMatchingStacks()[0].getItem()))
                .group(getItemName(input.getMatchingStacks()[0].getItem()))
                .offerTo(pRecipeOutput);
    }

    private String getItemName(ItemConvertible item) {
        if (item.asItem() == Items.SLIME_BALL) return "slimeball";
        String name = item.asItem().getName().getString();
        return name.substring(name.lastIndexOf('.') + 1);
    }

    private AdvancementCriterion<?> has(NumberRange.IntRange pCount, ItemConvertible item) {
        return inventoryTrigger(ItemPredicate.Builder.create().items(item).count(pCount).build());
    }

    private AdvancementCriterion<?> has(ItemConvertible item) {
        return inventoryTrigger(ItemPredicate.Builder.create().items(item));
    }

    private AdvancementCriterion<?> has(TagKey<Item> pTag) {
        return inventoryTrigger(ItemPredicate.Builder.create().tag(pTag));
    }

    private AdvancementCriterion<?> inventoryTrigger(ItemPredicate.Builder... pItems) {
        return inventoryTrigger(Arrays.stream(pItems).map(ItemPredicate.Builder::build).toArray(ItemPredicate[]::new));
    }

    private AdvancementCriterion<?> inventoryTrigger(ItemPredicate... pPredicates) {
        return Criteria.INVENTORY_CHANGED
                .create(new InventoryChangedCriterion.Conditions(Optional.empty(), InventoryChangedCriterion.Conditions.Slots.ANY, List.of(pPredicates)));
    }

    private String getHasName(ItemConvertible pItemLike) {
        return "has_" + getItemName(pItemLike);
    }
}
