package me.htrewrite.client.event.custom.player;

import me.htrewrite.client.event.custom.CustomEvent;

public class PlayerMotionUpdateEvent extends CustomEvent {
    public PlayerMotionUpdateEvent(Era era) { super(); setEra(era); }
}