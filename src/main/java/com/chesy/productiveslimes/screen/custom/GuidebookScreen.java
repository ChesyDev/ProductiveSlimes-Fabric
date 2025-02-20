package com.chesy.productiveslimes.screen.custom;

import com.chesy.productiveslimes.ProductiveSlimes;
import com.chesy.productiveslimes.block.ModBlocks;
import com.chesy.productiveslimes.item.ModItems;
import com.chesy.productiveslimes.recipe.*;
import com.chesy.productiveslimes.tier.ModTier;
import com.chesy.productiveslimes.tier.ModTiers;
import com.chesy.productiveslimes.util.GuideBookScreenHelper;
import com.chesy.productiveslimes.util.SlimeData;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.ShapedRecipe;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DefaultedList;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;

public class GuidebookScreen extends HandledScreen<GuidebookMenu> {
    private static final int NAVIGATION_WIDTH = 100;
    private static final int SCROLLBAR_WIDTH = 6;
    private static final int NAV_TEXT_HEIGHT = 20;
    private static final int INFO_SECTION_HEIGHT = 150;
    private static int SLIME_AND_SLIMEBALL_INFO_HEIGHT = 150;
    private static int SLIME_AND_SLIMEBALL_SECOND_INFO_HEIGHT = 150;
    private static int SLIME_AND_SLIMEBALL_THIRD_INFO_HEIGHT = 150;
    private static final int WELCOME_PAGE_HEIGHT = 150;
    private static int ENERGY_GENERATION_INFO_HEIGHT = 150;
    private static int WORLD_GEN_INFO_HEIGHT = 150;
    private static final Identifier CRAFTING_TEXTURE = Identifier.of(ProductiveSlimes.MODID, "textures/gui/guidebook/crafting_table_gui.png");
    private final List<Text> sections = List.of(
            Text.translatable("guidebook.productiveslimes.nav.welcome"),
            Text.translatable("guidebook.productiveslimes.slime_and_slimeball"),
            Text.translatable("guidebook.productiveslimes.energy_generation"),
            Text.translatable("guidebook.productiveslimes.nav.world_gen"),
            Text.translatable("guidebook.productiveslimes.dna_extracting"),
            Text.translatable("guidebook.productiveslimes.dna_synthesizing"),
            Text.translatable("guidebook.productiveslimes.nav.melting"),
            Text.translatable("guidebook.productiveslimes.soliding"),
            Text.translatable("guidebook.productiveslimes.squeezing")
    );
    private int selectedSection = 0;

    public static final int RECIPE_WIDTH = 153;
    public static final int RECIPE_HEIGHT = 83;

    public static int COLUMNS = 2;

    public static final int H_SPACING = 5;
    public static final int V_SPACING = 5;

    private int contentScrollOffset = 0;

    private final List<DnaExtractingRecipe> dnaExtractingRecipeList = handler.world.getRecipeManager().listAllOfType(ModRecipes.DNA_EXTRACTING_TYPE);
    private final List<DnaSynthesizingRecipe> dnaSynthesizingRecipeList = handler.world.getRecipeManager().listAllOfType(ModRecipes.DNA_SYNTHESIZING_TYPE);
    private final List<MeltingRecipe> meltingRecipeList = handler.world.getRecipeManager().listAllOfType(ModRecipes.MELTING_TYPE);
    private final List<SolidingRecipe> solidingRecipeList = handler.world.getRecipeManager().listAllOfType(ModRecipes.SOLIDING_TYPE);
    private final List<SqueezingRecipe> squeezingRecipeList = handler.world.getRecipeManager().listAllOfType(ModRecipes.SQUEEZING_TYPE);

    public GuidebookScreen(GuidebookMenu menu, PlayerInventory playerInventory, Text title) {
        super(menu, playerInventory, title);
    }

    @Override
    protected void init() {
        super.init();
        this.playerInventoryTitleX = 1000000;
        this.playerInventoryTitleY = 1000000;
        this.titleX = 10000000;

        this.backgroundWidth = this.width;
        this.backgroundHeight = this.height;
    }

    @Override
    protected void drawBackground(DrawContext context, float delta, int mouseX, int mouseY) {

    }

    @Override
    public void render(@NotNull DrawContext pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
        renderBackground(pGuiGraphics);
        super.render(pGuiGraphics, pMouseX, pMouseY, pPartialTick);

        renderNavigationPanel(pGuiGraphics);
        renderContentPanel(pGuiGraphics, pMouseX, pMouseY);
    }

    private void renderNavigationPanel(DrawContext pGuiGraphics) {
        int navigationX = 0;
        int navigationY = 0;
        int navigationHeight = this.height;
        COLUMNS = (this.width - NAVIGATION_WIDTH) / (RECIPE_WIDTH + H_SPACING);

        pGuiGraphics.fill(navigationX, navigationY, navigationX + NAVIGATION_WIDTH + 20, navigationY + navigationHeight, 0x55555555);

        int sectionY = navigationY + 10;
        for (int i = 0; i < sections.size(); i++) {
            if (sectionY + NAV_TEXT_HEIGHT > navigationY && sectionY < navigationY + navigationHeight) {
                boolean isSelected = i == selectedSection;
                int color = isSelected ? 0xFFFFFF00 : 0xFFFFFFFF;
                pGuiGraphics.drawTextWithShadow(this.textRenderer, sections.get(i), navigationX + 10, sectionY, color);
            }
            sectionY += NAV_TEXT_HEIGHT;
        }
    }

    private void renderContentPanel(DrawContext pGuiGraphics, int pMouseX, int pMouseY) {
        switch (selectedSection) {
            case 0:
                drawWelcomePage(pGuiGraphics, pMouseX, pMouseY);
                break;
            case 1:
                drawSlimeAndSlimeball(pGuiGraphics, pMouseX, pMouseY);
                break;
            case 2:
                drawEnergyGeneration(pGuiGraphics, pMouseX, pMouseY);
                break;
            case 3:
                drawWorldGen(pGuiGraphics, pMouseX, pMouseY);
                break;
            case 4:
                drawDnaExtracting(pGuiGraphics, pMouseX, pMouseY);
                break;
            case 5:
                drawDnaSynthesizing(pGuiGraphics, pMouseX, pMouseY);
                break;
            case 6:
                drawMelting(pGuiGraphics, pMouseX, pMouseY);
                break;
            case 7:
                drawSoliding(pGuiGraphics, pMouseX, pMouseY);
                break;
            case 8:
                drawSqueezing(pGuiGraphics, pMouseX, pMouseY);
                break;
        }
    }
    

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double amount) {
        int scrollSpeed = 10;
        int navX = 10;
        int navY = 10;
        int navHeight = this.height - 20;
        int contentX = navX + NAVIGATION_WIDTH + 10;
        int contentWidth = this.width - contentX - 10;

        boolean overContent = (mouseX >= contentX && mouseX < contentX + contentWidth && mouseY >= navY && mouseY < navY + navHeight);

        if (overContent) {
            if (selectedSection == 4) {
                int totalRecipeHeight = (int) ((Math.ceil((double) dnaExtractingRecipeList.size() / COLUMNS)) * RECIPE_HEIGHT) + RECIPE_HEIGHT + RECIPE_HEIGHT / 3 + INFO_SECTION_HEIGHT;
                int maxContentScroll = Math.max(0, totalRecipeHeight - navHeight);

                contentScrollOffset = GuideBookScreenHelper.scrollOffset(contentScrollOffset, amount, maxContentScroll, scrollSpeed);

                return true;
            } else if (selectedSection == 5) {
                int totalRecipeHeight = (int) ((Math.ceil((double) dnaSynthesizingRecipeList.size() / COLUMNS)) * RECIPE_HEIGHT) + RECIPE_HEIGHT + RECIPE_HEIGHT / 3 + INFO_SECTION_HEIGHT + 100;
                int maxContentScroll = Math.max(0, totalRecipeHeight - navHeight);

                contentScrollOffset = GuideBookScreenHelper.scrollOffset(contentScrollOffset, amount, maxContentScroll, scrollSpeed);

                return true;
            } else if (selectedSection == 6) {
                int totalRecipeHeight = (int) ((Math.ceil((double) meltingRecipeList.size() / COLUMNS)) * RECIPE_HEIGHT) + RECIPE_HEIGHT + RECIPE_HEIGHT / 3 + INFO_SECTION_HEIGHT + 100;
                int maxContentScroll = Math.max(0, totalRecipeHeight - navHeight);

                contentScrollOffset = GuideBookScreenHelper.scrollOffset(contentScrollOffset, amount, maxContentScroll, scrollSpeed);

                return true;
            } else if (selectedSection == 7) {
                int totalRecipeHeight = (int) ((Math.ceil((double) solidingRecipeList.size() / COLUMNS)) * RECIPE_HEIGHT) + INFO_SECTION_HEIGHT + 100;
                int maxContentScroll = Math.max(0, totalRecipeHeight - navHeight);

                contentScrollOffset = GuideBookScreenHelper.scrollOffset(contentScrollOffset, amount, maxContentScroll, scrollSpeed);

                return true;
            } else if (selectedSection == 8) {
                int totalRecipeHeight = (int) ((Math.ceil((double) squeezingRecipeList.size() / COLUMNS)) * RECIPE_HEIGHT) + INFO_SECTION_HEIGHT + 20;
                int maxContentScroll = Math.max(0, totalRecipeHeight - navHeight);

                contentScrollOffset = GuideBookScreenHelper.scrollOffset(contentScrollOffset, amount, maxContentScroll, scrollSpeed);

                return true;
            } else if (selectedSection == 1) {
                int totalRecipeHeight = (int) ((Math.ceil((double) ModTiers.getRegisteredTiers().size() / COLUMNS)) * RECIPE_HEIGHT) + INFO_SECTION_HEIGHT;
                int totalCooldownHeight = (int) ((Math.ceil((double) ModTiers.getRegisteredTiers().size() / COLUMNS)) * 46) + INFO_SECTION_HEIGHT;
                int maxContentScroll = Math.max(0, totalRecipeHeight - navHeight + SLIME_AND_SLIMEBALL_INFO_HEIGHT + SLIME_AND_SLIMEBALL_SECOND_INFO_HEIGHT + totalCooldownHeight + SLIME_AND_SLIMEBALL_THIRD_INFO_HEIGHT);

                contentScrollOffset = GuideBookScreenHelper.scrollOffset(contentScrollOffset, amount, maxContentScroll, scrollSpeed);

                return true;
            }
            else if (selectedSection == 0){
                int maxContentScroll = Math.max(0, WELCOME_PAGE_HEIGHT - navHeight);

                contentScrollOffset = GuideBookScreenHelper.scrollOffset(contentScrollOffset, amount, maxContentScroll, scrollSpeed);
            }
            else if (selectedSection == 2){
                int maxContentScroll = Math.max(0, ENERGY_GENERATION_INFO_HEIGHT - navHeight);

                contentScrollOffset = GuideBookScreenHelper.scrollOffset(contentScrollOffset, amount, maxContentScroll, scrollSpeed);
            }
            else if (selectedSection == 3){
                int maxContentScroll = Math.max(0, WORLD_GEN_INFO_HEIGHT - navHeight);

                contentScrollOffset = GuideBookScreenHelper.scrollOffset(contentScrollOffset, amount, maxContentScroll, scrollSpeed);
            }
        }

        return super.mouseScrolled(mouseX, mouseY, amount);
    }

    @Override
    public boolean mouseClicked(double pMouseX, double pMouseY, int pButton) {
        if (pMouseX >= 10 && pMouseX < 10 + NAVIGATION_WIDTH && pMouseY >= 10 && pMouseY < this.height - 10) {
            int sectionIndex = (int) ((pMouseY - 10) / NAV_TEXT_HEIGHT);
            if (sectionIndex >= 0 && sectionIndex < sections.size()) {
                if (sectionIndex == selectedSection) {
                    return true;
                }
                selectedSection = sectionIndex;
                contentScrollOffset = 0;
                return true;
            }
        }
        return super.mouseClicked(pMouseX, pMouseY, pButton);
    }

    @Override
    public void resize(@NotNull MinecraftClient minecraft, int width, int height) {
        super.resize(minecraft, width, height);
        contentScrollOffset = 0;
    }

    private void drawDnaExtracting(DrawContext pGuiGraphics, int pMouseX, int pMouseY) {
        int contentX = 10 + NAVIGATION_WIDTH;
        int contentY = 10;
        int contentWidth = this.width - contentX - 10;
        contentX += (contentWidth - (RECIPE_WIDTH * COLUMNS)) / 2;

        Identifier TEXTURE = Identifier.of(ProductiveSlimes.MODID, "textures/gui/rei/dna_extractor_gui.png");

        int infoY = contentY - contentScrollOffset;

        // Render the info section
        Text title = Text.translatable("guidebook.productiveslimes.dna_extracting");
        int fontX = textRenderer.getWidth(title);
        pGuiGraphics.drawTextWithShadow(textRenderer, title, (int) (contentX + (contentWidth - fontX) / 2 * 0.8f), infoY + 5, 0xFFFFFF);

        Text description = Text.translatable("guidebook.productiveslimes.dna_extracting.description");
        pGuiGraphics.drawTextWrapped(textRenderer, description, contentX + 5, infoY + 20, contentWidth - 20, 0xAAAAAA);

        pGuiGraphics.drawTexture(CRAFTING_TEXTURE, (int) (contentX + (contentWidth - RECIPE_WIDTH) / 2 * 0.8f), infoY + 45, 0, 0, RECIPE_WIDTH, RECIPE_HEIGHT, 256, 256);

        Optional<?> extractor = handler.world.getRecipeManager().get(Identifier.of(ProductiveSlimes.MODID, "dna_extractor"));
        if (extractor.isPresent()){
            if (extractor.get() instanceof ShapedRecipe shapedRecipe) {
                DefaultedList<Ingredient> ingredients = shapedRecipe.getIngredients();
                for (int i = 0; i < ingredients.size(); i++) {
                    Ingredient ingredient = ingredients.get(i);
                    ItemStack stacks = ingredient.getMatchingStacks()[0];
                    GuideBookScreenHelper.renderItemSlot(pGuiGraphics, pMouseX, pMouseY, (int) (contentX + (contentWidth - RECIPE_WIDTH) / 2 * 0.8f) + 19 + (i % 3) * 18, infoY + 46 + 16 + (i / 3) * 18, stacks, textRenderer);
                }

                ItemStack output = ModBlocks.DNA_EXTRACTOR.asItem().getDefaultStack();
                GuideBookScreenHelper.renderItemSlot(pGuiGraphics, pMouseX, pMouseY, (int) (contentX + (contentWidth - RECIPE_WIDTH) / 2 * 0.8f) + 95 + 18, infoY + 46 + 16 + 18, output, textRenderer);
            }
        }

        int index = 0;
        int numRecipeRows = (int) Math.ceil((double) dnaExtractingRecipeList.size() / COLUMNS);
        int totalRecipeHeight = numRecipeRows * (RECIPE_HEIGHT + V_SPACING);
        int totalContentHeight = INFO_SECTION_HEIGHT + totalRecipeHeight;

        // Render each recipe
        for (DnaExtractingRecipe recipe : dnaExtractingRecipeList) {
            int row = index / COLUMNS;
            int col = index % COLUMNS;
            int xPos = contentX + col * (RECIPE_WIDTH + H_SPACING);
            int yPos = contentY + INFO_SECTION_HEIGHT + row * (RECIPE_HEIGHT + V_SPACING) - contentScrollOffset;

            // Render recipe background (optional)
            pGuiGraphics.drawTexture(TEXTURE, xPos, yPos, 0, 0, RECIPE_WIDTH, RECIPE_HEIGHT, 256, 256);

            // Render energy bar
            int energyScaled = (int) (((float) recipe.energy() / (float) 10000) * 57);
            pGuiGraphics.drawTexture(TEXTURE, xPos + 9, yPos + 13 + (57 - energyScaled), 153, 8, 9, energyScaled, 256, 256);
            if (pMouseX >= xPos + 9 && pMouseX < xPos + 18 && pMouseY >= yPos + 13 && pMouseY < yPos + 70) {
                Text text = Text.translatable("gui.productiveslimes.energy_stored", recipe.energy(), 10000);
                pGuiGraphics.drawTooltip(textRenderer, text, pMouseX, pMouseY);
            }

            // Render recipe input
            Ingredient input = recipe.inputItems().getFirst();
            ItemStack inputStack = input.getMatchingStacks()[0];
            int inputX = xPos + 27;
            int inputY = yPos + 34;
            GuideBookScreenHelper.renderItemSlot(pGuiGraphics, pMouseX, pMouseY, inputX, inputY, inputStack, textRenderer);

            // Render recipe output
            ItemStack output = recipe.output().getFirst();
            int outputX = xPos + 108;
            int outputY = yPos + 34;
            GuideBookScreenHelper.renderItemSlot(pGuiGraphics, pMouseX, pMouseY, outputX, outputY, output, textRenderer);

            if (!output.isOf(ModItems.SLIME_DNA)) {
                GuideBookScreenHelper.renderItemSlot(pGuiGraphics, pMouseX, pMouseY, outputX + 20, outputY, Items.SLIME_BALL.getDefaultStack(), textRenderer);
            }

            pGuiGraphics.drawText(textRenderer, output.toHoverableText().getString().substring(1, output.toHoverableText().getString().length() - 1), xPos + 9, yPos + 4, 0x555555, false);

            Text outputChance = Text.translatable("gui.productiveslimes.output_chance", String.format("%.1f", recipe.outputChance() * 100) + "%");
            pGuiGraphics.drawText(textRenderer, outputChance, xPos + 9, yPos + 72, 0x555555, false);

            index++;
        }

        int scrollbarX = width - SCROLLBAR_WIDTH;
        int scrollbarHeight = (int) ((float) height / totalContentHeight * height);
        int scrollbarY = (int) ((float) contentScrollOffset / totalContentHeight * height);
        pGuiGraphics.fill(scrollbarX, 0, scrollbarX + SCROLLBAR_WIDTH, height, 0x55555555);
        pGuiGraphics.fill(scrollbarX, scrollbarY, scrollbarX + SCROLLBAR_WIDTH, scrollbarY + scrollbarHeight, 0x55888888);
    }

    private void drawDnaSynthesizing(DrawContext pGuiGraphics, int pMouseX, int pMouseY) {
        int contentX = 10 + NAVIGATION_WIDTH;
        int contentY = 10;
        int contentWidth = this.width - contentX - 10;
        contentX += (contentWidth - (RECIPE_WIDTH * COLUMNS)) / 2;

        Identifier TEXTURE = Identifier.of(ProductiveSlimes.MODID, "textures/gui/rei/dna_synthesizer_gui.png");

        int infoY = contentY - contentScrollOffset;

        // Render the info section
        Text title = Text.translatable("guidebook.productiveslimes.dna_synthesizing");
        int fontX = textRenderer.getWidth(title);
        pGuiGraphics.drawTextWithShadow(textRenderer, title, (int) (contentX + (contentWidth - fontX) / 2 * 0.8f), infoY + 5, 0xFFFFFF);

        Text description = Text.translatable("guidebook.productiveslimes.dna_synthesizing.description");
        pGuiGraphics.drawTextWrapped(textRenderer, description, contentX + 5, infoY + 20, contentWidth - 30, 0xAAAAAA);

        pGuiGraphics.drawTexture(CRAFTING_TEXTURE, (int) (contentX + (contentWidth - RECIPE_WIDTH) / 2 * 0.8f), infoY + 45, 0, 0, RECIPE_WIDTH, RECIPE_HEIGHT, 256, 256);

        Optional<?> extractor = handler.world.getRecipeManager().get(Identifier.of(ProductiveSlimes.MODID, "dna_synthesizer"));
        if (extractor.isPresent()){
            if (extractor.get() instanceof ShapedRecipe shapedRecipe) {
                DefaultedList<Ingredient> ingredients = shapedRecipe.getIngredients();
                for (int i = 0; i < ingredients.size(); i++) {
                    Ingredient ingredient = ingredients.get(i);
                    ItemStack stacks = ingredient.getMatchingStacks()[0];
                    GuideBookScreenHelper.renderItemSlot(pGuiGraphics, pMouseX, pMouseY, (int) (contentX + (contentWidth - RECIPE_WIDTH) / 2 * 0.8f) + 19 + (i % 3) * 18, infoY + 46 + 16 + (i / 3) * 18, stacks, textRenderer);
                }

                GuideBookScreenHelper.renderItemSlot(pGuiGraphics, pMouseX, pMouseY, (int) (contentX + (contentWidth - RECIPE_WIDTH) / 2 * 0.8f) + 95 + 18, infoY + 46 + 16 + 18, ModBlocks.DNA_SYNTHESIZER.asItem().getDefaultStack(), textRenderer);
            }
        }

        int index = 0;
        int numRecipeRows = (int) Math.ceil((double) dnaSynthesizingRecipeList.size() / COLUMNS);
        int totalRecipeHeight = numRecipeRows * (RECIPE_HEIGHT + V_SPACING);
        int totalContentHeight = INFO_SECTION_HEIGHT + totalRecipeHeight;

        // Render each recipe
        for (DnaSynthesizingRecipe recipe : dnaSynthesizingRecipeList) {
            int row = index / COLUMNS;
            int col = index % COLUMNS;
            int xPos = contentX + col * (RECIPE_WIDTH + H_SPACING);
            int yPos = contentY + INFO_SECTION_HEIGHT + row * (RECIPE_HEIGHT + V_SPACING) - contentScrollOffset;

            pGuiGraphics.drawTexture(TEXTURE, xPos, yPos, 0, 0, RECIPE_WIDTH, RECIPE_HEIGHT, 256, 256);

            // Render energy bar
            int energyScaled = (int) (((float) recipe.energy() / (float) 10000) * 57);
            pGuiGraphics.drawTexture(TEXTURE, xPos + 9, yPos + 13 + (57 - energyScaled), 153, 8, 9, energyScaled, 256, 256);
            if (pMouseX >= xPos + 9 && pMouseX < xPos + 18 && pMouseY >= yPos + 13 && pMouseY < yPos + 70) {
                Text text = Text.translatable("gui.productiveslimes.energy_stored", recipe.energy(), 10000);
                pGuiGraphics.drawTooltip(textRenderer, text, pMouseX, pMouseY);
            }

            // Render recipe input
            List<Ingredient> input = recipe.inputItems();
            int ingredientIndex = 0;
            for (Ingredient ingredient : input) {
                ItemStack inputStack = ingredient.getMatchingStacks()[0];
                int inputX = xPos;
                int inputY = yPos;
                int inputCount = 1;
                switch (ingredientIndex) {
                    case 0:
                        inputX += 31;
                        inputY += 12;
                        break;
                    case 1:
                        inputX += 31;
                        inputY += 55;
                        break;
                    case 2:
                        inputX += 52;
                        inputY += 34;
                        inputCount = recipe.inputCount();
                        break;
                }

                GuideBookScreenHelper.renderItemSlot(pGuiGraphics, pMouseX, pMouseY, inputX, inputY, new ItemStack(inputStack.getItem(), inputCount), textRenderer);

                ingredientIndex++;
            }

            GuideBookScreenHelper.renderItemSlot(pGuiGraphics, pMouseX, pMouseY, xPos + 82, yPos + 55, Items.EGG.getDefaultStack(), textRenderer);
            GuideBookScreenHelper.renderItemSlot(pGuiGraphics, pMouseX, pMouseY, xPos + 125, yPos + 34, recipe.output().getFirst(), textRenderer);

            pGuiGraphics.getMatrices().push();
            pGuiGraphics.getMatrices().translate(xPos + 9, yPos + 4, 0);
            pGuiGraphics.getMatrices().scale(0.8f, 0.8f, 0.8f);
            pGuiGraphics.drawText(textRenderer, recipe.output().getFirst().toHoverableText().getString().substring(1, recipe.output().getFirst().toHoverableText().getString().length() - 1), 0, 0, 0x555555, false);
            pGuiGraphics.getMatrices().pop();

            index++;
        }

        int scrollbarX = width - SCROLLBAR_WIDTH;
        int scrollbarHeight = (int) ((float) height / totalContentHeight * height);
        int scrollbarY = (int) ((float) contentScrollOffset / totalContentHeight * height);
        pGuiGraphics.fill(scrollbarX, 0, scrollbarX + SCROLLBAR_WIDTH, height, 0x55555555);
        pGuiGraphics.fill(scrollbarX, scrollbarY, scrollbarX + SCROLLBAR_WIDTH, scrollbarY + scrollbarHeight, 0x55888888);
    }

    private void drawMelting(DrawContext pGuiGraphics, int pMouseX, int pMouseY) {
        int contentX = 10 + NAVIGATION_WIDTH;
        int contentY = 10;
        int contentWidth = this.width - contentX - 10;
        contentX += (contentWidth - (RECIPE_WIDTH * COLUMNS)) / 2;

        Identifier TEXTURE = Identifier.of(ProductiveSlimes.MODID, "textures/gui/rei/melting_station_gui.png");

        int infoY = contentY - contentScrollOffset;

        // Render the info section
        Text title = Text.translatable("guidebook.productiveslimes.slimeball_melting");
        int fontX = textRenderer.getWidth(title);
        pGuiGraphics.drawTextWithShadow(textRenderer, title, (int) (contentX + (contentWidth - fontX) / 2 * 0.8f), infoY + 5, 0xFFFFFF);

        Text description = Text.translatable("guidebook.productiveslimes.slimeball_melting.description");
        pGuiGraphics.drawTextWrapped(textRenderer, description, contentX + 5, infoY + 20, contentWidth - 20, 0xAAAAAA);

        pGuiGraphics.drawTexture(CRAFTING_TEXTURE, (int) (contentX + (contentWidth - RECIPE_WIDTH) / 2 * 0.8f), infoY + 45, 0, 0, RECIPE_WIDTH, RECIPE_HEIGHT, 256, 256);

        Optional<?> extractor = handler.world.getRecipeManager().get(Identifier.of(ProductiveSlimes.MODID, "melting_station"));
        if (extractor.isPresent()){
            if (extractor.get() instanceof ShapedRecipe shapedRecipe) {
                DefaultedList<Ingredient> ingredients = shapedRecipe.getIngredients();
                for (int i = 0; i < ingredients.size(); i++) {
                    Ingredient ingredient = ingredients.get(i);
                    ItemStack stacks = ingredient.getMatchingStacks()[0];
                    GuideBookScreenHelper.renderItemSlot(pGuiGraphics, pMouseX, pMouseY, (int) (contentX + (contentWidth - RECIPE_WIDTH) / 2 * 0.8f) + 19 + (i % 3) * 18, infoY + 46 + 16 + (i / 3) * 18, stacks, textRenderer);
                }

                GuideBookScreenHelper.renderItemSlot(pGuiGraphics, pMouseX, pMouseY, (int) (contentX + (contentWidth - RECIPE_WIDTH) / 2 * 0.8f) + 95 + 18, infoY + 46 + 16 + 18, ModBlocks.DNA_EXTRACTOR.asItem().getDefaultStack(), textRenderer);
            }
        }

        int index = 0;
        int numRecipeRows = (int) Math.ceil((double) meltingRecipeList.size() / COLUMNS);
        int totalRecipeHeight = numRecipeRows * (RECIPE_HEIGHT + V_SPACING);
        int totalContentHeight = INFO_SECTION_HEIGHT + totalRecipeHeight;

        // Render each recipe
        for (MeltingRecipe recipe : meltingRecipeList) {
            int row = index / COLUMNS;
            int col = index % COLUMNS;
            int xPos = contentX + col * (RECIPE_WIDTH + H_SPACING);
            int yPos = contentY + INFO_SECTION_HEIGHT + row * (RECIPE_HEIGHT + V_SPACING) - contentScrollOffset;

            // Render recipe background (optional)
            pGuiGraphics.drawTexture(TEXTURE, xPos, yPos, 0, 0, RECIPE_WIDTH, RECIPE_HEIGHT, 256, 256);

            // Render energy bar
            int energyScaled = (int) (((float) recipe.getEnergy() / (float) 10000) * 57);
            pGuiGraphics.drawTexture(TEXTURE, xPos + 9, yPos + 13 + (57 - energyScaled), 153, 8, 9, energyScaled, 256, 256);
            if (pMouseX >= xPos + 9 && pMouseX < xPos + 18 && pMouseY >= yPos + 13 && pMouseY < yPos + 70) {
                Text text = Text.translatable("gui.productiveslimes.energy_stored", recipe.getEnergy(), 10000);
                pGuiGraphics.drawTooltip(textRenderer, text, pMouseX, pMouseY);
            }

            GuideBookScreenHelper.renderItemSlot(pGuiGraphics, pMouseX, pMouseY, xPos + 25, yPos + 34, new ItemStack(Items.BUCKET, recipe.getOutputs().getFirst().getCount()), textRenderer);
            GuideBookScreenHelper.renderItemSlot(pGuiGraphics, pMouseX, pMouseY, xPos + 45, yPos + 34, new ItemStack(recipe.getInputItems().getFirst().getMatchingStacks()[0].getItem(), recipe.getInputCount()), textRenderer);
            GuideBookScreenHelper.renderItemSlot(pGuiGraphics, pMouseX, pMouseY, xPos + 108 + 20, yPos + 34, recipe.getOutputs().getFirst(), textRenderer);

            pGuiGraphics.getMatrices().push();
            pGuiGraphics.getMatrices().translate(xPos + 9, yPos + 4, 0);
            pGuiGraphics.getMatrices().scale(0.8f, 0.8f, 0.8f);
            pGuiGraphics.drawText(textRenderer, recipe.getOutputs().getFirst().toHoverableText().getString().substring(1, recipe.getOutputs().getFirst().toHoverableText().getString().length() - 1), 0, 0, 0x555555, false);
            pGuiGraphics.getMatrices().pop();

            index++;
        }

        int scrollbarX = width - SCROLLBAR_WIDTH;
        int scrollbarHeight = (int) ((float) height / totalContentHeight * height);
        int scrollbarY = (int) ((float) contentScrollOffset / totalContentHeight * height);
        pGuiGraphics.fill(scrollbarX, 0, scrollbarX + SCROLLBAR_WIDTH, height, 0x55555555);
        pGuiGraphics.fill(scrollbarX, scrollbarY, scrollbarX + SCROLLBAR_WIDTH, scrollbarY + scrollbarHeight, 0x55888888);
    }

    private void drawSoliding(DrawContext pGuiGraphics, int pMouseX, int pMouseY) {
        int contentX = 10 + NAVIGATION_WIDTH;
        int contentY = 10;
        int contentWidth = this.width - contentX - 10;
        contentX += (contentWidth - (RECIPE_WIDTH * COLUMNS)) / 2;

        Identifier TEXTURE = Identifier.of(ProductiveSlimes.MODID, "textures/gui/rei/soliding_station_gui.png");

        int infoY = contentY - contentScrollOffset;

        // Render the info section
        Text title = Text.translatable("guidebook.productiveslimes.soliding");
        int fontX = textRenderer.getWidth(title);
        pGuiGraphics.drawTextWithShadow(textRenderer, title, (int) (contentX + (contentWidth - fontX) / 2 * 0.8f), infoY + 5, 0xFFFFFF);

        Text description = Text.translatable("guidebook.productiveslimes.soliding.description");
        pGuiGraphics.drawTextWrapped(textRenderer, description, contentX + 5, infoY + 20, contentWidth - 20, 0xAAAAAA);

        pGuiGraphics.drawTexture(CRAFTING_TEXTURE, (int) (contentX + (contentWidth - RECIPE_WIDTH) / 2 * 0.8f), infoY + 45, 0, 0, RECIPE_WIDTH, RECIPE_HEIGHT, 256, 256);

        Optional<?> extractor = handler.world.getRecipeManager().get(Identifier.of(ProductiveSlimes.MODID, "soliding_station"));
        if(extractor.isPresent()){
            if (extractor.get() instanceof ShapedRecipe shapedRecipe) {
                DefaultedList<Ingredient> ingredients = shapedRecipe.getIngredients();
                for (int i = 0; i < ingredients.size(); i++) {
                    Ingredient ingredient = ingredients.get(i);
                    GuideBookScreenHelper.renderItemSlot(pGuiGraphics, pMouseX, pMouseY, (int) (contentX + (contentWidth - RECIPE_WIDTH) / 2 * 0.8f) + 19 + (i % 3) * 18, infoY + 46 + 16 + (i / 3) * 18, ingredient.getMatchingStacks()[0], textRenderer);

                }

                ItemStack output = ModBlocks.SOLIDING_STATION.asItem().getDefaultStack();
                GuideBookScreenHelper.renderItemSlot(pGuiGraphics, pMouseX, pMouseY, (int) (contentX + (contentWidth - RECIPE_WIDTH) / 2 * 0.8f) + 95 + 18, infoY + 46 + 16 + 18, output, textRenderer);
            }
        }

        int index = 0;
        int numRecipeRows = (int) Math.ceil((double) solidingRecipeList.size() / COLUMNS);
        int totalRecipeHeight = numRecipeRows * (RECIPE_HEIGHT + V_SPACING);
        int totalContentHeight = INFO_SECTION_HEIGHT + totalRecipeHeight;

        // Render each recipe
        for (SolidingRecipe recipe : solidingRecipeList) {
            int row = index / COLUMNS;
            int col = index % COLUMNS;
            int xPos = contentX + col * (RECIPE_WIDTH + H_SPACING);
            int yPos = contentY + INFO_SECTION_HEIGHT + row * (RECIPE_HEIGHT + V_SPACING) - contentScrollOffset;

            // Render recipe background (optional)
            pGuiGraphics.drawTexture(TEXTURE, xPos, yPos, 0, 0, RECIPE_WIDTH, RECIPE_HEIGHT, 256, 256);

            // Render energy bar
            int energyScaled = (int) (((float) recipe.getEnergy() / (float) 10000) * 57);
            pGuiGraphics.drawTexture(TEXTURE, xPos + 9, yPos + 13 + (57 - energyScaled), 153, 8, 9, energyScaled, 256, 256);
            if (pMouseX >= xPos + 9 && pMouseX < xPos + 18 && pMouseY >= yPos + 13 && pMouseY < yPos + 70) {
                Text text = Text.translatable("gui.productiveslimes.energy_stored", recipe.getEnergy(), 10000);
                pGuiGraphics.drawTooltip(textRenderer, text, pMouseX, pMouseY);
            }

            GuideBookScreenHelper.renderItemSlot(pGuiGraphics, pMouseX, pMouseY, xPos + 26, yPos + 34, new ItemStack( recipe.getInputItems().getFirst().getMatchingStacks()[0].getItem(), recipe.getInputCount()), textRenderer);
            GuideBookScreenHelper.renderItemSlot(pGuiGraphics, pMouseX, pMouseY, xPos + 87 + 20, yPos + 34, recipe.getOutputs().getFirst(), textRenderer);
            GuideBookScreenHelper.renderItemSlot(pGuiGraphics, pMouseX, pMouseY, xPos + 107 + 20, yPos + 34, recipe.getOutputs().get(1), textRenderer);

            pGuiGraphics.drawText(textRenderer, recipe.getOutputs().getFirst().toHoverableText().getString().substring(1, recipe.getOutputs().getFirst().toHoverableText().getString().length() - 1), xPos + 9, yPos + 4, 0x555555, false);

            index++;
        }

        int scrollbarX = width - SCROLLBAR_WIDTH;
        int scrollbarHeight = (int) ((float) height / totalContentHeight * height);
        int scrollbarY = (int) ((float) contentScrollOffset / totalContentHeight * height);
        pGuiGraphics.fill(scrollbarX, 0, scrollbarX + SCROLLBAR_WIDTH, height, 0x55555555);
        pGuiGraphics.fill(scrollbarX, scrollbarY, scrollbarX + SCROLLBAR_WIDTH, scrollbarY + scrollbarHeight, 0x55888888);
    }

    private void drawSqueezing(DrawContext pGuiGraphics, int pMouseX, int pMouseY) {
        int contentX = 10 + NAVIGATION_WIDTH;
        int contentY = 10;
        int contentWidth = this.width - contentX - 10;
        contentX += (contentWidth - (RECIPE_WIDTH * COLUMNS)) / 2;

        Identifier TEXTURE = Identifier.of(ProductiveSlimes.MODID, "textures/gui/rei/slime_squeezer_gui.png");

        int infoY = contentY - contentScrollOffset;

        // Render the info section
        Text title = Text.translatable("guidebook.productiveslimes.squeezing");
        int fontX = textRenderer.getWidth(title);
        pGuiGraphics.drawTextWithShadow(textRenderer, title, (int) (contentX + (contentWidth - fontX) / 2 * 0.8f), infoY + 5, 0xFFFFFF);

        Text description = Text.translatable("guidebook.productiveslimes.squeezing.description");
        pGuiGraphics.drawTextWrapped(textRenderer, description, contentX + 5, infoY + 20, contentWidth - 20, 0xAAAAAA);

        pGuiGraphics.drawTexture(CRAFTING_TEXTURE, contentX, infoY + 45, 0, 0, RECIPE_WIDTH, RECIPE_HEIGHT, 256, 256);
        pGuiGraphics.drawTexture(CRAFTING_TEXTURE, contentX + RECIPE_WIDTH + V_SPACING, infoY + 45, 0, 0, RECIPE_WIDTH, RECIPE_HEIGHT, 256, 256);

        Optional<?> squeezer = handler.world.getRecipeManager().get(Identifier.of(ProductiveSlimes.MODID, "squeezer"));
        if (squeezer.isPresent()){
            if (squeezer.get() instanceof ShapedRecipe shapedRecipe) {
                DefaultedList<Ingredient> ingredients = shapedRecipe.getIngredients();
                for (int i = 0; i < ingredients.size(); i++) {
                    Ingredient ingredient = ingredients.get(i);
                    GuideBookScreenHelper.renderItemSlot(pGuiGraphics, pMouseX, pMouseY, contentX + 19 + (i % 3) * 18, infoY + 46 + 16 + (i / 3) * 18, ingredient.getMatchingStacks().length > 0 ? ingredient.getMatchingStacks()[0] : ItemStack.EMPTY, textRenderer);
                }

                GuideBookScreenHelper.renderItemSlot(pGuiGraphics, pMouseX, pMouseY, contentX + 95 + 18, infoY + 46 + 16 + 18, ModBlocks.SQUEEZER.asItem().getDefaultStack(), textRenderer);
            }
        }

        Optional<?> extractor = handler.world.getRecipeManager().get(Identifier.of(ProductiveSlimes.MODID, "slime_squeezer"));
        if (extractor.isPresent()){
            if (extractor.get() instanceof ShapedRecipe shapedRecipe) {
                DefaultedList<Ingredient> ingredients = shapedRecipe.getIngredients();
                for (int i = 0; i < ingredients.size(); i++) {
                    Ingredient ingredient = ingredients.get(i);
                    GuideBookScreenHelper.renderItemSlot(pGuiGraphics, pMouseX, pMouseY, contentX + RECIPE_WIDTH + V_SPACING + 19 + (i % 3) * 18, infoY + 46 + 16 + (i / 3) * 18, ingredient.getMatchingStacks().length > 0 ? ingredient.getMatchingStacks()[0] : ItemStack.EMPTY, textRenderer);
                }

                GuideBookScreenHelper.renderItemSlot(pGuiGraphics, pMouseX, pMouseY, contentX + RECIPE_WIDTH + V_SPACING + 95 + 18, infoY + 46 + 16 + 18, ModBlocks.SLIME_SQUEEZER.asItem().getDefaultStack(), textRenderer);
            }
        }

        int index = 0;
        int numRecipeRows = (int) Math.ceil((double) squeezingRecipeList.size() / COLUMNS);
        int totalRecipeHeight = numRecipeRows * (RECIPE_HEIGHT + V_SPACING);
        int totalContentHeight = INFO_SECTION_HEIGHT + totalRecipeHeight;

        // Render each recipe
        for (SqueezingRecipe recipe : squeezingRecipeList) {
            int row = index / COLUMNS;
            int col = index % COLUMNS;
            int xPos = contentX + col * (RECIPE_WIDTH + H_SPACING);
            int yPos = contentY + INFO_SECTION_HEIGHT + row * (RECIPE_HEIGHT + V_SPACING) - contentScrollOffset;

            // Render recipe background (optional)
            pGuiGraphics.drawTexture(TEXTURE, xPos, yPos, 0, 0, RECIPE_WIDTH, RECIPE_HEIGHT, 256, 256);

            // Render energy bar
            int energyScaled = (int) (((float) recipe.energy() / (float) 10000) * 57);
            pGuiGraphics.drawTexture(TEXTURE, xPos + 9, yPos + 13 + (57 - energyScaled), 153, 8, 9, energyScaled, 256, 256);
            if (pMouseX >= xPos + 9 && pMouseX < xPos + 18 && pMouseY >= yPos + 13 && pMouseY < yPos + 70) {
                Text text = Text.translatable("gui.productiveslimes.energy_stored", recipe.energy(), 10000);
                pGuiGraphics.drawTooltip(textRenderer, text, pMouseX, pMouseY);
            }

            GuideBookScreenHelper.renderItemSlot(pGuiGraphics, pMouseX, pMouseY, xPos + 26, yPos + 34, recipe.inputItems().getFirst().getMatchingStacks()[0], textRenderer);
            GuideBookScreenHelper.renderItemSlot(pGuiGraphics, pMouseX, pMouseY, xPos + 87 + 20, yPos + 34, recipe.output().getFirst(), textRenderer);
            GuideBookScreenHelper.renderItemSlot(pGuiGraphics, pMouseX, pMouseY, xPos + 107 + 20, yPos + 34, recipe.output().get(1), textRenderer);

            pGuiGraphics.drawText(textRenderer, recipe.output().getFirst().toHoverableText().getString().substring(1, recipe.output().getFirst().toHoverableText().getString().length() - 1), xPos + 9, yPos + 4, 0x555555, false);

            index++;
        }

        int scrollbarX = width - SCROLLBAR_WIDTH;
        int scrollbarHeight = (int) ((float) height / totalContentHeight * height);
        int scrollbarY = (int) ((float) contentScrollOffset / totalContentHeight * height);
        pGuiGraphics.fill(scrollbarX, 0, scrollbarX + SCROLLBAR_WIDTH, height, 0x55555555);
        pGuiGraphics.fill(scrollbarX, scrollbarY, scrollbarX + SCROLLBAR_WIDTH, scrollbarY + scrollbarHeight, 0x55888888);
    }

    private void drawSlimeAndSlimeball(DrawContext pGuiGraphics, int pMouseX, int pMouseY) {
        int contentX = 10 + NAVIGATION_WIDTH;
        int contentY = 10;
        int contentWidth = this.width - contentX - 10;
        contentX += (contentWidth - (RECIPE_WIDTH * COLUMNS)) / 2;

        int cooldownGUIHeight = 46;

        int infoY = contentY - contentScrollOffset;
        int wordWarpLength = (int) (contentWidth * 0.85f);

        Text title = Text.translatable("guidebook.productiveslimes.slime_and_slimeball");
        int fontX = textRenderer.getWidth(title);
        pGuiGraphics.drawTextWithShadow(textRenderer, title, (int) (contentX + (contentWidth - fontX) / 2 * 0.8f), infoY + 5, 0xFFFFFF);

        Text description = Text.translatable("guidebook.productiveslimes.slime_and_slimeball.description1");
        pGuiGraphics.drawTextWrapped(textRenderer, description, contentX + 5, infoY + 20, wordWarpLength, 0xAAAAAA);

        Text description2 = Text.translatable("guidebook.productiveslimes.slime_and_slimeball.description2");
        pGuiGraphics.drawTextWrapped(textRenderer, description2, contentX + 5, infoY + textRenderer.getWrappedLinesHeight(description, wordWarpLength) + 25, (int) (contentWidth * 0.85f), 0xAAAAAA);

        Text title2 = Text.translatable("guidebook.productiveslimes.slime_growing");
        int fontX2 = textRenderer.getWidth(title2);
        pGuiGraphics.drawTextWithShadow(textRenderer, title2, (int) (contentX + (contentWidth - fontX2) / 2 * 0.8f), infoY + textRenderer.getWrappedLinesHeight(description, wordWarpLength) + textRenderer.getWrappedLinesHeight(description2, wordWarpLength) + 30, 0xFFFFFF);

        Text description3 = Text.translatable("guidebook.productiveslimes.slime_growing.description");
        pGuiGraphics.drawTextWrapped(textRenderer, description3, contentX + 5, infoY + textRenderer.getWrappedLinesHeight(description, wordWarpLength) + textRenderer.getWrappedLinesHeight(description2, wordWarpLength) + textRenderer.getWrappedLinesHeight(title2, wordWarpLength) + 35, (int) (contentWidth * 0.85f), 0xAAAAAA);

        SLIME_AND_SLIMEBALL_INFO_HEIGHT = contentY + textRenderer.getWrappedLinesHeight(description, wordWarpLength) + textRenderer.getWrappedLinesHeight(description2, wordWarpLength) + textRenderer.getWrappedLinesHeight(title2, wordWarpLength) + textRenderer.getWrappedLinesHeight(description3, wordWarpLength) + 40;

        List<ModTier> registeredTiers = ModTiers.getRegisteredTiers();
        int index = 0;
        int numRecipeRows = (int) Math.ceil((double) registeredTiers.size() / COLUMNS);
        int totalRecipeHeight = numRecipeRows * (RECIPE_HEIGHT + V_SPACING);

        Identifier TEXTURE = Identifier.of(ProductiveSlimes.MODID, "textures/gui/guidebook/slime_grow_gui.png");

        for (ModTier tiers : registeredTiers) {
            int row = index / COLUMNS;
            int col = index % COLUMNS;
            int xPos = contentX + col * (RECIPE_WIDTH + H_SPACING);
            int yPos = contentY + SLIME_AND_SLIMEBALL_INFO_HEIGHT + row * (RECIPE_HEIGHT + V_SPACING) - contentScrollOffset;

            pGuiGraphics.drawTexture(TEXTURE, xPos, yPos, 0, 0, RECIPE_WIDTH, RECIPE_HEIGHT, 256, 256);

            Text tierName = Text.translatable("entity.productiveslimes." + tiers.name() + "_slime");
            pGuiGraphics.getMatrices().push();
            pGuiGraphics.getMatrices().translate(xPos + 5, yPos + 3, 0);
            pGuiGraphics.getMatrices().scale(0.8f, 0.8f, 0.8f);
            pGuiGraphics.drawText(textRenderer, tierName, 0, 0, 0x555555, false);
            pGuiGraphics.getMatrices().pop();

            SlimeData slimeData = GuideBookScreenHelper.generateSlimeData(tiers);
            ItemStack slimeItem = new ItemStack(ModItems.SLIME_ITEM);
            NbtCompound tag = new NbtCompound();
            tag.put("slime_data", slimeData.toTag(new NbtCompound()));
            slimeItem.setNbt(tag);

            GuideBookScreenHelper.renderItemSlot(pGuiGraphics, pMouseX, pMouseY, xPos + 63, yPos + 10, slimeItem, textRenderer);
            GuideBookScreenHelper.renderItemSlot(pGuiGraphics, pMouseX, pMouseY, xPos + 63, yPos + 36, slimeItem, textRenderer);
            GuideBookScreenHelper.renderItemSlot(pGuiGraphics, pMouseX, pMouseY, xPos + 63, yPos + 62, slimeItem, textRenderer);

            ItemStack growthItem = slimeData.growthItem();
            growthItem.setCount(3);

            GuideBookScreenHelper.renderItemSlot(pGuiGraphics, pMouseX, pMouseY, xPos + 124, yPos + 10, growthItem, textRenderer);
            growthItem.setCount(4);
            GuideBookScreenHelper.renderItemSlot(pGuiGraphics, pMouseX, pMouseY, xPos + 124, yPos + 36, growthItem, textRenderer);
            growthItem.setCount(5);
            GuideBookScreenHelper.renderItemSlot(pGuiGraphics, pMouseX, pMouseY, xPos + 124, yPos + 62, growthItem, textRenderer);

            index++;
        }

        int infoY2 = contentY - contentScrollOffset + SLIME_AND_SLIMEBALL_INFO_HEIGHT + totalRecipeHeight;

        Text title3 = Text.translatable("guidebook.productiveslimes.slimeball_obtaining");
        int fontX3 = textRenderer.getWidth(title);
        pGuiGraphics.drawTextWithShadow(textRenderer, title3, (int) (contentX + (contentWidth - fontX3) / 2 * 0.8f), infoY2 + 5, 0xFFFFFF);

        Text description4 = Text.translatable("guidebook.productiveslimes.slimeball_obtaining.description");
        pGuiGraphics.drawTextWrapped(textRenderer, description4, contentX + 5, infoY2 + 20, wordWarpLength, 0xAAAAAA);

        Text description5 = Text.translatable("guidebook.productiveslimes.slimeball_obtaining.description2");
        pGuiGraphics.drawTextWrapped(textRenderer, description5, contentX + 5, infoY2 + 25 + textRenderer.getWrappedLinesHeight(description4, wordWarpLength), wordWarpLength, 0xAAAAAA);

        Text title4 = Text.translatable("guidebook.productiveslimes.slime_cooldown_time");
        int fontX4 = textRenderer.getWidth(title);
        pGuiGraphics.drawTextWithShadow(textRenderer, title4, (int) (contentX + (contentWidth - fontX4) / 2 * 0.8f), infoY2 + 25 + textRenderer.getWrappedLinesHeight(description4, wordWarpLength) + textRenderer.getWrappedLinesHeight(description5, wordWarpLength) + 5, 0xFFFFFF);

        SLIME_AND_SLIMEBALL_SECOND_INFO_HEIGHT = textRenderer.getWrappedLinesHeight(description4, wordWarpLength) + textRenderer.getWrappedLinesHeight(description5, wordWarpLength) + 45;

        int index2 = 0;
        int numCooldownRow = (int) Math.ceil((double) registeredTiers.size() / COLUMNS);
        int totalCooldownHeight = numCooldownRow * (cooldownGUIHeight + V_SPACING);

        Identifier COOLDOWN_TEXTURE = Identifier.of(ProductiveSlimes.MODID, "textures/gui/guidebook/slime_cooldown_gui.png");

        for (ModTier tiers : registeredTiers) {
            int row = index2 / COLUMNS;
            int col = index2 % COLUMNS;
            int xPos = contentX + col * (RECIPE_WIDTH + H_SPACING);
            int yPos = contentY + SLIME_AND_SLIMEBALL_INFO_HEIGHT + SLIME_AND_SLIMEBALL_SECOND_INFO_HEIGHT + totalRecipeHeight + row * (cooldownGUIHeight + V_SPACING) - contentScrollOffset;

            pGuiGraphics.drawTexture(COOLDOWN_TEXTURE, xPos, yPos, 0, 0, RECIPE_WIDTH, cooldownGUIHeight, 256, 256);

            Text tierName = Text.translatable("entity.productiveslimes." + tiers.name() + "_slime");
            pGuiGraphics.drawText(textRenderer, tierName, xPos + 6, yPos + 4, 0x555555, false);

            SlimeData slimeData = GuideBookScreenHelper.generateSlimeData(tiers);
            ItemStack slimeItem = new ItemStack(ModItems.SLIME_ITEM);
            NbtCompound tag = new NbtCompound();
            tag.put("slime_data", slimeData.toTag(new NbtCompound()));
            slimeItem.setNbt(tag);

            GuideBookScreenHelper.renderItemSlot(pGuiGraphics, pMouseX, pMouseY, xPos + 29, yPos + 15, slimeItem, textRenderer);

            Text cooldownText = Text.translatable("guidebook.productiveslimes.cooldown", tiers.cooldown() / 20);
            pGuiGraphics.drawText(textRenderer, cooldownText, xPos + 55, yPos + 19, 0x555555, false);

            index2++;
        }

        int infoY3 = contentY - contentScrollOffset + SLIME_AND_SLIMEBALL_INFO_HEIGHT + totalRecipeHeight + SLIME_AND_SLIMEBALL_SECOND_INFO_HEIGHT + totalCooldownHeight;

        Text title5 = Text.translatable("block.productiveslimes.slimeball_collector");
        int fontX5 = textRenderer.getWidth(title5);
        pGuiGraphics.drawTextWithShadow(textRenderer, title5, (int) (contentX + (contentWidth - fontX5) / 2 * 0.8f), infoY3 + 5, 0xFFFFFF);

        Text description6 = Text.translatable("guidebook.productiveslimes.slimeball_collector.description");
        pGuiGraphics.drawTextWrapped(textRenderer, description6, contentX + 5, infoY3 + 20, wordWarpLength, 0xAAAAAA);

        Optional<?> slimeballCollectorHolder = handler.world.getRecipeManager().get(Identifier.of(ProductiveSlimes.MODID, "slimeball_collector"));
        ShapedRecipe slimeballCollector = (ShapedRecipe) slimeballCollectorHolder.get();
        DefaultedList<Ingredient> ingredients = slimeballCollector.getIngredients();

        int textureY = infoY3 + 20 + textRenderer.getWrappedLinesHeight(description6, wordWarpLength) + 10;

        pGuiGraphics.drawTexture(CRAFTING_TEXTURE, (int) (contentX + (contentWidth - RECIPE_WIDTH) / 2 * 0.8f), textureY, 0, 0, RECIPE_WIDTH, RECIPE_HEIGHT, 256, 256);

        for (int i = 0; i < ingredients.size(); i++) {
            Ingredient ingredient = ingredients.get(i);
            ItemStack stacks = ingredient.getMatchingStacks()[0];
            GuideBookScreenHelper.renderItemSlot(pGuiGraphics, pMouseX, pMouseY, (int) (contentX + (contentWidth - RECIPE_WIDTH) / 2 * 0.8f) + 19 + (i % 3) * 18, textureY + 17 + (i / 3) * 18, stacks, textRenderer);
        }

        ItemStack output = ModBlocks.SLIMEBALL_COLLECTOR.asItem().getDefaultStack();
        GuideBookScreenHelper.renderItemSlot(pGuiGraphics, pMouseX, pMouseY, (int) (contentX + (contentWidth - RECIPE_WIDTH) / 2 * 0.8f) + 95 + 18, textureY + 17 + 18, output, textRenderer);

        Text note = Text.translatable("guidebook.productiveslimes.slimeball_collector.note");
        pGuiGraphics.drawText(textRenderer, note, (int) (contentX + (contentWidth - textRenderer.getWidth(note)) / 2 * 0.8f), infoY3 + 110 + 18 + 26, 0x555555, false);

        Text title6 = Text.translatable("guidebook.productiveslimes.slime_simulation_chamber_and_upgrades");
        int fontX6 = textRenderer.getWidth(title6);
        pGuiGraphics.drawTextWithShadow(textRenderer, title6, (int) (contentX + (contentWidth - fontX6) / 2 * 0.8f), infoY3 + 110 + 18 + 26 + textRenderer.getWrappedLinesHeight(note, wordWarpLength) + 15, 0xFFFFFF);

        Text description7 = Text.translatable("guidebook.productiveslimes.slime_simulation_chamber_and_upgrades.description");
        pGuiGraphics.drawTextWrapped(textRenderer, description7, contentX + 5, infoY3 + 110 + 18 + 26 + textRenderer.getWrappedLinesHeight(note, wordWarpLength) + textRenderer.getWrappedLinesHeight(title6, wordWarpLength) + 20, wordWarpLength, 0xAAAAAA);

        Text description8 = Text.translatable("guidebook.productiveslimes.slime_simulation_chamber_and_upgrades.description2");
        pGuiGraphics.drawTextWrapped(textRenderer, description8, contentX + 5, infoY3 + 110 + 18 + 26 + textRenderer.getWrappedLinesHeight(note, wordWarpLength) + textRenderer.getWrappedLinesHeight(title6, wordWarpLength) + textRenderer.getWrappedLinesHeight(description7, wordWarpLength) + 25, wordWarpLength, 0xAAAAAA);

        Optional<?> slimeNestHolder = handler.world.getRecipeManager().get(Identifier.of(ProductiveSlimes.MODID, "slime_nest"));
        ShapedRecipe SlimeNest = (ShapedRecipe) slimeNestHolder.get();
        DefaultedList<Ingredient> ingredients2 = SlimeNest.getIngredients();

        int textureY2 = infoY3 + 110 + 18 + 26 + textRenderer.getWrappedLinesHeight(note, wordWarpLength) + textRenderer.getWrappedLinesHeight(title6, wordWarpLength) + textRenderer.getWrappedLinesHeight(description7, wordWarpLength) + textRenderer.getWrappedLinesHeight(description8, wordWarpLength) + 30;

        pGuiGraphics.drawTexture(CRAFTING_TEXTURE, (int) (contentX + (contentWidth - RECIPE_WIDTH) / 2 * 0.8f), textureY2, 0, 0, RECIPE_WIDTH, RECIPE_HEIGHT, 256, 256);

        for (int i = 0; i < ingredients2.size(); i++) {
            Ingredient ingredient = ingredients2.get(i);
            ItemStack stacks = ingredient.getMatchingStacks()[0];
            GuideBookScreenHelper.renderItemSlot(pGuiGraphics, pMouseX, pMouseY, (int) (contentX + (contentWidth - RECIPE_WIDTH) / 2 * 0.8f) + 19 + (i % 3) * 18, textureY2 + 17 + (i / 3) * 18, stacks, textRenderer);
        }

        ItemStack output2 = ModBlocks.SLIME_NEST.asItem().getDefaultStack();
        GuideBookScreenHelper.renderItemSlot(pGuiGraphics, pMouseX, pMouseY, (int) (contentX + (contentWidth - RECIPE_WIDTH) / 2 * 0.8f) + 95 + 18, textureY2 + 17 + 18, output2, textRenderer);

        Optional<?> upgrade1Holder = handler.world.getRecipeManager().get(Identifier.of(ProductiveSlimes.MODID, "slime_nest_speed_upgrade_1"));
        ShapedRecipe upgrade1 = (ShapedRecipe) upgrade1Holder.get();
        DefaultedList<Ingredient> ingredients3 = upgrade1.getIngredients();

        int textureY3 = infoY3 + 110 + 18 + 26 + textRenderer.getWrappedLinesHeight(note, wordWarpLength) + textRenderer.getWrappedLinesHeight(title6, wordWarpLength) + textRenderer.getWrappedLinesHeight(description7, wordWarpLength) + textRenderer.getWrappedLinesHeight(description8, wordWarpLength) + 30 + RECIPE_HEIGHT + 10;

        pGuiGraphics.drawTexture(CRAFTING_TEXTURE, (int) (contentX + (contentWidth - RECIPE_WIDTH) / 2 * 0.8f), textureY3, 0, 0, RECIPE_WIDTH, RECIPE_HEIGHT, 256, 256);

        for (int i = 0; i < ingredients3.size(); i++) {
            Ingredient ingredient = ingredients3.get(i);
            ItemStack stacks = ingredient.getMatchingStacks()[0];
            GuideBookScreenHelper.renderItemSlot(pGuiGraphics, pMouseX, pMouseY, (int) (contentX + (contentWidth - RECIPE_WIDTH) / 2 * 0.8f) + 19 + (i % 3) * 18, textureY3 + 17 + (i / 3) * 18, stacks, textRenderer);
        }

        ItemStack output3 = ModItems.SLIME_NEST_SPEED_UPGRADE_1.getDefaultStack();
        GuideBookScreenHelper.renderItemSlot(pGuiGraphics, pMouseX, pMouseY, (int) (contentX + (contentWidth - RECIPE_WIDTH) / 2 * 0.8f) + 95 + 18, textureY3 + 17 + 18, output3, textRenderer);

        Optional<?> upgrade2Holder = handler.world.getRecipeManager().get(Identifier.of(ProductiveSlimes.MODID, "slime_nest_speed_upgrade_2"));
        ShapedRecipe upgrade2 = (ShapedRecipe) upgrade2Holder.get();
        DefaultedList<Ingredient> ingredients4 = upgrade2.getIngredients();

        int textureY4 = infoY3 + 110 + 18 + 26 + textRenderer.getWrappedLinesHeight(note, wordWarpLength) + textRenderer.getWrappedLinesHeight(title6, wordWarpLength) + textRenderer.getWrappedLinesHeight(description7, wordWarpLength) + textRenderer.getWrappedLinesHeight(description8, wordWarpLength) + 30 + RECIPE_HEIGHT + 10 + RECIPE_HEIGHT + 10;

        pGuiGraphics.drawTexture(CRAFTING_TEXTURE, (int) (contentX + (contentWidth - RECIPE_WIDTH) / 2 * 0.8f), textureY4, 0, 0, RECIPE_WIDTH, RECIPE_HEIGHT, 256, 256);

        for (int i = 0; i < ingredients4.size(); i++) {
            Ingredient ingredient = ingredients4.get(i);
            ItemStack stacks = ingredient.getMatchingStacks()[0];
            GuideBookScreenHelper.renderItemSlot(pGuiGraphics, pMouseX, pMouseY, (int) (contentX + (contentWidth - RECIPE_WIDTH) / 2 * 0.8f) + 19 + (i % 3) * 18, textureY4 + 17 + (i / 3) * 18, stacks, textRenderer);
        }

        ItemStack output4 = ModItems.SLIME_NEST_SPEED_UPGRADE_2.getDefaultStack();
        GuideBookScreenHelper.renderItemSlot(pGuiGraphics, pMouseX, pMouseY, (int) (contentX + (contentWidth - RECIPE_WIDTH) / 2 * 0.8f) + 95 + 18, textureY4 + 17 + 18, output4, textRenderer);

        Text title7 = Text.translatable("guidebook.productiveslimes.slimeball_fragment");
        int fontX7 = textRenderer.getWidth(title7);
        pGuiGraphics.drawTextWithShadow(textRenderer, title7, (int) (contentX + (contentWidth - fontX7) / 2 * 0.8f), infoY3 + 110 + 18 + 26 + textRenderer.getWrappedLinesHeight(note, wordWarpLength) + textRenderer.getWrappedLinesHeight(title6, wordWarpLength) + textRenderer.getWrappedLinesHeight(description7, wordWarpLength) + textRenderer.getWrappedLinesHeight(description8, wordWarpLength) + 30 + RECIPE_HEIGHT + 10 + RECIPE_HEIGHT + 10 + RECIPE_HEIGHT + 5, 0xFFFFFF);

        Text description9 = Text.translatable("guidebook.productiveslimes.slimeball_fragment.description");
        pGuiGraphics.drawTextWrapped(textRenderer, description9, contentX + 5, infoY3 + 110 + 18 + 26 + textRenderer.getWrappedLinesHeight(note, wordWarpLength) + textRenderer.getWrappedLinesHeight(title6, wordWarpLength) + textRenderer.getWrappedLinesHeight(description7, wordWarpLength) + textRenderer.getWrappedLinesHeight(description8, wordWarpLength) + 30 + RECIPE_HEIGHT + 10 + RECIPE_HEIGHT + 10 + RECIPE_HEIGHT + 10 + 5, wordWarpLength, 0xAAAAAA);

        Optional<?> slimeball = handler.world.getRecipeManager().get(Identifier.of(ProductiveSlimes.MODID, "slimeball_from_fragment"));
        ShapedRecipe slimeballFragment = (ShapedRecipe) slimeball.get();
        DefaultedList<Ingredient> ingredients5 = slimeballFragment.getIngredients();

        int textureY5 = infoY3 + 110 + 18 + 26 + textRenderer.getWrappedLinesHeight(note, wordWarpLength) + textRenderer.getWrappedLinesHeight(title6, wordWarpLength) + textRenderer.getWrappedLinesHeight(description7, wordWarpLength) + textRenderer.getWrappedLinesHeight(description8, wordWarpLength) + 30 + RECIPE_HEIGHT + 10 + RECIPE_HEIGHT + 10 + RECIPE_HEIGHT + 10 + textRenderer.getWrappedLinesHeight(description9, wordWarpLength) + 10;

        pGuiGraphics.drawTexture(CRAFTING_TEXTURE, (int) (contentX + (contentWidth - RECIPE_WIDTH) / 2 * 0.8f), textureY5, 0, 0, RECIPE_WIDTH, RECIPE_HEIGHT, 256, 256);

        for (int i = 0, k = 0; i < ingredients5.size(); i++, k++) {
            Ingredient ingredient = ingredients5.get(i);
            if (k == 2) k+=1;
            ItemStack stacks = ingredient.getMatchingStacks()[0];
            GuideBookScreenHelper.renderItemSlot(pGuiGraphics, pMouseX, pMouseY, (int) (contentX + (contentWidth - RECIPE_WIDTH) / 2 * 0.8f) + 19 + (k % 3) * 18, textureY5 + 17 + (k / 3) * 18, stacks, textRenderer);
        }

        ItemStack output5 = Items.SLIME_BALL.getDefaultStack();
        GuideBookScreenHelper.renderItemSlot(pGuiGraphics, pMouseX, pMouseY, (int) (contentX + (contentWidth - RECIPE_WIDTH) / 2 * 0.8f) + 95 + 18, textureY5 + 17 + 18, output5, textRenderer);

        SLIME_AND_SLIMEBALL_THIRD_INFO_HEIGHT = 600;

        int totalContentHeight = SLIME_AND_SLIMEBALL_INFO_HEIGHT + totalRecipeHeight + SLIME_AND_SLIMEBALL_SECOND_INFO_HEIGHT + totalCooldownHeight + SLIME_AND_SLIMEBALL_THIRD_INFO_HEIGHT;

        int scrollbarX = width - SCROLLBAR_WIDTH;
        int scrollbarHeight = (int) ((float) height / totalContentHeight * height);
        int scrollbarY = (int) ((float) contentScrollOffset / totalContentHeight * height);
        pGuiGraphics.fill(scrollbarX, 0, scrollbarX + SCROLLBAR_WIDTH, height, 0x55555555);
        pGuiGraphics.fill(scrollbarX, scrollbarY, scrollbarX + SCROLLBAR_WIDTH, scrollbarY + scrollbarHeight, 0x55888888);
    }

    private void drawWelcomePage(DrawContext pGuiGraphics, int pMouseX, int pMouseY){
        int contentX = 10 + NAVIGATION_WIDTH + 20;
        int contentY = 10;
        int contentWidth = this.width - contentX - 10;

        int infoY = contentY - contentScrollOffset;

        Text title = Text.translatable("guidebook.productiveslimes.welcome");
        int fontX = textRenderer.getWidth(title);
        pGuiGraphics.drawTextWithShadow(textRenderer, title, contentX + (contentWidth - fontX) / 2, infoY + 5, 0xFFFFFF);

        Text description = Text.translatable("guidebook.productiveslimes.welcome.description");
        pGuiGraphics.drawTextWrapped(textRenderer, description, contentX + 5, infoY + 20, contentWidth, 0xAAAAAA);

        Text description2 = Text.translatable("guidebook.productiveslimes.welcome.description2");
        pGuiGraphics.drawTextWrapped(textRenderer, description2, contentX + 5, infoY + 20 + textRenderer.getWrappedLinesHeight(description, contentWidth) + 5, contentWidth, 0xAAAAAA);

        Text description3 = Text.translatable("guidebook.productiveslimes.welcome.description3");
        pGuiGraphics.drawTextWrapped(textRenderer, description3, contentX + 5, infoY + 20 + textRenderer.getWrappedLinesHeight(description, contentWidth) + textRenderer.getWrappedLinesHeight(description2, contentWidth) + 10, contentWidth, 0xAAAAAA);

        Text description4 = Text.translatable("guidebook.productiveslimes.welcome.description4");
        pGuiGraphics.drawTextWrapped(textRenderer, description4, contentX + 5, infoY + 20 + textRenderer.getWrappedLinesHeight(description, contentWidth) + textRenderer.getWrappedLinesHeight(description2, contentWidth) + textRenderer.getWrappedLinesHeight(description3, contentWidth) + 15, contentWidth, 0xAAAAAA);

        Text wikiLink = Text.translatable("guidebook.productiveslimes.welcome.wiki_link");
        int wikiLinkWidth = textRenderer.getWidth(wikiLink);
        pGuiGraphics.drawTextWithShadow(textRenderer, wikiLink, contentX + (contentWidth - wikiLinkWidth) / 2, infoY + 20 + textRenderer.getWrappedLinesHeight(description, contentWidth) + textRenderer.getWrappedLinesHeight(description2, contentWidth) + textRenderer.getWrappedLinesHeight(description3, contentWidth) + textRenderer.getWrappedLinesHeight(description4, contentWidth) + 20, 0x5555FF);
    }

    private void drawEnergyGeneration(DrawContext pGuiGraphics, int pMouseX, int pMouseY) {
        int contentX = 10 + NAVIGATION_WIDTH + 20;
        int contentY = 10;
        int contentWidth = this.width - contentX - 10 - SCROLLBAR_WIDTH;

        int infoY = contentY - contentScrollOffset;

        Text title = Text.translatable("guidebook.productiveslimes.energy_generation");
        int fontX = textRenderer.getWidth(title);
        pGuiGraphics.drawTextWithShadow(textRenderer, title, contentX + (contentWidth - fontX) / 2, infoY + 5, 0xFFFFFF);

        Text description = Text.translatable("guidebook.productiveslimes.energy_generation.description");
        pGuiGraphics.drawTextWrapped(textRenderer, description, contentX + 5, infoY + 20, contentWidth, 0xAAAAAA);

        int recipeBaseY = infoY + textRenderer.fontHeight + textRenderer.getWrappedLinesHeight(description, contentWidth) + 25;

        pGuiGraphics.drawTexture(CRAFTING_TEXTURE, contentX + (contentWidth - RECIPE_WIDTH) / 2, recipeBaseY, 0, 0, RECIPE_WIDTH, RECIPE_HEIGHT, 256, 256);

        Optional<?> RecipeEntry = handler.world.getRecipeManager().get(Identifier.of(ProductiveSlimes.MODID, "energy_slime_spawn_egg"));
        ShapedRecipe recipe = (ShapedRecipe) RecipeEntry.get();
        DefaultedList<Ingredient> ingredients = recipe.getIngredients();

        for (int i = 0; i < ingredients.size(); i++) {
            Ingredient ingredient = ingredients.get(i);
            ItemStack stacks = ingredient.getMatchingStacks()[0];
            GuideBookScreenHelper.renderItemSlot(pGuiGraphics, pMouseX, pMouseY, contentX + (contentWidth - RECIPE_WIDTH) / 2 + 19 + (i % 3) * 18, recipeBaseY + 17 + (i / 3) * 18, stacks, textRenderer);
        }

        ItemStack output = ModItems.ENERGY_SLIME_SPAWN_EGG.getDefaultStack();
        GuideBookScreenHelper.renderItemSlot(pGuiGraphics, pMouseX, pMouseY, contentX + (contentWidth - RECIPE_WIDTH) / 2 + 95 + 18, recipeBaseY + 17 + 18, output, textRenderer);

        Optional<?> RecipeEntry2 = handler.world.getRecipeManager().get(Identifier.of(ProductiveSlimes.MODID, "energy_generator"));
        ShapedRecipe recipe2 = (ShapedRecipe) RecipeEntry2.get();
        DefaultedList<Ingredient> ingredients2 = recipe2.getIngredients();

        int recipeBaseY2 = recipeBaseY + RECIPE_HEIGHT + 10;

        pGuiGraphics.drawTexture(CRAFTING_TEXTURE, contentX + (contentWidth - RECIPE_WIDTH) / 2, recipeBaseY2, 0, 0, RECIPE_WIDTH, RECIPE_HEIGHT, 256, 256);

        for (int i = 0; i < ingredients2.size(); i++) {
            Ingredient ingredient = ingredients2.get(i);
            ItemStack stacks = ingredient.getMatchingStacks()[0];
            GuideBookScreenHelper.renderItemSlot(pGuiGraphics, pMouseX, pMouseY, contentX + (contentWidth - RECIPE_WIDTH) / 2 + 19 + (i % 3) * 18, recipeBaseY2 + 17 + (i / 3) * 18, stacks, textRenderer);
        }

        ItemStack output2 = ModBlocks.ENERGY_GENERATOR.asItem().getDefaultStack();
        GuideBookScreenHelper.renderItemSlot(pGuiGraphics, pMouseX, pMouseY, contentX + (contentWidth - RECIPE_WIDTH) / 2 + 95 + 18, recipeBaseY2 + 17 + 18, output2, textRenderer);

        Optional<?> RecipeEntry3 = handler.world.getRecipeManager().get(Identifier.of(ProductiveSlimes.MODID, "energy_multiplier_upgrade"));
        ShapedRecipe recipe3 = (ShapedRecipe) RecipeEntry3.get();
        DefaultedList<Ingredient> ingredients3 = recipe3.getIngredients();

        int recipeBaseY3 = recipeBaseY2 + RECIPE_HEIGHT + 10;

        pGuiGraphics.drawTexture(CRAFTING_TEXTURE, contentX + (contentWidth - RECIPE_WIDTH) / 2, recipeBaseY3, 0, 0, RECIPE_WIDTH, RECIPE_HEIGHT, 256, 256);

        for (int i = 0; i < ingredients3.size(); i++) {
            Ingredient ingredient = ingredients3.get(i);
            ItemStack stacks = ingredient.getMatchingStacks()[0];
            GuideBookScreenHelper.renderItemSlot(pGuiGraphics, pMouseX, pMouseY, contentX + (contentWidth - RECIPE_WIDTH) / 2 + 19 + (i % 3) * 18, recipeBaseY3 + 17 + (i / 3) * 18, stacks, textRenderer);
        }

        ItemStack output3 = ModItems.ENERGY_MULTIPLIER_UPGRADE.getDefaultStack();
        GuideBookScreenHelper.renderItemSlot(pGuiGraphics, pMouseX, pMouseY, contentX + (contentWidth - RECIPE_WIDTH) / 2 + 95 + 18, recipeBaseY3 + 17 + 18, output3, textRenderer);

        Optional<?> RecipeEntry4 = handler.world.getRecipeManager().get(Identifier.of(ProductiveSlimes.MODID, "cable"));
        ShapedRecipe recipe4 = (ShapedRecipe) RecipeEntry4.get();
        DefaultedList<Ingredient> ingredients4 = recipe4.getIngredients();

        int recipeBaseY4 = recipeBaseY3 + RECIPE_HEIGHT + 10;

        pGuiGraphics.drawTexture(CRAFTING_TEXTURE, contentX + (contentWidth - RECIPE_WIDTH) / 2, recipeBaseY4, 0, 0, RECIPE_WIDTH, RECIPE_HEIGHT, 256, 256);

        for (int i = 0; i < ingredients4.size(); i++) {
            Ingredient ingredient = ingredients4.get(i);
            ItemStack stacks = ingredient.getMatchingStacks().length > 0 ? ingredient.getMatchingStacks()[0] : ItemStack.EMPTY;
            GuideBookScreenHelper.renderItemSlot(pGuiGraphics, pMouseX, pMouseY, contentX + (contentWidth - RECIPE_WIDTH) / 2 + 19 + (i % 3) * 18, recipeBaseY4 + 17 + (i / 3) * 18, stacks, textRenderer);
        }

        ItemStack output4 = ModBlocks.CABLE.asItem().getDefaultStack();
        GuideBookScreenHelper.renderItemSlot(pGuiGraphics, pMouseX, pMouseY, contentX + (contentWidth - RECIPE_WIDTH) / 2 + 95 + 18, recipeBaseY4 + 17 + 18, output4, textRenderer);

        ENERGY_GENERATION_INFO_HEIGHT = RECIPE_HEIGHT * 4 + 50 + textRenderer.getWrappedLinesHeight(description, contentWidth) + textRenderer.getWrappedLinesHeight(title, contentWidth) + 20;

        int scrollbarX = width - SCROLLBAR_WIDTH;
        int scrollbarHeight = (int) ((float) height / ENERGY_GENERATION_INFO_HEIGHT * height);
        int scrollbarY = (int) ((float) contentScrollOffset / ENERGY_GENERATION_INFO_HEIGHT * height);
        pGuiGraphics.fill(scrollbarX, 0, scrollbarX + SCROLLBAR_WIDTH, height, 0x55555555);
        pGuiGraphics.fill(scrollbarX, scrollbarY, scrollbarX + SCROLLBAR_WIDTH, scrollbarY + scrollbarHeight, 0x55888888);
    }

    private void drawWorldGen(DrawContext pGuiGraphics, int pMouseX, int pMouseY) {
        int contentX = 10 + NAVIGATION_WIDTH + 20;
        int contentY = 10;
        int contentWidth = this.width - contentX - 10 - SCROLLBAR_WIDTH;

        int infoY = contentY - contentScrollOffset;

        Text title = Text.translatable("guidebook.productiveslimes.world_generation");
        int fontX = textRenderer.getWidth(title);
        pGuiGraphics.drawTextWithShadow(textRenderer, title, contentX + (contentWidth - fontX) / 2, infoY + 5, 0xFFFFFF);

        Text description = Text.translatable("guidebook.productiveslimes.world_generation.description");
        pGuiGraphics.drawTextWrapped(textRenderer, description, contentX + 5, infoY + 20, contentWidth, 0xAAAAAA);

        Text description2 = Text.translatable("guidebook.productiveslimes.world_generation.description2");
        pGuiGraphics.drawTextWrapped(textRenderer, description2, contentX + 5, infoY + 20 + textRenderer.getWrappedLinesHeight(description, contentWidth) + 5, contentWidth, 0xAAAAAA);

        Text description3 = Text.translatable("guidebook.productiveslimes.world_generation.description3");
        pGuiGraphics.drawTextWrapped(textRenderer, description3, contentX + 5, infoY + 20 + textRenderer.getWrappedLinesHeight(description, contentWidth) + textRenderer.getWrappedLinesHeight(description2, contentWidth) + 10, contentWidth, 0xAAAAAA);
    }
}