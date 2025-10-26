package com.danrus.mvc;

import com.danrus.mvc.config.ModConfig;
import net.fabricmc.api.ClientModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MultiVanillaCit implements ClientModInitializer {

    public static Logger LOGGER = LoggerFactory.getLogger("Rp Vanilla CIT Fix");

    @Override
    public void onInitializeClient() {
        ModConfig.initialize();
    }
}
