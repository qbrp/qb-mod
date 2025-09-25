package org.qbrp.main.engine.physics.dynamics.chunk.factory

class ChunkColliderDescriptor(override val segments: List<Segment>,
                              override val minY: Int,
                              override val maxY: Int
): ChunkCollider