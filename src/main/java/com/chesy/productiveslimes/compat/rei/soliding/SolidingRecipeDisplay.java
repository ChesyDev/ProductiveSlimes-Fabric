package com.chesy.productiveslimes.compat.rei.soliding;

import com.chesy.productiveslimes.recipe.SolidingRecipe;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.display.Display;
import me.shedaniel.rei.api.common.display.DisplaySerializer;
import me.shedaniel.rei.api.common.display.basic.BasicDisplay;
import me.shedaniel.rei.api.common.entry.EntryIngredient;
import me.shedaniel.rei.api.common.util.EntryIngredients;
import me.shedaniel.rei.api.common.util.EntryStacks;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.recipe.RecipeEntry;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class SolidingRecipeDisplay extends BasicDisplay {
    private final int energy;
    private final int inputCount;

    public SolidingRecipeDisplay(RecipeEntry<SolidingRecipe> recipe) {
        super(
                List.of(EntryIngredients.ofIngredient(recipe.value().getInputItems().getFirst())),
                List.of(
                        EntryIngredient.of(EntryStacks.of(recipe.value().getOutputs().get(0))),
                        EntryIngredient.of(EntryStacks.of(recipe.value().getOutputs().get(1)))
                )
        );

        energy = recipe.value().getEnergy();
        inputCount = recipe.value().getInputCount();
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
