package com.chesy.productiveslimes.util;

import com.chesy.productiveslimes.ProductiveSlimes;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.mojang.logging.LogUtils;
import net.minecraft.resource.*;
import net.minecraft.resource.metadata.ResourceMetadataReader;
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
        LOGGER.info("Registered resources in CustomVariantResourcePack:");
        resources.keySet().forEach(path -> LOGGER.info("  - {}", path));
    }

    @Nullable
    @Override
    public InputSupplier<InputStream> openRoot(String... segments) {
        String path = String.join("/", segments);
        LOGGER.info("Attempting to open root path: {}", path);
        byte[] data = resources.get(path);
        LOGGER.info("Found root resource: {}", path);
        if (data != null) {
            return () -> new ByteArrayInputStream(data);
        }
        LOGGER.info("Root resource not found: {}", path);
        return null;
    }

    @Nullable
    @Override
    public InputSupplier<InputStream> open(ResourceType type, Identifier id) {
        String path = String.format("%s/%s/%s", type.getDirectory(), id.getNamespace(), id.getPath());
        LOGGER.info("Attempting to open resource: {}", path);
        byte[] data = resources.get(path);
        if (data != null) {
            LOGGER.info("Found resource: {}", path);
            byte[] finalData = data;
            return () -> new ByteArrayInputStream(finalData);
        }
        // Try alternate path format
        String alternatePath = type.getDirectory() + "/" + id.getNamespace() + "/" + id.getPath();
        data = resources.get(alternatePath);
        if (data != null) {
            LOGGER.info("Found resource (alternate path): {}", alternatePath);
            byte[] finalData1 = data;
            return () -> new ByteArrayInputStream(finalData1);
        }

        LOGGER.info("Resource not found: {} (or alternate: {})", path, alternatePath);
        return null;
    }

    @Override
    public void findResources(ResourceType type, String namespace, String path, ResultConsumer consumer) {
        String prefix = type.getDirectory() + "/" + namespace + "/" + path;
        LOGGER.info("Finding resources for base path: {}", prefix);
        resources.forEach((key, data) -> {
            if (key.startsWith(prefix)) {
                String resourcePath = key.substring((type.getDirectory() + "/" + namespace + "/").length());
                Identifier location = Identifier.of(namespace, resourcePath);
                LOGGER.info("Found matching resource: {} -> {}", path, location);
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
    public <T> T parseMetadata(ResourceMetadataReader<T> metaReader) throws IOException {
        if ("pack".equals(metaReader.getKey())) { // Check the section name
            LOGGER.info("Parsing metadata section: {}", metaReader.getKey());
            InputSupplier<InputStream> supplier = openRoot("pack.mcmeta");
            if (supplier != null) {
                try (InputStream stream = supplier.get()) {
                    JsonObject json = new Gson().fromJson(new InputStreamReader(stream, StandardCharsets.UTF_8), JsonObject.class);
                    return metaReader.fromJson(json.getAsJsonObject("pack"));
                }
            }
        }
        return null;
    }

    @Override
    public String getName() {
        return "Productive Slimes Resources";
    }

    @Override
    public void close() {

    }
}
