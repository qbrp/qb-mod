package org.qbrp.main.network.item

import kotlinx.serialization.Serializable
import org.qbrp.main.engine.chemistry.params.PhysicalParameters
import org.qbrp.main.engine.items.ModelId
import org.qbrp.main.engine.items.model.ItemId
import org.qbrp.main.engine.players.model.ServerPlayerObject
import org.qbrp.main.engine.props.components.Dimensions
import org.qbrp.main.engine.text.components.TextComponent
import org.qbrp.main.engine.text.literal
import org.qbrp.main.engine.items.ItemModelId
import org.qbrp.main.engine.items.display.components.ItemDisplay
import org.qbrp.main.engine.physics.system.entity.BodyId
import org.qbrp.main.network.physics.ShapeDto
import org.qbrp.main.network.transport.readTextComponent
import org.qbrp.main.network.transport.NetworkChannel
import org.qbrp.main.network.transport.Packet
import org.qbrp.main.network.transport.PacketCodec
import org.qbrp.main.network.transport.wrapEndpoint
import org.qbrp.main.network.transport.writeId
import org.qbrp.main.network.transport.writeTextComponent

interface ItemPacket : Packet {
    val itemId: ItemId
}

val ITEM_DISPLAY_ENDPOINT = NetworkChannel<ItemDisplayPacket>(
    codec = PacketCodec.Binary(
        {
            ItemDisplayPacket(
                ItemId(readString()),
                ItemModelId(readString()),
                readTextComponent(),
                readList { buf -> buf.readTextComponent() }
            )
        },
        { p ->
            writeId(p.itemId)
            writeString(p.model.modelId)
            writeTextComponent(p.name)
            writeCollection(p.description) { buf, component ->
                buf.writeTextComponent(component)
            }
        }
    )
).wrapEndpoint()

data class ItemDisplayPacket(
    override val itemId: ItemId,
    val model: ModelId,
    val name: TextComponent,
    val description: List<TextComponent>
) : ItemPacket

fun ItemDisplay.sendPacket(player: ServerPlayerObject) {
    val packet = ItemDisplayPacket(
        ItemId(item.id),
        model,
        literal(getName()),
        getDescription(player),
    )
    ITEM_DISPLAY_ENDPOINT
        .sendS2C(packet, player)
}

val ITEM_PHYSICAL_PARAMETERS_ENDPOINT = NetworkChannel<ItemPhysicalParametersPacket>()
    .wrapEndpoint()

@Serializable
data class PropData(val bodyId: BodyId, val shape: ShapeDto)

@Serializable
data class PhysicalParametersDto(override val mass: Double) : PhysicalParameters

fun PhysicalParameters.toDto() = PhysicalParametersDto(mass)

@Serializable
data class ItemPhysicalParametersPacket(
    override val itemId: ItemId,
    val dimensions: Dimensions?,
    val prop: PropData?,
    val physicalParameters: PhysicalParametersDto?
) : ItemPacket
