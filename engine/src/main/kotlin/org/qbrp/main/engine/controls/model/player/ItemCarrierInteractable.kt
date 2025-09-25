package org.qbrp.main.engine.controls.model.player

import org.qbrp.main.engine.containers.util.ContainerId
import org.qbrp.main.engine.controls.entity.InteractableTypes
import org.qbrp.main.engine.controls.entity.display.InteractableViewData
import org.qbrp.main.engine.controls.entity.interactable.AnyInteractableType
import org.qbrp.main.engine.controls.entity.interactable.Interactable
import org.qbrp.main.engine.controls.model.container.LocateableInteractable
import org.qbrp.main.engine.players.inventory.hand.CarryVariance
import org.qbrp.main.engine.players.inventory.hand.ItemCarrier
import org.qbrp.main.engine.world.location.Location
import org.qbrp.main.engine.world.location.Position
import org.qbrp.main.engine.world.model.World

interface ItemCarrierInteractable: ItemCarrier, LocateableInteractable {
    val variancesId: List<ContainerId> get() = variances.map { it.id }
    val varianceInteractables: List<CarryVarianceInteractable>
}

interface CarryVarianceInteractable: CarryVariance, Interactable, LocateableInteractable

class CarryVarianceInteractableImpl(
    private val location: Location,
    private val logic: CarryVariance,
    override val interactableDisplay: InteractableViewData
): CarryVarianceInteractable, CarryVariance by logic, LocateableInteractable, Location by location {
    override val interactableType: AnyInteractableType get() = InteractableTypes.CARRY_VARIANCE
}

class ItemCarrierInteractableImpl(
    private val logic: ItemCarrier,
    override val interactableType: AnyInteractableType,
    override val interactableDisplay: InteractableViewData,
    override val varianceInteractables: List<CarryVarianceInteractable>,
): ItemCarrierInteractable, ItemCarrier by logic {
    override val world: World get() = owner.context.world
    override val pos: Position get() = owner.context.pos
}