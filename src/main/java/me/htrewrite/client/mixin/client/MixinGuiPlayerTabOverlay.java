package me.htrewrite.client.mixin.client;

import com.mojang.realmsclient.gui.ChatFormatting;
import me.htrewrite.client.HTRewrite;
import me.htrewrite.client.module.modules.miscellaneous.TabExpanderModule;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiPlayerTabOverlay;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.scoreboard.ScorePlayerTeam;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
import java.util.Objects;

@Mixin(GuiPlayerTabOverlay.class)
public class MixinGuiPlayerTabOverlay extends Gui {
    @Inject(method = {"getPlayerName"}, at = @At("HEAD"), cancellable = true)
    public void getPlayerName(NetworkPlayerInfo networkPlayerInfo, CallbackInfoReturnable<String> infoReturnable) {
        String name = networkPlayerInfo.getDisplayName() != null ? networkPlayerInfo.getDisplayName().getFormattedText() : ScorePlayerTeam.formatPlayerName(networkPlayerInfo.getPlayerTeam(), networkPlayerInfo.getGameProfile().getName());
        if(HTRewrite.INSTANCE.getFriendManager().isFriend(name))
            infoReturnable.setReturnValue(ChatFormatting.AQUA + name);
    }

    @Redirect(method = {"renderPlayerlist"}, at = @At(value = "INVOKE", target = "Ljava/util/List;subList(II)Ljava/util/List;"))
    public List<NetworkPlayerInfo> renderPlayerList(List<NetworkPlayerInfo> list, int fromIdx, int toIdx) {
        return list.subList(fromIdx, TabExpanderModule.INSTANCE.isEnabled()?Math.min(TabExpanderModule.size.getValue().intValue(), list.size()) : toIdx);
    }
}