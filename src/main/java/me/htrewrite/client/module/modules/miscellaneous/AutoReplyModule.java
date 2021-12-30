package me.htrewrite.client.module.modules.miscellaneous;

import me.htrewrite.client.module.Module;
import me.htrewrite.client.module.ModuleType;
import me.htrewrite.exeterimports.mcapi.settings.StringSetting;
import me.zero.alpine.fork.listener.EventHandler;
import me.zero.alpine.fork.listener.Listener;
import net.minecraftforge.client.event.ClientChatReceivedEvent;

public class AutoReplyModule extends Module {
    public static final StringSetting reply = new StringSetting("Reply", null, "hi");

    public AutoReplyModule() {
        super("AutoReply", "Replies to messages.", ModuleType.Miscellaneous, 0);
        addOption(reply);
        endOption();
    }

    @EventHandler
    private Listener<ClientChatReceivedEvent> clientChatReceivedEventListener = new Listener<>(event -> {
        if(event.getMessage().getUnformattedText().contains("whispers: ") && !event.getMessage().getUnformattedText().startsWith(mc.player.getName()))
            mc.player.sendChatMessage("/r " + reply.getValue());
    });
}