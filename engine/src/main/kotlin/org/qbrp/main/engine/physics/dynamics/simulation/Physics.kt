package org.qbrp.main.engine.physics.dynamics.simulation

import com.github.stephengold.joltjni.AllHitRayCastBodyCollector
import com.github.stephengold.joltjni.JobSystem
import com.github.stephengold.joltjni.PhysicsSystem
import com.github.stephengold.joltjni.RayCast
import com.github.stephengold.joltjni.RayCastBodyCollector
import com.github.stephengold.joltjni.TempAllocator
import com.github.stephengold.joltjni.Vec3
import com.github.stephengold.joltjni.enumerate.EActivation
import com.github.stephengold.joltjni.enumerate.EBodyType
import net.minecraft.server.MinecraftServer
import org.qbrp.main.core.utils.log.LoggerUtil
import org.qbrp.main.engine.physics.dynamics.dataformats.Body
import org.qbrp.main.engine.world.location.Position
import org.qbrp.main.engine.physics.dynamics.dataformats.Vec3Adapter
import org.qbrp.main.engine.physics.dynamics.dataformats.jolt
import org.qbrp.main.engine.physics.dynamics.events.BodyEvents
import org.qbrp.main.engine.physics.dynamics.manipulate.ManipulatorImpl
import java.util.concurrent.ArrayBlockingQueue
import com.github.stephengold.joltjni.Body as JoltBody
import java.util.concurrent.ConcurrentLinkedQueue
import kotlin.concurrent.thread
import kotlin.properties.Delegates

class Physics(
    private val bodyFactory: BodyFactory,
    private val system: PhysicsSystem,
    private val tempAllocator: TempAllocator,
    private val jobSystem: JobSystem,
    private val server: MinecraftServer,
    private val manipulatorImpl: ManipulatorImpl
) : WorldBodyManager, PhysicsSimulation {
    private val taskQueue = ConcurrentLinkedQueue<Runnable>()
    private val stepSignal = ArrayBlockingQueue<Float>(100)
    private val logger = LoggerUtil.get("physics")
    private var startTicking by Delegates.notNull<Long>()
    private var tick = 0
    override var debugLog: Boolean = false

    override val bodies: MutableList<Body> = mutableListOf<Body>()

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

    fun run() {
        startTicking = System.currentTimeMillis()
        thread(name = "PhysicsThread", isDaemon = true) {
            var inspectCounter = 0
            while (true) {
                val delta = stepSignal.take()
                tick++
                flushPendingTasks()
                system.update(delta, 1, tempAllocator, jobSystem)
                if (inspectCounter++ > 40) {
                    if (debugLog) log()
                    startTicking = System.currentTimeMillis()
                    tick = 0
                    inspectCounter = 0
                }
                manipulatorImpl.onStep()
            }
        }
    }

    override fun step(delta: Float) {
        stepSignal.put(delta)
    }

    private fun getJoltBody(body: Body): JoltBody {
        if (body is LoadedJoltBody) return body.joltBody else throw NotImplementedError()
    }

    private fun flushPendingTasks() {
        while (taskQueue.isNotEmpty()) {
            val task = taskQueue.poll()
            task.run()
        }
    }

    override fun addBody(body: Body, activate: Boolean, broadcastEvent: Boolean) {
        taskQueue.add {
            system.bodyInterface.addBody(getJoltBody(body), if (activate) EActivation.Activate else EActivation.DontActivate)
            bodies.add(body)
            if (broadcastEvent) server.execute { BodyEvents.ADDED.invoker().onBodyAdded(body, this) }
        }
    }

    override fun destroyBody(body: Body, broadcastEvent: Boolean) {
        taskQueue.add {
            val jBody = getJoltBody(body)
            system.bodyInterface.removeBody(jBody.id)
            system.bodyInterface.destroyBody(jBody.id)
            bodies.remove(body)
            if (broadcastEvent) server.execute { BodyEvents.REMOVED.invoker().onBodyRemoved(body, this) }
        }
    }

    override fun activate(body: Body) {
        taskQueue.add {
            system.bodyInterface.activateBody(body.id)
        }
    }

    override fun raycast(
        start: Position,
        end: Position,
    ): List<Body> {
        val origin = start.jolt()
        val offset = end.sub(start).jolt()

        val raycast = RayCast(origin, offset)
        val collector =  AllHitRayCastBodyCollector()
        system.broadPhaseQuery.castRay(raycast, collector)
        val bodies: MutableList<Body> = mutableListOf()
        for (hit in collector.hits) {
            bodies += getBody(hit.bodyId)
        }
        return bodies
    }

    private fun getBody(id: Int): Body {
        return bodies.find { it.id == id }!!
    }
}