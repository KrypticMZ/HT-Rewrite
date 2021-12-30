package me.htrewrite.client.mixin.client;

import me.htrewrite.client.HTRewrite;
import me.htrewrite.client.event.custom.player.PlayerApplyCollisionEvent;
import me.htrewrite.client.event.custom.player.PlayerPushedByWaterEvent;
import me.htrewrite.client.event.custom.player.PlayerTravelEvent;
import net.minecraft.entity.Entity;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.player.EntityPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = EntityPlayer.class, priority = Integer.MAX_VALUE)
public abstract class MixinEntityPlayer extends MixinEntityLivingBase {
    @Inject(method = "travel", at = @At("HEAD"), cancellable = true)
    public void travel(float strafe, float vertical, float forward, CallbackInfo callbackInfo) {
        PlayerTravelEvent event = new PlayerTravelEvent(strafe, vertical, forward);
        HTRewrite.EVENT_BUS.post(event);
        if(event.isCancelled()) {
            move(MoverType.SELF, motionX, motionY, motionZ);
            callbackInfo.cancel();
        }
    }

    @Inject(method = "isPushedByWater()Z", at = @At("HEAD"), cancellable = true)
    public void isPushedByWater(CallbackInfoReturnable<Boolean> callbackInfoReturnable) {
        PlayerPushedByWaterEvent event = new PlayerPushedByWaterEvent();
        HTRewrite.EVENT_BUS.post(event);
        if(event.isCancelled())
            callbackInfoReturnable.setReturnValue(false);
    }

    @Inject(method = "applyEntityCollision", at = @At("HEAD"), cancellable = true)
    public void applyEntityCollision(Entity entity, CallbackInfo callbackInfo) {
        PlayerApplyCollisionEvent event = new PlayerApplyCollisionEvent(entity);
        HTRewrite.EVENT_BUS.post(event);
        if(event.isCancelled())
            callbackInfo.cancel();
    }
}