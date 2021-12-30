package me.htrewrite.client.event.custom.player;

import me.htrewrite.client.event.custom.CustomEvent;
import net.minecraft.potion.Potion;

public class PlayerIsPotionActiveEvent extends CustomEvent {
    public Potion potion;
    public PlayerIsPotionActiveEvent(Potion potion) {
        super();
        this.potion = potion;
    }
}