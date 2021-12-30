package me.htrewrite.client.module.modules.gui.hud.component;

import me.htrewrite.client.module.modules.gui.hud.component.components.*;

import java.util.ArrayList;

public class HUDComponentManager {
    public static HUDComponentManager INSTANCE;

    private ArrayList<HUDComponent> hudComponents;
    public HUDComponentManager() {
        INSTANCE = this;

        hudComponents = new ArrayList<>();
        hudComponents.add(new HUDArrayListComponent());
        hudComponents.add(new HUDConnectedHTUsersComponent());
        hudComponents.add(new HUDFPSComponent());
        hudComponents.add(new HUDPlaytimeComponent());
        hudComponents.add(new HUDPositionComponent());
        hudComponents.add(new HUDTPSComponent());
        hudComponents.add(new HUDWatermarkComponent());
    }

    public HUDComponent getComponentByClass(Class<?> componentClass) {
        for(HUDComponent component : hudComponents)
            if(component.getClass() == componentClass)
                return component;
        return null;
    }
}