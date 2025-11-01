package org.qbrp.main.engine.physics.chunk

import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.ChunkPos
import net.minecraft.world.chunk.Chunk
import org.qbrp.main.core.events.Event
import org.qbrp.main.core.events.emit
import org.qbrp.main.core.fabricmc.chunks.ChunkStorage
import org.qbrp.main.core.util.math.Pos
import org.qbrp.main.core.util.math.Vec3
import org.qbrp.main.core.util.math.engine
import org.qbrp.main.core.util.math.getChunkPos
import org.qbrp.main.core.util.math.isInsideChunk
import org.qbrp.main.engine.physics.player.velocity
import org.qbrp.main.engine.physics.system.entity.Body
import org.qbrp.main.engine.physics.system.service.BodyFactory
import org.qbrp.main.engine.physics.system.service.WorldBodyManager
import org.qbrp.main.engine.players.model.pos
import org.qbrp.main.engine.players.model.service.ServerPlayerStorage
import org.qbrp.main.engine.world.location.WorldVec3
import org.qbrp.main.engine.world.location.asWorldVec3
import org.qbrp.main.network.physics.chunkSquare
import java.util.concurrent.ConcurrentLinkedQueue
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.concurrent.thread
import kotlin.math.abs

data class ChunkDirtyEvent(val chunk: PhysicsChunk) : Event {
    val pos get() = chunk.pos
}

fun WorldBodyManager.activateNearestBodies(pos: BlockPos, distance: Double) {
    nearest(pos.toCenterPos().engine(), distance).forEach { activate(it) }
}

/** # Точки
 * Система получает на вход материальные точки с позицией и вектором движения.
 * На основе этих данных решается, какие чанки следует загрузить
 * */

data class MovingPoint(val pos: WorldVec3, val velocity: WorldVec3)

interface TrackingPositions {
    val points: List<MovingPoint>
}

class PlayerTrackingPositions(
    private val playerStorage: ServerPlayerStorage,
) : TrackingPositions {
    override val points: List<MovingPoint> get() = mapPositions()

    private fun mapPositions(): List<MovingPoint> {
        return playerStorage.map { MovingPoint(it.pos.asWorldVec3(), it.velocity) }
    }
}

class ChunkSchedulerSystem(
    private val physicsChunkBank: ChunkBank,
    private val bodyFactory: BodyFactory,
    private val mcChunkStorage: ChunkStorage,
    private val positionsManagers: MutableList<TrackingPositions>
) {
    var enableLoadDebug = false
    var periodTicks = 15

    private var ticks = 0
    private var shouldTrack = AtomicBoolean()
    private val tasks: ConcurrentLinkedQueue<Runnable> = ConcurrentLinkedQueue()
    private val trackTasks: ConcurrentLinkedQueue<TrackTask> = ConcurrentLinkedQueue()
    private val loadedChunks: MutableMap<ChunkPos, LoadState> = mutableMapOf()

    data class TrackTask(val points: List<MovingPoint>)

    private data class LoadState(var loaded: Boolean, val minY: Int, val maxY: Int)

    fun startTrackThread() {
        thread(name = "Engine Physics Chunk Scheduler", isDaemon = true) {
            while (true) {
                try {
                    while (tasks.isNotEmpty()) {
                        tasks.poll().run()
                    }
                    if (!shouldTrack.get()) {
                        Thread.sleep(1)
                        continue
                    }
                    while (trackTasks.isNotEmpty()) {
                        val task = trackTasks.poll()

                        // Загружаемые чанки с позициями
                        val chunksToLoadWithPositions = mutableMapOf<Chunk, MutableList<Pos>>()
                        val chunks = mcChunkStorage.chunks
                        for (chunk in chunks) {
                            val chunkPos = chunk.pos
                            chunksToLoadWithPositions.getOrPut(chunk) { mutableListOf() }
                            for (point in task.points) {
                                val startPos = point.pos
                                val endPos = startPos.add(point.velocity.mul(periodTicks))

                                var pos: Vec3 = startPos
                                while (pos.squaredDistanceTo(endPos) > 1.0) {
                                    if (pos.isInsideChunk(chunk)) {
                                        val start = ChunkPos(chunkPos.x - 1, chunkPos.z - 1)
                                        val square = chunkSquare(start, 3)
                                        val chunks = square.mapNotNull { mcChunkStorage.getChunk(it) }
                                        chunks.forEach {
                                            chunksToLoadWithPositions.getOrPut(it) { mutableListOf() }.add(pos)
                                        }
                                    }
                                    val direction = endPos
                                        .sub(pos)
                                        .clampMax(16.0)
                                    pos = pos.add(direction)
                                }
                            }
                        }

                        for (entry in chunksToLoadWithPositions) {
                            val poses = entry.value
                            val chunk = entry.key
                            val chunkPos = chunk.pos
                            val loadedChunk = loadedChunks[chunkPos]

                            if (poses.isNotEmpty()) {
                                val loadSettings = getLoadSettings(poses.map { it.y })
                                if (loadedChunk != null && !loadedChunk.isNotActual(loadSettings) && loadedChunk.loaded) continue
                                loadServerChunk(chunk, loadSettings)
                                loadedChunks[chunkPos] = loadStateOf(loadSettings)
                            } else if (loadedChunk != null) {
                                physicsChunkBank.unloadServerChunk(chunk)
                                loadedChunks.remove(chunkPos)
                            }
                        }
                        shouldTrack.set(false)
                    }
                } catch (e: Throwable) {
                    e.printStackTrace()
                }
            }
        }
    }

    private fun loadServerChunk(chunk: Chunk, settings: ChunkLoadSettings) {
        try {
            physicsChunkBank.loadServerChunk(chunk, mcChunkStorage.world, settings, bodyFactory, enableLoadDebug)
        } catch (e: Throwable) {
            e.printStackTrace()
        }
    }

    private fun fetchLoadState(pos: ChunkPos): LoadState {
        return loadedChunks.getOrPut(pos) { LoadState(false, 0, 0) }
    }

    private fun loadStateOf(settings: ChunkLoadSettings, loaded: Boolean = true): LoadState {
        return LoadState(loaded, settings.minHeight, settings.maxHeight)
    }

    private fun LoadState.isNotActual(settings: ChunkLoadSettings): Boolean {
        val (minY, maxY) = settings.minHeight to settings.maxHeight
        return abs(this.minY - minY) > NOT_ACTUAL_DIFF || abs(this.maxY - maxY) > NOT_ACTUAL_DIFF
    }

    fun addPositionTracker(positionTracker: TrackingPositions) {
        positionsManagers.add(positionTracker)
    }

    fun handleAddedBody(body: Body) {
        val pos = body.transform.pos
        val chunkPos = pos.getChunkPos()
        val mcChunk = mcChunkStorage.getChunk(chunkPos)
        if (physicsChunkBank.getChunkAt(pos.getChunkPos().asPhysics()) == null && mcChunk != null) {
            body.freeze()
            tasks.add {
                try {
                    loadServerChunk(mcChunk, getLoadSettings(listOf(pos.y)))
                } finally {
                    body.unfreeze()
                }
            }
        }
    }

    fun markDirty(pos: Pos) = tasks.add {
        val chunkPos = pos.getChunkPos()
        fetchLoadState(chunkPos).loaded = false
        val chunk = physicsChunkBank.getChunkAt(chunkPos.asPhysics())
        if (chunk != null) {
            ChunkDirtyEvent(chunk).emit()
        }
    }

    fun trackAndUpsert() {
        trackTasks += TrackTask(positionsManagers.flatMap { it.points })
        shouldTrack.set(true)
    }

    fun tickTimer() {
        if (ticks++ >= periodTicks) {
            trackAndUpsert()
            ticks = 0
        }
    }

    private fun getLoadSettings(y: List<Double>): ChunkLoadSettings {
        val heights = y.map {
            it.toInt().coerceIn(ChunkConstants.WORLD_MIN_HEIGHT, ChunkConstants.WORLD_MAX_HEIGHT)
        }

        val rawMin = heights.minOrNull() ?: ChunkConstants.WORLD_MIN_HEIGHT
        val rawMax = heights.maxOrNull() ?: ChunkConstants.WORLD_MAX_HEIGHT

        val min = (rawMin - 12).coerceAtLeast(ChunkConstants.WORLD_MIN_HEIGHT)
        val max = (rawMax + 12).coerceAtMost(ChunkConstants.WORLD_MAX_HEIGHT)

        return ChunkLoadSettings(minHeight = min, maxHeight = max, BlockSettings.DEFAULT)
    }

    companion object {
        const val NOT_ACTUAL_DIFF = 4
    }
}