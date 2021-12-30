package me.htrewrite.client.module.modules.combat;

import static me.htrewrite.client.util.ChatColor.*;

import me.htrewrite.client.clickgui.ClickGuiScreen;
import me.htrewrite.client.clickgui.components.buttons.settings.bettermode.BetterMode;
import me.htrewrite.client.event.custom.player.PlayerUpdateEvent;
import me.htrewrite.client.module.Module;
import me.htrewrite.client.module.ModuleType;
import me.htrewrite.exeterimports.mcapi.settings.ModeSetting;
import me.htrewrite.exeterimports.mcapi.settings.ToggleableSetting;
import me.htrewrite.exeterimports.mcapi.settings.ValueSetting;
import me.htrewrite.salimports.util.PlayerUtil;
import me.zero.alpine.fork.listener.EventHandler;
import me.zero.alpine.fork.listener.Listener;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.init.Items;
import net.minecraft.init.MobEffects;
import net.minecraft.inventory.ClickType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;

public class AutoTotemModule extends Module {
    public static final ValueSetting<Double> health = new ValueSetting<Double>("Health", null, 13.1D, 0D, 20D);
    public static final ModeSetting mode = new ModeSetting("Mode", null, 2, BetterMode.construct("Totem", "Gap", "Crystal", "Pearl", "Chorus", "Strength"));
    public static final ModeSetting fallbackMode = new ModeSetting("Fallback", null, 2, BetterMode.construct("Totem", "Gap", "Crystal", "Pearl", "Chorus", "Strength"));
    public static final ValueSetting<Double> fallDistance = new ValueSetting<Double>("FallDistance", null, 15D, 0D, 100D);
    public static final ToggleableSetting totemOnElytra = new ToggleableSetting("TotemOnElytra", null, true);
    public static final ToggleableSetting offhandGapOnSword = new ToggleableSetting("SwordGap", null, true);
    public static final ToggleableSetting offhandStrNoStrSword = new ToggleableSetting("StrGap", null, false);
    public static final ToggleableSetting hotbarFirst = new ToggleableSetting("HotbarFirst", null, false);
    public static final ToggleableSetting showMsg = new ToggleableSetting("ShowMsg", null,true);
    public static AutoTotemModule INSTANCE;


    public AutoTotemModule() {
        super("AutoTotem", "Makes you don't die if you have totems.", ModuleType.Combat, 0);
        addOption(health);
        addOption(mode);
        addOption(fallbackMode);
        addOption(fallDistance);
        addOption(totemOnElytra);
        addOption(offhandGapOnSword);
        addOption(offhandStrNoStrSword);
        addOption(hotbarFirst);
        addOption(showMsg);
        endOption();

        INSTANCE = this;
    }

    @Override
    public String getMeta() { return mode.getValue(); }

    private void switchOffHandIfNeed(int iMode) {
        Item item = getItemFromModeVal(iMode);
        if (mc.player.getHeldItemOffhand().getItem() != item) {
            int slot = hotbarFirst.isEnabled() ? PlayerUtil.getRecursiveItemSlot(item) : PlayerUtil.getItemSlot(item);
            Item fallback = getItemFromModeVal(fallbackMode.getI());
            String display = getItemNameFromModeVal(iMode);

            if (slot == -1 && item != fallback && mc.player.getHeldItemOffhand().getItem() != fallback) {
                slot = PlayerUtil.getRecursiveItemSlot(fallback);
                display = getItemNameFromModeVal(fallbackMode.getI());

                if (slot == -1 && fallback != Items.TOTEM_OF_UNDYING) {
                    fallback = Items.TOTEM_OF_UNDYING;
                    if (item != fallback && mc.player.getHeldItemOffhand().getItem() != fallback) {
                        slot = PlayerUtil.getRecursiveItemSlot(fallback);
                        display = "Emergency Totem";
                    }
                }
            }
            if (slot != -1) {
                mc.playerController.windowClick(mc.player.inventoryContainer.windowId, slot, 0,
                        ClickType.PICKUP, mc.player);
                mc.playerController.windowClick(mc.player.inventoryContainer.windowId, 45, 0, ClickType.PICKUP,
                        mc.player);

                mc.playerController.windowClick(mc.player.inventoryContainer.windowId, slot, 0,
                        ClickType.PICKUP, mc.player);
                mc.playerController.updateController();

                if (showMsg.isEnabled())
                    mc.player.sendMessage(new TextComponentString(prefix_parse('&', "&e[AutoTotem] &bOffhand has now a " + display + " &c(May desync)")));
            }
        }
    }

    public Item getItemFromModeVal(int iMode) {
        switch (iMode) {
            case 2:
                return Items.END_CRYSTAL;
            case 1:
                return Items.GOLDEN_APPLE;
            case 3:
                return Items.ENDER_PEARL;
            case 4:
                return Items.CHORUS_FRUIT;
            case 5:
                return Items.POTIONITEM;
            default:
                break;
        }
        return Items.TOTEM_OF_UNDYING;
    }
    private String getItemNameFromModeVal(int iMode) {
        if(iMode == 2) return "End Crystal";
        return mode.getModes()[iMode].mode;
    }

    private boolean rightClicking = false;

    @EventHandler
    private Listener<PlayerUpdateEvent> playerUpdateEventListener = new Listener<>(event -> {
        if (mc.currentScreen != null && (!(mc.currentScreen instanceof GuiInventory) && !(mc.currentScreen instanceof ClickGuiScreen)))
            return;

        float healthF = (mc.player.getHealth() + mc.player.getAbsorptionAmount());
        if (!mc.player.getHeldItemMainhand().isEmpty()) {
            if (health.getValue().doubleValue() <= healthF && mc.player.getHeldItemMainhand().getItem() instanceof ItemSword && offhandStrNoStrSword.isEnabled() && !mc.player.isPotionActive(MobEffects.STRENGTH)) {
                switchOffHandIfNeed(5);
                return;
            }
            if (health.getValue().doubleValue() <= healthF && mc.player.getHeldItemMainhand().getItem() instanceof ItemSword && offhandGapOnSword.isEnabled()) {
                if(rightClicking) {
                    switchOffHandIfNeed(1);
                    return;
                }
            }
        }

        if (health.getValue().doubleValue() > healthF || mode.getI() == 0 || (totemOnElytra.isEnabled() && mc.player.isElytraFlying()) || (mc.player.fallDistance >= fallDistance.getValue().doubleValue() && !mc.player.isElytraFlying())) {
            switchOffHandIfNeed(0);
            return;
        }
        switchOffHandIfNeed(mode.getI());
    });

    @EventHandler
    private Listener<PlayerInteractEvent.RightClickItem> rightClickItemListener = new Listener<>(event -> {
        ItemStack itemStack = event.getItemStack();
        if(itemStack.getItem() instanceof ItemSword)
            rightClicking = true;
    });

    @EventHandler
    private Listener<LivingEntityUseItemEvent.Stop> stopListener = new Listener<>(event -> {
        rightClicking = false;
    });
}