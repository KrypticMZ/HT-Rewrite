package me.htrewrite.client.module.modules.miscellaneous;

import me.htrewrite.client.event.custom.player.PlayerUpdateEvent;
import me.htrewrite.client.module.Module;
import me.htrewrite.client.module.ModuleType;
import me.htrewrite.exeterimports.mcapi.settings.ValueSetting;
import me.zero.alpine.fork.listener.EventHandler;
import me.zero.alpine.fork.listener.Listener;
import net.minecraft.init.Items;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;

public class AutoEatModule extends Module {
    public static final ValueSetting<Double> eatGapHealth = new ValueSetting<>("Health", null, 15d, 0d, 36d);
    public static final ValueSetting<Double> hunger = new ValueSetting<>("Hunger", null, 18d, 0d, 20d);

    public AutoEatModule() {
        super("AutoEat", "Eats food lol.", ModuleType.Miscellaneous, 0);
        addOption(eatGapHealth);
        addOption(hunger);
        endOption();
    }

    private boolean wasEating = false;

    @Override
    public void onDisable() {
        super.onDisable();

        if(wasEating) {
            wasEating = false;
            mc.gameSettings.keyBindUseItem.pressed = false;
        }
    }

    private boolean isEating() { return mc.player != null && mc.player.getHeldItemMainhand().getItem() instanceof ItemFood && mc.player.isHandActive(); }

    @EventHandler
    private Listener<PlayerUpdateEvent> updateEventListener = new Listener<>(event -> {
        float health = mc.player.getHealth() + mc.player.getAbsorptionAmount();
        if(eatGapHealth.getValue().floatValue() >= health && !isEating()) {
            if(mc.player.getHeldItemMainhand().getItem() != Items.GOLDEN_APPLE) {
                for(int i = 0; i < 9; ++i) {
                    if(mc.player.inventory.getStackInSlot(i).isEmpty() || mc.player.inventory.getStackInSlot(i).getItem() != Items.GOLDEN_APPLE)
                        continue;

                    mc.player.inventory.currentItem = i;
                    mc.playerController.updateController();
                    break;
                }

                mc.playerController.processRightClick(mc.player, mc.world, EnumHand.MAIN_HAND);
                wasEating = true;
            }
            return;
        }

        if(!isEating() && hunger.getValue().floatValue() >= mc.player.getFoodStats().getFoodLevel()) {
            boolean canEat = false;
            for(int i = 0; i < 9; ++i) {
                ItemStack itemStack = mc.player.inventory.getStackInSlot(i);
                if(mc.player.inventory.getStackInSlot(i).isEmpty())
                    continue;

                if(itemStack.getItem() instanceof ItemFood) {
                    canEat = true;
                    mc.player.inventory.currentItem = i;
                    mc.playerController.updateController();
                    break;
                }
            }

            if(canEat) {
                if(mc.currentScreen == null)
                    mc.gameSettings.keyBindUseItem.pressed = true;
                else mc.playerController.processRightClick(mc.player, mc.world, EnumHand.MAIN_HAND);

                wasEating = true;
            }
        }

        if(wasEating) {
            wasEating = false;
            mc.gameSettings.keyBindUseItem.pressed = false;
        }
    });
}