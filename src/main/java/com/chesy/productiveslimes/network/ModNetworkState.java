package com.chesy.productiveslimes.network;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.datafixer.DataFixTypes;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.world.PersistentState;
import net.minecraft.world.PersistentStateType;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class ModNetworkState extends PersistentState {
    private final Map<Integer, CableNetwork> networks = new HashMap<>();
    private int nextId = 1;

    public static final PersistentStateType<ModNetworkState> MY_TYPE =
            new PersistentStateType<>(
                    "productiveslimes_cable_networks",
                    ModNetworkState::new,
                    ctx -> RecordCodecBuilder.create(instance -> instance.group(
                                    Codec.list(
                                            RecordCodecBuilder.<Map.Entry<Integer, CableNetwork>>create(entryInstance ->
                                                    entryInstance.group(
                                                            Codec.INT.fieldOf("NetId").forGetter(Map.Entry::getKey),
                                                            CableNetwork.CODEC.fieldOf("CableNetwork").forGetter(Map.Entry::getValue)
                                                    ).apply(entryInstance, AbstractMap.SimpleEntry::new)
                                            )
                                    ).xmap(
                                            entries -> entries.stream().collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue)),
                                            map -> new ArrayList<>(map.entrySet())
                                    ).fieldOf("Networks").forGetter(ModNetworkState::getAllNetworks),
                                    Codec.INT.fieldOf("NextId").forGetter(ModNetworkState::getNextId)
                            ).apply(instance, ModNetworkState::new)
                    ),
                    DataFixTypes.LEVEL
            );

    public ModNetworkState() {
        super();
    }

    public ModNetworkState(Map<Integer, CableNetwork> networks, int nextId) {
        this.networks.putAll(networks);
        this.nextId = nextId;
    }

    public ModNetworkState(Context context) {
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
