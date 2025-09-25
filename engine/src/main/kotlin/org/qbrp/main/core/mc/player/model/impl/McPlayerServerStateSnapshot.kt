package org.qbrp.main.core.mc.player.model.impl

import org.qbrp.main.core.mc.player.gamemode.MinecraftGameMode
import org.qbrp.main.core.mc.player.model.ServerStateSnapshot

data class McPlayerServerStateSnapshot(override val gameMode: MinecraftGameMode) : ServerStateSnapshot