package com.chesy.productiveslimes.compat.rei;

import com.chesy.productiveslimes.screen.renderer.FluidTankRenderer;
import com.google.common.collect.Lists;
import dev.architectury.fluid.FluidStack;
import dev.architectury.hooks.fluid.FluidStackHooks;
import dev.architectury.platform.Platform;
import me.shedaniel.math.Rectangle;
import me.shedaniel.rei.api.client.entry.renderer.BatchedEntryRenderer;
import me.shedaniel.rei.api.client.gui.widgets.Tooltip;
import me.shedaniel.rei.api.client.gui.widgets.TooltipContext;
import me.shedaniel.rei.api.common.entry.EntryStack;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.texture.Sprite;
import net.minecraft.registry.Registries;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class FluidEntryRenderer implements BatchedEntryRenderer<FluidStack, Sprite> {
    private static final String FLUID_AMOUNT = !Platform.isForgeLike() ? "tooltip.rei.fluid_amount.forge" : "tooltip.rei.fluid_amount";

    @Override
    public Sprite getExtraData(EntryStack<FluidStack> entryStack) {
        FluidStack stack = entryStack.getValue();
        if (stack.isEmpty()) return null;
        return FluidStackHooks.getStillTexture(stack);
    }

    @Override
    public int getBatchIdentifier(EntryStack<FluidStack> entryStack, Rectangle rectangle, Sprite spriteAtlasTexture) {
        return 0;
    }

    @Override
    public void startBatch(EntryStack<FluidStack> entryStack, Sprite spriteAtlasTexture, DrawContext drawContext, float v) {

    }

    @Override
    public void renderBase(EntryStack<FluidStack> entryStack, Sprite spriteAtlasTexture, DrawContext drawContext, VertexConsumerProvider.Immediate immediate, Rectangle rectangle, int i, int i1, float v) {
        FluidTankRenderer.renderFluidStack(drawContext, new com.chesy.productiveslimes.util.FluidStack(entryStack.getValue().getFluid(), ((int) entryStack.getValue().getAmount())), (int) entryStack.getValue().getAmount(), rectangle.width, rectangle.height, rectangle.x, rectangle.y);

        if (rectangle.contains(i, i1)) {
            drawContext.fill(rectangle.x, rectangle.y, rectangle.x + rectangle.width, rectangle.y + rectangle.height, 0x80FFFFFF);
        }
    }

    @Override
    public void afterBase(EntryStack<FluidStack> entryStack, Sprite spriteAtlasTexture, DrawContext drawContext, float v) {

    }

    @Override
    public void renderOverlay(EntryStack<FluidStack> entryStack, Sprite spriteAtlasTexture, DrawContext drawContext, VertexConsumerProvider.Immediate immediate, Rectangle rectangle, int i, int i1, float v) {

    }

    @Override
    public void endBatch(EntryStack<FluidStack> entryStack, Sprite spriteAtlasTexture, DrawContext drawContext, float v) {

    }

    @Override
    public @Nullable Tooltip getTooltip(EntryStack<FluidStack> entryStack, TooltipContext tooltipContext) {
        if (entryStack.isEmpty())
            return null;
        List<Text> toolTip = Lists.newArrayList(entryStack.asFormattedText(tooltipContext));
        long amount = entryStack.getValue().getAmount();
        if (amount >= 0 && entryStack.get(EntryStack.Settings.FLUID_AMOUNT_VISIBLE)) {
            String amountTooltip = I18n.translate(FLUID_AMOUNT, entryStack.getValue().getAmount());
            if (amountTooltip != null) {
                toolTip.addAll(Stream.of(amountTooltip.split("\n")).map(Text::literal).collect(Collectors.toList()));
            }
        }
        if (MinecraftClient.getInstance().options.advancedItemTooltips) {
            Identifier fluidId = Registries.FLUID.getId(entryStack.getValue().getFluid());
            toolTip.add((Text.literal(fluidId.toString())).formatted(Formatting.DARK_GRAY));
        }
        return Tooltip.create(toolTip);
    }
}
