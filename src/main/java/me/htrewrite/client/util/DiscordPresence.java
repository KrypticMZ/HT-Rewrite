package me.htrewrite.client.util;

import club.minnced.discord.rpc.DiscordEventHandlers;
import club.minnced.discord.rpc.DiscordRPC;
import club.minnced.discord.rpc.DiscordRichPresence;
import me.htrewrite.client.HTRewrite;
import me.htrewrite.client.customgui.CustomMainMenuGui;
import me.htrewrite.client.module.modules.gui.DiscordRPCModule;
import me.htrewrite.client.module.modules.render.IbaiModule;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiMainMenu;

public class DiscordPresence {
    public static final DiscordRichPresence richPresence = new DiscordRichPresence();
    private static final DiscordRPC discordRPC = DiscordRPC.INSTANCE;
    private static Thread thread;

    public static String generateState() {
        if(Minecraft.getMinecraft().player == null)
            return "";

        switch(DiscordRPCModule.mode.getValue()) {
            case "Movement": return (Minecraft.getMinecraft().player.moveForward > 0f || Minecraft.getMinecraft().player.moveStrafing > 0f)?"I'm moving rn.":"I'm idle.";
            case "Discord": return "https://discord.gg/7f6DqJs2vr";
            case "Custom": return DiscordRPCModule.customState.getValue();
        } return "";
    }

    public static void setPresence() {
        richPresence.details = (Minecraft.getMinecraft().currentScreen instanceof CustomMainMenuGui || Minecraft.getMinecraft().currentScreen instanceof GuiMainMenu)?"On the Main Menu!":"Playing " + (Minecraft.getMinecraft().getCurrentServerData() == null?"on a singleplayer world!":"on a multiplayer server!");
        richPresence.state = generateState();
        richPresence.largeImageKey = HTRewrite.INSTANCE.getModuleManager().getModuleByClass(IbaiModule.class).isEnabled() ? "ibai_truco" : "logo";
        richPresence.largeImageText = HTRewrite.NAME+" "+HTRewrite.VERSION;
        richPresence.smallImageKey = "nomason";
        richPresence.smallImageText = "PK2Technology";
    }

    public static void boot() {
        DiscordEventHandlers eventHandlers = new DiscordEventHandlers();
        discordRPC.Discord_Initialize("845029478790332426", eventHandlers, true, "");
        richPresence.startTimestamp = System.currentTimeMillis() / 1000L;
        setPresence();
        discordRPC.Discord_UpdatePresence(richPresence);
        thread = new Thread(() -> {
            while (!Thread.currentThread().isInterrupted()) {
                discordRPC.Discord_RunCallbacks();
                setPresence();
                discordRPC.Discord_UpdatePresence(richPresence);
                try { Thread.sleep(2000); } catch (Exception exception) {}
            }
        }, "RPC-Callback-Handler");
        thread.start();
    }

    public static void terminate() {
        if(thread != null && !thread.isInterrupted())
            thread.interrupt();

        discordRPC.Discord_Shutdown();
    }
}