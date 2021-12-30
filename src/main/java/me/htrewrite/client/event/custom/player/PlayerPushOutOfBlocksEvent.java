package me.htrewrite.client.event.custom.player;

import me.htrewrite.client.event.custom.CustomEvent;

public class PlayerPushOutOfBlocksEvent extends CustomEvent {
    public double x,y,z;
    public PlayerPushOutOfBlocksEvent(double x, double y, double z) {
        super();
        this.x = x;
        this.y = y;
        this.z = z;
    }
}