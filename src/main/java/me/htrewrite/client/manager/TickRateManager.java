package me.htrewrite.client.manager;

import me.htrewrite.client.HTRewrite;
import me.htrewrite.client.event.custom.CustomEvent;
import me.htrewrite.client.event.custom.networkmanager.NetworkPacketEvent;
import me.htrewrite.client.util.MathUtil;
import me.zero.alpine.fork.listener.EventHandler;
import me.zero.alpine.fork.listener.Listenable;
import me.zero.alpine.fork.listener.Listener;
import net.minecraft.network.play.server.SPacketTimeUpdate;
import net.minecraft.util.math.MathHelper;

public class TickRateManager implements Listenable {
    private long prevTime;
    private float[] ticks = new float[20];
    private int currentTick;

    public TickRateManager() {
        this.prevTime = -1;
        for(int i = 0, len = this.ticks.length; i < len; i++)
            ticks[i] = 0f;
        HTRewrite.EVENT_BUS.subscribe(this);
    }

    public float getTickRate() {
        int tickCount = 0;
        float tickRate = 0.0f;

        for(int i = 0; i < ticks.length; i++) {
            final float tick = ticks[i];
            if(tick > 0f) {
                tickRate+=tick;
                tickCount++;
            }
        }

        return MathHelper.clamp((tickRate/tickCount), 0f, 20f);
    }

    @EventHandler
    private Listener<NetworkPacketEvent> packetEventListener = new Listener<>(event -> {
        if(!(event.getEra() == CustomEvent.Era.PRE && event.reading))
            return;
        if(event.getPacket() instanceof SPacketTimeUpdate) {
            if(prevTime != -1) {
                ticks[currentTick % ticks.length] = MathUtil.clamp((20f / ((float)(System.currentTimeMillis()-prevTime) / 1000f)), 0f, 20f);
                currentTick++;
            }
            prevTime = System.currentTimeMillis();
        }
    });
}