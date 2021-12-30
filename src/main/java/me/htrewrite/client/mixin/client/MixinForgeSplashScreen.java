package me.htrewrite.client.mixin.client;

import net.minecraftforge.fml.client.SplashProgress;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(SplashProgress.class)
public class MixinForgeSplashScreen {
    @Shadow
    private static boolean enabled;

    @Overwrite
    public static void start() { enabled = false; }
}