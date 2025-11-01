package org.qbrp.client.render.ui.utilview

import net.fabricmc.api.EnvType
import org.qbrp.client.core.mc.connection.ConnectEvent
import org.qbrp.client.render.ui.UiContext
import org.qbrp.client.render.ui.components.transform.Pivot
import org.qbrp.client.render.ui.dsl.toolstrip
import org.qbrp.client.render.ui.dsl.viewOf
import org.qbrp.client.render.ui.withUiContext
import org.qbrp.main.core.events.on
import org.qbrp.main.core.modules.Autoload
import org.qbrp.main.core.modules.EngineModule
import org.qbrp.main.engine.Colors

@Autoload(env = EnvType.CLIENT)
class WidgetMenuModule : EngineModule("widgets-menu") {
    override fun onEnable(): Unit {
        on<ConnectEvent>() {
            withUiContext { open() }
        }
    }

    context(ui: UiContext)
    private fun open() {
        ui.viewManager.addView(viewOf {
            anchors(0.0f, 0.0f, 1.0f, 0.0f, Pivot(0.5f, 0.5f))
            zIndex(1000)
            background(Colors.BACKGROUND_DARK)
            toolstrip {
                eternal()
                item {
                    label("Инвентарь")
                    item {
                        label("Творческий инвентарь")
                    }
                    item {
                        label("Руки")
                    }
                }
                item {
                    label("Утилиты")
                    item {
                        label("Игровое меню")
                    }
                }
            }
        })
    }

}