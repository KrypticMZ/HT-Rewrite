package me.htrewrite.client.event.custom.entity;

import me.htrewrite.client.event.custom.CustomEvent;
import net.minecraft.util.math.MathHelper;

public class EntityLookDirectionEvent extends CustomEvent {
    public float yaw, pitch;
    public float fixedYaw, fixedPitch;

    public EntityLookDirectionEvent(float yaw, float pitch) {
        super();

        this.pitch = pitch;
        this.fixedPitch = MathHelper.clamp(pitch, -90f, 90f);
        this.fixedYaw = this.yaw = yaw;
    }
}