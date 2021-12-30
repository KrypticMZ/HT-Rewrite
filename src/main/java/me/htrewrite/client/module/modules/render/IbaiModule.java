package me.htrewrite.client.module.modules.render;

import me.htrewrite.client.module.Module;
import me.htrewrite.client.module.ModuleType;
import me.zero.alpine.fork.listener.EventHandler;
import me.zero.alpine.fork.listener.Listener;
import net.minecraftforge.client.event.RenderPlayerEvent;
import org.lwjgl.opengl.GL11;

public class IbaiModule extends Module {
    public IbaiModule() {
        super("Ibai", "Ibai.", ModuleType.Render, 0);
        endOption();
    }

    @EventHandler
    private Listener<RenderPlayerEvent.Pre> eventListener = new Listener<>(event -> {
        if(event.getEntity() == mc.player)
            GL11.glScalef(10f, .8f, 6f);
    });
}