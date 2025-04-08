package com.chesy.productiveslimes.compat.jade;

import com.chesy.productiveslimes.ProductiveSlimes;
import com.chesy.productiveslimes.entity.BaseSlime;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import snownee.jade.api.*;
import snownee.jade.api.config.IPluginConfig;

@WailaPlugin
public class JadePlugin implements IWailaPlugin {
    @Override
    public void registerClient(IWailaClientRegistration registration) {
        registration.registerEntityComponent(EntityInfoProvider.INSTANCE, BaseSlime.class);
    }

    public enum EntityInfoProvider implements IEntityComponentProvider {
        INSTANCE;

        @Override
        public void appendTooltip(ITooltip iTooltip, EntityAccessor entityAccessor, IPluginConfig iPluginConfig) {
            if (entityAccessor.getEntity() instanceof BaseSlime slime) {
                int nextDrop = slime.getNextDropTime();
                iTooltip.add(Text.translatable("tooltip.productiveslimes.next_drop" , (int) Math.ceil((double) nextDrop / 20) + "s"));
            }
        }

        @Override
        public Identifier getUid() {
            return Identifier.of(ProductiveSlimes.MODID, "slime_info");
        }
    }
}
