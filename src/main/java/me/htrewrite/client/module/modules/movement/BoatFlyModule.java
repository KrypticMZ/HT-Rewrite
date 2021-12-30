package me.htrewrite.client.module.modules.movement;

import me.htrewrite.client.module.Module;
import me.htrewrite.client.module.ModuleType;
import me.htrewrite.exeterimports.mcapi.settings.ValueSetting;
import me.zero.alpine.fork.listener.EventHandler;
import me.zero.alpine.fork.listener.Listener;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class BoatFlyModule extends Module {
    public static final ValueSetting<Double> upSpeed = new ValueSetting<>("UpSpeed", 1.5d, .1d, 5d);
    public static final ValueSetting<Double> downSpeed = new ValueSetting<>("DownSpeed", .033d, .001d, 1d);

    public BoatFlyModule() {
        super("BloatFly", "Allows you to fly with boat.", ModuleType.Movement, 0);
        addOption(upSpeed);
        addOption(downSpeed);
        endOption();
    }

    @EventHandler
    private Listener<TickEvent.ClientTickEvent> tickEventListener = new Listener<>(event -> {
        if(nullCheck() || mc.player.getRidingEntity() == null)
            return;

        double velY;
        if(mc.gameSettings.keyBindJump.isKeyDown()) {
            mc.player.getRidingEntity().onGround = false;
            velY = mc.gameSettings.keyBindSpectatorOutlines.isKeyDown() ? 5d : upSpeed.getValue();
        } else velY = mc.gameSettings.keyBindSprint.isKeyDown() ? -1d : -downSpeed.getValue();

        mc.player.getRidingEntity().motionY = velY;
    });
}