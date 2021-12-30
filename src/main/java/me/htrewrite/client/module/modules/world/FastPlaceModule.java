package me.htrewrite.client.module.modules.world;

import me.htrewrite.client.event.custom.player.PlayerUpdateEvent;
import me.htrewrite.client.module.Module;
import me.htrewrite.client.module.ModuleType;
import me.zero.alpine.fork.listener.EventHandler;
import me.zero.alpine.fork.listener.Listener;

public class FastPlaceModule extends Module {
    public FastPlaceModule() {
        super("FastPlace", "Place blocks fast.", ModuleType.World, 0);
        endOption();
    }

    @Override
    public void onDisable() {
        super.onDisable();

        mc.rightClickDelayTimer = 6;
    }

    @EventHandler private Listener<PlayerUpdateEvent> updateEventListener = new Listener<>(event -> mc.rightClickDelayTimer = 0);
}