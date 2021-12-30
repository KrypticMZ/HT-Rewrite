package me.htrewrite.client.command.commands;

import me.htrewrite.client.HTRewrite;
import me.htrewrite.client.command.Command;
import me.htrewrite.client.manager.FriendManager;
import me.htrewrite.client.util.ChatColor;
import net.minecraft.util.text.TextComponentString;

public class FriendCommand extends Command {
    FriendManager friendManager;

    public FriendCommand() {
        super("friend", "[<[add/remove]> <user>] [\"list\"]", "Friend command lol.");

        friendManager = HTRewrite.INSTANCE.getFriendManager();
    }

    @Override
    public void call(String[] args) {
        if(args.length < 1) {
            mc.player.sendMessage(new TextComponentString(ChatColor.prefix_parse('&', "&cInvalid usage! &l" + formatCmd(getAlias() + " " + getUsage()))));
            return;
        }
        if(args.length > 1) {
            if(args[0].equalsIgnoreCase("add")) {
                String user = args[1];
                friendManager.addFriend(user);
                mc.player.sendMessage(new TextComponentString(ChatColor.prefix_parse('&', "&aAdded friend &l'" + user + "'")));
            } else if(args[0].equalsIgnoreCase("remove")) {
                String user = args[1];
                friendManager.remFriend(user);
                mc.player.sendMessage(new TextComponentString(ChatColor.prefix_parse('&', "&cRemoved friend &l'" + user + "'")));
            } else {
                mc.player.sendMessage(new TextComponentString(ChatColor.prefix_parse('&', "&cInvalid usage! &l" + formatCmd(getAlias() + " " + getUsage()))));
                return;
            }

            return;
        }

        if(args[0].equalsIgnoreCase("list")) {
            StringBuilder msg = new StringBuilder();
            msg.append(ChatColor.prefix_parse('&', "&bFriend list:\n"));
            for(Object object : friendManager.getFriends())
                msg.append(ChatColor.prefix_parse('&', "   &e" + (String)object + "\n"));

            mc.player.sendMessage(new TextComponentString(msg.toString()));
        } else {
            mc.player.sendMessage(new TextComponentString(ChatColor.prefix_parse('&', "&cInvalid usage! &l" + formatCmd(getAlias() + " " + getUsage()))));
            return;
        }
    }
}