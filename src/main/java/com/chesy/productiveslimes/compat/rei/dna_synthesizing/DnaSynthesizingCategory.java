/*
package com.chesy.productiveslimes.compat.rei.dna_synthesizing;

import com.chesy.productiveslimes.ProductiveSlimes;
import com.chesy.productiveslimes.block.ModBlocks;
import me.shedaniel.math.Point;
import me.shedaniel.math.Rectangle;
import me.shedaniel.rei.api.client.gui.Renderer;
import me.shedaniel.rei.api.client.gui.widgets.Widget;
import me.shedaniel.rei.api.client.gui.widgets.Widgets;
import me.shedaniel.rei.api.client.registry.display.DisplayCategory;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.util.EntryStacks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Element;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.item.Items;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class DnaSynthesizingCategory implements DisplayCategory<DnaSynthesizingRecipeDisplay> {
    public static final CategoryIdentifier<? extends DnaSynthesizingRecipeDisplay> DNA_SYNTHESIZING = CategoryIdentifier.of(ProductiveSlimes.MODID, "dna_synthesizing");
    public static final Identifier TEXTURE = Identifier.of(ProductiveSlimes.MODID,"textures/gui/rei/dna_synthesizer_gui.png");

    private int tickCount = 0;

    @Override
    public CategoryIdentifier<? extends DnaSynthesizingRecipeDisplay> getCategoryIdentifier() {
        return DNA_SYNTHESIZING;
    }

    @Override
    public Text getTitle() {
        return Text.translatable("block.productiveslimes.dna_synthesizer");
    }

    @Override
    public Renderer getIcon() {
        return EntryStacks.of(ModBlocks.DNA_SYNTHESIZER);
    }

    @Override
    public List<Widget> setupDisplay(DnaSynthesizingRecipeDisplay display, Rectangle bounds) {
        Point startPoint = new Point(bounds.getCenterX() - 77, bounds.getCenterY() - 41);
        List<Widget> widgets = new LinkedList<>();

        widgets.add(Widgets.createTexturedWidget(TEXTURE, new Rectangle(startPoint.x, startPoint.y, 153, 83)));

        widgets.add(Widgets.createSlot(new Point(startPoint.x + 31, startPoint.y + 12))
                .entries(display.getInputEntries().get(0)).markInput());

        widgets.add(Widgets.createSlot(new Point(startPoint.x + 31, startPoint.y + 55))
                .entries(display.getInputEntries().get(1)).markInput());

        widgets.add(Widgets.createSlot(new Point(startPoint.x + 52, startPoint.y + 34))
                .entries(display.getInputEntries().get(2)).markInput());

        widgets.add(Widgets.createSlot(new Point(startPoint.x + 82, startPoint.y + 55))
                .entries(Collections.singleton(EntryStacks.of(Items.EGG))).markInput());

        widgets.add(Widgets.createSlot(new Point(startPoint.x + 125, startPoint.y + 34))
                .entries(display.getOutputEntries().getFirst()).markOutput());

        Text text = Text.translatable("tooltip.productiveslimes.energy_usage", display.getEnergy());

        widgets.add(Widgets.createTooltip(new Rectangle(startPoint.x + 8, startPoint.y + 12, 10, 58), text));

        widgets.add(new Widget() {
            @Override
            public void render(DrawContext guiGraphics, int mouseX, int mouseY, float partialTick) {
                MinecraftClient.getInstance().getTextureManager().getTexture(TEXTURE);

                // Arrow
                tickCount++;
                int arrowWidth = (tickCount % 600) * 26 / 600;
                int dnaHeight = (tickCount % 600) * 23 / 600;

                guiGraphics.drawTexture(RenderLayer::getGuiTextured, TEXTURE, startPoint.x + 77, startPoint.y + 38, 153, 0, arrowWidth, 8, 256,256);

                // Energy bar
                int energyScaled = (int) Math.ceil((double) display.getEnergy() / 10000 * 57);
                energyScaled = arrowWidth >= 25 ? 0 : energyScaled;

                guiGraphics.drawTexture(RenderLayer::getGuiTextured, TEXTURE, startPoint.x + 9, (startPoint.y + 18) + (52 - energyScaled), 153, 65 - energyScaled, 9, energyScaled, 256,256);
                guiGraphics.drawTexture(RenderLayer::getGuiTextured, TEXTURE, startPoint.x + 36, startPoint.y + 30, 153, 66, 6, dnaHeight, 256,256);
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
}*/
