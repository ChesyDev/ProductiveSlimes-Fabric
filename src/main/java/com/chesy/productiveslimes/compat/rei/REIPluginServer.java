/*
package com.chesy.productiveslimes.compat.rei;

import com.chesy.productiveslimes.ProductiveSlimes;
import com.chesy.productiveslimes.compat.rei.melting.MeltingRecipeDisplay;
import com.chesy.productiveslimes.compat.rei.soliding.SolidingRecipeDisplay;
import com.chesy.productiveslimes.recipe.MeltingRecipe;
import com.chesy.productiveslimes.recipe.ModRecipes;
import com.chesy.productiveslimes.recipe.SolidingRecipe;
import me.shedaniel.rei.api.common.display.DisplaySerializerRegistry;
import me.shedaniel.rei.api.common.plugins.REICommonPlugin;
import me.shedaniel.rei.api.common.registry.display.ServerDisplayRegistry;
import net.minecraft.util.Identifier;

public class REIPluginServer implements REICommonPlugin {
    @Override
    public void registerDisplaySerializer(DisplaySerializerRegistry registry) {
        registry.register(Identifier.of(ProductiveSlimes.MODID, "melting"), MeltingRecipeDisplay.SERIALIZER);
        registry.register(Identifier.of(ProductiveSlimes.MODID, "soliding"), SolidingRecipeDisplay.SERIALIZER);
    }

    @Override
    public void registerDisplays(ServerDisplayRegistry registry) {
        registry.beginRecipeFiller(MeltingRecipe.class).filterType(ModRecipes.MELTING_TYPE).fill(MeltingRecipeDisplay::new);
        registry.beginRecipeFiller(SolidingRecipe.class).filterType(ModRecipes.SOLIDING_TYPE).fill(SolidingRecipeDisplay::new);
    }
}
*/
