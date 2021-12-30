package me.htrewrite.client.module.modules.gui.hud;

import me.htrewrite.client.clickgui.components.buttons.settings.bettermode.BetterMode;
import me.htrewrite.client.module.Module;
import me.htrewrite.client.module.ModuleType;
import me.htrewrite.client.module.modules.gui.hud.component.HUDComponentManager;
import me.htrewrite.client.module.modules.gui.hud.component.components.*;
import me.htrewrite.exeterimports.mcapi.settings.ModeSetting;
import me.htrewrite.exeterimports.mcapi.settings.ToggleableSetting;
import me.htrewrite.exeterimports.mcapi.settings.ValueSetting;
import me.zero.alpine.fork.listener.EventHandler;
import me.zero.alpine.fork.listener.Listener;
import net.minecraftforge.client.event.RenderGameOverlayEvent;

public class HUDModule extends Module {
    public static final ModeSetting setting = new ModeSetting("Setting", null, 0, BetterMode.construct("Toggle", "Edit"));
    public static final ToggleableSetting watermark = new ToggleableSetting("Watermark", null, true);
    public static final ToggleableSetting position = new ToggleableSetting("Position", null, true);
    public static final ToggleableSetting fps = new ToggleableSetting("FPS", null, true);
    public static final ToggleableSetting arraylist = new ToggleableSetting("ArrayList", null, true);
    public static final ToggleableSetting tps = new ToggleableSetting("TPS", null, false);
    public static final ToggleableSetting htUsers = new ToggleableSetting("HT+Users", null, false);
    public static final ToggleableSetting playtime = new ToggleableSetting("Playtime", null, false);

    public static final ModeSetting moduleEdit = new ModeSetting("SModule", null, 0, BetterMode.construct("Watermark", "Position", "FPS", "ArrayList", "TPS", "HT+Users", "Playtime"));
    /* WATERMARK */
    public static final ValueSetting<Double> watermarkX = new ValueSetting<>("WaterX", null, 4D, 0D, 1000D);
    public static final ValueSetting<Double> watermarkY = new ValueSetting<>("WaterY", null, 4D, 0D, 1000D);
    public static final ValueSetting<Double> watermarkScale = new ValueSetting<>("WaterSize", null, 1.5D, 0.5D, 3D);
    /* POSITION */
    public static final ValueSetting<Double> positionX = new ValueSetting<>("PosX", null, 4d, 0D, 1000D);
    public static final ValueSetting<Double> positionY = new ValueSetting<>("PosY", null, 0d, 0d, 1000d);
    /* FPS */
    public static final ValueSetting<Double> fpsX = new ValueSetting<>("FPSX", null, 4d, 0D, 1000D);
    public static final ValueSetting<Double> fpsY = new ValueSetting<>("FPSY", null, 0d, 0d, 1000d);
    /* ArrayList */
    public static final ValueSetting<Double> arrayX = new ValueSetting<>("ArrayX", null, 4d, 0d, 1000d);
    public static final ValueSetting<Double> arrayY = new ValueSetting<>("ArrayY", null, 24d, 0d, 1000d);
    /* TPS */
    public static final ValueSetting<Double> tpsX = new ValueSetting<>("TPSX", null, 0d, 0d, 1000d);
    public static final ValueSetting<Double> tpsY = new ValueSetting<>("TPSY", null, 0d, 0d, 1000d);
    /* HT+Users */
    public static final ValueSetting<Double> htUsersX = new ValueSetting<>("HT+UsersX", null, 0d, 0d, 1000d);
    public static final ValueSetting<Double> htUsersY = new ValueSetting<>("HT+UsersY", null, 0d, 0d, 1000d);
    /* PLAYTIME */
    public static final ValueSetting<Double> playtimeX = new ValueSetting<>("PlaytimeX", null, 0d, 0d, 1000d);
    public static final ValueSetting<Double> playtimeY = new ValueSetting<>("PlaytimeY", null, 0d, 0d, 1000d);

    public HUDComponentManager hudComponentManager;
    private HUDWatermarkComponent hudWatermarkComponent;
    private HUDPositionComponent hudPositionComponent;
    private HUDFPSComponent hudfpsComponent;
    private HUDArrayListComponent hudArrayListComponent;
    private HUDTPSComponent hudTPSComponent;
    private HUDConnectedHTUsersComponent hudConnectedHTUsersComponent;
    private HUDPlaytimeComponent hudPlaytimeComponent;
    public HUDModule() {
        super("HUD", "Interface", ModuleType.Gui, 0);
        addOption(setting);
        /* SETTINGS */
        addOption(watermark.setVisibility(a -> setting.getI() == 0));
        addOption(position.setVisibility(a -> setting.getI() == 0));
        addOption(fps.setVisibility(a -> setting.getI() == 0));
        addOption(arraylist.setVisibility(a -> setting.getI() == 0));
        addOption(tps.setVisibility(a -> setting.getI() == 0));
        addOption(htUsers.setVisibility(a -> setting.getI() == 0));
        addOption(playtime.setVisibility(a -> setting.getI() == 0));
        /* EDIT */
        addOption(moduleEdit.setVisibility(v -> setting.getI()==1));
        // - Water - \\
        addOption(watermarkX.setVisibility(v -> setting.getI()==1&&moduleEdit.getI()==0));
        addOption(watermarkY.setVisibility(v -> setting.getI()==1&&moduleEdit.getI()==0));
        addOption(watermarkScale.setVisibility(v -> setting.getI()==1&&moduleEdit.getI()==0));
        // - Position - \\
        addOption(positionX.setVisibility(v -> setting.getI()==1&&moduleEdit.getI()==1));
        addOption(positionY.setVisibility(v -> setting.getI()==1&&moduleEdit.getI()==1));
        // - FPS - \\
        addOption(fpsX.setVisibility(v -> setting.getI()==1&&moduleEdit.getI()==2));
        addOption(fpsY.setVisibility(v -> setting.getI()==1&&moduleEdit.getI()==2));
        // - ArrayList - \\
        addOption(arrayX.setVisibility(v -> setting.getI()==1&&moduleEdit.getI()==3));
        addOption(arrayY.setVisibility(v -> setting.getI()==1&&moduleEdit.getI()==3));
        // - TPS - \\
        addOption(tpsX.setVisibility(v -> setting.getI()==1&&moduleEdit.getI()==4));
        addOption(tpsY.setVisibility(v -> setting.getI()==1&&moduleEdit.getI()==4));
        // - Connected HT+Users - \\
        addOption(htUsersX.setVisibility(v -> setting.getI()==1&&moduleEdit.getI()==5));
        addOption(htUsersY.setVisibility(v -> setting.getI()==1&&moduleEdit.getI()==5));
        // - Playtime - \\
        addOption(playtimeX.setVisibility(v -> setting.getI()==1&&moduleEdit.getI()==6));
        addOption(playtimeY.setVisibility(v -> setting.getI()==1&&moduleEdit.getI()==6));

        endOption();
    }

    @Override
    public void onEnable() {
        super.onEnable();

        this.hudComponentManager = new HUDComponentManager();
        this.hudWatermarkComponent = (HUDWatermarkComponent)hudComponentManager.getComponentByClass(HUDWatermarkComponent.class);
        this.hudPositionComponent = (HUDPositionComponent)hudComponentManager.getComponentByClass(HUDPositionComponent.class);
        this.hudfpsComponent = (HUDFPSComponent)hudComponentManager.getComponentByClass(HUDFPSComponent.class);
        this.hudArrayListComponent = (HUDArrayListComponent)hudComponentManager.getComponentByClass(HUDArrayListComponent.class);
        this.hudTPSComponent = (HUDTPSComponent)hudComponentManager.getComponentByClass(HUDTPSComponent.class);
        this.hudConnectedHTUsersComponent = (HUDConnectedHTUsersComponent)hudComponentManager.getComponentByClass(HUDConnectedHTUsersComponent.class);
        this.hudPlaytimeComponent = (HUDPlaytimeComponent) hudComponentManager.getComponentByClass(HUDPlaytimeComponent.class);
    }

    @EventHandler
    private Listener<RenderGameOverlayEvent.Text> textListener = new Listener<>(event -> {
        if(watermark.isEnabled())
            hudWatermarkComponent.render(event, watermarkX.getValue().intValue(), watermarkY.getValue().intValue());
        if(position.isEnabled())
            hudPositionComponent.render(event, positionX.getValue().intValue(), positionY.getValue().intValue());
        if(fps.isEnabled())
            hudfpsComponent.render(event, fpsX.getValue().intValue(), fpsY.getValue().intValue());
        if(arraylist.isEnabled())
            hudArrayListComponent.render(event, arrayX.getValue().intValue(), arrayY.getValue().intValue());
        if(tps.isEnabled())
            hudTPSComponent.render(event, tpsX.getValue().intValue(), tpsY.getValue().intValue());
        if(htUsers.isEnabled())
            hudConnectedHTUsersComponent.render(event, htUsersX.getValue().intValue(), htUsersY.getValue().intValue());
        if (playtime.isEnabled())
            hudPlaytimeComponent.render(event, playtimeX.getValue().intValue(), playtimeY.getValue().intValue());
    });
}