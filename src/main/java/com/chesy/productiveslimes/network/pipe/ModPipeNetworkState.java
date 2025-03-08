package com.chesy.productiveslimes.network.pipe;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.datafixer.DataFixTypes;
import net.minecraft.world.PersistentState;
import net.minecraft.world.PersistentStateType;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class ModPipeNetworkState extends PersistentState {
    private final Map<Integer, PipeNetwork> networks = new HashMap<>();
    private int nextId = 1;

    public static final PersistentStateType<ModPipeNetworkState> MY_TYPE =
            new PersistentStateType<>(
                    "productiveslimes_pipe_networks",
                    ModPipeNetworkState::new,
                    ctx -> RecordCodecBuilder.create(instance -> instance.group(
                                    Codec.list(
                                            RecordCodecBuilder.<Map.Entry<Integer, PipeNetwork>>create(entryInstance ->
                                                    entryInstance.group(
                                                            Codec.INT.fieldOf("NetId").forGetter(Map.Entry::getKey),
                                                            PipeNetwork.CODEC.fieldOf("PipeNetwork").forGetter(Map.Entry::getValue)
                                                    ).apply(entryInstance, AbstractMap.SimpleEntry::new)
                                            )
                                    ).xmap(
                                            entries -> entries.stream().collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue)),
                                            map -> new ArrayList<>(map.entrySet())
                                    ).fieldOf("Networks").forGetter(ModPipeNetworkState::getAllNetworks),
                                    Codec.INT.fieldOf("NextId").forGetter(ModPipeNetworkState::getNextId)
                            ).apply(instance, ModPipeNetworkState::new)
                    ),
                    DataFixTypes.LEVEL
            );

    public ModPipeNetworkState() {
        super();
    }

    private ModPipeNetworkState(Map<Integer, PipeNetwork> networks, int nextId) {
        this.networks.putAll(networks);
        this.nextId = nextId;
    }

    public ModPipeNetworkState(Context context) {
    }

    public int getNextId() {
        return nextId;
    }

    public PipeNetwork getNetwork(int netId) {
        return networks.get(netId);
    }

    public int createNetwork() {
        int id = nextId++;
        PipeNetwork net = new PipeNetwork();
        net.setNetworkId(id);
        networks.put(id, net);
        this.setDirty(true);
        return id;
    }

    public void removeNetwork(int netId) {
        networks.remove(netId);
        this.setDirty(true);
    }

    public Map<Integer, PipeNetwork> getAllNetworks() {
        return networks;
    }
}