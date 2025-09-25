package org.qbrp.main.engine.physics.dynamics.chunk.factory

import net.minecraft.util.math.Box

class Segment(val y: Int, val flats: List<ChunkColliderFactoryImpl.Flat>, val shapes: List<Box>) {
    fun isEmpty(): Boolean = flats.isEmpty() && shapes.isEmpty()
}