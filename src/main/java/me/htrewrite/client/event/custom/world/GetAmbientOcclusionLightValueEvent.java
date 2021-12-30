package me.htrewrite.client.event.custom.world;

import me.htrewrite.client.event.custom.CustomEvent;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

public class GetAmbientOcclusionLightValueEvent extends CustomEvent {
    public CallbackInfoReturnable<Float> callbackInfoReturnable;
    public GetAmbientOcclusionLightValueEvent(CallbackInfoReturnable<Float> callbackInfoReturnable) {
        super();

        this.callbackInfoReturnable = callbackInfoReturnable;
    }
}