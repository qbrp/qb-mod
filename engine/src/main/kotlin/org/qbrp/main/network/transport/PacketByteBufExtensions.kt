package org.qbrp.main.network.transport

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.protobuf.ProtoBuf
import kotlinx.serialization.serializer
import net.minecraft.network.PacketByteBuf
import net.minecraft.util.Identifier
import org.koin.core.context.GlobalContext
import org.qbrp.main.core.Core
import org.qbrp.main.core.components.serialization.Identifiable
import org.qbrp.main.core.util.StringId
import org.qbrp.main.core.networking.serialization.SerializerRegistryId
import org.qbrp.main.core.networking.serialization.SerializersRegistry
import org.qbrp.main.core.util.math.Pos
import org.qbrp.main.core.util.math.Quat
import org.qbrp.main.core.util.math.Vec3
import org.qbrp.main.core.util.math.asVec3
import org.qbrp.main.engine.containers.util.ContainerId
import org.qbrp.main.engine.containers.util.GlobalContainerId
import org.qbrp.main.engine.items.model.ItemObject
import org.qbrp.main.engine.items.service.ItemStorage
import org.qbrp.main.engine.physics.system.entity.BodyId
import org.qbrp.main.engine.physics.system.entity.Transform
import org.qbrp.main.engine.physics.system.entity.asPhysical
import org.qbrp.main.engine.players.model.PlayerObject
import org.qbrp.main.engine.players.model.ServerPlayerObject
import org.qbrp.main.engine.players.model.pos
import org.qbrp.main.engine.players.model.service.PlayerStorage
import org.qbrp.main.engine.players.model.world
import org.qbrp.main.engine.text.components.TextComponent
import org.qbrp.main.engine.text.map.TextDto
import org.qbrp.main.engine.text.map.TextMappingRegistry
import org.qbrp.main.engine.world.location.WorldPos
import org.qbrp.main.engine.world.model.World

fun toIdentifier(str: String): Identifier {
    return Identifier(Core.MOD_ID, str)
}

fun getPlayersInRadius(pos: WorldPos, world: World, radius: Double): List<ServerPlayerObject> {
    val radiusSquared = radius * radius
    return world.players.filter {
        it.pos.asVec3().squaredDistanceTo(pos) <= radiusSquared
    }
        .filterIsInstance<ServerPlayerObject>()
}

fun getPlayersInRadius(player: ServerPlayerObject, radius: Double): List<PlayerObject> {
    val radiusSquared = radius * radius
    return player.world.players.filter {
        it !== player && it.pos.asVec3().squaredDistanceTo(player.pos) <= radiusSquared
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

fun PacketByteBuf.writeTextComponent(textComponent: TextComponent, registry: TextMappingRegistry = GlobalContext.get().get()) {
    val dto = registry.toDto(textComponent)
    writeProtobuf(dto, TextDto.serializer())
}

fun PacketByteBuf.readTextComponent(registry: TextMappingRegistry = GlobalContext.get().get()): TextComponent {
    val dto = readProtobuf<TextDto>()
    return registry.fromDto(dto)
}

fun PacketByteBuf.writeVec3(v: Vec3) {
    writeDouble(v.x)
    writeDouble(v.y)
    writeDouble(v.z)
}

fun PacketByteBuf.writePos(p: Pos) {
    writeDouble(p.x)
    writeDouble(p.y)
    writeDouble(p.z)
}

fun PacketByteBuf.writeQuat(quat: Quat) {
    writeDouble(quat.x)
    writeDouble(quat.y)
    writeDouble(quat.z)
    writeDouble(quat.w)
}

fun PacketByteBuf.readPos(): Pos {
    return Vec3(readDouble(), readDouble(), readDouble())
}

fun PacketByteBuf.readVec3(): Vec3 {
    return Vec3(readDouble(), readDouble(), readDouble())
}

fun PacketByteBuf.readQuat(): Quat {
    return Quat(readDouble(), readDouble(), readDouble(), readDouble())
}

fun PacketByteBuf.writeTransform(transform: Transform) {
    writeVec3(transform.pos.asVec3())
    writeQuat(transform.rot.q)
}

fun PacketByteBuf.readTransform(): Transform {
    return Transform(
        readVec3().asPhysical(),
        readQuat().asPhysical()
    )
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

fun PacketByteBuf.writeBodyId(id: BodyId) {
    writeInt(id.value)
}

fun PacketByteBuf.readBodyId(): BodyId {
    return BodyId(readInt())
}