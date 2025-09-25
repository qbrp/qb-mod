package org.qbrp.client.engine.players.networking

import org.qbrp.client.engine.players.ClientPlayerObject
import org.qbrp.client.engine.players.profile.PlayerProfileManager
import org.qbrp.main.engine.players.auth.SessionData
import org.qbrp.main.engine.players.model.util.PlayerId

class ClientPlayerFactory(private val playerProfileManager: PlayerProfileManager) {
    fun buildLocalPlayer(session: SessionData): ClientPlayerObject {
        return build(session.playerId)
    }

    fun build(id: PlayerId): ClientPlayerObject {
        val profile = playerProfileManager.requireProfile(id)
        val player = ClientPlayerObject(id, profile.username, profile)
        player.state.parent = player
        return player
    }
}