package org.qbrp.engine.client.render.hud
import icyllis.modernui.core.Core
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
import net.minecraft.client.MinecraftClient

class TimeHud {

    object HUDUpdater {
        init {
            // Регистрируем обработчик тика; в Fabric API нет отдельного RenderTickEvent,
            // поэтому используем END_CLIENT_TICK, который вызывается в основном (UI) потоке.
            ClientTickEvents.END_CLIENT_TICK.register { client ->
                Core.executeOnUiThread {
                    // Получаем игрока; если его нет – ничего не обновляем.
                    val player = MinecraftClient.getInstance().player ?: return@executeOnUiThread
                }
            }
        }

        fun shouldClose(): Boolean = false

        fun shouldBlurBackground(): Boolean = false
    }

}