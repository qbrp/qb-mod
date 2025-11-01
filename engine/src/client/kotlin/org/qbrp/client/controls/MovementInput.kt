package org.qbrp.client.controls

import net.minecraft.client.MinecraftClient
import net.minecraft.client.input.Input
import org.qbrp.main.engine.physics.player.MovementInput
import org.qbrp.main.engine.physics.player.MovementInputComponent

fun updateMovementInput(engineInput: MovementInputComponent, client: MinecraftClient) = with(engineInput) {
    val input = client.player?.input ?: return
    input.tick(false, 0.0f)
    pressingForward = input.pressingForward
    pressingBack = input.pressingBack
    pressingLeft = input.pressingLeft
    pressingRight = input.pressingRight
    isSneaking = input.sneaking
    isJumping = input.jumping
}