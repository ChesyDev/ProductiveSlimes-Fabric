package com.chesy.productiveslimes.network;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.datafixer.DataFixTypes;
import net.minecraft.world.PersistentState;
import net.minecraft.world.PersistentStateType;

import java.util.HashMap;
import java.util.Map;

public class ModNetworkState extends PersistentState {
    private final Map<Integer, CableNetwork> networks = new HashMap<>();
    private int nextId = 1;

    public static final Codec<ModNetworkState> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.unboundedMap(Codec.INT, CableNetwork.CODEC).fieldOf("Networks").forGetter(modNetworkState -> modNetworkState.networks),
            Codec.INT.fieldOf("NextId").forGetter(ModNetworkState::getNextId)
    ).apply(instance, ModNetworkState::new));

    public static final PersistentStateType<ModNetworkState> MY_TYPE =
            new PersistentStateType<>(
                    "productiveslimes_cable_networks",
                    ModNetworkState::new,
                    CODEC,
                    DataFixTypes.LEVEL
            );

    public ModNetworkState() {
        super();
    }

    public ModNetworkState(Map<Integer, CableNetwork> networks, int nextId) {
        this.networks.putAll(networks);
        this.nextId = nextId;
    }

    public int getNextId() {
        return nextId;
    }

    public CableNetwork getNetwork(int netId) {
        return networks.get(netId);
    }

    public int createNetwork() {
        int id = nextId++;
        CableNetwork net = new CableNetwork();
        net.setNetworkId(id);
        networks.put(id, net);
        this.setDirty(true);
        return id;
    }

    public void removeNetwork(int netId) {
        networks.remove(netId);
        this.setDirty(true);
    }

    public Map<Integer, CableNetwork> getAllNetworks() {
        return networks;
    }
}
