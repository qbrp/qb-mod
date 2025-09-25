package org.qbrp.main.engine.physics.dynamics.tracking

import org.qbrp.main.engine.world.location.Position
import org.qbrp.main.engine.players.model.service.ServerPlayerStorage

class PlayerTrackingPositions(private val playerStorage: ServerPlayerStorage): BodyTrackingPositions {
    override val positions: List<Position> get() = mapPositions()

    private fun mapPositions(): List<Position> {
        return playerStorage.map { it.pos }
    }
}