package org.qbrp.main.engine.containers.event

import org.qbrp.main.core.events.Event
import org.qbrp.main.core.events.emit
import org.qbrp.main.engine.containers.util.ContainerId
import org.qbrp.main.engine.players.model.ServerPlayerObject

data class ContainerOpenEvent(val by: ServerPlayerObject, val id: ContainerId) : Event

data class ContainerCloseIntentionEvent(val by: ServerPlayerObject, val id: ContainerId) : Event

fun ServerPlayerObject.openContainer(id: ContainerId) {
    ContainerOpenEvent(this, id).emit()
}

fun ServerPlayerObject.intentCloseContainer(id: ContainerId) {
    ContainerCloseIntentionEvent(this, id).emit()
}