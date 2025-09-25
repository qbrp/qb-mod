package org.qbrp.client.render.text.render.model

import org.qbrp.main.core.mc.text.RenderableText
import org.qbrp.main.engine.text.components.TextComponent
import kotlin.reflect.KClass

interface TextComponentRendererRegistry {
    fun <C: RenderableText> register(kClass: KClass<out C>, renderer: TextComponentRenderer<C>)
}