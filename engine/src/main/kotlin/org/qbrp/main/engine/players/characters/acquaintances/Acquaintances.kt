package org.qbrp.main.engine.players.characters.acquaintances

import org.qbrp.main.core.game.ComponentsRegistry
import org.qbrp.main.core.game.prefabs.PrefabField
import org.qbrp.main.core.mc.commands.CommandsModule
import org.qbrp.main.engine.Engine
import org.qbrp.main.engine.chat.addons.placeholders.PlaceholdersAPI
import org.qbrp.main.core.modules.Autoload
import org.qbrp.main.core.modules.GameModule
import org.qbrp.main.core.modules.LoadPriority
import org.qbrp.main.core.utils.Deps
import org.koin.core.component.get
import org.qbrp.main.core.game.ComponentRegistryInitializationEvent
import org.qbrp.main.core.mc.commands.CommandsAPI

@Autoload(LoadPriority.ADDON - 1)
class Acquaintances(): GameModule("acquaintances") {
    init {
        dependsOn { Engine.isApiAvailable<PlaceholdersAPI>() }
        dependsOn { Engine.isModuleEnabled("characters") }
    }

    override fun onLoad() {
        get<CommandsAPI>().add(NameCommand())
        ComponentRegistryInitializationEvent.EVENT.register {
            it.register(NamesPerception::class.java)
            Deps.PLAYER_PREFAB.components += PrefabField { NamesPerception() }
        }
    }
}