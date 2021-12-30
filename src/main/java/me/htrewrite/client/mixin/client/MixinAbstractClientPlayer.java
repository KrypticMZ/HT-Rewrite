package me.htrewrite.client.mixin.client;

import me.htrewrite.client.HTRewrite;
import me.htrewrite.client.capes.obj.CapeObj;
import me.htrewrite.client.command.commands.CapeReloadCommand;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.util.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import javax.annotation.Nullable;

@Mixin(AbstractClientPlayer.class)
public abstract class MixinAbstractClientPlayer {
    @Shadow
    @Nullable
    protected abstract NetworkPlayerInfo getPlayerInfo();

    @Inject(method = {"getLocationCape"}, at = {@At("HEAD")}, cancellable = true)
    public void getLocationCape(CallbackInfoReturnable<ResourceLocation> callbackInfoReturnable) {
        if(!CapeReloadCommand.reloading.get()) {
            NetworkPlayerInfo info = getPlayerInfo();
            CapeObj capeObj = HTRewrite.INSTANCE.getCapes().getWCapeByUsername(info.getGameProfile().getName());
            HTRewrite.INSTANCE.getCapes().debug_name = capeObj.name;
            HTRewrite.INSTANCE.getCapes().debug_fileName = capeObj.file_name;
            HTRewrite.INSTANCE.getCapes().debug_id = capeObj.id;
            HTRewrite.INSTANCE.getCapes().debug_url = capeObj.url;
            if(capeObj != null)
                callbackInfoReturnable.setReturnValue(capeObj.resourceLocation);
        }
    }
}