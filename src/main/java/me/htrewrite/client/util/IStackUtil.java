package me.htrewrite.client.util;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.item.ItemStack;

import java.util.Map;

public class IStackUtil {
    public static boolean containsEnchantment(ItemStack itemStack, Enchantment enchantment) {
        Map<Enchantment, Integer> enchantments = EnchantmentHelper.getEnchantments(itemStack);
        if(enchantments.size() == 0)
            return false;
        for(Enchantment entryEnchantment : enchantments.keySet())
            if(entryEnchantment.getName().contentEquals(enchantment.getName()))
                return true;
        return false;
    }
}