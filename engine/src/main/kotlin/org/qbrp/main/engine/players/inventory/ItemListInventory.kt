package org.qbrp.main.engine.players.inventory

import org.qbrp.main.core.assets.prefabs.Prefab
import org.qbrp.main.core.assets.prefabs.PrefabsAPI
import org.qbrp.main.core.components.StateUnavailableComponent
import org.qbrp.main.core.components.extensions.add
import org.qbrp.main.core.fabricmc.player.hasPermission
import org.qbrp.main.core.fabricmc.player.Permission
import org.qbrp.main.engine.containers.api.ContainerService
import org.qbrp.main.engine.containers.components.ContainerDisplayName
import org.qbrp.main.engine.containers.event.openContainer
import org.qbrp.main.engine.containers.util.ContainerId
import org.qbrp.main.engine.items.ItemsModule
import org.qbrp.main.engine.items.components.meta.Meta
import org.qbrp.main.engine.items.model.ItemInstantiateContext
import org.qbrp.main.engine.items.model.ServerItemObject
import org.qbrp.main.engine.items.service.ItemFactory
import org.qbrp.main.engine.players.model.ServerPlayerObject
import org.qbrp.main.engine.world.location.WorldVec3
import org.qbrp.main.engine.world.model.World

val ITEM_LIST_PERMISSION = Permission("item-list")
val ITEM_LIST_CONTAINER_ID = ContainerId("item-list")

class ItemListInstance() : StateUnavailableComponent

fun ServerPlayerObject.hasItemListPermission(): Boolean {
    return hasPermission(ITEM_LIST_PERMISSION)
}

fun ServerPlayerObject.openItemList() {
    if (hasItemListPermission()) {
        openContainer(ITEM_LIST_CONTAINER_ID)
    } else {
        sendChatMessage("<red>Недостаточно прав")
    }
}

fun ContainerService.createItemListContainer(
    prefabsAPI: PrefabsAPI,
    factory: ItemFactory,
    world: World
) {
    val items = prefabsAPI.parseCategory(ItemsModule.PREFAB_CATEGORY)
        .flatMap { it.tags }
        .map { factory.newItemListInstance(it, world) }
    newContainer(
        name = ContainerDisplayName("Список предметов"),
        items = items,
        containerId = ITEM_LIST_CONTAINER_ID
    )
}

fun ItemFactory.newItemListInstance(
    tag: Prefab.Tag,
    world: World,
    ctx: ItemInstantiateContext = ItemInstantiateContext.Companion.DEFAULT
): ServerItemObject {
    val item = ServerItemObject(ctx)
    item.state.parent = item
    item.add(PositionProxy(world, WorldVec3()))
    return handleInstance(item, tag).apply { add(ItemListInstance()) }
}