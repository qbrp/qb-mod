package org.qbrp.client.engine.movement

import org.qbrp.main.core.events.Event

data class PlayerPositionLookPacketEvent(
    val x: Double,
    val y: Double,
    val z: Double,
    val pitch: Double,
    val yaw: Double
) : Event