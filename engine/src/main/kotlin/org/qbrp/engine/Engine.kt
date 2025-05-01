package org.qbrp.engine

import org.koin.core.component.KoinComponent
import org.qbrp.core.EngineInitializedEvent
import org.qbrp.core.resources.ServerResources
import org.qbrp.engine.chat.ChatModule
import org.qbrp.engine.damage.DamageControllerModule
import org.qbrp.engine.spectators.SpectatorsModule
import org.qbrp.engine.time.TimeModule
import org.qbrp.system.modules.ModuleAPI
import org.qbrp.system.modules.ModuleManager
import org.qbrp.system.modules.QbModule
import org.qbrp.system.utils.log.Loggers

class Engine: KoinComponent {
    companion object {
        val moduleManager: ModuleManager = ModuleManager()

        inline fun <reified T : ModuleAPI> getAPI(): T? {
            return moduleManager.getAPI<T>()
        }

        inline fun <reified T : ModuleAPI> isApiAvailable(): Boolean {
            return moduleManager.isApiAvailable<T>()
        }

        fun isModuleEnabled(name: String): Boolean {
            return moduleManager.isModuleEnabled(name)
        }

        val globalLogger = Loggers.get("engine")
    }

    fun initialize() {
        moduleManager.initialize()
        EngineInitializedEvent.EVENT.invoker().event()
    }
}
