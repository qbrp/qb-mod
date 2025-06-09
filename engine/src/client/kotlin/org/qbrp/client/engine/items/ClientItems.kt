package org.qbrp.client.engine.items

import net.fabricmc.api.EnvType
import net.fabricmc.fabric.api.client.rendering.v1.TooltipComponentCallback
import net.minecraft.client.item.TooltipData
import org.koin.core.component.get
import org.qbrp.client.core.synchronization.ClientLocalMessageSender
import org.qbrp.client.core.synchronization.ComponentConverter
import org.qbrp.client.core.synchronization.ContainerHolderFabric
import org.qbrp.main.core.synchronization.channels.ObjectMessagingChannel
import org.qbrp.client.core.synchronization.ObjectSynchronizeChannel
import org.qbrp.client.core.synchronization.enableClear
import org.qbrp.client.core.synchronization.runClient
import org.qbrp.client.engine.inventory.model.InventoryListener
import org.qbrp.client.engine.items.components.inventory.ItemContainerHolder
import org.qbrp.client.engine.items.components.tooltip.ClientItemDisplay
import org.qbrp.client.engine.items.components.tooltip.ProgressBarTooltip
import org.qbrp.client.engine.items.components.tooltip.mc.TooltipContainer
import org.qbrp.main.core.game.ComponentRegistryInitializationEvent
import org.qbrp.main.core.modules.Autoload
import org.qbrp.main.core.modules.QbModule
import org.qbrp.main.core.utils.networking.messages.components.Cluster
import org.qbrp.main.engine.inventory.InventorySynchronizer
import org.qbrp.main.engine.items.ItemsModule
import org.qbrp.main.engine.items.components.tooltip.impl.Description
import org.qbrp.main.engine.items.components.tooltip.impl.ItemDisplay
import org.qbrp.main.engine.items.components.containers.ItemContainer
import org.qbrp.main.engine.items.model.ItemStorage

@Autoload(env = EnvType.CLIENT)
class ClientItems() : QbModule("client-items"), ClientItemsAPI {
    override fun getKoinModule() = innerWithApi({
        single { ItemStorage<ClientItemObject>().apply { enableClear() } } },
        {
        scoped { ClientLocalMessageSender(ItemsModule.ITEMS_MESSAGING_CHANNEL) }
    })

    override val storage: ItemStorage<ClientItemObject> get() = getLocal()

    override fun onEnable() {
        ObjectSynchronizeChannel<ClientItemObject>(ItemsModule.ITEMS_CHANNEL, storage) { cluster, id ->
            ClientItemObject.deserialize(cluster, getLocal<ClientLocalMessageSender>())
                .apply { state.putObjectAndEnableBehaviours(this) }
        }.apply {
            addFabric(ComponentConverter(ItemDisplay::class, { cluster ->
                ClientItemDisplay(
                    cluster.getComponentData("display.lines")!!,
                    cluster.getComponentData("display.name")!!
                )
            }))
            addAssociation("ItemDisplay", "ClientItemDisplay")
            addFabric(ComponentConverter(Description::class, { cluster ->
                ProgressBarTooltip()
            }))
            addAssociation("Description", "ProgressBarTooltip")
            addFabric(ContainerHolderFabric(ItemContainer::class, { cluster, id ->
                ItemContainerHolder(id)
            }))
            addAssociation("ItemContainer", "ItemContainerHolder")
            addFabric(ComponentConverter(InventorySynchronizer::class, { cluster ->
                cluster.getComponentData<List<Cluster>>("containers")!!
                    .flatMap { it.getData().getComponentData<List<Cluster>>("entries")!! }
                    .forEach { handleCluster(it.getData()) }
                InventoryListener(get())
            }))
            addAssociation("InventorySynchronizer", "InventoryListener")
            runClient()
        }
        ObjectMessagingChannel<ClientItemObject>(ItemsModule.ITEMS_MESSAGING_CHANNEL, storage)
            .runClient()
        ComponentRegistryInitializationEvent.EVENT.register {
            it.register(ClientItemDisplay::class.java)
            it.register(ProgressBarTooltip::class.java)
            it.register(ItemContainerHolder::class.java)
        }

        TooltipComponentCallback.EVENT.register { data: TooltipData? ->
            when (data) {
                is TooltipContainer -> data
                else -> null
            }
        }
    }
}