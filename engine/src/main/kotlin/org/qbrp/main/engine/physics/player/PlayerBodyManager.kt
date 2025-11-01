package org.qbrp.main.engine.physics.player

import net.minecraft.entity.player.PlayerEntity
import net.minecraft.server.network.ServerPlayerEntity
import org.qbrp.main.core.components.DataComponent
import org.qbrp.main.core.components.extensions.add
import org.qbrp.main.core.components.extensions.get
import org.qbrp.main.core.components.extensions.remove
import org.qbrp.main.core.fabricmc.CameraOrientation
import org.qbrp.main.core.fabricmc.player.MinecraftPlayerId
import org.qbrp.main.engine.physics.system.entity.PhysicalCharacter
import org.qbrp.main.engine.physics.system.service.BodyFactory
import org.qbrp.main.engine.physics.system.service.WorldBodyManager
import org.qbrp.main.engine.players.model.PlayerObject
import org.qbrp.main.engine.players.model.ServerPlayerObject
import org.qbrp.main.engine.players.model.pos
import org.qbrp.main.engine.players.model.service.PlayerStorage
import org.qbrp.main.core.util.PlayerId
import org.qbrp.main.engine.world.location.WorldVec3
import java.util.concurrent.ConcurrentLinkedQueue

interface PlayerBodyManager {
    fun addPlayerCharacter(player: PlayerObject)
    fun removePlayerCharacter(player: PlayerObject)
}

interface MinecraftEntities<E : PlayerEntity> {
    fun getEntity(playerId: PlayerId): E?
    fun getEntity(minecraftId: MinecraftPlayerId): E?
}

typealias AnyMinecraftEntities = MinecraftEntities<out PlayerEntity>

interface ServerMinecraftEntities : MinecraftEntities<ServerPlayerEntity>

class JoltPlayerBodyManager(
    private val bodyFactory: BodyFactory,
    private val worldBodyManager: WorldBodyManager,
    private val playerStorage: PlayerStorage<out PlayerObject>,
) : PlayerBodyManager {
    override fun addPlayerCharacter(player: PlayerObject) {
        val character = bodyFactory.playerCharacter(player.pos)
        worldBodyManager.addCharacter(character)
        player.add(PlayerPhysicalCharacter(character, worldBodyManager.simulation))
    }

    override fun removePlayerCharacter(player: PlayerObject) {
        val character = player.remove<PlayerPhysicalCharacter>()?.character ?: return
        worldBodyManager.removeCharacter(character)
    }
}