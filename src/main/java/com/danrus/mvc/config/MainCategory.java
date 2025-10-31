package com.danrus.mvc.config;

import dev.isxander.yacl3.api.*;
import dev.isxander.yacl3.api.controller.StringControllerBuilder;
import dev.isxander.yacl3.api.controller.TickBoxControllerBuilder;
import dev.isxander.yacl3.platform.YACLPlatform;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import java.nio.file.Path;
import java.util.ArrayList;

public class MainCategory {

    private static Option<Boolean> enableMergingOption;
    private static Option<Boolean> enablePackCrashFixOption;

    public static ConfigCategory get(){

        enableMergingOption = Option.<Boolean>createBuilder()
                .name(Component.translatable("mvc.config.enable_merging"))
                .binding(
                        true,
                        () -> ModConfig.get().enableMerging,
                        newVal -> ModConfig.get().enableMerging = newVal
                )
                .description(OptionDescription.createBuilder().text(Component.translatable("mvc.config.enable_merging.desc")).build())
                .controller(TickBoxControllerBuilder::create)
                .build();

        enablePackCrashFixOption = Option.<Boolean>createBuilder()
                .name(Component.translatable("mvc.config.enable_pack_crash_fix"))
                .binding(
                        true,
                        () -> ModConfig.get().enablePackCrashFix,
                        newVal -> ModConfig.get().enablePackCrashFix = newVal
                )
                .description(OptionDescription.createBuilder().text(Component.translatable("mvc.config.enable_pack_crash_fix.desc")).build())
                .controller(TickBoxControllerBuilder::create)
                .build();


        return ConfigCategory.createBuilder()

                .name(Component.translatable("mvc.config.group.general"))
                .group(OptionGroup.createBuilder()
                        .name(Component.translatable("mvc.config.group.general"))

                        .option(enableMergingOption)
                        .option(enablePackCrashFixOption)

                        .build())

                .group(OptionGroup.createBuilder()
                        .name(Component.translatable("mvc.config.group.debug"))

                        .option(Option.<Boolean>createBuilder()
                                .name(Component.translatable("mvc.config.enable_debug"))
                                .binding(
                                        false,
                                        () -> ModConfig.get().enableDebug,
                                        newVal -> ModConfig.get().enableDebug = newVal
                                )
                                .controller(TickBoxControllerBuilder::create)
                                .description(OptionDescription.createBuilder().text(Component.translatable("mvc.config.enable_debug.desc")).build())
                                .build())


                        .option(ButtonOption.createBuilder()
                                .name(Component.translatable("mvc.config.open_exported_folder"))
                                .action(((yaclScreen, buttonOption) -> {
                                    Path path = FabricLoader.getInstance().getGameDir().resolve(".debug/mvc");
                                    if (!path.toFile().exists()) {
                                        path.toFile().mkdirs();
                                    }
                                    Util.getPlatform().openPath(path);
                                }))
                                .text(Component.translatable("mvc.config.open"))
                                .build())

                        .option(ButtonOption.createBuilder()
                                .name(Component.translatable("mvc.config.reload_textures"))
                                .action(((yaclScreen, buttonOption) -> {
                                    Minecraft.getInstance().reloadResourcePacks();
                                }))
                                .description(OptionDescription.createBuilder().text(Component.translatable("mvc.config.reload_textures.desc")).build())
                                .build())

                        .build())

                .group(ListOption.<String>createBuilder()
                                .name(Component.translatable("mvc.config.debug_items"))
                                .binding(
                                        new ArrayList<>(),
                                        () -> ModConfig.get().debugItems,
                                        newVal -> ModConfig.get().debugItems = newVal
                                )
                                .controller(StringControllerBuilder::create)
                                .initial("minecraft:items/netherite_sword.json")
                                .build())

                .build();
    }

    public static boolean shouldReload() {
        return enableMergingOption.changed() && enablePackCrashFixOption.changed();
    }

    private static ResourceLocation imageSample(String name) {
        return YACLPlatform.rl("rp-vanilla-cit-fix", "textures/images/" + name);
    }
}