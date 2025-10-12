package com.danrus.mvc;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackResources;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Stream;

public class MergingResourceManagerWrapper implements ResourceManager {
    private final ResourceManager delegate;
    private static final String SELECT_TYPE = "minecraft:select";

    public MergingResourceManagerWrapper(ResourceManager delegate) {
        this.delegate = delegate;
    }

    @Override
    public @NotNull Set<String> getNamespaces() {
        return this.delegate.getNamespaces();
    }

    @Override
    public @NotNull List<Resource> getResourceStack(ResourceLocation location)  {
        return this.delegate.getResourceStack(location);
    }

    @Override
    public @NotNull Map<ResourceLocation, Resource> listResources(String path, Predicate<ResourceLocation> filter) {
        if (!path.contains("items")) {
            return this.delegate.listResources(path, filter);
        }

        Map<ResourceLocation, Resource> originalMap = this.delegate.listResources(path, filter);
        Map<ResourceLocation, Resource> mergedMap = new TreeMap<>();

        for(Map.Entry<ResourceLocation, Resource> entry : originalMap.entrySet()) {
            Resource merged = mergeItemModelResource(entry.getKey(), entry.getValue());
            mergedMap.put(entry.getKey(), merged);
        }

        return mergedMap;
    }

    @Override
    public @NotNull Map<ResourceLocation, List<Resource>> listResourceStacks(String path, Predicate<ResourceLocation> filter) {
        return this.delegate.listResourceStacks(path, filter);
    }

    @Override
    public @NotNull Stream<PackResources> listPacks() {
        return this.delegate.listPacks();
    }

    @Override
    public @NotNull Optional<Resource> getResource(ResourceLocation location) {
        if (!location.getPath().contains("items/")) {
            return this.delegate.getResource(location);
        }

        Optional<Resource> optional = this.delegate.getResource(location);
        if (optional.isEmpty()) {
            return optional;
        }

        Resource merged = mergeItemModelResource(location, optional.get());
        return Optional.of(merged);
    }

    private Resource mergeItemModelResource(ResourceLocation location, Resource topResource) {
        List<Resource> stack = this.delegate.getResourceStack(location);
        if (stack.isEmpty()) {
            return topResource;
        }

        List<JsonObject> allModels = new ArrayList<>(stack.size());
        for (Resource res : stack) {
            try (InputStream is = res.open();
                 Reader reader = new InputStreamReader(is, StandardCharsets.UTF_8)) {
                JsonObject json = JsonParser.parseReader(reader).getAsJsonObject();
                if (json.has("model") && json.getAsJsonObject("model").has("type")) {
                    allModels.add(json);
                }
            } catch (Exception e) {
                // Ignore invalid models
            }
        }

        if (allModels.isEmpty()) {
            return topResource;
        }

        Map<String, PropertyGroup> groupedByProperty = new LinkedHashMap<>();
        JsonObject baseFallback = null;

        for (JsonObject modelJson : allModels) {
            JsonObject model = modelJson.getAsJsonObject("model");
            String type = model.get("type").getAsString();

            if (SELECT_TYPE.equals(type) || "select".equals(type)) {
                String propertyKey = extractPropertyKey(model);

                PropertyGroup group = groupedByProperty.computeIfAbsent(propertyKey,
                        k -> new PropertyGroup(propertyKey));

                if (group.templateNode == null) {
                    group.templateNode = model.deepCopy();
                }

                if (model.has("cases")) {
                    JsonArray cases = model.getAsJsonArray("cases");
                    group.addCases(cases);
                }

                if (baseFallback == null && model.has("fallback")) {
                    JsonElement fb = model.get("fallback");
                    if (fb.isJsonObject()) {
                        baseFallback = fb.getAsJsonObject();
                    }
                }
            }
        }

        if (groupedByProperty.isEmpty()) {
            return topResource;
        }

        JsonObject chainedModel = buildFallbackChain(groupedByProperty, baseFallback);

        JsonObject result = new JsonObject();
        result.add("model", chainedModel);

        byte[] bytes = result.toString().getBytes(StandardCharsets.UTF_8);
        return new Resource(topResource.source(), () -> new ByteArrayInputStream(bytes));
    }

    private String extractPropertyKey(JsonObject model) {
        if (!model.has("property")) {
            return "unknown";
        }

        JsonElement propElement = model.get("property");

        if (propElement.isJsonPrimitive()) {
            return propElement.getAsString();
        }

        return "unknown";
    }

    private JsonObject buildFallbackChain(Map<String, PropertyGroup> groups, JsonObject baseFallback) {
        JsonObject currentFallback = baseFallback;

        List<PropertyGroup> groupList = new ArrayList<>(groups.values());
        Collections.reverse(groupList);

        for (PropertyGroup group : groupList) {
            JsonObject selectNode = new JsonObject();

            if (group.templateNode != null) {
                for (Map.Entry<String, JsonElement> entry : group.templateNode.entrySet()) {
                    String key = entry.getKey();
                    if (!"cases".equals(key) && !"fallback".equals(key)) {
                        selectNode.add(key, entry.getValue());
                    }
                }
            } else {
                selectNode.addProperty("type", SELECT_TYPE);
            }

            JsonArray dedupedCases = deduplicateCases(group.allCases);
            if (!dedupedCases.isEmpty()) {
                selectNode.add("cases", dedupedCases);
            }

            if (currentFallback != null) {
                selectNode.add("fallback", currentFallback);
            }

            currentFallback = selectNode;
        }

        return currentFallback;
    }

    private JsonArray deduplicateCases(List<JsonArray> allCases) {
        JsonArray result = new JsonArray();
        Set<String> seenWhen = new HashSet<>();

        for (JsonArray casesArray : allCases) {
            for (int i = 0; i < casesArray.size(); i++) {
                JsonObject caseObj = casesArray.get(i).getAsJsonObject();
                String whenKey = caseObj.get("when").toString();

                if (!seenWhen.contains(whenKey)) {
                    seenWhen.add(whenKey);
                    result.add(caseObj);
                }
            }
        }

        return result;
    }

    private static class PropertyGroup {
        final String propertyKey;
        JsonObject templateNode;
        final List<JsonArray> allCases = new ArrayList<>();

        PropertyGroup(String key) {
            this.propertyKey = key;
        }

        void addCases(JsonArray cases) {
            if (cases != null && !cases.isEmpty()) {
                allCases.add(cases);
            }
        }
    }
}
