package me.htrewrite.client.command.commands;

import me.htrewrite.client.HTRewrite;
import me.htrewrite.client.command.Command;
import me.htrewrite.client.event.custom.CustomEvent;
import me.htrewrite.client.event.custom.networkmanager.NetworkPacketEvent;
import me.htrewrite.client.event.custom.player.PlayerUpdateEvent;
import me.zero.alpine.fork.listener.EventHandler;
import me.zero.alpine.fork.listener.Listenable;
import me.zero.alpine.fork.listener.Listener;
import net.minecraft.entity.Entity;
import net.minecraft.network.play.client.CPacketVehicleMove;
import net.minecraft.network.play.server.SPacketDestroyEntities;
import net.minecraft.network.play.server.SPacketSetPassengers;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;

public class VanishCommand extends Command implements Listenable {
    private Entity riding = null;
    public VanishCommand() {
        super("vanish", "<[dismount]/[remount]>", "Clientside dismount/remount an entity.");
    }

    public void dismount() {
        if(riding != null) {
            sendMessage("&cYou are already dismounted from your entity!");
            return;
        }
        if(!mc.player.isRiding()) {
            sendMessage("&cYou are not riding an entity!");
            return;
        }

        HTRewrite.EVENT_BUS.subscribe(this);

        riding = mc.player.getRidingEntity();
        mc.player.dismountRidingEntity();
        mc.world.removeEntity(riding);
    }

    public void remount() {
        if(riding == null) {
            sendMessage("&cYou are not dismounted!");
            return;
        }

        HTRewrite.EVENT_BUS.unsubscribe(this);

        riding.isDead = false;
        if(!mc.player.isRiding()) {
            mc.world.spawnEntity(riding);
            mc.player.startRiding(riding, true);
        }
        riding = null;
        sendMessage("&eForced a remount!");
    }

    @Override
    public void call(String[] args) {
        if(args.length < 1) {
            sendInvalidUsageMessage();
            return;
        }

        switch (args[0].toLowerCase()) {
            case "dismount":
                dismount();
                break;

            case "remount":
                remount();
                break;

            default:
                sendInvalidUsageMessage();
                break;
        }
    }

    @EventHandler
    private Listener<PlayerUpdateEvent> updateEventListener = new Listener<>(event -> {
        if(riding == null || mc.player.isRiding())
            return;

        mc.player.onGround = true;
        riding.setPosition(mc.player.posX, mc.player.posY, mc.player.posZ);
        mc.player.connection.sendPacket(new CPacketVehicleMove(riding));
    });

    @EventHandler
    private Listener<NetworkPacketEvent> packetEventListener = new Listener<>(event -> {
        if(event.getEra() != CustomEvent.Era.PRE)
            return;

        if(event.getPacket() instanceof SPacketSetPassengers) {
            if(riding == null)
                return;

            SPacketSetPassengers packet = (SPacketSetPassengers)event.getPacket();
            Entity entity = mc.world.getEntityByID(packet.getEntityId());
            if(entity == riding) {
                for(int i : packet.getPassengerIds())
                    if(mc.world.getEntityByID(i) == mc.player)
                        return;
                sendMessage("&cYou dismounted");
                remount();
            }
        } else if(event.getPacket() instanceof SPacketDestroyEntities) {
            SPacketDestroyEntities packet = (SPacketDestroyEntities)event.getPacket();
            for(int id : packet.getEntityIDs())
                if(id == riding.getEntityId())
                    sendMessage("&cEntity got null because of a packet!");
        }
    });

    @EventHandler
    private Listener<EntityJoinWorldEvent> joinWorldEventListener = new Listener<>(event -> {
        if(event.getEntity() == mc.player)
            sendMessage("&cWorld join event fired!");
    });
}