package me.htrewrite.ciruimports;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.CPacketHeldItemChange;

public class InventoryUtil {
    public static final Minecraft mc = Minecraft.getMinecraft();

    public static void switchToSlot(int slot, boolean silent) {
        if (mc.player.inventory.currentItem == slot || slot < 0)
            return;
        if (silent) {
            mc.player.connection.sendPacket(new CPacketHeldItemChange(slot));
            mc.playerController.updateController();
        } else {
            mc.player.connection.sendPacket(new CPacketHeldItemChange(slot));
            mc.player.inventory.currentItem = slot;
            mc.playerController.updateController();
        }
    }

    public static int findHotbarBlock(Class clazz) {
        for (int i = 0; i < 9; i++) {
            ItemStack stack = mc.player.inventory.getStackInSlot(i);
            if (stack != ItemStack.EMPTY) {
                if (clazz.isInstance(stack.getItem()))
                    return i;
                if (stack.getItem() instanceof ItemBlock) {
                    Block block = ((ItemBlock) stack.getItem()).getBlock();
                    if (clazz.isInstance(block))
                        return i;
                }
            }
        }
        return -1;
    }
}