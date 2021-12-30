package me.htrewrite.client.event.custom.render;

import me.htrewrite.client.event.custom.CustomEvent;
import net.minecraft.entity.Entity;

public class RenderEntityEvent extends CustomEvent {
    public Entity entity;
    public double x, y, z;
    public float yaw;
    public boolean p_188391_10_;
    public RenderEntityEvent(Entity entityIn, double x, double y, double z, float yaw, float partialTicks, boolean p_188391_10_) {
        super(partialTicks);
        this.entity = entityIn;
        this.x = x;
        this.y = y;
        this.z = z;
        this.yaw = yaw;
        this.p_188391_10_ = p_188391_10_;
    }
}