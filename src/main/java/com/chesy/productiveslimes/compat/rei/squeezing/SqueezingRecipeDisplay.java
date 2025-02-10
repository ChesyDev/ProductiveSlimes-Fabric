package com.chesy.productiveslimes.compat.rei.squeezing;

import com.chesy.productiveslimes.recipe.SqueezingRecipe;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.display.Display;
import me.shedaniel.rei.api.common.display.DisplaySerializer;
import me.shedaniel.rei.api.common.display.basic.BasicDisplay;
import me.shedaniel.rei.api.common.entry.EntryIngredient;
import me.shedaniel.rei.api.common.entry.EntryStack;
import me.shedaniel.rei.api.common.util.EntryIngredients;
import me.shedaniel.rei.api.common.util.EntryStacks;
import net.minecraft.item.ItemStack;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.recipe.RecipeEntry;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class SqueezingRecipeDisplay extends BasicDisplay {
    private final int energy;
    private final EntryStack<ItemStack> inputItem;

    public SqueezingRecipeDisplay(RecipeEntry<SqueezingRecipe> recipe) {
        super(
                List.of(EntryIngredients.ofIngredient(recipe.value().inputItems().getFirst())),
                List.of(
                        EntryIngredient.of(EntryStacks.of(recipe.value().output().get(0))),
                        EntryIngredient.of(EntryStacks.of(recipe.value().output().get(1)))
                )
        );
        energy = recipe.value().energy();
        inputItem = EntryStacks.of(new ItemStack(recipe.value().inputItems().getFirst().getMatchingStacks()[0].getItem()));
    }

    public SqueezingRecipeDisplay(List<EntryIngredient> input, List<EntryIngredient> output, int energy) {
        super(input, output);
        this.energy = energy;
        this.inputItem = (EntryStack<ItemStack>) input.get(0).getFirst();
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