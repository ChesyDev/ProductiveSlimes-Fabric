package com.chesy.productiveslimes.util;

import com.chesy.productiveslimes.datacomponent.ModDataComponents;
import com.chesy.productiveslimes.entity.model.BaseSlimeModel;
import com.chesy.productiveslimes.entity.renderer.BaseSlimeRenderer;
import com.mojang.serialization.MapCodec;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.RenderLayers;
import net.minecraft.client.render.command.OrderedRenderCommandQueue;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.LoadedEntityModels;
import net.minecraft.client.render.entity.state.SlimeEntityRenderState;
import net.minecraft.client.render.item.model.special.SpecialModelRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemDisplayContext;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.RotationAxis;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;
import org.joml.Vector3fc;

import java.util.function.Consumer;

public record SlimeItemSpecialRenderer() implements SpecialModelRenderer<SlimeData> {
    @Override
    public void render(@Nullable SlimeData slimeData, ItemDisplayContext displayContext, MatrixStack matrices, OrderedRenderCommandQueue nodeCollector, int light, int overlay, boolean glint, int i) {
        LoadedEntityModels entityModelSet = MinecraftClient.getInstance().getLoadedEntityModels();
        BaseSlimeModel slimeModel = new BaseSlimeModel(entityModelSet.getModelPart(EntityModelLayers.SLIME), -1);
        BaseSlimeModel slimeModelOuter = new BaseSlimeModel(entityModelSet.getModelPart(EntityModelLayers.SLIME_OUTER), -1);

        if (slimeData != null){
            matrices.push();
            matrices.scale(2, 2, 2);
            matrices.translate(0.25f, 1.5f, 0.25f);
            matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(180.0F));
            matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(180.0F));
            nodeCollector.getBatchingQueue(0).submitModel(slimeModel, new SlimeEntityRenderState(), matrices, RenderLayers.entityTranslucent(BaseSlimeRenderer.TEXTURE), light, overlay, slimeData.color(), null, 0, null);
            nodeCollector.getBatchingQueue(1).submitModel(slimeModelOuter, new SlimeEntityRenderState(), matrices, RenderLayers.entityTranslucent(BaseSlimeRenderer.TEXTURE), light, overlay, slimeData.color(), null, 0, null);
            matrices.pop();
        }
    }

    @Override
    public void collectVertices(Consumer<Vector3fc> consumer) {
        consumer.accept(new Vector3f(0.0f, 0.0f, 0.0f));
        consumer.accept(new Vector3f(1.0f, 1.0f, 1.0f));
    }

    @Nullable
    @Override
    public SlimeData getData(ItemStack stack) {
        return stack.get(ModDataComponents.SLIME_DATA);
    }

    public record Unbaked(Identifier texture) implements SpecialModelRenderer.Unbaked{
        public static final MapCodec<Unbaked> MAP_CODEC = Identifier.CODEC.fieldOf("texture").xmap(SlimeItemSpecialRenderer.Unbaked::new, SlimeItemSpecialRenderer.Unbaked::texture);

        @Override
        public @Nullable SpecialModelRenderer<?> bake(BakeContext context) {
            return new SlimeItemSpecialRenderer();
        }

        @Override
        public MapCodec<? extends SpecialModelRenderer.Unbaked> getCodec() {
            return MAP_CODEC;
        }
    }
}