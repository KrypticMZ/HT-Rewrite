package me.htrewrite.client.util.shader.shaders;

import me.htrewrite.client.util.shader.FramebufferShader;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import org.lwjgl.opengl.GL20;

public class AquaShader extends FramebufferShader {
    public static AquaShader AQUA_SHADER = new AquaShader();
    public float time;

    public AquaShader() {
        super("aqua.frag");
    }

    @Override
    public void setupUniforms() {
        this.setupUniform("resolution");
        this.setupUniform("time");
    }

    @Override
    public void updateUniforms() {
        GL20.glUniform2f(this.getUniform("resolution"), (float)new ScaledResolution(Minecraft.getMinecraft()).getScaledWidth(), (float)new ScaledResolution(Minecraft.getMinecraft()).getScaledHeight());
        GL20.glUniform1f(this.getUniform("time"), (float)this.time);
        this.time += 0.003f * (float) getDeltaTime();
    }
}


