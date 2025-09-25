package org.qbrp.client.render.text.render.impl

import net.minecraft.client.font.TextRenderer
import net.minecraft.client.render.VertexConsumerProvider
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.text.OrderedText
import org.qbrp.client.render.dataformats.Vec2
import org.qbrp.client.render.text.entity.McOrderedText
import org.qbrp.client.render.text.render.impl.MinecraftTextRenderer.Companion.TEXT_BRIGHTNESS
import org.qbrp.client.render.text.render.model.TextComponentRenderer
import org.qbrp.client.render.ui.components.layout.Size
import org.qbrp.main.engine.Colors

open class OrderedTextRenderer(private val mcTextRenderer: TextRenderer): TextComponentRenderer<McOrderedText> {
    override fun getWidth(text: McOrderedText): Float {
        return mcTextRenderer.getWidth(text.text).toFloat()
    }

    override fun getHeight(text: McOrderedText): Float {
        return mcTextRenderer.fontHeight.toFloat()
    }

    override fun render(
        matrices: MatrixStack,
        vertexConsumers: VertexConsumerProvider,
        text: McOrderedText,
        pos: Vec2,
        z: Float
    ): Int {
        val mcText = text.text
        return renderTextAndApplyTransforms(mcTextRenderer, matrices, vertexConsumers, Colors.WHITE, false, mcText, pos, z)
    }
}

internal fun renderTextAndApplyTransforms(
    textRenderer: TextRenderer,
    matrices: MatrixStack,
    vertexConsumers: VertexConsumerProvider,
    color: Int,
    shadow: Boolean,
    text: OrderedText,
    pos: Vec2,
    z: Float
): Int {
    matrices.push()
    matrices.translate(0.0, 0.0, z.toDouble())
    val i = textRenderer.draw(
        text,
        pos.x,
        pos.y,
        color,
        shadow,
        matrices.peek().getPositionMatrix(),
        vertexConsumers,
        TextRenderer.TextLayerType.NORMAL,
        0,
        TEXT_BRIGHTNESS
    )
    matrices.pop()
    return i
}