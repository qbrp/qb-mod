package org.qbrp.main.engine.controls.model.container

import org.qbrp.main.engine.controls.entity.interactable.Interactable
import org.qbrp.main.engine.world.location.Location

interface LocateableInteractable: Interactable, Location {
    val ignorePosCondition: Boolean get() = false
}