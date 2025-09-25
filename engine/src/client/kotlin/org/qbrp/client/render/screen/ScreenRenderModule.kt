package org.qbrp.client.render.screen

import net.fabricmc.api.EnvType
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback
import org.koin.core.component.inject
import org.koin.core.module.Module
import org.koin.dsl.module
import org.qbrp.client.render.screen.context.DrawContextAdapterFactory
import org.qbrp.client.render.screen.context.DrawContextAdapterFactoryImpl
import org.qbrp.client.render.screen.event.ScreenRenderPipeline
import org.qbrp.main.core.events.emit
import org.qbrp.main.core.mc.server.context.withCoreContext
import org.qbrp.main.core.modules.Autoload
import org.qbrp.main.core.modules.EngineModule

@Autoload(env = EnvType.CLIENT)
class ScreenRenderModule: EngineModule("screen-render") {
    override fun getKoinModule(): Module = module {
        single<DrawContextAdapterFactory> { DrawContextAdapterFactoryImpl(get()) }
    }

    override fun onEnable() = withCoreContext {
        val factory by inject<DrawContextAdapterFactory>()
        HudRenderCallback.EVENT.register { context, delta ->
            val ctx = factory.adapt(context, delta)
            ScreenRenderPipeline(ctx).emit()
            ctx.draw()
        }
    }
}