package com.chesy.productiveslimes.recipe;

import com.chesy.productiveslimes.ProductiveSlimes;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;


public class ModRecipes {
    public static void register() {
        Registry.register(Registries.RECIPE_SERIALIZER, new Identifier(ProductiveSlimes.MODID, MeltingRecipe.Serializer.ID), MeltingRecipe.Serializer.INSTANCE);
        Registry.register(Registries.RECIPE_TYPE, new Identifier(ProductiveSlimes.MODID, MeltingRecipe.Type.ID), MeltingRecipe.Type.INSTANCE);

        Registry.register(Registries.RECIPE_SERIALIZER, new Identifier(ProductiveSlimes.MODID, SolidingRecipe.Serializer.ID), SolidingRecipe.Serializer.INSTANCE);
        Registry.register(Registries.RECIPE_TYPE, new Identifier(ProductiveSlimes.MODID, SolidingRecipe.Type.ID), SolidingRecipe.Type.INSTANCE);

        Registry.register(Registries.RECIPE_SERIALIZER, new Identifier(ProductiveSlimes.MODID, DnaExtractingRecipe.Serializer.ID), DnaExtractingRecipe.Serializer.INSTANCE);
        Registry.register(Registries.RECIPE_TYPE, new Identifier(ProductiveSlimes.MODID, DnaExtractingRecipe.Type.ID), DnaExtractingRecipe.Type.INSTANCE);

        Registry.register(Registries.RECIPE_SERIALIZER, new Identifier(ProductiveSlimes.MODID, DnaSynthesizingRecipe.Serializer.ID), DnaSynthesizingRecipe.Serializer.INSTANCE);
        Registry.register(Registries.RECIPE_TYPE, new Identifier(ProductiveSlimes.MODID, DnaSynthesizingRecipe.Type.ID), DnaSynthesizingRecipe.Type.INSTANCE);

        Registry.register(Registries.RECIPE_SERIALIZER, new Identifier(ProductiveSlimes.MODID, SqueezingRecipe.Serializer.ID), SqueezingRecipe.Serializer.INSTANCE);
        Registry.register(Registries.RECIPE_TYPE, new Identifier(ProductiveSlimes.MODID, SqueezingRecipe.Type.ID), SqueezingRecipe.Type.INSTANCE);
    }
}
