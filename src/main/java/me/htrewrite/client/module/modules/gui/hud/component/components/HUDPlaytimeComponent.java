package me.htrewrite.client.module.modules.gui.hud.component.components;

import me.htrewrite.client.module.modules.gui.hud.component.HUDComponent;
import me.htrewrite.client.util.ColorUtils;
import me.htrewrite.client.util.RenderUtils;
import net.minecraftforge.client.event.RenderGameOverlayEvent;

import java.awt.*;
import java.time.Duration;

public class HUDPlaytimeComponent implements HUDComponent {
    private long time = System.currentTimeMillis();
    public void reset() {
        this.time = System.currentTimeMillis();
    }


    @Override
    public void render(RenderGameOverlayEvent.Text event, int x, int y) {
        int colorRect = ColorUtils.color(0.0F, 0.0F, 0.0F, 0.0F);

        Duration duration = Duration.ofMillis(System.currentTimeMillis()-time);
        long seconds = duration.getSeconds();
        long hours = seconds/3600;
        long minutes = (seconds%3600)/60;
        long secondss = seconds%60;

        String playtime = (hours>0? hours+"h ":"") + (minutes>0? minutes+"m ":"") + secondss+"s";

        RenderUtils.drawStringWithRect("Playtime: " + playtime, x, y, Color.WHITE.getRGB(), colorRect, colorRect);

    }
}
