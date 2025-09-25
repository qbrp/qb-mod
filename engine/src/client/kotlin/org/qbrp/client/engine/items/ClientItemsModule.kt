package org.qbrp.client.engine.items

import net.fabricmc.api.EnvType
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
import org.koin.core.component.get
import org.qbrp.client.core.networking.utils.enableClear
import org.qbrp.client.engine.items.components.physics.SyncedPhysicalParameters
import org.qbrp.client.engine.items.components.tooltip.ClientItemDisplayComponent
import org.qbrp.client.engine.items.networking.ItemSyncResolver
import org.qbrp.client.engine.items.networking.ItemNetworkingRegistrar
import org.qbrp.main.core.components.ComponentRegistryInitializationEvent
import org.qbrp.main.core.events.on
import org.qbrp.main.core.events.withEventContext
import org.qbrp.main.core.modules.Autoload
import org.qbrp.main.core.modules.EngineModule
import org.qbrp.main.core.networking.serialization.SerializersRegistry
import org.qbrp.main.core.networking.serialization.impl.ComponentPacketDtoMapperRegistry
import org.qbrp.main.core.networking.serialization.mapping.registerDataComponentMapper
import org.qbrp.main.core.networking.serialization.serializerId
import org.qbrp.main.engine.ModLoadedEvent
import org.qbrp.main.mechanics.items.display.components.Description
import org.qbrp.main.engine.items.service.ItemStorage

typealias ClientItemStorage = ItemStorage<ClientItemObject>

@Autoload(env = EnvType.CLIENT)
class ClientItemsModule() : EngineModule("client-items") {
    override fun getKoinModule() = innerWithApi({
        single {
            ItemStorage<ClientItemObject>()
                .apply { enableClear() }
        }
        single { ItemSyncResolver(get()) }
        single { ItemNetworkingRegistrar(get(), get(), get(), get()) }
    }, {})

    private val storage: ItemStorage<ClientItemObject> get() = getLocal()

    override fun onEnable() {
        ClientTickEvents.END_WORLD_TICK.register {
            storage.forEach { it.tickStateExact() }
        }
        get<SerializersRegistry>().registerSerializer<Description>(Description::class.serializerId(), Description.serializer())
        get<ComponentPacketDtoMapperRegistry>().registerDataComponentMapper<Description>()

        ComponentRegistryInitializationEvent.EVENT.register {
            it.register { data<Description>() }
            it.register { data<SyncedPhysicalParameters>() }
            it.register { componentClass(ClientItemDisplayComponent::class) }
        }

        withEventContext {
            on<ModLoadedEvent>() {
                get<ItemNetworkingRegistrar>().run()
            }
        }
    }
}