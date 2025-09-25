package org.qbrp.main.engine.physics.model.params

import org.qbrp.main.core.components.AnyComponent

fun Iterable<AnyComponent>.contributeMass(): Double {
    return filterIsInstance<MassContributor>().sumOf { it.calculateMass() }
}