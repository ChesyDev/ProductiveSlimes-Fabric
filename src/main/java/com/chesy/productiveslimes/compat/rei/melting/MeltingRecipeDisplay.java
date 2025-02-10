package com.chesy.productiveslimes.compat.rei.melting;

import com.chesy.productiveslimes.recipe.MeltingRecipe;
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
import net.minecraft.item.Items;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.recipe.RecipeEntry;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class MeltingRecipeDisplay extends BasicDisplay {
    private final int energy;
    private final int inputCount;

    public MeltingRecipeDisplay(RecipeEntry<MeltingRecipe> recipe) {
        super(List.of(EntryIngredients.of(new ItemStack(recipe.value().getInputItems().getFirst().getMatchingStacks()[0].getItem(), recipe.value().getInputCount())),
                        EntryIngredients.of(new ItemStack(Items.BUCKET, recipe.value().getOutputs().getFirst().getCount()))),
                List.of(EntryIngredient.of(EntryStacks.of(recipe.value().getOutputs().getFirst()))));

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
        return MeltingCategory.MELTING;
    }
}
