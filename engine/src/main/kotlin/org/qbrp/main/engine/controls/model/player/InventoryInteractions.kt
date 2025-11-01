package org.qbrp.main.engine.controls.model.player

import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.qbrp.main.core.components.extensions.has
import org.qbrp.main.core.fabricmc.player.Permission
import org.qbrp.main.engine.containers.api.ContainerService
import org.qbrp.main.engine.containers.event.openContainer
import org.qbrp.main.engine.containers.system.request.ContainerEntryPrototype
import org.qbrp.main.engine.containers.system.request.Destination
import org.qbrp.main.engine.containers.system.request.MoveMode
import org.qbrp.main.engine.controls.dsl.InteractionDSL
import org.qbrp.main.engine.controls.dsl.InteractionScope
import org.qbrp.main.engine.controls.dsl.finish
import org.qbrp.main.engine.controls.dsl.interaction
import org.qbrp.main.engine.controls.entity.InteractableTypes
import org.qbrp.main.engine.controls.entity.InteractionPriorities
import org.qbrp.main.engine.controls.entity.conditions.AvailableResult.*
import org.qbrp.main.engine.controls.entity.interactable.Interactable
import org.qbrp.main.engine.controls.model.container.ContainerInteractable
import org.qbrp.main.engine.controls.model.container.LocateableInteractable
import org.qbrp.main.engine.controls.model.container.condition.ContainerCanAccessItemCondition
import org.qbrp.main.engine.controls.model.container.condition.ContainerProvider
import org.qbrp.main.engine.controls.model.container.condition.InventoryOpenedCondition
import org.qbrp.main.engine.controls.model.container.condition.ItemInDistanceCondition
import org.qbrp.main.engine.controls.model.container.condition.ItemProvider
import org.qbrp.main.engine.controls.model.container.condition.SingleContainerProvider
import org.qbrp.main.engine.controls.model.item.ItemInteractable
import org.qbrp.main.engine.controls.model.player.condition.ItemInCarrierCondition
import org.qbrp.main.engine.items.service.ItemDeleteService
import org.qbrp.main.engine.players.inventory.Equip
import org.qbrp.main.engine.players.inventory.INFINITY_INVENTORY_COMPONENT_NAME
import org.qbrp.main.engine.players.inventory.INFINITY_INVENTORY_PERMISSION
import org.qbrp.main.engine.players.inventory.ITEM_LIST_PERMISSION
import org.qbrp.main.engine.players.inventory.isEquipment

object InventoryInteractions : KoinComponent {
    private val containerService: ContainerService by inject()
    private val copyItemPrototype: ContainerEntryPrototype by inject()
    private val itemDeleteService: ItemDeleteService by inject()

    private val BASE = interaction<LocateableInteractable, ItemInteractable> {
        priority(InteractionPriorities.GENERIC_ACTIONS)
        condition(ItemInDistanceCondition())
        condition(InventoryOpenedCondition())
    }

    val SEE_INVENTORIES = interaction<ItemCarrierInteractable, ItemInteractable> {
        identity {
            subject(InteractableTypes.HAND)
            obj(InteractableTypes.ITEM)
            kind("see_inventories")
        }

        execute { hand, item, ctx ->
            item.containers.forEach {
                hand.owner.openContainer(it.id)
            }
        }

        condition { _, item, _ ->
            val itemHasContainers = item.containers.isNotEmpty()
            when(itemHasContainers) {
                true -> Yes
                false -> No("Предмет не имеет инвентаря")
            }
        }

        priority(InteractionPriorities.GENERIC_ACTIONS)
    }.finish()

    val TAKE_ITEM = interaction<CarryVarianceInteractable, ItemInteractable> {
        identity {
            subject(InteractableTypes.CARRY_VARIANCE)
            obj(InteractableTypes.ITEM)
            kind("take_item")
        }

        execute { carrier, item, ctx ->
            carrier.held(item.logic)
        }

        forContainer { carrier, item, ctx -> carrier.id }

        extends(BASE)
    }.finish()

    val PUT_ITEM = interaction<ItemInteractable, ContainerInteractable> {
        identity {
            subject(InteractableTypes.ITEM)
            obj(InteractableTypes.CONTAINER)
            kind("put_item")
        }

        execute { item, container, ctx ->
            containerService.requestMove(
                item.logic,
                Destination.Container(container.id),
                MoveMode.Transfer
            )
        }

        condition { item, container, ctx ->
            val other = container.anchored != item.id
            when (other) {
                true -> Yes
                false -> No("Предмет нельзя положить в самого себя")
            }
        }

        condition { item, container, ctx ->
            when (container.id.isEquipment) {
                true -> No("Класть предметы в экипировку требуется через отдельное взаимодействие")
                false -> Yes
            }
        }

        condition(ItemInCarrierCondition { item, _, _ -> item })
        condition(InventoryOpenedCondition())
        priority(InteractionPriorities.GENERIC_ACTIONS)
        itemForContainer { item, container, ctx -> item }
    }.finish()

    val EQUIP = interaction<ItemCarrierInteractable, ItemInteractable> {
        identity {
            subject(InteractableTypes.HAND)
            obj(InteractableTypes.ITEM)
            kind("equip")
        }

        execute { carrier, item, ctx ->
            carrier.equip(item.logic)
        }

        condition { carrier, item, ctx ->
            when(item.logic.has<Equip>()) {
                true -> Yes
                false -> No("Предмет должен быть экипировкой")
            }
        }

        condition(InventoryOpenedCondition())
        priority(InteractionPriorities.GENERIC_ACTIONS + 1)
    }.finish()

    val COPY_ITEM = interaction<PlayerInteractable, ItemInteractable> {
        identity {
            subject(InteractableTypes.PLAYER)
            obj(InteractableTypes.ITEM)
            kind("copy_item")
        }

        execute { player, item, ctx ->
            val infinityInventoryId = player.getAnchoredContainer(INFINITY_INVENTORY_COMPONENT_NAME)
            containerService.requestMove(
                item.logic,
                Destination.Container(infinityInventoryId),
                MoveMode.Copy(copyItemPrototype)
            )
        }

        permission(ITEM_LIST_PERMISSION)
        permission(INFINITY_INVENTORY_PERMISSION)
        priority(InteractionPriorities.GENERIC_ACTIONS)
    }.finish()

    val DELETE_ITEM = interaction<PlayerInteractable, ItemInteractable> {
        identity {
            subject(InteractableTypes.PLAYER)
            obj(InteractableTypes.ITEM)
            kind("delete_item")
        }

        execute { player, item, ctx ->
            itemDeleteService.delete(item.logic)
        }

        permission(Permission("inventory.delete"))
        priority(InteractionPriorities.GENERIC_ACTIONS)
    }.finish()

    @InteractionDSL
    fun <S: Interactable, O: ItemInteractable> InteractionScope<S, O>.forContainers(containerProvider: ContainerProvider<S, O>) {
        condition(ContainerCanAccessItemCondition(containerService, containerProvider, { sub, obj, ctx -> obj }))
    }

    @InteractionDSL
    fun <S: Interactable, O: Interactable> InteractionScope<S, O>.forContainers(containerProvider: ContainerProvider<S, O>, itemProvider: ItemProvider<S, O>) {
        condition(ContainerCanAccessItemCondition(containerService, containerProvider, itemProvider))
    }

    @InteractionDSL
    fun <S: Interactable, O: Interactable> InteractionScope<S, O>.forContainer(containerProvider: SingleContainerProvider<S, O>, itemProvider: ItemProvider<S, O>) {
        condition(ContainerCanAccessItemCondition(containerProvider, itemProvider, containerService))
    }

    @InteractionDSL
    fun <S: Interactable, O: ItemInteractable> InteractionScope<S, O>.forContainer(containerProvider: SingleContainerProvider<S, O>) {
        condition(ContainerCanAccessItemCondition(containerProvider, { _, item, _ -> item }, containerService))
    }

    @InteractionDSL
    fun <S: Interactable, O: ContainerInteractable> InteractionScope<S, O>.itemForContainer(itemProvider: ItemProvider<S, O>) {
        condition(ContainerCanAccessItemCondition({ sub, obj, ctx -> obj.id }, itemProvider, containerService))
    }
}