package me.htrewrite.client.module.modules.world;

import me.htrewrite.client.clickgui.components.buttons.settings.bettermode.BetterMode;
import me.htrewrite.client.module.Module;
import me.htrewrite.client.module.ModuleType;
import me.htrewrite.client.util.BlockData;
import me.htrewrite.client.util.Timer;
import me.htrewrite.exeterimports.mcapi.settings.ModeSetting;
import me.zero.alpine.fork.listener.EventHandler;
import me.zero.alpine.fork.listener.Listener;
import net.minecraft.block.Block;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.client.event.EntityViewRenderEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.awt.*;
import java.awt.event.InputEvent;
import java.util.Random;

public class ScaffoldModule extends Module {
    public static final ModeSetting mode = new ModeSetting("Mode", 0, BetterMode.construct("AAC", "VANILLA"));

    public Timer timer;
    public BlockData blockData;
    boolean isBridging = false;
    BlockPos blockDown = null;
    public static float[] facingCam = null;
    float startYaw, startPitch = startYaw = 0;
    private Random random = new Random();

    public ScaffoldModule() {
        super("Scaffold", "Places blocks.", ModuleType.World, 0);
        addOption(mode);
        endOption();
    }

    private float[] rotationsToBlock = null;

    private boolean check() {
        RayTraceResult traceResult = mc.objectMouseOver;
        ItemStack itemStack = mc.player.inventory.getCurrentItem();
        return traceResult != null && traceResult.typeOfHit == RayTraceResult.Type.BLOCK && !(mc.player.rotationYawHead <= 70) && mc.player.onGround && !mc.player.isOnLadder() && !mc.player.isInLava() && !mc.player.isInWater() && mc.gameSettings.keyBindBack.isKeyDown();
    }
    private int findSlotWithBlock() {
        for(int i = 0; i < 9; ++i) {
            ItemStack itemStack = mc.player.inventory.getStackInSlot(i);
            if(itemStack.getItem() instanceof ItemBlock) {
                Block block = Block.getBlockFromItem(itemStack.getItem()).getDefaultState().getBlock();
                if(block.isFullBlock(mc.world.getBlockState(blockDown).getBlock().getDefaultState()) && block != Blocks.SAND && block != Blocks.GRAVEL)
                    return i;
            }
        }

        return -1;
    }
    private float updateRotation(float p_70663_1_, float p_70663_2_, float p_70663_3_) {
        float var4 = MathHelper.wrapDegrees(p_70663_2_ - p_70663_1_);
        if(var4 > p_70663_3_)
            var4 = p_70663_3_;
        if(var4 < -p_70663_3_)
            var4 = -p_70663_3_;

        return p_70663_1_ + var4;
    }
    private int random(int min, int max) { return random.nextInt(max - min) + min; }
    private void clickMouse(int button) {
        try {
            Robot robot = new Robot();
            switch (button) {
                case 0: {
                    robot.mousePress(InputEvent.BUTTON1_MASK);
                    robot.mouseRelease(InputEvent.BUTTON1_MASK);
                } break;

                case 1: {
                    robot.mousePress(InputEvent.BUTTON3_DOWN_MASK);
                    robot.mouseRelease(InputEvent.BUTTON3_DOWN_MASK);
                } break;

                default:
                    break;
            }
        } catch (AWTException exception) { exception.printStackTrace(); }
    }
    private float[] getNeededRotations(Vec3d vec3d) {
        final Vec3d eyesPos = new Vec3d(mc.player.posX, mc.player.posY + mc.player.eyeHeight, mc.player.posZ);
        final double diffX = vec3d.x - eyesPos.x;
        final double diffY = vec3d.y - eyesPos.y;
        final double diffZ = vec3d.z - eyesPos.z;
        final double diffXZ = Math.sqrt(diffX * diffX + diffZ * diffZ);
        final float yaw = (float) Math.toDegrees(Math.atan2(diffZ, diffX)) - 90.0f;
        final float pitch = (float) (-Math.toDegrees(Math.atan2(diffY, diffXZ)));

        return new float[] {mc.player.rotationYaw + MathHelper.wrapDegrees(yaw - mc.player.rotationYaw), mc.player.rotationPitch + MathHelper.wrapDegrees(pitch - mc.player.rotationPitch) };
    }
    private boolean placeBlock(final BlockPos blockPos) {
        final Vec3d eyeVec = new Vec3d(mc.player.posX, mc.player.posY + mc.player.getEyeHeight(), mc.player.posZ);
        EnumFacing[] values;
        for (int length = (values = EnumFacing.values()).length, i = 0; i < length; ++i) {
            final EnumFacing side = values[i];
            final BlockPos neighbor = blockPos.offset(side);
            final EnumFacing side2 = side.getOpposite();
            if (eyeVec.squareDistanceTo(new Vec3d(blockPos).add(0.5, 0.5, 0.5)) < eyeVec
                    .squareDistanceTo(new Vec3d(neighbor).add(0.5, 0.5, 0.5)) && mc.world.getBlockState(blockPos).getBlock().canCollideCheck(mc.world.getBlockState(neighbor), false)) {
                final Vec3d hitVec = new Vec3d(neighbor).add(0.5, 0.5, 0.5)
                        .add(new Vec3d(side2.getDirectionVec()).scale(0.5));
                if (eyeVec.squareDistanceTo(hitVec) <= 18.0625) {
                    rotationsToBlock = getNeededRotations(hitVec);
                    mc.player.swingArm(EnumHand.MAIN_HAND);
                    mc.playerController.processRightClickBlock(mc.player,
                            mc.world, neighbor, side2, hitVec, EnumHand.MAIN_HAND);
                    mc.rightClickDelayTimer = 4;
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public void onEnable() {
        super.onEnable();

        blockDown = null;
        facingCam = null;
        isBridging = false;
        startPitch = startYaw = 0;
        if(mode.getValue().contentEquals("AAC") && mc.gameSettings.keyBindBack.isKeyDown())
            KeyBinding.setKeyBindState(mc.gameSettings.keyBindBack.getKeyCode(), false);
    }

    @Override
    public void onDisable() {
        super.onDisable();

        facingCam = null;
    }

    @EventHandler
    private Listener<EntityViewRenderEvent.CameraSetup> cameraSetupListener = new Listener<>(event -> {
        if(mode.getValue().contentEquals("AAC") && event.getEntity() == mc.player) {
            if(startYaw == 0 || startPitch == 0)
                return;
            event.setYaw(startYaw);
            event.setPitch(startPitch - 70);
            facingCam = new float[]{startYaw - 180, startPitch - 70};
        }
    });

    @EventHandler
    private Listener<TickEvent.ClientTickEvent> tickEventListener = new Listener<>(event -> {
        if(nullCheck())
            return;

        if(mode.getValue().contentEquals("AAC")) {
            int oldSlot = -1;
            if(!check()) {
                if(isBridging) {
                    KeyBinding.setKeyBindState(mc.gameSettings.keyBindSneak.getKeyCode(), mc.world.getBlockState(new BlockPos(mc.player).down()).getBlock() == Blocks.AIR);
                    isBridging = false;
                    if(oldSlot != -1)
                        mc.player.inventory.currentItem = oldSlot;
                }

                startYaw = startPitch = 0;
                facingCam = null;
                blockDown = null;
                return;
            }

            startYaw = mc.player.rotationYaw;
            startPitch = mc.player.rotationPitch;
            KeyBinding.setKeyBindState(mc.gameSettings.keyBindRight.getKeyCode(), false);
            KeyBinding.setKeyBindState(mc.gameSettings.keyBindLeft.getKeyCode(), false);
            blockDown = new BlockPos(mc.player).down();
            float r1 = new Random().nextFloat();
            if(r1 == 1f)
                r1--;
            int newSlot = findSlotWithBlock();
            if(newSlot == -1)
                return;
            oldSlot = mc.player.inventory.currentItem;
            mc.player.inventory.currentItem = newSlot;
            mc.player.rotationPitch = updateRotation(mc.player.rotationPitch, 82f - r1, 15f);
            int currentCPS = random(3, 4);
            if(timer.passed(1000d / currentCPS)) {
                clickMouse(1);
                mc.player.swingArm(EnumHand.MAIN_HAND);
                timer.reset();
            } isBridging = true;
            KeyBinding.setKeyBindState(mc.gameSettings.keyBindSneak.getKeyCode(), mc.world.getBlockState(new BlockPos(mc.player).down()).getBlock() == Blocks.AIR);
        } else if(mode.getValue().contentEquals("VANILLA")) {
            blockDown = new BlockPos(mc.player).down();
            if(!mc.world.getBlockState(blockDown).getBlock().getMaterial(mc.world.getBlockState(blockDown).getBlock().getDefaultState()).isReplaceable())
                return;
            int newSlot = findSlotWithBlock();
            if(newSlot == -1)
                return;
            final int oldSlot = mc.player.inventory.currentItem;
            mc.player.inventory.currentItem = newSlot;
            placeBlock(blockDown);
            mc.player.inventory.currentItem = oldSlot;
        }
    });
}