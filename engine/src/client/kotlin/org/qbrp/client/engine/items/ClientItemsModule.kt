package org.qbrp.client.engine.items

import net.fabricmc.api.EnvType
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
import org.qbrp.client.core.networking.utils.clearOnDisconnect
import org.qbrp.main.core.components.ComponentRegistryInitializationEvent
import org.qbrp.main.core.modules.Autoload
import org.qbrp.main.core.modules.EngineModule
import org.qbrp.main.engine.items.display.components.Description
import org.qbrp.main.engine.items.service.ItemStorage
import org.qbrp.main.engine.props.components.Dimensions

typealias ClientItemStorage = ItemStorage<ClientItemObject>

@Autoload(env = EnvType.CLIENT)
class ClientItemsModule() : EngineModule("client-items") {
    override fun getKoinModule() = innerWithApi({
        single { ItemStorage<ClientItemObject>() }
    }, {})

    private val storage: ItemStorage<ClientItemObject> get() = getLocal()

    override fun onEnable() {
        storage.clearOnDisconnect()

        ClientTickEvents.END_WORLD_TICK.register {
            storage.forEach { it.tickStateExact() }
        }

        ComponentRegistryInitializationEvent.EVENT.register {
            it.register { data<Description>() }
            it.register { data<SyncedPhysicalParameters>() }
            it.register { componentClass(ClientItemDisplayComponent::class) }
            it.register { data<PropInterpolation>(false) }
            it.register { data<Dimensions>() }
        }
    }
}