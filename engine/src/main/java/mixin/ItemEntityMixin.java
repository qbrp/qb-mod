package mixin;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;
import org.qbrp.main.engine.Engine;
import org.qbrp.main.engine.items.ItemsModule;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemEntity.class)
abstract class ItemEntityMixin extends Entity {

    public ItemEntityMixin(EntityType<?> type, World world) {
        super(type, world);
    }

    @Inject(method = "onPlayerCollision", at = @At("HEAD"), cancellable = true)
    private void qbrp$preventPickup(PlayerEntity player, CallbackInfo ci) {
        if (this.getWorld().isClient) return;
        ItemsModule items = Engine.INSTANCE.getModule("items");
        assert items != null;
        if (items.getItemPickupDisabled()) {
            ci.cancel();
        }
    }
}
