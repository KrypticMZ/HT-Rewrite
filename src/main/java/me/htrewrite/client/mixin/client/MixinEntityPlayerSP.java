package me.htrewrite.client.mixin.client;

import me.htrewrite.client.HTRewrite;
import me.htrewrite.client.event.custom.CustomEvent;
import me.htrewrite.client.event.custom.player.*;
import me.htrewrite.client.module.modules.world.PortalModule;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.MoverType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EntityPlayerSP.class)
public abstract class MixinEntityPlayerSP {
    @Inject(method = "onUpdate", at = @At("HEAD"), cancellable = true)
    public void onUpdate(CallbackInfo callbackInfo) {
        PlayerUpdateEvent playerUpdateEvent = new PlayerUpdateEvent();
        playerUpdateEvent.setEra(CustomEvent.Era.PRE);
        HTRewrite.EVENT_BUS.post(playerUpdateEvent);
        if(playerUpdateEvent.isCancelled())
            callbackInfo.cancel();
    }

    @Inject(method = "onUpdateWalkingPlayer", at = @At("HEAD"), cancellable = true)
    public void onUpdateWalkingPlayer(CallbackInfo callbackInfo) {
        PlayerMotionUpdateEvent playerMotionUpdateEvent = new PlayerMotionUpdateEvent(CustomEvent.Era.PRE);
        HTRewrite.EVENT_BUS.post(playerMotionUpdateEvent);
        if(playerMotionUpdateEvent.isCancelled())
            callbackInfo.cancel();
    }

    @Inject(method = "onUpdateWalkingPlayer", at = @At("RETURN"), cancellable = true)
    public void onUpdateWalkingPlayerRet(CallbackInfo callbackInfo) {
        PlayerMotionUpdateEvent playerMotionUpdateEvent = new PlayerMotionUpdateEvent(CustomEvent.Era.POST);
        HTRewrite.EVENT_BUS.post(playerMotionUpdateEvent);
        if(playerMotionUpdateEvent.isCancelled())
            callbackInfo.cancel();
    }

    @Inject(method = "sendChatMessage", at = @At("HEAD"), cancellable = true)
    public void onMessageEvent(String message, CallbackInfo callbackInfo) {
        PlayerChatEvent playerChatEvent = new PlayerChatEvent(message);
        HTRewrite.EVENT_BUS.post(playerChatEvent);
        if(playerChatEvent.isCancelled())
            callbackInfo.cancel();
    }

    @Inject(method = "pushOutOfBlocks(DDD)Z", at = @At("HEAD"), cancellable = true)
    public void pushOutOfBlocks(double x, double y, double z, CallbackInfoReturnable<Boolean> callbackInfo) {
        PlayerPushOutOfBlocksEvent event = new PlayerPushOutOfBlocksEvent(x, y, z);
        HTRewrite.EVENT_BUS.post(event);
        if(event.isCancelled())
            callbackInfo.setReturnValue(false);
    }

    @Inject(method={"onUpdateWalkingPlayer"}, at={@At(value="HEAD")}, cancellable=true)
    private void preUpdateWalkingPlayer(CallbackInfo callbackInfo) {
        UpdateWalkingPlayerEvent event = new UpdateWalkingPlayerEvent(CustomEvent.Era.PRE);
        HTRewrite.EVENT_BUS.post(event);
        if(event.isCancelled())
            callbackInfo.cancel();
    }

    @Inject(method={"onUpdateWalkingPlayer"}, at={@At(value="RETURN")})
    private void postUpdateWalkingPlayer(CallbackInfo callbackInfo) {
        UpdateWalkingPlayerEvent event = new UpdateWalkingPlayerEvent(CustomEvent.Era.POST);
        HTRewrite.EVENT_BUS.post(event);
    }

    @Inject(method = "move", at = @At("HEAD"), cancellable = true)
    public void move(MoverType type, double x, double y, double z, CallbackInfo callbackInfo) {
        PlayerMoveEvent event = new PlayerMoveEvent(type, x, y, z);
        HTRewrite.EVENT_BUS.post(event);
        if(event.isCancelled())
            callbackInfo.cancel();
    }

    @Redirect(method = {"onLivingUpdate"}, at=@At(value = "INVOKE", target = "Lnet/minecraft/client/entity/EntityPlayerSP;closeScreen()V"))
    public void closeScreenRedirect(EntityPlayerSP entityPlayerSP) {
        if(!PortalModule.INSTANCE.isEnabled() || !PortalModule.chat.isEnabled())
            entityPlayerSP.closeScreen();
    }

    @Redirect(method={"onLivingUpdate"}, at=@At(value="INVOKE", target="Lnet/minecraft/client/Minecraft;displayGuiScreen(Lnet/minecraft/client/gui/GuiScreen;)V"))
    public void displayGuiScreenRedirect(Minecraft mc, GuiScreen screen) {
        if(!PortalModule.INSTANCE.isEnabled() || !PortalModule.chat.isEnabled())
            mc.displayGuiScreen(screen);
    }
}