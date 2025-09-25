package org.qbrp.main.core.mc.player.gamemode

import net.minecraft.world.GameMode

enum class MinecraftGameMode {
    SURVIVAL, CREATIVE, SPECTATOR, ADVENTURE;

    companion object {
        fun from(gameMode: GameMode): MinecraftGameMode {
            return when (gameMode) {
                GameMode.CREATIVE -> CREATIVE
                GameMode.SURVIVAL -> SURVIVAL
                GameMode.SPECTATOR -> SPECTATOR
                GameMode.ADVENTURE -> ADVENTURE
            }
        }
    }
}