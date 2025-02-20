package com.chesy.productiveslimes.compat.rei.dna_synthesizing;

import com.chesy.productiveslimes.recipe.DnaSynthesizingRecipe;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.display.basic.BasicDisplay;
import me.shedaniel.rei.api.common.entry.EntryIngredient;
import me.shedaniel.rei.api.common.util.EntryIngredients;
import me.shedaniel.rei.api.common.util.EntryStacks;
import net.minecraft.item.ItemStack;

import java.util.List;

public class DnaSynthesizingRecipeDisplay extends BasicDisplay {
    private final int energy;
    private final int inputCount;

    public DnaSynthesizingRecipeDisplay(DnaSynthesizingRecipe recipe) {
        super(
                List.of(
                        EntryIngredients.ofIngredient(recipe.inputItems().get(0)),
                        EntryIngredients.ofIngredient(recipe.inputItems().get(1)),
                        EntryIngredients.of(new ItemStack(recipe.inputItems().get(2).getMatchingStacks()[0].getItem(), recipe.inputCount()))
                ),
                List.of(EntryIngredient.of(EntryStacks.of(recipe.output().getFirst())))
        );

        energy = recipe.energy();
        inputCount = recipe.inputCount();
    }

    public int getEnergy() {
        return energy;
    }

    public int getInputCount() {
        return inputCount;
    }

    @Override
    public CategoryIdentifier<?> getCategoryIdentifier() {
        return DnaSynthesizingCategory.DNA_SYNTHESIZING;
    }
}