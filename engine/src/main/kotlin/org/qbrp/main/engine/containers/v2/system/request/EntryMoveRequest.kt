package org.qbrp.main.engine.containers.v2.system.request

import org.qbrp.main.engine.containers.util.ContainerId
import org.qbrp.main.engine.containers.v2.entity.ContainerEntry
import org.qbrp.main.engine.containers.v2.system.TransferResult
import org.qbrp.main.engine.world.location.Location

typealias MoveCallback = (TransferResult) -> Unit

data class EntryMove(
    val entry: ContainerEntry,
    val destination: Destination,
    val mode: MoveMode,
    val callback: MoveCallback,
    val required: Boolean
)

sealed class Destination {
    object Void : Destination()
    data class Container(val id: ContainerId): Destination()
    data class World(val location: Location): Destination()
}

sealed class MoveMode() {
    object Transfer : MoveMode()
    class Copy(val prototype: ContainerEntryPrototype) : MoveMode()
}