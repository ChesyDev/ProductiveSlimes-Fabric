package com.chesy.productiveslimes.datagen;

import com.chesy.productiveslimes.ProductiveSlimes;
import com.chesy.productiveslimes.datagen.builder.MeltingRecipeBuilder;
import com.chesy.productiveslimes.datagen.builder.SolidingRecipeBuilder;
import com.chesy.productiveslimes.tier.ModTiers;
import com.chesy.productiveslimes.tier.ModTier;
import com.chesy.productiveslimes.tier.Tier;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.fabricmc.fabric.api.tag.convention.v2.ConventionalItemTags;
import net.minecraft.advancement.AdvancementCriterion;
import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.advancement.criterion.InventoryChangedCriterion;
import net.minecraft.data.recipe.RecipeExporter;
import net.minecraft.data.recipe.RecipeGenerator;
import net.minecraft.data.recipe.ShapedRecipeJsonBuilder;
import net.minecraft.data.recipe.ShapelessRecipeJsonBuilder;
import net.minecraft.item.Item;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.predicate.NumberRange;
import net.minecraft.predicate.item.ItemPredicate;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.ShapelessRecipe;
import net.minecraft.recipe.book.RecipeCategory;
import net.minecraft.registry.RegistryEntryLookup;
import net.minecraft.registry.RegistryKeys;
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
    protected RecipeGenerator getRecipeGenerator(RegistryWrapper.WrapperLookup registries, RecipeExporter exporter) {
        return new RecipeGenerator(registries, exporter) {
            private final RegistryEntryLookup<Item> items = registries.getOrThrow(RegistryKeys.ITEM);

            @Override
            public void generate() {
                //Override vanilla recipes
                ShapelessRecipeJsonBuilder.create(items, RecipeCategory.MISC, Items.STICKY_PISTON, 1)
                        .input(ConventionalItemTags.SLIME_BALLS)
                        .input(Items.PISTON)
                        .criterion(getHasName(Items.SLIME_BALL), has(Items.PISTON))
                        .offerTo(exporter);

                ShapelessRecipeJsonBuilder.create(items, RecipeCategory.MISC, Items.MAGMA_CREAM, 1)
                        .input(ConventionalItemTags.SLIME_BALLS)
                        .input(Items.BLAZE_POWDER)
                        .criterion(getHasName(Items.SLIME_BALL), has(Items.BLAZE_POWDER))
                        .offerTo(exporter);

                ShapedRecipeJsonBuilder.create(items, RecipeCategory.MISC, Items.LEAD,2)
                        .pattern("AA ")
                        .pattern("AB ")
                        .pattern("  A")
                        .input('A', Items.STRING)
                        .input('B', ConventionalItemTags.SLIME_BALLS)
                        .criterion(getHasName(Items.DEEPSLATE), has(Items.LAVA_BUCKET))
                        .offerTo(exporter);

                for (Tier tier : Tier.values()) {
                    ModTier tiers = ModTiers.getTierByName(tier);
                    slimeBlockToSlimeBall(exporter, ModTiers.getBlockByName(tiers.name()), ModTiers.getSlimeballItemByName(tiers.name()));
                    slimeBallToSlimeBlock(exporter, ModTiers.getSlimeballItemByName(tiers.name()), ModTiers.getBlockByName(tiers.name()));

                    meltingRecipe(exporter, ModTiers.getBlockByName(tiers.name()).asItem(), ModTiers.getBucketItemByName(tiers.name()), 2, 5);
                    meltingRecipe(exporter, ModTiers.getSlimeballItemByName(tiers.name()), ModTiers.getBucketItemByName(tiers.name()), 4, 1);

                    solidingRecipe(exporter, ModTiers.getBucketItemByName(tiers.name()), ModTiers.getItemByKey(tiers.solidingOutputKey()), 1, tiers.solidingOutputAmount());
                }
            }

            private void slimeBlockToSlimeBall(RecipeExporter pRecipeOutput, ItemConvertible pSlimeBlock, ItemConvertible pSlimeBall) {
                ShapelessRecipeJsonBuilder.create(items, RecipeCategory.MISC, pSlimeBall, 9)
                        .input(pSlimeBlock)
                        .criterion(getHasName(pSlimeBlock), has(pSlimeBlock))
                        .offerTo(pRecipeOutput, getItemName(pSlimeBall) + "_from_" + getItemName(pSlimeBlock));
            }

            private void slimeBallToSlimeBlock(RecipeExporter pRecipeOutput, ItemConvertible pSlimeBall, ItemConvertible pSlimeBlock) {
                ShapedRecipeJsonBuilder.create(items, RecipeCategory.BUILDING_BLOCKS, pSlimeBlock, 1)
                        .pattern("AAA")
                        .pattern("AAA")
                        .pattern("AAA")
                        .input('A', pSlimeBall)
                        .criterion(getHasName(pSlimeBall), has(pSlimeBall))
                        .offerTo(pRecipeOutput, getItemName(pSlimeBlock) + "_from_" + getItemName(pSlimeBall));
            }

            private void meltingRecipe(RecipeExporter pRecipeOutput, Item pIngredient, Item pResult, int pInputCount, int outputCount) {
                MeltingRecipeBuilder.meltingRecipe()
                        .addIngredient(Ingredient.ofItem(pIngredient))
                        .setInputCount(pInputCount)
                        .addOutput(new ItemStack(pResult, outputCount))
                        .setEnergy(200)
                        .criterion(getHasName(pIngredient), has(pIngredient))
                        .offerTo(pRecipeOutput, Identifier.of(ProductiveSlimes.MOD_ID, "melting/" + getItemName(pIngredient) + "_melting").toString());
            }

            private void solidingRecipe(RecipeExporter pRecipeOutput, Item pIngredient, Item pResult, int pInputCount, int outputCount) {
                SolidingRecipeBuilder.solidingRecipe()
                        .addIngredient(Ingredient.ofItem(pIngredient))
                        .setInputCount(pInputCount)
                        .addOutput(new ItemStack(pResult, outputCount))
                        .addOutput(new ItemStack(Items.BUCKET, pInputCount))
                        .setEnergy(200)
                        .criterion(getHasName(pIngredient), has(pIngredient))
                        .offerTo(pRecipeOutput, Identifier.of(ProductiveSlimes.MOD_ID, "soliding/" + getItemName(pIngredient) + "_soliding").toString());
            }

            private String getItemName(ItemConvertible item) {
                String name = item.asItem().getName().getString();
                return name.substring(name.lastIndexOf('.') + 1);
            }

            private AdvancementCriterion<?> has(NumberRange.IntRange pCount, ItemConvertible item) {
                return inventoryTrigger(ItemPredicate.Builder.create().items(this.items, item).count(pCount).build());
            }

            private AdvancementCriterion<?> has(ItemConvertible item) {
                return inventoryTrigger(ItemPredicate.Builder.create().items(this.items, item));
            }

            private AdvancementCriterion<?> has(TagKey<Item> pTag) {
                return inventoryTrigger(ItemPredicate.Builder.create().tag(this.items, pTag));
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
        };
    }

    @Override
    public String getName() {
        return null;
    }
}
