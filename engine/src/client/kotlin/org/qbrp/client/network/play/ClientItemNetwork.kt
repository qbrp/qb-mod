package org.qbrp.client.network.play

import net.minecraft.client.MinecraftClient
import org.koin.core.component.KoinComponent
import org.koin.core.context.GlobalContext
import org.qbrp.client.core.resources.ModelRepository
import org.qbrp.client.engine.items.ClientItemObject
import org.qbrp.client.engine.items.ClientItemStorage
import org.qbrp.client.engine.items.ClientItemDisplayComponent
import org.qbrp.client.engine.items.SyncedPhysicalParameters
import org.qbrp.client.engine.items.setPropData
import org.qbrp.client.engine.players.ClientPlayerManager
import org.qbrp.client.network.transport.ClientNetworkContext
import org.qbrp.client.network.transport.registerClientReceiver
import org.qbrp.main.core.components.extensions.getOrAdd
import org.qbrp.main.engine.items.model.ItemId
import org.qbrp.main.engine.physics.system.entity.Shape
import org.qbrp.main.engine.physics.system.service.BodyFactory
import org.qbrp.main.engine.physics.system.service.WorldBodyManager
import org.qbrp.main.engine.players.model.pos
import org.qbrp.main.engine.props.PropDescriptor
import org.qbrp.main.engine.world.location.asWorld
import org.qbrp.main.network.item.ITEM_DISPLAY_ENDPOINT
import org.qbrp.main.network.item.ITEM_PHYSICAL_PARAMETERS_ENDPOINT
import org.qbrp.main.network.item.ItemPacket
import org.qbrp.main.network.physics.toEntity
import org.qbrp.main.network.transport.Endpoint
import java.util.concurrent.LinkedBlockingQueue
import kotlin.concurrent.thread

private data class PropCommand(
    val item: ClientItemObject,
    val shape: Shape,
    val id: BodyServerId,
    var acknowledges: Int = 0
)

fun KoinComponent.runClientItemNetworkHandler(
    modelRepository: ModelRepository,
    bodyManager: WorldBodyManager,
    bodyFactory: BodyFactory,
    bodyIdTable: BodyIdTable,
    playerManager: ClientPlayerManager
) {
    val propCommandQueue = LinkedBlockingQueue<PropCommand>()

    thread(name = "Engine Prop Network Handler", isDaemon = true) {
        while (true) {
            val snapshot = propCommandQueue.take()

            val item = snapshot.item
            val clientId = bodyIdTable.getClientIdOrNull(snapshot.id)
            var body = clientId?.let { bodyManager.getBody(it) }
            if (body == null) {
                val playerPos = playerManager.clientPlayer?.pos ?: return@thread
                runCatching {
                    val new = bodyFactory.build(
                        PropDescriptor(
                            item,
                            playerPos,
                            shape = snapshot.shape
                        )
                    )
                    body = new
                }.let { result ->
                    val acknowledges = snapshot.acknowledges
                    val ex = result.exceptionOrNull()
                    if (!result.isSuccess && acknowledges < 100) {
                        propCommandQueue.add(snapshot)
                        snapshot.acknowledges++
                    } else if (ex != null) {
                        ex.printStackTrace()
                    }
                }
            }

            body?.let {
                bodyManager.addBody(it, true)
                item.setPropData(it)
            }
        }
    }

    ITEM_DISPLAY_ENDPOINT
        .registerClientItemReceiver { ctx, item ->
            item
                .getOrAdd {
                    ClientItemDisplayComponent(
                        item.state.componentContext,
                        modelRepository,
                        MinecraftClient.getInstance().bakedModelManager
                    )
                }
                .also {
                    it.name = name
                    it.description = description
                    it.model = model
                }
        }

    ITEM_PHYSICAL_PARAMETERS_ENDPOINT
        .registerClientItemReceiver { ctx, item ->
            dimensions?.let {
                item.getOrAdd { it }
            }
            physicalParameters?.let {
                val params = item.getOrAdd { SyncedPhysicalParameters(it.mass) }
                params.mass = it.mass
            }
            prop?.let {
                propCommandQueue += PropCommand(item, it.shape.toEntity(), BodyServerId(it.bodyId.value))
            }
        }
}

typealias ItemPacketHandler<P> = context(ClientNetworkContext) P.(ClientNetworkContext, ClientItemObject) -> Unit

private fun <P : ItemPacket> Endpoint<P>.registerClientItemReceiver(
    itemStorage: ClientItemStorage = GlobalContext.get().get(),
    handler: ItemPacketHandler<P>,
) {
    registerClientReceiver { ctx ->
        val item = createItemIfNotExists(itemId, itemStorage)
        this.handler(ctx, item)
    }
}

private fun createItemIfNotExists(id: ItemId, itemStorage: ClientItemStorage): ClientItemObject {
    return itemStorage.getById(id.value) ?: ClientItemObject(id).also {
        it.state.parent = it
        itemStorage.add(it)
    }
}