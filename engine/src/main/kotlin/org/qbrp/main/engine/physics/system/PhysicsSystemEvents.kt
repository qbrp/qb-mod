package org.qbrp.main.engine.physics.system

import org.qbrp.main.core.events.Event
import org.qbrp.main.core.fabricmc.server.LogicThread
import org.qbrp.main.engine.physics.system.entity.Body

object PhysicsSystemEvents {
    sealed class PhysicEvent(val mainThread: LogicThread) : Event {
        fun onMainThread(r: Runnable) {
            mainThread.execute(r)
        }
    }

    class BodyAdd(val body: Body, thread: LogicThread) : PhysicEvent(thread)
    class BodyRemove(val body: Body, thread: LogicThread) : PhysicEvent(thread)
    object Tick : Event
}