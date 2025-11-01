package org.qbrp.client.core.mc.skin

import net.minecraft.util.Identifier
import org.qbrp.main.core.fabricmc.player.MinecraftPlayerId

interface SkinTextureManager {
    fun getSkinId(playerId: MinecraftPlayerId): Identifier
}