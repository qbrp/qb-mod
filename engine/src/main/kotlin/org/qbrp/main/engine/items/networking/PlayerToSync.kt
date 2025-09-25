package org.qbrp.main.engine.items.networking

import org.qbrp.main.engine.players.model.ServerPlayerObject

interface PlayerToSync {
    fun providePlayersToSync(): Collection<ServerPlayerObject>
}