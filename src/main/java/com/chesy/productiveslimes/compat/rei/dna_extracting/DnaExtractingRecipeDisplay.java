package com.chesy.productiveslimes.compat.rei.dna_extracting;

import com.chesy.productiveslimes.recipe.DnaExtractingRecipe;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.display.Display;
import me.shedaniel.rei.api.common.display.DisplaySerializer;
import me.shedaniel.rei.api.common.display.basic.BasicDisplay;
import me.shedaniel.rei.api.common.entry.EntryIngredient;
import me.shedaniel.rei.api.common.util.EntryIngredients;
import me.shedaniel.rei.api.common.util.EntryStacks;
import net.minecraft.item.ItemStack;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.recipe.RecipeEntry;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class DnaExtractingRecipeDisplay extends BasicDisplay {
    private final int energy;
    private final float outputChance;
    private final int inputCount;

    public DnaExtractingRecipeDisplay(RecipeEntry<DnaExtractingRecipe> recipe) {
        super(List.of(EntryIngredients.ofIngredient(recipe.value().inputItems().getFirst())),
                List.of(EntryIngredient.of(EntryStacks.of(recipe.value().output().get(0))),
                        EntryIngredient.of(EntryStacks.of(recipe.value().output().size() > 1 ? recipe.value().output().get(1) : ItemStack.EMPTY))));
        energy = recipe.value().energy();
        outputChance = recipe.value().outputChance();
        inputCount = recipe.value().inputCount();
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