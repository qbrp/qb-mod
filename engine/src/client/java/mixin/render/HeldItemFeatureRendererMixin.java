package mixin.render;

import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.feature.HeldItemFeatureRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import org.qbrp.client.engine.players.mixin.HeldItemRendererMixinAccess;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(HeldItemFeatureRenderer.class)
public abstract class HeldItemFeatureRendererMixin<T extends LivingEntity, M> {

    @ModifyVariable(
            method = "render(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;ILnet/minecraft/entity/LivingEntity;FFFFFF)V",
            at = @At(value = "STORE", ordinal = 0),
            ordinal = 0
    )
    private ItemStack replaceFirstStack(ItemStack original, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, T livingEntity, float f, float g, float h, float j, float k, float l
    ) {
        if (!(livingEntity instanceof AbstractClientPlayerEntity)) return original;
        return HeldItemRendererMixinAccess.INSTANCE.getPlayerRightHandItem((AbstractClientPlayerEntity) livingEntity);
    }

    @ModifyVariable(
            method = "render(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;ILnet/minecraft/entity/LivingEntity;FFFFFF)V",
            at = @At(value = "STORE", ordinal = 0),
            ordinal = 1
    )
    private ItemStack replaceSecondStack(ItemStack original, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, T livingEntity, float f, float g, float h, float j, float k, float l
    ) {
        if (!(livingEntity instanceof AbstractClientPlayerEntity)) return original;
        return HeldItemRendererMixinAccess.INSTANCE.getPlayerLeftHandItem((AbstractClientPlayerEntity) livingEntity);
    }
}