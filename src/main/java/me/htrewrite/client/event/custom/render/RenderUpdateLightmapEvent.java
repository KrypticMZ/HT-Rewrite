package me.htrewrite.client.event.custom.render;

import net.minecraftforge.fml.common.eventhandler.Event;

public class RenderUpdateLightmapEvent extends Event {
    public float PartialTicks;

    public void EventRenderUpdateLightmap(float p_PartialTicks)
    {
        PartialTicks = p_PartialTicks;
    }

}
