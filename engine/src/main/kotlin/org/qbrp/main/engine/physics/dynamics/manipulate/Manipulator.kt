package org.qbrp.main.engine.physics.dynamics.manipulate

import org.qbrp.main.engine.physics.dynamics.dataformats.Body

interface Manipulator {
    fun pin(body: Body, target: ManipulationTarget)
    fun unpin(body: Body)
}