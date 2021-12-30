package me.htrewrite.client.module.modules.gui.hud.component.components;

import me.htrewrite.client.module.modules.gui.hud.component.HUDComponent;
import me.htrewrite.client.util.ColorUtils;
import me.htrewrite.client.util.RenderUtils;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.world.DimensionType;
import net.minecraftforge.client.event.RenderGameOverlayEvent;

import java.awt.*;

public class HUDFPSComponent implements HUDComponent {
    @Override
    public void render(RenderGameOverlayEvent.Text event, int x, int y) {
        int colorRect = ColorUtils.color(0.0F, 0.0F, 0.0F, 0.0F);
        int colorRect2 = ColorUtils.color(0.0F, 0.0F, 0.0F, 0.5F);
        boolean isChatOpen = mc.currentScreen instanceof GuiChat;
        ScaledResolution sr = new ScaledResolution(mc);

        int scaledHeight = sr.getScaledHeight() - y;
        int heightFPS = isChatOpen ? scaledHeight - 37 : scaledHeight - 22;
        RenderUtils.drawStringWithRect("\u00a77FPS: \u00a7f" + mc.getDebugFPS(), x, heightFPS, Color.WHITE.getRGB(),
                colorRect, colorRect2);
    }
}