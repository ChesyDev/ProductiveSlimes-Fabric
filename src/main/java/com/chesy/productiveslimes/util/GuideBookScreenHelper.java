package com.chesy.productiveslimes.util;

import com.chesy.productiveslimes.ProductiveSlimes;
import com.chesy.productiveslimes.entity.BaseSlime;
import com.chesy.productiveslimes.tier.ModTier;
import com.chesy.productiveslimes.tier.ModTiers;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.entity.EntityType;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;

public class GuideBookScreenHelper {
    public static int scrollOffset(int contentScrollOffset, double scroll, int maxContentScroll, int scrollSpeed) {
        contentScrollOffset -= scroll * scrollSpeed;
        if (contentScrollOffset < 0) contentScrollOffset = 0;
        if (contentScrollOffset > maxContentScroll) contentScrollOffset = maxContentScroll;
        return contentScrollOffset;
    }

    public static void renderItemSlot(DrawContext pGuiGraphics, int pMouseX, int pMouseY, int inputX, int inputY, ItemStack inputStack, TextRenderer font) {
        pGuiGraphics.drawItem(inputStack, inputX, inputY);
        pGuiGraphics.drawStackOverlay(font, inputStack, inputX, inputY);
        if (pMouseX >= inputX && pMouseX < inputX + 16 && pMouseY >= inputY && pMouseY < inputY + 16) {
            pGuiGraphics.drawItemTooltip(font, inputStack, pMouseX, pMouseY);
            pGuiGraphics.fill(RenderLayer.getGui(), inputX, inputY, inputX + 16, inputY + 16, 0x80FFFFFF);
        }
    }

    public static SlimeData generateSlimeData(ModTier tiers) {
        return new SlimeData(
                1,
                tiers.color(),
                tiers.cooldown(),
                Registries.ITEM.get(Identifier.of(ProductiveSlimes.MODID, tiers.name() + "_slimeball")).getDefaultStack(),
                ModTiers.getItemByKey(tiers.growthItemKey()).asItem().getDefaultStack(),
                (EntityType<BaseSlime>) Registries.ENTITY_TYPE.get(Identifier.of(ProductiveSlimes.MODID, tiers.name() + "_slime"))
        );
    }
}