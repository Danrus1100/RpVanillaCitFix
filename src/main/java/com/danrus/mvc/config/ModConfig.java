package com.danrus.mvc.config;

import com.google.gson.GsonBuilder;
import dev.isxander.yacl3.api.ConfigCategory;
import dev.isxander.yacl3.api.YetAnotherConfigLib;
import dev.isxander.yacl3.config.v2.api.ConfigClassHandler;
import dev.isxander.yacl3.config.v2.api.SerialEntry;
import dev.isxander.yacl3.config.v2.api.serializer.GsonConfigSerializerBuilder;
import dev.isxander.yacl3.platform.YACLPlatform;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.List;

public class ModConfig {
    public static final ConfigClassHandler<ModConfig> HANDLER = ConfigClassHandler.createBuilder(ModConfig.class)
            .serializer(config -> GsonConfigSerializerBuilder.create(config)
                    .setPath(YACLPlatform.getConfigDir().resolve("mcv.json5"))
                    .appendGsonBuilder(GsonBuilder::setPrettyPrinting)
                    .setJson5(true)
                    .build())
            .build();

    public static List<ConfigCategory> CATEGORIES = new ArrayList<>();

    @SerialEntry
    public boolean enableMerging = true;

    @SerialEntry
    public boolean enablePackCrashFix = true;

    @SerialEntry
    public boolean enableDebug = false;

    @SerialEntry
    public List<String> debugItems = new ArrayList<>();

    public static Screen getConfigScreen(Screen parent) {
        return YetAnotherConfigLib.createBuilder()
                .title(Component.literal("Player Armor Stand"))
                .categories(CATEGORIES)
                .save(ModConfig::save)
                .build()
                .generateScreen(parent);
    }


    public static void initialize() {
        load();
        CATEGORIES.add(MainCategory.get());
    }

    public static ModConfig get() {
        return HANDLER.instance();
    }

    public static void save() {
        HANDLER.save();
        Minecraft.getInstance().reloadResourcePacks();
    }

    public static void load() {
        HANDLER.load();
    }
}
