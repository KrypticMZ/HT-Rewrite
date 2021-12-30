package me.htrewrite.client.util.shader;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.shader.Framebuffer;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;

public abstract class FramebufferShader extends Shader {
    public static int deltaTime;
    public Minecraft mc = Minecraft.getMinecraft();
    public static Framebuffer framebuffer;
    public float red;
    public float green;
    public float blue;
    public float alpha = 1f;
    public float radius = 2f;
    public float quality = 1f;
    public boolean entityShadows;

    public FramebufferShader(String fragmentShader) {
        super(fragmentShader);
    }

    public void startDraw(final float partialTicks) {
        GlStateManager.enableAlpha();
        GlStateManager.pushMatrix();
        GlStateManager.pushAttrib();
        (FramebufferShader.framebuffer = this.setupFrameBuffer(FramebufferShader.framebuffer)).framebufferClear();
        FramebufferShader.framebuffer.bindFramebuffer(true);
        this.entityShadows = this.mc.gameSettings.entityShadows;
        this.mc.gameSettings.entityShadows = false;
        this.mc.entityRenderer.updateRenderer();
    }

    public void stopDraw(final float red, final float green, final float blue, final float alpha, final float radius, final float quality) {
        this.mc.gameSettings.entityShadows = this.entityShadows;
        GL11.glEnable(3042);
        GL11.glBlendFunc(770, 771);
        this.mc.getFramebuffer().bindFramebuffer(true);
        this.red = red / 255f;
        this.green = green / 255f;
        this.blue = blue / 255f;
        this.alpha = alpha / 255f;
        this.radius = radius;
        this.quality = quality;
        this.mc.entityRenderer.disableLightmap();
        RenderHelper.disableStandardItemLighting();
        this.startShader();
        this.mc.entityRenderer.setupOverlayRendering();
        this.drawFramebuffer(FramebufferShader.framebuffer);
        this.stopShader();
        this.mc.entityRenderer.disableLightmap();
        GlStateManager.popMatrix();
        GlStateManager.popAttrib();
    }

    public Framebuffer setupFrameBuffer(Framebuffer frameBuffer) {
        if (frameBuffer != null) {
            frameBuffer.deleteFramebuffer();
        }
        frameBuffer = new Framebuffer(this.mc.displayWidth, this.mc.displayHeight, true);
        return frameBuffer;
    }

    public void drawFramebuffer(final Framebuffer framebuffer) {
        final ScaledResolution scaledResolution = new ScaledResolution(this.mc);
        GL11.glBindTexture(3553, framebuffer.framebufferTexture);
        GL11.glBegin(7);
        GL11.glTexCoord2d(0d, 1d);
        GL11.glVertex2d(0d, 0d);
        GL11.glTexCoord2d(0d, 0d);
        GL11.glVertex2d(0d, scaledResolution.getScaledHeight());
        GL11.glTexCoord2d(1d, 0d);
        GL11.glVertex2d(scaledResolution.getScaledWidth(), scaledResolution.getScaledHeight());
        GL11.glTexCoord2d(1d, 1d);
        GL11.glVertex2d(scaledResolution.getScaledWidth(), 0d);
        GL11.glEnd();
        GL20.glUseProgram(0);
    }

    public void setDeltaTime(int deltaTime) {
        this.deltaTime = deltaTime;
    }

    public int getDeltaTime() {
        return this.deltaTime;
    }

}

