package org.qbrp.main.core.mc.player.service

import net.minecraft.network.packet.s2c.play.PlayerListS2CPacket
import net.minecraft.server.MinecraftServer
import net.minecraft.server.network.ServerPlayerEntity
import org.qbrp.main.core.mc.player.dataformats.MinecraftPlayerId
import org.qbrp.main.core.mc.player.model.PlayerReference
import org.qbrp.main.core.mc.player.model.PlayerSnapshot
import java.util.UUID

class MinecraftServerPlayerManager(private val server: MinecraftServer) : MinecraftPlayerManager {
    private val serverPlayerManager by lazy { server.playerManager }
    private val emittedPlayers: MutableMap<MinecraftPlayerId, ServerPlayerEntity> = mutableMapOf()
    override val players: Collection<PlayerReference> get() = server.playerManager.playerList.map { Ref(it.uuid) }

    override fun sendTabPlayerListPacket(id: MinecraftPlayerId) {
        serverPlayerManager.sendToAll(PlayerListS2CPacket.entryFromPlayer(listOf(getById(id))))
    }

    override fun getById(id: MinecraftPlayerId): ServerPlayerEntity? {
        return serverPlayerManager.getPlayer(id.value) ?: emittedPlayers[id]
    }

    fun onEmit(snapshot: PlayerSnapshot, entity: ServerPlayerEntity) {
        emittedPlayers[snapshot.uuid] = entity
    }

    class Ref(uuid: UUID): PlayerReference {
        override val uuid: MinecraftPlayerId = MinecraftPlayerId(uuid)
    }
}