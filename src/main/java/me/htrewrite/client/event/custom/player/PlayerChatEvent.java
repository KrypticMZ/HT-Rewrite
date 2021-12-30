package me.htrewrite.client.event.custom.player;

import me.htrewrite.client.event.custom.CustomEvent;

public class PlayerChatEvent extends CustomEvent {
    public final String message;

    public PlayerChatEvent(String message) {
        super();
        this.message = message;
    }
}