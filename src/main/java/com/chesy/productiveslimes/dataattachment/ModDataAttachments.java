package com.chesy.productiveslimes.dataattachment;

import com.chesy.productiveslimes.ProductiveSlimes;
import com.mojang.serialization.Codec;
import net.fabricmc.fabric.api.attachment.v1.AttachmentRegistry;
import net.fabricmc.fabric.api.attachment.v1.AttachmentType;
import net.minecraft.util.Identifier;

public class ModDataAttachments {
    public static final AttachmentType<Boolean> IS_FIRST_TIME_LOGIN = AttachmentRegistry.create(Identifier.of(ProductiveSlimes.MODID, "is_first_time_login"), booleanBuilder -> booleanBuilder.initializer(() -> true).persistent(Codec.BOOL).copyOnDeath());
    public static void register(){
    }
}
