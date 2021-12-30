package me.htrewrite.client.mixin.client;

import me.htrewrite.client.HTRewrite;
import me.htrewrite.client.event.custom.render.RenderBlockEvent;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BlockRendererDispatcher.class)
public class MixinBlockRendererDispatcher {
    @Inject(method="renderBlock", at=@At("HEAD"), cancellable = true)
    public void renderBlock(IBlockState state, BlockPos pos, IBlockAccess blockAccess, BufferBuilder bufferBuilderIn, CallbackInfoReturnable<Boolean> callbackInfoReturnable) {
        RenderBlockEvent event = new RenderBlockEvent(state, pos, blockAccess, bufferBuilderIn);
        HTRewrite.EVENT_BUS.post(event);
        if(event.isCancelled()) {
            callbackInfoReturnable.setReturnValue(false);
            callbackInfoReturnable.cancel();
        }
    }
}