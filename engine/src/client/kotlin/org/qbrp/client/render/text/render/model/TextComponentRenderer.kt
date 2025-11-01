package org.qbrp.client.render.text.render.model
import net.minecraft.client.render.VertexConsumerProvider
import net.minecraft.client.util.math.MatrixStack
import org.qbrp.client.render.dataformats.Vec2
import org.qbrp.client.render.screen.DefaultZIndexes
import org.qbrp.client.render.ui.components.layout.Size
import org.qbrp.main.core.fabricmc.text.RenderableText

typealias TextRenderer = TextComponentRenderer<RenderableText>

interface TextComponentRenderer<C: RenderableText> {
    fun getWidth(text: C): Float
    fun getHeight(text: C): Float
    fun measure(text: C): Size {
        return Size(getHeight(text), getWidth(text))
    }
    fun render(matrices: MatrixStack, vertexConsumers: VertexConsumerProvider, text: C, pos: Vec2, z: Float = DefaultZIndexes.TEXT.value): Int
}