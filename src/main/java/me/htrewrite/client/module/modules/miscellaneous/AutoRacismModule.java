package me.htrewrite.client.module.modules.miscellaneous;

import me.htrewrite.client.event.custom.player.PlayerUpdateEvent;
import me.htrewrite.client.module.Module;
import me.htrewrite.client.module.ModuleType;
import me.htrewrite.client.util.TickedTimer;
import me.htrewrite.exeterimports.mcapi.settings.ValueSetting;
import me.zero.alpine.fork.listener.EventHandler;
import me.zero.alpine.fork.listener.Listener;

public class AutoRacismModule extends Module {
    public static final ValueSetting<Double> delay = new ValueSetting<>("Delay", null, 1d, 1d, 60d);

    private TickedTimer tickedTimer;
    public AutoRacismModule() {
        super("AutoRacism", "I hate black people.", ModuleType.Miscellaneous, 0);
        addOption(delay);
        endOption();

        tickedTimer = new TickedTimer();
        tickedTimer.stop();
    }

    @Override
    public void onEnable() {
        super.onEnable();

        tickedTimer.reset();
        tickedTimer.start();
    }

    @Override
    public void onDisable() {
        super.onDisable();

        tickedTimer.stop();
    }

    @EventHandler
    private Listener<PlayerUpdateEvent> updateEventListener = new Listener<>(event -> {
        int realDelay = delay.getValue().intValue()*20;
        if(tickedTimer.passed(realDelay)) {
            mc.player.sendChatMessage("I hate black people <[HT+Rewrite]>");
            tickedTimer.reset();
        }
    });
}