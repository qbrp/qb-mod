package org.qbrp.main.engine.players.inventory.hand

import org.qbrp.main.engine.controls.entity.interactable.InteractableType
import org.qbrp.main.engine.controls.entity.InteractableTypes
import org.qbrp.main.engine.controls.entity.display.InteractableName
import org.qbrp.main.engine.controls.entity.display.InteractableViewData
import org.qbrp.main.engine.world.location.Position
import org.qbrp.main.engine.controls.model.player.ItemCarrierInteractable
import org.qbrp.main.engine.text.plus
import org.qbrp.main.engine.text.toSequenceBuilder
import org.qbrp.main.engine.world.model.World
import org.qbrp.main.engine.controls.model.item.ItemInteractable
//
//class ItemInCarrier(
//    val carrier: ItemCarrierInteractable,
//    val item: ItemInteractable
//) : ItemCarrierInteractable by carrier, ItemInteractable by item {
//
//    override val ignorePosCondition: Boolean
//        get() = item.ignorePosCondition
//    override val pos: Position
//        get() = carrier.pos
//    override val world: World
//        get() = carrier.world
//
//    override val interactableType: InteractableType<ItemInCarrier> = InteractableTypes.ITEM
//    override val interactableDisplay: InteractableViewData get() =
//        item.interactableDisplay.let {
//            val seq = it.name.text.toSequenceBuilder()+" (в руке)"
//            it.copy(name = InteractableName(seq.build()))
//        }
//}