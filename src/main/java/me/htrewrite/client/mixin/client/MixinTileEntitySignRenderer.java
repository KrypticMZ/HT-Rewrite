package me.htrewrite.client.mixin.client;

import me.htrewrite.client.HTRewrite;
import me.htrewrite.client.event.custom.render.RenderSignEvent;
import net.minecraft.client.renderer.tileentity.TileEntitySignRenderer;
import net.minecraft.tileentity.TileEntitySign;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(TileEntitySignRenderer.class)
public class MixinTileEntitySignRenderer {
    @Inject(method = "render", at = @At("HEAD"), cancellable = true)
    public void render(TileEntitySign te, double x, double y, double z, float partialTicks, int destroyStage, float alpha, CallbackInfo ci) {
        RenderSignEvent event = new RenderSignEvent();
        HTRewrite.EVENT_BUS.post(event);
        if(event.isCancelled())
            ci.cancel();
    }
}