package me.htrewrite.client.mixin.client;

import me.htrewrite.client.HTRewrite;
import me.htrewrite.client.event.custom.player.PlayerBlockReachEvent;
import me.htrewrite.client.event.custom.world.BlockDestroyEvent;
import me.htrewrite.client.event.custom.world.BlockEvent;
import net.minecraft.client.multiplayer.PlayerControllerMP;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerControllerMP.class)
public class MixinPlayerControllerMP {
    @Inject(method = "getBlockReachDistance", at = @At("HEAD"), cancellable = true)
    public void getBlockReachDistance(CallbackInfoReturnable<Float> callbackInfoReturnable) {
        PlayerBlockReachEvent event = new PlayerBlockReachEvent();
        HTRewrite.EVENT_BUS.post(event);
        if(event.reachDistance > 0f) {
            callbackInfoReturnable.setReturnValue(event.reachDistance);
            callbackInfoReturnable.cancel();
        }
    }

    @Inject(method = "onPlayerDestroyBlock", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;playEvent(ILnet/minecraft/util/math/BlockPos;I)V"), cancellable = true)
    private void onPlayerDestroyBlock(BlockPos blockPos, CallbackInfoReturnable<Boolean> callbackInfoReturnable) {
        HTRewrite.EVENT_BUS.post(new BlockDestroyEvent(blockPos));
    }

    @Inject(method = { "clickBlock" }, at = { @At("HEAD") }, cancellable = true)
    private void clickBlockHook(final BlockPos pos, final EnumFacing face, final CallbackInfoReturnable<Boolean> info) {
        final BlockEvent event = new BlockEvent(3, pos, face);
        HTRewrite.EVENT_BUS.post(event);
    }

    @Inject(method = { "onPlayerDamageBlock" }, at = { @At("HEAD") }, cancellable = true)
    private void onPlayerDamageBlockHook(final BlockPos pos, final EnumFacing face, final CallbackInfoReturnable<Boolean> info) {
        final BlockEvent event = new BlockEvent(4, pos, face);
        HTRewrite.EVENT_BUS.post(event);
    }
}