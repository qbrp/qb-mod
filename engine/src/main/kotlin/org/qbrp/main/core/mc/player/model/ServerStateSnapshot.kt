package org.qbrp.main.core.mc.player.model

import org.qbrp.main.core.mc.player.gamemode.MinecraftGameMode

interface ServerStateSnapshot {
    val gameMode: MinecraftGameMode
}