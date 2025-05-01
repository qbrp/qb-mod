package org.qbrp.engine.players.characters.appearance

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonProperty
import net.minecraft.server.world.ServerWorld
import net.minecraft.world.GameMode
import org.qbrp.core.game.model.tick.Tick
import org.qbrp.core.mc.player.PlayerManager
import org.qbrp.core.mc.player.PlayerObject
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
        private const val WAIT_TIME = 3 * 60 * 1000
    }

    // Храним список состояний с ID персонажа внутри
    @JsonIgnore private var tickCount = 0

    fun read(player: PlayerObject) {
        val character = player.getComponent<Character>() ?: return
        readStates.find { it.characterId == character.data.id }?.let {
            readStates.remove(it)
            readStates.add(ReadState.read(character.data.id, player.getComponent<Appearance>()!!))
        }
    }

    override fun tick(world: ServerWorld) {
        if (++tickCount <= CHECK_TICKS) return
        tickCount = 0

        val notification = mutableListOf<String>()
        val now = System.currentTimeMillis()
        PlayerManager.playersList.toList()
            .getPlayersSessionsInRadius(player.entity, RADIUS)
            .asSequence()
            .filter { it.entity.interactionManager.gameMode != GameMode.SPECTATOR && player.entity.canSee(it.entity) }
            .forEach { player ->
                val character = player.state.getComponent<Character>()?.data ?: return@forEach
                val appearance = player.state.getComponentOrThrow<Appearance>()
                // Ищем существующее состояние или создаём новое
                val state = readStates.find { it.characterId == character.id }
                    ?: ReadState.blank(character).also { readStates.add(it) }

                val pending = mutableListOf<String>().apply {
                    if (!state.isDescriptionRead(appearance)) add("описание внешности ${character.formattedName}")
                    else if (!state.isLookRead(appearance)) add("описание образа ${character.formattedName}")
                }

                val notifyHash = pending.joinToString("\n").hashCode()

                if (pending.isNotEmpty()) {
                    if (state.notifyHash == notifyHash) {
                        if (now < state.time + WAIT_TIME) return@forEach
                    }
                    notification.addAll(pending)
                    state.time = now
                    state.notifyHash = notifyHash
                }
            }
        if (notification.isNotEmpty()) {
            sendMessage(
                "<red>⬥ <gold>Вы не прочитали ${notification.joinToString(", ")}</gold>"
            )
        }
    }

    data class ReadState(
        val characterId: Int,
        val descHash: Int,
        val lookHash: Int,
        var time: Long
    ) {
        var notifyHash: Int = 0
        fun isDescriptionRead(a: Appearance): Boolean {
            println("Проверка описания: ${descHash} == ${a.description.hashCode()}")
            return descHash == a.description.hashCode()
        }
        fun isLookRead(a: Appearance): Boolean {
            println("Проверка образа: ${lookHash} == ${a.look?.description.hashCode()}")
            return lookHash == a.look?.description.hashCode()
        }

        companion object {
            fun read(id: Int, appearance: Appearance) = ReadState(
                id,
                appearance.description.hashCode(),
                appearance.look?.description.hashCode(),
                System.currentTimeMillis() - WAIT_TIME
            )
            fun blank(c: CharacterData) = ReadState(c.id, 0, 0, System.currentTimeMillis() - WAIT_TIME)
        }
    }
}
