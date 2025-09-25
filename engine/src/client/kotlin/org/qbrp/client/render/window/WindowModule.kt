package org.qbrp.client.render.window

import net.fabricmc.api.EnvType
import net.minecraft.client.MinecraftClient
import org.koin.core.module.Module
import org.koin.dsl.module
import org.qbrp.client.render.screen.event.ScreenRenderPipeline
import org.qbrp.main.core.events.emit
import org.qbrp.main.core.events.on
import org.qbrp.main.core.events.withEventContext
import org.qbrp.main.core.modules.Autoload
import org.qbrp.main.core.modules.Priorities
import org.qbrp.main.core.modules.EngineModule

@Autoload(priority = Priorities.HIGHEST + 1, env = EnvType.CLIENT)
class WindowModule : EngineModule("window"), Window {
    override val widthDp: Double get() = MinecraftClient.getInstance().window.scaledWidth.toDouble()
    override val heightDp: Double get() = MinecraftClient.getInstance().window.scaledHeight.toDouble()
    override fun getKoinModule(): Module = module {
        single<Window> { this@WindowModule }
    }

    override fun onEnable() = withEventContext {
        on<ScreenRenderPipeline>() {
            handleResize(MinecraftClient.getInstance())
        }
    }

    private fun handleResize(client: MinecraftClient) = withEventContext {
        val currentW = widthDp
        val currentH = heightDp

        if (currentW != lastWindowWidth || currentH != lastWindowHeight) {
            WindowResizeEvent(client, this).emit()
        }
        lastWindowWidth = currentW
        lastWindowHeight = currentH
    }

    override val scale: Double get() = MinecraftClient.getInstance().window.scaleFactor
    private var lastWindowWidth: Double = -1.0
    private var lastWindowHeight: Double = -1.0
}
