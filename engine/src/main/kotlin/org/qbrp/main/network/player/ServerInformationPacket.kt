package org.qbrp.main.network.player

import kotlinx.serialization.Serializable
import org.koin.core.Koin
import org.qbrp.deprecated.resources.data.config.ServerConfigData
import org.qbrp.main.core.CoreContext
import org.qbrp.main.engine.chat.addons.groups.ChatGroupsAPI
import org.qbrp.main.engine.chat.core.system.ChatGroup
import org.qbrp.main.engine.players.model.ServerPlayerObject
import org.qbrp.main.network.transport.NetworkChannel
import org.qbrp.main.network.transport.Packet
import org.qbrp.main.network.transport.taskPacket
import org.qbrp.main.network.transport.wrapEndpoint

val SERVER_INFORMATION_ENDPOINT = NetworkChannel<ServerInformationPacket>()
    .wrapEndpoint()

@Serializable
data class ServerInformationPacket(val information: ServerInformationDto) : Packet

data class ServerInformation(
    val contentPacks: ContentPacks,
    val chatGroups: List<ChatGroup>
) {
    @Serializable
    data class ContentPacks(
        val enabled: Boolean,
        val serverName: String,
        val downloadPort: Int
    )
}

fun ServerInformation.toDto(): ServerInformationDto {
    val groups = chatGroups.map { ChatGroupDto(it.name, it.simpleName, it.prefix, it.color) }
    return ServerInformationDto(contentPacks, groups)
}

fun ServerInformationDto.map(): ServerInformation {
    val groups = chatGroups.map { ChatGroup(it.name, it.simpleName, it.prefix, it.color) }
    return ServerInformation(contentPacks, groups)
}

// Dto

@Serializable
data class ChatGroupDto(
    val name: String,
    val simpleName: String,
    val prefix: String,
    val color: String
)

@Serializable
data class ServerInformationDto(
    val contentPacks: ServerInformation.ContentPacks,
    val chatGroups: List<ChatGroupDto>
)

// Send

suspend fun ServerPlayerObject.sendServerInformationPacket(koin: Koin) {
    val config = koin.get<ServerConfigData>()
    val chatGroups = koin.get<ChatGroupsAPI>()

    val contentPacks = ServerInformation.ContentPacks(
        true,
        config.serverName,
        config.http.port
    )

    val groups = chatGroups.getStorage().getAllGroups()

    SERVER_INFORMATION_ENDPOINT
        .taskS2C(
            ServerInformationPacket(
                ServerInformation(contentPacks, groups).toDto()
            ),
            this
        )
        .queue()
        .requestAcknowledge()
}