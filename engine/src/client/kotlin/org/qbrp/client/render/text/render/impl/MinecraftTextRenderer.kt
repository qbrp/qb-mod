package org.qbrp.client.render.text.render.impl

import net.minecraft.client.font.TextRenderer
import net.minecraft.client.render.VertexConsumerProvider
import net.minecraft.client.util.math.MatrixStack
import org.qbrp.client.render.dataformats.Vec2
import org.qbrp.client.render.text.render.model.TextComponentRenderer
import org.qbrp.main.core.fabricmc.text.MinecraftTextMapper
import org.qbrp.main.engine.text.components.TextComponent

open class MinecraftTextRenderer<C: TextComponent>(private val mcTextRenderer: TextRenderer): TextComponentRenderer<C> {
    override fun getWidth(text: C): Float {
        val mcText = MinecraftTextMapper.fromEngineText(text)
        return mcTextRenderer.getWidth(mcText).toFloat()
    }

    override fun getHeight(text: C): Float {
        return mcTextRenderer.fontHeight.toFloat()
    }

    override fun render(
        matrices: MatrixStack,
        vertexConsumers: VertexConsumerProvider,
        text: C,
        pos: Vec2,
        z: Float
    ): Int {
        val mcText = MinecraftTextMapper.fromEngineText(text)
        val color = text.style.color.hex
        val shadow = text.style.shadow
        return renderTextAndApplyTransforms(
            mcTextRenderer,
            matrices,
            vertexConsumers,
            color,
            shadow,
            mcText.asOrderedText(),
            pos,
            z
        )
    }

    companion object {
        const val TEXT_BRIGHTNESS = 15728880
    }
}