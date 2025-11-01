package org.qbrp.client.render.props

import net.fabricmc.api.EnvType
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents
import org.koin.core.component.get
import org.koin.core.module.Module
import org.koin.core.parameter.parametersOf
import org.koin.dsl.module
import org.qbrp.client.engine.items.ClientItemObject
import org.qbrp.client.engine.items.PropInterpolation
import org.qbrp.client.render.debug.DebugRenderContext
import org.qbrp.main.core.components.extensions.get
import org.qbrp.main.core.modules.Autoload
import org.qbrp.main.core.modules.EngineModule
import org.qbrp.main.engine.items.service.ItemStorage
import org.qbrp.main.engine.props.components.propShape
import kotlin.math.cos
import kotlin.math.sin

@Autoload(env = EnvType.CLIENT)
class PropRenderModule : EngineModule("prop-render") {
    override fun getKoinModule(): Module = module {
        single { DebugPropRenderer() }
    }

    override fun onEnable() {
        val storage = get<ItemStorage<ClientItemObject>>()
        val renderer = get<DebugPropRenderer>()
        WorldRenderEvents.AFTER_ENTITIES.register { ctx ->
            val snapshot = storage.toList()
            for (item in snapshot) {
                val prop = item.state.get<PropInterpolation>() ?: continue
                val shape = item.propShape ?: continue
                renderer.render(get<DebugRenderContext> { parametersOf(ctx) }, prop, shape, ctx)
            }
        }
    }
}