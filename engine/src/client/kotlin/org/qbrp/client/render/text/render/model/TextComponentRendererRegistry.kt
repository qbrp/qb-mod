package org.qbrp.client.render.text.render.model

import org.qbrp.main.core.fabricmc.text.RenderableText
import kotlin.reflect.KClass

interface TextComponentRendererRegistry {
    fun <C: RenderableText> register(kClass: KClass<out C>, renderer: TextComponentRenderer<C>)
}