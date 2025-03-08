package com.chesy.productiveslimes.screen.renderer;

import com.chesy.productiveslimes.fluid.FluidStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.fabric.api.transfer.v1.client.fluid.FluidVariantRendering;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.texture.SpriteAtlasTexture;
import org.joml.Matrix4f;

public class FluidTankRenderer {
    public static void renderFluidStack(DrawContext guiGraphics, FluidStack fluidStack, long tankCapacity, int w, int h, int x, int y) {
        if(fluidStack.isEmpty())
            return;

        Sprite sprite = FluidVariantRendering.getSprite(fluidStack.getFluid());
        RenderLayer renderLayer = RenderLayer.getEntityTranslucent(sprite.getAtlasId());

        int fluidColorTint = FluidVariantRendering.getColor(fluidStack.getFluid()) | 0xFF000000;

        long fluidMeterPos = tankCapacity == -1 || (fluidStack.getAmount() > 0 && fluidStack.getAmount() == tankCapacity) ? 0:(h - ((fluidStack.getAmount() <= 0 || tankCapacity == 0)?0: (Math.min(fluidStack.getAmount(), tankCapacity - 1) * h / tankCapacity + 1)));

        RenderSystem.setShaderTexture(0, SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE);

        Matrix4f mat = guiGraphics.getMatrices().peek().getPositionMatrix();

        for (int yOffset = h; yOffset > fluidMeterPos; yOffset -= 16) {
            for (int xOffset = 0; xOffset < w; xOffset += 16) {
                int finalXOffset = x + xOffset;
                int finalYOffset = y + yOffset;

                int finalXOffset1 = xOffset;
                guiGraphics.draw(vertexConsumers -> {
                    int width = Math.min(w - finalXOffset1, 16);
                    long height = Math.min(finalYOffset - (y + fluidMeterPos), 16);

                    float u0 = sprite.getMinU();
                    float u1 = sprite.getMaxU();
                    float v0 = sprite.getMinV();
                    float v1 = sprite.getMaxV();
                    u1 = u1 - ((16 - width) / 16.f * (u1 - u0));
                    v0 = v0 - ((16 - height) / 16.f * (v0 - v1));

                    VertexConsumer bufferBuilder = vertexConsumers.getBuffer(renderLayer);
                    bufferBuilder.vertex(mat, finalXOffset, finalYOffset, 0).color(fluidColorTint).texture(u0, v1).light(15728880).overlay(OverlayTexture.DEFAULT_UV).normal(0, 0, 1);
                    bufferBuilder.vertex(mat, finalXOffset + width, finalYOffset, 0).color(fluidColorTint).texture(u1, v1).light(15728880).overlay(OverlayTexture.DEFAULT_UV).normal(0, 0, 1);
                    bufferBuilder.vertex(mat, finalXOffset + width, finalYOffset - height, 0).color(fluidColorTint).texture(u1, v0).light(15728880).overlay(OverlayTexture.DEFAULT_UV).normal(0, 0, 1);
                    bufferBuilder.vertex(mat, finalXOffset, finalYOffset - height, 0).color(fluidColorTint).texture(u0, v0).light(15728880).overlay(OverlayTexture.DEFAULT_UV).normal(0, 0, 1);
                });
            }
        }
    }
}
