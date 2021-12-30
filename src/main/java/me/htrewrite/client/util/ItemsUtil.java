package me.htrewrite.client.util;

import net.minecraft.client.Minecraft;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class ItemsUtil {
    private static final Minecraft mc = Minecraft.getMinecraft();

    public static int getItemCount(Item input) {
        if (mc.player == null)
            return 0;

        int items = 0;
        for (int i = 0; i < 45; i++) {
            final ItemStack stack = mc.player.inventory.getStackInSlot(i);
            if (stack.getItem() == input)
                items += stack.getCount();
        } return items;
    }
}