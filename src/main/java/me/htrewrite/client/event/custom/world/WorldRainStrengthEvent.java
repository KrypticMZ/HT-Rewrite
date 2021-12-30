package me.htrewrite.client.event.custom.world;

import me.htrewrite.client.event.custom.CustomEvent;

public class WorldRainStrengthEvent extends CustomEvent {
    public float delta;
    public float strength;
    public WorldRainStrengthEvent(float delta, float strength) {
        this.delta = delta;
        this.strength = strength;
    }
}