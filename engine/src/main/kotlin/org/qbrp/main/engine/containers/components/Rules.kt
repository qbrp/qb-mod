package org.qbrp.main.engine.containers.components

import org.qbrp.main.engine.containers.entity.ContainerEntry
import org.qbrp.main.engine.containers.entity.Occupancy

fun interface AcceptResolver {
    fun canAccept(entry: ContainerEntry, cap: Capacity, occ: Occupancy): Boolean
}

class Rules(
    val canAccept: AcceptResolver = AcceptResolver { _, _, _ -> true }
)