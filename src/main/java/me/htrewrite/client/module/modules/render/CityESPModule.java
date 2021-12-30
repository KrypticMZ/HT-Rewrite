package me.htrewrite.client.module.modules.render;

import me.htrewrite.client.HTRewrite;
import me.htrewrite.client.Wrapper;
import me.htrewrite.client.clickgui.components.buttons.settings.bettermode.BetterMode;
import me.htrewrite.client.event.custom.render.RenderEvent;
import me.htrewrite.client.manager.FriendManager;
import me.htrewrite.client.module.Module;
import me.htrewrite.client.module.ModuleType;
import me.htrewrite.exeterimports.mcapi.settings.ModeSetting;
import me.htrewrite.exeterimports.mcapi.settings.ValueSetting;
import me.htrewrite.salimports.util.CrystalUtils;
import me.htrewrite.salimports.util.ESPUtil;
import me.htrewrite.salimports.util.EntityUtil;
import me.htrewrite.salimports.util.Pair;
import me.zero.alpine.fork.listener.EventHandler;
import me.zero.alpine.fork.listener.Listener;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.culling.ICamera;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.stream.Collectors;

public class CityESPModule extends Module {
    public static final ModeSetting holeMode = new ModeSetting("Mode", null, 0, BetterMode.construct("None", "FlatOutline", "Flat", "Outline", "Full"));
    public static final ValueSetting<Double> obsidianRed = new ValueSetting<>("ORed", null, 1D, 0D, 1D);
    public static final ValueSetting<Double> obsidianGreen = new ValueSetting<>("OGreen", null, 0D, 0D, 1D);
    public static final ValueSetting<Double> obsidianBlue = new ValueSetting<>("OBlue", null, 0D, 0D, 1D);
    public static final ValueSetting<Double> obsidianAlpha = new ValueSetting<>("OAlpha", null, .5D, 0D, 1D);

    private static FriendManager friendManager;

    public CityESPModule() {
        super("CityESP", "Renders blocks.", ModuleType.Render, 0);
        addOption(holeMode);
        addOption(obsidianRed);
        addOption(obsidianGreen);
        addOption(obsidianBlue);
        addOption(obsidianAlpha);
        endOption();

        friendManager = HTRewrite.INSTANCE.getFriendManager();
    }

    private ICamera camera = new Frustum();
    private static final BlockPos[] surroundOffset = {
            new BlockPos(0, 0, -1),
            new BlockPos(1, 0, 0),
            new BlockPos(0, 0, 1),
            new BlockPos(-1, 0, 0)
    };

    public static ArrayList<Pair<EntityPlayer, ArrayList<BlockPos>>> GetPlayersReadyToBeCitied() {
        ArrayList<Pair<EntityPlayer, ArrayList<BlockPos>>> players = new ArrayList<Pair<EntityPlayer, ArrayList<BlockPos>>>();

        for (Entity entity : Wrapper.getMC().world.playerEntities.stream().filter(entityPlayer -> !friendManager.isFriend(entityPlayer.getName())).collect(Collectors.toList()))
        {
            ArrayList<BlockPos> positions = new ArrayList<BlockPos>();

            for (int i = 0; i < 4; ++i)
            {
                BlockPos o = EntityUtil.GetPositionVectorBlockPos(entity, surroundOffset[i]);

                if (Wrapper.getMC().world.getBlockState(o).getBlock() != Blocks.OBSIDIAN)
                    continue;

                boolean passCheck = false;

                switch (i)
                {
                    case 0:
                        passCheck = CrystalUtils.canPlaceCrystal(o.north(1).down());
                        break;
                    case 1:
                        passCheck = CrystalUtils.canPlaceCrystal(o.east(1).down());
                        break;
                    case 2:
                        passCheck = CrystalUtils.canPlaceCrystal(o.south(1).down());
                        break;
                    case 3:
                        passCheck = CrystalUtils.canPlaceCrystal(o.west(1).down());
                        break;
                }

                if (passCheck)
                    positions.add(o);
            }

            if (!positions.isEmpty())
                players.add(new Pair<EntityPlayer, ArrayList<BlockPos>>((EntityPlayer)entity, positions));
        }

        return players;
    }

    @EventHandler
    private Listener<RenderEvent> renderEventListener = new Listener<>(event -> {
        if (mc.getRenderManager() == null || mc.getRenderManager().options == null)
            return;

        GetPlayersReadyToBeCitied().forEach(pair -> {
            pair.getSecond().forEach(o ->
            {
                final AxisAlignedBB bb = new AxisAlignedBB(o.getX() - mc.getRenderManager().viewerPosX, o.getY() - mc.getRenderManager().viewerPosY,
                        o.getZ() - mc.getRenderManager().viewerPosZ, o.getX() + 1 - mc.getRenderManager().viewerPosX, o.getY() + 1 - mc.getRenderManager().viewerPosY,
                        o.getZ() + 1 - mc.getRenderManager().viewerPosZ);

                camera.setPosition(mc.getRenderViewEntity().posX, mc.getRenderViewEntity().posY, mc.getRenderViewEntity().posZ);

                if (camera.isBoundingBoxInFrustum(new AxisAlignedBB(bb.minX + mc.getRenderManager().viewerPosX, bb.minY + mc.getRenderManager().viewerPosY, bb.minZ + mc.getRenderManager().viewerPosZ,
                        bb.maxX + mc.getRenderManager().viewerPosX, bb.maxY + mc.getRenderManager().viewerPosY, bb.maxZ + mc.getRenderManager().viewerPosZ)))
                {
                    GlStateManager.pushMatrix();
                    GlStateManager.enableBlend();
                    GlStateManager.disableDepth();
                    GlStateManager.tryBlendFuncSeparate(770, 771, 0, 1);
                    GlStateManager.disableTexture2D();
                    GlStateManager.depthMask(false);
                    GL11.glEnable(GL11.GL_LINE_SMOOTH);
                    GL11.glHint(GL11.GL_LINE_SMOOTH_HINT, GL11.GL_NICEST);
                    GL11.glLineWidth(1.5f);

                    ESPUtil.Render(ESPUtil.HoleModes.valueOf(holeMode.getValue()), bb, obsidianRed.getValue().floatValue(), obsidianGreen.getValue().floatValue(), obsidianBlue.getValue().floatValue(), obsidianAlpha.getValue().floatValue());

                    GL11.glDisable(GL11.GL_LINE_SMOOTH);
                    GlStateManager.depthMask(true);
                    GlStateManager.enableDepth();
                    GlStateManager.enableTexture2D();
                    GlStateManager.disableBlend();
                    GlStateManager.popMatrix();
                }
            });
        });
    });
}