package mixin.render;

import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.item.HeldItemRenderer;
import net.minecraft.item.ItemStack;
import org.qbrp.client.engine.players.mixin.HeldItemRendererMixinAccess;
import org.qbrp.client.render.ActionBarMixinRenderData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(HeldItemRenderer.class)
public class ItemInHandRendererMixin {

    @Redirect(
            method = "updateHeldItems",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/network/ClientPlayerEntity;getMainHandStack()Lnet/minecraft/item/ItemStack;"
            )
    )
    private ItemStack qbrp$redirectGetMainHandStack(ClientPlayerEntity player) {
        if (ActionBarMixinRenderData.INSTANCE.getOVERRIDE_HAND_ITEMS()) {
            return HeldItemRendererMixinAccess.INSTANCE.getClientPlayerRightHandItem();
        } else {
            return player.getMainHandStack();
        }
    }

    @Redirect(
            method = "updateHeldItems",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/network/ClientPlayerEntity;getOffHandStack()Lnet/minecraft/item/ItemStack;"
            )
    )
    private ItemStack qbrp$redirectGetOffHandStack(ClientPlayerEntity player) {
        if (ActionBarMixinRenderData.INSTANCE.getOVERRIDE_HAND_ITEMS()) {
            return HeldItemRendererMixinAccess.INSTANCE.getClientPlayerLeftHandItem();
        } else {
            return player.getOffHandStack();
        }
    }
}
