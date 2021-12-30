package me.htrewrite.client.module.modules.gui.hud.component.components;

import me.htrewrite.client.module.modules.gui.hud.component.HUDComponent;
import me.htrewrite.client.util.ColorUtils;
import me.htrewrite.client.util.RenderUtils;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.world.DimensionType;
import net.minecraftforge.client.event.RenderGameOverlayEvent;

import java.awt.*;

public class HUDPositionComponent implements HUDComponent {
    @Override
    public void render(RenderGameOverlayEvent.Text event, int renderX, int renderY) {
        int colorRect = ColorUtils.color(0.0F, 0.0F, 0.0F, 0.0F);
        int colorRect2 = ColorUtils.color(0.0F, 0.0F, 0.0F, 0.5F);
        boolean isChatOpen = mc.currentScreen instanceof GuiChat;
        ScaledResolution sr = new ScaledResolution(mc);
        boolean nether = mc.world.provider.isNether();
        boolean end = mc.world.provider.getDimensionType() == DimensionType.THE_END;

        double x = mc.player.posX;
        double y = mc.player.posY;
        double z = mc.player.posZ;
        String coords = String.format("\u00a77X: \u00a7f%s \u00a77Y: \u00a7f%s \u00a77Z: \u00a7f%s", RenderUtils.DF((float)x, 1), RenderUtils.DF((float)y, 1), RenderUtils.DF((float)z, 1));
        if(nether) {
            double tX = x*8;
            double tZ = z*8;
            coords = String.format("\u00a77X: \u00a7f%s \u00a77Y: \u00a7f%s \u00a77Z: \u00a7f%s \u00a77(\u00a7f%s \u00a7f%s\u00a77)", RenderUtils.DF((float)x, 1), RenderUtils.DF((float)y, 1), RenderUtils.DF((float)z, 1), RenderUtils.DF((float)tX, 1), RenderUtils.DF((float)tZ, 1));
        } else if(!nether && !end) {
            double tX = x/8;
            double tZ = z/8;
            coords = String.format("\u00a77X: \u00a7f%s \u00a77Y: \u00a7f%s \u00a77Z: \u00a7f%s \u00a77(\u00a7f%s \u00a7f%s\u00a77)", RenderUtils.DF((float)x, 1), RenderUtils.DF((float)y, 1), RenderUtils.DF((float)z, 1), RenderUtils.DF((float)tX, 1), RenderUtils.DF((float)tZ, 1));
        }

        int scaledHeight = sr.getScaledHeight() - renderY;
        int heightCoords = isChatOpen ? scaledHeight - 25 : scaledHeight - 10;

        RenderUtils.drawStringWithRect(coords, renderX, heightCoords, Color.WHITE.getRGB(),
                colorRect, colorRect2);
    }
}