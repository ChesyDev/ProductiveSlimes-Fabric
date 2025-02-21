
package com.chesy.productiveslimes.config;

import com.chesy.productiveslimes.ProductiveSlimes;
import com.electronwill.nightconfig.core.file.FileConfig;

public class ModConfig {
    public static void config(){
        ProductiveSlimes.config = FileConfig.builder(ProductiveSlimes.CONFIG_PATH.toFile())
                .autosave()
                .build();

        if (!ProductiveSlimes.config.getFile().exists()) {
            ProductiveSlimes.config.save();
        }

        ProductiveSlimes.config.load();

        if (!ProductiveSlimes.config.contains("slime_settings.vanilla_slime_can_attack_player")) {
            ProductiveSlimes.config.add("slime_settings.vanilla_slime_can_attack_player", false);
        }

        if (!ProductiveSlimes.config.contains("iron_golem_settings.iron_golem_can_attack_slime")) {
            ProductiveSlimes.config.add("iron_golem_settings.iron_golem_can_attack_slime", false);
        }

        ProductiveSlimes.config.save();

        ProductiveSlimes.vanillaSlimeCanAttackPlayer = ProductiveSlimes.config.get("slime_settings.vanilla_slime_can_attack_player");
        ProductiveSlimes.ironGolemCanAttackSlime = ProductiveSlimes.config.get("iron_golem_settings.iron_golem_can_attack_slime");
    }
}