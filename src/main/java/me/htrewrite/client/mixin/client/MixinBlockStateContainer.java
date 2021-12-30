package me.htrewrite.client.mixin.client;

import me.htrewrite.client.HTRewrite;
import me.htrewrite.client.event.custom.world.GetAmbientOcclusionLightValueEvent;
import me.htrewrite.client.event.custom.world.ShouldSideBeRenderedEvent;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = BlockStateContainer.class)
public class MixinBlockStateContainer {
    @Inject(method = "c(Lamy;Let;Lfa;)Z", at = @At("HEAD"), remap = false)
    @SideOnly(Side.CLIENT)
    public void shouldSideBeRendered(IBlockAccess blockAccess, BlockPos pos, EnumFacing facing, CallbackInfoReturnable<Boolean> callbackInfoReturnable) {
        ShouldSideBeRenderedEvent event = new ShouldSideBeRenderedEvent(blockAccess, pos, facing, callbackInfoReturnable);
        HTRewrite.EVENT_BUS.post(event);
        if(event.isCancelled()) {
            callbackInfoReturnable.setReturnValue(false);
            callbackInfoReturnable.cancel();
        }
    }

    @Inject(method = "getAmbientOcclusionLightValue", at = @At("HEAD"), cancellable = true)
    @SideOnly(Side.CLIENT)
    public void getAmbientOcclusionLightValue(CallbackInfoReturnable<Float> callbackInfoReturnable) {
        GetAmbientOcclusionLightValueEvent event = new GetAmbientOcclusionLightValueEvent(callbackInfoReturnable);
        HTRewrite.EVENT_BUS.post(event);
        if(event.isCancelled())
            callbackInfoReturnable.cancel();
    }
}