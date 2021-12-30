package me.htrewrite.client.module.modules.combat;

import me.htrewrite.client.event.custom.CustomEvent;
import me.htrewrite.client.event.custom.networkmanager.NetworkPacketEvent;
import me.htrewrite.client.module.Module;
import me.htrewrite.client.module.ModuleType;
import me.zero.alpine.fork.listener.EventHandler;
import me.zero.alpine.fork.listener.Listener;
import net.minecraft.item.ItemBow;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.client.CPacketPlayerDigging;

public class BowBombModule extends Module {
    public BowBombModule() {
        super("BowBomb", "More damage with bow while flying or falling.", ModuleType.Combat, 0);
        endOption();
    }

    @EventHandler
    private Listener<NetworkPacketEvent> packetEventListener = new Listener<>(event -> {
        if(event.getEra() != CustomEvent.Era.PRE || !(event.getPacket() instanceof CPacketPlayerDigging))
            return;
        CPacketPlayerDigging packet = (CPacketPlayerDigging)event.getPacket();
        if(packet.getAction() != CPacketPlayerDigging.Action.RELEASE_USE_ITEM || mc.player.onGround || !(mc.player.inventory.getCurrentItem().getItem() instanceof ItemBow && mc.player.getItemInUseMaxCount() > 20))
            return;

        mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY - 0.1f, mc.player.posZ, false));
        mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY - 10000f, mc.player.posZ, true));
    });
}