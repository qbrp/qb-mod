package org.qbrp.main.engine.players.inventory.hand

import org.qbrp.main.engine.containers.util.ContainerId
import org.qbrp.main.engine.containers.v2.system.request.MoveCallback
import org.qbrp.main.engine.items.model.ServerItemObject
import org.qbrp.main.engine.players.model.ServerPlayerObject

interface ItemCarrier {
    val owner: ServerPlayerObject
    val carriedItems: Collection<CarriedItem>
        get() = variances.flatMap { it.carriedItems }
    val variances: Collection<CarryVariance>
}

interface CarryVariance {
    val id: ContainerId
    val carriedItems: Collection<CarriedItem>
    fun held(item: ServerItemObject, callback: MoveCallback = {})
}

interface CarriedItem {
    val model: ServerItemObject
    val grabbed: Boolean
    fun moveTo(container: ContainerId, callback: MoveCallback = {})
    fun release(callback: MoveCallback = {})
    fun grabInWord()
    fun returnBack()
}