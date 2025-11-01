package org.qbrp.main.engine.spectators

import org.qbrp.deprecated.resources.data.config.ServerConfigData
import org.qbrp.main.core.components.DataComponent
import org.qbrp.main.core.components.extensions.add
import org.qbrp.main.core.components.extensions.get
import org.qbrp.main.core.components.extensions.has
import org.qbrp.main.core.components.extensions.remove
import org.qbrp.main.core.fabricmc.lockActionBarStatus
import org.qbrp.main.core.fabricmc.unlockActionBarStatus
import org.qbrp.main.engine.controls.entity.InteractionMode
import org.qbrp.main.engine.controls.system.modes.InteractionModeComponent
import org.qbrp.main.engine.controls.system.modes.setInteractionMode
import org.qbrp.main.engine.players.model.ServerPlayerObject
import org.qbrp.main.engine.players.model.service.PlayerList
import org.qbrp.main.core.util.PlayerId

enum class SpectatingStatus {
    START, ACKNOWLEDGED, STOP
}

class SpectatingMark(var status: SpectatingStatus) : DataComponent

fun ServerPlayerObject.startSpectating(): SpectatingMark {
    return add(SpectatingMark(SpectatingStatus.START))
}

fun ServerPlayerObject.stopSpectating() {
    get<SpectatingMark>()?.status = SpectatingStatus.STOP
}

val ServerPlayerObject.isSpectating get() = this.has<SpectatingMark>()

class SpectatingSystem(private val config: ServerConfigData) {
    private val cachedInteractionModes: MutableMap<PlayerId, InteractionMode> = mutableMapOf()

    fun update(players: PlayerList) {
        for (player in players) {
            val mark = player.get<SpectatingMark>() ?: continue

            when(mark.status) {
                SpectatingStatus.START -> {
                    player.setInteractionMode(InteractionMode.CROW)
                    player.lockActionBarStatus(config.spectators.formatTooltip)
                    mark.status = SpectatingStatus.ACKNOWLEDGED
                }

                SpectatingStatus.STOP -> {
                    player.unlockActionBarStatus()
                    player.setInteractionMode(cachedInteractionModes[player.playerId] ?: InteractionMode.DEFAULT)
                    player.remove<SpectatingMark>()
                }

                SpectatingStatus.ACKNOWLEDGED -> {
                    player.get<InteractionModeComponent>()?.apply {
                        if (dirty && mode != InteractionMode.CROW) {
                            player.unlockActionBarStatus()
                            player.remove<SpectatingMark>()
                        }
                    }
                }
            }
        }
    }
}