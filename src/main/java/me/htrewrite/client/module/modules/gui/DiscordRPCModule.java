package me.htrewrite.client.module.modules.gui;

import me.htrewrite.client.clickgui.components.buttons.settings.bettermode.BetterMode;
import me.htrewrite.client.module.Module;
import me.htrewrite.client.module.ModuleType;
import me.htrewrite.client.util.DiscordPresence;
import me.htrewrite.exeterimports.mcapi.settings.ModeSetting;
import me.htrewrite.exeterimports.mcapi.settings.StringSetting;

public class DiscordRPCModule extends Module {
    public static final ModeSetting mode = new ModeSetting("Mode", null, 0, BetterMode.construct("Movement", "Discord", "Custom"));
    public static final StringSetting customState = new StringSetting("State", null, "PK2_Stimpy#7089 big dev!1!1!!!!!!1!");

    public DiscordRPCModule() {
        super("DiscordRPC", "Shows stuff on your DiscordRPC!!!1!", ModuleType.Gui, 0);
        addOption(mode);
        addOption(customState.setVisibility(v -> mode.getValue().contentEquals("Custom")));
        endOption();
    }

    @Override
    public void onEnable() {
        super.onEnable();

        DiscordPresence.boot();
    }

    @Override
    public void onDisable() {
        super.onDisable();

        DiscordPresence.terminate();
    }

    @Override public String getMeta() { return mode.getValue(); }
}