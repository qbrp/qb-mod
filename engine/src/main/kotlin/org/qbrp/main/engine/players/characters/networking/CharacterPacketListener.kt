package org.qbrp.main.engine.players.characters.networking

import org.qbrp.main.core.networking.PacketListener
import org.qbrp.main.engine.players.model.PlayerObject
import org.qbrp.main.mechanics.characters.model.CharacterData

interface CharacterPacketListener: PacketListener {
    fun onCharacterApply(character: CharacterData, player: PlayerObject)
}