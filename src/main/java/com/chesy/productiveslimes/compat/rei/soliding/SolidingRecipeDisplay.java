package com.chesy.productiveslimes.compat.rei.soliding;

import com.chesy.productiveslimes.recipe.SolidingRecipe;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.display.basic.BasicDisplay;
import me.shedaniel.rei.api.common.entry.EntryIngredient;
import me.shedaniel.rei.api.common.util.EntryIngredients;
import me.shedaniel.rei.api.common.util.EntryStacks;

import java.util.List;

public class SolidingRecipeDisplay extends BasicDisplay {
    private final int energy;
    private final int inputCount;

    public SolidingRecipeDisplay(SolidingRecipe recipe) {
        super(
                List.of(EntryIngredients.ofIngredient(recipe.getInputItems().get(0))),
                List.of(
                        EntryIngredient.of(EntryStacks.of(recipe.getOutputs().get(0))),
                        EntryIngredient.of(EntryStacks.of(recipe.getOutputs().get(1)))
                )
        );

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
        return SolidingCategory.SOLIDING;
    }
}
