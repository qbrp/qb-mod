package org.qbrp.main.engine.players.inventory

import org.qbrp.main.core.components.extensions.getOrAdd
import org.qbrp.main.core.fabricmc.player.hasPermission
import org.qbrp.main.core.fabricmc.player.Permission
import org.qbrp.main.engine.containers.api.ContainerService
import org.qbrp.main.engine.containers.components.Capacity
import org.qbrp.main.engine.containers.components.Container
import org.qbrp.main.engine.containers.components.ContainerDisplayName
import org.qbrp.main.engine.containers.event.openContainer
import org.qbrp.main.engine.containers.util.ContainerId
import org.qbrp.main.engine.players.model.ServerPlayerObject

val INFINITY_INVENTORY_COMPONENT_NAME = Container.name("infinity")

val INFINITY_INVENTORY_PERMISSION = Permission("infinity-inventory")

fun ServerPlayerObject.hasInfinityInventoryPermission() = hasPermission(INFINITY_INVENTORY_PERMISSION)

fun ServerPlayerObject.getInfinityContainerId(service: ContainerService) : ContainerId {
    return getOrAdd(setName = INFINITY_INVENTORY_COMPONENT_NAME) { Container(service.createInfinityContainer()) }.id
}

fun ServerPlayerObject.openInfinityInventory(service: ContainerService) {
    if (!hasPermission(INFINITY_INVENTORY_PERMISSION)) return
    val id = getInfinityContainerId(service)
    openContainer(id)
}

fun ContainerService.createInfinityContainer(): ContainerId {
    return newContainer(
        ContainerDisplayName("Неограниченный инвентарь"),
        Capacity.Infinity
    )
}