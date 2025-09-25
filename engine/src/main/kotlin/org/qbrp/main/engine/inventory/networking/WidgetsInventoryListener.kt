package org.qbrp.main.engine.inventory.networking

import org.qbrp.main.core.networking.PacketListener
import org.qbrp.main.engine.containers.util.ContainerId
import org.qbrp.main.engine.containers.v2.components.ContainerDisplayName
import org.qbrp.main.engine.inventory.entity.InventoryGroup

interface WidgetsInventoryListener: PacketListener {
    fun onWidgetClose(id: ContainerId)
    fun onGroup(group: InventoryGroup)
}