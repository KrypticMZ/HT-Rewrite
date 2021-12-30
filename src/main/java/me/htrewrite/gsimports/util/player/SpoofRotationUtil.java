package me.htrewrite.gsimports.util.player;

import me.htrewrite.client.HTRewrite;
import me.htrewrite.client.event.custom.CustomEvent;
import me.htrewrite.client.event.custom.networkmanager.NetworkPacketEvent;
import me.htrewrite.gsimports.util.world.EntityUtil;
import me.zero.alpine.fork.listener.EventHandler;
import me.zero.alpine.fork.listener.Listenable;
import me.zero.alpine.fork.listener.Listener;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketPlayer;

public class SpoofRotationUtil implements Listenable {
    private static final Minecraft mc = Minecraft.getMinecraft();
    public static final SpoofRotationUtil ROTATION_UTIL = new SpoofRotationUtil();

    private int rotationConnections = 0;

    private boolean shouldSpoofAngles;
    private boolean isSpoofingAngles;
    private double yaw;
    private double pitch;

    private SpoofRotationUtil() { }

    public void onEnable() {
        rotationConnections++;
        if (rotationConnections == 1)
            HTRewrite.EVENT_BUS.subscribe(this);
    }

    public void onDisable() {
        rotationConnections--;
        if (rotationConnections == 0)
            HTRewrite.EVENT_BUS.unsubscribe(this);
    }

    public void lookAtPacket(double px, double py, double pz, EntityPlayer me) {
        double[] v = EntityUtil.calculateLookAt(px, py, pz, me);
        this.setYawAndPitch((float) v[0], (float) v[1]);
    }

    public void setYawAndPitch(float yaw1, float pitch1) {
        yaw = yaw1;
        pitch = pitch1;
        isSpoofingAngles = true;
    }

    public void resetRotation() {
        if (isSpoofingAngles) {
            yaw = mc.player.rotationYaw;
            pitch = mc.player.rotationPitch;
            isSpoofingAngles = false;
        }
    }

    public void shouldSpoofAngles(boolean e) {
        shouldSpoofAngles = e;
    }

    public boolean isSpoofingAngles() {
        return isSpoofingAngles;
    }

    @EventHandler
    private final Listener<NetworkPacketEvent> packetSendListener = new Listener<>(event -> {
        if(!(event.reading && event.getEra() == CustomEvent.Era.PRE))
            return;

        Packet packet = event.getPacket();
        if (packet instanceof CPacketPlayer && shouldSpoofAngles) {
            if (isSpoofingAngles) {
                ((CPacketPlayer) packet).yaw = (float) yaw;
                ((CPacketPlayer) packet).pitch = (float) pitch;
            }
        }
    });
}