package com.chesy.productiveslimes.util;

import team.reborn.energy.api.EnergyStorage;

public interface IEnergyBlockEntity {
    <T extends EnergyStorage> T getEnergyHandler();
}
