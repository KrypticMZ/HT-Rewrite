package me.htrewrite.client.module.modules.miscellaneous;

import me.htrewrite.client.clickgui.components.buttons.settings.bettermode.BetterMode;
import me.htrewrite.client.event.custom.CustomEvent;
import me.htrewrite.client.event.custom.networkmanager.NetworkPacketEvent;
import me.htrewrite.client.module.Module;
import me.htrewrite.client.module.ModuleType;
import me.htrewrite.client.util.ChatColor;
import me.htrewrite.exeterimports.mcapi.settings.*;
import me.zero.alpine.fork.listener.EventHandler;
import me.zero.alpine.fork.listener.Listener;
import net.minecraft.network.play.client.CPacketUpdateSign;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class SignPlusModule extends Module {
    public static final ToggleableSetting date = new ToggleableSetting("Date", true);
    public static final IntegerSetting line = new IntegerSetting("Line", 4, 1, 4);
    public static final ModeSetting format = new ModeSetting("DateFormat", 0, BetterMode.construct("US", "EU"));

    public static final ToggleableSetting coloredSign = new ToggleableSetting("ColoredSign", false);
    
    public SignPlusModule() {
        super("Sign+", "Modifies the sign info of the sign you place", ModuleType.Miscellaneous, 0);
        addOption(date);
        addOption(line.setVisibility(v -> date.isEnabled()));
        addOption(format.setVisibility(v -> date.isEnabled()));
        addOption(new SeparatorSetting(" ", null));
        addOption(coloredSign);
        endOption();
    }

    @EventHandler
    private Listener<NetworkPacketEvent> eventListener = new Listener<>(event -> {
        if(event.getEra() != CustomEvent.Era.PRE || !(event.getPacket() instanceof CPacketUpdateSign))
            return;
        CPacketUpdateSign packet = (CPacketUpdateSign)event.getPacket();

        if(date.isEnabled()) {
            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern(format.getValue().equalsIgnoreCase("US") ? "MM/dd/yyyy" : "dd/MM/yyyy");
            LocalDateTime dateTime = LocalDateTime.now();

            packet.lines[line.getValue() - 1] = dateTime.format(dateFormatter);
        }
        if(coloredSign.isEnabled())
            for(int i = 0; i < 4; i++)
                packet.lines[i] = packet.lines[i].replaceAll("&([0-9a-fk-or])", "\247" + "\247a");
    });
}