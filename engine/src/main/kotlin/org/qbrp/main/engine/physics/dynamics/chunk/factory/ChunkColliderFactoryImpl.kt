package org.qbrp.main.engine.physics.dynamics.chunk.factory

import net.minecraft.server.world.ServerWorld
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Box
import net.minecraft.world.chunk.Chunk
import org.qbrp.main.engine.physics.dynamics.ChunkConstants

class ChunkColliderFactoryImpl : ChunkColliderFactory {
    data class Flat(val startX: Int, val endX: Int, val startZ: Int, val endZ: Int)

    override fun build(
        world: ServerWorld,
        chunk: Chunk,
        settings: LoadSettings
    ): ChunkCollider {
        require(settings.minHeight >= ChunkConstants.WORLD_MIN_HEIGHT)
        require(settings.maxHeight <= ChunkConstants.WORLD_MAX_HEIGHT)

        // Сбор всех коллизионных боксов по y
        val boxesByY = mutableMapOf<Int, MutableList<Box>>()
        for (x in 0 until ChunkConstants.CHUNK_SIZE) {
            for (z in 0 until ChunkConstants.CHUNK_SIZE) {
                for (y in settings.minHeight until settings.maxHeight) {
                    val pos = BlockPos(x, y, z)
                    val shapes = chunk.getBlockState(pos)
                        .getCollisionShape(world, pos)
                        .boundingBoxes
                    if (shapes.isNotEmpty()) {
                        boxesByY.computeIfAbsent(y) { mutableListOf() }.addAll(shapes)
                    }
                }
            }
        }

        val segments = mutableListOf<Segment>()

        for (y in settings.minHeight until settings.maxHeight) {
            val flats = mutableListOf<Flat>()
            for (z in 0 until ChunkConstants.CHUNK_SIZE) {
                var x = 0
                while (x < ChunkConstants.CHUNK_SIZE) {
                    while (x < ChunkConstants.CHUNK_SIZE && !shouldInclude(world, chunk, x, y, z)) {
                        x++
                    }
                    val startX = x
                    while (x < ChunkConstants.CHUNK_SIZE && shouldInclude(world, chunk, x, y, z)) {
                        x++
                    }
                    val endX = x
                    if (startX < endX) {
                        flats.add(Flat(startX, endX, z, z))
                    }
                }
            }

            val boxes = boxesByY[y] ?: emptyList()
            val notCoveredBoxes = boxes.filter { box ->
                flats.none { flat ->
                    val coversX = (box.minX >= flat.startX.toDouble()
                            && box.maxX <= flat.endX.toDouble())
                    val coversZ = (box.minZ >= flat.startZ.toDouble()
                            && box.maxZ <= flat.endZ.toDouble())
                    coversX && coversZ
                }
            }
            val segment = Segment(y, flats, notCoveredBoxes)
            if (!segment.isEmpty()) segments.add(segment)
        }

        return ChunkColliderDescriptor(segments, settings.minHeight, settings.maxHeight)
    }

    /**
     * Блок включается, если у него есть коллизионная форма и он граничит хотя бы с одним блоком без коллизии.
     */
    private fun shouldInclude(
        world: ServerWorld,
        chunk: Chunk,
        x: Int,
        y: Int,
        z: Int
    ): Boolean {
        val pos = BlockPos(x, y, z)
        val shape = chunk.getBlockState(pos).getCollisionShape(world, pos)
        if (shape.isEmpty) return false
        // Проверяем соседей по шести направлениям
        val dirs = listOf(
            BlockPos(x + 1, y, z), BlockPos(x - 1, y, z),
            BlockPos(x, y + 1, z), BlockPos(x, y - 1, z),
            BlockPos(x, y, z + 1), BlockPos(x, y, z - 1)
        )
        return dirs.any { neighbor ->
            val neighborShape = chunk.getBlockState(neighbor)
                .getCollisionShape(world, neighbor)
            neighborShape.isEmpty
        }
    }
}
