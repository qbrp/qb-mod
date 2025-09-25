package org.qbrp.main.engine.players.model.mapping

import org.qbrp.main.core.networking.serialization.mapping.ToDtoStrategy
import org.qbrp.main.engine.players.model.service.PlayerList

class PlayerListMapper: ToDtoStrategy<PlayerListDto, PlayerList> {
    override fun toDto(obj: PlayerList): PlayerListDto {
        val entries = obj.map { PlayerListDto.Entry(it.id, it.username.value, it.uuid.value.toString()) }
        return PlayerListDto(entries)
    }
}