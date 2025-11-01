package org.qbrp.main.engine.physics.system.jolt

import com.github.stephengold.joltjni.AllHitRayCastBodyCollector
import com.github.stephengold.joltjni.BodyIdArray
import com.github.stephengold.joltjni.Character
import com.github.stephengold.joltjni.JobSystem
import com.github.stephengold.joltjni.PhysicsSystem
import com.github.stephengold.joltjni.RayCast
import com.github.stephengold.joltjni.TempAllocator
import com.github.stephengold.joltjni.enumerate.EActivation
import com.github.stephengold.joltjni.enumerate.EBodyType
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.job
import kotlinx.coroutines.launch
import kotlinx.coroutines.newSingleThreadContext
import org.qbrp.main.core.events.emit
import org.qbrp.main.core.fabricmc.server.LogicThread
import org.qbrp.main.core.util.log.LoggerUtil
import org.qbrp.main.core.util.math.Pos
import org.qbrp.main.core.util.math.asVec3
import org.qbrp.main.engine.physics.system.entity.Body
import org.qbrp.main.engine.physics.system.entity.PhysicalCharacter
import org.qbrp.main.engine.physics.system.PhysicsSystemEvents
import org.qbrp.main.engine.physics.system.entity.BodyId
import org.qbrp.main.engine.physics.system.entity.Transform
import org.qbrp.main.engine.physics.system.service.BodyBatchAddTask
import org.qbrp.main.engine.physics.system.service.BodyBatchAdder
import org.qbrp.main.engine.physics.system.service.PhysicsSimulation
import org.qbrp.main.engine.physics.system.service.WorldBodyManager
import java.util.concurrent.ConcurrentLinkedQueue
import kotlin.collections.plusAssign
import kotlin.properties.Delegates

class JoltPhysicsSimulation(
    private val system: PhysicsSystem,
    private val tempAllocator: TempAllocator,
    private val jobSystem: JobSystem,
    private val mainThread: LogicThread,
) : WorldBodyManager, PhysicsSimulation, BodyBatchAdder {
    private val joltBodyInterface
        get() = system.bodyInterface

    private val logger = LoggerUtil.get("physics")
    private var startTicking by Delegates.notNull<Long>()
    private var tick = 0
    private var bodiesEditable = true

    @OptIn(DelicateCoroutinesApi::class, ExperimentalCoroutinesApi::class)
    private val physicsDispatcher = newSingleThreadContext("PhysicsThread")

    @OptIn(ExperimentalCoroutinesApi::class)
    private var coroutineScope = CoroutineScope(physicsDispatcher + SupervisorJob())

    private var inspectCounter = 0

    private val taskQueue = ConcurrentLinkedQueue<Runnable>()
    private val bodiesById: MutableMap<BodyId, Body> = mutableMapOf()
    private val characters: MutableList<PhysicalCharacter> = mutableListOf()

    override var debugLog: Boolean = false
    override var isRunning: Boolean = false

    override val bodies: MutableList<Body> = mutableListOf<Body>()
    override val simulation: PhysicsSimulation = this

    private fun log() {
        val now = System.currentTimeMillis()
        val elapsedMs = now - startTicking
        val tps = if (elapsedMs > 0) ((tick * 1000) / elapsedMs).toInt() else 0

        logger.log(
            "Количество тел: ${system.numBodies}, активных: ${
                system.getNumActiveBodies(EBodyType.RigidBody) +
                        system.getNumActiveBodies(EBodyType.SoftBody)
            }"
        )
        logger.log("Трансформы: ${bodies.map { it.transform.pos }}")
        logger.log("TPS: $tps")
    }

    override fun step(delta: Float, callback: () -> Unit): Job = coroutineScope.launch {
        val startTime = System.currentTimeMillis()
        tick++
        bodiesEditable = false
        flushPendingTasks()
        system.update(delta, 1, tempAllocator, jobSystem)
        characters.forEach { (it as JoltCharacter).jolt.postSimulation(0.1f) }
        if (inspectCounter++ > 40) {
            if (debugLog) log()
            startTicking = System.currentTimeMillis()
            tick = 0
            inspectCounter = 0
        }
        bodiesEditable = true
        try {
            callback()
            PhysicsSystemEvents.Tick.emit()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        val endTime = System.currentTimeMillis()
        val totalTime = endTime - startTime
    }.job

    private fun getJoltBody(body: Body): com.github.stephengold.joltjni.Body {
        if (body is JoltBody) return body.joltBody else throw NotImplementedError()
    }

    private fun getJoltCharacter(character: PhysicalCharacter): Character {
        if (character is JoltCharacter) return character.jolt else throw NotImplementedError()
    }

    private fun flushPendingTasks() {
        while (taskQueue.isNotEmpty()) {
            val task = taskQueue.poll()
            task.run()
        }
    }

    override fun addCharacter(character: PhysicalCharacter) {
        taskQueue.add {
            getJoltCharacter(character).addToPhysicsSystem()
            characters += character
        }
    }

    override fun removeCharacter(character: PhysicalCharacter) {
        taskQueue.add {
            val characterDeleted = characters.remove(character)
            if (characterDeleted) getJoltCharacter(character).removeFromPhysicsSystem()
        }
    }

    override fun removeCharacters() {
        characters.forEach { removeCharacter(it) }
    }

    override fun destroyBody(id: BodyId, broadcastEvent: Boolean) {
        taskQueue.add {
            val body = bodiesById[id]
            if (body == null) return@add
            val jBody = getJoltBody(body)
            PhysicsSystemEvents.BodyRemove(body, mainThread).emit()
            bodies.remove(body)
            bodiesById.remove(body.id)
            system.bodyInterface.removeBody(jBody.id)
            system.bodyInterface.destroyBody(jBody.id)
        }
    }

    override fun addBody(body: Body, activate: Boolean, broadcastEvent: Boolean, callback: () -> Unit) {
        taskQueue.add {
            if (bodies.contains(body)) return@add
            system.bodyInterface.addBody(getJoltBody(body), if (activate) EActivation.Activate else EActivation.DontActivate)
            bodies.add(body)
            bodiesById[body.id] = body
            if (broadcastEvent) PhysicsSystemEvents.BodyAdd(body, mainThread).emit()
            callback()
        }
    }

    override fun destroyBody(body: Body, broadcastEvent: Boolean) {
        destroyBody(body.id)
    }

    override fun activate(body: Body) {
        taskQueue.add {
            system.bodyInterface.activateBody(body.id.value)
        }
    }

    override fun getBody(id: BodyId): Body? {
        return bodiesById[id]
    }

    override fun setTransform(id: BodyId, transform: Transform) {
        taskQueue.add {
            system.bodyInterface.setPositionAndRotation(
                id.value,
                transform.pos.asVec3().joltR(),
                transform.rot.jolt(),
                EActivation.Activate
            )
        }
    }

    inner class JoltBodyBatchAddTask(val ids: Set<BodyId>) : BodyBatchAddTask {
        private val array = ids.toArray()
        private val numBodies = ids.size

        override fun prepare() {
            joltBodyInterface.addBodiesPrepare(array, numBodies)
        }

        override fun finalize() {
            joltBodyInterface.addBodiesFinalize(array, numBodies)
        }

        override fun abort() {
            joltBodyInterface.addBodiesAbort()
        }
    }

    private fun Set<BodyId>.toArray(): BodyIdArray {
        val array = BodyIdArray(size)
        forEachIndexed { i, elem -> array.set(i, elem.value) }
        return array
    }

    override fun task(ids: Set<BodyId>): BodyBatchAddTask {
        return JoltBodyBatchAddTask(ids)
    }

    override fun raycast(start: Pos, end: Pos): List<Body> {
        val startVec = start.asVec3()
        val origin = startVec.jolt()
        val offset = end.asVec3().sub(startVec).jolt()

        val raycast = RayCast(origin, offset)
        val collector = AllHitRayCastBodyCollector()
        system.broadPhaseQuery.castRay(raycast, collector)
        val bodies: MutableList<Body> = mutableListOf()
        for (hit in collector.hits) {
            bodies += bodiesById[BodyId(hit.bodyId)]!!
        }
        return bodies
    }

    override fun isBodiesEditable(): Boolean {
        return bodiesEditable
    }

    override fun isOnThread(): Boolean {
        return Thread.currentThread().name == "PhysicsThread"
    }
}