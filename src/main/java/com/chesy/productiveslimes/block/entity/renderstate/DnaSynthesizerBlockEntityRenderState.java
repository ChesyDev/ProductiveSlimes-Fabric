package com.chesy.productiveslimes.block.entity.renderstate;

import com.chesy.productiveslimes.block.entity.DnaSynthesizerBlockEntity;
import net.minecraft.client.render.block.entity.state.BlockEntityRenderState;
import net.minecraft.client.render.item.ItemRenderState;

import java.util.ArrayList;
import java.util.List;

public class DnaSynthesizerBlockEntityRenderState extends BlockEntityRenderState {
    public DnaSynthesizerBlockEntity blockEntity;
    public List<ItemRenderState> itemStackRenderStates = new ArrayList<>();
}
