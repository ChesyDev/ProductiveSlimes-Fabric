package com.chesy.productiveslimes.compat.rei.dna_extracting;

import com.chesy.productiveslimes.ProductiveSlimes;
import com.chesy.productiveslimes.block.ModBlocks;
import me.shedaniel.math.Point;
import me.shedaniel.math.Rectangle;
import me.shedaniel.rei.api.client.gui.Renderer;
import me.shedaniel.rei.api.client.gui.widgets.Widget;
import me.shedaniel.rei.api.client.gui.widgets.Widgets;
import me.shedaniel.rei.api.client.registry.display.DisplayCategory;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.entry.EntryIngredient;
import me.shedaniel.rei.api.common.util.EntryStacks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Element;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class DnaExtractingCategory implements DisplayCategory<DnaExtractingRecipeDisplay> {
    public static final Identifier TEXTURE = Identifier.of(ProductiveSlimes.MODID,"textures/gui/rei/dna_extractor_gui.png");

    private int tickCount = 0;

    @Override
    public CategoryIdentifier<? extends DnaExtractingRecipeDisplay> getCategoryIdentifier() {
        return DnaExtractingRecipeDisplay.CATEGORY;
    }

    @Override
    public Text getTitle() {
        return Text.translatable("block.productiveslimes.dna_extractor");
    }

    @Override
    public Renderer getIcon() {
        return EntryStacks.of(ModBlocks.DNA_EXTRACTOR);
    }

    @Override
    public List<Widget> setupDisplay(DnaExtractingRecipeDisplay display, Rectangle bounds) {
        Point startPoint = new Point(bounds.getCenterX() - 77, bounds.getCenterY() - 41);
        List<Widget> widgets = new LinkedList<>();

        widgets.add(Widgets.createTexturedWidget(TEXTURE, new Rectangle(startPoint.x, startPoint.y, 153, 83)));

        widgets.add(Widgets.createSlot(new Point(startPoint.x + 27, startPoint.y + 34))
                .entries(display.getInputEntries().getFirst()).markInput());

        widgets.add(Widgets.createSlot(new Point(startPoint.x + 108, startPoint.y + 34))
                .entries(display.getOutputEntries().get(0)).markOutput());

        if (display.getOutputEntries().get(1) != EntryIngredient.of(EntryStacks.of(ItemStack.EMPTY))) {
            widgets.add(Widgets.createSlot(new Point(startPoint.x + 128, startPoint.y + 34))
                    .entries(display.getOutputEntries().get(1)).markOutput());
        }

        Text text = Text.translatable("tooltip.productiveslimes.energy_usage", display.recipe().value().energy());

        widgets.add(Widgets.createTooltip(new Rectangle(startPoint.x + 8, startPoint.y + 12, 10, 58), text));

        widgets.add(new Widget() {
            @Override
            public void render(DrawContext guiGraphics, int mouseX, int mouseY, float partialTick) {
                MinecraftClient.getInstance().getTextureManager().getTexture(TEXTURE);

                // Arrow
                tickCount++;
                int arrowWidth = (tickCount % 600) * 26 / 600;

                guiGraphics.drawTexture(RenderLayer::getGuiTextured, TEXTURE, startPoint.x + 70, startPoint.y + 38, 153, 0, arrowWidth, 8, 256, 256);

                // Energy bar
                int energyScaled = (int) Math.ceil((double) display.recipe().value().energy() / 10000 * 57);
                energyScaled = arrowWidth >= 25 ? 0 : energyScaled;

                guiGraphics.drawTexture(RenderLayer::getGuiTextured, TEXTURE, startPoint.x + 9, (startPoint.y + 18) + (52 - energyScaled), 153, 65 - energyScaled, 9, energyScaled, 256, 256);

                Text outputChance = Text.translatable("gui.productiveslimes.output_chance", String.format("%.1f", display.recipe().value().outputChance() * 100) + "%");

                guiGraphics.drawText(MinecraftClient.getInstance().textRenderer, outputChance, startPoint.x + 7, startPoint.y + 72, 0xFFFFFF, false);
            }

            @Override
            public List<? extends Element> children() {
                return new ArrayList<>();
            }
        });

        return widgets;
    }

    @Override
    public int getDisplayHeight() {
        return 83;
    }
}
