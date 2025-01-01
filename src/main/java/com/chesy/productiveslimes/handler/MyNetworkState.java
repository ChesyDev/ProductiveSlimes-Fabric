package com.chesy.productiveslimes.handler;

import net.minecraft.datafixer.DataFixTypes;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.world.PersistentState;

import java.util.HashMap;
import java.util.Map;

public class MyNetworkState extends PersistentState {

    // The main map of networks
    private final Map<Integer, CableNetwork> networks = new HashMap<>();

    // Next ID to assign so we don't reuse old IDs
    private int nextId = 1;

    // The "Type" record your code references
    // (depends on your snapshot’s method signature)
    public static final PersistentState.Type<MyNetworkState> MY_TYPE =
            new PersistentState.Type<>(
                    MyNetworkState::new, // constructor
                    (nbt, registry) -> {
                        MyNetworkState state = new MyNetworkState();
                        state.readNbt(nbt, registry);
                        return state;
                    },
                    DataFixTypes.LEVEL // or DataFixTypes.LEVEL if you prefer
            );

    public MyNetworkState() {
        super();
    }

    /* ---------------------------------------------------------------------- */
    /* 1) Accessors for your networks                                         */
    /* ---------------------------------------------------------------------- */

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

    /* ---------------------------------------------------------------------- */
    /* 2) Reading from & Writing to NBT                                       */
    /* ---------------------------------------------------------------------- */

    @Override
    public NbtCompound writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registry) {
        // 2A) Write all networks
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

        // 2B) Write nextId
        nbt.putInt("NextId", this.nextId);

        return nbt;
    }

    protected void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registry) {
        networks.clear();

        // 2C) Read all networks
        if (nbt.contains("Networks", NbtElement.LIST_TYPE)) {
            NbtList list = nbt.getList("Networks", NbtElement.COMPOUND_TYPE);
            for (int i = 0; i < list.size(); i++) {
                NbtCompound netTag = list.getCompound(i);
                int netId = netTag.getInt("NetId");
                CableNetwork net = CableNetwork.readFromNbt(netTag);
                networks.put(netId, net);

                // Ensure we skip reusing old IDs
                if (netId >= nextId) {
                    nextId = netId + 1;
                }
            }
        }

        // 2D) Read nextId
        this.nextId = Math.max(this.nextId, nbt.getInt("NextId"));
    }
}