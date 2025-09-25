package org.qbrp.main.engine.controls.model.item

import org.qbrp.main.core.dataformats.StringId
import org.qbrp.main.engine.containers.v2.components.Container
import org.qbrp.main.engine.controls.model.container.ContainerInteractable
import org.qbrp.main.engine.controls.model.container.LocateableInteractable
import org.qbrp.main.engine.controls.model.player.IdentifiableInteractable
import org.qbrp.main.engine.items.model.ServerItemObject

interface ItemInteractable: LocateableInteractable, IdentifiableInteractable {
    val logic: ServerItemObject
    val heldByPlayer: Boolean
    val containers: List<ContainerInteractable>
    val spawnedAsProp: Boolean
    override val id: StringId
        get() = logic.stringId
}