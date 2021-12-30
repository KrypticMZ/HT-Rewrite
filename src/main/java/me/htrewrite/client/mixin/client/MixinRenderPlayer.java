package me.htrewrite.client.mixin.client;

import me.htrewrite.client.HTRewrite;
import me.htrewrite.client.event.custom.render.RenderPlayerNameEvent;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.renderer.entity.RenderPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(RenderPlayer.class)
public class MixinRenderPlayer {
    @Inject(method = "renderEntityName", at = @At("HEAD"), cancellable = true)
    public void renderEntityName(AbstractClientPlayer entityIn, double x, double y, double z, String name, double distanceSq, CallbackInfo callbackInfo) {
        RenderPlayerNameEvent event = new RenderPlayerNameEvent(entityIn, x, y, z, name, distanceSq);
        HTRewrite.EVENT_BUS.post(event);
        if(event.isCancelled())
            callbackInfo.cancel();
    }
}