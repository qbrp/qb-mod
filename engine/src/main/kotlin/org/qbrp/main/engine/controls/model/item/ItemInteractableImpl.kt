package org.qbrp.main.engine.controls.model.item

import org.qbrp.main.core.components.extensions.get
import org.qbrp.main.core.components.extensions.has
import org.qbrp.main.core.components.extensions.require
import org.qbrp.main.engine.containers.api.ContainerService
import org.qbrp.main.engine.containers.components.ContainedIn
import org.qbrp.main.engine.containers.components.Container
import org.qbrp.main.engine.controls.entity.IconIds
import org.qbrp.main.engine.controls.entity.InteractableTypes
import org.qbrp.main.engine.controls.entity.display.InteractableViewData
import org.qbrp.main.engine.controls.entity.interactable.AnyInteractableType
import org.qbrp.main.engine.controls.model.InteractionService
import org.qbrp.main.engine.controls.model.container.ContainerInteractable
import org.qbrp.main.engine.items.components.meta.ItemLocation
import org.qbrp.main.engine.items.model.ServerItemObject
import org.qbrp.main.engine.players.inventory.PositionProxy
import org.qbrp.main.engine.players.model.PlayerObject
import org.qbrp.main.engine.props.components.PropManager
import org.qbrp.main.engine.world.location.WorldPos
import org.qbrp.main.engine.world.model.World
import org.qbrp.main.engine.items.display.components.ItemDisplay

class ItemInteractableImpl(
    override val logic: ServerItemObject,
    private val containerService: ContainerService,
    private val interactionService: InteractionService
): ItemInteractable {
    override val containers: List<ContainerInteractable>
        get() = logic.filterIsInstance<Container>().map { interactionService.getContainerInteractable(it.id, this) }

    override val spawnedAsProp: Boolean
        get() = logic.get<PropManager>()?.isSpawned == true

    override val heldByPlayer: Boolean
        get() {
            val containerId = logic.get<ContainedIn>()?.id ?: return false
            return containerService.getAnchoredEntity(containerId) is PlayerObject
        }
    override val world: World get() = logic.require<ItemLocation>().world
    override val pos: WorldPos get() = logic.require<ItemLocation>().pos
    override val ignorePosCondition: Boolean get() = logic.has<PositionProxy>()

    override val interactableType: AnyInteractableType get() = InteractableTypes.ITEM
    override val interactableDisplay: InteractableViewData
        get() = logic.get<ItemDisplay>()?.getInteractableDisplay() ?: EMPTY_INTERACTION_DISPLAY

    companion object {
        val EMPTY_INTERACTION_DISPLAY = InteractableViewData.of("Предмет", IconIds.NOTHING)
    }
}