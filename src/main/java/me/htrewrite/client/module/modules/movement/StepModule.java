package me.htrewrite.client.module.modules.movement;

import me.htrewrite.client.event.custom.player.PlayerUpdateEvent;
import me.htrewrite.client.module.Module;
import me.htrewrite.client.module.ModuleType;
import me.htrewrite.exeterimports.mcapi.settings.ToggleableSetting;
import me.htrewrite.exeterimports.mcapi.settings.ValueSetting;
import me.zero.alpine.fork.listener.EventHandler;
import me.zero.alpine.fork.listener.Listener;
import net.minecraft.block.Block;
import net.minecraft.block.BlockAir;
import net.minecraft.block.material.Material;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;

public class StepModule extends Module {
    public static final ToggleableSetting vanilla = new ToggleableSetting("Vanilla", null, false);
    public static final ValueSetting<Double> stepHeightVanilla = new ValueSetting<>("VHeight", null, 2D, 0D, 4D);
    public static final ValueSetting<Double> stepHeight = new ValueSetting<>("Height", null, 1D, 1D, 4D);
    public static final ToggleableSetting spoof = new ToggleableSetting("Spoof", null, true);
    public static final ValueSetting<Double> ticks = new ValueSetting<>("Ticks", null, 3D, 0D, 25D);
    public static final ToggleableSetting turnOff = new ToggleableSetting("Toggle", null, false);
    public static final ToggleableSetting check = new ToggleableSetting("Check", null, true);
    public static final ToggleableSetting small = new ToggleableSetting("Offset", null, false);

    private final double[] oneblockPositions = new double[]{0.42, 0.75};
    private final double[] twoblockPositions = new double[]{0.4, 0.75, 0.5, 0.41, 0.83, 1.16, 1.41, 1.57, 1.58, 1.42};
    private final double[] futurePositions = new double[]{0.42, 0.78, 0.63, 0.51, 0.9, 1.21, 1.45, 1.43};
    final double[] twoFiveOffset = new double[]{0.425, 0.821, 0.699, 0.599, 1.022, 1.372, 1.652, 1.869, 2.019, 1.907};
    private final double[] threeBlockPositions = new double[]{0.42, 0.78, 0.63, 0.51, 0.9, 1.21, 1.45, 1.43, 1.78, 1.63, 1.51, 1.9, 2.21, 2.45, 2.43};
    private final double[] fourBlockPositions = new double[]{0.42, 0.78, 0.63, 0.51, 0.9, 1.21, 1.45, 1.43, 1.78, 1.63, 1.51, 1.9, 2.21, 2.45, 2.43, 2.78, 2.63, 2.51, 2.9, 3.21, 3.45, 3.43};
    private double[] selectedPositions = new double[0];
    private int packets;
    private static StepModule instance;

    public StepModule() {
        super("Step", "You can step blocks.", ModuleType.Movement, 0);
        addOption(vanilla);
        addOption(stepHeightVanilla.setVisibility(v -> vanilla.isEnabled()));
        addOption(stepHeight.setVisibility(v -> !vanilla.isEnabled()));
        addOption(spoof.setVisibility(v -> !vanilla.isEnabled()));
        addOption(ticks.setVisibility(v -> spoof.isEnabled() && !vanilla.isEnabled()));
        addOption(turnOff.setVisibility(v -> !vanilla.isEnabled()));
        addOption(check.setVisibility(v -> !vanilla.isEnabled()));
        addOption(small.setVisibility(v -> stepHeight.getValue().intValue()>1 && !vanilla.isEnabled()));
        endOption();

        instance = this;
    }

    public static StepModule getInstance() { return instance; }

    @Override
    public void toggle() {
        super.toggle();
        mc.player.stepHeight = 0.6f;
    }

    @EventHandler
    private Listener<PlayerUpdateEvent> updateEventListener = new Listener<>(event -> {
        if(vanilla.isEnabled()) {
            mc.player.stepHeight = stepHeightVanilla.getValue().floatValue();
            return;
        }
        switch (stepHeight.getValue().intValue()) {
            case 1: {
                selectedPositions = oneblockPositions;
                break;
            }
            case 2: {
                selectedPositions = small.isEnabled() ? twoblockPositions : futurePositions;
                break;
            }
            case 3: {
                selectedPositions = twoFiveOffset;
            }
            case 4: {
                selectedPositions = fourBlockPositions;
            }
        }
        if(mc.player.collidedHorizontally && mc.player.onGround)
            ++packets;

        AxisAlignedBB bb = mc.player.getEntityBoundingBox();
        if(check.isEnabled()) {
            for (int x = MathHelper.floor((double)bb.minX); x < MathHelper.floor((double)(bb.maxX + 1.0)); ++x) {
                for (int z = MathHelper.floor((double)bb.minZ); z < MathHelper.floor((double)(bb.maxZ + 1.0)); ++z) {
                    Block block = mc.world.getBlockState(new BlockPos((double)x, bb.maxY + 1.0, (double)z)).getBlock();
                    if (block instanceof BlockAir)
                        continue;
                    return;
                }
            }
        }
        if (mc.player.onGround && !mc.player.isInsideOfMaterial(Material.WATER) && !mc.player.isInsideOfMaterial(Material.LAVA) && mc.player.collidedVertically && mc.player.fallDistance == 0.0f && !mc.gameSettings.keyBindJump.pressed && mc.player.collidedHorizontally && !mc.player.isOnLadder() && (this.packets > this.selectedPositions.length - 2 || this.spoof.isEnabled() && this.packets > this.ticks.getValue())) {
            for (double position : this.selectedPositions)
                mc.player.connection.sendPacket((Packet)new CPacketPlayer.Position(mc.player.posX, mc.player.posY + position, mc.player.posZ, true));
            mc.player.setPosition(mc.player.posX, mc.player.posY + this.selectedPositions[this.selectedPositions.length - 1], mc.player.posZ);
            this.packets = 0;
            if (this.turnOff.isEnabled())
                toggle();
        }
    });
}