package org.qbrp.main.mechanics.characters.acquaintances

import org.qbrp.main.engine.Engine
import org.qbrp.main.engine.chat.addons.placeholders.PlaceholdersAPI
import org.qbrp.main.core.modules.GameModule
import org.koin.core.component.get
import org.qbrp.main.core.components.ComponentRegistryInitializationEvent
import org.qbrp.main.core.mc.commands.CommandsAPI

//@Autoload(Priorities.ADDON)
class Acquaintances(): GameModule("acquaintances") {
    init {
        dependsOn { Engine.isApiAvailable<PlaceholdersAPI>() }
        dependsOn { Engine.isModuleEnabled("characters") }
    }

    override fun onLoad() {
        get<CommandsAPI>().add(NameCommand())
        ComponentRegistryInitializationEvent.EVENT.register {
            //it.register(NamesPerception::class.java)
            //Deps.PLAYER_PREFAB.components += PrefabField { NamesPerception() }
        }
    }
}