package com.danrus.mvc;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.resources.ResourceLocation;
import org.slf4j.Logger;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public class DebugUtils {

    static Logger logger = MultiVanillaCit.LOGGER;

    public static void ExportDebugItem(ResourceLocation location, JsonObject json) {
        Path path = FabricLoader.getInstance().getGameDir().resolve(".debug/mvc");
        try {
            Path fullPath = path.resolve(location.getNamespace()).resolve(location.getPath());
            Files.createDirectories(fullPath.getParent());

            Gson gson = new GsonBuilder().setPrettyPrinting().create();

            try (Writer writer = new BufferedWriter(new OutputStreamWriter(
                    new FileOutputStream(fullPath.toFile()), StandardCharsets.UTF_8))) {
                gson.toJson(json, writer);
            }
        } catch (Exception e) {
            logger.error("Failed to export debug item: " + location.toString(), e);
        }
    }
}
