package org.qbrp.client.engine.players

class ClientPlayerManagerImpl() : ClientPlayerManager {
    private var _clientPlayer: ClientPlayerObject? = null
    override val clientPlayer: ClientPlayerObject get() = _clientPlayer ?: throw NullPointerException("Локальный игрок не инициализирован")
    override val isInstantiated: Boolean get() = _clientPlayer != null

    fun instantiate(player: ClientPlayerObject) {
        _clientPlayer = player
    }

    fun remove() {
        _clientPlayer = null
    }
}