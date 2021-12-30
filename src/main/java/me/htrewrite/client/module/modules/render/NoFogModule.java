package me.htrewrite.client.module.modules.render;

import me.htrewrite.client.event.custom.render.RenderSetupFogEvent;
import me.htrewrite.client.module.Module;
import me.htrewrite.client.module.ModuleType;
import me.zero.alpine.fork.listener.EventHandler;
import me.zero.alpine.fork.listener.Listener;
import net.minecraft.client.renderer.GlStateManager;

public class NoFogModule extends Module {
    public NoFogModule() {
        super("NoFog", "Prevents fog from rendering.", ModuleType.Render, 0);
        endOption();
    }

    @EventHandler
    private Listener<RenderSetupFogEvent> fogEventListener = new Listener<>(event -> {
        event.cancel();
        
        mc.entityRenderer.setupFogColor(false);
        GlStateManager.glNormal3f(0.0F, -1.0F, 0.0F);
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.colorMaterial(1028, 4608);
    });
}