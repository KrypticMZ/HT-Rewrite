package me.htrewrite.client.mixin;

import me.htrewrite.client.HTRewrite;
import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin;
import org.spongepowered.asm.launch.MixinBootstrap;
import org.spongepowered.asm.mixin.MixinEnvironment;
import org.spongepowered.asm.mixin.Mixins;

import java.util.Map;

public class MixinLoaderForge implements IFMLLoadingPlugin {
    private static boolean isObfuscatedEnvironment = false;

    public MixinLoaderForge() {
        HTRewrite.logger.info("Mixins are loading...");
        MixinBootstrap.init();
        Mixins.addConfigurations("mixins.htrewrite.json");
        MixinEnvironment.getDefaultEnvironment().setObfuscationContext("searge");
        HTRewrite.logger.info(MixinEnvironment.getDefaultEnvironment().getObfuscationContext());
    }

    @Override public String[] getASMTransformerClass() { return new String[0]; }
    @Override public String getModContainerClass() { return null; }
    @Override public String getSetupClass() { return null; }
    @Override public void injectData(Map<String, Object> map) { isObfuscatedEnvironment = (boolean)map.get("runtimeDeobfuscationEnabled"); }
    @Override public String getAccessTransformerClass() { return null; }
}