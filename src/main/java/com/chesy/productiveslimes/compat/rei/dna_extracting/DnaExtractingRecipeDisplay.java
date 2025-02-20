package com.chesy.productiveslimes.compat.rei.dna_extracting;

import com.chesy.productiveslimes.recipe.DnaExtractingRecipe;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.display.basic.BasicDisplay;
import me.shedaniel.rei.api.common.entry.EntryIngredient;
import me.shedaniel.rei.api.common.util.EntryIngredients;
import me.shedaniel.rei.api.common.util.EntryStacks;
import net.minecraft.item.ItemStack;

import java.util.List;

public class DnaExtractingRecipeDisplay extends BasicDisplay {
    private final int energy;
    private final float outputChance;
    private final int inputCount;

    public DnaExtractingRecipeDisplay(DnaExtractingRecipe recipe) {
        super(List.of(EntryIngredients.ofIngredient(recipe.inputItems().getFirst())),
                List.of(EntryIngredient.of(EntryStacks.of(recipe.output().get(0))),
                        EntryIngredient.of(EntryStacks.of(recipe.output().size() > 1 ? recipe.output().get(1) : ItemStack.EMPTY))));
        energy = recipe.energy();
        outputChance = recipe.outputChance();
        inputCount = recipe.inputCount();
    }

    public int getEnergy() {
        return energy;
    }

    public float getOutputChance() {
        return outputChance;
    }

    public int getInputCount() {
        return inputCount;
    }

    @Override
    public CategoryIdentifier<?> getCategoryIdentifier() {
        return DnaExtractingCategory.DNA_EXTRACTING;
    }
}