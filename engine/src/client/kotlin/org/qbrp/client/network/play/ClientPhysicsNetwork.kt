package org.qbrp.client.network.play

import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import org.qbrp.client.engine.movement.PlayerPositionLookPacketEvent
import org.qbrp.client.engine.physics.cleanFarChunks
import org.qbrp.client.engine.physics.loadChunkFromPacketData
import org.qbrp.client.engine.players.ClientPlayerManager
import org.qbrp.client.engine.players.ClientPlayerObject
import org.qbrp.client.network.transport.registerClientReceiver
import org.qbrp.main.core.components.extensions.require
import org.qbrp.main.core.events.on
import org.qbrp.main.core.util.math.Pos
import org.qbrp.main.core.util.math.Vec3
import org.qbrp.main.core.util.math.angleBetween
import org.qbrp.main.core.util.math.getChunkPos
import org.qbrp.main.engine.physics.chunk.ChunkBank
import org.qbrp.main.engine.physics.system.PhysicsSystemEvents
import org.qbrp.main.engine.physics.system.entity.BodyDescriptor
import org.qbrp.main.engine.physics.system.entity.BodyId
import org.qbrp.main.engine.physics.system.entity.PhysicalCharacter
import org.qbrp.main.engine.physics.system.entity.Shape
import org.qbrp.main.engine.physics.system.entity.Transform
import org.qbrp.main.engine.physics.system.service.BodyFactory
import org.qbrp.main.engine.physics.system.service.WorldBodyManager
import org.qbrp.main.engine.physics.system.service.createWorldGround
import org.qbrp.main.engine.physics.system.service.everyPhysicsTick
import org.qbrp.main.engine.players.model.pos
import org.qbrp.main.network.physics.BodyType
import org.qbrp.main.network.physics.ChunkData
import org.qbrp.main.network.physics.PHYSICS_BODY_ENDPOINT
import org.qbrp.main.network.physics.PHYSICS_CHUNK_ENDPOINT
import org.qbrp.main.network.physics.PHYSICS_DESTROY_BODY_ENDPOINT
import org.qbrp.main.network.physics.PHYSICS_TRANSFORM_ENDPOINT
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentLinkedQueue
import kotlin.collections.component1
import kotlin.collections.component2
import kotlin.collections.iterator
import kotlin.concurrent.thread

fun KoinComponent.runClientPhysicsNetworkHandler(
    handler: ClientPhysicsNetworkHandler,
    bodyIdTable: BodyIdTable
) {
    handler.runCleanerThread()

    everyPhysicsTick {
        handler.tickPhysics()
    }

    PHYSICS_BODY_ENDPOINT
        .registerClientReceiver { ctx ->
            bodies.forEach {
                handler.onBody(
                    BodyServerId(it.id.value),
                    it.transform,
                    it.shape,
                    it.mass,
                    it.type
                )
            }
        }

    PHYSICS_TRANSFORM_ENDPOINT
        .registerClientReceiver { ctx ->
            transforms.forEach { handler.onTransform(BodyServerId(it.id.value), it.transform) }
        }

    PHYSICS_DESTROY_BODY_ENDPOINT
        .registerClientReceiver { handler.onDestroy(BodyServerId(id.value)) }

    PHYSICS_CHUNK_ENDPOINT
        .registerClientReceiver { handler.onChunk(chunk) }

    on<PhysicsSystemEvents.BodyRemove>() {
        bodyIdTable.remove(body.id)
    }
}

class ClientPhysicsNetworkHandler(
    private val bodyManager: WorldBodyManager,
    private val bodyFactory: BodyFactory,
    private val chunkBank: ChunkBank,
    private val playerManager: ClientPlayerManager,
    private val bodyIdTable: BodyIdTable = BodyIdTable()
) {
    private val bodyTimeouts: ConcurrentHashMap<BodyServerId, Long> = ConcurrentHashMap()
    private val commands: ConcurrentLinkedQueue<Command> = ConcurrentLinkedQueue()
    private var cleanerThreadRunning = false
    private var cleanerThreadEnabled = true
    private val player
        get() = playerManager.clientPlayer

    private sealed class Command {
        data class CreateBody(
            val id: BodyServerId,
            val transform: Transform,
            val shape: Shape,
            val mass: Double,
            val type: BodyType
        ) : Command()

        data class DestroyBody(
            val id: BodyServerId
        ) : Command()

        data class TransformBody(
            val id: BodyServerId,
            val transform: Transform
        ) : Command()

        data class LoadChunk(
            val chunk: ChunkData
        ) : Command()
    }

    fun disableCleanerThread() {
        cleanerThreadEnabled = false
    }

    fun enableCleanerThread() {
        cleanerThreadEnabled = true
    }

    fun runCleanerThread() {
        if (cleanerThreadRunning) error("Cleaner thread already set upped")
        thread(name = "Engine Physics Garbage Collector", isDaemon = true) {
            while (true) {
                if (!cleanerThreadEnabled) continue
                val now = System.currentTimeMillis()
                for ((id, timeout) in bodyTimeouts) {
                    if (timeout <= now) queueDestroyCommand(id)
                }
                Thread.sleep(1000)
            }
        }
    }

    fun deferTimeout(id: BodyServerId) {
        bodyTimeouts[id] = System.currentTimeMillis() + TIMEOUT
    }

    fun tickPhysics() {
        handlePackets()
        val pos = player?.pos ?: return
        chunkBank.cleanFarChunks(pos.getChunkPos(), 6)
    }

    fun handlePackets() {
        while (commands.isNotEmpty()) {
            val command = commands.poll()
            try {
                processCommand(command)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun processCommand(command: Command) {
        when (command) {
            is Command.CreateBody -> {
                val bodyData = command
                val serverId = bodyData.id
                val transform = bodyData.transform
                val bodyShape = bodyData.shape

                val clientId = bodyIdTable.getClientIdOrNull(serverId)
                val existingBody = clientId?.let { bodyManager.getBody(it) }

                val shouldRecreate = existingBody == null || existingBody.shape != bodyShape

                if (shouldRecreate) {
                    if (existingBody != null) {
                        bodyManager.destroyBody(clientId)
                    }

                    val newBody = bodyFactory.build(
                        BodyDescriptor(
                            bodyShape,
                            transform,
                            bodyData.mass
                        )
                    )

                    bodyIdTable.set(serverId, newBody.id)
                    bodyManager.addBody(newBody, true)
                }

                deferTimeout(serverId)
            }

            is Command.DestroyBody -> {
                val serverId = command.id
                val clientId = bodyIdTable.getClientIdOrNull(serverId)
                if (clientId == null) return
                bodyManager.destroyBody(clientId)
            }

            is Command.TransformBody -> {
                val clientId = bodyIdTable.getClientIdOrNull(command.id)
                if (clientId == null) return
                val newTransform = command.transform
                val currentTransform = bodyManager.getBody(clientId)?.transform
                if (currentTransform == null) return

                val posDiff = (newTransform.posVector.sub(currentTransform.posVector)).length()
                val rotDiff = angleBetween(newTransform.rot, currentTransform.rot)

                val positionEpsilon = 0.005f
                val rotationEpsilon = 0.005f

                if (posDiff < positionEpsilon && rotDiff < rotationEpsilon) {
                    return
                }

                bodyManager.setTransform(clientId, newTransform)
                //println("Изменен трансформ тела ${c.id} на ${c.transform.pos}")
                deferTimeout(command.id)
            }

            is Command.LoadChunk -> {
                chunkBank.loadChunkFromPacketData(command.chunk, bodyFactory)
            }
        }
    }

    fun init(p: ClientPlayerObject) {
        bodyManager.createWorldGround(bodyFactory)
        enableCleanerThread()
    }

    fun invalidate() {
        disableCleanerThread()
        bodyManager.invalidate()
        bodyTimeouts.clear()
        commands.clear()
    }

    fun onBody(id: BodyServerId, transform: Transform, shape: Shape, mass: Double, type: BodyType) {
        commands += Command.CreateBody(id, transform, shape, mass, type)
    }

    fun onDestroy(id: BodyServerId) {
        queueDestroyCommand(id)
    }

    fun onTransform(id: BodyServerId, transform: Transform) {
        commands += Command.TransformBody(id, transform)
    }

    fun onChunk(chunk: ChunkData) {
        commands += Command.LoadChunk(chunk)
    }

    fun queueDestroyCommand(id: BodyServerId) {
        commands += Command.DestroyBody(id)
    }

    companion object {
        const val TIMEOUT = 10 * 60 * 1000
    }
}

@JvmInline
value class BodyServerId(val value: Int)

class BodyIdTable {
    private val serverIdToClient: MutableMap<BodyServerId, BodyId> = mutableMapOf()
    private val clientIdToServer: MutableMap<BodyId, BodyServerId> = mutableMapOf()

    fun set(serverId: BodyServerId, clientId: BodyId) {
        serverIdToClient[serverId] = clientId
        clientIdToServer[clientId] = serverId
    }

    fun getServerId(clientId: BodyId) = clientIdToServer[clientId] ?: error("Запись не существует")

    fun getClientId(serverId: BodyServerId) = getClientIdOrNull(serverId) ?: error("Запись не существует")

    fun getClientIdOrNull(serverId: BodyServerId) = serverIdToClient[serverId]

    fun remove(clientId: BodyId) {
        val serverId = clientIdToServer.remove(clientId)
        if (serverId != null) serverIdToClient.remove(serverId)
    }
}