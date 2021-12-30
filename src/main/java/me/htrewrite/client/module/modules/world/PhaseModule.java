package me.htrewrite.client.module.modules.world;

import me.htrewrite.client.clickgui.components.buttons.settings.bettermode.BetterMode;
import me.htrewrite.client.event.custom.CustomEvent;
import me.htrewrite.client.event.custom.networkmanager.NetworkPacketEvent;
import me.htrewrite.client.event.custom.player.*;
import me.htrewrite.client.event.custom.render.RenderHelmetEvent;
import me.htrewrite.client.event.custom.world.SetOpaqueCubeEvent;
import me.htrewrite.client.module.Module;
import me.htrewrite.client.module.ModuleType;
import me.htrewrite.client.util.MathUtil;
import me.htrewrite.exeterimports.mcapi.settings.ModeSetting;
import me.htrewrite.exeterimports.mcapi.settings.ToggleableSetting;
import me.zero.alpine.fork.event.type.Cancellable;
import me.zero.alpine.fork.listener.EventHandler;
import me.zero.alpine.fork.listener.Listener;
import net.minecraft.entity.item.EntityBoat;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.client.event.RenderBlockOverlayEvent;

public class PhaseModule extends Module {
    public static final ModeSetting mode = new ModeSetting("Mode", null, 0, BetterMode.construct("SAND", "PACKET", "SKIP", "NOCLIP"));
    public static final ToggleableSetting floor = new ToggleableSetting("Floor", null, true);

    @Override public String getMeta() { return mode.getValue(); }
    public PhaseModule() {
        super("Phase", "Glitch through blocks.", ModuleType.World, 0);
        addOption(mode);
        addOption(floor);
        endOption();
    }

    @Override
    public void onDisable() {
        super.onDisable();

        if(mc.player != null)
            mc.player.noClip = false;
    }

    @EventHandler private Listener<SetOpaqueCubeEvent> setOpaqueCubeEventListener = new Listener<>(Cancellable::cancel);
    @EventHandler private Listener<RenderBlockOverlayEvent> renderBlockOverlayEventListener = new Listener<>(event -> event.setCanceled(true));
    @EventHandler private Listener<RenderHelmetEvent> renderHelmetEventListener = new Listener<>(Cancellable::cancel);
    @EventHandler private Listener<PlayerPushOutOfBlocksEvent> pushOutOfBlocksEventListener = new Listener<>(Cancellable::cancel);
    @EventHandler private Listener<PlayerPushedByWaterEvent> pushedByWaterEventListener = new Listener<>(Cancellable::cancel);
    @EventHandler private Listener<PlayerApplyCollisionEvent> applyCollisionEventListener = new Listener<>(Cancellable::cancel);
    @EventHandler private Listener<PlayerMoveEvent> moveEventListener = new Listener<>(event -> {
        if(nullCheck())
            return;

        mc.player.noClip = true;
        boolean floor = PhaseModule.floor.isEnabled() || mc.player.posY >= 1;
        if(mode.getValue().equalsIgnoreCase("SAND")) {
            if(mc.player.getRidingEntity() != null) {
                if ((mc.gameSettings.keyBindSprint.isKeyDown() && floor) ||
                        (!mc.gameSettings.keyBindJump.isKeyDown() && mc.player.posY < mc.player.getRidingEntity().posY) ||
                        (mc.player.posY < mc.player.getRidingEntity().posY))
                    mc.player.motionY = 0;
            } else if(!mc.gameSettings.keyBindJump.isKeyDown() || (!mc.gameSettings.keyBindSneak.isKeyDown() && floor))
                    mc.player.motionY = 0;
        }
    });
    @EventHandler private Listener<NetworkPacketEvent> packetEventListener = new Listener<>(event -> {
        if(!(event.getEra() == CustomEvent.Era.PRE && !event.reading && mode.getValue().contentEquals("NOCLIP") && event.getPacket() instanceof CPacketPlayer && !(event.getPacket() instanceof CPacketPlayer.Position)))
            return;
        event.cancel();
    });
    @EventHandler private Listener<UpdateWalkingPlayerEvent> updateWalkingPlayerEventListener = new Listener<>(event -> {
        if(!(event.getEra() == CustomEvent.Era.PRE && mode.getValue().contentEquals("NOCLIP")))
            return;
        mc.player.setVelocity(0, 0, 0);
        if(mc.gameSettings.keyBindForward.isKeyDown() || mc.gameSettings.keyBindBack.isKeyDown() || mc.gameSettings.keyBindLeft.isKeyDown() || mc.gameSettings.keyBindRight.isKeyDown()) {
            double[] speed = MathUtil.directionSpeed(.06f);
            mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX + speed[0], mc.player.posY, mc.player.posZ + speed[1], mc.player.onGround));
            mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX, 0, mc.player.posZ, mc.player.onGround));
        } if(mc.gameSettings.keyBindSneak.isKeyDown()) {
            mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY - .06f, mc.player.posZ, mc.player.onGround));
            mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX, 0, mc.player.posZ, mc.player.onGround));
        } if(mc.gameSettings.keyBindJump.isKeyDown()) {
            mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY + .06f, mc.player.posZ, mc.player.onGround));
            mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX, 0, mc.player.posZ, mc.player.onGround));
        }
    });
    @EventHandler private Listener<PlayerUpdateEvent> updateEventListener = new Listener<>(event -> {
        if(event.getEra() != CustomEvent.Era.PRE)
            return;

        switch(mode.getValue()) {
            case "SAND": {
                if(mc.gameSettings.keyBindJump.isKeyDown() && mc.player.getRidingEntity() != null && mc.player.getRidingEntity() instanceof EntityBoat) {
                    final EntityBoat boat = (EntityBoat)mc.player.getRidingEntity();
                    if(boat.onGround)
                        boat.motionY = .42f;
                }
            } break;

            case "PACKET": {
                final Vec3d vec3d = MathUtil.direction(mc.player.rotationYaw);
                if(mc.player.onGround && mc.player.collidedHorizontally) {
                    mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX + vec3d.x * .00001f, mc.player.posY, mc.player.posZ + vec3d.z * .0001f, mc.player.onGround));
                    mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX + vec3d.x * 2f, mc.player.posY, mc.player.posZ + vec3d.z * 2f, mc.player.onGround));
                }
            } break;

            case "SKIP": {
                final Vec3d vec3d = MathUtil.direction(mc.player.rotationYaw);
                if(mc.player.onGround && mc.player.collidedHorizontally) {
                    mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY, mc.player.posZ, mc.player.onGround));
                    mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX + vec3d.x * .001f, mc.player.posY + .1f, mc.player.posZ + vec3d.z * .001f, mc.player.onGround));
                    mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX + vec3d.x * .03f, 0, mc.player.posZ + vec3d.z * .03f, mc.player.onGround));
                    mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX + vec3d.x * .06f, mc.player.posY, mc.player.posZ + vec3d.z + .06f, mc.player.onGround));
                }
            } break;

            default:
                break;
        }
    });
}