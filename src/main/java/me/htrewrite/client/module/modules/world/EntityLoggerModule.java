package me.htrewrite.client.module.modules.world;

import me.htrewrite.client.event.custom.world.EntityAddedEvent;
import me.htrewrite.client.module.Module;
import me.htrewrite.client.module.ModuleType;
import me.htrewrite.client.util.ChatColor;
import me.htrewrite.exeterimports.mcapi.settings.ToggleableSetting;
import me.zero.alpine.fork.listener.EventHandler;
import me.zero.alpine.fork.listener.Listener;
import net.minecraft.entity.Entity;
import net.minecraft.entity.monster.EntitySlime;
import net.minecraft.entity.passive.EntityDonkey;
import net.minecraft.entity.passive.EntityLlama;
import net.minecraft.util.text.TextComponentString;

public class EntityLoggerModule extends Module {
    public static final ToggleableSetting donkey = new ToggleableSetting("Donkey", null, true);
    public static final ToggleableSetting llama = new ToggleableSetting("Llama", null, true);
    public static final ToggleableSetting slime = new ToggleableSetting("Slime", null, false);

    public EntityLoggerModule() {
        super("EntityLogger", "Logs entities and sends it to you chat.", ModuleType.World, 0);
        addOption(donkey);
        addOption(llama);
        addOption(slime);
        endOption();
    }

    @EventHandler
    private Listener<EntityAddedEvent> entityAddedEventListener = new Listener<>(event -> {
        if(mc.player == null) return;

        Entity entity = event.getEntity();
        if((entity instanceof EntityLlama) && llama.isEnabled()) {
            EntityLlama llama = (EntityLlama)entity;
            mc.player.sendMessage(new TextComponentString(ChatColor.prefix_parse('&', "&e[EntityLogger] &bFound llama at [" + (int)llama.posX + ", " + (int)llama.posZ + "]")));
        } else if((entity instanceof EntityDonkey) && donkey.isEnabled()) {
            EntityDonkey donkey = (EntityDonkey)entity;
            mc.player.sendMessage(new TextComponentString(ChatColor.prefix_parse('&', "&e[EntityLogger] &bFound donkey at [" + (int)donkey.posX + ", " + (int)donkey.posZ + "]")));
        } else if((entity instanceof EntitySlime) && slime.isEnabled()) {
            EntitySlime entitySlime = (EntitySlime)entity;
            mc.player.sendMessage(new TextComponentString(ChatColor.prefix_parse('&', "&e[EntityLogger] &bFound slime at [" + (int)entitySlime.posX + ", " + (int)entitySlime.posZ + "]")));
        }
    });
}