package org.qbrp.main.engine.controls.system

import org.qbrp.main.engine.controls.entity.interactable.Interactable
import org.qbrp.main.engine.controls.entity.interactable.InteractableObject
import org.qbrp.main.engine.controls.entity.interactable.InteractableSubject
import org.qbrp.main.engine.controls.entity.interactable.InteractableType

fun Interactable.asObject(): InteractableObject {
    return InteractableObject(this)
}

fun Interactable.asSubject(): InteractableSubject {
    return InteractableSubject(this)
}

@JvmName("containsInSubjects")
fun List<InteractableSubject>.contains(type: InteractableType<*>): Boolean {
    return find { it.interactable.interactableType == type } != null
}

@JvmName("containsInObjects")
fun List<InteractableObject>.contains(type: InteractableType<*>): Boolean {
    return find { it.interactable.interactableType == type } != null
}