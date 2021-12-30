package me.htrewrite.client.module.modules.gui;

import me.htrewrite.client.HTRewrite;
import me.htrewrite.client.Wrapper;
import me.htrewrite.client.clickgui.ClickGuiScreen;
import me.htrewrite.client.clickgui.StaticClickGuiColor;
import me.htrewrite.client.clickgui.components.Colors;
import me.htrewrite.client.clickgui.components.buttons.settings.bettermode.BetterMode;
import me.htrewrite.client.event.custom.client.ClientSettingChangeEvent;
import me.htrewrite.client.module.Module;
import me.htrewrite.client.module.ModuleType;
import me.htrewrite.client.util.ChatColor;
import me.htrewrite.client.util.RainbowUtil;
import me.htrewrite.exeterimports.mcapi.settings.ModeSetting;
import me.htrewrite.exeterimports.mcapi.settings.ToggleableSetting;
import me.htrewrite.exeterimports.mcapi.settings.ValueSetting;
import me.zero.alpine.fork.listener.EventHandler;
import me.zero.alpine.fork.listener.Listener;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.lwjgl.input.Keyboard;

public class ClickGuiModule extends Module {
    public static final ModeSetting color = new ModeSetting("Color", null, 0, BetterMode.construct(ChatColor.enumToStringArray(Colors.values())));

    public static final ValueSetting<Double> b_r = new ValueSetting<>("B_R", null, 86d, 0D, 255D);
    public static final ValueSetting<Double> b_g = new ValueSetting<>("B_G", null, 86d, 0D, 255D);
    public static final ValueSetting<Double> b_b = new ValueSetting<>("B_B", null, 86d, 0D, 255D);
    public static final ValueSetting<Double> b_a = new ValueSetting<>("B_A", null, 102d, 0D, 255D);

    public static final ToggleableSetting be_rainbow = new ToggleableSetting("BE_Rainbow", null, false);
    public static final ValueSetting<Double> be_r = new ValueSetting<>("BE_R", null, 178d, 0D, 255D);
    public static final ValueSetting<Double> be_g = new ValueSetting<>("BE_G", null, 13d, 0D, 255D);
    public static final ValueSetting<Double> be_b = new ValueSetting<>("BE_B", null, 30d, 0D, 255D);
    public static final ValueSetting<Double> be_a = new ValueSetting<>("BE_A", null, 186d, 0D, 255D);

    public static final ValueSetting<Double> bh_r = new ValueSetting<>("BH_R", null, 196d, 0D, 255D);
    public static final ValueSetting<Double> bh_g = new ValueSetting<>("BH_G", null, 196d, 0D, 255D);
    public static final ValueSetting<Double> bh_b = new ValueSetting<>("BH_B", null, 196d, 0D, 255D);
    public static final ValueSetting<Double> bh_a = new ValueSetting<>("BH_A", null, 170d, 0D, 255D);

    public static final ValueSetting<Double> beh_r = new ValueSetting<>("BEH_R", null, 234d, 0D, 255D);
    public static final ValueSetting<Double> beh_g = new ValueSetting<>("BEH_G", null, 171d, 0D, 255D);
    public static final ValueSetting<Double> beh_b = new ValueSetting<>("BEH_B", null, 25d, 0D, 255D);
    public static final ValueSetting<Double> beh_a = new ValueSetting<>("BEH_A", null, 170d, 0D, 255D);

    public static final ValueSetting<Double> bc_r = new ValueSetting<>("BC_R", null, 255d, 0D, 255D);
    public static final ValueSetting<Double> bc_g = new ValueSetting<>("BC_G", null, 255d, 0D, 255D);
    public static final ValueSetting<Double> bc_b = new ValueSetting<>("BC_B", null, 255d, 0D, 255D);
    public static final ValueSetting<Double> bc_a = new ValueSetting<>("BC_A", null, 45d, 0D, 255D);

    public static final ValueSetting<Double> bce_r = new ValueSetting<>("BCE_R", null, 69d, 0D, 255D);
    public static final ValueSetting<Double> bce_g = new ValueSetting<>("BCE_G", null, 177d, 0D, 255D);
    public static final ValueSetting<Double> bce_b = new ValueSetting<>("BCE_B", null, 21d, 0D, 255D);
    public static final ValueSetting<Double> bce_a = new ValueSetting<>("BCE_A", null, 141d, 0D, 255D);

    public static final ValueSetting<Double> bl_r = new ValueSetting<>("BL_R", null, 221d, 0D, 255D);
    public static final ValueSetting<Double> bl_g = new ValueSetting<>("BL_G", null, 221d, 0D, 255D);
    public static final ValueSetting<Double> bl_b = new ValueSetting<>("BL_B", null, 221d, 0D, 255D);
    public static final ValueSetting<Double> bl_a = new ValueSetting<>("BL_A", null, 255d, 0D, 255D);

    public static final ValueSetting<Double> ble_r = new ValueSetting<>("BLE_R", null, 221d, 0D, 255D);
    public static final ValueSetting<Double> ble_g = new ValueSetting<>("BLE_G", null, 221d, 0D, 255D);
    public static final ValueSetting<Double> ble_b = new ValueSetting<>("BLE_B", null, 221d, 0D, 255D);
    public static final ValueSetting<Double> ble_a = new ValueSetting<>("BLE_A", null, 255d, 0D, 255D);

    public static final ValueSetting<Double> blh_r = new ValueSetting<>("BLH_R", null, 204d, 0D, 255D);
    public static final ValueSetting<Double> blh_g = new ValueSetting<>("BLH_G", null, 204d, 0D, 255D);
    public static final ValueSetting<Double> blh_b = new ValueSetting<>("BLH_B", null, 204d, 0D, 255D);
    public static final ValueSetting<Double> blh_a = new ValueSetting<>("BLH_A", null, 255d, 0D, 255D);

    public static final ValueSetting<Double> pi_r = new ValueSetting<>("PI_R", null, 0d, 0D, 255D);
    public static final ValueSetting<Double> pi_g = new ValueSetting<>("PI_G", null, 0d, 0D, 255D);
    public static final ValueSetting<Double> pi_b = new ValueSetting<>("PI_B", null, 0d, 0D, 255D);
    public static final ValueSetting<Double> pi_a = new ValueSetting<>("PI_A", null, 136d, 0D, 255D);

    public static final ValueSetting<Double> pb_r = new ValueSetting<>("PB_R", null, 0d, 0D, 255D);
    public static final ValueSetting<Double> pb_g = new ValueSetting<>("PB_G", null, 0d, 0D, 255D);
    public static final ValueSetting<Double> pb_b = new ValueSetting<>("PB_B", null, 0d, 0D, 255D);
    public static final ValueSetting<Double> pb_a = new ValueSetting<>("PB_A", null, 85d, 0D, 255D);

    public static final ValueSetting<Double> pl_r = new ValueSetting<>("PL_R", null, 255d, 0D, 255D);
    public static final ValueSetting<Double> pl_g = new ValueSetting<>("PL_G", null, 255d, 0D, 255D);
    public static final ValueSetting<Double> pl_b = new ValueSetting<>("PL_B", null, 255d, 0D, 255D);
    public static final ValueSetting<Double> pl_a = new ValueSetting<>("PL_A", null, 255d, 0D, 255D);

    private final RainbowUtil rainbowUtil = new RainbowUtil();
    public ClickGuiModule() {
        super("ClickGUI", "Opens a gui.", ModuleType.Gui, Keyboard.KEY_P);
        addOption(color);
        /* BUTTON */
        addOption(b_r.setVisibility(v -> color.getValue().contentEquals(Colors.BUTTON.name())));
        addOption(b_g.setVisibility(v -> color.getValue().contentEquals(Colors.BUTTON.name())));
        addOption(b_b.setVisibility(v -> color.getValue().contentEquals(Colors.BUTTON.name())));
        addOption(b_a.setVisibility(v -> color.getValue().contentEquals(Colors.BUTTON.name())));
        /* BUTTON_ENABLED */
        addOption(be_rainbow.setVisibility(v -> color.getValue().contentEquals(Colors.BUTTON_ENABLED.name())));
        addOption(be_r.setVisibility(v -> color.getValue().contentEquals(Colors.BUTTON_ENABLED.name()) && !be_rainbow.isEnabled()));
        addOption(be_g.setVisibility(v -> color.getValue().contentEquals(Colors.BUTTON_ENABLED.name()) && !be_rainbow.isEnabled()));
        addOption(be_b.setVisibility(v -> color.getValue().contentEquals(Colors.BUTTON_ENABLED.name()) && !be_rainbow.isEnabled()));
        addOption(be_a.setVisibility(v -> color.getValue().contentEquals(Colors.BUTTON_ENABLED.name())));
        /* BUTTON_HOVER */
        addOption(bh_r.setVisibility(v -> color.getValue().contentEquals(Colors.BUTTON_HOVER.name())));
        addOption(bh_g.setVisibility(v -> color.getValue().contentEquals(Colors.BUTTON_HOVER.name())));
        addOption(bh_b.setVisibility(v -> color.getValue().contentEquals(Colors.BUTTON_HOVER.name())));
        addOption(bh_a.setVisibility(v -> color.getValue().contentEquals(Colors.BUTTON_HOVER.name())));
        /* BUTTON_ENABLED_HOVER */
        addOption(beh_r.setVisibility(v -> color.getValue().contentEquals(Colors.BUTTON_ENABLED_HOVER.name())));
        addOption(beh_g.setVisibility(v -> color.getValue().contentEquals(Colors.BUTTON_ENABLED_HOVER.name())));
        addOption(beh_b.setVisibility(v -> color.getValue().contentEquals(Colors.BUTTON_ENABLED_HOVER.name())));
        addOption(beh_a.setVisibility(v -> color.getValue().contentEquals(Colors.BUTTON_ENABLED_HOVER.name())));
        /* BUTTON_COMPONENT */
        addOption(bc_r.setVisibility(v -> color.getValue().contentEquals(Colors.BUTTON_COMPONENT.name())));
        addOption(bc_g.setVisibility(v -> color.getValue().contentEquals(Colors.BUTTON_COMPONENT.name())));
        addOption(bc_b.setVisibility(v -> color.getValue().contentEquals(Colors.BUTTON_COMPONENT.name())));
        addOption(bc_a.setVisibility(v -> color.getValue().contentEquals(Colors.BUTTON_COMPONENT.name())));
        /* BUTTON_COMPONENT_ENABLED */
        addOption(bce_r.setVisibility(v -> color.getValue().contentEquals(Colors.BUTTON_COMPONENT_ENABLED.name())));
        addOption(bce_g.setVisibility(v -> color.getValue().contentEquals(Colors.BUTTON_COMPONENT_ENABLED.name())));
        addOption(bce_b.setVisibility(v -> color.getValue().contentEquals(Colors.BUTTON_COMPONENT_ENABLED.name())));
        addOption(bce_a.setVisibility(v -> color.getValue().contentEquals(Colors.BUTTON_COMPONENT_ENABLED.name())));
        /* BUTTON_LABEL */
        addOption(bl_r.setVisibility(v -> color.getValue().contentEquals(Colors.BUTTON_LABEL.name())));
        addOption(bl_g.setVisibility(v -> color.getValue().contentEquals(Colors.BUTTON_LABEL.name())));
        addOption(bl_b.setVisibility(v -> color.getValue().contentEquals(Colors.BUTTON_LABEL.name())));
        addOption(bl_a.setVisibility(v -> color.getValue().contentEquals(Colors.BUTTON_LABEL.name())));
        /* BUTTON_LABEL_ENABLED */
        addOption(ble_r.setVisibility(v -> color.getValue().contentEquals(Colors.BUTTON_LABEL_ENABLED.name())));
        addOption(ble_g.setVisibility(v -> color.getValue().contentEquals(Colors.BUTTON_LABEL_ENABLED.name())));
        addOption(ble_b.setVisibility(v -> color.getValue().contentEquals(Colors.BUTTON_LABEL_ENABLED.name())));
        addOption(ble_a.setVisibility(v -> color.getValue().contentEquals(Colors.BUTTON_LABEL_ENABLED.name())));
        /* BUTTON_LABEL_HOVER */
        addOption(blh_r.setVisibility(v -> color.getValue().contentEquals(Colors.BUTTON_LABEL_HOVER.name())));
        addOption(blh_g.setVisibility(v -> color.getValue().contentEquals(Colors.BUTTON_LABEL_HOVER.name())));
        addOption(blh_b.setVisibility(v -> color.getValue().contentEquals(Colors.BUTTON_LABEL_HOVER.name())));
        addOption(blh_a.setVisibility(v -> color.getValue().contentEquals(Colors.BUTTON_LABEL_HOVER.name())));
        /* PANEL_INSIDE */
        addOption(pi_r.setVisibility(v -> color.getValue().contentEquals(Colors.PANEL_INSIDE.name())));
        addOption(pi_g.setVisibility(v -> color.getValue().contentEquals(Colors.PANEL_INSIDE.name())));
        addOption(pi_b.setVisibility(v -> color.getValue().contentEquals(Colors.PANEL_INSIDE.name())));
        addOption(pi_a.setVisibility(v -> color.getValue().contentEquals(Colors.PANEL_INSIDE.name())));
        /* PANEL_BORDER */
        addOption(pb_r.setVisibility(v -> color.getValue().contentEquals(Colors.PANEL_BORDER.name())));
        addOption(pb_g.setVisibility(v -> color.getValue().contentEquals(Colors.PANEL_BORDER.name())));
        addOption(pb_b.setVisibility(v -> color.getValue().contentEquals(Colors.PANEL_BORDER.name())));
        addOption(pb_a.setVisibility(v -> color.getValue().contentEquals(Colors.PANEL_BORDER.name())));
        /* PANEL_LABEL */
        addOption(pl_r.setVisibility(v -> color.getValue().contentEquals(Colors.PANEL_LABEL.name())));
        addOption(pl_g.setVisibility(v -> color.getValue().contentEquals(Colors.PANEL_LABEL.name())));
        addOption(pl_b.setVisibility(v -> color.getValue().contentEquals(Colors.PANEL_LABEL.name())));
        addOption(pl_a.setVisibility(v -> color.getValue().contentEquals(Colors.PANEL_LABEL.name())));

        endOption();
    }

    private boolean isBusInitialized = false;

    @EventHandler
    private Listener<TickEvent.ClientTickEvent> clientTickEventListener = new Listener<>(event -> {
        if(be_rainbow.isEnabled())
            Colors.BUTTON_ENABLED.setColor(StaticClickGuiColor.newColor(rainbowUtil.getR(), rainbowUtil.getG(), rainbowUtil.getB(), be_a.getValue().intValue()));
    });

    @Override
    public void onEnable() {
        if(Wrapper.getPlayer() != null && Wrapper.getMC().world != null && !(Wrapper.getMC().currentScreen instanceof ClickGuiScreen))
            mc.displayGuiScreen(HTRewrite.INSTANCE.getClickGuiScreen());
        toggle();

        if(!isBusInitialized) {
            HTRewrite.EVENT_BUS.subscribe(this);
            isBusInitialized = true;
        }
    }

    @Override public void onDisable() { }

    @EventHandler
    private Listener<ClientSettingChangeEvent> settingChangeEventListener = new Listener<>(event -> {
        if(event.setting instanceof ValueSetting) {
            if(!getSettings().contains(event.setting))
                return;

            String name = event.setting.getLabel();
            if(name.startsWith("B_"))            /* BUTTON */
                Colors.BUTTON.setColor(StaticClickGuiColor.newColor(b_r.getValue().intValue(), b_g.getValue().intValue(), b_b.getValue().intValue(), b_a.getValue().intValue()));
            else if(name.startsWith("BE_"))      /* BUTTON_ENABLED */
                Colors.BUTTON_ENABLED.setColor(StaticClickGuiColor.newColor(be_r.getValue().intValue(), be_g.getValue().intValue(), be_b.getValue().intValue(), be_a.getValue().intValue()));
            else if(name.startsWith("BH_"))      /* BUTTON_HOVER */
                Colors.BUTTON_HOVER.setColor(StaticClickGuiColor.newColor(bh_r.getValue().intValue(), bh_g.getValue().intValue(), bh_b.getValue().intValue(), bh_a.getValue().intValue()));
            else if(name.startsWith("BEH_"))     /* BUTTON_ENABLED_HOVER */
                Colors.BUTTON_ENABLED_HOVER.setColor(StaticClickGuiColor.newColor(beh_r.getValue().intValue(), beh_g.getValue().intValue(), beh_b.getValue().intValue(), beh_a.getValue().intValue()));
            else if(name.startsWith("BC_"))      /* BUTTON_COMPONENT */
                Colors.BUTTON_COMPONENT.setColor(StaticClickGuiColor.newColor(bc_r.getValue().intValue(), bc_g.getValue().intValue(), bc_b.getValue().intValue(), bc_a.getValue().intValue()));
            else if(name.startsWith("BCE_"))     /* BUTTON_COMPONENT_ENABLED */
                Colors.BUTTON_COMPONENT_ENABLED.setColor(StaticClickGuiColor.newColor(bce_r.getValue().intValue(), bce_g.getValue().intValue(), bce_b.getValue().intValue(), bce_a.getValue().intValue()));
            else if(name.startsWith("BL_"))      /* BUTTON_LABEL */
                Colors.BUTTON_LABEL.setColor(StaticClickGuiColor.newColor(bl_r.getValue().intValue(), bl_g.getValue().intValue(), bl_b.getValue().intValue(), bl_a.getValue().intValue()));
            else if(name.startsWith("BLE_"))     /* BUTTON_LABEL_ENABLED */
                Colors.BUTTON_LABEL_ENABLED.setColor(StaticClickGuiColor.newColor(ble_r.getValue().intValue(), ble_g.getValue().intValue(), ble_b.getValue().intValue(), ble_a.getValue().intValue()));
            else if(name.startsWith("BLH_"))     /* BUTTON_LABEL_HOVER */
                Colors.BUTTON_LABEL_HOVER.setColor(StaticClickGuiColor.newColor(blh_r.getValue().intValue(), blh_g.getValue().intValue(), blh_b.getValue().intValue(), blh_a.getValue().intValue()));
            else if(name.startsWith("PI_"))      /* PANEL_INSIDE */
                Colors.PANEL_INSIDE.setColor(StaticClickGuiColor.newColor(pi_r.getValue().intValue(), pi_g.getValue().intValue(), pi_b.getValue().intValue(), pi_a.getValue().intValue()));
            else if(name.startsWith("PB_"))      /* PANEL_BORDER */
                Colors.PANEL_BORDER.setColor(StaticClickGuiColor.newColor(pb_r.getValue().intValue(), pb_g.getValue().intValue(), pb_b.getValue().intValue(), pb_a.getValue().intValue()));
            else if(name.startsWith("PL_"))      /* PANEL_LABEL */
                Colors.PANEL_LABEL.setColor(StaticClickGuiColor.newColor(pl_r.getValue().intValue(), pl_g.getValue().intValue(), pl_b.getValue().intValue(), pl_a.getValue().intValue()));
        }

        if(event.setting == be_rainbow && !be_rainbow.isEnabled())
            Colors.BUTTON_ENABLED.setColor(StaticClickGuiColor.newColor(be_r.getValue().intValue(), be_g.getValue().intValue(), be_b.getValue().intValue(), be_a.getValue().intValue()));
    });
}