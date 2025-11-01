package org.qbrp.main.network.input

import org.koin.core.component.KoinComponent
import org.qbrp.main.core.fabricmc.getCameraOrientation
import org.qbrp.main.engine.physics.player.MovementInput
import org.qbrp.main.engine.physics.player.input
import org.qbrp.main.network.transport.NetworkChannel
import org.qbrp.main.network.transport.Packet
import org.qbrp.main.network.transport.PacketCodec
import org.qbrp.main.network.transport.wrapEndpoint

data class UpdateCameraOrientationPacket(
    val yaw: Double,
    val pitch: Double
) : Packet

val UPDATE_CAMERA_ORIENTATION_ENDPOINT = NetworkChannel<UpdateCameraOrientationPacket>(
    codec = PacketCodec.Binary(
        {
            UpdateCameraOrientationPacket(
                readDouble(),
                readDouble()
            )
        },
        {
            writeDouble(it.yaw)
            writeDouble(it.pitch)
        }
    )
)
    .wrapEndpoint()

data class UpdatePlayerInputPacket(
    override val pressingForward: Boolean,
    override val pressingBack: Boolean,
    override val pressingLeft: Boolean,
    override val pressingRight: Boolean,
    override val isSneaking: Boolean,
    override val isJumping: Boolean
) : Packet, MovementInput {
    companion object {
        fun from(i: MovementInput): UpdatePlayerInputPacket {
            return UpdatePlayerInputPacket(
                i.pressingForward,
                i.pressingBack,
                i.pressingLeft,
                i.pressingRight,
                i.isSneaking,
                i.isJumping
            )
        }
    }
}

val UPDATE_PLAYER_INPUT_ENDPOINT = NetworkChannel<UpdatePlayerInputPacket>(
    codec = PacketCodec.Binary(
        {
            UpdatePlayerInputPacket(
                readBoolean(),
                readBoolean(),
                readBoolean(),
                readBoolean(),
                readBoolean(),
                readBoolean()
            )
        },
        {
            writeBoolean(it.pressingForward)
            writeBoolean(it.pressingBack)
            writeBoolean(it.pressingLeft)
            writeBoolean(it.pressingRight)
            writeBoolean(it.isSneaking)
            writeBoolean(it.isJumping)
        }
    )
).wrapEndpoint()

fun KoinComponent.runPlayerInputHandler() {
    UPDATE_PLAYER_INPUT_ENDPOINT
        .registerReceiver { ctx ->
            val input = ctx.player?.input ?: return@registerReceiver
            input.pressingForward = pressingForward
            input.pressingBack = pressingBack
            input.pressingLeft = pressingLeft
            input.pressingRight = pressingRight
            input.isJumping = isJumping
            input.isSneaking = isSneaking
        }

    UPDATE_CAMERA_ORIENTATION_ENDPOINT
        .registerReceiver { ctx ->
            val newYaw = yaw
            val newPitch = pitch
            val orientation = ctx.player?.getCameraOrientation(ctx.playerEntity) ?: return@registerReceiver
            orientation.apply {
                yaw = newYaw.toDouble()
                pitch = newPitch.toDouble()
            }
        }
}