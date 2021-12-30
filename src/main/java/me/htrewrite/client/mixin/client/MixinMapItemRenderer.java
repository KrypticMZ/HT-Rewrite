package me.htrewrite.client.mixin.client;

import me.htrewrite.client.HTRewrite;
import me.htrewrite.client.event.custom.render.RenderMapEvent;
import net.minecraft.client.gui.MapItemRenderer;
import net.minecraft.world.storage.MapData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MapItemRenderer.class)
public class MixinMapItemRenderer {
    @Inject(method ="renderMap", at = @At("HEAD"), cancellable = true)
    public void render(MapData mapData, boolean noOverlayRendering, CallbackInfo ci) {
        RenderMapEvent event = new RenderMapEvent();
        HTRewrite.EVENT_BUS.post(event);
        if(event.isCancelled())
            ci.cancel();
    }
}