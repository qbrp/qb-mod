package org.qbrp.main.engine.controls.entity.interactable

import org.qbrp.main.engine.controls.entity.interactable.Interactable

data class InteractableType<out T>(val name: String) {
    fun contains(collection: Collection<Interactable>): Boolean {
        return collection.find { it.interactableType == this } != null
    }
}

typealias AnyInteractableType = InteractableType<*>