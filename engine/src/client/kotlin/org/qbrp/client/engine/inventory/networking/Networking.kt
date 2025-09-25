package org.qbrp.client.engine.inventory.networking

import org.koin.core.context.GlobalContext
import org.qbrp.client.core.networking.v2.ClientPacketSender
import org.qbrp.client.engine.inventory.model.ClientContainerState
import org.qbrp.main.core.components.AssociatedComponentState
import org.qbrp.main.core.components.objects.StateObject
import org.qbrp.main.engine.inventory.InventoriesModule
import org.qbrp.main.engine.inventory.packet.ContainerSyncRequest

typealias GenericObject = StateObject<out AssociatedComponentState<*>>

fun ClientContainerState.requestSync(packetSender: ClientPacketSender = GlobalContext.get().get()) {
    packetSender.send(ContainerSyncRequest(id), InventoriesModule.SYNC)
}