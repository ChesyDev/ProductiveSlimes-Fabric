/*
package com.chesy.productiveslimes.compat.rei.dna_synthesizing;

import com.chesy.productiveslimes.ProductiveSlimes;
import com.chesy.productiveslimes.recipe.DnaSynthesizingRecipe;
import com.chesy.productiveslimes.recipe.ingredient.SizedIngredient;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.display.Display;
import me.shedaniel.rei.api.common.display.DisplaySerializer;
import me.shedaniel.rei.api.common.entry.EntryIngredient;
import me.shedaniel.rei.api.common.util.EntryIngredients;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.recipe.RecipeEntry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public record DnaSynthesizingRecipeDisplay(RecipeEntry<DnaSynthesizingRecipe> recipe) implements Display {
    public static final CategoryIdentifier<? extends DnaSynthesizingRecipeDisplay> CATEGORY = CategoryIdentifier.of(ProductiveSlimes.MODID, "dna_synthesizing");

    public static final DisplaySerializer<DnaSynthesizingRecipeDisplay> SERIALIZER = DisplaySerializer.of(
            RecordCodecBuilder.mapCodec(instance -> instance.group(
                    Identifier.CODEC.fieldOf("recipeId").forGetter(display -> display.recipe.id().getValue()),
                    DnaSynthesizingRecipe.Serializer.CODEC.fieldOf("ingredient").forGetter(display -> display.recipe.value())
            ).apply(instance, (identifier, dnaSynthesizingRecipe) -> new DnaSynthesizingRecipeDisplay(new RecipeEntry<>(RegistryKey.of(RegistryKeys.RECIPE, identifier), dnaSynthesizingRecipe)))),
            PacketCodec.tuple(
                    Identifier.PACKET_CODEC,
                    dnaSynthesizingRecipeDisplay -> dnaSynthesizingRecipeDisplay.recipe.id().getValue(),
                    DnaSynthesizingRecipe.Serializer.PACKET_CODEC,
                    dnaSynthesizingRecipeDisplay -> dnaSynthesizingRecipeDisplay.recipe.value(),
                    (identifier, dnaSynthesizingRecipe) -> new DnaSynthesizingRecipeDisplay(new RecipeEntry<>(RegistryKey.of(RegistryKeys.RECIPE, identifier), dnaSynthesizingRecipe))
            )
    );

    @Override
    public List<EntryIngredient> getInputEntries() {
        return EntryIngredients.ofIngredients(recipe.value().inputItems().stream().map(SizedIngredient::ingredient).collect(Collectors.toList()));
    }

    @Override
    public List<EntryIngredient> getOutputEntries() {
        return List.of(EntryIngredients.ofItemStacks(recipe.value().output()));
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
*/
