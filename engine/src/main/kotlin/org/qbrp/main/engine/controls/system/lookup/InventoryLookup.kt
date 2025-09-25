package org.qbrp.main.engine.controls.system.lookup

import org.qbrp.main.core.components.extensions.get
import org.qbrp.main.engine.containers.v2.components.Container
import org.qbrp.main.engine.controls.entity.call.InteractionRequestContext
import org.qbrp.main.engine.controls.model.InteractionService
import org.qbrp.main.engine.controls.model.player.InventoryInteractions
import org.qbrp.main.engine.controls.model.player.PlayerInteractable
import org.qbrp.main.engine.controls.model.player.PropInteractions
import org.qbrp.main.engine.controls.system.asObject
import org.qbrp.main.engine.controls.system.asSubject
import org.qbrp.main.engine.inventory.player.impl.ServerPlayerInventoryManager
import org.qbrp.main.engine.items.model.ServerItemObject
import org.qbrp.main.engine.players.inventory.CharacterInventory
import org.qbrp.main.engine.players.inventory.creative.ItemListModule
import org.qbrp.main.engine.players.model.ServerPlayerObject

// HeldItem или рука > наведенный предмет
class InventoryLookup(
    private val logicalPlayer: ServerPlayerObject,
    self: Boolean,
    private val call: InteractionRequestContext
) : PlayerLookup(logicalPlayer, self) {
    private val inventoryHoveredItem: ServerItemObject? = call.inventoryHoveredItem as? ServerItemObject

    override fun lookup(player: PlayerInteractable, interactionService: InteractionService) {
        super.lookup(player, interactionService)
        if (call.inventoryOpened) {
            // Мы взаимодействуем с объектом, который наведен курсором. Он - объект
            inventoryHoveredItem?.let {
                val interactable = interactionService.getItemInteractable(it)
                objects += interactable.asObject()
            }

            if (!player.characterApplied) return

            val character = interactionService.getCharacterInteractable(logicalPlayer)!!
            character.carrier.carriedItems.forEach {
                subjects += interactionService.getItemInteractable(it.model).asSubject()
            }

            // Рука как способ взаимодействия уже добавлена

            // Добавляем все открытые игроком инвентари в объекты
            val containers = logicalPlayer.let {
                val itemInventories = it
                    .get<CharacterInventory>()
                    ?.carriedItems
                    ?.flatMap { it.model.filterIsInstance<Container>() }
                    ?.map { it.id }
                    ?: emptyList()
                val openedInventories = it
                    .get<ServerPlayerInventoryManager>()
                    ?.openedInventories
                    ?.map { it.containerId }
                    ?: emptyList()
                val ids = (itemInventories + openedInventories).distinct()
                ids
                    .map { interactionService.getContainerInteractable(it, player).asObject() }
            }

            objects.addAll(containers)

            // Взаимодействия с предметами
            if (call.hoveredInventoryContainerId != ItemListModule.ITEM_LIST_CONTAINER_ID) {
                interactions.addAll(listOf(InventoryInteractions.TAKE_ITEM, InventoryInteractions.PUT_ITEM))
                interactions.add(InventoryInteractions.DELETE_ITEM)
                interactions.add(InventoryInteractions.SEE_INVENTORIES)
                interactions.add(PropInteractions.GRAB_PROP)
                interactions.add(PropInteractions.RELEASE_PROP)
            }

            interactions.add(InventoryInteractions.COPY_ITEM)
        }
    }
}