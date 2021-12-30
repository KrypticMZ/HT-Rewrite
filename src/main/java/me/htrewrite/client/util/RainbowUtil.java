package me.htrewrite.client.util;

import me.htrewrite.client.HTRewrite;
import me.htrewrite.client.event.custom.player.PlayerUpdateEvent;
import me.zero.alpine.fork.listener.EventHandler;
import me.zero.alpine.fork.listener.Listenable;
import me.zero.alpine.fork.listener.Listener;

import java.awt.*;

public class RainbowUtil implements Listenable {
    private int r, g, b;

    public RainbowUtil() {
        r = g = b = 0;

        HTRewrite.EVENT_BUS.subscribe(this);
    }

    @EventHandler
    private Listener<PlayerUpdateEvent> updateEventListener = new Listener<>(event -> {
        float colorTick = (System.currentTimeMillis() % (360 * 32)) / (360f * 32);
        int rgbColor = Color.HSBtoRGB(colorTick, .8f, .8f);

        r = ((rgbColor >> 16) & 0xFF);
        g = ((rgbColor >> 8) & 0xFF);
        b = (rgbColor & 0xFF);
    });

    public int getR() { return r; }
    public int getG() { return g; }
    public int getB() { return b; }
    
    public static Color getRainbow(int Offset) {
        return ColourUtil.fromHSB((System.currentTimeMillis() + Offset % (360 * 32)) / (360f * 32), 1, 1);
    }
    
}
