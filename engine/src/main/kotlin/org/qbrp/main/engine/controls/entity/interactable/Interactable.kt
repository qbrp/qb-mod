package org.qbrp.main.engine.controls.entity.interactable

import org.qbrp.main.engine.controls.entity.display.InteractableViewData

interface Interactable {
    val interactableType: AnyInteractableType
    val interactableDisplay: InteractableViewData
}