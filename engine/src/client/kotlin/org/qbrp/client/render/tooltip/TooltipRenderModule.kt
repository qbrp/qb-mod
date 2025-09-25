package org.qbrp.client.render.tooltip

import net.fabricmc.api.EnvType
import org.koin.core.component.get
import org.koin.core.component.inject
import org.koin.core.module.Module
import org.koin.dsl.module
import org.qbrp.main.mechanics.tooltip.component.NameTooltipComponent
import org.qbrp.client.render.tooltip.components.NameTooltipRenderer
import org.qbrp.main.mechanics.tooltip.component.PlainTextTooltipComponent
import org.qbrp.client.render.tooltip.components.PlaintTextTooltipRenderer
import org.qbrp.main.mechanics.tooltip.component.ProgressBarTooltipComponent
import org.qbrp.client.render.tooltip.components.ProgressBarTooltipRenderer
import org.qbrp.client.render.tooltip.render.MainTooltipRenderer
import org.qbrp.client.render.tooltip.render.TooltipBackgroundRenderer
import org.qbrp.client.render.tooltip.render.TooltipBackgroundRendererImpl
import org.qbrp.client.render.tooltip.render.TooltipRendererRegistry
import org.qbrp.main.core.modules.Autoload
import org.qbrp.main.core.modules.EngineModule
import org.qbrp.main.core.modules.Priorities

@Autoload(Priorities.HIGHEST, env = EnvType.CLIENT)
class TooltipRenderModule: EngineModule("tooltip-render") {
    override fun getKoinModule(): Module = module {
        single { TooltipRendererRegistry() }
        single<MainTooltipRenderer> { get<TooltipRendererRegistry>() }
        single<TooltipBackgroundRenderer> { TooltipBackgroundRendererImpl() }
    }

    private val tooltipRendererRegistry: TooltipRendererRegistry by inject()

    override fun onEnable() {
        tooltipRendererRegistry.apply {
            register(PlainTextTooltipComponent::class, PlaintTextTooltipRenderer(
                get(),
                get(),
                get())
            )
            register(NameTooltipComponent::class, NameTooltipRenderer(get(), 2f, 1f))
            register(ProgressBarTooltipComponent::class, ProgressBarTooltipRenderer(get()))
        }
    }
}