package me.htrewrite.client.command.commands;

import com.sun.jna.StringArray;
import me.htrewrite.client.command.Command;
import me.htrewrite.client.module.modules.miscellaneous.ChatModule;
import me.htrewrite.client.util.ChatColor;
import me.htrewrite.client.util.StringArrayUtils;
import net.minecraft.util.text.TextComponentString;

public class ChatExceptCommand extends Command {
    public ChatExceptCommand() {
        super("chat-except", "[<'add'/'remove'> <name>]", "Add/Remove/List chat exceptions.");
    }

    @Override
    public void call(String[] args) {
        String exceptionList = ChatModule.exceptions.getValue();
        String exceptions[] = exceptionList.split(" ");

        if(args.length < 1) {
            StringBuilder builder = new StringBuilder();
            builder.append("&7Exceptions: &8[");
            for(int i = 0; i < exceptions.length; i++)
                builder.append(exceptions[i] + (exceptions.length-1==i?"]":", "));
            mc.player.sendMessage(new TextComponentString(ChatColor.prefix_parse('&', builder.toString())));
            return;
        }
        if(args.length < 2) {
            mc.player.sendMessage(new TextComponentString(ChatColor.prefix_parse('&', "&c" + formatCmd(getAlias() + " " + getUsage()))));
            return;
        }

        if(args[0].equalsIgnoreCase("add")) {
            if(StringArrayUtils.contains(exceptions, args[1], false)) {
                mc.player.sendMessage(new TextComponentString(ChatColor.prefix_parse('&', "&cThat exception already exists!")));
                return;
            }

            String[] newExceptions = StringArrayUtils.addElement(exceptions, args[1]);
            ChatModule.exceptions.setValue(String.join(" ", newExceptions));
            mc.player.sendMessage(new TextComponentString(ChatColor.prefix_parse('&', "&aAdded new exception '" + args[1] + "'")));
        } else if(args[0].equalsIgnoreCase("remove")) {
            if(!StringArrayUtils.contains(exceptions, args[1], false)) {
                mc.player.sendMessage(new TextComponentString(ChatColor.prefix_parse('&', "&cThat exception does not exist!")));
                return;
            }

            String[] newExceptions = StringArrayUtils.removeElement(exceptions, args[1], false);
            ChatModule.exceptions.setValue(String.join(" ", newExceptions));
            mc.player.sendMessage(new TextComponentString(ChatColor.prefix_parse('&', "&eRemoved exception '" + args[1] + "'")));
        } else mc.player.sendMessage(new TextComponentString(ChatColor.prefix_parse('&', "&c" + formatCmd(getAlias() + " " + getUsage()))));
    }
}