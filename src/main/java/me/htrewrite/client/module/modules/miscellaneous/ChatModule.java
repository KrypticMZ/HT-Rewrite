package me.htrewrite.client.module.modules.miscellaneous;

import me.htrewrite.client.clickgui.components.buttons.settings.bettermode.BetterMode;
import me.htrewrite.client.command.CommandManager;
import me.htrewrite.client.event.custom.player.PlayerChatEvent;
import me.htrewrite.client.module.Module;
import me.htrewrite.client.module.ModuleType;
import me.htrewrite.exeterimports.mcapi.settings.IntegerSetting;
import me.htrewrite.exeterimports.mcapi.settings.ModeSetting;
import me.htrewrite.exeterimports.mcapi.settings.StringSetting;
import me.htrewrite.exeterimports.mcapi.settings.ToggleableSetting;
import me.pk2.moodlyencryption.util.RandomString;
import me.zero.alpine.fork.listener.EventHandler;
import me.zero.alpine.fork.listener.Listener;
import net.minecraft.network.play.client.CPacketChatMessage;
import org.apache.commons.lang3.RandomStringUtils;

public class ChatModule extends Module {
    public static final ModeSetting configMode = new ModeSetting("Config", null, 0, BetterMode.construct("Modules", "Settings"));

    /** MODULES **/
    public static final ModeSetting setting = new ModeSetting("Setting", null, 0, BetterMode.construct("Suffix", "Color"));
    /* Suffix */
    public static final ToggleableSetting suffix_enabled = new ToggleableSetting("SuffixEnabled", null, true);
    public static final StringSetting suffix = new StringSetting("Suffix", null, "HTRewrite");
    public static final ToggleableSetting randomletters = new ToggleableSetting("RandomLetters", null, false);
    public static final IntegerSetting numberletters = new IntegerSetting("Letters", null, 3, 1, 20);
    /* Color */
    public static final ToggleableSetting color_enabled = new ToggleableSetting("ColorEnabled", null, false);
    public static final ModeSetting color = new ModeSetting("Color", null, 0, BetterMode.construct("YELLOW", "GREEN", "BLUE"));
    /** SETTINGS **/
    public static final StringSetting exceptions = new StringSetting("Except", null, "/");

    public ChatModule() {
        super("Chat", "Chat modifier.", ModuleType.Miscellaneous, 0);
        addOption(configMode);
        addOption(setting.setVisibility(a -> configMode.getI() == 0));
        addOption(suffix_enabled.setVisibility(a -> setting.getI() == 0 && configMode.getI() == 0));
        addOption(suffix.setVisibility(a -> setting.getI() == 0 && configMode.getI() == 0));
        addOption(randomletters.setVisibility(a -> setting.getI() == 0 && configMode.getI() == 0));
        addOption(numberletters.setVisibility(a -> setting.getI() == 0 && configMode.getI() == 0 && randomletters.isEnabled()));
        addOption(color_enabled.setVisibility(a -> setting.getI() == 1 && configMode.getI() == 0));
        addOption(color.setVisibility(a -> setting.getI() == 1 && configMode.getI() == 0));
        addOption(exceptions.setVisibility(a -> configMode.getI() == 1));
        endOption();
    }

    @EventHandler
    private Listener<PlayerChatEvent> chatEventListener = new Listener<>(event -> {
        String[] exceptions = ChatModule.exceptions.getValue().split(" ");
        for(String exception : exceptions)
            if(event.message.startsWith(exception))
                return;
        if(event.message.startsWith(CommandManager.prefix))
            return;
        event.cancel();

        String message = event.message;
        if(suffix_enabled.isEnabled())
            message += (" >> " + suffix.getValue());
        if(color_enabled.isEnabled()) {
            String colorMsg = "";
            switch (color.getValue()) {
                case "YELLOW": colorMsg = "#"; break;
                case "BLUE": colorMsg = "``"; break;
                case "GREEN": colorMsg = ">"; break;
                default: break;
            }
            message = colorMsg + message;
        }
        if (randomletters.isEnabled())
            message += " " + (new RandomString(numberletters.getValue()).nextString());

        mc.player.connection.sendPacket(new CPacketChatMessage(message));
    });
}