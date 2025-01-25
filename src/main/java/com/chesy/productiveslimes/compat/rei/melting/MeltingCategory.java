package com.chesy.productiveslimes.compat.rei.melting;

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
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.LinkedList;
import java.util.List;

public class MeltingCategory implements DisplayCategory<MeltingRecipeDisplay> {
    public static final CategoryIdentifier<? extends MeltingRecipeDisplay> MELTING = CategoryIdentifier.of(ProductiveSlimes.MODID, "melting");
    public static final Identifier TEXTURE = Identifier.of(ProductiveSlimes.MODID, "textures/gui/rei/melting_station_gui.png");

    private int tickCount = 0;

    @Override
    public CategoryIdentifier<? extends MeltingRecipeDisplay> getCategoryIdentifier() {
        return MELTING;
    }

    @Override
    public Text getTitle() {
        return Text.translatable("block.productiveslimes.melting_station");
    }

    @Override
    public Renderer getIcon() {
        return EntryStacks.of(ModBlocks.MELTING_STATION);
    }

    @Override
    public List<Widget> setupDisplay(MeltingRecipeDisplay display, Rectangle bounds) {
        Point startPoint = new Point(bounds.getCenterX() - 77, bounds.getCenterY() - 41);
        List<Widget> widgets = new LinkedList<>();

        widgets.add(Widgets.createTexturedWidget(TEXTURE, new Rectangle(startPoint.x, startPoint.y, 153, 83)));

        widgets.add(Widgets.createSlot(new Point(startPoint.x + 25, startPoint.y + 34))
                .entries(display.getInputEntries().get(1)).markInput());

        widgets.add(Widgets.createSlot(new Point(startPoint.x + 45, startPoint.y + 34))
                .entries(display.getInputEntries().get(0)).markInput());

        widgets.add(Widgets.createSlot(new Point(startPoint.x + 128, startPoint.y + 34))
                .entries(display.getOutputEntries().getFirst()).markOutput());

        Text text = Text.translatable("tooltip.productiveslimes.energy_usage", display.getEnergy());

        widgets.add(Widgets.createTooltip(new Rectangle(startPoint.x + 8, startPoint.y + 12, 10, 58), text));

        widgets.add(new Widget() {
            @Override
            public List<? extends Element> children() {
                return new LinkedList<>();
            }

            @Override
            public void render(DrawContext guiGraphics, int mouseX, int mouseY, float partialTick) {
                MinecraftClient.getInstance().getTextureManager().getTexture(TEXTURE);

                // Arrow
                tickCount++;
                int arrowWidth = (tickCount % 600) * 26 / 600;

                guiGraphics.drawTexture(RenderLayer::getGuiTextured, TEXTURE, startPoint.x + 77, startPoint.y + 38, 153, 0, arrowWidth, 8, 256, 256);

                // Energy bar
                int energyScaled = (int) Math.ceil((double) display.getEnergy() / 10000 * 57);
                energyScaled = arrowWidth >= 25 ? 0 : energyScaled;

                guiGraphics.drawTexture(RenderLayer::getGuiTextured, TEXTURE, startPoint.x + 9, (startPoint.y + 18) + (52 - energyScaled), 153, 65 - energyScaled, 9, energyScaled, 256, 256);
            }

        });

        return widgets;
    }

    @Override
    public int getDisplayHeight() {
        return 83;
    }
}
