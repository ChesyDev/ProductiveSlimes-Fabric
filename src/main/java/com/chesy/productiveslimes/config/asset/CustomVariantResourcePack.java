package com.chesy.productiveslimes.config.asset;

import com.chesy.productiveslimes.ProductiveSlimes;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.JsonOps;
import net.minecraft.resource.*;
import net.minecraft.resource.metadata.ResourceMetadataSerializer;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class CustomVariantResourcePack implements ResourcePack {
    private static final Logger LOGGER = LogUtils.getLogger();
    private final Map<String, byte[]> resources;

    public CustomVariantResourcePack(Map<String, byte[]> resources) {
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
        String path = String.format("%s/%s/%s", type.getDirectory(), id.getNamespace(), id.getPath());
        byte[] data = resources.get(path);
        if (data != null) {
            byte[] finalData = data;
            return () -> new ByteArrayInputStream(finalData);
        }
        // Try alternate path format
        String alternatePath = type.getDirectory() + "/" + id.getNamespace() + "/" + id.getPath();
        data = resources.get(alternatePath);
        if (data != null) {
            byte[] finalData1 = data;
            return () -> new ByteArrayInputStream(finalData1);
        }

        return null;
    }

    @Override
    public void findResources(ResourceType type, String namespace, String path, ResultConsumer consumer) {
        String prefix = type.getDirectory() + "/" + namespace + "/" + path;
        resources.forEach((key, data) -> {
            if (key.startsWith(prefix)) {
                String resourcePath = key.substring((type.getDirectory() + "/" + namespace + "/").length());
                Identifier location = Identifier.of(namespace, resourcePath);
                consumer.accept(location, () -> new ByteArrayInputStream(data));
            }
        });
    }

    @Override
    public Set<String> getNamespaces(ResourceType type) {
        return Collections.singleton(ProductiveSlimes.MODID);
    }

    @Nullable
    @Override
    public <T> T parseMetadata(ResourceMetadataSerializer<T> metadataSerializer) throws IOException {
        if ("pack".equals(metadataSerializer.name())) { // Check the section name
            InputSupplier<InputStream> supplier = openRoot("pack.mcmeta");
            if (supplier != null) {
                try (InputStream stream = supplier.get()) {
                    // Parse the JSON using Gson
                    JsonObject json = new Gson().fromJson(new InputStreamReader(stream, StandardCharsets.UTF_8), JsonObject.class);

                    // Deserialize the JSON using the Codec from the MetadataSectionType
                    return metadataSerializer.codec()
                            .parse(JsonOps.INSTANCE, json.getAsJsonObject("pack"))
                            .resultOrPartial(error -> {
                                // Log or handle errors here
                                System.err.println("Error parsing metadata section: " + error);
                            })
                            .orElse(null); // Return null if parsing fails
                }
            }
        }
        return null;
    }

    @Override
    public ResourcePackInfo getInfo() {
        return new ResourcePackInfo("productiveslimes_extra", Text.literal("Productive Slimes Resources"), ResourcePackSource.BUILTIN, Optional.empty());
    }

    @Override
    public void close() {

    }
}
