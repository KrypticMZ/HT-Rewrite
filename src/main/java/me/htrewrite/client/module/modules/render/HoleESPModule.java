package me.htrewrite.client.module.modules.render;

import me.htrewrite.client.clickgui.components.buttons.settings.bettermode.BetterMode;
import me.htrewrite.client.event.custom.player.PlayerUpdateEvent;
import me.htrewrite.client.event.custom.render.RenderEvent;
import me.htrewrite.client.module.Module;
import me.htrewrite.client.module.ModuleType;
import me.htrewrite.client.util.ChatColor;
import me.htrewrite.client.util.MathUtil;
import me.htrewrite.client.util.RainbowUtil;
import me.htrewrite.exeterimports.mcapi.settings.ModeSetting;
import me.htrewrite.exeterimports.mcapi.settings.ToggleableSetting;
import me.htrewrite.exeterimports.mcapi.settings.ValueSetting;
import me.htrewrite.salimports.util.ESPUtil;
import me.htrewrite.salimports.util.Hole;
import me.zero.alpine.fork.listener.EventHandler;
import me.zero.alpine.fork.listener.Listener;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.culling.ICamera;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.List;

public class HoleESPModule extends Module {
    public static final ModeSetting mode = new ModeSetting("Mode", 0, BetterMode.construct(ChatColor.enumToStringArray(ESPUtil.HoleModes.values())));
    public static final ValueSetting<Double> radius = new ValueSetting<>("Radius", 8d, 0d, 32d);
    public static final ToggleableSetting ignoreOwnHole = new ToggleableSetting("IgnoreOwnHole", false);

    public static final ModeSetting color_setting = new ModeSetting("ColorSetting", 0, BetterMode.construct("OBSIDIAN", "BEDROCK"));
    /* OBSIDIAN */
    public static final ToggleableSetting O_Rainbow = new ToggleableSetting("O_Rainbow", false);
    public static final ValueSetting<Double> O_Red = new ValueSetting<>("O_Red", 1d, 0d, 1d);
    public static final ValueSetting<Double> O_Green = new ValueSetting<>("O_Green", 0d, 0d , 1d);
    public static final ValueSetting<Double> O_Blue = new ValueSetting<>("O_Blue", 0d, 0d, 1d);
    public static final ValueSetting<Double> O_Alpha = new ValueSetting<>("O_Alpha", .5d, 0d, 1d);
    /* BEDROCK */
    public static final ToggleableSetting B_Rainbow = new ToggleableSetting("B_Rainbow", false);
    public static final ValueSetting<Double> B_Red = new ValueSetting<>("B_Red", 0d, 0d, 1d);
    public static final ValueSetting<Double> B_Green = new ValueSetting<>("B_Green", 1d, 0d, 1d);
    public static final ValueSetting<Double> B_Blue = new ValueSetting<>("B_Blue", 0d, 0d, 1d);
    public static final ValueSetting<Double> B_Alpha = new ValueSetting<>("B_Alpha", .5d, 0d, 1d);

    public final List<Hole> holes = new ArrayList<>();
    private ICamera camera;
    private RainbowUtil rainbowUtil;

    public HoleESPModule() {
        super("HoleESP", "Highlights holes.", ModuleType.Render, 0);
        addOption(mode);
        addOption(radius);
        addOption(ignoreOwnHole);
        addOption(color_setting);
        addOption(O_Rainbow.setVisibility(v -> color_setting.getValue().contentEquals("OBSIDIAN")));
        addOption(O_Red.setVisibility(v -> color_setting.getValue().contentEquals("OBSIDIAN") && !O_Rainbow.isEnabled()));
        addOption(O_Green.setVisibility(v -> color_setting.getValue().contentEquals("OBSIDIAN") && !O_Rainbow.isEnabled()));
        addOption(O_Blue.setVisibility(v -> color_setting.getValue().contentEquals("OBSIDIAN") && !O_Rainbow.isEnabled()));
        addOption(O_Alpha.setVisibility(v -> color_setting.getValue().contentEquals("OBSIDIAN") && !O_Rainbow.isEnabled()));
        addOption(B_Rainbow.setVisibility(v -> color_setting.getValue().contentEquals("BEDROCK")));
        addOption(B_Red.setVisibility(v -> color_setting.getValue().contentEquals("BEDROCK") && !B_Rainbow.isEnabled()));
        addOption(B_Green.setVisibility(v -> color_setting.getValue().contentEquals("BEDROCK") && !B_Rainbow.isEnabled()));
        addOption(B_Blue.setVisibility(v -> color_setting.getValue().contentEquals("BEDROCK") && !B_Rainbow.isEnabled()));
        addOption(B_Alpha.setVisibility(v -> color_setting.getValue().contentEquals("BEDROCK") && !B_Rainbow.isEnabled()));
        endOption();

        this.camera = new Frustum();
        this.rainbowUtil = new RainbowUtil();
    }

    @EventHandler
    private Listener<PlayerUpdateEvent> updateEventListener = new Listener<>(event -> {
        this.holes.clear();
        final Vec3i playerPos = new Vec3i(mc.player.posX, mc.player.posY, mc.player.posZ);
        for(int x = playerPos.getX() - radius.getValue().intValue(); x < playerPos.getX() + radius.getValue().intValue(); x++)
            for(int z = playerPos.getZ() - radius.getValue().intValue(); z < playerPos.getZ() + radius.getValue().intValue(); z++)
                for(int y = playerPos.getY() + radius.getValue().intValue(); y < playerPos.getY() + radius.getValue().intValue(); y++)
                    if(!mode.getValue().contentEquals(ESPUtil.HoleModes.None.toString())) {
                        final BlockPos blockPos = new BlockPos(x, y, z);
                        if(ignoreOwnHole.isEnabled() && mc.player.getDistanceSq(blockPos) <= 1)
                            continue;

                        final IBlockState blockState = mc.world.getBlockState(blockPos);
                        Hole.HoleTypes type = ESPUtil.isBlockValid(blockState, blockPos);
                        if(type != Hole.HoleTypes.None) {
                            final IBlockState downBlockState = mc.world.getBlockState(blockPos.down());
                            if(downBlockState.getBlock() == Blocks.AIR) {
                                final BlockPos downPos = blockPos.down();
                                type = ESPUtil.isBlockValid(blockState, downPos);
                                if(type != Hole.HoleTypes.None)
                                    this.holes.add(new Hole(downPos.getX(), downPos.getY(), downPos.getZ(), downPos, type, true));
                            } else this.holes.add(new Hole(blockPos.getX(), blockPos.getY(), blockPos.getZ(), blockPos, type));
                        }
                    }
    });

    @EventHandler
    private Listener<RenderEvent> renderEventListener = new Listener<>(event -> {
        if(mc.getRenderManager().options == null || mc.getRenderViewEntity() == null)
            return;

        float o_r = O_Rainbow.isEnabled() ? (float)MathUtil.transform(255, 1, rainbowUtil.getR()) : O_Red.getValue().floatValue();
        float o_g = O_Rainbow.isEnabled() ? (float)MathUtil.transform(255, 1, rainbowUtil.getG()) : O_Green.getValue().floatValue();
        float o_b = O_Rainbow.isEnabled() ? (float)MathUtil.transform(255, 1, rainbowUtil.getB()) : O_Blue.getValue().floatValue();
        float o_a = O_Alpha.getValue().floatValue();

        float b_r = B_Rainbow.isEnabled() ? (float)MathUtil.transform(255, 1, rainbowUtil.getR()) : B_Red.getValue().floatValue();
        float b_g = B_Rainbow.isEnabled() ? (float)MathUtil.transform(255, 1, rainbowUtil.getG()) : B_Green.getValue().floatValue();
        float b_b = B_Rainbow.isEnabled() ? (float)MathUtil.transform(255, 1, rainbowUtil.getB()) : B_Blue.getValue().floatValue();
        float b_a = B_Alpha.getValue().floatValue();

        if(!mode.getValue().contentEquals(ESPUtil.HoleModes.None.toString())) {
            new ArrayList<>(holes).forEach(hole -> {
                final AxisAlignedBB bb = new AxisAlignedBB(
                        hole.getX() - mc.getRenderManager().viewerPosX,
                        hole.getY() - mc.getRenderManager().viewerPosY,
                        hole.getZ() - mc.getRenderManager().viewerPosZ,

                        hole.getX() + 1 - mc.getRenderManager().viewerPosX,
                        hole.getY() + (hole.isTall() ? 2 : 1) - mc.getRenderManager().viewerPosY,
                        hole.getZ() + 1 - mc.getRenderManager().viewerPosZ);
                camera.setPosition(mc.getRenderViewEntity().posX, mc.getRenderViewEntity().posY, mc.getRenderViewEntity().posZ);
                if(camera.isBoundingBoxInFrustum(new AxisAlignedBB(
                        bb.minX + mc.getRenderManager().viewerPosX,
                        bb.minY + mc.getRenderManager().viewerPosY,
                        bb.minZ + mc.getRenderManager().viewerPosZ,

                        bb.maxX + mc.getRenderManager().viewerPosX,
                        bb.maxY + mc.getRenderManager().viewerPosY,
                        bb.maxZ + mc.getRenderManager().viewerPosZ))) {
                    GlStateManager.pushMatrix();
                    GlStateManager.enableBlend();
                    GlStateManager.disableDepth();
                    GlStateManager.tryBlendFuncSeparate(770, 771, 0, 1);
                    GlStateManager.disableTexture2D();
                    GlStateManager.depthMask(false);
                    GL11.glEnable(GL11.GL_LINE_SMOOTH);
                    GL11.glHint(GL11.GL_LINE_SMOOTH_HINT, GL11.GL_NICEST);
                    GL11.glLineWidth(1.5f);

                    switch(hole.GetHoleType()) {
                        case Obsidian: {
                            ESPUtil.Render(ESPUtil.HoleModes.valueOf(mode.getValue()), bb, o_r, o_g, o_b, o_a);
                        } break;

                        case Bedrock: {
                            ESPUtil.Render(ESPUtil.HoleModes.valueOf(mode.getValue()), bb, b_r, b_g, b_b, b_a);
                        } break;
                        default:
                            break;
                    }

                    GL11.glDisable(GL11.GL_LINE_SMOOTH);
                    GlStateManager.depthMask(true);
                    GlStateManager.enableDepth();
                    GlStateManager.enableTexture2D();
                    GlStateManager.disableBlend();
                    GlStateManager.popMatrix();
                }
            });
        }
    });
}