package org.qbrp.client.engine.inventory

import net.fabricmc.api.EnvType
import net.minecraft.item.ItemStack
import org.koin.core.component.get
import org.koin.dsl.module
import org.qbrp.client.core.networking.ClientNetworkUtil
import org.qbrp.client.engine.inventory.model.ComponentActionHandler
import org.qbrp.client.engine.inventory.model.InventoryListener
import org.qbrp.main.core.game.ComponentRegistryInitializationEvent
import org.qbrp.main.core.modules.Autoload
import org.qbrp.main.core.modules.QbModule
import org.qbrp.main.core.utils.networking.ClientReceiver
import org.qbrp.main.core.utils.networking.messages.Message
import org.qbrp.main.core.utils.networking.messages.components.Cluster
import org.qbrp.main.core.utils.networking.messages.components.ClusterBuilder
import org.qbrp.main.core.utils.networking.messages.components.readonly.ClusterViewer
import org.qbrp.main.engine.inventory.InventoriesModule

@Autoload(env = EnvType.CLIENT)
class ClientInventoriesModule: QbModule("inventories"), ActionMessageSender {
    override fun getKoinModule() = module {
        single<ActionMessageSender> { this@ClientInventoriesModule }
        single<HeldItem> { HeldItemHolder() }
    }

    override fun onEnable() {
        val heldItem = get<HeldItem>()
        ComponentRegistryInitializationEvent.EVENT.register {
            it.register(InventoryListener::class.java)
            it.register(ComponentActionHandler::class.java)
        }
        ClientReceiver(InventoriesModule.HELD_ITEM, Cluster::class) { message, context, receiver ->
            val itemStack = message.getContent<ClusterViewer>().getComponentData<ItemStack>("itemstack")
            heldItem.setItem(itemStack)
            true
        }.register()
    }

    override fun sendTakeItemMessage(slot: Int, containerId: String, objectId: String) {
        ClientNetworkUtil.sendMessage(
            Message(InventoriesModule.TAKE_ITEM, ClusterBuilder()
                .component("slot", slot)
                .component("containerId", containerId)
                .component("objectId", objectId)
                .build())
        )
    }

    override fun sendPutItemMessage(slot: Int, containerId: String, objectId: String) {
        ClientNetworkUtil.sendMessage(
            Message(InventoriesModule.PUT_ITEM, ClusterBuilder()
                .component("slot", slot)
                .component("containerId", containerId)
                .component("objectId", objectId)
                .build())
        )
    }

    override fun sendSwapItemMessage(slot: Int, containerId: String, objectId: String) {
        ClientNetworkUtil.sendMessage(
            Message(InventoriesModule.SWAP_ITEM, ClusterBuilder()
                .component("slot", slot)
                .component("containerId", containerId)
                .component("objectId", objectId)
                .build()
            )
        )
    }


}