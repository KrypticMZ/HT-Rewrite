package me.htrewrite.client;

import me.htrewrite.client.util.ChatColor;
import me.htrewrite.exeterimports.mcapi.settings.BindSetting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.util.text.TextComponentString;

public class Wrapper {
    public static Minecraft getMC() { return Minecraft.getMinecraft(); }
    public static EntityPlayerSP getPlayer() { return getMC().player; }

    public static void sendClientText(String message) {
        if(getPlayer() == null)
            return;

        getPlayer().sendMessage(new TextComponentString(ChatColor.prefix_parse('&', message)));
    }

    public static BindSetting binding = null;
}