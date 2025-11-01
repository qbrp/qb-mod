package mixin;

import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public class LivingEntityMixin extends Entity {
    @Shadow protected int bodyTrackingIncrements;

    @Shadow protected double serverX;

    @Shadow protected double serverY;

    @Shadow protected double serverZ;

    @Shadow protected double serverYaw;

    @Shadow protected double serverPitch;

    @Shadow protected int headTrackingIncrements;

    @Shadow public float headYaw;

    @Shadow protected double serverHeadYaw;

    @Shadow
    protected void initDataTracker() {}

    @Shadow
    public void readCustomDataFromNbt(NbtCompound nbt) {}

    @Shadow
    public void writeCustomDataToNbt(NbtCompound nbt) {}

    public LivingEntityMixin(EntityType<?> type, World world) {
        super(type, world);
    }

    @Inject(
            method = "tickMovement",
            at = @At("HEAD"),
            cancellable = true
    )
    private void appendTickMovement(CallbackInfo ci) {
        if ((Object) this instanceof ClientPlayerEntity entity) {
            ci.cancel();
            if (this.bodyTrackingIncrements > 0) {
                double d = entity.getX() + (this.serverX - entity.getX()) / (double) this.bodyTrackingIncrements;
                double e = entity.getY() + (this.serverY - entity.getY()) / (double) this.bodyTrackingIncrements;
                double f = entity.getZ() + (this.serverZ - entity.getZ()) / (double) this.bodyTrackingIncrements;
                double g = MathHelper.wrapDegrees(this.serverYaw - (double) entity.getYaw());
                entity.setYaw(entity.getYaw() + (float) g / (float) this.bodyTrackingIncrements);
                entity.setPitch(entity.getPitch() + (float) (this.serverPitch - (double) entity.getPitch()) / (float) this.bodyTrackingIncrements);
                --this.bodyTrackingIncrements;
                entity.setPosition(d, e, f);
                setRotation(entity.getYaw(), entity.getPitch());
            } else if (!this.canMoveVoluntarily()) {
                this.setVelocity(this.getVelocity().multiply(0.98));
            }

            if (this.headTrackingIncrements > 0) {
                this.headYaw += (float) MathHelper.wrapDegrees(this.serverHeadYaw - (double) this.headYaw) / (float) this.headTrackingIncrements;
                --this.headTrackingIncrements;
            }

            if ((Object) this instanceof ClientPlayerEntity) {
                bodyTrackingIncrements = 0;
            }
        }
    }
}
