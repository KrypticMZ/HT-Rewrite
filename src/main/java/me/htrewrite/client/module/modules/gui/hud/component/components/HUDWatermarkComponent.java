package me.htrewrite.client.module.modules.gui.hud.component.components;

import me.htrewrite.client.HTRewrite;
import me.htrewrite.client.module.modules.gui.hud.HUDModule;
import me.htrewrite.client.module.modules.gui.hud.component.HUDComponent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import org.lwjgl.opengl.GL11;

import java.awt.*;

public class HUDWatermarkComponent implements HUDComponent {
    @Override
    public void render(RenderGameOverlayEvent.Text event, int x, int y) {
        GL11.glPushMatrix();
        GL11.glScalef(getScale(), getScale(), getScale());
        mc.fontRenderer.drawStringWithShadow(HTRewrite.NAME + " " + HTRewrite.VERSION, x, y, Color.WHITE.getRGB());
        GL11.glPopMatrix();
    }

    public float getScale() { return HUDModule.watermarkScale.getValue().floatValue(); }
}