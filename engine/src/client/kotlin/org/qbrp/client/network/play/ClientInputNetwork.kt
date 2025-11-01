package org.qbrp.client.network.play

import net.minecraft.client.MinecraftClient
import org.koin.core.component.KoinComponent
import org.qbrp.client.core.getClientPlayer
import org.qbrp.client.core.onClientTick
import org.qbrp.client.core.onClientWorldTick
import org.qbrp.client.network.transport.sendC2SPacket
import org.qbrp.main.core.components.extensions.get
import org.qbrp.main.core.fabricmc.pitch
import org.qbrp.main.core.fabricmc.yaw
import org.qbrp.main.engine.physics.player.MovementInput
import org.qbrp.main.network.input.UPDATE_CAMERA_ORIENTATION_ENDPOINT
import org.qbrp.main.network.input.UPDATE_PLAYER_INPUT_ENDPOINT
import org.qbrp.main.network.input.UpdateCameraOrientationPacket
import org.qbrp.main.network.input.UpdatePlayerInputPacket

fun KoinComponent.runClientInputPacketSender() {
    onClientWorldTick {
        val player = getClientPlayer()
        val input = player?.get<MovementInput>() ?: return@onClientWorldTick
        val packet = UpdatePlayerInputPacket.from(input)
        UPDATE_PLAYER_INPUT_ENDPOINT
            .sendC2SPacket(packet)

        // Yaw и pitch вычисляются в Mouse
        UPDATE_CAMERA_ORIENTATION_ENDPOINT
            .sendC2SPacket(UpdateCameraOrientationPacket(player.yaw, player.pitch))
    }
}