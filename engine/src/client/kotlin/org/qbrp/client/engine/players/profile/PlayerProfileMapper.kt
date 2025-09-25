package org.qbrp.client.engine.players.profile

import net.minecraft.client.MinecraftClient
import net.minecraft.client.texture.TextureManager
import net.minecraft.util.Identifier
import org.qbrp.main.core.mc.player.dataformats.MinecraftPlayerId
import org.qbrp.main.core.mc.player.dataformats.Username
import org.qbrp.main.core.networking.serialization.mapping.FromDtoStrategy
import org.qbrp.main.engine.players.model.mapping.PlayerListDto
import org.qbrp.main.engine.players.model.util.PlayerId
import java.util.UUID

class PlayerProfileMapper(): FromDtoStrategy<PlayerListDto, List<PlayerProfile>> {
    override fun fromDto(dto: PlayerListDto): List<PlayerProfile> {
        return dto.entries.map { PlayerProfile(
            PlayerId(it.playerId),
            Username(it.username),
            MinecraftPlayerId(UUID.fromString(it.minecraftPlayerId)))
        }
    }
}