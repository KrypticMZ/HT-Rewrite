package me.htrewrite.client.module.modules.combat;

import me.htrewrite.client.HTRewrite;
import me.htrewrite.client.clickgui.components.buttons.settings.bettermode.BetterMode;
import me.htrewrite.client.event.custom.CustomEvent;
import me.htrewrite.client.event.custom.player.PlayerMotionUpdateEvent;
import me.htrewrite.client.module.Module;
import me.htrewrite.client.module.ModuleType;
import me.htrewrite.client.util.MathUtil;
import me.htrewrite.client.util.RotationSpoof;
import me.htrewrite.client.util.Timer;
import me.htrewrite.exeterimports.mcapi.settings.ModeSetting;
import me.htrewrite.exeterimports.mcapi.settings.ToggleableSetting;
import me.htrewrite.exeterimports.mcapi.settings.ValueSetting;
import me.htrewrite.salimports.util.EntityUtil;
import me.htrewrite.salimports.util.PlayerUtil;
import me.zero.alpine.fork.listener.EventHandler;
import me.zero.alpine.fork.listener.Listener;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.passive.AbstractChestHorse;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityFireball;
import net.minecraft.entity.projectile.EntityShulkerBullet;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.client.CPacketUseEntity;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import javax.annotation.Nullable;
import java.util.Comparator;

public class KillAuraModule extends Module {
    public static final ModeSetting aimMode = new ModeSetting("AimMode", null, 0, BetterMode.construct("Packet", "Client"));
    public static final ModeSetting mode = new ModeSetting("Mode", null, 0, BetterMode.construct("Closest", "Priority", "Switch"));
    public static final ValueSetting<Double> distance = new ValueSetting<>("Distance", null, 5d, 0d, 10d);
    public static final ToggleableSetting hitDelay = new ToggleableSetting("HitDelay", null, true);
    public static final ToggleableSetting tpsSync = new ToggleableSetting("TPSSync", null, false);
    public static final ToggleableSetting players = new ToggleableSetting("Players", null, true);
    public static final ToggleableSetting monsters = new ToggleableSetting("Monsters", null, true);
    public static final ToggleableSetting neutrals = new ToggleableSetting("Neutrals", null, false);
    public static final ToggleableSetting animals = new ToggleableSetting("Animals", null, false);
    public static final ToggleableSetting tamed = new ToggleableSetting("Tamed", null, false);
    public static final ToggleableSetting projectiles = new ToggleableSetting("Projectiles", null, false);
    public static final ToggleableSetting swordOnly = new ToggleableSetting("SwordOnly", null, false);
    public static final ToggleableSetting pauseIfCrystal = new ToggleableSetting("CrystalPause", null, false);
    public static final ToggleableSetting pauseIfEating = new ToggleableSetting("EatingPause", null, false);
    public static final ToggleableSetting autoSwitch = new ToggleableSetting("AutoSwitch", null, false);
    public static final ToggleableSetting only32k = new ToggleableSetting("Only32k", null, false);
    public static final ValueSetting<Double> ticks = new ValueSetting<>("Ticks", null, 10d, 0d, 40d);
    public static final ValueSetting<Double> iterations = new ValueSetting<>("Iterations", null, 1d, 1d, 10d);

    public RotationSpoof rotationSpoof;

    public KillAuraModule() {
        super("KillAura", "Automatically hits entities around you.", ModuleType.Combat, 0);
        addOption(aimMode);
        addOption(mode);
        addOption(distance);
        addOption(hitDelay);
        addOption(tpsSync);
        addOption(players);
        addOption(monsters);
        addOption(neutrals);
        addOption(animals);
        addOption(tamed);
        addOption(projectiles);
        addOption(swordOnly);
        addOption(pauseIfCrystal);
        addOption(pauseIfEating);
        addOption(autoSwitch);
        addOption(only32k);
        addOption(ticks);
        addOption(iterations);
        endOption();
    }

    @EventHandler
    private Listener<PlayerMotionUpdateEvent> motionUpdateEventListener = new Listener<>(event -> {
        if(event.getEra() != CustomEvent.Era.PRE || rotationSpoof == null)
            return;
        event.cancel();

        boolean sprinting = mc.player.isSprinting();
        if(sprinting != mc.player.serverSprintState) {
            mc.player.connection.sendPacket(sprinting?
                    new CPacketEntityAction(mc.player, CPacketEntityAction.Action.START_SPRINTING):
                    new CPacketEntityAction(mc.player, CPacketEntityAction.Action.STOP_SPRINTING));
            mc.player.serverSprintState = sprinting;
        }

        boolean sneaking = mc.player.isSneaking();
        if(sneaking != mc.player.serverSneakState) {
            mc.player.connection.sendPacket(sneaking?
                    new CPacketEntityAction(mc.player, CPacketEntityAction.Action.START_SNEAKING):
                    new CPacketEntityAction(mc.player, CPacketEntityAction.Action.STOP_SNEAKING));
            mc.player.serverSneakState = sneaking;
        }

        if(PlayerUtil.isCurrentViewEntity()) {
            float yaw = mc.player.rotationYaw;
            float pitch = mc.player.rotationPitch;
            if(rotationSpoof != null) {
                yaw = rotationSpoof.yaw;
                pitch = rotationSpoof.pitch;

                switch (aimMode.getI()) {
                    case 0: mc.player.rotationYawHead = yaw; break;
                    case 1:
                    default:
                        mc.player.rotationYaw = yaw;
                        mc.player.rotationPitch = pitch;
                        break;
                }
            }

            AxisAlignedBB alignedBB = mc.player.getEntityBoundingBox();
            double posXDif = mc.player.posX - mc.player.lastReportedPosX;
            double posYDif = alignedBB.minY - mc.player.lastReportedPosY;
            double posZDif = mc.player.posZ - mc.player.lastReportedPosZ;
            double posYawDif = yaw - mc.player.lastReportedYaw;
            double posPitchDif = pitch - mc.player.lastReportedPitch;
            ++mc.player.positionUpdateTicks;

            boolean movedXYX = (posXDif*posXDif + posYDif*posYDif + posZDif*posZDif)>9.0E-4D || mc.player.positionUpdateTicks >= 20;
            boolean movedRot = posYawDif!=.0d || posPitchDif!=.0d;

            if(mc.player.isRiding()) {
                mc.player.connection.sendPacket(new CPacketPlayer.PositionRotation(mc.player.motionX, -999D, mc.player.motionZ, yaw, pitch, mc.player.onGround));
                movedXYX = false;
            } else if(movedXYX && movedRot) mc.player.connection.sendPacket(new CPacketPlayer.PositionRotation(mc.player.posX, alignedBB.minY, mc.player.posZ, yaw, pitch, mc.player.onGround));
            else if(movedXYX) mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX, alignedBB.minY, mc.player.posZ, mc.player.onGround));
            else if(movedRot) mc.player.connection.sendPacket(new CPacketPlayer.Rotation(yaw, pitch, mc.player.onGround));
            else if(mc.player.prevOnGround != mc.player.onGround) mc.player.connection.sendPacket(new CPacketPlayer(mc.player.onGround));

            if(movedXYX) {
                mc.player.lastReportedPosX = mc.player.posX;
                mc.player.lastReportedPosY = alignedBB.minY;
                mc.player.lastReportedPosZ = mc.player.posZ;
                mc.player.positionUpdateTicks = 0;
            }

            if(movedRot) {
                mc.player.lastReportedYaw = yaw;
                mc.player.lastReportedPitch = pitch;
            }
            mc.player.prevOnGround = mc.player.onGround;
            mc.player.autoJumpEnabled = mc.player.mc.gameSettings.autoJump;
        }
    });

    private Entity currentTarget;
    private Timer aimResetTimer = new Timer();
    private int remainingTicks = 0;

    @Override
    public void onEnable() {
        super.onEnable();

        remainingTicks = 0;
    }

    @Override
    public void onDisable() {
        super.onDisable();

        rotationSpoof = null;
    }

    @Override public String getMeta() { return aimMode.getValue().substring(0, 1)+mode.getValue(); }

    private boolean isValidTarget(Entity entity, @Nullable Entity toIgnore) {
        if(!(entity instanceof EntityLivingBase)) {
            boolean isProjectile = (entity instanceof EntityShulkerBullet || entity instanceof EntityFireball);
            if(!isProjectile || (isProjectile && !projectiles.isEnabled()))
                return false;
        }

        if(toIgnore != null && entity == toIgnore)
            return false;
        if(entity instanceof EntityPlayer && (entity == mc.player || !players.isEnabled() || HTRewrite.INSTANCE.getFriendManager().isFriend(entity.getName())))
            return false;
        if(EntityUtil.isHostileMob(entity) && !monsters.isEnabled())
            return false;
        if(EntityUtil.isPassive(entity)) {
            if(entity instanceof AbstractChestHorse)
                if(((AbstractChestHorse)entity).isTame() && !tamed.isEnabled())
                    return false;
            if(!animals.isEnabled())
                return false;
        }
        if(EntityUtil.isNeutralMob(entity) && !neutrals.isEnabled())
            return false;
        boolean healthCheck = true;
        if(entity instanceof EntityLivingBase) {
            EntityLivingBase livingBase = (EntityLivingBase)entity;
            healthCheck = !livingBase.isDead&&livingBase.getHealth()>.0f;
        }
        return healthCheck && entity.getDistance(entity)<=distance.getValue().floatValue();
    }

    private boolean is32k(ItemStack itemStack) {
        if(itemStack.getEnchantmentTagList() != null) {
            NBTTagList tagList = itemStack.getEnchantmentTagList();
            for(int i = 0; i < tagList.tagCount(); i++) {
                final NBTTagCompound tagCompound = tagList.getCompoundTagAt(i);
                if(tagCompound != null && Enchantment.getEnchantmentByID(tagCompound.getByte("id")) != null) {
                    final Enchantment enchantment = Enchantment.getEnchantmentByID(tagCompound.getShort("id"));
                    final short lvl = tagCompound.getShort("lvl");
                    if(enchantment != null) {
                        if(enchantment.isCurse())
                            continue;
                        if(lvl >= 1000)
                            return true;
                    }
                }
            }
        }

        return false;
    }

    @EventHandler
    private Listener<TickEvent.ClientTickEvent> tickEventListener = new Listener<>(event -> {
        if(nullCheck())
            return;

        ItemStack mainHandItemStack = mc.player.getHeldItemMainhand();
        Item mainHandItem = mainHandItemStack.getItem();
        if(!(mainHandItem instanceof ItemSword)) {
            if(mainHandItem == Items.END_CRYSTAL && pauseIfCrystal.isEnabled())
                return;
            if(mainHandItem == Items.GOLDEN_APPLE && pauseIfEating.isEnabled())
                return;
            int slot = -1;

            if(autoSwitch.isEnabled())
                for(int i = 0; i < 0; i++)
                    if(mc.player.inventory.getStackInSlot(i).getItem() instanceof ItemSword) {
                        slot = i;

                        mc.player.inventory.currentItem = slot;
                        mc.playerController.updateController();
                        break;
                    }
            if(swordOnly.isEnabled() && slot == -1)
                return;
        }

        if(only32k.isEnabled())
            if(!is32k(mainHandItemStack))
                return;

        if(aimResetTimer.passed(5000)) {
            aimResetTimer.reset();
            rotationSpoof = null;
        }

        if(remainingTicks>0)
            --remainingTicks;

        Entity targetToHit = currentTarget;
        switch (mode.getValue()) {
            case "Closest": {
                targetToHit = mc.world.loadedEntityList.stream()
                        .filter(entity -> isValidTarget(entity, null))
                        .min(Comparator.comparing(entity -> mc.player.getDistance(entity)))
                        .orElse(null);
            } break;
            case "Priority": {
                if(targetToHit == null) {
                    targetToHit = mc.world.loadedEntityList.stream()
                            .filter(entity -> isValidTarget(entity, null))
                            .min(Comparator.comparing(entity -> mc.player.getDistance(entity)))
                            .orElse(null);
                }
            } break;
            case "Switch": {
                targetToHit = mc.world.loadedEntityList.stream()
                        .filter(entity -> isValidTarget(entity, null))
                        .min(Comparator.comparing(entity -> mc.player.getDistance(entity)))
                        .orElse(null);
                if(targetToHit == null)
                    targetToHit = currentTarget;
            } break;
            default: break;
        }

        if(targetToHit == null || targetToHit.getDistance(mc.player) > distance.getValue().floatValue()) {
            currentTarget = null;
            return;
        }

        float[] rotation = MathUtil.calcAngle(mc.player.getPositionEyes(mc.getRenderPartialTicks()), targetToHit.getPositionEyes(mc.getRenderPartialTicks()));
        rotationSpoof = new RotationSpoof(rotation[0], rotation[1]);

        final float ticks = 20f - HTRewrite.INSTANCE.getTickRateManager().getTickRate();
        final boolean isAttackReady = hitDelay.isEnabled()?(mc.player.getCooledAttackStrength(tpsSync.isEnabled()?-ticks:0f) >= 1):true;
        if(!isAttackReady || (!hitDelay.isEnabled()&&remainingTicks>0))
            return;
        remainingTicks = this.ticks.getValue().intValue();
        for(int i = 0; i < iterations.getValue().intValue(); i++) {
            mc.player.connection.sendPacket(new CPacketUseEntity(targetToHit));
            mc.player.swingArm(EnumHand.MAIN_HAND);
            mc.player.resetCooldown();
        }
    });
}