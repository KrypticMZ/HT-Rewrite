package me.htrewrite.client.module.modules.movement;

import me.htrewrite.client.event.custom.player.PlayerUpdateEvent;
import me.htrewrite.client.module.Module;
import me.htrewrite.client.module.ModuleType;
import me.htrewrite.exeterimports.mcapi.settings.ValueSetting;
import me.zero.alpine.fork.listener.EventHandler;
import me.zero.alpine.fork.listener.Listener;
import net.minecraft.entity.Entity;
import net.minecraft.util.MovementInput;

public class EntitySpeedModule extends Module {
    public static final ValueSetting<Double> speed = new ValueSetting<>("Speed", null, 1d, .1d, 20d);

    public EntitySpeedModule() {
        super("EntitySpeed", "Make entities go faster while riding them.", ModuleType.Movement, 0);
        addOption(speed);
        endOption();
    }

    @EventHandler
    private Listener<PlayerUpdateEvent> eventListener = new Listener<>(event -> {
        if(!mc.player.isRiding())
            return;

        MovementInput movementInput = mc.player.movementInput;
        double forward = movementInput.moveForward;
        double strafe = movementInput.moveStrafe;
        float yaw = mc.player.rotationYaw;
        final Entity ridingEntity = mc.player.getRidingEntity();

        if(forward == 0d && strafe == 0d) {
            ridingEntity.motionX = 0d;
            ridingEntity.motionZ = 0d;
        } else {
            final double cos = Math.cos(Math.toRadians(yaw + 90.0F));
            final double sin = Math.sin(Math.toRadians(yaw + 90.0F));

            if(forward != 0d) {
                yaw += ((strafe > 0d)?(forward > 0d ? -45 : 45):(forward > 0d ? 45 : -45));
                strafe = 0d;
                forward = ((forward > 0d)?1d:(forward<0d)?-1d:forward);
            }
            ridingEntity.motionX = (forward * speed.getValue().doubleValue() * cos + strafe * speed.getValue().doubleValue() * sin);
            ridingEntity.motionZ = (forward * speed.getValue().doubleValue() * sin - strafe * speed.getValue().doubleValue() * cos);
        }
    });
}