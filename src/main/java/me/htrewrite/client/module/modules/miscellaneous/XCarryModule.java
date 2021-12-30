package me.htrewrite.client.module.modules.miscellaneous;

import me.htrewrite.client.event.custom.networkmanager.NetworkPacketEvent;
import me.htrewrite.client.module.Module;
import me.htrewrite.client.module.ModuleType;
import me.htrewrite.exeterimports.mcapi.settings.ToggleableSetting;
import me.zero.alpine.fork.listener.EventHandler;
import me.zero.alpine.fork.listener.Listener;
import net.minecraft.network.play.client.CPacketCloseWindow;

public class XCarryModule extends Module {
    public XCarryModule() {
        super("XCarry", "4 more slots.", ModuleType.Miscellaneous, 0);
        endOption();
    }

    @Override
    public void onDisable() {
        super.onDisable();
        if(mc.world != null)
            mc.player.connection.sendPacket(new CPacketCloseWindow(mc.player.inventoryContainer.windowId));
    }

    @EventHandler
    private Listener<NetworkPacketEvent> packetEventListener = new Listener<>(event -> {
        if(event.getPacket() instanceof CPacketCloseWindow) {
            final CPacketCloseWindow packet = (CPacketCloseWindow)event.getPacket();
            if(packet.windowId == mc.player.inventoryContainer.windowId)
                event.cancel();
        }
    });
}