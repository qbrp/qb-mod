package core.server

import net.minecraft.server.network.ServerPlayerEntity
import org.qbrp.main.core.mc.player.dataformats.MinecraftPlayerId
import org.qbrp.main.core.mc.player.model.PlayerReference
import org.qbrp.main.core.mc.player.service.MinecraftPlayerManager
import org.qbrp.main.core.mc.player.asReference

class PlayerManagerMock(val playersMap: MutableList<ServerPlayerEntity> = mutableListOf(), ): MinecraftPlayerManager {
    constructor(vararg players: ServerPlayerEntity): this(players.toMutableList())
    override val players: Collection<PlayerReference> get() = playersMap.map { it.asReference() }
    override fun getById(id: MinecraftPlayerId): ServerPlayerEntity? {
        return playersMap.find { it.uuid == id.value }
    }
}