package me.htrewrite.client.util;

import me.htrewrite.client.HTRewrite;
import me.zero.alpine.fork.listener.EventHandler;
import me.zero.alpine.fork.listener.Listenable;
import me.zero.alpine.fork.listener.Listener;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class TickedTimer implements Listenable {
    private int ticks = 0;
    public void reset() { ticks = 0; }
    public boolean passed(int ticks) { return this.ticks >= ticks; }
    public void start() { ticks = 0; HTRewrite.EVENT_BUS.subscribe(this); }
    public void stop() { HTRewrite.EVENT_BUS.unsubscribe(this); }

    @EventHandler
    private Listener<TickEvent.ClientTickEvent> tickEventListener = new Listener<>(event -> {
        ticks++;
    });

    public TickedTimer() { start(); }

    @Override protected void finalize() { HTRewrite.EVENT_BUS.unsubscribe(this); }
}