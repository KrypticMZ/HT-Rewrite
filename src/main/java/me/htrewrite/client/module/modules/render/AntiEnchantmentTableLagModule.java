package me.htrewrite.client.module.modules.render;

import me.htrewrite.client.mixin.client.MixinTileEntityRendererDispatcher;
import me.htrewrite.client.module.Module;
import me.htrewrite.client.module.ModuleType;

public class AntiEnchantmentTableLagModule extends Module {
    public static boolean blockEnchanting = false;

    public AntiEnchantmentTableLagModule() {
        super("AntiEnchantingLag", "Prevents enchanting table lag.", ModuleType.Render, 0);
        endOption();
    }

    @Override
    public void onEnable() {
        super.onEnable();

        blockEnchanting = true;
    }

    @Override
    public void onDisable() {
        super.onDisable();

        blockEnchanting = false;
    }
}