package org.qbrp.client.render.text.render.impl

import net.minecraft.client.render.VertexConsumerProvider
import net.minecraft.client.util.math.MatrixStack
import org.qbrp.client.render.dataformats.Vec2
import org.qbrp.client.render.text.render.model.TextComponentRenderer
import org.qbrp.client.render.text.render.model.TextComponentRendererRegistry
import org.qbrp.client.render.text.render.model.TextRenderer
import org.qbrp.main.core.mc.text.RenderableText
import org.qbrp.main.engine.text.components.TextComponent
import kotlin.reflect.KClass

class MainTextRenderer(val defaultRenderer: MinecraftTextRenderer<TextComponent>): TextRenderer, TextComponentRendererRegistry {
    private val renderers: MutableMap<KClass<out RenderableText>, TextComponentRenderer<*>> = mutableMapOf()

    override fun <C : RenderableText> register(kClass: KClass<out C>, renderer: TextComponentRenderer<C>) {
        renderers[kClass] = renderer
    }

    private fun <C: RenderableText> getRenderer(kClass: KClass<out C>): TextComponentRenderer<C> {
        return (renderers[kClass] ?: defaultRenderer) as TextComponentRenderer<C>
    }

    override fun getHeight(text: RenderableText): Float {
        return getRenderer(text::class).getHeight(text)
    }

    override fun getWidth(text: RenderableText): Float {
        return getRenderer(text::class).getWidth(text)
    }

    override fun render(matrices: MatrixStack, vertexConsumers: VertexConsumerProvider, text: RenderableText, pos: Vec2, z: Float): Int {
        //matrices.translate(0f, 0f, TEXT_Z_OFFSET)
        return getRenderer(text::class).render(matrices, vertexConsumers, text, pos, z)
    }

    companion object {
        const val TEXT_Z_OFFSET = 0.001
    }
}