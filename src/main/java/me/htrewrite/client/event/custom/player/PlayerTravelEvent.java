package me.htrewrite.client.event.custom.player;

import me.htrewrite.client.event.custom.CustomEvent;

public class PlayerTravelEvent extends CustomEvent {
    public float strafe, vertical, forward;
    public PlayerTravelEvent(float strafe, float vertical, float forward) {
        super();
        this.strafe = strafe;
        this.vertical = vertical;
        this.forward = forward;
    }
}