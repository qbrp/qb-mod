package org.qbrp.main.network.physics

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import org.koin.core.component.inject
import org.qbrp.main.core.events.on
import org.qbrp.main.core.fabricmc.player.PlayerReference
import org.qbrp.main.core.util.math.Pos
import org.qbrp.main.core.util.math.asVec3
import org.qbrp.main.core.util.math.cloneAsVec3
import org.qbrp.main.engine.physics.system.PhysicsSystemEvents
import org.qbrp.main.engine.physics.system.entity.Body
import org.qbrp.main.engine.physics.system.entity.BodyId
import org.qbrp.main.engine.physics.system.entity.Shape
import org.qbrp.main.engine.physics.system.entity.Transform
import org.qbrp.main.engine.physics.system.service.everyPhysicsTick
import org.qbrp.main.engine.physics.chunk.PhysicsChunk
import org.qbrp.main.engine.physics.chunk.PhysicsChunkManager
import org.qbrp.main.engine.physics.chunk.PhysicsChunkPos
import org.qbrp.main.engine.physics.chunk.asPhysics
import org.qbrp.main.engine.players.model.PlayerDisconnectEvent
import org.qbrp.main.engine.players.model.ServerPlayerObject
import org.qbrp.main.engine.players.model.PlayerInstantiateEvent
import org.qbrp.main.engine.players.model.pos
import org.qbrp.main.engine.players.model.service.PlayerList
import org.qbrp.main.engine.players.model.service.ServerPlayerList
import java.util.concurrent.ArrayBlockingQueue
import kotlin.collections.set
import kotlin.concurrent.thread

internal data class BodyStatic(
    val id: BodyId,
    val shape: Shape,
    val mass: Double
)

internal data class BodySnapshot(
    val id: BodyId,
    val transform: Transform,
    val shape: Shape,
    val mass: Double
)

private data class NetFrame(
    val snapshots: List<BodySnapshot>,
    val destroys: List<DestroyCommand>,
    val propagates: List<PropagateCommand>,
    val dirty: List<ChunkDirtyCommand>
)

private data class DestroyCommand(val id: BodyId, val pos: Pos)

private data class PropagateCommand(val player: PlayerReference, val pos: Pos)

private data class ChunkDirtyCommand(val pos: PhysicsChunkPos)

private data class PlayerChunkLoading(
    val chunks: MutableMap<PhysicsChunkPos, ChunkLoadingState> = mutableMapOf()
) : MutableMap<PhysicsChunkPos, ChunkLoadingState> by chunks

private data class ChunkLoadingState(val chunk: PhysicsChunk, var sent: Boolean) {
    fun isDifferent(chunk: PhysicsChunk): Boolean {
        return this.chunk.minY != chunk.minY || this.chunk.maxY != chunk.maxY
    }
}

fun KoinComponent.runPhysicsNetworkHandler() {
    val netFrames = ArrayBlockingQueue<NetFrame>(2)
    val bodiesById = mutableMapOf<BodyId, Body>()
    val staticsById = mutableMapOf<BodyId, BodyStatic>()
    val destroyCommands = mutableListOf<DestroyCommand>()
    val propagateCommands = mutableListOf<PropagateCommand>()
    val chunkDirtyCommand = mutableListOf<ChunkDirtyCommand>()
    val chunkManager by inject<PhysicsChunkManager>()
    val playerLoadedChunks: MutableMap<PlayerReference, PlayerChunkLoading> = mutableMapOf()

    on<PhysicsSystemEvents.BodyAdd> {
        bodiesById[body.id] = body
        staticsById[body.id] = BodyStatic(body.id, body.shape, body.mass)
    }

    on<PhysicsSystemEvents.BodyRemove> {
        if (body in chunkManager.chunkBodies) return@on
        destroyCommands += DestroyCommand(body.id, body.transform.pos)
        bodiesById.remove(body.id)
        staticsById.remove(body.id)
    }

    on<ChunkDirtyEvent>() {
        chunkDirtyCommand += ChunkDirtyCommand(pos)
    }

    on<PlayerInstantiateEvent>() {
        propagateCommands += PropagateCommand(player, player.pos)
    }

    on<PlayerDisconnectEvent>() {
        playerLoadedChunks.remove(player)
    }

    thread(name = "Engine Physics Synchronization", isDaemon = true) {
        setupPhysicsSyncThread(
            netFrames,
            get(),
            playerLoadedChunks,
            chunkManager
        )
    }

    everyPhysicsTick {
        val snapshots = bodiesById.values.mapNotNull { b ->
            val s = staticsById[b.id] ?: return@mapNotNull null
            BodySnapshot(b.id, b.transform, s.shape, s.mass)
        }

        val frame = NetFrame(
            snapshots,
            drain(destroyCommands),
            drain(propagateCommands),
            drain(chunkDirtyCommand),
        )
        if (!netFrames.offer(frame)) {
            netFrames.poll()
            netFrames.offer(frame)
        }
    }
}

private fun setupPhysicsSyncThread(
    frames: ArrayBlockingQueue<NetFrame>,
    playerList: ServerPlayerList,
    playerLoadedChunks: MutableMap<PlayerReference, PlayerChunkLoading>,
    chunkManager: PhysicsChunkManager
) {
    val coroutineScope = CoroutineScope(Dispatchers.IO)
    var counter = 0
    while (true) {
        val frame = frames.take()
        val playersNow = playerList.toList()

        frame.dirty.forEach { dirtyChunkPos ->
            playerLoadedChunks.forEach { _, chunkMap ->
                chunkMap.values
                    .filter { it.chunk.pos == dirtyChunkPos }
                    .forEach { it.sent = false }
            }
        }

        val bodies = frame.snapshots
            .filter { !chunkManager.chunkBodies.any { ch -> ch.id == it.id } }
        val nearby = nearestBodiesToPlayersSnapshot(bodies, playersNow)

        val transforms = nearby.mapToBodyTransformsSnapshot()
        var bodiesData = emptyMap<ServerPlayerObject, List<BodyData>>()

        for (player in playersNow) {
            val nearestChunks = nearestPlayerChunks(player)
                .map { it.asPhysics() }
            val loadedChunks = playerLoadedChunks.getOrPut(player) { PlayerChunkLoading() }
            //println("Чанки: ${loadedChunks.values.map { it.chunk.pos }}")
            for (chunkPos in nearestChunks) {
                val chunk = chunkManager.getChunkAt(chunkPos) ?: continue

                var state = loadedChunks[chunkPos]
                if (state == null || state.isDifferent(chunk)) {
                    state = ChunkLoadingState(chunk, false)
                    loadedChunks[chunkPos] = state
                }

                if (!state.sent) {
                    state.sent = true
                    coroutineScope.launch {
                        player.taskPhysicsChunkPacket(chunk)
                            .queue()
                            .withAcknowledge()
                            .onTimeout { state.sent = false }
                            .run()
                    }
                }
            }
        }

        if (counter++ >= 20) {
            bodiesData = nearby.mapToBodyDataSnapshot { BodyType.OTHER }
            counter = 0
        }

        transforms.sendTransformPackets()
        bodiesData.sendDataPackets()

        frame.destroys.forEach { destroy ->
            playersNow
                .filter { it.pos.cloneAsVec3(y = destroy.pos.y).squaredDistanceTo(destroy.pos.asVec3()) < 42.0 * 42.2 }
                .broadcastBodyDestroyPacket(destroy.id)
        }
    }
}

private fun <T : Any> drain(list: MutableList<T>): List<T> {
    return list.toList()
        .also { list.clear() }
}

val PhysicsChunkManager.chunkBodies get() = chunks.mapNotNull { it.collider.body }