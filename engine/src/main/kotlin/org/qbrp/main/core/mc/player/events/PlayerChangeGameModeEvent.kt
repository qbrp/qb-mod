package org.qbrp.main.core.mc.player.events

import org.qbrp.main.core.events.Event
import org.qbrp.main.core.mc.player.gamemode.MinecraftGameMode
import org.qbrp.main.engine.players.model.PlayerObject

class PlayerChangeGameModeEvent(val gameMode: MinecraftGameMode, val player: PlayerObject): Event
