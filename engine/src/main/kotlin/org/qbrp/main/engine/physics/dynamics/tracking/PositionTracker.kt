package org.qbrp.main.engine.physics.dynamics.tracking

import org.qbrp.main.core.mc.chunks.ChunkStorage
import org.qbrp.main.engine.physics.dynamics.ChunkConstants
import org.qbrp.main.engine.physics.dynamics.chunk.factory.LoadSettings
import org.qbrp.main.engine.physics.dynamics.chunk.loader.PhysicsChunkManager
import org.qbrp.main.engine.physics.dynamics.dataformats.Body
import org.qbrp.main.engine.world.location.Position
import org.qbrp.main.engine.physics.dynamics.simulation.WorldBodyManager

class PositionTracker(private val chunkLoader: PhysicsChunkManager,
                      private val chunkStorage: ChunkStorage,
                      private val positionsManagers: MutableList<BodyTrackingPositions>) {
    private val positions get() = positionsManagers.flatMap { it.positions }

    fun addPositionTracker(positionTracker: BodyTrackingPositions) {
        positionsManagers.add(positionTracker)
    }

    fun handleAddedBody(body: Body, bodyManager: WorldBodyManager) {
        val pos = body.transform.pos
        val world = chunkStorage.world
        val chunk = world.getChunk(pos.toBlockPos())
        chunkLoader.load(chunk, world, getLoadSettings(listOf(pos)))
    }

    fun trackAndUpsert() {
        val world = chunkStorage.world
        for (chunk in chunkStorage.chunks) {
            var poses = positions
                .filter { it.isInsideChunk(chunk) }
                .map { it.toLocal(chunk.pos) }
            if (poses.isNotEmpty()) {
                chunkLoader.load(chunk, world, getLoadSettings(poses))
            } else {
                chunkLoader.unload(chunk, world)
            }
        }
    }

    private fun getLoadSettings(positions: List<Position>): LoadSettings {
        val heights = positions.map {
            it.y.toInt().coerceIn(ChunkConstants.WORLD_MIN_HEIGHT, ChunkConstants.WORLD_MAX_HEIGHT)
        }

        val rawMin = heights.minOrNull() ?: ChunkConstants.WORLD_MIN_HEIGHT
        val rawMax = heights.maxOrNull() ?: ChunkConstants.WORLD_MAX_HEIGHT

        val min = (rawMin - 12).coerceAtLeast(ChunkConstants.WORLD_MIN_HEIGHT)
        val max = (rawMax + 12).coerceAtMost(ChunkConstants.WORLD_MAX_HEIGHT)

        return LoadSettings(minHeight = min, maxHeight = max)
    }
}
