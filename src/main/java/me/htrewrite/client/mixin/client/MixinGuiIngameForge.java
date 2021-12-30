package me.htrewrite.client.mixin.client;

import me.htrewrite.client.HTRewrite;
import me.htrewrite.client.event.custom.render.RenderHelmetEvent;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraftforge.client.GuiIngameForge;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = GuiIngameForge.class, remap = false)
public class MixinGuiIngameForge {
    @Inject(method = "renderPortal", at = @At("HEAD"), cancellable = true)
    protected void renderPortal(ScaledResolution res, float partialTicks, CallbackInfo callbackInfo) {
        RenderHelmetEvent event = new RenderHelmetEvent();
        HTRewrite.EVENT_BUS.post(event);
        if(event.isCancelled())
            callbackInfo.cancel();
    }
}