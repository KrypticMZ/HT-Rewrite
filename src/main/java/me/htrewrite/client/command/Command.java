package me.htrewrite.client.command;

import me.htrewrite.client.util.ChatColor;
import net.minecraft.client.Minecraft;
import net.minecraft.util.text.TextComponentString;

public abstract class Command {
    protected final Minecraft mc;
    private String alias, usage, description;

    public Command(String alias, String usage, String description) {
        mc = Minecraft.getMinecraft();
        this.alias = alias;
        this.usage = usage;
        this.description = description;
    }

    public String formatCmd(String cmd) { return CommandManager.prefix + cmd; }

    public String getAlias() { return alias; }
    public String getUsage() { return usage; }
    public String getDescription() { return description; }

    public abstract void call(String[] args);
    public void sendMessage(String message) { if(mc.player != null) mc.player.sendMessage(new TextComponentString(ChatColor.prefix_parse('&', message))); }
    public void sendInvalidUsageMessage() { sendMessage("&c"+formatCmd(alias + " " + usage)); }
}