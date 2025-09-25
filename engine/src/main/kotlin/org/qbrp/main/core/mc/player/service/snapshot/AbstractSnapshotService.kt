package org.qbrp.main.core.mc.player.service.snapshot

import net.minecraft.server.network.ServerPlayerEntity
import org.qbrp.main.core.mc.player.dataformats.MinecraftPlayerId
import org.qbrp.main.core.mc.player.dataformats.Username
import org.qbrp.main.core.mc.player.gamemode.MinecraftGameMode
import org.qbrp.main.core.mc.player.model.EntitySnapshot
import org.qbrp.main.core.mc.player.model.PlayerAttributes
import org.qbrp.main.core.mc.player.model.ServerStateSnapshot
import org.qbrp.main.core.mc.player.model.impl.LazyMcServerPlayerSnapshot
import org.qbrp.main.core.mc.player.model.impl.McPlayerAttributesSnapshot
import org.qbrp.main.core.mc.player.model.impl.McPlayerEntitySnapshot
import org.qbrp.main.core.mc.player.model.impl.McPlayerServerStateSnapshot
import org.qbrp.main.core.mc.player.service.attribute.MinecraftPlayerAttributeService
import org.qbrp.main.engine.physics.dynamics.dataformats.Vec3Adapter
import org.qbrp.main.engine.physics.dynamics.dataformats.engine
import org.qbrp.main.engine.world.location.WorldPosition

abstract class AbstractSnapshotService(private val attributesService: MinecraftPlayerAttributeService): MinecraftPlayerSnapshotService {
    fun lazySnapshotPlayer(entity: ServerPlayerEntity): LazyMcServerPlayerSnapshot {
        return LazyMcServerPlayerSnapshot(
            this,
            Username(entity.name.string),
            WorldPosition(entity.pos.x, entity.pos.y, entity.pos.z),
            MinecraftPlayerId(entity.uuid)
        )
    }

    fun snapshotEntity(entity: ServerPlayerEntity): EntitySnapshot {
        return McPlayerEntitySnapshot(
            entity.eyePos.engine(),
            Vec3Adapter(entity.rotationVector.engine().normalize())
        )
    }

    fun snapshotServerState(entity: ServerPlayerEntity): ServerStateSnapshot {
        val engineGameMode = MinecraftGameMode.from(entity.interactionManager.gameMode)
        return McPlayerServerStateSnapshot(engineGameMode)
    }

    fun snapshotAttributes(entity: ServerPlayerEntity): PlayerAttributes {
        val speedModifier = attributesService.getSpeedModifier(entity)
        return McPlayerAttributesSnapshot(speedModifier)
    }
}