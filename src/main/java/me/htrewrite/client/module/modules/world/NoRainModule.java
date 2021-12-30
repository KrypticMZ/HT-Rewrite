package me.htrewrite.client.module.modules.world;

import me.htrewrite.client.event.custom.world.WorldRainStrengthEvent;
import me.htrewrite.client.module.Module;
import me.htrewrite.client.module.ModuleType;
import me.htrewrite.exeterimports.mcapi.settings.ToggleableSetting;
import me.htrewrite.exeterimports.mcapi.settings.ValueSetting;
import me.zero.alpine.fork.listener.EventHandler;
import me.zero.alpine.fork.listener.Listener;

public class NoRainModule extends Module {
    public static final ToggleableSetting modify = new ToggleableSetting("Modify", false);
    public static final ValueSetting<Double> newStrength = new ValueSetting<>("Strength", 0d, 0d, 1d);

    public NoRainModule() {
        super("NoRain", "Stops rain client-side.", ModuleType.World, 0);
        addOption(modify);
        addOption(newStrength.setVisibility(v -> modify.isEnabled()));
        endOption();
    }

    @EventHandler
    private Listener<WorldRainStrengthEvent> worldRainStrengthEventListener = new Listener<>(event -> {
        if(mc.world == null)
            return;
        if(modify.isEnabled()) {
            event.strength = newStrength.getValue().floatValue();
            return;
        }

        event.cancel();
    });
}