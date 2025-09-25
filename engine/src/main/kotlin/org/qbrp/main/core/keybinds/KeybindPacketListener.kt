package org.qbrp.main.core.keybinds

import org.qbrp.main.core.networking.PacketListener
import org.qbrp.main.engine.players.model.PlayerObject

interface KeybindPacketListener: PacketListener {
    fun onKeybind(player: PlayerObject, id: KeybindId)
}