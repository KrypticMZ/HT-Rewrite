package me.htrewrite.client.module.modules.gui.hud.component;

import me.htrewrite.client.HTRewrite;
import me.htrewrite.client.module.modules.gui.hud.HUDModule;
import net.minecraft.client.Minecraft;
import net.minecraftforge.client.event.RenderGameOverlayEvent;

public interface HUDComponent {
    Minecraft mc = Minecraft.getMinecraft();

    void render(RenderGameOverlayEvent.Text event, int x, int y);

    default HUDModule getHUDInstance() { return (HUDModule) HTRewrite.INSTANCE.getModuleManager().getModuleByClass(HUDModule.class); }
}