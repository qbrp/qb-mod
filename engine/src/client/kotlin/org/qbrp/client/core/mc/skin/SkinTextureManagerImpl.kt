package org.qbrp.client.core.mc.skin

import net.minecraft.client.network.ClientPlayNetworkHandler
import net.minecraft.client.texture.TextureManager
import net.minecraft.util.Identifier
import org.qbrp.main.core.fabricmc.player.MinecraftPlayerId

class SkinTextureManagerImpl(private val networkHandler: ClientPlayNetworkHandler): SkinTextureManager {
    override fun getSkinId(playerId: MinecraftPlayerId): Identifier {
        return networkHandler.getPlayerListEntry(playerId.value)?.skinTexture ?: TextureManager.MISSING_IDENTIFIER
    }
}