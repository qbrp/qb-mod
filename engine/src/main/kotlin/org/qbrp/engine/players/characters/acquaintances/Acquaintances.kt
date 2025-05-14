package org.qbrp.engine.players.characters.acquaintances

import org.qbrp.core.game.ComponentsRegistry
import org.qbrp.core.game.prefabs.PrefabField
import org.qbrp.core.mc.registry.CommandsRepository
import org.qbrp.engine.Engine
import org.qbrp.engine.chat.addons.placeholders.PlaceholdersAPI
import org.qbrp.system.modules.Autoload
import org.qbrp.system.modules.GameModule
import org.qbrp.system.modules.LoadPriority

@Autoload(LoadPriority.ADDON - 1)
class Acquaintances(): GameModule("acquaintances") {
    init {
        dependsOn { Engine.isApiAvailable<PlaceholdersAPI>() }
        dependsOn { Engine.isModuleEnabled("characters") }
    }

    override fun onLoad() {
        CommandsRepository.add(NameCommand())
    }

    override fun registerComponents(registry: ComponentsRegistry) {
        registry.register(NamesPerception::class.java)
        gameAPI.getPlayerPrefab().components += PrefabField { NamesPerception() }
    }

}