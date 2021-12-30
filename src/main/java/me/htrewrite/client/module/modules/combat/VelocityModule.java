package me.htrewrite.client.module.modules.combat;

import me.htrewrite.client.event.custom.networkmanager.NetworkPacketEvent;
import me.htrewrite.client.event.custom.player.PlayerApplyCollisionEvent;
import me.htrewrite.client.event.custom.player.PlayerPushOutOfBlocksEvent;
import me.htrewrite.client.event.custom.player.PlayerPushedByWaterEvent;
import me.htrewrite.client.module.Module;
import me.htrewrite.client.module.ModuleType;
import me.htrewrite.exeterimports.mcapi.settings.ToggleableSetting;
import me.htrewrite.exeterimports.mcapi.settings.ValueSetting;
import me.zero.alpine.fork.listener.EventHandler;
import me.zero.alpine.fork.listener.Listener;
import net.minecraft.entity.Entity;
import net.minecraft.entity.projectile.EntityFishHook;
import net.minecraft.network.play.server.SPacketEntityStatus;
import net.minecraft.network.play.server.SPacketEntityVelocity;
import net.minecraft.network.play.server.SPacketExplosion;

public class VelocityModule extends Module {
    public static final ValueSetting<Double> horizontalVelocity = new ValueSetting<>("Horizontal", null, 0D, 0D, 100D);
    public static final ValueSetting<Double> verticalVelocity = new ValueSetting<>("Vertical", null, 0D, 0D, 100D);
    public static final ToggleableSetting explosions = new ToggleableSetting("Explosions", null, true);
    public static final ToggleableSetting bobbers = new ToggleableSetting("Bobbers", null, true);
    public static final ToggleableSetting noPush = new ToggleableSetting("NoPush", null, true);

    @Override public String getMeta() { return String.format("H:%s%% V:%s%%", horizontalVelocity.getValue(), verticalVelocity.getValue()); }
    public VelocityModule() {
        super("Velocity", "Modifies the velocity you take.", ModuleType.Combat, 0);
        addOption(horizontalVelocity);
        addOption(verticalVelocity);
        addOption(explosions);
        addOption(bobbers);
        addOption(noPush);
        endOption();
    }

    @EventHandler
    private Listener<PlayerPushOutOfBlocksEvent> blocksEventListener = new Listener<>(event -> {
        if(noPush.isEnabled())
            event.cancel();
    });

    @EventHandler
    private Listener<PlayerPushedByWaterEvent> playerPushedByWaterEventListener = new Listener<>(event -> {
        if(noPush.isEnabled())
            event.cancel();
    });

    @EventHandler
    private Listener<PlayerApplyCollisionEvent> applyCollisionEventListener = new Listener<>(event -> {
        if(noPush.isEnabled())
            event.cancel();
    });

    @EventHandler
    private Listener<NetworkPacketEvent> packetEventListener = new Listener<>(event -> {
        if(mc.player == null)
            return;
        if(event.getPacket() instanceof SPacketEntityStatus && bobbers.isEnabled()) {
            SPacketEntityStatus packet = (SPacketEntityStatus)event.getPacket();
            if(packet.getOpCode() == 31) {
                Entity entity = packet.getEntity(mc.world);
                if(entity != null && entity instanceof EntityFishHook) {
                    EntityFishHook fishHook = (EntityFishHook) entity;
                    if(fishHook.caughtEntity == mc.player)
                        event.cancel();
                }
            }
        }
        if(event.getPacket() instanceof SPacketEntityVelocity) {
            SPacketEntityVelocity packet = (SPacketEntityVelocity)event.getPacket();
            if(packet.getEntityID() == mc.player.getEntityId()) {
                if (horizontalVelocity.getValue().intValue() == 0 && verticalVelocity.getValue().intValue() == 0) {
                    event.cancel();
                    return;
                }
                if(horizontalVelocity.getValue().intValue() != 100) {
                    packet.motionX = packet.motionX / 100 * horizontalVelocity.getValue().intValue();
                    packet.motionZ = packet.motionZ / 100 * horizontalVelocity.getValue().intValue();
                }
                if(verticalVelocity.getValue().intValue() != 100)
                    packet.motionY = packet.motionY / 100 * verticalVelocity.getValue().intValue();
            }
        }
        if(event.getPacket() instanceof SPacketExplosion && explosions.isEnabled()) {
            SPacketExplosion packet = (SPacketExplosion)event.getPacket();
            if(horizontalVelocity.getValue().intValue() == 0 && verticalVelocity.getValue().intValue() == 0) {
                event.cancel();
                return;
            }
            if(horizontalVelocity.getValue().intValue() != 100) {
                packet.motionX = packet.motionX / 100 * horizontalVelocity.getValue().intValue();
                packet.motionZ = packet.motionZ / 100 * horizontalVelocity.getValue().intValue();
            }
            if (verticalVelocity.getValue().intValue() != 100)
                packet.motionY = packet.motionY / 100 * verticalVelocity.getValue().intValue();
        }
    });
}