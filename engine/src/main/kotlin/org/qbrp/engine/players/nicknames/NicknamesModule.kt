package org.qbrp.engine.players.nicknames

import com.fasterxml.jackson.annotation.JsonIgnore
import org.qbrp.core.game.ComponentsRegistry
import org.qbrp.core.mc.player.model.PlayerBehaviour
import org.qbrp.core.mc.registry.CommandsRepository
import org.qbrp.engine.players.characters.Character
import org.qbrp.system.modules.Autoload
import org.qbrp.system.modules.GameModule
import org.qbrp.system.utils.format.Format.miniMessage

@Autoload(5)
class NicknamesModule: GameModule("nicknames") {
    override fun onLoad() {
        CommandsRepository.add(NicknameCommand())
        gameAPI.getPlayerPrefab().components += NicknameManager()
    }

    override fun registerComponents(registry: ComponentsRegistry) {
        registry.register(NicknameManager::class.java)
    }

    class NicknameManager: PlayerBehaviour() {
        var customName: String? = null
        @JsonIgnore fun getDisplayName() = customName
            ?: getComponent<Character>()?.data?.formattedName
            ?: player.entity.name.string

        fun update(name: String) { customName = name }
        fun reset() { customName = null }
    }

}