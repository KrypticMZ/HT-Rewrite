package me.htrewrite.client.event.custom.player;

import me.htrewrite.client.event.custom.CustomEvent;
import net.minecraft.entity.Entity;

public class PlayerApplyCollisionEvent extends CustomEvent {
    public Entity entity;
    public PlayerApplyCollisionEvent(Entity entity) {
        super();
        this.entity = entity;
    }
}