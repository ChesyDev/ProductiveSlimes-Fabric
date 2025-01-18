package com.chesy.productiveslimes.network;

import net.minecraft.datafixer.DataFixTypes;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.world.PersistentState;

import java.util.HashMap;
import java.util.Map;

public class ModNetworkState extends PersistentState {
    private final Map<Integer, CableNetwork> networks = new HashMap<>();

    private int nextId = 1;

    public static final PersistentState.Type<ModNetworkState> MY_TYPE =
            new PersistentState.Type<>(
                    ModNetworkState::new,
                    (nbt, registry) -> {
                        ModNetworkState state = new ModNetworkState();
                        state.readNbt(nbt, registry);
                        return state;
                    },
                    DataFixTypes.LEVEL
            );

    public ModNetworkState() {
        super();
    }

    public CableNetwork getNetwork(int netId) {
        return networks.get(netId);
    }

    public int createNetwork() {
        int id = nextId++;
        CableNetwork net = new CableNetwork();
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
    public NbtCompound writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registry) {
        NbtList list = new NbtList();
        for (Map.Entry<Integer, CableNetwork> entry : networks.entrySet()) {
            int netId = entry.getKey();
            CableNetwork net = entry.getValue();

            NbtCompound netTag = new NbtCompound();
            netTag.putInt("NetId", netId);
            CableNetwork.writeToNbt(net, netTag);
            list.add(netTag);
        }
        nbt.put("Networks", list);

        nbt.putInt("NextId", this.nextId);

        return nbt;
    }

    protected void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registry) {
        networks.clear();

        if (nbt.contains("Networks", NbtElement.LIST_TYPE)) {
            NbtList list = nbt.getList("Networks", NbtElement.COMPOUND_TYPE);
            for (int i = 0; i < list.size(); i++) {
                NbtCompound netTag = list.getCompound(i);
                int netId = netTag.getInt("NetId");
                CableNetwork net = CableNetwork.readFromNbt(netTag);
                networks.put(netId, net);

                if (netId >= nextId) {
                    nextId = netId + 1;
                }
            }
        }

        this.nextId = Math.max(this.nextId, nbt.getInt("NextId"));
    }
}