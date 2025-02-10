package com.chesy.productiveslimes.compat.jei;

import com.chesy.productiveslimes.ProductiveSlimes;
import com.chesy.productiveslimes.block.ModBlocks;
import com.chesy.productiveslimes.recipe.DnaSynthesizingRecipe;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.List;

public class DnaSynthesizingCategory implements IRecipeCategory<DnaSynthesizingRecipe> {
    public static final Identifier UID = Identifier.of(ProductiveSlimes.MODID, "dna_synthesizing");
    public static final Identifier TEXTURE = Identifier.of(ProductiveSlimes.MODID, "textures/gui/dna_synthesizer_gui.png");
    public static final RecipeType<DnaSynthesizingRecipe> DNA_SYNTHESIZING_TYPE = new RecipeType<>(UID, DnaSynthesizingRecipe.class);
    private int tickCount = 0;

    private final IDrawable background;
    private final IDrawable icon;

    public DnaSynthesizingCategory(IGuiHelper helper) {
        this.background = helper.createDrawable(TEXTURE, 5, 5, 168, 77);
        this.icon = helper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(ModBlocks.DNA_SYNTHESIZER));
    }

    @Override
    public RecipeType<DnaSynthesizingRecipe> getRecipeType() {
        return DNA_SYNTHESIZING_TYPE;
    }

    @Override
    public Text getTitle() {
        return Text.translatable("block.productiveslimes.dna_synthesizer");
    }

    @SuppressWarnings("removal")
    @Override
    public IDrawable getBackground() {
        return this.background;
    }

    @Override
    public IDrawable getIcon() {
        return this.icon;
    }

    @Override
    public void draw(DnaSynthesizingRecipe recipe, IRecipeSlotsView recipeSlotsView, DrawContext guiGraphics, double mouseX, double mouseY) {
        MinecraftClient.getInstance().getTextureManager().bindTexture(TEXTURE);

        tickCount++;
        int arrowWidth = (tickCount % 600) * 26 / 600;
        int dnaHeight = (tickCount % 600) * 23 / 600;

        guiGraphics.drawTexture(TEXTURE, 72, 33, 176, 0, arrowWidth, 8);

        int energyScaled = (int) Math.ceil((double) recipe.energy() / 10000 * 57);
        energyScaled = arrowWidth >= 25 ? 0 : energyScaled;

        guiGraphics.drawTexture(TEXTURE, 4, 13 + (52 - energyScaled), 176, 65 - energyScaled, 9, energyScaled);
        guiGraphics.drawTexture(TEXTURE, 31, 25, 176, 66, 6, dnaHeight);

        Text text = Text.translatable("tooltip.productiveslimes.energy_usage", recipe.energy());

        if (mouseX >= 4 && mouseX <= 13 && mouseY >= 8 && mouseY <= 65) {
            guiGraphics.drawTooltip(MinecraftClient.getInstance().textRenderer, text, (int) mouseX, (int) mouseY);
        }
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder iRecipeLayoutBuilder, DnaSynthesizingRecipe dnaSynthesizingRecipe, IFocusGroup iFocusGroup) {
        iRecipeLayoutBuilder.addSlot(RecipeIngredientRole.INPUT, 26, 7).addIngredients(dnaSynthesizingRecipe.inputItems().get(0));
        iRecipeLayoutBuilder.addSlot(RecipeIngredientRole.INPUT, 26, 50).addIngredients(dnaSynthesizingRecipe.inputItems().get(1));
        iRecipeLayoutBuilder.addSlot(RecipeIngredientRole.INPUT, 47, 29).addItemStack(new ItemStack(dnaSynthesizingRecipe.inputItems().get(2).getMatchingStacks()[0].getItem(), dnaSynthesizingRecipe.inputCount()));
        iRecipeLayoutBuilder.addSlot(RecipeIngredientRole.INPUT, 77, 49).addItemStack(new ItemStack(Items.EGG, 1));
        iRecipeLayoutBuilder.addSlot(RecipeIngredientRole.OUTPUT, 120, 29).addItemStack(dnaSynthesizingRecipe.output().get(0));
    }
}
