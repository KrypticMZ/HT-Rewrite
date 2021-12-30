package me.htrewrite.client.module.modules.gui.hud.component.components;

import me.htrewrite.client.HTRewrite;
import me.htrewrite.client.module.Module;
import me.htrewrite.client.module.modules.gui.hud.HUDModule;
import me.htrewrite.client.module.modules.gui.hud.component.HUDComponent;
import me.htrewrite.client.module.modules.gui.hud.component.components.util.ListUtil;
import me.htrewrite.client.module.modules.gui.hud.component.components.util.ModuleComparator;
import me.htrewrite.client.util.ChatColor;
import me.htrewrite.client.util.ColorUtils;
import me.htrewrite.client.util.RenderUtils;
import me.htrewrite.client.util.Timer;
import net.minecraftforge.client.event.RenderGameOverlayEvent;

import java.awt.*;
import java.util.Collections;
import java.util.List;

public class HUDArrayListComponent implements HUDComponent {
    public List<Module> ordered_modules;
    private ModuleComparator moduleComparator;
    private Timer timer;

    public HUDArrayListComponent() {
        this.moduleComparator = new ModuleComparator();
        this.timer = new Timer();

        ListUtil<Module> listUtil = new ListUtil<>();
        ordered_modules = listUtil.copyArray(HTRewrite.INSTANCE.getModuleManager().getModules());
        Collections.sort(ordered_modules, moduleComparator);
    }

    @Override
    public void render(RenderGameOverlayEvent.Text event, int x, int y) {
        if(timer.passed(100)) {
            Collections.sort(ordered_modules, moduleComparator);
            timer.reset();
        }

        int colorRect = ColorUtils.color(0.0F, 0.0F, 0.0F, 0.0F);
        int colorRect2 = ColorUtils.color(0.0F, 0.0F, 0.0F, 0.5F);

        int yPos = (int)((y/1.5)*getScale());
        int xPos = x;
        for(Module module : ordered_modules) {
            if(!module.isEnabled()) continue;
            RenderUtils.drawStringWithRect(
                    module.getName() + ((module.getMeta().contentEquals("") ? "" : (" " + ChatColor.parse('&', "&7" + module.getMeta())))),
                    xPos,
                    yPos,
                    Color.YELLOW.getRGB(),
                    colorRect,
                    colorRect2);
            yPos += 12;
        }
    }

    private double getScale() { return HUDModule.watermarkScale.getValue(); }
}