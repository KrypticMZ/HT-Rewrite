package me.htrewrite.client.module.modules.gui.hud.component.components;

import me.htrewrite.client.HTRewrite;
import me.htrewrite.client.module.modules.gui.hud.component.HUDComponent;
import me.htrewrite.client.util.ColorUtils;
import me.htrewrite.client.util.RenderUtils;
import net.minecraftforge.client.event.RenderGameOverlayEvent;

import java.awt.*;

public class HUDTPSComponent implements HUDComponent {
    @Override
    public void render(RenderGameOverlayEvent.Text event, int x, int y) {
        int colorRect = ColorUtils.color(0.0F, 0.0F, 0.0F, 0.0F);

        double tps = Math.round(HTRewrite.INSTANCE.getTickRateManager().getTickRate()*100.0)/100.0;
        String coloredTPS = ("\u00a7"+((tps >= 16d)?"a":(tps >= 10d)?"6":"c"))+tps;

        RenderUtils.drawStringWithRect("\u00a77TPS: " + coloredTPS, x, y, Color.WHITE.getRGB(),
                colorRect, colorRect);
    }
}