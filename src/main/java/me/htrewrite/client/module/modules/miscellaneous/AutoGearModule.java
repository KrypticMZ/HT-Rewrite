package me.htrewrite.client.module.modules.miscellaneous;

import me.htrewrite.client.module.Module;
import me.htrewrite.client.module.ModuleType;
import me.htrewrite.client.util.ChatColor;
import me.htrewrite.exeterimports.mcapi.settings.ToggleableSetting;
import me.htrewrite.exeterimports.mcapi.settings.ValueSetting;
import net.minecraft.util.text.TextComponentString;

public class AutoGearModule extends Module {
    public static final ValueSetting<Double> tickDelay = new ValueSetting<>("TickDelay", null, 0D, 0D, 20D);
    public static final ToggleableSetting chatMsg = new ToggleableSetting("ChatMsg", null, true);
    public static final ToggleableSetting enderChest = new ToggleableSetting("EnderChest", null, false);
    public static final ToggleableSetting confirmSort = new ToggleableSetting("ConfirmSort", null, true);
    public static final ToggleableSetting invasive = new ToggleableSetting("Invasive", null, false);
    public static final ToggleableSetting closeAfter = new ToggleableSetting("CloseAfter", null, false);
    public static final ToggleableSetting debugMode = new ToggleableSetting("DebugMode", null, false);

    public AutoGearModule() {
        super("AutoGear", "Yeah, name says it.", ModuleType.Miscellaneous, 0);
        addOption(tickDelay);
        addOption(chatMsg);
        addOption(enderChest);
        addOption(confirmSort);
        addOption(invasive);
        addOption(closeAfter);
        addOption(debugMode);
        endOption();
    }

    @Override
    public void onEnable() {
        super.onEnable();

       // String curConfigName = AutoGear
    }
}