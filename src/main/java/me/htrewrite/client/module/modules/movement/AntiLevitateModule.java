package me.htrewrite.client.module.modules.movement;

import me.htrewrite.client.event.custom.player.PlayerIsPotionActiveEvent;
import me.htrewrite.client.module.Module;
import me.htrewrite.client.module.ModuleType;
import me.zero.alpine.fork.listener.EventHandler;
import me.zero.alpine.fork.listener.Listener;
import net.minecraft.init.MobEffects;

public class AntiLevitateModule extends Module {
    public AntiLevitateModule() {
        super("AntiLevitate", "It prevents levitation effect.", ModuleType.Movement, 0);
        endOption();
    }

    @EventHandler
    private Listener<PlayerIsPotionActiveEvent> potionActiveEventListener = new Listener<>(event -> {
        if(event.potion == MobEffects.LEVITATION)
            event.cancel();
    });
}