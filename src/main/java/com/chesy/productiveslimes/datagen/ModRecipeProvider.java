package com.chesy.productiveslimes.datagen;

import com.chesy.productiveslimes.ProductiveSlimes;
import com.chesy.productiveslimes.datagen.builder.MeltingRecipeBuilder;
import com.chesy.productiveslimes.tier.ModTierLists;
import com.chesy.productiveslimes.tier.ModTiers;
import com.chesy.productiveslimes.tier.Tier;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.advancement.AdvancementCriterion;
import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.advancement.criterion.InventoryChangedCriterion;
import net.minecraft.data.recipe.RecipeExporter;
import net.minecraft.data.recipe.RecipeGenerator;
import net.minecraft.item.Item;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.predicate.NumberRange;
import net.minecraft.predicate.item.ItemPredicate;
import net.minecraft.recipe.Ingredient;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryEntryLookup;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public class ModRecipeProvider extends FabricRecipeProvider {
    private final RegistryEntryLookup<Item> items;

    public ModRecipeProvider(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture) {
        super(output, registriesFuture);

        RegistryWrapper.WrapperLookup wrapper = registriesFuture.join();
        this.items = wrapper.getOrThrow(Registries.ITEM.getKey());
    }


    @Override
    protected RecipeGenerator getRecipeGenerator(RegistryWrapper.WrapperLookup registries, RecipeExporter exporter) {
        RecipeGenerator recipeGenerator = new RecipeGenerator(registries, exporter) {
            @Override
            public void generate() {}
        };

        for (Tier tier : Tier.values()) {
            ModTiers tiers = ModTierLists.getTierByName(tier);
            meltingRecipe(exporter, ModTierLists.getBlockByName(tiers.name()).asItem(), ModTierLists.getBucketItemByName(tiers.name()), 2, 5);
            meltingRecipe(exporter, ModTierLists.getSlimeballItemByName(tiers.name()), ModTierLists.getBucketItemByName(tiers.name()), 4, 1);

            solidingRecipe(exporter, ModTierLists.getBucketItemByName(tiers.name()), ModTierLists.getItemByKey(tiers.growthItemKey()), 1, tiers.solidingOutputAmount());
        }

        return recipeGenerator;
    }

    @Override
    public String getName() {
        return "";
    }

    protected void meltingRecipe(RecipeExporter pRecipeOutput, Item pIngredient, Item pResult, int pInputCount, int outputCount) {
        MeltingRecipeBuilder.meltingRecipe()
                .addIngredient(Ingredient.ofItem(pIngredient))
                .setInputCount(pInputCount)
                .addOutput(new ItemStack(pResult, outputCount))
                .setEnergy(200)
                .criterion(getHasName(pIngredient), has(pIngredient))
                .offerTo(pRecipeOutput, Identifier.of(ProductiveSlimes.MOD_ID, "melting/" + getItemName(pIngredient) + "_melting").toString());
    }

    protected void solidingRecipe(RecipeExporter pRecipeOutput, Item pIngredient, Item pResult, int pInputCount, int outputCount) {
        MeltingRecipeBuilder.meltingRecipe()
                .addIngredient(Ingredient.ofItem(pIngredient))
                .setInputCount(pInputCount)
                .addOutput(new ItemStack(pResult, outputCount))
                .addOutput(new ItemStack(Items.BUCKET, pInputCount))
                .setEnergy(200)
                .criterion(getHasName(pIngredient), has(pIngredient))
                .offerTo(pRecipeOutput, Identifier.of(ProductiveSlimes.MOD_ID, "soliding/" + getItemName(pIngredient) + "_soliding").toString());
    }

    private static String getItemName(Item item) {
        String name = item.getName().getString();
        return name.substring(name.lastIndexOf('.') + 1);
    }

    protected AdvancementCriterion<?> has(NumberRange.IntRange pCount, ItemConvertible item) {
        ;
        return inventoryTrigger(ItemPredicate.Builder.create().items(this.items, item).count(pCount).build());
    }

    protected AdvancementCriterion<?> has(ItemConvertible item) {

        return inventoryTrigger(ItemPredicate.Builder.create().items(this.items, item));
        //return inventoryTrigger(ItemPredicate.Builder.item().of(pItemLike));
    }

    protected AdvancementCriterion<?> has(TagKey<Item> pTag) {
        return inventoryTrigger(ItemPredicate.Builder.create().tag(this.items, pTag));
    }

    protected AdvancementCriterion<?> inventoryTrigger(ItemPredicate.Builder... pItems) {
        return inventoryTrigger(Arrays.stream(pItems).map(ItemPredicate.Builder::build).toArray(ItemPredicate[]::new));
    }

    protected AdvancementCriterion<?> inventoryTrigger(ItemPredicate... pPredicates) {
        return Criteria.INVENTORY_CHANGED
                .create(new InventoryChangedCriterion.Conditions(Optional.empty(), InventoryChangedCriterion.Conditions.Slots.ANY, List.of(pPredicates)));
    }

    protected static String getHasName(Item pItemLike) {
        return "has_" + getItemName(pItemLike);
    }
}
