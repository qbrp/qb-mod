package org.qbrp.main.engine.players.model.mapping

import kotlinx.serialization.Serializable
import org.qbrp.main.core.components.serialization.DTO

@Serializable
data class PlayerListDto(val entries: List<Entry>): DTO {
    @Serializable
    data class Entry(val playerId: String, val username: String, val minecraftPlayerId: String)
}