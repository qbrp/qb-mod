package org.qbrp.main.engine.players.nicknames

import kotlinx.serialization.Serializable
import org.qbrp.main.core.game.ComponentsRegistry
import org.qbrp.main.core.mc.player.service.PlayerBehaviour
import org.qbrp.main.core.mc.commands.CommandsModule
import org.qbrp.main.engine.players.characters.Character
import org.qbrp.main.core.modules.Autoload
import org.qbrp.main.core.modules.GameModule
import org.qbrp.main.core.utils.Deps
import org.koin.core.component.get
import org.qbrp.main.core.game.ComponentRegistryInitializationEvent
import org.qbrp.main.core.game.prefabs.PrefabField
import org.qbrp.main.core.mc.commands.CommandsAPI

@Autoload(5)
class NicknamesModule: GameModule("nicknames") {
    override fun onLoad() {
        get<CommandsAPI>().add(NicknameCommand())
        ComponentRegistryInitializationEvent.EVENT.register {
            Deps.PLAYER_PREFAB.components += PrefabField { NicknameManager() }
            it.register(NicknameManager::class.java)
        }
    }

    @Serializable
    class NicknameManager: PlayerBehaviour() {
        var customName: String? = null
        fun getDisplayName() = customName
            ?: getComponent<Character>()?.data?.formattedName
            ?: player.entity.name.string

        fun update(name: String) { customName = name }
        fun reset() { customName = null }
    }

}