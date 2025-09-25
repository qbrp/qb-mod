package org.qbrp.main.engine.containers.v2.system

import org.qbrp.main.engine.containers.util.ContainerId
import org.qbrp.main.engine.containers.v2.components.Capacity
import org.qbrp.main.engine.containers.v2.components.volume
import org.qbrp.main.engine.containers.v2.entity.ContainerEntry
import org.qbrp.main.engine.containers.v2.entity.Occupancy
import org.qbrp.main.engine.containers.v2.entity.PhysicalEntry
import org.qbrp.main.engine.containers.v2.system.request.Destination
import org.qbrp.main.engine.containers.v2.system.request.EntryMove
import org.qbrp.main.engine.containers.v2.system.request.MoveMode
import java.util.Queue
import kotlin.collections.component1
import kotlin.collections.component2
import kotlin.collections.get
import kotlin.collections.iterator

class TransferSystem(private val buffer: ItemCommandBuffer) {
    fun occupancy(containerId: ContainerId, items: ContainerCache): Occupancy {
        val items = items.get(containerId)
        val occ = Occupancy()
        items.forEach {
            val agent = (it as? PhysicalEntry)?.agent ?: return@forEach
            occ.mass += agent.mass
            occ.volume += agent.volume
        }
        return occ
    }

    fun update(
        requests: Queue<EntryMove>,
        capacities: Map<ContainerId, Capacity>,
        containerItems: Map<ContainerEntry, ContainerId>,
    ) {
        val occ = mutableMapOf<ContainerId, Occupancy>()
        for ((entry, cid) in containerItems) {
            val agent = (entry as? PhysicalEntry)?.agent ?: continue
            val containerOccupancy = occ.getOrPut(cid) { Occupancy() }
            containerOccupancy.mass += agent.mass
            containerOccupancy.volume += agent.volume
        }

        while (true) {
            val req = requests.poll() ?: break
            val destination = req.destination
            val entry = req.entry
            val moveMode = req.mode

            val from = containerItems[entry]
            val fromContainerOccupancy = occ[from]

            var moved = false
            when (destination) {
                is Destination.Container -> {
                    val destinationId = destination.id
                    val cap = capacities[destinationId] ?: run { continue }

                    if (from != null && moveMode == MoveMode.Transfer && from == destinationId) continue

                    val containerOccupancy = occ.getOrPut(destinationId) { Occupancy() }

                    if (canAccept(cap, containerOccupancy, entry) || req.required) {
                        when (moveMode) {
                            is MoveMode.Transfer -> {
                                buffer.add(entry, destinationId)
                            }

                            is MoveMode.Copy -> {
                                buffer.copy(entry, destinationId, moveMode.prototype)
                            }
                        }

                        (entry as? PhysicalEntry)?.agent?.let { a ->
                            containerOccupancy.mass += a.mass
                            containerOccupancy.volume += a.volume
                        }
                        moved = true
                        buffer.callback(TransferResult.Success, req.callback)
                    } else {
                        buffer.callback(TransferResult.Failure, req.callback)
                    }
                }

                is Destination.World -> {
                    buffer.spawnProp(entry, destination.location)
                    moved = true
                    buffer.callback(TransferResult.Success, req.callback)
                }

                is Destination.Void -> {
                    buffer.add(entry, TRASH_CAN)
                    moved = true
                    buffer.callback(TransferResult.Success, req.callback)
                }
            }

            if (moveMode == MoveMode.Transfer && moved) {
                if (from == null) return
                buffer.remove(entry, from)
                if (fromContainerOccupancy != null && entry is PhysicalEntry) {
                    entry.agent?.let {
                        fromContainerOccupancy.mass -= it.mass
                        fromContainerOccupancy.volume -= it.volume
                    }
                }
            }
        }
    }

    fun canAccept(cap: Capacity, occ: Occupancy, entry: ContainerEntry): Boolean {
        if (cap is Capacity.Infinity) return true
        val agent = (entry as? PhysicalEntry)?.agent ?: return false

        return when (cap) {
            is Capacity.Physical -> occ.volume + agent.volume <= cap.volume
            is Capacity.FixedMass -> occ.mass + agent.mass <= cap.mass
            else -> false
        }
    }

    companion object {
        val TRASH_CAN = ContainerId("trash_can")
    }
}