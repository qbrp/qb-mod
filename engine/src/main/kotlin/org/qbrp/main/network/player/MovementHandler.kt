package org.qbrp.main.network.player

import kotlinx.serialization.Serializable
import net.minecraft.network.PacketByteBuf
import org.koin.core.component.KoinComponent
import org.qbrp.main.core.components.extensions.get
import org.qbrp.main.engine.physics.player.PlayerMovementAbilities
import org.qbrp.main.engine.physics.player.fly
import org.qbrp.main.engine.physics.player.hasFlyPermission
import org.qbrp.main.engine.physics.player.velocity
import org.qbrp.main.engine.physics.system.service.everyPhysicsTick
import org.qbrp.main.engine.players.model.pos
import org.qbrp.main.engine.players.model.service.ServerPlayerStorage
import org.qbrp.main.engine.world.location.WorldPos
import org.qbrp.main.engine.world.location.WorldVec3
import org.qbrp.main.engine.world.location.asWorld
import org.qbrp.main.engine.world.location.asWorldVec3
import org.qbrp.main.network.transport.Endpoint
import org.qbrp.main.network.transport.NetworkChannel
import org.qbrp.main.network.transport.Packet
import org.qbrp.main.network.transport.PacketCodec
import org.qbrp.main.network.transport.readPos
import org.qbrp.main.network.transport.readVec3
import org.qbrp.main.network.transport.wrapEndpoint
import org.qbrp.main.network.transport.writePos
import org.qbrp.main.network.transport.writeVec3

// Position and Velocity packet

data class PlayerPositionVelocityPacket(val pos: WorldPos, val velocity: WorldVec3) : Packet

fun serializePlayerPositionPacket(packet: PlayerPositionVelocityPacket, buf: PacketByteBuf) {
    buf.writePos(packet.pos)
    buf.writeVec3(packet.velocity)
}

fun deserializePlayerPositionPacket(buf: PacketByteBuf): PlayerPositionVelocityPacket {
    return PlayerPositionVelocityPacket(
        buf.readPos().asWorld(),
        buf.readVec3().asWorldVec3()
    )
}

val PlayerPositionCodec = PacketCodec.Binary(
    { deserializePlayerPositionPacket(this) },
    { serializePlayerPositionPacket(it, this) }
)

val MAIN_PLAYER_POSITION_VELOCITY_ENDPOINT = NetworkChannel<PlayerPositionVelocityPacket>(
    id = "other-player-pos",
    codec = PlayerPositionCodec
)
    .wrapEndpoint()

// Fly update packet

@Serializable
data class UpdateFlyAbilityPacket(
    val fly: Boolean
) : Packet

val UPDATE_FLY_ABILITY_ENDPOINT = Endpoint<UpdateFlyAbilityPacket>()

fun KoinComponent.runPlayerMovementHandler(playerStorage: ServerPlayerStorage) {
    UPDATE_FLY_ABILITY_ENDPOINT
        .registerReceiver { ctx ->
            val player = ctx.player ?: return@registerReceiver
            if (player.hasFlyPermission()) {
                ctx.player?.fly = fly
            }
        }

    everyPhysicsTick {
        for (player in playerStorage) {
            val pos = player.pos
            val velocity = player.velocity

            MAIN_PLAYER_POSITION_VELOCITY_ENDPOINT
                .sendS2C(PlayerPositionVelocityPacket(pos, velocity), player)
        }
    }
}