package me.htrewrite.client.command.commands;

import me.htrewrite.client.HTRewrite;
import me.htrewrite.client.command.Command;
import me.htrewrite.client.command.CommandManager;
import me.htrewrite.client.util.ChatColor;
import net.minecraft.util.text.TextComponentString;

public class PrefixCommand extends Command {
    CommandManager commandManager;

    public PrefixCommand(CommandManager manager) {
        super("prefix", "<prefix>", "Change your prefix.");

        commandManager = manager;
    }

    @Override
    public void call(String[] args) {
        if(args.length < 1) {
            mc.player.sendMessage(new TextComponentString(ChatColor.prefix_parse('&', "&cInvalid usage! &l" + getAlias() + " " + getUsage())));
            return;
        }
        commandManager.setPrefix(args[0]);
        mc.player.sendMessage(new TextComponentString(ChatColor.prefix_parse('&', "&aSet the new prefix to &l'" + args[0] + "'")));
    }
}