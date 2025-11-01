package org.qbrp.main.engine.chemistry.params

import org.qbrp.main.core.components.AnyComponent

fun Iterable<AnyComponent>.contributeMass(): Double {
    return filterIsInstance<MassContributor>().sumOf { it.calculateMass() }
}