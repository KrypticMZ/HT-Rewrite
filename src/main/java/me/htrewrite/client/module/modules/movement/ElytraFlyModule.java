package me.htrewrite.client.module.modules.movement;

import me.htrewrite.client.clickgui.components.buttons.settings.bettermode.BetterMode;
import me.htrewrite.client.event.custom.networkmanager.NetworkPacketEvent;
import me.htrewrite.client.event.custom.player.PlayerTravelEvent;
import me.htrewrite.client.module.Module;
import me.htrewrite.client.module.ModuleType;
import me.htrewrite.client.util.ChatColor;
import me.htrewrite.client.util.MathUtil;
import me.htrewrite.client.util.Timer;
import me.htrewrite.exeterimports.mcapi.settings.ModeSetting;
import me.htrewrite.exeterimports.mcapi.settings.ToggleableSetting;
import me.htrewrite.exeterimports.mcapi.settings.ValueSetting;
import me.zero.alpine.fork.listener.EventHandler;
import me.zero.alpine.fork.listener.Listener;
import net.minecraft.init.Items;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemElytra;
import net.minecraft.item.ItemStack;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.util.text.TextComponentString;

public class ElytraFlyModule extends Module {
    public static final ModeSetting mode = new ModeSetting("Mode", null, 2, BetterMode.construct("Normal", "Superior", "Control"));
    public static final ValueSetting<Double> speed = new ValueSetting<>("Speed", null, 1.82D, 0D, 10D);
    public static final ValueSetting<Double> downSpeed = new ValueSetting<>("DownSpeed", null, 1.82D, 0D, 10D);
    public static final ValueSetting<Double> glideSpeed = new ValueSetting<>("GlideSpeed", null, 1D, 0D, 10D);
    public static final ValueSetting<Double> upSpeed = new ValueSetting<>("UpSpeed", null, 2D, 0D, 10D);
    public static final ToggleableSetting accelerate = new ToggleableSetting("Accelerate", null, true);
    public static final ValueSetting<Double> accelerationTimer = new ValueSetting<>("AccelerationTimer", null, 1D, 0D, 10D);
    public static final ValueSetting<Double> rotationPitch = new ValueSetting<>("RotationPitch", null, 0D, -90D, 90D);
    public static final ToggleableSetting cancelInWater = new ToggleableSetting("CancelInWater", null, false);
    public static final ValueSetting<Double> cancelAtHeight = new ValueSetting<>("CancelAtHeight", null, 5D, 0D, 10D);
    public static final ToggleableSetting instantFly = new ToggleableSetting("InstantFly", null, true);
    public static final ToggleableSetting equipElytra = new ToggleableSetting("EquipElytra", null, false);
    public static final ToggleableSetting pitchSpoof = new ToggleableSetting("PitchSpoof", null, false);

    private Timer _packetTimer = new Timer();
    private Timer _accelerationTimer = new Timer();
    private Timer _accelerationResetTimer = new Timer();
    private Timer _instantFlyTimer = new Timer();
    private boolean sendMessage = false;
    private int elytraSlot = -1;

    public ElytraFlyModule() {
        super("ElytraFly", "Fly with elytra.", ModuleType.Movement, 0);
        addOption(mode);
        addOption(speed);
        addOption(downSpeed);
        addOption(glideSpeed);
        addOption(upSpeed);
        addOption(accelerate);
        addOption(accelerationTimer);
        addOption(rotationPitch);
        addOption(cancelInWater);
        addOption(cancelAtHeight);
        addOption(instantFly);
        addOption(equipElytra);
        addOption(pitchSpoof);
        endOption();
    }

    @Override
    public void onEnable() {
        super.onEnable();

        elytraSlot = -1;
        if(equipElytra.isEnabled()) {
            if(mc.player != null && mc.player.getItemStackFromSlot(EntityEquipmentSlot.CHEST).getItem() != Items.ELYTRA) {
                for(int i = 0; i < 44; i++) {
                    ItemStack itemStack = mc.player.inventory.getStackInSlot(i);
                    if(itemStack.isEmpty() || itemStack.getItem() != Items.ELYTRA) continue;

                    ItemElytra itemElytra = (ItemElytra) itemStack.getItem();
                    elytraSlot = i;
                    break;
                }
                if(elytraSlot != -1) {
                    boolean armorChest = mc.player.getItemStackFromSlot(EntityEquipmentSlot.CHEST).getItem() != Items.AIR;
                    mc.playerController.windowClick(mc.player.inventoryContainer.windowId, elytraSlot, 0, ClickType.PICKUP, mc.player);
                    mc.playerController.windowClick(mc.player.inventoryContainer.windowId, 6, 0, ClickType.PICKUP, mc.player);
                    if(armorChest) mc.playerController.windowClick(mc.player.inventoryContainer.windowId, elytraSlot, 0, ClickType.PICKUP, mc.player);
                }
            }
        }
    }

    @Override
    public void onDisable() {
        super.onDisable();

        if(mc.player == null) return;
        if(elytraSlot != -1) {
            boolean hasItem = !mc.player.inventory.getStackInSlot(elytraSlot).isEmpty() || mc.player.inventory.getStackInSlot(elytraSlot).getItem() != Items.AIR;
            mc.playerController.windowClick(mc.player.inventoryContainer.windowId, 6, 0, ClickType.PICKUP, mc.player);
            mc.playerController.windowClick(mc.player.inventoryContainer.windowId, elytraSlot, 0, ClickType.PICKUP, mc.player);
            if (hasItem) mc.playerController.windowClick(mc.player.inventoryContainer.windowId, 6, 0, ClickType.PICKUP, mc.player);
        }
    }

    @EventHandler
    private Listener<PlayerTravelEvent> playerTravelEventListener = new Listener<>(event -> {
        if(mc.player == null) return;
        if(mc.player.getItemStackFromSlot(EntityEquipmentSlot.CHEST).getItem() != Items.ELYTRA) return;

        if(!mc.player.isElytraFlying()) {
            if(!mc.player.onGround && instantFly.isEnabled() && _instantFlyTimer.passed(1000)) {
                _instantFlyTimer.reset();
                mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.START_FALL_FLYING));
            }
            return;
        }

        /*
            * Normal   == 0
              Superior == 1
            * Control  == 2
                             */

        switch (mode.getI()) {
            case 0: handleNormal(event); break;
            case 1: handleSuperior(event); break;
            case 2: handleControl(event); break;
            default: break;
        }
    });

    public void accelerate() {
        if(_accelerationResetTimer.passed(accelerationTimer.getValue() * 1000)) {
            _accelerationResetTimer.reset();
            _accelerationTimer.reset();
            sendMessage = false;
        }
        float speed = this.speed.getValue().floatValue();
        double[] dir = MathUtil.directionSpeed(speed);

        boolean cum = mc.player.movementInput.moveStrafe != 0 || mc.player.movementInput.moveForward != 0;
        mc.player.motionX = cum ? dir[0] : 0;
        mc.player.motionY = mc.player.movementInput.sneak ? -downSpeed.getValue() : -(glideSpeed.getValue() / 10000f);
        mc.player.motionZ = cum ? dir[1] : 0;

        mc.player.prevLimbSwingAmount = 0;
        mc.player.limbSwingAmount = 0;
        mc.player.limbSwing = 0;
    }

    public void handleNormal(PlayerTravelEvent event) {
        double height = mc.player.posY;
        if(height <= cancelAtHeight.getValue() && !sendMessage) {
            mc.player.sendMessage(new TextComponentString(ChatColor.prefix_parse('&', "&eHEY! &cYou must go up because your y-height is lower than cancelAtHeight value!")));
            sendMessage = true;
            return;
        }
        boolean moveKeyDown = mc.player.movementInput.moveForward > 0 || mc.player.movementInput.moveStrafe > 0;
        boolean _cancelInWater = !mc.player.isInWater() && !mc.player.isInLava() && cancelInWater.isEnabled();
        if(mc.player.movementInput.jump) {
            event.cancel();
            accelerate();
            return;
        }
        if(moveKeyDown) _accelerationTimer.resetTimeSkipTo(-accelerationTimer.getValue().longValue());
        else if((mc.player.rotationPitch <= rotationPitch.getValue()) && _cancelInWater && accelerate.isEnabled() && _accelerationTimer.passed(accelerationTimer.getValue())) {
            accelerate();
            return;
        }
        event.cancel();
        accelerate();
    }

    public void handleSuperior(PlayerTravelEvent event) {
        if(mc.player.movementInput.jump) {
            double motion = Math.sqrt(mc.player.motionX * mc.player.motionX + mc.player.motionZ * mc.player.motionZ);
            if(motion > 1D) return;

            double[] dir = MathUtil.directionSpeedNoForward(speed.getValue());
            mc.player.motionX = dir[0];
            mc.player.motionY = -(glideSpeed.getValue() / 10000f);
            mc.player.motionZ = dir[1];

            event.cancel();
            return;
        }
        mc.player.setVelocity(0, 0, 0);

        double[] dir = MathUtil.directionSpeed(speed.getValue());
        if(mc.player.movementInput.moveStrafe != 0 || mc.player.movementInput.moveForward != 0)
        {
            mc.player.motionX = dir[0];
            mc.player.motionY = -(glideSpeed.getValue() / 10000f);
            mc.player.motionZ = dir[1];
        }
        if(mc.player.movementInput.sneak) mc.player.motionY = -downSpeed.getValue();
        mc.player.prevLimbSwingAmount = 0;
        mc.player.limbSwingAmount = 0;
        mc.player.limbSwing = 0;
    }

    public void handleControl(PlayerTravelEvent event) {
        double[] dir = MathUtil.directionSpeed(speed.getValue());
        if (mc.player.movementInput.moveStrafe != 0 || mc.player.movementInput.moveForward != 0)
        {
            mc.player.motionX = dir[0];
            mc.player.motionZ = dir[1];
            mc.player.motionX -= (mc.player.motionX*(Math.abs(mc.player.rotationPitch)+90)/90) - mc.player.motionX;
            mc.player.motionZ -= (mc.player.motionZ*(Math.abs(mc.player.rotationPitch)+90)/90) - mc.player.motionZ;
        } else {
            mc.player.motionX = 0;
            mc.player.motionZ = 0;
        }
        mc.player.motionY = (-MathUtil.degToRad(mc.player.rotationPitch)) * mc.player.movementInput.moveForward;

        mc.player.prevLimbSwingAmount = 0;
        mc.player.limbSwingAmount = 0;
        mc.player.limbSwing = 0;
        event.cancel();
    }

    @EventHandler
    private Listener<NetworkPacketEvent> networkPacketEventListener = new Listener<>(event -> {
        Packet packet = event.getPacket();
        if(packet instanceof CPacketPlayer && pitchSpoof.isEnabled() && mc.player.isElytraFlying())
            if(packet instanceof CPacketPlayer.PositionRotation) {
                CPacketPlayer.PositionRotation packetC = (CPacketPlayer.PositionRotation)packet;
                mc.getConnection().sendPacket(new CPacketPlayer.Position(packetC.getX(0), packetC.getY(0), packetC.getZ(0), packetC.isOnGround()));
                event.cancel();
            } else if(packet instanceof CPacketPlayer.Rotation) event.cancel();
    });

    @Override
    public String getMeta() { return mode.getValue(); }
}