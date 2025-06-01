package org.qbrp.deprecated.resources.data.config

import net.fabricmc.fabric.api.event.Event
import net.fabricmc.fabric.api.event.EventFactory
import net.minecraft.util.ActionResult

fun interface ConfigInitializationCallback {
    fun onConfigUpdated(config: ServerConfigData)

    companion object {
        val EVENT: Event<ConfigInitializationCallback> = EventFactory.createArrayBacked(
            ConfigInitializationCallback::class.java,
            { listeners: Array<out ConfigInitializationCallback> ->
                ConfigInitializationCallback { config ->
                    for (listener in listeners) {
                        listener.onConfigUpdated(config)
                    }
                    ActionResult.SUCCESS
                }
            }
        )
    }
}