package me.htrewrite.client.module.modules.movement;

import me.htrewrite.client.event.custom.player.PlayerMotionUpdateEvent;
import me.htrewrite.client.module.Module;
import me.htrewrite.client.module.ModuleType;
import me.htrewrite.client.module.modules.render.HoleESPModule;
import me.htrewrite.exeterimports.mcapi.settings.ToggleableSetting;
import me.htrewrite.exeterimports.mcapi.settings.ValueSetting;
import me.htrewrite.salimports.util.ESPUtil;
import me.htrewrite.salimports.util.Hole;
import me.zero.alpine.fork.listener.EventHandler;
import me.zero.alpine.fork.listener.Listener;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public class AnchorModule extends Module {
    public static final ValueSetting<Double> pitch = new ValueSetting<>("Pitch", null, 60d, 0d, 90d);
    public static final ToggleableSetting pull = new ToggleableSetting("Pull", null, true);

    public static boolean AnchorING;
    int holeBlocks;
    public boolean isBlockHole(BlockPos blockpos) { return ESPUtil.isBlockValid(mc.world.getBlockState(blockpos), blockpos) != Hole.HoleTypes.None; }

    public AnchorModule() {
        super("Anchor", "Stops movements if player above hole.", ModuleType.Movement, 0);
        addOption(pitch);
        addOption(pull);
        endOption();
    }
    private Vec3d Center = Vec3d.ZERO;
    public Vec3d GetCenter(double posX, double posY, double posZ) {
        double x = Math.floor(posX) + 0.5D;
        double y = Math.floor(posY);
        double z = Math.floor(posZ) + 0.5D ;

        return new Vec3d(x, y, z);
    }

    @EventHandler
    private Listener<PlayerMotionUpdateEvent> motionUpdateEventListener = new Listener<>(event -> {
        if(mc.player.rotationPitch >= pitch.getValue().floatValue()) {
            if (isBlockHole(getPlayerPos().down(1)) || isBlockHole(getPlayerPos().down(2)) ||
                    isBlockHole(getPlayerPos().down(3)) || isBlockHole(getPlayerPos().down(4))) {
                AnchorING = true;

                if (!pull.isEnabled()) {
                    mc.player.motionX = 0.0;
                    mc.player.motionZ = 0.0;
                } else {
                    Center = GetCenter(mc.player.posX, mc.player.posY, mc.player.posZ);
                    double XDiff = Math.abs(Center.x - mc.player.posX);
                    double ZDiff = Math.abs(Center.z - mc.player.posZ);

                    if (XDiff <= 0.1 && ZDiff <= 0.1) {
                        Center = Vec3d.ZERO;
                    }
                    else {
                        double MotionX = Center.x-mc.player.posX;
                        double MotionZ = Center.z-mc.player.posZ;

                        mc.player.motionX = MotionX/2;
                        mc.player.motionZ = MotionZ/2;
                    }
                }
            } else AnchorING = false;
        }
    });

    @Override
    public void onDisable() {
        super.onDisable();

        AnchorING = false;
        holeBlocks = 0;
    }

    public BlockPos getPlayerPos() {
        return new BlockPos(Math.floor(mc.player.posX), Math.floor(mc.player.posY), Math.floor(mc.player.posZ));
    }
}