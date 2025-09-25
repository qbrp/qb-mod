package org.qbrp.main.engine.controls.model.container

import org.qbrp.main.core.dataformats.StringId
import org.qbrp.main.engine.containers.util.ContainerId
import org.qbrp.main.engine.controls.entity.interactable.Interactable

interface ContainerInteractable : Interactable {
    val id: ContainerId
    val anchored: StringId?
}