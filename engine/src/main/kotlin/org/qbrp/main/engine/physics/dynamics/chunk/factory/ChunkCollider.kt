package org.qbrp.main.engine.physics.dynamics.chunk.factory

import net.minecraft.util.math.ChunkPos
import org.qbrp.main.engine.physics.dynamics.dataformats.Shape

interface ChunkCollider: Shape {
    val segments: List<Segment>
    val minY: Int
    val maxY: Int

    fun isEmpty(): Boolean = segments.isEmpty() || !segments.any { !it.isEmpty() }
}