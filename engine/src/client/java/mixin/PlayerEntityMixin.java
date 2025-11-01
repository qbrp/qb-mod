package mixin;

import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Arm;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerEntity.class)
public class PlayerEntityMixin extends LivingEntity {
    protected PlayerEntityMixin(EntityType<? extends LivingEntity> entityType, World world) {
        super(entityType, world);
    }

    @Inject(
            method = "tickMovement",
            at = @At("HEAD"),
            cancellable = true
    )
    private void appendTickMovement(CallbackInfo ci) {
        if ((Object)this instanceof ClientPlayerEntity) {
            ci.cancel();
            super.tickMovement();
        }
    }

    @Shadow
    protected void initDataTracker() {}


    @Shadow
    public Iterable<ItemStack> getArmorItems() {
        return null;
    }

    @Shadow
    public ItemStack getEquippedStack(EquipmentSlot slot) {
        return null;
    }

    @Shadow
    public void equipStack(EquipmentSlot slot, ItemStack stack) {

    }

    @Shadow
    public Arm getMainArm() {
        return null;
    }
}
