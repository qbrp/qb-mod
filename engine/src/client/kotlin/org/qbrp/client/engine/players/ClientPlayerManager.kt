package org.qbrp.client.engine.players

import org.qbrp.main.core.components.extensions.require
import org.qbrp.main.core.util.PlayerId

interface ClientPlayerManager {
    val clientPlayer: ClientPlayerObject?
    val isInstantiated: Boolean

    fun instantiateMainPlayer(player: ClientPlayerObject)
    fun destroy()
}

class ClientPlayerManagerImpl(
    private val playerStorage: ClientPlayerStorage
) : ClientPlayerManager {
    override var clientPlayer: ClientPlayerObject? = null
    override val isInstantiated: Boolean
        get() = clientPlayer != null

    override fun instantiateMainPlayer(player: ClientPlayerObject) {
        playerStorage.add(player)
        clientPlayer = player
    }

    override fun destroy() {
        val player = clientPlayer
        if (player != null) {
            playerStorage.remove(player.id)
            clientPlayer = null
        }
    }
}