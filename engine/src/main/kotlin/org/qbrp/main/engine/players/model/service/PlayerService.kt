package org.qbrp.main.engine.players.model.service

import org.qbrp.main.core.events.emit
import org.qbrp.main.engine.physics.player.PlayerBodyManager
import org.qbrp.main.engine.players.model.ServerPlayerObject
import org.qbrp.main.engine.players.model.PlayerComponentBehaviour
import org.qbrp.main.engine.players.model.PlayerInstantiateEvent
import org.qbrp.main.engine.spectators.startSpectating

interface PlayerService {
    fun instantiatePlayer(player: ServerPlayerObject)
    fun destroyPlayer(player: ServerPlayerObject)
}

class ServerPlayerService(
    private val playerSaver: PlayerSaver,
    private val playerStorage: ServerPlayerStorage,
    private val worldPlayerBodyManager: PlayerBodyManager
) : PlayerService {
    override fun instantiatePlayer(player: ServerPlayerObject) {
        if (!playerStorage.contains(player)) playerStorage.add(player)
        player.enableAll()
        //player.startSpectating()
        worldPlayerBodyManager.addPlayerCharacter(player)
        PlayerInstantiateEvent(player).emit()
        playerSaver.saveObject(player)
    }

    override fun destroyPlayer(player: ServerPlayerObject) {
        player
            .filterIsInstance<PlayerComponentBehaviour>()
            .forEach { it.onLeave() }
        playerSaver.saveObject(player)
        playerStorage.remove(player.id)
        worldPlayerBodyManager.removePlayerCharacter(player)
        player.removeAll()
    }
}