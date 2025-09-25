package org.qbrp.main.engine.items.service

import org.qbrp.main.core.components.extensions.get
import org.qbrp.main.engine.containers.util.ContainerId
import org.qbrp.main.engine.containers.v2.api.ContainerService
import org.qbrp.main.engine.containers.v2.components.ContainedIn
import org.qbrp.main.engine.containers.v2.components.Container
import org.qbrp.main.engine.containers.v2.system.request.Destination
import org.qbrp.main.engine.containers.v2.system.request.MoveMode
import org.qbrp.main.engine.controls.model.player.InventoryInteractions.openInventory
import org.qbrp.main.engine.inventory.entity.Inventory
import org.qbrp.main.engine.inventory.entity.InventoryGroup
import org.qbrp.main.engine.inventory.player.impl.ServerPlayerInventoryManager
import org.qbrp.main.engine.items.model.ServerItemObject
import org.qbrp.main.engine.physics.dynamics.manipulate.Manipulator
import org.qbrp.main.engine.players.inventory.CharacterInventory
import org.qbrp.main.engine.players.model.service.PlayerStorage
import org.qbrp.main.engine.players.model.service.ServerPlayerStorage
import org.qbrp.main.engine.props.components.PropManager
import org.qbrp.main.engine.props.components.SpawnedProp

class ItemDeleteServiceImpl(
    private val containerService: ContainerService,
    private val playerStorage: ServerPlayerStorage,
    private val manipulator: Manipulator,
    private val itemStorage: ItemStorage<ServerItemObject>,
) : ItemDeleteService {
    override fun delete(item: ServerItemObject) {
        val containers = item
            .filterIsInstance<Container>()
            .map { it.id }

        for (player in playerStorage) {
            val inventoryManager = player.get<ServerPlayerInventoryManager>() ?: continue
            inventoryManager.apply {
                openedGroups
                    .filter { it.inIds(containers) }
                    .forEach { closeGroup(it) }
            }
        }

        containerService.requestMove(
            item,
            destination = Destination.Void,
            moveMode = MoveMode.Transfer,
            required = true
        )

        item.get<SpawnedProp>()?.let { manipulator.unpin(it.body) }
        item.get<PropManager>()?.despawn()

        itemStorage.remove(item.id)
    }

    fun Inventory.inIds(ids: List<ContainerId>) = ids.any { it == containerId }

    fun InventoryGroup.inIds(ids: List<ContainerId>) = inventories.any { it.inIds(ids) }
}