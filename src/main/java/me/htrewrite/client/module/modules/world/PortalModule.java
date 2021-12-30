package me.htrewrite.client.module.modules.world;

import me.htrewrite.client.event.custom.CustomEvent;
import me.htrewrite.client.event.custom.networkmanager.NetworkPacketEvent;
import me.htrewrite.client.module.Module;
import me.htrewrite.client.module.ModuleType;
import me.htrewrite.exeterimports.mcapi.settings.ToggleableSetting;
import me.zero.alpine.fork.listener.EventHandler;
import me.zero.alpine.fork.listener.Listener;
import net.minecraft.network.play.client.CPacketConfirmTeleport;

public class PortalModule extends Module {
    public static final ToggleableSetting chat = new ToggleableSetting("Chat", true);
    public static final ToggleableSetting godmode = new ToggleableSetting("GodMode", false);

    public static volatile PortalModule INSTANCE;

    public PortalModule() {
        super("Portal", "Better portals.", ModuleType.World, 0);
        addOption(chat);
        addOption(godmode);
        endOption();

        INSTANCE = this;
    }

    @EventHandler
    private Listener<NetworkPacketEvent> packetEventListener = new Listener<>(event -> {
        if(event.getEra() == CustomEvent.Era.PRE && godmode.isEnabled() && event.getPacket() instanceof CPacketConfirmTeleport)
            event.cancel();
    });
}