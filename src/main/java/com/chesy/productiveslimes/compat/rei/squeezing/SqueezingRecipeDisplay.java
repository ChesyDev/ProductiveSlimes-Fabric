package com.chesy.productiveslimes.compat.rei.squeezing;

import com.chesy.productiveslimes.ProductiveSlimes;
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
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public record SqueezingRecipeDisplay(RecipeEntry<SqueezingRecipe> recipe) implements Display {
    public static final CategoryIdentifier<? extends SqueezingRecipeDisplay> CATEGORY = CategoryIdentifier.of(ProductiveSlimes.MODID, "squeezing");

    public static final DisplaySerializer<SqueezingRecipeDisplay> SERIALIZER = DisplaySerializer.of(
            RecordCodecBuilder.mapCodec(instance -> instance.group(
                    Identifier.CODEC.fieldOf("recipeId").forGetter(display -> display.recipe.id().getValue()),
                    SqueezingRecipe.Serializer.CODEC.fieldOf("ingredients").forGetter(display -> display.recipe.value())
            ).apply(instance, (identifier, recipe) -> new SqueezingRecipeDisplay(new RecipeEntry<>(RegistryKey.of(RegistryKeys.RECIPE, identifier), recipe)))),
            PacketCodec.tuple(
                    Identifier.PACKET_CODEC,
                    squeezingRecipeDisplay -> squeezingRecipeDisplay.recipe.id().getValue(),
                    SqueezingRecipe.Serializer.PACKET_CODEC,
                    squeezingRecipeDisplay -> squeezingRecipeDisplay.recipe.value(),
                    (identifier, recipe) -> new SqueezingRecipeDisplay(new RecipeEntry<>(RegistryKey.of(RegistryKeys.RECIPE, identifier), recipe))
            )
    );

    @Override
    public List<EntryIngredient> getInputEntries() {
        return List.of(EntryIngredients.ofIngredient(recipe.value().inputItems()));
    }

    @Override
    public List<EntryIngredient> getOutputEntries() {
        List<ItemStack> outputs = recipe.value().output();
        List<EntryIngredient> entryIngredients = new ArrayList<>();
        for (ItemStack output : outputs) {
            entryIngredients.add(EntryIngredients.of(output));
        }
        return entryIngredients;
    }

    @Override
    public CategoryIdentifier<?> getCategoryIdentifier() {
        return CATEGORY;
    }

    @Override
    public Optional<Identifier> getDisplayLocation() {
        return Optional.of(recipe.id().getValue());
    }

    @Override
    public @Nullable DisplaySerializer<? extends Display> getSerializer() {
        return SERIALIZER;
    }
}
