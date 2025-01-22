package com.chesy.productiveslimes.config;

public record CustomVariant(String name, String color, int mapColorId, int cooldown, String growthItem, String solidingOutput, int solidingOutputCount, String synthesizingInputDna1, String synthesizingInputDna2, String synthesizingInputItem, double dnaOutputChance) {
    public int getColor(){
        return hexToInt(color);
    }

    public int hexToInt(String hexColor){
        if (hexColor.startsWith("#")){
            hexColor = hexColor.substring(1);
        }

        return ((int) Long.parseLong(hexColor, 16));
    }
}
