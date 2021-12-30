package me.htrewrite.client.module.modules.movement;

import me.htrewrite.client.clickgui.components.buttons.settings.bettermode.BetterMode;
import me.htrewrite.client.event.custom.CustomEvent;
import me.htrewrite.client.event.custom.networkmanager.NetworkPacketEvent;
import me.htrewrite.client.event.custom.player.PlayerMoveEvent;
import me.htrewrite.client.event.custom.player.PlayerUpdateEvent;
import me.htrewrite.client.event.custom.player.UpdateWalkingPlayerEvent;
import me.htrewrite.client.module.Module;
import me.htrewrite.client.module.ModuleType;
import me.htrewrite.client.util.Timer;
import me.htrewrite.exeterimports.mcapi.settings.ModeSetting;
import me.htrewrite.exeterimports.mcapi.settings.ToggleableSetting;
import me.htrewrite.exeterimports.mcapi.settings.ValueSetting;
import me.htrewrite.phobosimports.EntityUtil;
import me.zero.alpine.fork.listener.EventHandler;
import me.zero.alpine.fork.listener.Listener;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.init.MobEffects;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.server.SPacketPlayerPosLook;
import net.minecraft.util.MovementInput;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.util.List;
import java.util.Objects;

public class LongJumpModule extends Module {
    public static final ModeSetting mode = new ModeSetting("Mode", 1, BetterMode.construct("VIRTUE", "DIRECT", "TICK"));
    public static final ValueSetting<Double> timeout = new ValueSetting<>("TimeOut", 2d, 0d, 5d);
    public static final ValueSetting<Double> boost = new ValueSetting<>("Boost", 4.48d, 1d, 20d);
    public static final ToggleableSetting lagOff = new ToggleableSetting("LagOff", false);
    public static final ToggleableSetting autoOff = new ToggleableSetting("AutoOff", false);
    public static final ToggleableSetting step = new ToggleableSetting("Step", false);

    private final Timer timer = new Timer();

    private int stage, lastHDistance, airTicks, headStart, groundTicks;
    private double moveSpeed, lastDist;
    private boolean isSpeeding, beganJump;
    public LongJumpModule() {
        super("LongJump", "Jump longer.", ModuleType.Movement, 0);
        addOption(timeout);
        addOption(boost);
        addOption(lagOff);
        addOption(autoOff);
        addOption(step);
        endOption();
        
        beganJump = false;
    }

    private float tickLength = 50f;
    private void setTimer(float tick) { tickLength = 50f / (tick <= 0f ? .1f : tick); }
    private void normal(final UpdateWalkingPlayerEvent event) {
        if(autoOff.isEnabled() && beganJump && mc.player.onGround) {
            toggle();
            return;
        }
        switch (mode.getValue()) {
            case "VIRTUE": {
                if(mc.player.moveForward != 0f || mc.player.moveStrafing != 0f) {
                    final double xDist = mc.player.posX - mc.player.prevPosX;
                    final double zDist = mc.player.posZ - mc.player.prevPosZ;
                    lastDist = Math.sqrt(xDist * xDist + zDist * zDist);
                    break;
                }
                event.cancel();
            } break;

            case "TICK": {
                if(event != null)
                    return;
            } break;

            case "DIRECT": {
                if(EntityUtil.isInLiquid() || EntityUtil.isOnLiquid())
                    break;
                if(mc.player.onGround)
                    lastHDistance = 0;
                final float direction = mc.player.rotationYaw + ((mc.player.moveForward < 0f) ? 180 : 0) + ((mc.player.moveStrafing > 0f) ? (-90f * ((mc.player.moveForward < 0f) ? -.5f : ((mc.player.moveForward > 0f) ? .5f : 1f))) : 0f) - ((mc.player.moveStrafing < 0f) ? (-90f * ((mc.player.moveForward < 0f) ? -.5f : ((mc.player.moveForward > 0f) ? .5f : 1f))) : 0f);
                final float xDir = (float) Math.cos((direction + 90.0f) * 3.141592653589793 / 180.0);
                final float zDir = (float) Math.sin((direction + 90.0f) * 3.141592653589793 / 180.0);
                if(!mc.player.collidedVertically) {
                    airTicks++;
                    isSpeeding = true;
                    if(mc.gameSettings.keyBindSneak.isKeyDown())
                        mc.player.connection.sendPacket(new CPacketPlayer.Position(0d, 2.147483647E9d, 0d, false));
                    groundTicks = 0;
                    if(!mc.player.collidedVertically) {
                        if (mc.player.motionY == -0.07190068807140403)
                            mc.player.motionY *= 0.3499999940395355;
                        else if (mc.player.motionY == -0.10306193759436909)
                            mc.player.motionY *= 0.550000011920929;
                        else if (mc.player.motionY == -0.13395038817442878)
                            mc.player.motionY *= 0.6700000166893005;
                        else if (mc.player.motionY == -0.16635183030382)
                            mc.player.motionY *= 0.6899999976158142;
                        else if (mc.player.motionY == -0.19088711097794803)
                            mc.player.motionY *= 0.7099999785423279;
                        else if (mc.player.motionY == -0.21121925191528862)
                            mc.player.motionY *= 0.20000000298023224;
                        else if (mc.player.motionY == -0.11979897632390576)
                            mc.player.motionY *= 0.9300000071525574;
                        else if (mc.player.motionY == -0.18758479151225355)
                            mc.player.motionY *= 0.7200000286102295;
                        else if (mc.player.motionY == -0.21075983825251726)
                            mc.player.motionY *= 0.7599999904632568;
                        if (mc.player.motionY < -0.2 && mc.player.motionY > -0.24)
                            mc.player.motionY *= 0.7;
                        if (mc.player.motionY < -0.25 && mc.player.motionY > -0.32)
                            mc.player.motionY *= 0.8;
                        if (mc.player.motionY < -0.35 && mc.player.motionY > -0.8)
                            mc.player.motionY *= 0.98;
                        if (mc.player.motionY < -0.8 && mc.player.motionY > -1.6)
                            mc.player.motionY *= 0.99;
                    }
                    setTimer(.85f);
                    final double[] speedValues = {0.420606, 0.417924, 0.415258, 0.412609, 0.409977, 0.407361, 0.404761, 0.402178, 0.399611, 0.39706, 0.394525, 0.392, 0.3894, 0.38644, 0.383655, 0.381105, 0.37867, 0.37625, 0.37384, 0.37145, 0.369, 0.3666, 0.3642, 0.3618, 0.35945, 0.357, 0.354, 0.351, 0.348, 0.345, 0.342, 0.339, 0.336, 0.333, 0.33, 0.327, 0.324, 0.321, 0.318, 0.315, 0.312, 0.309, 0.307, 0.305, 0.303, 0.3, 0.297, 0.295, 0.293, 0.291, 0.289, 0.287, 0.285, 0.283, 0.281, 0.279, 0.277, 0.275, 0.273, 0.271, 0.269, 0.267, 0.265, 0.263, 0.261, 0.259, 0.257, 0.255, 0.253, 0.251, 0.249, 0.247, 0.245, 0.243, 0.241, 0.239, 0.237};
                    if(mc.gameSettings.keyBindForward.pressed) {
                        int index = airTicks - 1;
                        if(index >= speedValues.length)
                            return;

                        mc.player.motionX = 0d;
                        mc.player.motionY = 0d;
                        break;
                    }
                    setTimer(1f);
                    airTicks = 0;
                    ++groundTicks;
                    --headStart;
                    mc.player.motionX = mc.player.motionZ /= 13d;
                    if(groundTicks == 1) {
                        updatePosition(mc.player.posX, mc.player.posY, mc.player.posZ);
                        updatePosition(mc.player.posX + .0624, mc.player.posY, mc.player.posZ);
                        updatePosition(mc.player.posX, mc.player.posY + .419, mc.player.posZ);
                        updatePosition(mc.player.posX + .0624, mc.player.posY, mc.player.posZ);
                        updatePosition(mc.player.posX, mc.player.posY + .419, mc.player.posZ);
                        break;
                    }
                    if(groundTicks > 2) {
                        groundTicks = 0;
                        mc.player.motionX = xDir * .3;
                        mc.player.motionZ = zDir * .3;
                        mc.player.motionY = .42399999499320984;
                        beganJump = true;
                        break;
                    }
                    break;
                }
            } break;
        }
    }
    private void setMoveSpeed(final PlayerMoveEvent event, final double speed) {
        final MovementInput movementInput = mc.player.movementInput;
        double forward = movementInput.moveForward;
        double strafe = movementInput.moveStrafe;
        float yaw = mc.player.rotationYaw;
        if(forward == 0d && strafe == 0d)
            event.x = event.z = 0d;
        else {
            if(forward != 0d) {
                if(strafe > 0d)
                    yaw += ((forward > 0d) ? -45 : 45);
                else if(strafe < 0d) yaw += ((forward > 0d) ? 45 : -45);
                strafe = 0d;
                if(forward > 0d)
                    forward = 1d;
                else if(forward < 0d)
                    forward = -1d;
            }
        }
        final double cos = Math.cos(Math.toRadians(yaw + 90f));
        final double sin = Math.sin(Math.toRadians(yaw + 90f));

        event.x = forward * speed * cos + strafe * speed * sin;
        event.y = forward * speed * sin - strafe * speed * cos;
    }
    private double getBaseMoveSpeed() {
        double baseSpeed = 0.2873;
        if (mc.player != null && mc.player.isPotionActive(MobEffects.SPEED))
            baseSpeed *= 1.0 + 0.2 * (Objects.requireNonNull(mc.player.getActivePotionEffect(MobEffects.SPEED)).getAmplifier() + 1);
        return baseSpeed;
    }
    private void virtue(final PlayerMoveEvent event) {
        if(mode.getValue().contentEquals("VIRTUE") && (mc.player.moveForward != 0f || (mc.player.moveStrafing != 0f && !EntityUtil.isOnLiquid() && !EntityUtil.isInLiquid()))) {
            if(stage == 0)
                moveSpeed = boost.getValue().floatValue() * getBaseMoveSpeed();
            else if(stage == 1) {
                event.y = mc.player.motionY = .42;
                moveSpeed *= 2.149;
            } else if(stage == 2)
                moveSpeed = lastDist - (.66 * (lastDist - getBaseMoveSpeed()));
            else moveSpeed = lastDist - lastDist / 159d;
            setMoveSpeed(event, moveSpeed = Math.max(getBaseMoveSpeed(), moveSpeed));
            final List<AxisAlignedBB> collidingList = mc.world.getCollisionBoxes(mc.player, mc.player.getEntityBoundingBox().offset(0d, mc.player.motionY, 0d));
            final List<AxisAlignedBB> collidingList2 = mc.world.getCollisionBoxes(mc.player, mc.player.getEntityBoundingBox().offset(0d, -.4d, 0d));
            if(!mc.player.collidedVertically && (collidingList.size() > 0 || collidingList2.size() > 0))
                event.y = mc.player.motionY = -.001d;
            ++stage;
        } else if(stage > 0)
            toggle();
    }
    private void updatePosition(final double x, final double y, final double z) { mc.player.connection.sendPacket(new CPacketPlayer.Position(x, y, z, mc.player.onGround)); }

    @Override
    public void onEnable() {
        super.onEnable();

        timer.reset();
        headStart = 4;
        groundTicks = 0;
        stage = 0;
        beganJump = false;
    }

    @Override
    public void onDisable() {
        super.onDisable();

        setTimer(1f);
    }

    @EventHandler
    private Listener<NetworkPacketEvent> packetEventListener = new Listener<>(event -> {
        if(event.reading && event.getEra() == CustomEvent.Era.PRE && lagOff.isEnabled() && event.getPacket() instanceof SPacketPlayerPosLook)
            toggle();
    });

    @EventHandler
    private Listener<PlayerMoveEvent> moveEventListener = new Listener<>(event -> {
        if(event.getEra() != CustomEvent.Era.PRE)
            return;
        if(!timer.passed((int)(timeout.getValue()*1000))) {
            event.x = event.y = event.z = 0;
            return;
        }
        if(step.isEnabled())
            mc.player.stepHeight = .6f;
        virtue(event);
    });

    @EventHandler
    private Listener<TickEvent.ClientTickEvent> tickEventListener = new Listener<>(event -> {
        if(nullCheck() || event.phase != TickEvent.Phase.START)
            return;
        switch(mode.getValue()) {
            case "TICK": {
                normal(null);
            } break;
        }
    });

    @EventHandler
    private Listener<UpdateWalkingPlayerEvent> updateWalkingPlayerEventListener = new Listener<>(event -> {
        if(event.getEra() != CustomEvent.Era.PRE)
            return;
        if(!timer.passed((int)(timeout.getValue()*1000))) {
            event.cancel();
            return;
        } normal(event);
    });

    @EventHandler
    private Listener<PlayerUpdateEvent> updateEventListener = new Listener<>(event -> mc.timer.tickLength = tickLength);
}