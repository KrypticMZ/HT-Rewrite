package me.htrewrite.client.command.commands;

import me.htrewrite.client.HTRewrite;
import me.htrewrite.client.Wrapper;
import me.htrewrite.client.command.Command;
import me.htrewrite.client.module.ModuleManager;
import me.htrewrite.client.module.modules.gui.NotificationsModule;
import me.htrewrite.client.util.ChatColor;
import me.htrewrite.client.util.ConfigUtils;
import me.pk2.chatserver.ChatAPI;
import me.pk2.chatserver.clientside.objects.KeepAliveResponse;
import me.pk2.chatserver.message.Message;
import net.minecraft.client.audio.Sound;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.text.TextComponentString;

public class HTChatCommand extends Command {
    ConfigUtils authConfigUtils;
    String username;
    Thread thread;

    public HTChatCommand() {
        super("ht-chat", "<message>", "Chat for people with access to HT+Rewrite.");
        authConfigUtils = new ConfigUtils("auth", "");
        username = (String)authConfigUtils.get("u");
        HTRewrite.executorService.submit(() -> ChatAPI.handshake(username));
        thread = new Thread(()->{
            while (true) {
                KeepAliveResponse aliveResponse = ChatAPI.keepAlive();
                if(NotificationsModule.receiveChatNotifications.isEnabled()) {
                    if(mc.world == null || mc.player == null)
                        continue;
                    if(aliveResponse.queued_messages.size() > 0)
                        mc.player.playSound(SoundEvents.ENTITY_ARROW_HIT_PLAYER, 1, 2);
                    for (Message msg : aliveResponse.queued_messages)
                        Wrapper.sendClientText("&d" + msg.user.username + " &7&l-> &r" + msg.message);
                }
                try { Thread.sleep(5000); } catch (Exception exception) { exception.printStackTrace(); }
            }
        });
        thread.start();
    }

    @Override
    public void call(String[] args) {
        if(!NotificationsModule.receiveChatNotifications.isEnabled() || !ModuleManager.notificationsModule.isEnabled()) {
            Wrapper.sendClientText("&cHT+Chat is disabled, please enable it on Notifications Module!");
            return;
        }

        if(args.length < 1) {
            mc.player.sendMessage(new TextComponentString(ChatColor.prefix_parse('&', "&c"+formatCmd(getAlias() + " " + getUsage()))));
            return;
        }
        StringBuilder builder = new StringBuilder();
        for(int i = 0; i < args.length; i++)
            builder.append(args[i]+(i==args.length-1?"":" "));
        final String message = builder.toString();
        HTRewrite.executorService.submit(() -> {
            ChatAPI.sendMessage(message);
            mc.player.playSound(SoundEvents.BLOCK_LEVER_CLICK, 1, 2);

            KeepAliveResponse aliveResponse = ChatAPI.keepAlive();
            if(mc.world == null || mc.player == null)
                return;
            if(aliveResponse.queued_messages.size() > 1)
                mc.player.playSound(SoundEvents.ENTITY_ARROW_HIT_PLAYER, 1, 2);
            for(Message msg : aliveResponse.queued_messages)
                    mc.player.sendMessage(new TextComponentString(ChatColor.prefix_parse('&', "&d" + msg.user.username + " &7&l-> &r" + msg.message)));
        });
    }
}