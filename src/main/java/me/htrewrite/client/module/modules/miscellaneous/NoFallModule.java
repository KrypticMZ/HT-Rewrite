package me.htrewrite.client.module.modules.miscellaneous;

import me.htrewrite.client.event.custom.player.PlayerUpdateEvent;
import me.htrewrite.client.module.Module;
import me.htrewrite.client.module.ModuleType;
import me.zero.alpine.fork.listener.EventHandler;
import me.zero.alpine.fork.listener.Listener;
import net.minecraft.network.play.client.CPacketPlayer;

public class NoFallModule extends Module {
    public NoFallModule() {
        super("NoFall", "Prevents fall damage.", ModuleType.Miscellaneous, 0);
        endOption();
    }

    @EventHandler
    private Listener<PlayerUpdateEvent> updateEventListener = new Listener<>(event -> {
        if(mc.player.fallDistance > 1f)
            mc.player.connection.sendPacket(new CPacketPlayer(true));
    });
}