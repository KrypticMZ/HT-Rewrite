package me.htrewrite.client.command.commands;

import me.htrewrite.client.command.Command;
import me.htrewrite.client.util.MathUtil;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Vec3d;

public class ClipCommand extends Command {
    public ClipCommand() {
        super("clip", "<horizontal/vertical> <blocks>", "Allows you to clip blocks.");
    }

    @Override
    public void call(String[] args) {
        if(args.length < 2) {
            sendInvalidUsageMessage();
            return;
        }

        double blocks = Double.parseDouble(args[1]);
        Vec3d direction = MathUtil.direction(mc.player.rotationYaw);
        Entity entity = mc.player.isRiding() ? mc.player.getRidingEntity() : mc.player;
        if(entity == null)
            return;

        switch(args[0].toLowerCase()) {
            case "horizontal": case "h": {
                entity.setPosition(mc.player.posX + direction.x*blocks, mc.player.posY, mc.player.posZ + direction.z*blocks);
                sendMessage("Clipped " + blocks + " forward!");
            } break;

            case "vertical": case "v": {
                entity.setPosition(mc.player.posX, mc.player.posY + blocks, mc.player.posZ);
                sendMessage("Clipped " + blocks + " up!");
            } break;

            default:
                sendInvalidUsageMessage();
                break;
        }
    }
}