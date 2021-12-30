package me.htrewrite.client.mixin.client;

import me.htrewrite.client.HTRewrite;
import me.htrewrite.client.event.custom.CustomEvent;
import me.htrewrite.client.event.custom.render.RenderEntityEvent;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(RenderManager.class)
public class MixinRenderManager {
    @Inject(method = "renderEntity", at = @At("HEAD"), cancellable = true)
    public void renderEntity(Entity entityIn, double x, double y, double z, float yaw, float partialTicks, boolean p_188391_10_, CallbackInfo callbackInfo) {
        RenderEntityEvent event = new RenderEntityEvent(entityIn, x, y, z, yaw, partialTicks, p_188391_10_);
        event.setEra(CustomEvent.Era.PRE);
        HTRewrite.EVENT_BUS.post(event);
        if(event.isCancelled())
            callbackInfo.cancel();
    }

    @Inject(method = "renderEntity", at = @At("RETURN"))
    public void postRenderEntity(Entity entityIn, double x, double y, double z, float yaw, float partialTicks, boolean p_188391_10_, CallbackInfo callbackInfo) {
        RenderEntityEvent event = new RenderEntityEvent(entityIn, x, y, z, yaw, partialTicks, p_188391_10_);
        event.setEra(CustomEvent.Era.POST);
        HTRewrite.EVENT_BUS.post(event);
    }
}