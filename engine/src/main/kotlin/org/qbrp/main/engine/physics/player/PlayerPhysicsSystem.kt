package org.qbrp.main.engine.physics.player

import net.minecraft.entity.player.PlayerEntity
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.server.world.ServerWorld
import net.minecraft.util.math.Box
import net.minecraft.util.math.MathHelper
import net.minecraft.world.World
import org.qbrp.main.core.components.extensions.get
import org.qbrp.main.core.components.extensions.require
import org.qbrp.main.core.fabricmc.pitch
import org.qbrp.main.core.fabricmc.yaw
import org.qbrp.main.core.util.math.convertToPhysics
import org.qbrp.main.core.util.math.convertToWorld
import org.qbrp.main.engine.players.model.PlayerLocation
import org.qbrp.main.engine.players.model.PlayerObject
import org.qbrp.main.engine.players.model.service.GenericPlayerList
import org.qbrp.main.engine.players.model.service.PlayerList
import org.qbrp.main.engine.world.location.WorldVec3
import org.qbrp.main.engine.world.location.asWorld
import org.qbrp.main.engine.world.location.asWorldVec3
import java.util.Optional

class PlayerPhysicsSystem {
    fun beforeSimulation(playerList: PlayerList, entities: AnyMinecraftEntities) {
        for (player in playerList) {
            val component = player.get<PlayerPhysicalCharacter>() ?: continue
            val entity = entities.getEntity(player.playerId) ?: continue
            val character = component.character
            val position = character.transform.pos
            val isSupported = character.isSupported

            player.require<VelocityComponent>().let { velocity ->
                if (velocity.dirty) {
                    character.linearVelocity = velocity.vec3.convertToPhysics()
                    velocity.dirty = false
                }
                velocity.vec3 = character.linearVelocity.convertToWorld()
            }

            player.get<NewPositionComponent>()?.let { newPositionComponent ->
                player.removeComponent(newPositionComponent)
                character.setPos(newPositionComponent.pos)
            }

            player.require<IsSupportedComponent>().let { component ->
                component.isSupported = isSupported
            }

            updatePlayerEntityPosition(
                player.yaw,
                player.pitch,
                position.asWorldVec3(),
                isSupported,
                entity
            )

            player.require<PlayerLocation>().pos = position.asWorld()
        }
    }

    private fun updatePlayerEntityPosition(
        yaw: Double,
        pitch: Double,
        enginePos: WorldVec3,
        isOnGround: Boolean,
        entity: PlayerEntity
    ) {
        val player = entity
        val world: World = entity.world

        if (player is ServerPlayerEntity && player.notInAnyWorld) return
        val x = enginePos.x
        val y = enginePos.y
        val z = enginePos.z
        entity.updatePosition(x, y, z)
        if (world is ServerWorld && player is ServerPlayerEntity) {
            val yaw = MathHelper.wrapDegrees(yaw.toFloat())
            val pitch = MathHelper.wrapDegrees(pitch.toFloat())
            entity.yaw = yaw
            entity.pitch = pitch
            world.chunkManager.updatePosition(entity)
        }
        entity.setOnGround(isOnGround)
        entity.updateSupportingBlockPosEngine(isOnGround)
    }

    private fun PlayerEntity.updateSupportingBlockPosEngine(isOnGround: Boolean) {
        if (!isOnGround) {
            supportingBlockPos = Optional.empty()
            return
        }
        val box = boundingBox
        val belowBox = Box(
            box.minX,
            box.minY - 1.0E-6,
            box.minZ,
            box.maxX,
            box.minY,
            box.maxZ
        )

        val blockPos = world.findSupportingBlockPos(this, belowBox)

        supportingBlockPos = if (blockPos.isPresent) {
            blockPos
        } else {
            Optional.empty()
        }
    }
}