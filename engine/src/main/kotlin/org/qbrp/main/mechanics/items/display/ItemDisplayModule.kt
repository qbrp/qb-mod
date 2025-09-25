package org.qbrp.main.mechanics.items.display

import org.koin.core.component.get
import org.qbrp.main.core.components.ComponentRegistryInitializationEvent
import org.qbrp.main.core.modules.Autoload
import org.qbrp.main.core.modules.EngineModule
import org.qbrp.main.core.networking.serialization.SerializersRegistry
import org.qbrp.main.core.networking.serialization.impl.ComponentPacketDtoMapperRegistry
import org.qbrp.main.core.networking.serialization.mapping.registerDataComponentMapper
import org.qbrp.main.core.networking.serialization.serializerId
import org.qbrp.main.engine.items.model.ItemState
import org.qbrp.main.mechanics.ItemModelId
import org.qbrp.main.mechanics.items.display.components.Brief
import org.qbrp.main.mechanics.items.display.components.Description
import org.qbrp.main.mechanics.items.display.components.ItemDisplay
import org.qbrp.main.mechanics.items.display.components.Name

@Autoload
class ItemDisplayModule: EngineModule("items-display") {
    override fun onEnable() {
        get<SerializersRegistry>().registerSerializer<Description>(Description::class.serializerId(), Description.serializer())
        get<ComponentPacketDtoMapperRegistry>().registerDataComponentMapper<Description>()

        ComponentRegistryInitializationEvent.EVENT.register {
            it.register { data<Brief>() }
            it.register { data<Description>() }
            it.register { data<Name>() }
            it.register {
                componentClass(ItemDisplay::class)
                serializeConstructor<ItemDisplay.Data, ItemState, ItemDisplay> { builder, dto ->
                    ItemDisplay(builder.componentContext, ItemModelId(dto.model))
                }
            }
        }
    }
}