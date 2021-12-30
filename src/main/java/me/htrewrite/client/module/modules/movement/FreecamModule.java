package me.htrewrite.client.module.modules.movement;

import me.htrewrite.client.event.custom.CustomEvent;
import me.htrewrite.client.event.custom.networkmanager.NetworkPacketEvent;
import me.htrewrite.client.event.custom.player.PlayerTravelEvent;
import me.htrewrite.client.event.custom.player.PlayerUpdateEvent;
import me.htrewrite.client.module.Module;
import me.htrewrite.client.module.ModuleType;
import me.htrewrite.client.util.MathUtil;
import me.htrewrite.exeterimports.mcapi.settings.ToggleableSetting;
import me.htrewrite.exeterimports.mcapi.settings.ValueSetting;
import me.zero.alpine.fork.listener.EventHandler;
import me.zero.alpine.fork.listener.Listener;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.Entity;
import net.minecraft.network.play.client.CPacketInput;
import net.minecraft.network.play.client.CPacketPlayer;

public class FreecamModule extends Module {
    ToggleableSetting cancel_packets = new ToggleableSetting("CancelPackets", null, true);
    ValueSetting<Double> speed = new ValueSetting<>("Speed", null, .5d, .1d, 5d);

    private double posX;
    private double posY;
    private double posZ;
    private float pitch;
    private float yaw;
    private EntityOtherPlayerMP clonedPlayer;
    private boolean isRidingEntity;
    private Entity ridingEntity;
    public FreecamModule() {
        super("Freecam", "Allows you to move freely without moving.", ModuleType.Movement, 0);
        addOption(cancel_packets);
        addOption(speed);
        endOption();
    }

    @EventHandler
    private Listener<PlayerUpdateEvent> updateEventListener = new Listener<>(event -> {
        mc.player.noClip = true;
        mc.player.setVelocity(0, 0, 0);
        mc.player.jumpMovementFactor = speed.getValue().floatValue();
        double[] dir = MathUtil.directionSpeed(speed.getValue());
        if (mc.player.movementInput.moveStrafe != 0 || mc.player.movementInput.moveForward != 0) {
            mc.player.motionX = dir[0];
            mc.player.motionZ = dir[1];
        } else {
            mc.player.motionX = 0;
            mc.player.motionZ = 0;
        }

        mc.player.setSprinting(false);

        if (mc.gameSettings.keyBindJump.isKeyDown()) {
            mc.player.motionY += speed.getValue();
        } else if (mc.gameSettings.keyBindSneak.isKeyDown()) {
            mc.player.motionY -= speed.getValue();
        }
    });

    @Override
    public void onEnable() {
        super.onEnable();

        if (mc.player != null) {
            this.isRidingEntity = (mc.player.getRidingEntity() != null);

            if (mc.player.getRidingEntity() == null) {
                this.posX = mc.player.posX;
                this.posY = mc.player.posY;
                this.posZ = mc.player.posZ;
            } else {
                this.ridingEntity = mc.player.getRidingEntity();
                mc.player.dismountRidingEntity();
            }

            this.pitch = mc.player.rotationPitch;
            this.yaw = mc.player.rotationYaw;

            (this.clonedPlayer = new EntityOtherPlayerMP(mc.world, mc.getSession().getProfile())).copyLocationAndAnglesFrom((Entity)mc.player);
            this.clonedPlayer.rotationYawHead = mc.player.rotationYawHead;

            mc.world.addEntityToWorld(-100, (Entity)this.clonedPlayer);
            mc.player.noClip = true;
        }
    }

    @Override
    public void onDisable() {
        super.onDisable();

        if (mc.player != null) {
            mc.player.setPositionAndRotation(this.posX, this.posY, this.posZ, this.yaw, this.pitch);
            mc.world.removeEntityFromWorld(-100);
            this.clonedPlayer = null;

            this.posX = 0d;
            this.posY = 0d;
            this.posZ = 0d;
            this.yaw = 0f;
            this.pitch = 0f;

            mc.player.capabilities.isFlying = false;
            mc.player.capabilities.setFlySpeed(0.05f);
            mc.player.noClip = false;

            final EntityPlayerSP player = mc.player;
            final EntityPlayerSP player2 = mc.player;
            final EntityPlayerSP player3 = mc.player;
            player3.motionZ = 0d;
            player2.motionY = 0d;
            player.motionX = 0d;

            if (isRidingEntity) mc.player.startRiding(ridingEntity, true);
        }

        mc.renderGlobal.loadRenderers();
    }

    @EventHandler
    private Listener<NetworkPacketEvent> packetEventListener = new Listener<>(event -> {
        if(!event.reading && event.getEra() == CustomEvent.Era.PRE && cancel_packets.isEnabled() && (event.getPacket() instanceof CPacketPlayer || event.getPacket() instanceof CPacketInput))
            event.cancel();
    });

    @EventHandler
    private Listener<PlayerTravelEvent> travelEventListener = new Listener<>(event -> {
        mc.player.noClip = true;
    });
}