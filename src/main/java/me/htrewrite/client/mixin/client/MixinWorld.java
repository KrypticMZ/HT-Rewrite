package me.htrewrite.client.mixin.client;

import me.htrewrite.client.HTRewrite;
import me.htrewrite.client.event.custom.world.EntityAddedEvent;
import me.htrewrite.client.event.custom.world.WorldRainStrengthEvent;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(World.class)
public class MixinWorld {
    @Shadow
    public float prevRainingStrength;

    @Shadow
    public float rainingStrength;

    @Inject(method = "onEntityAdded", at = @At("HEAD"), cancellable = true)
    public void onEntityAdded(Entity entity, CallbackInfo callbackInfo) {
        EntityAddedEvent entityAddedEvent = new EntityAddedEvent(entity);
        HTRewrite.EVENT_BUS.post(entityAddedEvent);
        if(entityAddedEvent.isCancelled())
            callbackInfo.cancel();
    }

    @Overwrite
    public float getRainStrength(float delta) {
        float retValue = prevRainingStrength + (rainingStrength - prevRainingStrength) * delta;

        WorldRainStrengthEvent event = new WorldRainStrengthEvent(delta, retValue);

        HTRewrite.EVENT_BUS.post(event);
        if(event.isCancelled())
            return 0f;
        return event.strength;
    }
}