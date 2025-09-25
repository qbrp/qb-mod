package org.qbrp.client.engine.props

import net.fabricmc.api.EnvType
import org.koin.core.component.get
import org.koin.core.component.inject
import org.qbrp.client.core.networking.v2.ClientContext
import org.qbrp.client.engine.items.ClientItemStorage
import org.qbrp.main.core.components.ComponentRegistryInitializationEvent
import org.qbrp.main.core.components.extensions.getOrAdd
import org.qbrp.main.core.modules.Autoload
import org.qbrp.main.core.modules.EngineModule
import org.qbrp.main.core.networking.PacketListenerActivator
import org.qbrp.main.core.networking.serialization.SerializersRegistry
import org.qbrp.main.core.networking.serialization.impl.ComponentPacketDtoMapperRegistry
import org.qbrp.main.core.networking.serialization.mapping.registerDataComponentMapper
import org.qbrp.main.core.networking.serialization.serializerId
import org.qbrp.main.engine.physics.dynamics.dataformats.TransformAdapter
import org.qbrp.main.engine.props.components.Dimensions
import org.qbrp.main.engine.props.networking.PropTransformBatchPacket
import org.qbrp.main.engine.props.networking.PropTransformListener
import org.qbrp.main.engine.props.networking.PropTransformSynchronizer

@Autoload(env = EnvType.CLIENT)
class ClientPropsModule: EngineModule("client-props"), PropTransformListener {
    private val itemStorage by inject<ClientItemStorage>()
    override fun onEnable() {
        get<SerializersRegistry>().registerSerializer<Dimensions>(Dimensions::class.serializerId(), Dimensions.serializer())
        get<ComponentPacketDtoMapperRegistry>().registerDataComponentMapper<Dimensions>()

        ComponentRegistryInitializationEvent.EVENT.register {
            it.register { data<PropData>(false) }
            it.register { data<Dimensions>() }
        }
        get<PacketListenerActivator<ClientContext>>().run(
            PropTransformSynchronizer.CHANNEL,
            this,
            { buf, ctx -> PropTransformBatchPacket(buf) })
    }

    override fun onTransform(
        id: String,
        transform: TransformAdapter
    ) {
        val item = itemStorage.getById(id) ?: return
        item.getOrAdd { PropData(transform) }.transform = transform
    }
}