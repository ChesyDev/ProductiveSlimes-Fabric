package com.chesy.productiveslimes.config.asset;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.mojang.serialization.JsonOps;
import net.minecraft.resource.*;
import net.minecraft.resource.metadata.ResourceMetadataSerializer;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class CustomVariantDataPack implements ResourcePack {
    private final Map<String, byte[]> resources;
    public CustomVariantDataPack(Map<String, byte[]> resources) {
        this.resources = resources;
    }

    @Nullable
    @Override
    public InputSupplier<InputStream> openRoot(String... segments) {
        String path = String.join("/", segments);
        byte[] data = resources.get(path);
        if (data != null) {
            return () -> new ByteArrayInputStream(data);
        }
        return null;
    }

    @Nullable
    @Override
    public InputSupplier<InputStream> open(ResourceType type, Identifier id) {
        if (type != ResourceType.SERVER_DATA) {
            return null;
        }
        String path = "data/" + id.getNamespace() + "/" + id.getPath();
        byte[] data = resources.get(path);
        if (data != null) {
            return () -> new ByteArrayInputStream(data);
        }
        return null;
    }

    @Override
    public void findResources(ResourceType type, String namespace, String path, ResultConsumer consumer) {
        if (type != ResourceType.SERVER_DATA) {
            return;
        }
        String prefix = "data/" + namespace + "/" + path;
        resources.forEach((key, data) -> {
            if (key.startsWith(prefix)) {
                String resourcePath = key.substring(("data/" + namespace + "/").length());
                Identifier location = Identifier.of(namespace, resourcePath);
                consumer.accept(location, () -> new ByteArrayInputStream(data));
            }
        });
    }

    @Override
    public Set<String> getNamespaces(ResourceType type) {
        if (type != ResourceType.SERVER_DATA) {
            return Collections.emptySet();
        }
        Set<String> namespaces = new HashSet<>();
        resources.keySet().forEach(key -> {
            if (key.startsWith("data/")) {
                String[] parts = key.substring("data/".length()).split("/", 2);
                if (parts.length > 1) {
                    namespaces.add(parts[0]);
                }
            }
        });
        return namespaces;
    }

    @Nullable
    @Override
    public <T> T parseMetadata(ResourceMetadataSerializer<T> metadataSerializer) throws IOException {
        if ("pack".equals(metadataSerializer.name())) { // Use name() method to get the section name
            InputSupplier<InputStream> supplier = openRoot("pack.mcmeta");
            if (supplier != null) {
                try (InputStream stream = supplier.get()) {
                    JsonObject json = new Gson().fromJson(new InputStreamReader(stream, StandardCharsets.UTF_8), JsonObject.class);
                    // Use the Codec from the sectionType to deserialize the JSON object
                    return metadataSerializer.codec().parse(JsonOps.INSTANCE, json.getAsJsonObject("pack"))
                            .resultOrPartial(error -> {
                                System.err.println("Failed to parse metadata section: " + error);
                            })
                            .orElse(null);
                }
            }
        }
        return null;
    }

    @Override
    public ResourcePackInfo getInfo() {
        return new ResourcePackInfo("productiveslimes_datapack", Text.literal("In Memory Pack"), ResourcePackSource.SERVER, Optional.empty());
    }

    @Override
    public void close() {

    }

    @Override
    public String getId() {
        return "productiveslimes_datapack";
    }
}
