package me.htrewrite.client.event.custom.player;

import me.htrewrite.client.event.custom.CustomEvent;
import net.minecraft.entity.MoverType;

public class PlayerMoveEvent extends CustomEvent {
    public MoverType moverType;
    public double x, y, z;
    public PlayerMoveEvent(MoverType moverType, double x, double y, double z) {
        this.moverType = moverType;
        this.x = x;
        this.y = y;
        this.z = z;
    }
}