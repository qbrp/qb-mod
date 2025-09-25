package org.qbrp.client.render.text

import net.fabricmc.api.EnvType
import net.minecraft.client.MinecraftClient
import org.koin.core.component.inject
import org.koin.core.module.Module
import org.koin.dsl.module
import org.qbrp.client.render.text.entity.McOrderedText
import org.qbrp.client.render.text.render.impl.MainTextRenderer
import org.qbrp.client.render.text.render.impl.MinecraftTextRenderer
import org.qbrp.client.render.text.render.impl.OrderedTextRenderer
import org.qbrp.client.render.text.render.impl.TextSplitterImpl
import org.qbrp.client.render.text.render.model.TextComponentRendererRegistry
import org.qbrp.client.render.text.render.model.TextRenderer
import org.qbrp.client.render.text.render.model.TextSplitter
import org.qbrp.main.core.modules.Autoload
import org.qbrp.main.core.modules.Priorities
import org.qbrp.main.core.modules.EngineModule
import org.qbrp.main.engine.text.components.TextComponent

@Autoload(priority = Priorities.HIGHEST + 1, env = EnvType.CLIENT)
class TextRenderModule() : EngineModule("mcText-render") {
    private val textRenderer = MinecraftClient.getInstance().textRenderer
    override fun getKoinModule(): Module = module {
        single { MinecraftTextRenderer<TextComponent>(textRenderer) }
        single { MainTextRenderer(get()) }
        single<TextRenderer> { get<MainTextRenderer>() }
        single<TextComponentRendererRegistry> { get<MainTextRenderer>() }
        single<TextSplitter> { TextSplitterImpl(textRenderer) }
    }

    override fun onEnable() {
        val registry: TextComponentRendererRegistry by inject()
        registry.register(McOrderedText::class, OrderedTextRenderer(textRenderer))
    }
}