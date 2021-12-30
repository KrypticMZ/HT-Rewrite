package me.htrewrite.client.event.custom.player;

import me.htrewrite.client.event.custom.CustomEvent;

public class UpdateWalkingPlayerEvent extends CustomEvent {
    public UpdateWalkingPlayerEvent(Era era) {
        super();
        this.setEra(era);
    }
}