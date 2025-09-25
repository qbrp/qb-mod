package org.qbrp.main.core.mc.player.events

import org.qbrp.main.core.events.Event
import org.qbrp.main.core.mc.player.model.PlayerSnapshot
import org.qbrp.main.core.mc.server.thread.ServerThread

data class MinecraftPlayerLeaveEvent(val player: PlayerSnapshot, val serverThread: ServerThread): Event