package org.qbrp.main.engine.controls.model.player

import org.qbrp.main.core.util.StringId
import org.qbrp.main.engine.controls.entity.interactable.Interactable

interface IdentifiableInteractable: Interactable {
    val id: StringId
}