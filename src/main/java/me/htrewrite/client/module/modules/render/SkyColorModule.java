package me.htrewrite.client.module.modules.render;

import me.htrewrite.client.event.custom.player.PlayerUpdateEvent;
import me.htrewrite.client.module.Module;
import me.htrewrite.client.module.ModuleType;
import me.htrewrite.exeterimports.mcapi.settings.ToggleableSetting;
import me.htrewrite.exeterimports.mcapi.settings.ValueSetting;
import me.zero.alpine.fork.listener.EventHandler;
import me.zero.alpine.fork.listener.Listener;
import net.minecraftforge.client.event.EntityViewRenderEvent;

import java.awt.*;

public class SkyColorModule extends Module {
    public static final ToggleableSetting rainbow = new ToggleableSetting("Rainbow", null, false);
    public static final ValueSetting<Double> r = new ValueSetting<>("R", null, 255d, 0d, 255d);
    public static final ValueSetting<Double> g = new ValueSetting<>("G", null, 255d, 0d, 255d);
    public static final ValueSetting<Double> b = new ValueSetting<>("B", null, 255d, 0d, 255d);

    int rainbow_r = 0, rainbow_g = 0, rainbow_b = 0;
    public SkyColorModule() {
        super("SkyColor", "Changes the color of the sky.", ModuleType.Render, 0);
        addOption(rainbow);
        addOption(r.setVisibility(v -> !rainbow.isEnabled()));
        addOption(g.setVisibility(v -> !rainbow.isEnabled()));
        addOption(b.setVisibility(v -> !rainbow.isEnabled()));
        endOption();
    }

    @EventHandler
    private Listener<EntityViewRenderEvent.FogColors> fogColorsListener = new Listener<>(event -> {
        int red   = rainbow.isEnabled()?rainbow_r:r.getValue().intValue();
        int green = rainbow.isEnabled()?rainbow_g:g.getValue().intValue();
        int blue  = rainbow.isEnabled()?rainbow_b:b.getValue().intValue();

        event.setRed(red / 255f);
        event.setGreen(green / 255f);
        event.setBlue(blue / 255f);
    });

    @EventHandler
    private Listener<EntityViewRenderEvent.FogDensity> fogDensityListener = new Listener<>(event -> {
        event.setDensity(0.0f);
        event.setCanceled(true);
    });

    @EventHandler
    private Listener<PlayerUpdateEvent> updateEventListener = new Listener<>(event -> {
        if(rainbow.isEnabled())
            cycle_rainbow();
    });

    public void cycle_rainbow() {
        float tick_color = (System.currentTimeMillis() % (360 * 32)) / (360f * 32);
        int color_rgb_o = Color.HSBtoRGB(tick_color, 0.8f, 0.8f);

        rainbow_r = ((color_rgb_o >> 16) & 0xFF);
        rainbow_g = ((color_rgb_o >> 8) & 0xFF);
        rainbow_b = (color_rgb_o & 0xFF);
    }
}