package me.htrewrite.client.mixin.client;

import me.htrewrite.client.HTRewrite;
import me.htrewrite.client.event.custom.world.SetOpaqueCubeEvent;
import net.minecraft.client.renderer.chunk.VisGraph;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(VisGraph.class)
public class MixinVisGraph {
    @Inject(method = "setOpaqueCube", at = @At("HEAD"), cancellable = true)
    public void setOpaqueCube(BlockPos blockPos, CallbackInfo callbackInfo) {
        SetOpaqueCubeEvent event = new SetOpaqueCubeEvent(blockPos);
        HTRewrite.EVENT_BUS.post(event);
        if(event.isCancelled())
            callbackInfo.cancel();
    }
}