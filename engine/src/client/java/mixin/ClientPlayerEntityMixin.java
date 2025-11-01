package mixin;

import com.mojang.authlib.GameProfile;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.DownloadingTerrainScreen;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.player.PlayerAbilities;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.registry.tag.FluidTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayerEntity.class)
public abstract class ClientPlayerEntityMixin extends PlayerEntity {

    @Shadow private boolean inSneakingPose;

    @Shadow @Final protected MinecraftClient client;

    public ClientPlayerEntityMixin(World world, BlockPos pos, float yaw, GameProfile gameProfile) {
        super(world, pos, yaw, gameProfile);
    }

    @Shadow protected abstract void updateNausea();

    @Shadow private int underwaterVisibilityTicks;

    @Inject(
            method = "tickMovement",
            at = @At(value = "HEAD"),
            cancellable = true
    )
    void tickMovement$qbrp(CallbackInfo ci) {
        ci.cancel();
        PlayerEntity entity = ((PlayerEntity)(Object)this);
        PlayerAbilities abilities = entity.getAbilities();

        if (!(this.client.currentScreen instanceof DownloadingTerrainScreen)) {
            this.updateNausea();
        }

        this.inSneakingPose = !abilities.flying
                && !entity.isSwimming()
                && wouldPoseNotCollide(EntityPose.CROUCHING)
                && (this.isSneaking() || !entity.isSleeping()
                && !this.wouldPoseNotCollide(EntityPose.STANDING));

        int i;
        if (this.isSubmergedIn(FluidTags.WATER)) {
            i = this.isSpectator() ? 10 : 1;
            this.underwaterVisibilityTicks = MathHelper.clamp(this.underwaterVisibilityTicks + i, 0, 600);
        } else if (this.underwaterVisibilityTicks > 0) {
            this.isSubmergedIn(FluidTags.WATER);
            this.underwaterVisibilityTicks = MathHelper.clamp(this.underwaterVisibilityTicks - 10, 0, 600);
        }
        this.updateTrackedPosition(this.getX(), this.getY(), this.getZ());
        super.tickMovement();
    }
}
