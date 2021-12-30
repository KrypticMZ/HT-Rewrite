package me.htrewrite.client.module.modules.render;

import me.htrewrite.client.event.custom.player.PlayerUpdateEvent;
import me.htrewrite.client.module.Module;
import me.htrewrite.client.module.ModuleType;
import me.zero.alpine.fork.listener.EventHandler;
import me.zero.alpine.fork.listener.Listener;
import net.minecraft.init.MobEffects;
import net.minecraft.potion.PotionEffect;

public class SlowSwingModule extends Module {
    public SlowSwingModule() {
        super("SlowSwing", "Swings slowly", ModuleType.Render, 0);
        endOption();
    }

    @Override
    public void onDisable() {
        super.onDisable();

        mc.player.removePotionEffect(MobEffects.MINING_FATIGUE);
    }

    @EventHandler
    private Listener<PlayerUpdateEvent> updateEventListener = new Listener<>(event -> {
        mc.player.addPotionEffect(new PotionEffect(MobEffects.MINING_FATIGUE, 999, 6));
    });
}