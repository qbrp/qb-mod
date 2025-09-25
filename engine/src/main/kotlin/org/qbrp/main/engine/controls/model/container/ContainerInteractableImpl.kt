package org.qbrp.main.engine.controls.model.container

import org.qbrp.main.core.dataformats.StringId
import org.qbrp.main.engine.containers.util.ContainerId
import org.qbrp.main.engine.controls.entity.InteractableTypes
import org.qbrp.main.engine.controls.entity.display.InteractableName
import org.qbrp.main.engine.controls.entity.display.InteractableViewData
import org.qbrp.main.engine.controls.entity.interactable.AnyInteractableType
import org.qbrp.main.engine.controls.entity.interactable.Interactable
import org.qbrp.main.engine.text.literal

class ContainerInteractableImpl(
    override val id: ContainerId,
    val entity: Interactable,
    val containerName: String,
    override val anchored: StringId?,
): ContainerInteractable {
    override val interactableDisplay: InteractableViewData = InteractableViewData.of(
        literal(containerName),
        entity.interactableDisplay.icon,
    )
    override val interactableType: AnyInteractableType get() = InteractableTypes.CONTAINER
}