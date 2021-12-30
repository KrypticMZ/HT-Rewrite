package me.htrewrite.client.module.modules.render;

import me.htrewrite.client.event.custom.player.PlayerUpdateEvent;
import me.htrewrite.client.event.custom.render.RenderEvent;
import me.htrewrite.client.module.Module;
import me.htrewrite.client.module.ModuleType;
import me.htrewrite.client.util.*;
import me.htrewrite.exeterimports.mcapi.settings.ValueSetting;
import me.zero.alpine.fork.listener.EventHandler;
import me.zero.alpine.fork.listener.Listener;
import net.minecraft.block.BlockShulkerBox;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItemFrame;
import net.minecraft.entity.item.EntityMinecartChest;
import net.minecraft.entity.item.EntityMinecartFurnace;
import net.minecraft.entity.item.EntityMinecartHopper;
import net.minecraft.item.ItemBlock;
import net.minecraft.tileentity.*;
import net.minecraft.util.math.BlockPos;

import java.awt.*;
import java.util.ArrayList;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL11.glPopMatrix;

public class ChestESPModule extends Module {
    public static final ValueSetting<Double> delay = new ValueSetting<>("TickDelay", null, 1d, 0d, 20d);

    private ArrayList<Entry<BlockPos, Color>> storages;
    private TickedTimer tickedTimer;

    public ChestESPModule() {
        super("ChestESP", "Highlight storages.", ModuleType.Render, 0);
        addOption(delay);
        endOption();

        this.storages = new ArrayList<>();

        this.tickedTimer = new TickedTimer();
        this.tickedTimer.stop();
    }

    @Override
    public void onEnable() {
        super.onEnable();

        tickedTimer.start();
        tickedTimer.reset();
    }

    @Override
    public void onDisable() {
        super.onDisable();

        tickedTimer.stop();
    }

    @EventHandler
    private Listener<PlayerUpdateEvent> updateEventListener = new Listener<>(event -> {
        if(!tickedTimer.passed(delay.getValue().intValue()))
            return;

        storages.clear();
        for(TileEntity entity : mc.world.loadedTileEntityList)
            if(entity instanceof TileEntityChest)
                storages.add(new Entry<>(entity.getPos(), new Color(255, 165, 0, 200)));
            else if(entity instanceof TileEntityDispenser)
                storages.add(new Entry<>(entity.getPos(), new Color(255, 165, 0, 200)));
            else if(entity instanceof TileEntityShulkerBox)
                storages.add(new Entry<>(entity.getPos(), new Color(255, 255, 0, 200)));
            else if(entity instanceof TileEntityEnderChest)
                storages.add(new Entry<>(entity.getPos(), new Color(128, 0, 128, 200)));
            else if(entity instanceof TileEntityFurnace)
                storages.add(new Entry<>(entity.getPos(), new Color(128, 128, 128, 200)));
            else if(entity instanceof TileEntityHopper)
                storages.add(new Entry<>(entity.getPos(), new Color(128, 128, 128, 200)));
        for(Entity entity : mc.world.loadedEntityList)
            if(entity instanceof EntityMinecartChest)
                storages.add(new Entry<>(entity.getPosition(), new Color(255, 165, 0, 200)));
            else if(entity instanceof EntityMinecartFurnace)
                storages.add(new Entry<>(entity.getPosition(), new Color(128, 128, 128, 200)));
            else if(entity instanceof EntityMinecartHopper)
                storages.add(new Entry<>(entity.getPosition(), new Color(128, 128, 128, 200)));
            else if(entity instanceof EntityItemFrame && ((EntityItemFrame)entity).getDisplayedItem().getItem() instanceof ItemBlock && ((ItemBlock)((EntityItemFrame)entity).getDisplayedItem().getItem()).getBlock() instanceof BlockShulkerBox)
                storages.add(new Entry<>(entity.getPosition().add(0, -1, 0), new Color(255, 255, 0, 200)));

        tickedTimer.reset();
    });

    @EventHandler
    private Listener<RenderEvent> renderEventListener = new Listener<>(event -> {
        glPushMatrix();
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        glEnable(GL_LINE_SMOOTH);
        glLineWidth(1);
        glDisable(GL_TEXTURE_2D);
        glEnable(GL_CULL_FACE);
        glDisable(GL_DEPTH_TEST);
        glDisable(GL_LIGHTING);

        for (Entry<BlockPos, Color> entry : storages) {
            double x = mc.player.lastTickPosX + (mc.player.posX - mc.player.lastTickPosX) * (double) event.getPartialTicks();
            double y = mc.player.lastTickPosY + (mc.player.posY - mc.player.lastTickPosY) * (double) event.getPartialTicks();
            double z = mc.player.lastTickPosZ + (mc.player.posZ - mc.player.lastTickPosZ) * (double) event.getPartialTicks();
            GLUtils.glColor(entry.getValue());
            RenderUtils.drawSelectionBoundingBox(mc.world.getBlockState(entry.getKey()).getSelectedBoundingBox(mc.world, entry.getKey()).grow(0.0020000000949949026D).offset(-x, -y, -z));
        }

        glEnable(GL_LIGHTING);
        glEnable(GL_DEPTH_TEST);
        glEnable(GL_TEXTURE_2D);
        glEnable(GL_BLEND);
        glEnable(GL_LINE_SMOOTH);
        glPopMatrix();
    });

    public class EntryBlockSColor extends Entry<BlockPos, Color> {
        public EntryBlockSColor(BlockPos key, Color value) {
            super(key, value);
        }
    }
}