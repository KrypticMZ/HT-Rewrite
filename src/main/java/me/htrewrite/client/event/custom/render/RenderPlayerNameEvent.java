package me.htrewrite.client.event.custom.render;

import me.htrewrite.client.event.custom.CustomEvent;
import net.minecraft.client.entity.AbstractClientPlayer;

public class RenderPlayerNameEvent extends CustomEvent {
    public AbstractClientPlayer entityIn;
    public double x, y, z;
    public String name;
    public double distanceSq;
    public RenderPlayerNameEvent(AbstractClientPlayer entityIn, double x, double y, double z, String name, double distanceSq) {
        this.entityIn = entityIn;
        this.x = x;
        this.y = y;
        this.z = z;
        this.name = name;
        this.distanceSq = distanceSq;
    }
}