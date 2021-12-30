package me.htrewrite.client.module.modules.miscellaneous;

import com.mojang.realmsclient.gui.ChatFormatting;
import me.htrewrite.client.module.Module;
import me.htrewrite.client.module.ModuleType;
import me.zero.alpine.fork.listener.EventHandler;
import me.zero.alpine.fork.listener.Listener;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.client.event.ClientChatReceivedEvent;

import java.text.SimpleDateFormat;
import java.util.Date;

public class TimestampsModule extends Module {
    public TimestampsModule() {
        super("Timestamps", "Prefix messages with the time.", ModuleType.Miscellaneous, 0);
        endOption();
    }

    @EventHandler
    private final Listener<ClientChatReceivedEvent> receivedEventListener = new Listener<>(event -> {
        String dateTime = new SimpleDateFormat("HH:mm").format(new Date());
        event.setMessage(new TextComponentString(ChatFormatting.RED + "<" + ChatFormatting.GRAY + dateTime + ChatFormatting.RED + ">" + ChatFormatting.RESET + " ").appendSibling(event.getMessage()));
    });
}