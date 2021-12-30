package me.htrewrite.client.event.custom.render;

import me.htrewrite.client.event.custom.CustomEvent;
import net.minecraft.item.ItemStack;

public class RenderTooltipEvent extends CustomEvent {
    private ItemStack item;
    private int x, y;

    public RenderTooltipEvent(ItemStack item, int x, int y) {
        this.item = item;
        this.x = x;
        this.y = y;
    }

    public ItemStack getItemStack() { return item; }
    public int getX() { return x; }
    public int getY() { return y; }
}