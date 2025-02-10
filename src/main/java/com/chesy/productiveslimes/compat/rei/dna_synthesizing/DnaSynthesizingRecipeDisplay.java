package com.chesy.productiveslimes.compat.rei.dna_synthesizing;

import com.chesy.productiveslimes.recipe.DnaSynthesizingRecipe;
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

public class DnaSynthesizingRecipeDisplay extends BasicDisplay {
    private final int energy;
    private final int inputCount;

    public DnaSynthesizingRecipeDisplay(RecipeEntry<DnaSynthesizingRecipe> recipe) {
        super(
                List.of(
                        EntryIngredients.ofIngredient(recipe.value().inputItems().get(0)),
                        EntryIngredients.ofIngredient(recipe.value().inputItems().get(1)),
                        EntryIngredients.of(new ItemStack(recipe.value().inputItems().get(2).getMatchingStacks()[0].getItem(), recipe.value().inputCount()))
                ),
                List.of(EntryIngredient.of(EntryStacks.of(recipe.value().output().getFirst())))
        );

        energy = recipe.value().energy();
        inputCount = recipe.value().inputCount();
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