package me.htrewrite.client.module.modules.render;

import static org.lwjgl.opengl.GL11.*;

import me.htrewrite.client.HTRewrite;
import me.htrewrite.client.clickgui.components.buttons.settings.bettermode.BetterMode;
import me.htrewrite.client.event.custom.CustomEvent;
import me.htrewrite.client.event.custom.render.RenderEntityEvent;
import me.htrewrite.client.module.Module;
import me.htrewrite.client.module.ModuleType;
import me.htrewrite.exeterimports.mcapi.settings.ModeSetting;
import me.htrewrite.exeterimports.mcapi.settings.ToggleableSetting;
import me.htrewrite.exeterimports.mcapi.settings.ValueSetting;
import me.zero.alpine.fork.listener.EventHandler;
import me.zero.alpine.fork.listener.Listener;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.entity.RenderEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityBoat;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.passive.IAnimals;
import net.minecraft.entity.player.EntityPlayer;
import org.lwjgl.opengl.GL11;

import java.awt.*;

public class ChamsModule extends Module {
    public static final ModeSetting mode = new ModeSetting("Mode", null, 0, BetterMode.construct("NORMAL", "TEXTURE", "FLAT", "WIREFRAME"));

    public static final ModeSetting config = new ModeSetting("Config", null, 0, BetterMode.construct("Players", "Mobs", "Animals", "Vehicles", "Items", "Crystals", "Friends", "Sneaking"));
    /* Players */
    public static final ToggleableSetting players_enabled = new ToggleableSetting("PEnabled", null, true);
    public static final ValueSetting<Double> players_red = new ValueSetting<>("PRed", null, 255d, 0d, 255d);
    public static final ValueSetting<Double> players_green = new ValueSetting<>("PGreen", null, 68d, 0d, 255d);
    public static final ValueSetting<Double> players_blue = new ValueSetting<>("PBlue", null, 68d, 0d, 255d);
    /* Mobs */
    public static final ToggleableSetting mobs_enabled = new ToggleableSetting("MEnabled", null, true);
    public static final ValueSetting<Double> mobs_red = new ValueSetting<>("MRed", null, 255d, 0d, 255d);
    public static final ValueSetting<Double> mobs_green = new ValueSetting<>("MGreen", null, 170d, 0d, 255d);
    public static final ValueSetting<Double> mobs_blue = new ValueSetting<>("MBlue", null, 0d, 0d, 255d);
    /* Animals */
    public static final ToggleableSetting animals_enabled = new ToggleableSetting("AEnabled", null, true);
    public static final ValueSetting<Double> animals_red = new ValueSetting<>("ARed", null, 0d, 0d, 255d);
    public static final ValueSetting<Double> animals_green = new ValueSetting<>("AGreen", null, 255d, 0d, 255d);
    public static final ValueSetting<Double> animals_blue = new ValueSetting<>("ABlue", null, 68d, 0d, 255d);
    /* Vehicles */
    public static final ToggleableSetting vehicles_enabled = new ToggleableSetting("VEnabled", null, true);
    public static final ValueSetting<Double> vehicles_red = new ValueSetting<>("VRed", null, 213d, 0d, 255d);
    public static final ValueSetting<Double> vehicles_green = new ValueSetting<>("VGreen", null, 255d, 0d, 255d);
    public static final ValueSetting<Double> vehicles_blue = new ValueSetting<>("VBlue", null, 0d, 0d, 255d);
    /* Items */
    public static final ToggleableSetting items_enabled = new ToggleableSetting("IEnabled", null, true);
    public static final ValueSetting<Double> items_red = new ValueSetting<>("IRed", null, 0d, 0d, 255d);
    public static final ValueSetting<Double> items_green = new ValueSetting<>("IGreen", null, 255d, 0d, 255d);
    public static final ValueSetting<Double> items_blue = new ValueSetting<>("IBlue", null, 170d, 0d, 255d);
    /* Crystals */
    public static final ToggleableSetting crystals_enabled = new ToggleableSetting("CEnabled", null, true);
    public static final ValueSetting<Double> crystals_red = new ValueSetting<>("CRed", null, 205d, 0d, 255d);
    public static final ValueSetting<Double> crystals_green = new ValueSetting<>("CGreen", null, 0d, 0d, 255d);
    public static final ValueSetting<Double> crystals_blue = new ValueSetting<>("CBlue", null, 205d, 0d, 255d);
    /* Friends */
    public static final ValueSetting<Double> friends_red = new ValueSetting<>("FRed", null, 153d, 0d, 255d);
    public static final ValueSetting<Double> friends_green = new ValueSetting<>("FGreen", null, 0d, 0d, 255d);
    public static final ValueSetting<Double> friends_blue = new ValueSetting<>("FBlue", null, 238d, 0d, 255d);
    /* Sneaking */
    public static final ValueSetting<Double> sneaking_red = new ValueSetting<>("SRed", null, 238d, 0d, 255d);
    public static final ValueSetting<Double> sneaking_green = new ValueSetting<>("SGreen", null, 153d, 0d, 255d);
    public static final ValueSetting<Double> sneaking_blue = new ValueSetting<>("SBlue", null, 0d, 0d, 255d);

    @Override public String getMeta() { return mode.getValue(); }
    public ChamsModule() {
        super("Chams", "See entities through walls.", ModuleType.Render, 0);
        addOption(mode);
        addOption(config);
        /* Players */
        addOption(players_enabled.setVisibility(v -> config.getValue().contentEquals("Players")));
        addOption(players_red.setVisibility(v -> config.getValue().contentEquals("Players")));
        addOption(players_green.setVisibility(v -> config.getValue().contentEquals("Players")));
        addOption(players_blue.setVisibility(v -> config.getValue().contentEquals("Players")));
        /* Mobs */
        addOption(mobs_enabled.setVisibility(v -> config.getValue().contentEquals("Mobs")));
        addOption(mobs_red.setVisibility(v -> config.getValue().contentEquals("Mobs")));
        addOption(mobs_green.setVisibility(v -> config.getValue().contentEquals("Mobs")));
        addOption(mobs_blue.setVisibility(v -> config.getValue().contentEquals("Mobs")));
        /* Animals */
        addOption(animals_enabled.setVisibility(v -> config.getValue().contentEquals("Animals")));
        addOption(animals_red.setVisibility(v -> config.getValue().contentEquals("Animals")));
        addOption(animals_green.setVisibility(v -> config.getValue().contentEquals("Animals")));
        addOption(animals_blue.setVisibility(v -> config.getValue().contentEquals("Animals")));
        /* Vehicles */
        addOption(vehicles_enabled.setVisibility(v -> config.getValue().contentEquals("Vehicles")));
        addOption(vehicles_red.setVisibility(v -> config.getValue().contentEquals("Vehicles")));
        addOption(vehicles_green.setVisibility(v -> config.getValue().contentEquals("Vehicles")));
        addOption(vehicles_blue.setVisibility(v -> config.getValue().contentEquals("Vehicles")));
        /* Items */
        addOption(items_enabled.setVisibility(v -> config.getValue().contentEquals("Items")));
        addOption(items_red.setVisibility(v -> config.getValue().contentEquals("Items")));
        addOption(items_green.setVisibility(v -> config.getValue().contentEquals("Items")));
        addOption(items_blue.setVisibility(v -> config.getValue().contentEquals("Items")));
        /* Crystals */
        addOption(crystals_enabled.setVisibility(v -> config.getValue().contentEquals("Crystals")));
        addOption(crystals_red.setVisibility(v -> config.getValue().contentEquals("Crystals")));
        addOption(crystals_green.setVisibility(v -> config.getValue().contentEquals("Crystals")));
        addOption(crystals_blue.setVisibility(v -> config.getValue().contentEquals("Crystals")));
        /* Friends */
        addOption(friends_red.setVisibility(v -> config.getValue().contentEquals("Friends")));
        addOption(friends_green.setVisibility(v -> config.getValue().contentEquals("Friends")));
        addOption(friends_blue.setVisibility(v -> config.getValue().contentEquals("Friends")));
        /* Sneaking */
        addOption(sneaking_red.setVisibility(v -> config.getValue().contentEquals("Sneaking")));
        addOption(sneaking_green.setVisibility(v -> config.getValue().contentEquals("Sneaking")));
        addOption(sneaking_blue.setVisibility(v -> config.getValue().contentEquals("Sneaking")));
        endOption();
    }

    private boolean validEntity(Entity entity) {
        if(entity == null)
            return false;

        boolean ret = false;
        if(players_enabled.isEnabled() && entity instanceof EntityPlayer && entity != mc.player)
            ret = true;
        else if(animals_enabled.isEnabled() && entity instanceof IAnimals && !(entity instanceof IMob))
            ret = true;
        else if(mobs_enabled.isEnabled() && entity instanceof IMob)
            ret = true;
        else if(items_enabled.isEnabled() && entity instanceof EntityItem)
            ret = true;
        else if(crystals_enabled.isEnabled() && entity instanceof EntityEnderCrystal)
            ret = true;
        else if(vehicles_enabled.isEnabled() && (entity instanceof EntityBoat || entity instanceof EntityMinecart))
            ret = true;
        if(mc.player.getRidingEntity() != null && entity == mc.player.getRidingEntity())
            ret = false;
        if(entity instanceof EntityLivingBase && entity.ticksExisted <= 0)
            ret = false;
        return ret;
    }
    private int getColor(Entity entity) {
        int ret = 0xFFFFFFFF;
        if(entity instanceof IAnimals && !(entity instanceof IMob))
            ret = new Color(animals_red.getValue().intValue(), animals_green.getValue().intValue(), animals_blue.getValue().intValue()).getRGB();
        else if(entity instanceof IMob)
            ret = new Color(mobs_red.getValue().intValue(), mobs_green.getValue().intValue(), mobs_blue.getValue().intValue()).getRGB();
        else if(entity instanceof EntityBoat || entity instanceof EntityMinecart)
            ret = new Color(vehicles_red.getValue().intValue(), vehicles_green.getValue().intValue(), vehicles_blue.getValue().intValue()).getRGB();
        else if(entity instanceof EntityItem)
            ret = new Color(items_red.getValue().intValue(), items_green.getValue().intValue(), items_blue.getValue().intValue()).getRGB();
        else if(entity instanceof EntityEnderCrystal)
            ret = new Color(crystals_red.getValue().intValue(), crystals_green.getValue().intValue(), crystals_blue.getValue().intValue()).getRGB();
        else if(entity instanceof EntityPlayer) {
            ret = new Color(players_red.getValue().intValue(), players_green.getValue().intValue(), players_blue.getValue().intValue()).getRGB();
            if(entity == mc.player)
                ret = -1;
            if(entity.isSneaking())
                ret = new Color(sneaking_red.getValue().intValue(), sneaking_green.getValue().intValue(), sneaking_blue.getValue().intValue()).getRGB();
            if(HTRewrite.INSTANCE.getFriendManager().isFriend(entity.getName()))
                ret = new Color(friends_red.getValue().intValue(), friends_green.getValue().intValue(), friends_blue.getValue().intValue()).getRGB();
        } return ret;
    }

    private void glColor(int hex) {
        float alpha = (hex >> 24 & 0xFF) / 255.0F;
        float red = (hex >> 16 & 0xFF) / 255.0F;
        float green = (hex >> 8 & 0xFF) / 255.0F;
        float blue = (hex & 0xFF) / 255.0F;
        glColor4f(red, green, blue, alpha);
    }

    @EventHandler
    private Listener<RenderEntityEvent> entityEventListener = new Listener<>(event -> {
        if(!validEntity(event.entity))
            return;

        boolean shadow = mc.getRenderManager().isRenderShadow();
        if(event.getEra() == CustomEvent.Era.PRE) {
            mc.getRenderManager().setRenderShadow(false);
            mc.getRenderManager().setRenderOutlines(false);
            GlStateManager.pushMatrix();

            switch(mode.getValue()) {
                case "NORMAL": {
                    OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240f, 240f);
                    glEnable(GL_POLYGON_OFFSET_FILL);
                    glPolygonOffset(1f, -100000f);
                } break;

                case "TEXTURE": {
                    glEnable(GL_POLYGON_OFFSET_FILL);
                    glPolygonOffset(1f, -1100000f);
                    glDisable(GL_TEXTURE_2D);
                    glColor(getColor(event.entity));
                } break;

                case "FLAT": {
                    glEnable(GL_POLYGON_OFFSET_FILL);
                    glPolygonOffset(1f, -1100000f);
                    glDisable(GL_TEXTURE_2D);
                    glDisable(GL_LIGHTING);
                    glColor(getColor(event.entity));
                } break;

                case "WIREFRAME": {
                    glPolygonMode(GL_FRONT_AND_BACK, GL_LINE);
                    glEnable(GL_POLYGON_OFFSET_LINE);
                    glPolygonOffset(1f, -1100000f);
                    glDisable(GL_TEXTURE_2D);
                    glDisable(GL_LIGHTING);
                    glEnable(GL_LINE_SMOOTH);
                    glHint(GL_LINE_SMOOTH_HINT, GL_NICEST);
                    glLineWidth(1f);
                    glColor(getColor(event.entity));
                } break;
            } GlStateManager.popMatrix();
        } else if(event.getEra() == CustomEvent.Era.POST) {
            mc.getRenderManager().setRenderShadow(shadow);
            GlStateManager.pushMatrix();

            switch (mode.getValue()) {
                case "NORMAL": {
                    glDisable(GL_POLYGON_OFFSET_FILL);
                    glPolygonOffset(1f, 1100000f);
                } break;

                case "TEXTURE": {
                    glDisable(GL_POLYGON_OFFSET_FILL);
                    glPolygonOffset(1f, 1100000f);
                    glEnable(GL_TEXTURE_2D);
                } break;

                case "FLAT": {
                    glDisable(GL_POLYGON_OFFSET_FILL);
                    glPolygonOffset(1f, 1100000f);
                    glEnable(GL_TEXTURE_2D);
                    glEnable(GL_LIGHTING);
                } break;

                case "WIREFRAME": {
                    glPolygonMode(GL_FRONT_AND_BACK, GL_FILL);
                    glDisable(GL_POLYGON_OFFSET_LINE);
                    glPolygonOffset(1f, 1100000f);
                    glEnable(GL_TEXTURE_2D);
                    glEnable(GL_LIGHTING);
                    glDisable(GL_LINE_SMOOTH);
                } break;
            } GlStateManager.popMatrix();
        }
    });
}