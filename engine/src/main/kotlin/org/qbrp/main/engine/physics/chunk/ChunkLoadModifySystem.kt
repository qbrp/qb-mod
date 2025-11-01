package org.qbrp.main.engine.physics.chunk

import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.ChunkPos
import net.minecraft.util.math.Vec3i
import org.qbrp.main.core.util.drain
import org.qbrp.main.core.util.math.asVec3
import org.qbrp.main.core.util.math.engineWorldVec3
import org.qbrp.main.core.util.math.localOf
import org.qbrp.main.core.util.math.toMinecraft
import org.qbrp.main.engine.physics.system.entity.Segment
import org.qbrp.main.engine.physics.system.entity.Shape
import org.qbrp.main.engine.physics.system.entity.Transform
import org.qbrp.main.engine.physics.system.service.BodyBatchAdder
import org.qbrp.main.engine.physics.system.service.BodyFactory
import org.qbrp.main.engine.physics.system.service.WorldBodyManager
import java.util.Collections
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentLinkedQueue
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.compareTo
import kotlin.concurrent.thread
import kotlin.math.min

fun BlockShape() = Shape.Box(0.5, 0.5, 0.5)

interface PhysicsChunkManger {
    fun createChunk(pos: PhysicsChunkPos)
    fun appendChunk(pos: PhysicsChunkPos, startY: Int, endY: Int, chunkSegments: ChunkSegments)
    fun addFullSizeBlock(pos: BlockPos)
    fun addBlock(pos: BlockPos, blockShape: Shape.Box)
    fun removeBlockAndRebuildPart(pos: BlockPos)
    fun removeChunkPart(pos: PhysicsChunkPos, index: Int)
    fun removeChunk(pos: PhysicsChunkPos)
}

class ChunkLoadModifySystem(
    private val batchAdder: BodyBatchAdder,
    private val bodyFactory: BodyFactory,
    private val worldBodyManager: WorldBodyManager,
    private val chunkBank: PhysicsChunkBank
) : PhysicsChunkManger {
    private data class AppendChunkTask(val chunk: PhysicsChunk, val part: ChunkPart)

    private data class RebuildChunkTask(
        val chunk: PhysicsChunk,
        val part: ChunkPart,
        val withSegments: ChunkSegments,
        val shapesToRemove: List<SingleBlock>
    )

    private data class AddShapeTask(
        val chunk: PhysicsChunk,
        val transform: Transform,
        val shape: Shape.Box,
        val blockPos: BlockPos
    )

    private val chunkShapeAppends = ConcurrentLinkedQueue<AppendChunkTask>()
    private val rebuildChunkTasks = ConcurrentLinkedQueue<RebuildChunkTask>()
    private val addShapeTasks = ConcurrentLinkedQueue<AddShapeTask>()
    private val commands = ConcurrentLinkedQueue<Command>()

    private val loadingChunks = Collections.newSetFromMap(ConcurrentHashMap<PhysicsChunkPos, Boolean>())
    private val preparedChunks = Collections.newSetFromMap(ConcurrentHashMap<PhysicsChunk, Boolean>())
    private val removeChunks = Collections.newSetFromMap(ConcurrentHashMap<PhysicsChunk, Boolean>())

    data class SingleBlock(val pos: BlockPos, val index: Int, val shape: Shape.Box, val chunk: PhysicsChunk)

    data class ChunkBlocks(val chunk: PhysicsChunk, val blocks: MutableList<SingleBlock>)

    private val singleBlocks = ConcurrentHashMap<PhysicsChunkPos, ChunkBlocks>()

    private val flushPendingChunkTasksSignal = AtomicBoolean()

    private sealed class Command {
        data class AddChunk(val pos: PhysicsChunkPos) : Command()
        data class AddBlock(val pos: BlockPos, val shape: Shape.Box) : Command()
        data class AddFullBlock(val pos: BlockPos) : Command()
        data class MultiBlockAdd(val blockShapes: List<Shape>) : Command()
        data class RemoveChunk(val chunk: PhysicsChunk) : Command()
        data class AppendChunk(
            val pos: PhysicsChunkPos,
            val startY: Int,
            val endY: Int,
            val segments: ChunkSegments
        ) : Command()
    }

    fun flushPendingChunkTasks() {
        flushPendingChunkTasksSignal.set(true)
    }
    
    override fun createChunk(pos: PhysicsChunkPos) {
        if (loadingChunks.contains(pos)) return
        if (preparedChunks.any { it.pos == pos }) return

        commands += Command.AddChunk(pos)
    }

    override fun appendChunk(pos: PhysicsChunkPos, startY: Int, endY: Int, chunkSegments: ChunkSegments) {
        commands += Command.AppendChunk(pos, startY, endY, chunkSegments)
    }

    override fun removeChunk(pos: PhysicsChunkPos) {
        val chunk = chunkBank.getChunkAt(pos) ?: return
        commands += Command.RemoveChunk(chunk)
    }

    override fun addBlock(pos: BlockPos, blockShape: Shape.Box) {
        commands += Command.AddBlock(pos, blockShape)
    }

    override fun addFullSizeBlock(pos: BlockPos) {
        commands += Command.AddFullBlock(pos)
    }

    fun start() {
        thread(name = "Engine Chunk Load Modify System", isDaemon = true) {
            var chunksToAdd = 0
            var skippedChunkAddEvents = 0

            while (true) {
                val command = commands.poll() ?: continue
                when (command) {
                    is Command.AddChunk -> {
                        if (loadingChunks.contains(command.pos)) continue
                        if (preparedChunks.any { it.pos == command.pos }) continue

                        loadingChunks.add(command.pos)
                        chunksToAdd++
                    }

                    is Command.RemoveChunk -> {
                        removeChunks.add(command.chunk)
                    }

                    is Command.AddFullBlock -> {
                        val pos = command.pos
                        val physicsChunkPos = pos.getPhysicsChunkPos()
                        val localPos = ChunkPos(pos).localOf(pos.toCenterPos().engineWorldVec3())
                        addShapeTasks.add(
                            AddShapeTask(
                                chunkBank.getChunkAt(physicsChunkPos) ?: continue,
                                Transform(localPos),
                                BlockShape(),
                                pos
                            )
                        )
                    }

                    is Command.AddBlock -> {
                        val pos = command.pos
                        addShapeTasks.add(
                            AddShapeTask(
                                chunkBank.getChunkAt(pos.getPhysicsChunkPos()) ?: continue,
                                Transform(ChunkPos(pos).localOf(pos.toCenterPos().engineWorldVec3())),
                                command.shape,
                                pos
                            )
                        )
                    }

                    is Command.AppendChunk -> {
                        val pos = command.pos
                        val chunk = chunkBank.getChunkAt(pos) ?: continue
                        var minY = command.startY
                        var maxY = command.endY
                        val chunkCollider = chunk.collider
                        for (chunkPart in chunkCollider.parts) {
                            val chunkMinY = chunkPart.startY
                            val chunkMaxY = chunkPart.endY
                            if (chunkMinY <= minY) {
                                minY = chunkMinY - 1
                            }
                            if (chunkMaxY >= maxY) {
                                maxY = chunkMaxY + 1
                            }
                        }
                        // Убираем уже использованные сегменты в чанке, загружаем неиспользованные
                        val segments = command.segments.shape.subList(minY, maxY)
                        chunkShapeAppends.add(
                            AppendChunkTask(
                                chunk,
                                ChunkPart(minY, maxY, ChunkSegments(segments))
                            )
                        )
                    }

                    is Command.MultiBlockAdd -> {

                    }
                }

                // Abort подготовленных чанков, которые нужно удалить
                val toAbortChunks = preparedChunks.filter { it in removeChunks }
                if (toAbortChunks.isNotEmpty()) {
                    val toAbortChunkIds = toAbortChunks.map { it.collider.body.id }.toSet()
                    batchAdder.abort(toAbortChunkIds)

                    preparedChunks.removeAll(toAbortChunks)
                    removeChunks.removeAll(toAbortChunks)
                }

                val flushTasks = flushPendingChunkTasksSignal.getAndSet(false)
                if (flushTasks) {
                    // Подготовка батчей чанков
                    if (chunksToAdd > 0) {
                        val chunkPositions = loadingChunks
                            .toList()
                        val chunksToLoad = chunkPositions.map { bodyFactory.emptyPhysicsChunk(it) }
                        val chunkIds = chunksToLoad.map { it.collider.body.id }.toSet()

                        batchAdder.prepare(chunkIds)
                        preparedChunks.addAll(chunksToLoad)
                        loadingChunks.removeAll(chunkPositions)

                        chunksToAdd = 0
                        skippedChunkAddEvents = 0
                    } else {
                        skippedChunkAddEvents++
                    }

                    // Удаляем чанки
                    removeChunks.forEach {
                        chunkBank.removeChunk(it.pos)
                        worldBodyManager.removeChunk(it)
                    }
                    removeChunks.clear()

                    singleBlocks.forEach { chunkPos, blockList ->
                        val blocks = blockList.blocks
                        val chunk = blockList.chunk

                        if (blocks.size > 10) {
                            val partsToRebuild = mutableMapOf<ChunkPart, MutableList<SingleBlock>>()
                            blocks.forEach { block ->
                                val part = chunk.partOfY(block.pos.y) ?: return@forEach
                                partsToRebuild.computeIfAbsent(part) { mutableListOf() }.add(block)
                            }
                            partsToRebuild.forEach { part, newBlocks ->
                                val oldSegments = part.segmentsList
                                val segmentsList = oldSegments.map { segment ->
                                    val shapes = newBlocks
                                        .filter { it.pos.y == segment.y }
                                        .map { it.shape }
                                    Segment(
                                        segment.y,
                                        segment.flats,
                                        segment.shapes + shapes,
                                    )
                                }
                                val segments = ChunkSegments(Shape.SegmentCompound(segmentsList))
                                rebuildChunkTasks.add(
                                    RebuildChunkTask(
                                        chunk,
                                        part,
                                        segments,
                                        newBlocks
                                    )
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    fun afterPhysicsSimulation() {
        // Финализируем подготовленные чанки
        val snapshotPrepared = preparedChunks.toSet()
        if (snapshotPrepared.isNotEmpty()) {
            val ids = snapshotPrepared.map { it.collider.body.id }.toSet()
            batchAdder.finalize(ids)

            for (chunk in snapshotPrepared) {
                chunkBank.setChunk(chunk.pos, chunk)
            }
        }

        addShapeTasks.drain { task ->
            val chunk = task.chunk
            val chunkPos = chunk.pos
            val shape = task.shape
            val pos = task.blockPos
            val index = chunk.collider.body.addShape(shape, task.transform)
            singleBlocks
                .computeIfAbsent(chunkPos) { ChunkBlocks(chunk, mutableListOf()) }
                .blocks.add(SingleBlock(pos, index, shape, chunk))
        }

        rebuildChunkTasks.drain { task ->
            val chunk = task.chunk
            val chunkCollider = task.chunk.collider
            val body = chunkCollider.body
            task.shapesToRemove.forEach {
                body.removeShape(it.index)
            }
            chunkCollider.body.removeShape(chunk.partIndexOf(task.part))
            chunk.collider.body.addShape(task.withSegments.shape, Transform.NULL)
        }

        chunkShapeAppends.drain { task ->
            val part = task.part
            val chunkCollider = task.chunk.collider
            val pos = task.chunk.pos
            val segments = part.segmentsShape

            chunkCollider.body.addShape(segments.shape, ChunkTransform(pos))
            chunkCollider.parts += ChunkPart(part.startY, part.endY, segments)
        }
    }
}
