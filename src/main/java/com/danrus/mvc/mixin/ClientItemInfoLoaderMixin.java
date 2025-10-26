package com.danrus.mvc.mixin;

import com.danrus.mvc.MergingResourceManagerWrapper;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

import com.danrus.mvc.config.ModConfig;
import net.minecraft.client.resources.model.ClientItemInfoLoader;
import net.minecraft.server.packs.resources.ResourceManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ClientItemInfoLoader.class)
public class ClientItemInfoLoaderMixin {
    @Inject(
            method = "scheduleLoad",
            at = @At("HEAD"),
            cancellable = true
    )
    private static void onScheduleLoad(ResourceManager resourceManager, Executor executor, CallbackInfoReturnable<CompletableFuture<ClientItemInfoLoader.LoadedClientInfos>> cir) {
        if (!(resourceManager instanceof MergingResourceManagerWrapper) && ModConfig.get().enableMerging) {
            ResourceManager merged = new MergingResourceManagerWrapper(resourceManager);
            cir.setReturnValue(ClientItemInfoLoader.scheduleLoad(merged, executor));
        }

    }
}
