package org.qbrp.client.engine.items

import net.fabricmc.api.EnvType
import org.qbrp.client.core.synchronization.ComponentConverter
import org.qbrp.client.core.synchronization.ObjectChannelReceiver
import org.qbrp.main.core.game.ComponentRegistryInitializationEvent
import org.qbrp.main.core.modules.Autoload
import org.qbrp.main.core.modules.QbModule
import org.qbrp.main.engine.items.ItemsModule
import org.qbrp.main.engine.items.components.Tooltip
import org.qbrp.main.engine.items.model.ItemStorage

@Autoload(env = EnvType.CLIENT)
class ClientItems() : QbModule("client-items"), ClientItemsAPI {
    override fun getKoinModule() = inner {
        scoped { ItemStorage<ClientItemObject>() }
    }

    override val storage: ItemStorage<ClientItemObject> get() = getLocal()

    override fun onEnable() {
        ObjectChannelReceiver<ClientItemObject>(ItemsModule.ITEMS_CHANNEL, storage) { cluster, id ->
            ClientItemObject(cluster.getComponentData("id")!!)
        }
            .addFabric(ComponentConverter(Tooltip::class, { cluster ->
                Tooltip(cluster.getComponentData("tooltip")!!)
            }))
            .run()
        ComponentRegistryInitializationEvent.EVENT.register {
            it.register(Tooltip::class.java)
        }
    }
}