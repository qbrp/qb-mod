package org.qbrp.main.core.mc.server.command

import org.qbrp.main.core.networking.PacketListener

interface PlayerCommandExecuteListener: PacketListener {
    fun onCommand(command: RawMinecraftCommand)
}