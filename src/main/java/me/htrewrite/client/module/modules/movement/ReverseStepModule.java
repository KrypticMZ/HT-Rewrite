package me.htrewrite.client.module.modules.movement;

import me.htrewrite.client.event.custom.player.PlayerUpdateEvent;
import me.htrewrite.client.module.Module;
import me.htrewrite.client.module.ModuleType;
import me.htrewrite.exeterimports.mcapi.settings.ValueSetting;
import me.zero.alpine.fork.listener.EventHandler;
import me.zero.alpine.fork.listener.Listener;

public class ReverseStepModule extends Module {
    public static final ValueSetting<Double> blocks = new ValueSetting<Double>("Blocks", 2d, 0d, 10d);

    public ReverseStepModule() {
        super("ReverseStep", "Steps you down.", ModuleType.Movement, 0);
        addOption(blocks);
        endOption();
    }

    @EventHandler
    private Listener<PlayerUpdateEvent> updateEventListener = new Listener<>(event -> {
        if(nullCheck() || mc.player.isInLava() || mc.player.isInWater() || mc.player.isOnLadder())
            return;
        if(mc.player.onGround)
            for(double y = 0d; y < blocks.getValue() + .5; y += .01)
                if(!mc.world.getCollisionBoxes(mc.player, mc.player.getEntityBoundingBox().offset(.0, -y, .0)).isEmpty()) {
                    mc.player.motionY = -10d;
                    break;
                }
    });
}