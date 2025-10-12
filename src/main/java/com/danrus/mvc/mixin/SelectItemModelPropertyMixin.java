package com.danrus.mvc.mixin;

import com.mojang.serialization.DataResult;
import net.minecraft.client.renderer.item.SelectItemModel;
import net.minecraft.client.renderer.item.properties.select.SelectItemModelProperty;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(SelectItemModelProperty.Type.class)
public class SelectItemModelPropertyMixin {
    @Inject(
            method = "validateCases",
            at = @At("RETURN"),
            cancellable = true
    )
    private static <T> void mvc$validateCases(List<SelectItemModel.SwitchCase<T>> cases, CallbackInfoReturnable<DataResult<List<SelectItemModel.SwitchCase<T>>>> cir) {
        cir.setReturnValue(DataResult.success(cases));
    }
}
