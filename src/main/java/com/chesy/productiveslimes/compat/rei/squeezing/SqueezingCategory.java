package com.chesy.productiveslimes.compat.rei.squeezing;

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
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Element;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class SqueezingCategory implements DisplayCategory<SqueezingRecipeDisplay> {
    public static final CategoryIdentifier<? extends SqueezingRecipeDisplay> SQUEEZING = CategoryIdentifier.of(ProductiveSlimes.MODID, "squeezing");
    public static final Identifier TEXTURE = Identifier.of(ProductiveSlimes.MODID,"textures/gui/rei/slime_squeezer_gui.png");
    private int tickCount = 0;
    @Override
    public CategoryIdentifier<? extends SqueezingRecipeDisplay> getCategoryIdentifier() {
        return SQUEEZING;
    }
    @Override
    public Text getTitle() {
        return Text.translatable("block.productiveslimes.slime_squeezer");
    }
    @Override
    public Renderer getIcon() {
        return EntryStacks.of(ModBlocks.SLIME_SQUEEZER);
    }
    @Override
    public List<Widget> setupDisplay(SqueezingRecipeDisplay display, Rectangle bounds) {
        Point startPoint = new Point(bounds.getCenterX() - 77, bounds.getCenterY() - 41);
        List<Widget> widgets = new LinkedList<>();
        widgets.add(Widgets.createTexturedWidget(TEXTURE, new Rectangle(startPoint.x, startPoint.y, 153, 83)));
        widgets.add(Widgets.createSlot(new Point(startPoint.x + 26, startPoint.y + 34))
                .entries(Collections.singleton(display.getInputItem())).markInput());
        widgets.add(Widgets.createSlot(new Point(startPoint.x + 107, startPoint.y + 34))
                .entries(display.getOutputEntries().get(0)).markOutput());
        widgets.add(Widgets.createSlot(new Point(startPoint.x + 127, startPoint.y + 34))
                .entries(display.getOutputEntries().get(1)).markOutput());
        Text text = Text.translatable("tooltip.productiveslimes.energy_usage", display.getEnergy());
        widgets.add(Widgets.createTooltip(new Rectangle(startPoint.x + 8, startPoint.y + 12, 10, 58), text));
        widgets.add(new Widget() {
            @Override
            public void render(DrawContext guiGraphics, int mouseX, int mouseY, float partialTick) {
                // Arrow
                tickCount++;
                int arrowWidth = (tickCount % 600) * 26 / 600;
                guiGraphics.drawTexture(TEXTURE, startPoint.x + 69, startPoint.y + 38, 153, 0, arrowWidth, 8, 256, 256);
                // Energy bar
                int energyScaled = (int) Math.ceil((double) display.getEnergy() / 10000 * 57);
                energyScaled = arrowWidth >= 25 ? 0 : energyScaled;
                guiGraphics.drawTexture(TEXTURE, startPoint.x + 9, (startPoint.y + 18) + (52 - energyScaled), 153, 65 - energyScaled, 9, energyScaled, 256, 256);
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