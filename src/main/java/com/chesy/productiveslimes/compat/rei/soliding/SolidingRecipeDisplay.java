/*
package com.chesy.productiveslimes.compat.rei.soliding;

import com.chesy.productiveslimes.ProductiveSlimes;
import com.chesy.productiveslimes.recipe.SolidingRecipe;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.display.Display;
import me.shedaniel.rei.api.common.display.DisplaySerializer;
import me.shedaniel.rei.api.common.entry.EntryIngredient;
import me.shedaniel.rei.api.common.util.EntryIngredients;
import net.minecraft.item.ItemStack;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.recipe.RecipeEntry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public record SolidingRecipeDisplay(RecipeEntry<SolidingRecipe> recipe) implements Display {
    public static final CategoryIdentifier<? extends SolidingRecipeDisplay> CATEGORY = CategoryIdentifier.of(ProductiveSlimes.MODID, "soliding");

    public static final DisplaySerializer<SolidingRecipeDisplay> SERIALIZER = DisplaySerializer.of(
            RecordCodecBuilder.mapCodec(instance -> instance.group(
                    Identifier.CODEC.fieldOf("recipeId").forGetter(display -> display.recipe.id().getValue()),
                    SolidingRecipe.Serializer.CODEC.fieldOf("ingredients").forGetter(display -> display.recipe.value())
            ).apply(instance, (id, recipe) -> new SolidingRecipeDisplay(new RecipeEntry<>(RegistryKey.of(RegistryKeys.RECIPE, id), recipe)))),
            PacketCodec.tuple(
                    Identifier.PACKET_CODEC,
                    solidingRecipeDisplay -> solidingRecipeDisplay.recipe.id().getValue(),
                    SolidingRecipe.Serializer.STREAM_CODEC,
                    solidingRecipeDisplay -> solidingRecipeDisplay.recipe.value(),
                    (identifier, solidingRecipe) -> new SolidingRecipeDisplay(new RecipeEntry<>(RegistryKey.of(RegistryKeys.RECIPE, identifier), solidingRecipe))
            )
    );

    @Override
    public List<EntryIngredient> getInputEntries() {
        return List.of(EntryIngredients.of(recipe.value().fluidStack().getFluid().getFluid(), recipe.value().fluidStack().getAmount()));
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
*/
