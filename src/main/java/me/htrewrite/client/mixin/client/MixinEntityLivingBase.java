package me.htrewrite.client.mixin.client;

import me.htrewrite.client.HTRewrite;
import me.htrewrite.client.event.custom.player.PlayerIsPotionActiveEvent;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.potion.Potion;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EntityLivingBase.class)
public abstract class MixinEntityLivingBase extends MixinEntity {
    public MixinEntityLivingBase() { super(); }

    @Inject(method = "isPotionActive", at = @At("HEAD"), cancellable = true)
    public void isPotionActive(Potion potion, final CallbackInfoReturnable<Boolean> callbackInfo) {
        PlayerIsPotionActiveEvent event = new PlayerIsPotionActiveEvent(potion);
        HTRewrite.EVENT_BUS.post(event);
        if(event.isCancelled())
            callbackInfo.setReturnValue(false);
    }

    @Shadow public void jump() { }
}