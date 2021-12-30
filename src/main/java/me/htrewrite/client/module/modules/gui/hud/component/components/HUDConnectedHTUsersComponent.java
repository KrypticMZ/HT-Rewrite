package me.htrewrite.client.module.modules.gui.hud.component.components;

import me.htrewrite.client.module.modules.gui.hud.component.HUDComponent;
import me.htrewrite.client.util.ColorUtils;
import me.htrewrite.client.util.RenderUtils;
import me.pk2.chatserver.ChatAPI;
import net.minecraftforge.client.event.RenderGameOverlayEvent;

import java.awt.*;

public class HUDConnectedHTUsersComponent implements HUDComponent {
    @Override
    public void render(RenderGameOverlayEvent.Text event, int x, int y) {
        int colorRect = ColorUtils.color(0.0F, 0.0F, 0.0F, 0.0F);
        RenderUtils.drawStringWithRect("\u00a77Connected HT+Users: \u00a7f" + ChatAPI.lastKeepAliveUsers.get(), x, y, Color.WHITE.getRGB(),
                colorRect, colorRect);
    }
}