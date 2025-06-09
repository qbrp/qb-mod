package org.qbrp.main.engine.inventory

import org.koin.core.component.get
import org.qbrp.main.core.game.serialization.Identifiable
import org.qbrp.main.core.game.storage.Storage
import org.qbrp.main.core.keybinds.ServerKeybindCallback
import org.qbrp.main.core.mc.player.PlayerObject
import org.qbrp.main.core.mc.player.ServerPlayerObject
import org.qbrp.main.core.modules.Autoload
import org.qbrp.main.core.modules.LoadPriority
import org.qbrp.main.core.modules.QbModule
import org.qbrp.main.core.utils.networking.messages.Message
import org.qbrp.main.core.utils.networking.messages.components.Cluster
import org.qbrp.main.core.utils.networking.messages.components.readonly.ClusterViewer
import org.qbrp.main.core.utils.networking.messages.types.Signal
import org.qbrp.main.core.utils.networking.messaging.NetworkUtil
import org.qbrp.main.core.utils.networking.messaging.ServerReceiver
import org.qbrp.main.core.utils.networking.messaging.ServerReceiverContext
import org.qbrp.main.engine.items.model.ItemStorage
import org.qbrp.main.engine.items.model.ServerItemObject

@Autoload(LoadPriority.LOWEST)
class InventoriesModule: QbModule("inventories"), PlayerHeldItems {
    companion object {
        const val TAKE_ITEM = "inventory_take_item"
        const val PUT_ITEM = "inventory_put_item"
        const val HELD_ITEM = "inventory_held_item"
        const val SWAP_ITEM = "inventory_swap_item"
    }
    private val objectStorage = get<ItemStorage<ServerItemObject>>()
    private val heldItemsMap = mutableMapOf<PlayerObject, InventoryEntry?>()

    override fun onEnable() {
        ServerReceiver<ServerReceiverContext>(TAKE_ITEM, Cluster::class, { message, context, receiver ->
            val cluster = message.getContent<ClusterViewer>()
            val objectId = cluster.getComponentData<String>("objectId")!!
            val containerId = cluster.getComponentData<String>("containerId")!!
            val slot = cluster.getComponentData<Int>("slot")!!
            setHeldItem(context.playerObj,
                objectStorage.getById(objectId)!!
                .getComponent<InventoryHandler>()
                ?.handleTakeItem(containerId, slot, context.playerObj)
            )
            true
        }).register()
        ServerReceiver<ServerReceiverContext>(PUT_ITEM, Cluster::class, { message, context, receiver ->
            val cluster = message.getContent<ClusterViewer>()
            val objectId = cluster.getComponentData<String>("objectId")!!
            val containerId = cluster.getComponentData<String>("containerId")!!
            val slot = cluster.getComponentData<Int>("slot")!!
            val player = context.playerObj
            val heldItem = getHeldItem(player)
            if (heldItem != null) {
                setHeldItem(player, objectStorage.getById(objectId)!!
                        .getComponent<InventoryHandler>()
                        ?.handlePutItem(containerId, heldItem, slot, player)
                )
            }
            true
        }).register()
        ServerReceiver<ServerReceiverContext>(SWAP_ITEM, Cluster::class, { message, context, receiver ->
            val cluster = message.getContent<ClusterViewer>()
            val objectId = cluster.getComponentData<String>("objectId")!!
            val containerId = cluster.getComponentData<String>("containerId")!!
            val slot = cluster.getComponentData<Int>("slot")!!
            val player = context.playerObj
            val heldItem = getHeldItem(player)
            if (heldItem != null) {
                setHeldItem(player, objectStorage.getById(objectId)!!
                    .getComponent<InventoryHandler>()
                    ?.handlePutItem(containerId, heldItem, slot, player)
                )
            }
            true
        }).register()
    }

    override fun getHeldItem(player: ServerPlayerObject): InventoryEntry? {
        return heldItemsMap[player]
    }

    override fun setHeldItem(
        player: ServerPlayerObject,
        item: InventoryEntry?
    ) {
        heldItemsMap[player] = item
        NetworkUtil.sendMessage(
            Message(HELD_ITEM, item?.toCluster(player) ?: Cluster()), player.entity
        )
    }
}