package me.htrewrite.client.module.modules.movement;

import me.htrewrite.client.event.custom.entity.HorseSaddledEvent;
import me.htrewrite.client.event.custom.entity.SteerEntityEvent;
import me.htrewrite.client.module.Module;
import me.htrewrite.client.module.ModuleType;
import me.zero.alpine.fork.listener.EventHandler;
import me.zero.alpine.fork.listener.Listener;

public class EntityControlModule extends Module {
    public EntityControlModule() {
        super("EntityControl", "Allows you to control almost any entity without a saddle.", ModuleType.Movement, 0);
        endOption();
    }

    @EventHandler
    private Listener<SteerEntityEvent> steerEntityEventListener = new Listener<>(event -> {
        event.cancel();
    });

    @EventHandler
    private Listener<HorseSaddledEvent> horseSaddledEventListener = new Listener<>(event -> {
        event.cancel();
    });
}