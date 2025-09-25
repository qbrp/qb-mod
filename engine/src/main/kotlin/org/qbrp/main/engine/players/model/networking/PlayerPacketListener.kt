package org.qbrp.main.engine.players.model.networking

import org.qbrp.main.core.networking.PacketListener
import org.qbrp.main.engine.players.auth.SessionData
import org.qbrp.main.engine.players.model.mapping.PlayerListDto

interface PlayerPacketListener : PacketListener {
    fun onLocalPlayerInstantiatedOnServer(session: SessionData, entityName: String)
    fun onPlayerList(list: PlayerListDto)
}