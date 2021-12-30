package me.htrewrite.client.event.custom.render;

import me.htrewrite.client.event.custom.CustomEvent;

public class RenderHurtCameraEvent extends CustomEvent {
    public float ticks;

    public RenderHurtCameraEvent(float ticks) {
        super();
        this.ticks = ticks;
    }
}