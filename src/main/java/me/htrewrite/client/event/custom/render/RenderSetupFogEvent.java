package me.htrewrite.client.event.custom.render;

import me.htrewrite.client.event.custom.CustomEvent;

public class RenderSetupFogEvent extends CustomEvent {
    public int startCoords;
    public float partialTicks;

    public RenderSetupFogEvent(int startCoords, float partialTicks) {
        super();

        this.startCoords = startCoords;
        this.partialTicks = partialTicks;
    }
}