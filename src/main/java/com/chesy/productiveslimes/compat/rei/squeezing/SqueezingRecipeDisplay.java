package com.chesy.productiveslimes.compat.rei.squeezing;

import com.chesy.productiveslimes.recipe.SqueezingRecipe;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.display.basic.BasicDisplay;
import me.shedaniel.rei.api.common.entry.EntryIngredient;
import me.shedaniel.rei.api.common.entry.EntryStack;
import me.shedaniel.rei.api.common.util.EntryIngredients;
import me.shedaniel.rei.api.common.util.EntryStacks;
import net.minecraft.item.ItemStack;

import java.util.List;

public class SqueezingRecipeDisplay extends BasicDisplay {
    private final int energy;
    private final EntryStack<ItemStack> inputItem;

    public SqueezingRecipeDisplay(SqueezingRecipe recipe) {
        super(
                List.of(EntryIngredients.ofIngredient(recipe.inputItems().get(0))),
                List.of(
                        EntryIngredient.of(EntryStacks.of(recipe.output().get(0))),
                        EntryIngredient.of(EntryStacks.of(recipe.output().get(1)))
                )
        );
        energy = recipe.energy();
        inputItem = EntryStacks.of(new ItemStack(recipe.inputItems().get(0).getMatchingStacks()[0].getItem()));
    }

    public SqueezingRecipeDisplay(List<EntryIngredient> input, List<EntryIngredient> output, int energy) {
        super(input, output);
        this.energy = energy;
        this.inputItem = (EntryStack<ItemStack>) input.get(0).get(0);
    }

    public int getEnergy() {
        return energy;
    }

    public EntryStack<ItemStack> getInputItem() {
        return inputItem;
    }

    @Override
    public CategoryIdentifier<?> getCategoryIdentifier() {
        return SqueezingCategory.SQUEEZING;
    }
}