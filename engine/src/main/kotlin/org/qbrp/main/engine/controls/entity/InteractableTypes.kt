package org.qbrp.main.engine.controls.entity

import org.qbrp.main.engine.controls.entity.interactable.InteractableType
import org.qbrp.main.engine.controls.model.container.ContainerInteractable
import org.qbrp.main.engine.controls.model.player.PlayerInteractable
import org.qbrp.main.engine.controls.model.item.ItemInteractable
import org.qbrp.main.engine.controls.model.player.CharacterInteractable
import org.qbrp.main.engine.controls.model.player.CarryVarianceInteractable
import org.qbrp.main.engine.controls.model.player.ItemCarrierInteractable

object InteractableTypes {
    val PLAYER = InteractableType<PlayerInteractable>("player")
    val CHARACTER = InteractableType<CharacterInteractable>("character")
    val HAND = InteractableType<ItemCarrierInteractable>("hand")
    val CARRY_VARIANCE = InteractableType<CarryVarianceInteractable>("carry_variance")
    val ITEM = InteractableType<ItemInteractable>("item")
    val CONTAINER = InteractableType<ContainerInteractable>("container")
}