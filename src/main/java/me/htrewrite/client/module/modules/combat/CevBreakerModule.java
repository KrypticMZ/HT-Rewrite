package me.htrewrite.client.module.modules.combat;

import static me.htrewrite.gsimports.util.player.SpoofRotationUtil.ROTATION_UTIL;

import me.htrewrite.client.clickgui.components.buttons.settings.bettermode.BetterMode;
import me.htrewrite.client.event.custom.CustomEvent;
import me.htrewrite.client.event.custom.networkmanager.NetworkPacketEvent;
import me.htrewrite.client.event.custom.player.UpdateWalkingPlayerEvent;
import me.htrewrite.client.event.custom.world.BlockDestroyEvent;
import me.htrewrite.client.module.Module;
import me.htrewrite.client.module.ModuleType;
import me.htrewrite.client.util.ChatColor;
import me.htrewrite.exeterimports.mcapi.settings.ModeSetting;
import me.htrewrite.exeterimports.mcapi.settings.ToggleableSetting;
import me.htrewrite.exeterimports.mcapi.settings.ValueSetting;
import me.htrewrite.gsimports.util.player.PlayerUtil;
import me.htrewrite.gsimports.util.player.RotationUtil;
import me.htrewrite.gsimports.util.world.BlockUtil;
import me.htrewrite.gsimports.util.world.CrystalUtil;
import me.htrewrite.gsimports.util.world.EntityUtil;
import me.htrewrite.gsimports.util.world.HoleUtil;
import me.zero.alpine.fork.listener.EventHandler;
import me.zero.alpine.fork.listener.Listener;
import net.minecraft.block.Block;
import net.minecraft.block.BlockAir;
import net.minecraft.block.BlockObsidian;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.*;
import net.minecraft.network.play.server.SPacketSoundEffect;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;

import java.util.ArrayList;
import java.util.Objects;

/* WARNING!! This module is unfinished please don't add it. */
public class CevBreakerModule extends Module {
    public static final ModeSetting target = new ModeSetting("Target", null, ETarget.NEAREST.ordinal(), BetterMode.construct(ChatColor.enumToStringArray(ETarget.values())));
    public static final ModeSetting breakCrystal = new ModeSetting("BreakCrystal", null, EBreakCrystal.PACKET.ordinal(), BetterMode.construct(ChatColor.enumToStringArray(EBreakCrystal.values())));
    public static final ModeSetting breakBlock = new ModeSetting("BreakBlock", null, EBreakBlock.PACKET.ordinal(), BetterMode.construct(ChatColor.enumToStringArray(EBreakBlock.values())));

    public static final ValueSetting<Double> enemyRange = new ValueSetting<>("Range", null, 4.9d, 0d, 6d);

    public static final ValueSetting<Double> preRotationDelay = new ValueSetting<>("PreRotDelay", null, 0d, 0d, 20d);
    public static final ValueSetting<Double> afterRotationDelay = new ValueSetting<>("PostRotDelay", null, 0d, 0d, 20d);
    public static final ValueSetting<Double> supDelay = new ValueSetting<>("SupportDelay", null, 1d, 0d, 4d);
    public static final ValueSetting<Double> crystalDelay = new ValueSetting<>("CrystalDelay", null, 2d, 0d, 20d);
    public static final ValueSetting<Double> blocksPerTick = new ValueSetting<>("BlocksPerTick", null, 4d, 2d, 6d);
    public static final ValueSetting<Double> hitDelay = new ValueSetting<>("HitDelay", null, 2d, 0d, 20d);
    public static final ValueSetting<Double> midHitDelay = new ValueSetting<>("MidHitDelay", null, 1d, 0d, 20d);
    public static final ValueSetting<Double> endDelay = new ValueSetting<>("EndDelay", null, 1d, 0d, 20d);
    public static final ValueSetting<Double> pickSwitchTick = new ValueSetting<>("PickSwitchTick", null, 100d, 0d, 500d);

    public static final ToggleableSetting rotate = new ToggleableSetting("Rotate", null, false);
    public static final ToggleableSetting confirmBreak = new ToggleableSetting("NoGlitchBreak", null, true);
    public static final ToggleableSetting confirmPlace = new ToggleableSetting("NoGlitchPlace", null, true);
    public static final ToggleableSetting antiWeakness = new ToggleableSetting("AntiWeakness", null, false);
    public static final ToggleableSetting switchSword = new ToggleableSetting("SwitchSword", null, false);
    public static final ToggleableSetting fastPlace = new ToggleableSetting("FastPlace", null, false);
    public static final ToggleableSetting fastBreak = new ToggleableSetting("FastBreak", null, true);
    public static final ToggleableSetting trapPlayer = new ToggleableSetting("TrapPlayer", null, false);
    public static final ToggleableSetting antiStep = new ToggleableSetting("AntiStep", null, false);
    public static final ToggleableSetting placeCrystal = new ToggleableSetting("PlaceCrystal", null, true);
    public static final ToggleableSetting forceRotation = new ToggleableSetting("ForceRotation", null, false);
    public static final ToggleableSetting forceBreaker = new ToggleableSetting("ForceBreaker", null, false);

    public static int cur_item = -1;
    public static boolean isActive = false;
    public static boolean forceBrk = false;

    private boolean noMaterials = false,
            hasMoved = false,
            isSneaking = false,
            isHole = true,
            enoughSpace = true,
            broken,
            stoppedCa,
            deadPl,
            rotationPlayerMoved,
            prevBreak,
            preRotationBol;

    private int oldSlot = -1,
            stage,
            delayTimeTicks,
            hitTryTick,
            tickPick,
            afterRotationTick,
            preRotationTick;
    private final int[][] model = new int[][]{
            {1, 1, 0},
            {-1, 1, 0},
            {0, 1, 1},
            {0, 1, -1}
    };

    public static boolean isPossible = false;

    private int[] slot_mat,
            delayTable,
            enemyCoordsInt;

    private double[] enemyCoordsDouble;

    private structureTemp toPlace;


    Double[][] sur_block = new Double[4][3];

    private EntityPlayer aimTarget;

    public CevBreakerModule() {
        super("CevBreaker", "Good stuff.", ModuleType.Combat, 0);
        addOption(target);
        addOption(breakCrystal);
        addOption(breakBlock);

        addOption(enemyRange);

        addOption(preRotationDelay);
        addOption(afterRotationDelay);
        addOption(supDelay);
        addOption(crystalDelay);
        addOption(blocksPerTick);
        addOption(hitDelay);
        addOption(midHitDelay);
        addOption(endDelay);
        addOption(pickSwitchTick);

        addOption(rotate);
        addOption(confirmBreak);
        addOption(confirmPlace);
        addOption(antiWeakness);
        addOption(switchSword);
        addOption(fastPlace);
        addOption(fastBreak);
        addOption(trapPlayer);
        addOption(antiStep);
        addOption(placeCrystal);
        addOption(forceRotation);
        addOption(forceBreaker);

        endOption();
    }

    @Override
    public void onEnable() {
        super.onEnable();

        ROTATION_UTIL.onEnable();
        initValues();
        if(getAimTarget())
            return;

    }

    @EventHandler
    private Listener<BlockDestroyEvent> blockDestroyEventListener = new Listener<>(event -> {
        if (enemyCoordsInt != null && event.getBlockPos().getX() + (event.getBlockPos().getX() < 0 ? 1 : 0) == enemyCoordsInt[0] && event.getBlockPos().getZ() + (event.getBlockPos().getZ() < 0 ? 1 : 0) == enemyCoordsInt[2])
            destroyCrystalAlgo();
    });

    @EventHandler
    private Listener<NetworkPacketEvent> packetEventListener = new Listener<>(event -> {
        if(!(!event.reading && event.getEra() == CustomEvent.Era.PRE))
            return;

        if(event.getPacket() instanceof SPacketSoundEffect) {
            SPacketSoundEffect packet = (SPacketSoundEffect)event.getPacket();
            if (packet.getCategory() == SoundCategory.BLOCKS && packet.getSound() == SoundEvents.ENTITY_GENERIC_EXPLODE)
                if ((int) packet.getX() == enemyCoordsInt[0] && (int) packet.getZ() == enemyCoordsInt[2])
                    stage = 1;
        }
    });

    Vec3d lastHitVec;
    @EventHandler
    private Listener<UpdateWalkingPlayerEvent> updateWalkingPlayerEventListener = new Listener<>(event -> {
        if(event.getEra() != CustomEvent.Era.PRE || !rotate.isEnabled() || lastHitVec == null || !forceRotation.isEnabled())
            return;

        Vec2f rotation = RotationUtil.getRotationTo(lastHitVec);
        /*
        PlayerPacket packet = new PlayerPacket(this, rotation);
        PlayerPacketManager.INSTANCE.addPacket(packet);*/
        // TODO: Continue
    });

    private void initValues() {
        preRotationBol = false;
        afterRotationTick = preRotationTick = 0;
        isPossible = false;
        aimTarget = null;
        lastHitVec = null;
        delayTable = new int[]{
                supDelay.getValue().intValue(),
                crystalDelay.getValue().intValue(),
                hitDelay.getValue().intValue(),
                endDelay.getValue().intValue()
        };

        toPlace = new structureTemp(0, 0, new ArrayList<>());
        isHole = isActive = true;
        hasMoved = rotationPlayerMoved = deadPl = broken = false;
        slot_mat = new int[]{-1, -1, -1, -1};
        stage = delayTimeTicks = 0;

        if (mc.player == null) {
            toggle();
            return;
        }

        oldSlot = mc.player.inventory.currentItem;

        stoppedCa = false;

        cur_item = -1;

        /*
        if (ModuleManager.isModuleEnabled(AutoCrystal.class)) {
            AutoCrystal.stopAC = true;
            stoppedCa = true;
        }*/

        forceBrk = forceBreaker.isEnabled();
    }

    private void playerChecks() {
        if (getMaterialsSlot()) {
            if (is_in_hole()) {
                enemyCoordsDouble = new double[]{aimTarget.posX, aimTarget.posY, aimTarget.posZ};
                enemyCoordsInt = new int[]{(int) enemyCoordsDouble[0], (int) enemyCoordsDouble[1], (int) enemyCoordsDouble[2]};
                enoughSpace = createStructure();
            } else isHole = false;
        } else noMaterials = true;
    }

    private boolean getAimTarget() {
        if(target.getValue().contentEquals("NEAREST"))
            aimTarget = PlayerUtil.findClosestTarget(enemyRange.getValue().doubleValue(), aimTarget);
        else aimTarget = PlayerUtil.findLookingPlayer(enemyRange.getValue().doubleValue());
        if (aimTarget == null || !target.getValue().contentEquals("LOOKING")) {
            if (!target.getValue().contentEquals("LOOKING") && aimTarget == null)
                toggle();
            if (aimTarget == null)
                return true;
        }
        return false;
    }

    private Entity getCrystal() {
        for(Entity t : mc.world.loadedEntityList)
            if(t instanceof EntityEnderCrystal)
                if ((int) t.posX == enemyCoordsInt[0] && (int) t.posZ == enemyCoordsInt[2] && t.posY - enemyCoordsInt[1] == 3)
                    return t;
        return null;
    }

    private void breakCrystalPiston(Entity crystal) {
        if (hitTryTick++ < midHitDelay.getValue().intValue())
            return;
        else hitTryTick = 0;
        if(antiWeakness.isEnabled())
            mc.player.inventory.currentItem = slot_mat[3];

        Vec3d vecCrystal = crystal.getPositionVector().add(0.5, 0.5, 0.5);;
        if(!breakCrystal.getValue().equalsIgnoreCase("NONE") && rotate.isEnabled()) {
            ROTATION_UTIL.lookAtPacket(vecCrystal.x, vecCrystal.y, vecCrystal.z, mc.player);
            if (forceRotation.isEnabled())
                lastHitVec = vecCrystal;
        }
        try {
            switch (breakCrystal.getValue()) {
                case "Vanilla":
                    CrystalUtil.breakCrystal(crystal);
                    // Packet
                    break;
                case "Packet":
                    CrystalUtil.breakCrystalPacket(crystal);
                    break;
                case "None":

                    break;
            }
        } catch (NullPointerException e) {}
        if (rotate.isEnabled())
            ROTATION_UTIL.resetRotation();
    }

    public void destroyCrystalAlgo() {
        isPossible = false;

        Entity crystal = getCrystal();
        if(confirmBreak.isEnabled() && broken && crystal == null) {
            stage = 1;
            broken = false;
        }
        if(crystal != null) {
            breakCrystalPiston(crystal);
            if (confirmBreak.isEnabled())
                broken = true;
            else stage = 1;
        } else stage = 1;
    }

    private boolean createStructure() {
        if ((Objects.requireNonNull(BlockUtil.getBlock(enemyCoordsDouble[0], enemyCoordsDouble[1] + 2, enemyCoordsDouble[2]).getRegistryName()).toString().toLowerCase().contains("bedrock"))
                || !(BlockUtil.getBlock(enemyCoordsDouble[0], enemyCoordsDouble[1] + 3, enemyCoordsDouble[2]) instanceof BlockAir)
                || !(BlockUtil.getBlock(enemyCoordsDouble[0], enemyCoordsDouble[1] + 4, enemyCoordsDouble[2]) instanceof BlockAir))
            return false;

        double distance_now;
        double max_found = Double.MIN_VALUE;
        int cor = 0;
        int i = 0;
        for (Double[] cord_b : sur_block) {
            if ((distance_now = mc.player.getDistanceSq(new BlockPos(cord_b[0], cord_b[1], cord_b[2]))) > max_found) {
                max_found = distance_now;
                cor = i;
            }
            i++;
        }

        toPlace.to_place.add(new Vec3d(model[cor][0], 1, model[cor][2]));
        toPlace.to_place.add(new Vec3d(model[cor][0], 2, model[cor][2]));
        toPlace.supportBlock = 2;
        if (trapPlayer.isEnabled() || antiStep.isEnabled()) {
            for (int high = 1; high < 3; high++) {
                if (high != 2 || antiStep.isEnabled())
                    for (int[] modelBas : model) {
                        Vec3d toAdd = new Vec3d(modelBas[0], high, modelBas[2]);
                        if (!toPlace.to_place.contains(toAdd)) {
                            toPlace.to_place.add(toAdd);
                            toPlace.supportBlock++;
                        }
                    }
            }
        }

        toPlace.to_place.add(new Vec3d(0, 2, 0));
        toPlace.to_place.add(new Vec3d(0, 2, 0));
        return true;
    }

    private boolean getMaterialsSlot() {
        if (mc.player.getHeldItemOffhand().getItem() instanceof ItemEndCrystal)
            slot_mat[1] = 11;
        for (int i = 0; i < 9; i++) {
            ItemStack stack = mc.player.inventory.getStackInSlot(i);
            if (stack == ItemStack.EMPTY)
                continue;
            if (slot_mat[1] == -1 && stack.getItem() instanceof ItemEndCrystal)
                slot_mat[1] = i;
            else if((antiWeakness.isEnabled() || switchSword.isEnabled()) && stack.getItem() instanceof ItemSword) slot_mat[3] = i;
            else if (stack.getItem() instanceof ItemPickaxe) slot_mat[2] = i;

            if(stack.getItem() instanceof ItemBlock) {
                Block block = ((ItemBlock) stack.getItem()).getBlock();
                if(block instanceof BlockObsidian)
                    slot_mat[0] = i;
            }
        }

        int count = 0;
        for(int val : slot_mat)
            if (val != -1)
                count++;
        return count >= 3 + ((antiWeakness.isEnabled() || switchSword.isEnabled()) ? 1 : 0);
    }

    private boolean is_in_hole() {
        sur_block = new Double[][]{
                {aimTarget.posX + 1, aimTarget.posY, aimTarget.posZ},
                {aimTarget.posX - 1, aimTarget.posY, aimTarget.posZ},
                {aimTarget.posX, aimTarget.posY, aimTarget.posZ + 1},
                {aimTarget.posX, aimTarget.posY, aimTarget.posZ - 1}
        };
        return HoleUtil.isHole(EntityUtil.getPosition(aimTarget), true, true).getType() != HoleUtil.HoleType.NONE;
    }

    enum ETarget { NEAREST, LOOKING }
    enum EBreakCrystal { VANILLA, PACKET, NONE }
    enum EBreakBlock { NORMAL, PACKET }

    private static class structureTemp {
        public double distance;
        public int supportBlock;
        public ArrayList<Vec3d> to_place;
        public int direction;

        public structureTemp(double distance, int supportBlock, ArrayList<Vec3d> to_place) {
            this.distance = distance;
            this.supportBlock = supportBlock;
            this.to_place = to_place;
            this.direction = -1;
        }
    }
}