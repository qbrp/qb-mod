package org.qbrp.client.render.debug

import net.fabricmc.api.EnvType
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext
import org.koin.core.module.Module
import org.koin.dsl.module
import org.qbrp.main.core.modules.Autoload
import org.qbrp.main.core.modules.EngineModule

@Autoload(env = EnvType.CLIENT)
class DebugRenderModule: EngineModule("debug-render") {
    override fun getKoinModule(): Module = module {
        factory { (worldRenderCtx: WorldRenderContext) -> DebugRenderContext(
            worldRenderCtx.matrixStack(),
            worldRenderCtx.consumers()!!,
            worldRenderCtx.camera()) }
    }
}