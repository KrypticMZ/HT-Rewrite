package me.htrewrite.client.command.commands;

import me.htrewrite.client.HTRewrite;
import me.htrewrite.client.command.Command;
import me.htrewrite.client.util.ChatColor;
import me.pk2.chatserver.ChatAPI;
import me.pk2.chatserver.clientside.objects.KeepAliveResponse;
import me.pk2.chatserver.user.User;
import net.minecraft.util.text.TextComponentString;

public class HTListCommand extends Command {
    public HTListCommand() {
        super("ht-list", "", "List all users.");
    }

    @Override
    public void call(String[] args) {
        HTRewrite.chatExecutor.submit(() -> {
            KeepAliveResponse aliveResponse = ChatAPI.keepAlive();
            if(mc.player != null && mc.world != null)
                mc.player.sendMessage(new TextComponentString(ChatColor.prefix_parse('&', "&eList of users: ")));
            for(User user : aliveResponse.users)
                if(mc.player != null && mc.world != null && user.isAlive())
                    mc.player.sendMessage(new TextComponentString(ChatColor.prefix_parse('&', "   &b" + user.username)));
        });
    }
}