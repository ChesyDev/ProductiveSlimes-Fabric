package com.chesy.productiveslimes.util;

import com.chesy.productiveslimes.ProductiveSlimes;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;

public class ModTags {
    public static class Blocks {
        private static TagKey<Block> createTag(String id) {
            return TagKey.of(RegistryKeys.BLOCK, Identifier.of(ProductiveSlimes.MODID, id));
        }
    }

    public static class Items {
        public static final TagKey<Item> TRANSFORMABLE_ITEMS = createTag("transformable_items");
        public static final TagKey<Item> DNA_ITEM = createTag("dna_item");
        public static final TagKey<Item> SLIMY_LOG = createTag("slimy_log");

        private static TagKey<Item> createTag(String id) {
            return TagKey.of(RegistryKeys.ITEM, Identifier.of(ProductiveSlimes.MODID, id));
        }
    }
}
