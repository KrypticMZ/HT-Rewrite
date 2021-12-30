package me.htrewrite.client.mixin.client;

import me.htrewrite.client.HTRewrite;
import me.htrewrite.client.event.custom.world.BlockLiquidCollisionBBEvent;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BlockLiquid.class)
public class MixinBlockLiquid {
    @Inject(method = "getCollisionBoundingBox", at = @At("HEAD"), cancellable = true)
    public void getCollisionBoundingBox(IBlockState blockState, IBlockAccess worldIn, BlockPos pos, CallbackInfoReturnable<AxisAlignedBB> callbackInfoReturnable) {
        BlockLiquidCollisionBBEvent event = new BlockLiquidCollisionBBEvent(pos);
        HTRewrite.EVENT_BUS.post(event);
        if(event.isCancelled()) {
            callbackInfoReturnable.setReturnValue(event.axisAlignedBB);
            callbackInfoReturnable.cancel();
        }
    }
}