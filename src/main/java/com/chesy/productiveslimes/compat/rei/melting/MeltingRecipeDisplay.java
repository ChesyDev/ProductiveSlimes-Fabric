package com.chesy.productiveslimes.compat.rei.melting;

import com.chesy.productiveslimes.recipe.MeltingRecipe;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.display.basic.BasicDisplay;
import me.shedaniel.rei.api.common.entry.EntryIngredient;
import me.shedaniel.rei.api.common.util.EntryIngredients;
import me.shedaniel.rei.api.common.util.EntryStacks;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;

import java.util.List;

public class MeltingRecipeDisplay extends BasicDisplay {
    private final int energy;
    private final int inputCount;

    public MeltingRecipeDisplay(MeltingRecipe recipe) {
        super(List.of(EntryIngredients.of(new ItemStack(recipe.getInputItems().getFirst().getMatchingStacks()[0].getItem(), recipe.getInputCount())),
                        EntryIngredients.of(new ItemStack(Items.BUCKET, recipe.getOutputs().getFirst().getCount()))),
                List.of(EntryIngredient.of(EntryStacks.of(recipe.getOutputs().getFirst()))));

        energy = recipe.getEnergy();
        inputCount = recipe.getInputCount();
    }

    public int getEnergy() {
        return energy;
    }

    public int getInputCount() {
        return inputCount;
    }

    @Override
    public CategoryIdentifier<?> getCategoryIdentifier() {
        return MeltingCategory.MELTING;
    }
}
