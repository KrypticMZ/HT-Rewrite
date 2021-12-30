package me.htrewrite.client.event.custom.world;

import me.htrewrite.client.event.custom.CustomEvent;
import net.minecraft.entity.Entity;

public class EntityAddedEvent extends CustomEvent {
    private Entity entity;
    public EntityAddedEvent(Entity entity) { super(); this.entity = entity; }

    public Entity getEntity() { return entity; }
}