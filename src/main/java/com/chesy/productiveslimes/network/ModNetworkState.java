package com.chesy.productiveslimes.network;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.world.PersistentState;

import java.util.HashMap;
import java.util.Map;

public class ModNetworkState extends PersistentState {
    private final Map<Integer, CableNetwork> networks = new HashMap<>();
    private int nextId = 1;

    public ModNetworkState() {
        super();
    }

    private ModNetworkState(Map<Integer, CableNetwork> networks, int nextId) {
        networks.clear();
        this.networks.putAll(networks);
        this.nextId = nextId;
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

    @Override
    public NbtCompound writeNbt(NbtCompound nbt) {
        NbtList list = new NbtList();
        for (Map.Entry<Integer, CableNetwork> entry : networks.entrySet()) {
            int netId = entry.getKey();
            CableNetwork net = entry.getValue();

            NbtCompound netTag = new NbtCompound();
            netTag.putInt("NetId", netId);
            netTag.put("CableNetwork", CableNetwork.writeToNbt(net, new NbtCompound()));
            list.add(netTag);
        }
        nbt.put("Networks", list);

        nbt.putInt("NextId", this.nextId);
        return nbt;
    }

    protected static ModNetworkState readNbt(NbtCompound nbt) {
        Map<Integer, CableNetwork> networks = new HashMap<>();
        int nextId = -1;

        if (nbt.contains("Networks", NbtElement.LIST_TYPE)) {
            NbtList list = nbt.getList("Networks", NbtElement.COMPOUND_TYPE);
            for (int i = 0; i < list.size(); i++) {
                NbtCompound netTag = list.getCompound(i);
                int netId = netTag.getInt("NetId");
                CableNetwork net = CableNetwork.readFromNbt(netTag.getCompound("CableNetwork"));
                // Make sure the CableNetwork’s own ID is set:
                net.setNetworkId(netId);
                networks.put(netId, net);
                if (netId >= nextId) {
                    nextId = netId + 1;
                }
            }
        }
        nextId = Math.max(nextId, nbt.getInt("NextId"));

        return new ModNetworkState(networks, nextId);
    }
}
