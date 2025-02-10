package com.chesy.productiveslimes.compat.jei;

import com.chesy.productiveslimes.ProductiveSlimes;
import com.chesy.productiveslimes.block.ModBlocks;
import com.chesy.productiveslimes.recipe.MeltingRecipe;
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

public class MeltingCategory implements IRecipeCategory<MeltingRecipe> {
    public static final Identifier UID = Identifier.of(ProductiveSlimes.MODID,"melting");
    public static final Identifier TEXTURE = Identifier.of(ProductiveSlimes.MODID,"textures/gui/melting_station_gui.png");
    public static final RecipeType<MeltingRecipe> MELTING_TYPE = new RecipeType<>(UID, MeltingRecipe.class);
    private int tickCount = 0;

    private final IDrawable background;
    private final IDrawable icon;

    public MeltingCategory(IGuiHelper helper) {
        this.background = helper.createDrawable(TEXTURE,5,5,168,77);
        this.icon = helper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(ModBlocks.MELTING_STATION));
    }

    @Override
    public RecipeType<MeltingRecipe> getRecipeType() {
        return MELTING_TYPE;
    }

    @Override
    public Text getTitle() {
        return Text.translatable("block.productiveslimes.melting_station");
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
    public void draw(MeltingRecipe recipe, IRecipeSlotsView recipeSlotsView, DrawContext guiGraphics, double mouseX, double mouseY) {
        MinecraftClient.getInstance().getTextureManager().bindTexture(TEXTURE);

        tickCount++;
        int arrowWidth = (tickCount % 600) * 26 / 600;

        guiGraphics.drawTexture(TEXTURE, 72, 33, 176, 0, arrowWidth, 8);

        int energyScaled = (int) Math.ceil((double) recipe.getEnergy() / 10000 * 57);
        energyScaled = arrowWidth >= 25 ? 0 : energyScaled;

        guiGraphics.drawTexture(TEXTURE, 4, 13 + (52 - energyScaled), 176, 65 - energyScaled, 9, energyScaled);

        Text text = Text.translatable("tooltip.productiveslimes.energy_usage", recipe.getEnergy());

        if (mouseX >= 4 && mouseX <= 13 && mouseY >= 8 && mouseY <= 65) {
            guiGraphics.drawTooltip(MinecraftClient.getInstance().textRenderer, text, (int) mouseX, (int) mouseY);
        }
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder iRecipeLayoutBuilder, MeltingRecipe meltingRecipe, IFocusGroup iFocusGroup) {
        iRecipeLayoutBuilder.addSlot(RecipeIngredientRole.INPUT,20,29).addItemStack(new ItemStack(Items.BUCKET, meltingRecipe.getOutputs().get(0).getCount()));
        iRecipeLayoutBuilder.addSlot(RecipeIngredientRole.INPUT,40,29).addItemStack(new ItemStack(meltingRecipe.getIngredients().get(0).getMatchingStacks()[0].getItem(), meltingRecipe.getInputCount()));
        iRecipeLayoutBuilder.addSlot(RecipeIngredientRole.OUTPUT, 129, 29).addItemStack(meltingRecipe.getOutputs().get(0));
    }
}
