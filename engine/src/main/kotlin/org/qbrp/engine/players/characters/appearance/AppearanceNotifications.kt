package org.qbrp.engine.players.characters.appearance

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonProperty
import net.minecraft.server.world.ServerWorld
import net.minecraft.world.GameMode
import org.qbrp.core.game.model.tick.Tick
import org.qbrp.core.mc.player.PlayerManager
import org.qbrp.core.mc.player.model.PlayerBehaviour
import org.qbrp.engine.characters.model.CharacterData
import org.qbrp.engine.players.characters.Character
import org.qbrp.system.utils.world.getPlayersSessionsInRadius

class AppearanceNotifications @JsonCreator constructor(
    @JsonProperty("readStates")
    var readStates: MutableList<ReadState> = mutableListOf()
) : PlayerBehaviour(), Tick<ServerWorld> {
    companion object {
        private const val TIMER_DELAY = 4 * 20
        private const val CHECK_TICKS = TIMER_DELAY / TICK_RATE
        private const val RADIUS = 8.0
        private const val WAIT_TIME = 5 * 60
    }

    // Храним список состояний с ID персонажа внутри
    @JsonIgnore private var tickCount = 0

    fun read(characterData: CharacterData) {
        readStates.find { it.characterId == characterData.id }?.let {
            readStates.remove(it)
            readStates.add(ReadState.read(characterData))
        }
    }

    override fun tick(world: ServerWorld) {
        if (++tickCount <= CHECK_TICKS) return
        tickCount = 0

        val now = System.currentTimeMillis()
        PlayerManager.playersList.toList()
            .getPlayersSessionsInRadius(player.entity, RADIUS)
            .asSequence()
            .filter { it.entity.interactionManager.gameMode != GameMode.SPECTATOR && player.entity.canSee(it.entity) }
            .mapNotNull { it.state.getComponent<Character>()?.data }
            .forEach { character ->
                // Ищем существующее состояние или создаём новое
                val state = readStates.find { it.characterId == character.id }
                    ?: ReadState.blank(character).also { readStates.add(it) }

                if (now < state.time + WAIT_TIME) return@forEach

                val pending = mutableListOf<String>().apply {
                    if (!state.isDescriptionRead(character)) add("описание внешности ${character.formattedName}")
                    if (!state.isLookRead(character)) add("описание образа ${character.formattedName}")
                }

                if (pending.isNotEmpty()) {
                    state.time = now
                    sendMessage(
                        "<red>⬥ <gold>Вы не прочитали ${pending.joinToString(", ")}"
                    )
                }
            }
    }

    data class ReadState(
        val characterId: Int,
        val descHash: Int,
        val lookHash: Int,
        var time: Long
    ) {
        fun isDescriptionRead(c: CharacterData) = descHash == c.appearance.description.hashCode()
        fun isLookRead(c: CharacterData) = lookHash == c.appearance.look.description.hashCode()

        companion object {
            fun read(c: CharacterData) = ReadState(
                c.id,
                c.appearance.description.hashCode(),
                c.appearance.look.description.hashCode(),
                System.currentTimeMillis()
            )
            fun blank(c: CharacterData) = ReadState(c.id, 0, 0, System.currentTimeMillis())
        }
    }
}
