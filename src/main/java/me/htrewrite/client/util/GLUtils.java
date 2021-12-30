package me.htrewrite.client.util;

import net.minecraft.client.renderer.GlStateManager;

import java.awt.*;

public class GLUtils {
    public static void glColor(float red, float green, float blue, float alpha) {

        GlStateManager.color(red, green, blue, alpha);
    }

    public static void glColor(Color color) {

        GlStateManager.color((float) color.getRed() / 255F, (float) color.getGreen() / 255F, (float) color.getBlue() / 255F, (float) color.getAlpha() / 255F);
    }

    public static void glColor(int color) {

        GlStateManager.color((float) (color >> 16 & 255) / 255F, (float) (color >> 8 & 255) / 255F, (float) (color & 255) / 255F, (float) (color >> 24 & 255) / 255F);
    }
}