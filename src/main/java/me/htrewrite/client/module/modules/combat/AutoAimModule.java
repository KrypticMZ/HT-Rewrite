package me.htrewrite.client.module.modules.combat;

import me.htrewrite.client.HTRewrite;
import me.htrewrite.client.event.custom.player.PlayerMotionUpdateEvent;
import me.htrewrite.client.event.custom.player.PlayerUpdateEvent;
import me.htrewrite.client.module.Module;
import me.htrewrite.client.module.ModuleType;
import me.htrewrite.client.util.MathUtil;
import me.htrewrite.exeterimports.mcapi.settings.ToggleableSetting;
import me.htrewrite.exeterimports.mcapi.settings.ValueSetting;
import me.zero.alpine.fork.listener.EventHandler;
import me.zero.alpine.fork.listener.Listener;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;

import java.util.Comparator;

public class AutoAimModule extends Module {
    public static final ValueSetting<Double> range = new ValueSetting<>("Range", null, 30d, 0d, 50d);
    public static final ToggleableSetting friends = new ToggleableSetting("Friends", null, false);

    private Entity target;
    private float[] rotation = new float[2];
    public AutoAimModule() {
        super("AutoAim", "Aims automatically at players. (colisseum.net)", ModuleType.Combat, 0);
        addOption(range);
        addOption(friends);
        endOption();
    }

    @Override
    public void onEnable() {
        super.onEnable();
        if(mc.player == null) {
            toggle();
            return;
        }

        rotation[0] = mc.player.rotationYaw;
        rotation[1] = mc.player.rotationPitch;
    }

    private boolean isValidEntity(Entity entity) {
        boolean friendCheck = true;
        if(HTRewrite.INSTANCE.getFriendManager().isFriend(entity.getName()))
            if(!friends.isEnabled())
                friendCheck = false;

        return (entity instanceof EntityPlayer) && mc.player != entity && mc.player.getDistance(entity)<= range.getValue().intValue() && friendCheck;
    }

    @EventHandler
    private Listener<PlayerUpdateEvent> updateEventListener = new Listener<>(event -> {
        target = mc.world.loadedEntityList.stream()
                .filter(loadedEntity -> isValidEntity(loadedEntity))
                .min(Comparator.comparing(loadedEntity -> mc.player.getDistance(loadedEntity.getPosition().getX(), loadedEntity.getPosition().getY(), loadedEntity.getPosition().getZ())))
                .orElse(null);
        if(target == null)
            return;

        rotation = MathUtil.calcAngle(mc.player.getPositionEyes(mc.getRenderPartialTicks()), target.getPositionEyes(mc.getRenderPartialTicks()));
    });

    @EventHandler
    private Listener<PlayerMotionUpdateEvent> motionUpdateEventListener = new Listener<>(event -> {
        if(target == null)
            return;
        mc.player.rotationYawHead = rotation[0];
        mc.player.rotationYaw = rotation[0];
        mc.player.rotationPitch = rotation[1];
    });
}