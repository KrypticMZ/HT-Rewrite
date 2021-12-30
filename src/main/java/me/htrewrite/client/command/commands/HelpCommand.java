package me.htrewrite.client.command.commands;

import me.htrewrite.client.HTRewrite;
import me.htrewrite.client.Wrapper;
import me.htrewrite.client.command.Command;
import me.htrewrite.client.command.CommandManager;
import me.htrewrite.client.util.ChatColor;
import net.minecraft.util.text.TextComponentString;

public class HelpCommand extends Command {
    public HelpCommand() {
        super("help", "", "Shows all the commands.");
    }

    @Override
    public void call(String[] args) {
        StringBuilder message = new StringBuilder();
        message.append(ChatColor.prefix_parse('&', "&eCommands:\n"));
        for(Command command : HTRewrite.INSTANCE.getCommandManager().getCommands())
            message.append(ChatColor.prefix_parse('&', "   &r" + CommandManager.prefix + command.getAlias() + " " + command.getUsage() + " - &l" + command.getDescription() + "\n"));

        Wrapper.getPlayer().sendMessage(new TextComponentString(message.toString()));
    }
}