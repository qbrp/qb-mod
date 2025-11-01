package org.qbrp.main.deprecated

import org.koin.core.component.get
import org.qbrp.main.core.components.ComponentRegistryInitializationEvent
import org.qbrp.main.core.fabricmc.commands.CommandsAPI
import org.qbrp.main.core.modules.EngineModule
import org.qbrp.main.deprecated.acquaintances.NameCommand

//@Autoload(Priorities.ADDON)
class Acquaintances(): EngineModule("acquaintances") {

    override fun onLoad() {
        get<CommandsAPI>().add(NameCommand())
        ComponentRegistryInitializationEvent.EVENT.register {
            //it.register(NamesPerception::class.java)
            //Deps.PLAYER_PREFAB.components += PrefabField { NamesPerception() }
        }
    }
}