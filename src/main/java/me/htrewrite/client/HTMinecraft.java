package me.htrewrite.client;

import me.htrewrite.client.customgui.SplashProgressGui;
import net.minecraft.client.Minecraft;

public class HTMinecraft {
    private Minecraft parent;

    public HTMinecraft(Minecraft parent) {
        this.parent = parent;
    }

    public void onStartGame() {
        SplashProgressGui.setProgress(1, "Starting Game...");
    }

    public void onLoadDefaultResourcePack() {
        SplashProgressGui.setProgress(2, "Loading Resources...");
    }

    public void onCreateDisplay() {
        SplashProgressGui.setProgress(3, "Creating Display...");
    }

    public void onLoadTexture() {
        SplashProgressGui.setProgress(4, "Initializing Textures...");
    }

    public static String getServer() { return Minecraft.getMinecraft().getCurrentServerData() == null ? "singleplayer":Minecraft.getMinecraft().getCurrentServerData().serverIP; }
}