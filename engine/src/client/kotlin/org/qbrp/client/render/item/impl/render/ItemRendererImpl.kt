package org.qbrp.client.render.item.impl.render

import net.minecraft.client.render.DiffuseLighting
import net.minecraft.client.render.OverlayTexture
import net.minecraft.client.render.VertexConsumerProvider
import net.minecraft.client.render.model.BakedModel
import net.minecraft.client.render.model.json.ModelTransformationMode
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.entity.LivingEntity
import net.minecraft.item.ItemStack
import net.minecraft.world.World
import org.joml.Matrix4f
import org.qbrp.client.render.dataformats.Vec2
import org.qbrp.client.render.item.impl.mc.ItemStackAdapterWrapper
import org.qbrp.client.render.item.model.data.ItemDisplay
import org.qbrp.client.render.item.model.render.ItemRenderer
import org.qbrp.client.render.screen.context.Draw2dContext
import net.minecraft.client.render.item.ItemRenderer as MinecraftItemRenderer

class ItemRendererImpl(private val itemStackWrapper: ItemStackAdapterWrapper, private val itemRendererMc: MinecraftItemRenderer): ItemRenderer {
    override fun renderItem2d(
        ctx: Draw2dContext,
        item: ItemDisplay,
        pos: Vec2,
        scale: Float,
        z: Float
    ) {
        renderItemStack(
            ctx,
            ctx.matrices,
            ctx.vertexConsumers,
            pos.x,
            pos.y,
            z,
            itemStackWrapper.wrapItemStack(item),
            scale,
            null,
            null
        )
    }

    private fun renderItemStack(
        ctx: Draw2dContext,
        matrices: MatrixStack,
        vertexConsumers: VertexConsumerProvider,
        x: Float,
        y: Float,
        z: Float,
        stack: ItemStack,
        scale: Float,
        world: World?,
        entity: LivingEntity?
    ) {
        if (stack.isEmpty) return
        val bakedModel: BakedModel = itemRendererMc.getModel(stack, world, entity, 0)
        matrices.push()
        matrices.translate(
            (x + 8),
            (y + 8),
            (150 + (if (bakedModel.hasDepth()) z else 0f))
        )

        matrices.multiplyPositionMatrix(Matrix4f().scaling(1.0f, -1.0f, 1.0f))
        matrices.scale(scale, scale, scale)
        val isNotSideLit = !bakedModel.isSideLit
        if (isNotSideLit) { DiffuseLighting.disableGuiDepthLighting() }

        itemRendererMc
            .renderItem(
                stack,
                ModelTransformationMode.GUI,
                false,
                matrices,
                vertexConsumers,
                15728880,
                OverlayTexture.DEFAULT_UV,
                bakedModel
            )

        if (isNotSideLit) {
            DiffuseLighting.enableGuiDepthLighting()
        }

        ctx.matrices.pop()
    }
}