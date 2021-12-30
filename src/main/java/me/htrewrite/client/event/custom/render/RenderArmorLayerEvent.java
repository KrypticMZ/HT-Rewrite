package me.htrewrite.client.event.custom.render;

import me.htrewrite.client.event.custom.CustomEvent;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraftforge.fml.common.eventhandler.Event;

public class RenderArmorLayerEvent extends CustomEvent {

    public EntityLivingBase entity;

    public RenderArmorLayerEvent(EntityLivingBase entity, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale, EntityEquipmentSlot slotIn) {
        super();

        this.entity = entity;
    }

}
