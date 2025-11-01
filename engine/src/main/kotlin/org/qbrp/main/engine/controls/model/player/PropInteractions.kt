package org.qbrp.main.engine.controls.model.player

import org.qbrp.main.core.util.StringId
import org.qbrp.main.engine.controls.dsl.InteractionDSL
import org.qbrp.main.engine.controls.dsl.InteractionScope
import org.qbrp.main.engine.controls.dsl.finish
import org.qbrp.main.engine.controls.dsl.interaction
import org.qbrp.main.engine.controls.entity.InteractableTypes
import org.qbrp.main.engine.controls.entity.InteractionPriorities
import org.qbrp.main.engine.controls.entity.conditions.AvailableResult
import org.qbrp.main.engine.controls.model.container.condition.ItemInDistanceCondition
import org.qbrp.main.engine.controls.model.item.ItemInteractable
import org.qbrp.main.engine.controls.model.player.condition.ItemInCarrierCondition

object PropInteractions {
    val SPAWN_PROP = interaction<ItemCarrierInteractable, ItemInteractable> {
        identity {
            subject(InteractableTypes.HAND)
            obj(InteractableTypes.ITEM)
            kind("spawn_prop")
        }

        execute { hand, item, ctx ->
            val carriedItem = hand.carriedItems.find { it.model.stringId == item.id }!!
            carriedItem.release()
        }

        priority(InteractionPriorities.GENERIC_ACTIONS)
        condition(ItemInCarrierCondition { _, item, _ -> item })
        condition(ItemInDistanceCondition())
    }.finish()

    val GRAB_PROP = interaction<ItemCarrierInteractable, ItemInteractable> {
        identity {
            subject(InteractableTypes.HAND)
            obj(InteractableTypes.ITEM)
            kind("grab_prop")
        }

        execute { hand, item, ctx ->
            val carriedItem = hand.getItemAsCarried(item.id)!!
            carriedItem.grabInWord()
        }

        condition(ItemInCarrierCondition { _, item, _ -> item })
        priority(InteractionPriorities.GENERIC_ACTIONS)
    }.finish()

    val RELEASE_PROP = interaction<ItemCarrierInteractable, ItemInteractable> {
        identity {
            subject(InteractableTypes.HAND)
            obj(InteractableTypes.ITEM)
            kind("release_prop")
        }

        execute { hand, item, ctx ->
            val carriedItem = hand.getItemAsCarried(item.id)!!
            carriedItem.release()
        }

        condition(ItemInCarrierCondition { _, item, _ -> item })
        condition { hand, item, ctx ->
            when(hand.getItemAsCarried(item.id)?.grabbed == true) {
                true -> AvailableResult.Yes
                false -> AvailableResult.No("Предмет не захвачен игроком")
            }
        }
        spawnedAsPropCondition()
        priority(InteractionPriorities.GENERIC_ACTIONS)
    }.finish()

    fun ItemCarrierInteractable.getItemAsCarried(id: StringId) = carriedItems.find { it.model.stringId == id }

    @InteractionDSL
    fun InteractionScope<ItemCarrierInteractable, ItemInteractable>.spawnedAsPropCondition() {
        condition { _, item, ctx ->
            when(item.spawnedAsProp) {
                true -> AvailableResult.Yes
                false -> AvailableResult.No("Предмет не является пропом")
            }
        }
    }
}