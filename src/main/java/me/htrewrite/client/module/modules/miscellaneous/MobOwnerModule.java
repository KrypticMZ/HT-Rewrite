package me.htrewrite.client.module.modules.miscellaneous;

import me.htrewrite.client.event.custom.player.PlayerUpdateEvent;
import me.htrewrite.client.module.Module;
import me.htrewrite.client.module.ModuleType;
import me.htrewrite.salimports.util.PlayerUtil;
import me.zero.alpine.fork.listener.EventHandler;
import me.zero.alpine.fork.listener.Listener;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.AbstractHorse;
import net.minecraft.entity.passive.EntityTameable;
import net.minecraft.entity.player.EntityPlayer;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class MobOwnerModule extends Module {
    private Map<Entity, String> owners = new HashMap<>();
    private Map<Entity, UUID> toLookUp = new ConcurrentHashMap<>();
    private List<Entity> lookedUp = new ArrayList<>();

    public MobOwnerModule() {
        super("MobOwner", null, ModuleType.Miscellaneous, 0);
        endOption();
    }

    @Override
    public void onDisable() {
        super.onDisable();
        for (final Entity entity : mc.world.loadedEntityList) {
            if (!(entity instanceof EntityTameable)) {
                if (!(entity instanceof AbstractHorse)) {
                    continue;
                }
            }
            try {
                entity.setAlwaysRenderNameTag(false);
            }
            catch (Exception ex) {}
        }
    }

    @EventHandler
    private Listener<PlayerUpdateEvent> onPlayerUpdate = new Listener<>(event -> {
        if(PlayerUtil.timer.passed(5000)) {
            for (final Map.Entry<Entity, UUID> entry : toLookUp.entrySet()) {
                final Entity entity = entry.getKey();
                final UUID uuid = entry.getValue();
                if (uuid != null) {
                    final EntityPlayer owner = mc.world.getPlayerEntityByUUID(uuid);
                    if (owner == null) {
                        try {
                            final String name = PlayerUtil.getNameFromUUID(uuid);
                            if (name != null) {
                                this.owners.put(entity, name);
                                this.lookedUp.add(entity);
                            }
                        }
                        catch (Exception e) {
                            this.lookedUp.add(entity);
                            this.toLookUp.remove(entry);
                        }
                        PlayerUtil.timer.reset();
                        break;
                    }
                    this.owners.put(entity, owner.getName());
                    this.lookedUp.add(entity);
                }
                else {
                    this.lookedUp.add(entity);
                    this.toLookUp.remove(entry);
                }
            }
            for (final Entity entity2 : mc.world.getLoadedEntityList()) {
                if (!entity2.getAlwaysRenderNameTag()) {
                    if (entity2 instanceof EntityTameable) {
                        final EntityTameable tameableEntity = (EntityTameable)entity2;
                        if (!tameableEntity.isTamed() || tameableEntity.getOwnerId() == null) {
                            continue;
                        }
                        if (this.owners.get(tameableEntity) != null) {
                            tameableEntity.setAlwaysRenderNameTag(true);
                            tameableEntity.setCustomNameTag((String)this.owners.get(tameableEntity));
                        }
                        else {
                            if (this.lookedUp.contains(entity2)) {
                                continue;
                            }
                            this.toLookUp.put((Entity)tameableEntity, tameableEntity.getOwnerId());
                        }
                    }
                    else {
                        if (!(entity2 instanceof AbstractHorse)) {
                            continue;
                        }
                        final AbstractHorse tameableEntity2 = (AbstractHorse)entity2;
                        if (!tameableEntity2.isTame() || tameableEntity2.getOwnerUniqueId() == null) {
                            continue;
                        }
                        if (this.owners.get(tameableEntity2) != null) {
                            tameableEntity2.setAlwaysRenderNameTag(true);
                            tameableEntity2.setCustomNameTag((String)this.owners.get(tameableEntity2));
                        }
                        else {
                            if (this.lookedUp.contains(entity2)) {
                                continue;
                            }
                            this.toLookUp.put((Entity)tameableEntity2, tameableEntity2.getOwnerUniqueId());
                        }
                    }
                }
            }
        }
    });
}