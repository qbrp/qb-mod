package org.qbrp.main.engine.physics.chunk

import net.minecraft.block.Block
import net.minecraft.block.BlockState
import net.minecraft.registry.tag.BlockTags
import net.minecraft.registry.tag.TagKey
import net.minecraft.server.world.ServerWorld
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Box
import net.minecraft.world.chunk.Chunk
import org.qbrp.main.engine.physics.system.entity.Body
import org.qbrp.main.engine.physics.system.entity.Flat
import org.qbrp.main.engine.physics.system.entity.Segment
import org.qbrp.main.engine.physics.system.entity.Shape
import kotlin.math.max

data class ChunkLoadSettings(
    val minHeight: Int = ChunkConstants.WORLD_MIN_HEIGHT,
    val maxHeight: Int = ChunkConstants.WORLD_MAX_HEIGHT,
    val blockSettings: BlockSettings
)

sealed class BlockResolve {
    object Ignore : BlockResolve()
}

data class BlockSettings(
    val byTag: Map<TagKey<Block>, BlockResolve>
) {
    companion object {
        val DEFAULT = BlockSettings(
            mapOf(
                BlockTags.DOORS to BlockResolve.Ignore,
                BlockTags.TRAPDOORS to BlockResolve.Ignore,
            ),
        )
    }
}

const val MIN_HALF_EXTENT = 0.1

fun buildChunkCompoundShape(
    world: ServerWorld,
    chunk: Chunk,
    settings: ChunkLoadSettings
): Shape.SegmentCompound {
    require(settings.minHeight >= ChunkConstants.WORLD_MIN_HEIGHT)
    require(settings.maxHeight <= ChunkConstants.WORLD_MAX_HEIGHT)

    val blockParameters = settings.blockSettings
    val minHeight = max(ChunkConstants.WORLD_MIN_HEIGHT + 1, settings.minHeight)
    val maxHeight = settings.maxHeight

    // Сбор всех коллизионных боксов по y
    val boxesByPos = mutableMapOf<BlockPos, List<Box>>()
    for (x in 0 until ChunkConstants.CHUNK_SIZE) {
        for (z in 0 until ChunkConstants.CHUNK_SIZE) {
            for (y in minHeight until maxHeight) {
                val pos = BlockPos(x, y, z)
                val blockState = chunk.getBlockState(pos)
                for (entry in blockParameters.byTag) {
                    val tag = entry.key
                    val resolve = entry.value
                    if (blockState.isIn(tag)) {
                        when(resolve) {
                            is BlockResolve.Ignore -> continue
                        }
                    }
                }
                val shapes = blockState
                    .getCollisionShape(world, pos)
                    .boundingBoxes
                if (shapes.isNotEmpty()) {
                    boxesByPos[pos] = shapes
                }
            }
        }
    }

    val segments = mutableListOf<Segment>()

    val currentBlockPos = BlockPos.Mutable()
    for (y in minHeight until maxHeight) {
        val flats = mutableListOf<Flat>()
        val incompleteBoxes = mutableListOf<Box>()
        currentBlockPos.y = y
        for (z in 0 until ChunkConstants.CHUNK_SIZE) {
            var startFound = false
            var startX = 0
            currentBlockPos.z = z
            for (x in 0..ChunkConstants.CHUNK_SIZE) {
                currentBlockPos.x = x
                val shouldInclude = shouldInclude(world, chunk, x, y, z)
                if (shouldInclude) {
                    val boxes = boxesByPos[currentBlockPos]
                    boxes?.forEach { box ->
                        val fullBlock = (box.xLength == 1.0 && box.yLength == 1.0 && box.zLength == 1.0)
                        if (!fullBlock) {
                            incompleteBoxes += box
                        }
                    }
                    if (!startFound) {
                        startFound = true
                        startX = x
                    }
                } else if (startFound) {
                    startFound = false
                    val endX = x
                    if (startX < endX) {
                        flats.add(Flat(startX, endX, z, z))
                    }
                }
            }
        }

        val segment = Segment(
            y,
            flats,
            incompleteBoxes
                .map {
                    val xHalf = max(it.xLength / 2f, MIN_HALF_EXTENT)
                    val yHalf = max(it.yLength / 2f, MIN_HALF_EXTENT)
                    val zHalf = max(it.zLength / 2f, MIN_HALF_EXTENT)
                    Shape.Box(xHalf, yHalf, zHalf)
                }
        )
        if (!segment.isEmpty()) segments.add(segment)
    }

    return Shape.SegmentCompound(segments)
}

private val offsets = arrayOf(
    Triple(1,0,0), Triple(-1,0,0),
    Triple(0,1,0), Triple(0,-1,0),
    Triple(0,0,1), Triple(0,0,-1)
)

private fun shouldInclude(world: ServerWorld, chunk: Chunk, x: Int, y: Int, z: Int): Boolean {
    val pos = BlockPos.Mutable(x, y, z)
    val shape = chunk.getBlockState(pos).getCollisionShape(world, pos)
    if (shape.isEmpty) return false

    for ((dx, dy, dz) in offsets) {
        pos.set(x + dx, y + dy, z + dz)
        val neighborShape = chunk.getBlockState(pos).getCollisionShape(world, pos)
        if (neighborShape.isEmpty) return true
    }

    return false
}