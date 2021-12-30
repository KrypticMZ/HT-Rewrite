package me.htrewrite.client.module.modules.movement;

import me.htrewrite.client.event.custom.CustomEvent;
import me.htrewrite.client.event.custom.networkmanager.NetworkPacketEvent;
import me.htrewrite.client.event.custom.player.PlayerTravelEvent;
import me.htrewrite.client.module.Module;
import me.htrewrite.client.module.ModuleType;
import me.htrewrite.exeterimports.mcapi.settings.ToggleableSetting;
import me.htrewrite.exeterimports.mcapi.settings.ValueSetting;
import me.zero.alpine.fork.listener.EventHandler;
import me.zero.alpine.fork.listener.Listener;
import net.minecraft.entity.Entity;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.server.SPacketPlayerPosLook;
import net.minecraft.util.math.MathHelper;

public class ElytraFly2Module extends Module {
    public static final ToggleableSetting hover = new ToggleableSetting("Hover", null, true);
    public static final ToggleableSetting autoOpen = new ToggleableSetting("AutoOpen", null, true);
    public static final ValueSetting<Double> speed = new ValueSetting<>("Speed", null, 1.8d, 0d, 10d);
    public static final ValueSetting<Double> downSpeed = new ValueSetting<>("DownSpeed", null, 2d, 0d, 10d);
    public static final ValueSetting<Double> sinkSpeed = new ValueSetting<>("SinkSpeed", null, .001d, 0d, 10d);

    private double hoverTarget;
    private boolean hoverState;

    public static boolean flyUp = false;
    public static boolean autoFly = false;
    public float packetYaw;

    public ElytraFly2Module() {
        super("ElytraFly2", "ElytraFly two :)", ModuleType.Movement, 0);
        addOption(hover);
        addOption(autoOpen);
        addOption(speed);
        addOption(downSpeed);
        addOption(sinkSpeed);
        endOption();

        this.hoverTarget = -1.0D;
        this.hoverState = false;
        this.packetYaw = 0.0F;
    }

    @EventHandler
    private Listener<NetworkPacketEvent> networkPacket = new Listener<>(event -> {
        if(event.getEra() != CustomEvent.Era.PRE)
            return;

        if(!event.reading) {
            if (mc.player == null)
                return;
            if (event.getPacket() instanceof CPacketPlayer) {
                if (!mc.player.isElytraFlying())
                    return;
                final CPacketPlayer packet = (CPacketPlayer)event.getPacket();
                packet.pitch = 0.0F;
                packet.yaw = this.packetYaw;
            }
            if (event.getPacket() instanceof CPacketEntityAction) {
                final CPacketEntityAction packet = (CPacketEntityAction)event.getPacket();
                if (packet.getAction() == CPacketEntityAction.Action.START_FALL_FLYING)
                    this.hoverTarget = mc.player.posY + 0.35D;
            }
            return;
        }

        if (mc.player == null || !mc.player.isElytraFlying())
            return;
        if (event.getPacket() instanceof SPacketPlayerPosLook) {
            final SPacketPlayerPosLook packet = (SPacketPlayerPosLook)event.getPacket();
            packet.pitch = mc.player.rotationPitch;
        }
    });

    @EventHandler
    private Listener<PlayerTravelEvent> travelEventListener = new Listener<>(event -> {
        if (mc.player == null)
            return;
        if (!mc.player.isElytraFlying()) {
            if (autoOpen.isEnabled() && !mc.player.onGround && mc.player.motionY < -0.04D) {
                CPacketEntityAction packet = new CPacketEntityAction((Entity)mc.player, CPacketEntityAction.Action.START_FALL_FLYING);
                mc.player.connection.sendPacket((Packet)packet);
                mc.timer.tickLength = 200.0F;
                event.cancel();
                return;
            }
            return;
        }
        mc.timer.tickLength = 50.0F;
        if (this.hoverTarget < 0.0D)
            this.hoverTarget = mc.player.posY;
        boolean moveForward = mc.gameSettings.keyBindForward.isKeyDown();
        boolean moveBackward = mc.gameSettings.keyBindBack.isKeyDown();
        boolean moveLeft = mc.gameSettings.keyBindLeft.isKeyDown();
        boolean moveRight = mc.gameSettings.keyBindRight.isKeyDown();
        boolean moveUp = mc.gameSettings.keyBindJump.isKeyDown();
        boolean moveDown = mc.gameSettings.keyBindSneak.isKeyDown();
        float moveForwardFactor = moveForward ? 1.0F : -1.0F;
        float yawDeg = mc.player.rotationYaw;
        if (moveLeft && (moveForward || moveBackward)) yawDeg -= 40.0F * moveForwardFactor;
        else if (moveRight && (moveForward || moveBackward)) yawDeg += 40.0F * moveForwardFactor;
        else if (moveLeft) yawDeg -= 90.0F;
        else if (moveRight) yawDeg += 90.0F;
        if (moveBackward) yawDeg -= 180.0F;
        this.packetYaw = yawDeg;
        float yaw = (float)Math.toRadians(yawDeg);
        float pitch = (float)Math.toRadians(mc.player.rotationPitch);
        double d8 = Math.sqrt(mc.player.motionX * mc.player.motionX + mc.player.motionZ * mc.player.motionZ);
        if (this.hoverState) this.hoverState = (mc.player.posY < this.hoverTarget + 0.1D);
        else this.hoverState = (mc.player.posY < this.hoverTarget + 0.0D);
        boolean doHover = (this.hoverState && hover.isEnabled());
        if (moveUp || moveForward || moveBackward || moveLeft || moveRight || autoFly || AutoWalkModule.INSTANCE.isEnabled()) {
            if ((moveUp || doHover || flyUp) && d8 > 1.0D) {
                if (mc.player.motionX == 0.0D && mc.player.motionZ == 0.0D) mc.player.motionY = this.downSpeed.getValue().doubleValue();
                else {
                    double d6 = 1.0D;
                    double d10 = d8 * 0.2D * 0.04D;
                    mc.player.motionY += d10 * 3.2D;
                    mc.player.motionX -= -MathHelper.sin(yaw) * d10 / d6;
                    mc.player.motionZ -= MathHelper.cos(yaw) * d10 / d6;
                    if (d6 > 0.0D) {
                        mc.player.motionX += (-MathHelper.sin(yaw) / d6 * d8 - mc.player.motionX) * 0.3D;
                        mc.player.motionZ += (MathHelper.cos(yaw) / d6 * d8 - mc.player.motionZ) * 0.3D;
                    }
                    mc.player.motionX *= 0.9900000095367432D;
                    mc.player.motionY *= 0.9800000190734863D;
                    mc.player.motionZ *= 0.9900000095367432D;
                }
            } else {
                mc.player.motionX = -MathHelper.sin(yaw) * this.speed.getValue().doubleValue();
                mc.player.motionY = -this.sinkSpeed.getValue().doubleValue();
                mc.player.motionZ = MathHelper.cos(yaw) * this.speed.getValue().doubleValue();
            }
        } else {
            mc.player.motionX = 0.0D;
            mc.player.motionY = 0.0D;
            mc.player.motionZ = 0.0D;
        }
        if (moveDown)
            mc.player.motionY = -this.downSpeed.getValue().doubleValue();
        if (moveUp || moveDown)
            this.hoverTarget = mc.player.posY;
        event.cancel();
    });

    @Override
    public void onEnable() {
        super.onEnable();

        this.hoverTarget = -1.0D;
    }

    @Override
    public void onDisable() {
        super.onDisable();

        mc.timer.tickLength = 50.0F;
    }
}