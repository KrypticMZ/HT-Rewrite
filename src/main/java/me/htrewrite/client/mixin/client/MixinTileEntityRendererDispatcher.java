package me.htrewrite.client.mixin.client;

import me.htrewrite.client.module.modules.render.AntiEnchantmentTableLagModule;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityEnchantmentTable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(TileEntityRendererDispatcher.class)
public class MixinTileEntityRendererDispatcher {
    @Inject(method = "render(Lnet/minecraft/tileentity/TileEntity;FI)V", at = @At("HEAD"), cancellable = true)
    public void render(TileEntity entity, float partialTicks, int destroyStage, CallbackInfo callbackInfo) {
        if(AntiEnchantmentTableLagModule.blockEnchanting && entity.getClass() == TileEntityEnchantmentTable.class)
            callbackInfo.cancel();
    }
}