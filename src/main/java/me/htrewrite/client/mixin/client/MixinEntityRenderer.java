package me.htrewrite.client.mixin.client;

import me.htrewrite.client.HTRewrite;
import me.htrewrite.client.event.custom.render.RenderHurtCameraEvent;
import me.htrewrite.client.event.custom.render.RenderSetupFogEvent;
import net.minecraft.client.renderer.EntityRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityRenderer.class)
public class MixinEntityRenderer {
    @Inject(method = "setupFog", at = @At("HEAD"), cancellable = true)
    public void setupFog(int startCoords, float partialTicks, CallbackInfo callbackInfo) {
        RenderSetupFogEvent event = new RenderSetupFogEvent(startCoords, partialTicks);
        HTRewrite.EVENT_BUS.post(event);
        if(event.isCancelled())
            callbackInfo.cancel();
    }

    @Inject(method = "hurtCameraEffect", at = @At("HEAD"), cancellable = true)
    public void hurtCameraEffect(float ticks, CallbackInfo info) {
        RenderHurtCameraEvent event = new RenderHurtCameraEvent(ticks);
        HTRewrite.EVENT_BUS.post(event);
        if(event.isCancelled())
            info.cancel();
    }
}