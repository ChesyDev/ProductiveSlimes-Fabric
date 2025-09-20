package com.chesy.productiveslimes.block.entity.renderstate;

import com.chesy.productiveslimes.block.entity.DnaExtractorBlockEntity;
import net.minecraft.client.render.block.entity.state.BlockEntityRenderState;
import net.minecraft.client.render.item.ItemRenderState;

public class DnaExtractorBlockEntityRenderState extends BlockEntityRenderState {
    public float rotation;
    public DnaExtractorBlockEntity blockEntity;
    public ItemRenderState itemStackRenderState;

    public float getRenderingRotation() {
        rotation += 1f;
        if(rotation >= 360) {
            rotation = 0;
        }
        return rotation;
    }
}
