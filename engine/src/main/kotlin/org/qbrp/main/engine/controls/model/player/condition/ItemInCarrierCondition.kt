package org.qbrp.main.engine.controls.model.player.condition

import org.qbrp.main.core.components.extensions.get
import org.qbrp.main.engine.controls.entity.conditions.AvailableResult
import org.qbrp.main.engine.controls.entity.conditions.AvailableResult.No
import org.qbrp.main.engine.controls.entity.conditions.AvailableResult.Yes
import org.qbrp.main.engine.controls.entity.conditions.Condition
import org.qbrp.main.engine.controls.entity.interactable.Interactable
import org.qbrp.main.engine.controls.entity.interactions.InteractionContext
import org.qbrp.main.engine.controls.model.item.ItemInteractable
import org.qbrp.main.engine.players.inventory.ItemCarrier

typealias InteractionProvider<S, O, R> = (S, O, InteractionContext) -> R

class ItemInCarrierCondition<S : Interactable, O : Interactable>(
    val carrierProvider: InteractionProvider<S, O, ItemCarrier?> = { _, _, ctx ->
        ctx.player.get<ItemCarrier>()
    },
    val itemProvider: InteractionProvider<S, O, ItemInteractable>,
) : Condition<S, O> {
    override fun isAvailable(sub: S, obj: O, ctx: InteractionContext): AvailableResult {
        val holdsItem = carrierProvider(sub, obj, ctx)?.carriedItems?.any { it.model == itemProvider(sub, obj, ctx).logic } == true
        return when(holdsItem) {
            true -> Yes
            false -> No("Игрок не держит предмет")
        }
    }
}