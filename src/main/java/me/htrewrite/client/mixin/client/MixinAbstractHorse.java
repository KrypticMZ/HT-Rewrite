package me.htrewrite.client.mixin.client;

import me.htrewrite.client.HTRewrite;
import me.htrewrite.client.event.custom.entity.HorseSaddledEvent;
import me.htrewrite.client.event.custom.entity.SteerEntityEvent;
import net.minecraft.entity.passive.AbstractHorse;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AbstractHorse.class)
public class MixinAbstractHorse {
    @Inject(method = "canBeSteered", at = @At("HEAD"), cancellable = true)
    public void canBeSteered(CallbackInfoReturnable<Boolean> callbackInfoReturnable) {
        SteerEntityEvent event = new SteerEntityEvent();
        HTRewrite.EVENT_BUS.post(event);

        if(event.isCancelled()) {
            callbackInfoReturnable.cancel();
            callbackInfoReturnable.setReturnValue(true);
        }
    }

    @Inject(method = "isHorseSaddled", at = @At("HEAD"), cancellable = true)
    public void isHorseSaddled(CallbackInfoReturnable<Boolean> callbackInfoReturnable) {
        HorseSaddledEvent event = new HorseSaddledEvent();
        HTRewrite.EVENT_BUS.post(event);

        if(event.isCancelled()) {
            callbackInfoReturnable.cancel();
            callbackInfoReturnable.setReturnValue(true);
        }
    }
}