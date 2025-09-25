package org.qbrp.main.core.networking

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.protobuf.ProtoBuf
import kotlinx.serialization.serializer
import net.minecraft.network.PacketByteBuf
import net.minecraft.server.MinecraftServer
import net.minecraft.util.Identifier
import org.koin.core.context.GlobalContext
import org.qbrp.main.core.Core
import org.qbrp.main.core.components.serialization.DTO
import org.qbrp.main.core.components.serialization.Identifiable
import org.qbrp.main.core.dataformats.StringId
import org.qbrp.main.core.networking.sender.ServerPacketSender
import org.qbrp.main.core.networking.serialization.SerializerRegistryId
import org.qbrp.main.core.networking.serialization.SerializersRegistry
import org.qbrp.main.core.networking.serialization.serializerId
import org.qbrp.main.core.networking.synchronization.DtoTask
import org.qbrp.main.engine.containers.util.ContainerId
import org.qbrp.main.engine.containers.util.GlobalContainerId
import org.qbrp.main.engine.items.model.ItemObject
import org.qbrp.main.engine.items.service.ItemStorage
import org.qbrp.main.engine.world.location.Position
import org.qbrp.main.engine.physics.dynamics.dataformats.QuatAdapter
import org.qbrp.main.engine.physics.dynamics.dataformats.Quat
import org.qbrp.main.engine.physics.dynamics.dataformats.Transform
import org.qbrp.main.engine.physics.dynamics.dataformats.TransformAdapter
import org.qbrp.main.engine.physics.dynamics.dataformats.Vec3Adapter
import org.qbrp.main.engine.players.model.PlayerObject
import org.qbrp.main.engine.players.model.ServerPlayerObject
import org.qbrp.main.engine.players.model.service.PlayerList
import org.qbrp.main.engine.players.model.service.PlayerStorage
import org.qbrp.main.engine.text.components.TextComponent
import org.qbrp.main.engine.text.map.TextDto
import org.qbrp.main.engine.text.map.TextMappingRegistry
import org.qbrp.main.engine.world.model.World

fun toIdentifier(str: String): Identifier {
    return Identifier(Core.MOD_ID, str)
}

fun ServerPacketSender.sendAll(packet: Packet<*>, channelName: String, playerList: PlayerList) {
    playerList.forEach {
        sendImmediately(packet, channelName, it)
    }
}

fun getPlayersInRadius(pos: Position, world: World, radius: Double): List<ServerPlayerObject> {
    val radiusSquared = radius * radius
    return world.players.filter {
        it.pos.squaredDistanceTo(pos) <= radiusSquared
    }
}

fun getPlayersInRadius(player: ServerPlayerObject, radius: Double): List<ServerPlayerObject> {
    val radiusSquared = radius * radius
    return player.context.world.players.filter {
        it !== player && it.pos.squaredDistanceTo(player.pos) <= radiusSquared
    }
}

fun PacketByteBuf.writeStringList(strings: List<String>) {
    this.writeVarInt(strings.size)
    for (str in strings) {
        this.writeString(str)
    }
}

fun PacketByteBuf.readStringList(): List<String> {
    val size = this.readVarInt()
    return List(size) { this.readString() }
}

fun PacketByteBuf.writePlayer(player: PlayerObject) {
    writeString(player.id)
}

fun PacketByteBuf.readPlayer(storage: PlayerStorage<out PlayerObject>): PlayerObject? {
    return storage.getById(readString())
}


fun PacketByteBuf.writeItem(item: ItemObject) {
    writeString(item.id)
}

fun PacketByteBuf.readItem(storage: ItemStorage<out ItemObject>): ItemObject? {
    return storage.getById(readString())
}

@OptIn(ExperimentalSerializationApi::class)
fun <T> PacketByteBuf.writeProtobuf(data: T, serializer: KSerializer<T>) {
    val bytes = ProtoBuf.encodeToByteArray(serializer, data)
    this.writeByteArray(bytes)
}

@OptIn(ExperimentalSerializationApi::class)
inline fun <reified T: Any> PacketByteBuf.readProtobuf(serializer: KSerializer<T>): T {
    return ProtoBuf.decodeFromByteArray<T>(serializer, readByteArray())
}

@OptIn(ExperimentalSerializationApi::class, InternalSerializationApi::class)
inline fun <reified T: Any> PacketByteBuf.readProtobuf(): T {
    return ProtoBuf.decodeFromByteArray<T>(T::class.serializer(), readByteArray())
}

@OptIn(ExperimentalSerializationApi::class)
fun <T> PacketByteBuf.writeProtobuf(data: T, serializerId: SerializerRegistryId, registry: SerializersRegistry = GlobalContext.get().get()) {
    val bytes = ProtoBuf.encodeToByteArray(registry.getSerializationStrategy(serializerId), data)
    this.writeByteArray(bytes)
}

@OptIn(ExperimentalSerializationApi::class)
fun <T> PacketByteBuf.readProtobuf(serializerId: SerializerRegistryId, registry: SerializersRegistry = GlobalContext.get().get()): T {
    return ProtoBuf.decodeFromByteArray<T>(registry.getDeserializationStrategy(serializerId), readByteArray())
}

@OptIn(ExperimentalSerializationApi::class, InternalSerializationApi::class)
fun PacketByteBuf.writeDtos(dtos: List<DtoTask>) {
    writeInt(dtos.size)
    dtos.forEach { task ->
        writeString(task.registryId.id)
        writeString(task.name)
        writeProtobuf(task.data, task.registryId)
    }
}

@OptIn(ExperimentalSerializationApi::class, InternalSerializationApi::class)
fun <T: DTO> PacketByteBuf.readDtos(): List<DtoTask> {
    val count = readInt()
    val list = mutableListOf<DtoTask>()
    repeat(count) {
        val idString = readString()
        val nameString = readString()
        val registryId = idString.serializerId()
        val data = readProtobuf<T>(registryId)
        list += DtoTask(registryId, data, nameString)
    }
    return list
}

fun PacketByteBuf.writeTextComponent(textComponent: TextComponent, registry: TextMappingRegistry = GlobalContext.get().get()) {
    val dto = registry.toDto(textComponent)
    writeProtobuf(dto, TextDto.serializer())
}

fun PacketByteBuf.readTextComponent(registry: TextMappingRegistry = GlobalContext.get().get()): TextComponent {
    val dto = readProtobuf<TextDto>()
    return registry.fromDto(dto)
}

fun PacketByteBuf.writePos(pos: Position) {
    writeDouble(pos.x)
    writeDouble(pos.y)
    writeDouble(pos.z)
}

fun PacketByteBuf.writeQuat(quat: Quat) {
    writeDouble(quat.x)
    writeDouble(quat.y)
    writeDouble(quat.z)
    writeDouble(quat.w)
}

fun PacketByteBuf.readPos(): Position {
    return Vec3Adapter(readDouble(), readDouble(), readDouble())
}

fun PacketByteBuf.readQuat(): QuatAdapter {
    return QuatAdapter(readDouble(), readDouble(), readDouble(), readDouble())
}

fun PacketByteBuf.writeTransform(transform: Transform) {
    writePos(transform.pos)
    writeQuat(transform.quat)
}

fun PacketByteBuf.readTransform(): Transform {
    return TransformAdapter(readPos(), readQuat())
}

fun PacketByteBuf.writeId(id: Identifiable) {
    writeString(id.id)
}

fun PacketByteBuf.readStringId(): StringId {
    return StringId(readString())
}

fun PacketByteBuf.readContainerId(): ContainerId {
    return ContainerId(readString())
}

inline fun <reified E: Enum<E>> PacketByteBuf.writeEnum(e: Enum<E>) {
    writeEnumConstant(e)
}

inline fun <reified E: Enum<E>> PacketByteBuf.readEnum(): E {
    return readEnumConstant<E>(E::class.java)
}

fun PacketByteBuf.writeGlobalContainerId(id: GlobalContainerId) {
    writeId(id.obj)
    writeId(id.container)
}

fun PacketByteBuf.readGlobalContainerId(): GlobalContainerId {
    return GlobalContainerId(readStringId(), readContainerId())
}