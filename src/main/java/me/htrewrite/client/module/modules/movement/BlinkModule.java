package me.htrewrite.client.module.modules.movement;

import me.htrewrite.client.event.custom.CustomEvent;
import me.htrewrite.client.event.custom.networkmanager.NetworkPacketEvent;
import me.htrewrite.client.module.Module;
import me.htrewrite.client.module.ModuleType;
import me.htrewrite.exeterimports.mcapi.settings.ToggleableSetting;
import me.zero.alpine.fork.listener.EventHandler;
import me.zero.alpine.fork.listener.Listener;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.entity.passive.EntityDonkey;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketConfirmTeleport;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.client.CPacketVehicleMove;

import java.util.LinkedList;

public class BlinkModule extends Module {
    public static final ToggleableSetting visualize = new ToggleableSetting("Visualize", null, true);
    public static final ToggleableSetting entityBlink = new ToggleableSetting("EntityBlink", null, true);

    public BlinkModule() {
        super("Blink", "Holds your movement packets.", ModuleType.Movement, 0);
        addOption(visualize);
        addOption(entityBlink);
        endOption();
    }

    private EntityOtherPlayerMP original;
    private EntityDonkey ridingEntity;
    private LinkedList<Packet> packets = new LinkedList<Packet>();

    @Override
    public void onEnable() {
        super.onEnable();

        packets.clear();
        original = null;
        ridingEntity = null;

        if(visualize.isEnabled()) {
            original = new EntityOtherPlayerMP(mc.world, mc.session.getProfile());
            original.copyLocationAndAnglesFrom(mc.player);
            original.rotationYaw = mc.player.rotationYaw;
            original.rotationYawHead = mc.player.rotationYawHead;
            original.inventory.copyInventory(mc.player.inventory);
            mc.world.addEntityToWorld(-0xFFFFF, original);

            if(mc.player.isRiding() && mc.player.getRidingEntity() instanceof EntityDonkey) {
                EntityDonkey original = (EntityDonkey)mc.player.getRidingEntity();
                ridingEntity = new EntityDonkey(mc.world);
                ridingEntity.copyLocationAndAnglesFrom(original);
                ridingEntity.setChested(original.hasChest());
                mc.world.addEntityToWorld(-0xFFFFF+1, ridingEntity);
                original.startRiding(ridingEntity, true);
            }
        }
    }

    @Override
    public void onDisable() {
        super.onDisable();

        if(!packets.isEmpty() && mc.world != null) {
            while (!packets.isEmpty()) {
                mc.getConnection().sendPacket(packets.getFirst());
                packets.removeFirst();
            }
        }

        if(original != null) {
            if(original.isRiding())
                original.dismountRidingEntity();
            mc.world.removeEntity(original);
        }

        if(ridingEntity != null)
            mc.world.removeEntity(ridingEntity);
    }

    @EventHandler
    private Listener<NetworkPacketEvent> packetEventListener = new Listener<>(event -> {
        if(event.reading || event.getEra()!= CustomEvent.Era.PRE)
            return;

        Packet packet = event.getPacket();
        if(packet instanceof CPacketPlayer || packet instanceof CPacketConfirmTeleport || (entityBlink.isEnabled() && packet instanceof CPacketVehicleMove)) {
            event.cancel();
            packets.add(packet);
        }
    });
}