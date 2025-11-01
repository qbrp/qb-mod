package org.qbrp.main.network.player

import kotlinx.serialization.Serializable
import net.minecraft.network.PacketByteBuf
import net.minecraft.server.network.ServerPlayerEntity
import org.koin.core.component.KoinComponent
import org.koin.core.context.GlobalContext
import org.qbrp.main.core.fabricmc.isOp
import org.qbrp.main.engine.chat.ChatAPI
import org.qbrp.main.engine.chat.ChatModule
import org.qbrp.main.engine.chat.core.messages.ChatMessage
import org.qbrp.main.engine.chat.core.system.ServerChatNetworking
import org.qbrp.main.network.transport.NetworkChannel
import org.qbrp.main.network.transport.Packet
import org.qbrp.main.network.transport.ServerNetworkContext
import org.qbrp.main.network.transport.wrapEndpoint

data class Mod(val id: String, val ifFounded: ServerNetworkContext.(ConnectionSession) -> Unit)

internal val MINIMAP_WARNING =
    """Моды на мини-карты запрещены, поскольку позволяют получать информацию о местонахождении игроков и построек. 
      |Это нарушает принцип мета-информации и невозможно по РП: ваш персонаж в теории не способен знать это.
    """.trimMargin()

internal val MODS = listOf(
    Mod("freecam") { connection -> notifyPlayerAndOperators(playerEntity, "Freecam") },
    Mod("xaeroworldmap") { connection -> kickAndNotifyOperators(connection, "Xaero's World Map", MINIMAP_WARNING) },
    Mod("xaerominimap") { connection -> kickAndNotifyOperators(connection, "Xaero's Minimap", MINIMAP_WARNING) },
    Mod("voxelmap") { connection -> kickAndNotifyOperators(connection, "Voxel Map", MINIMAP_WARNING) },
)

internal fun ServerNetworkContext.notifyPlayerAndOperators(player: ServerPlayerEntity, id: String) {
    val name = player.displayName.string
    serverDependencies.koin.get<ServerChatNetworking>().sendMessagePacket(
        player,
        ChatMessage(
            ChatModule.SYSTEM_MESSAGE_AUTHOR,
            """<red>Вы используете мод <bold>${id}</bold></red>
                |Его использование способствует получению мета-информации, для игры на сервере он запрещен.
            """.trimMargin()
        )
    )
    sendChatMessageToOperators("\red>[!]<reset><gold> $name зашел на сервер с $id</gold>")
}

internal fun ServerNetworkContext.kickAndNotifyOperators(player: ConnectionSession, name: String, warn: String) {
    val reason = DisconnectReason("<bold>Вы были исключены с сервера из-за мода ${name}</bold><newline>${warn}")
    player.disconnect(reason)
    sendChatMessageToOperators("\red>[!]<reset><gold> $name кикнут с сервера из-за мода $name</gold>")
}

fun ServerNetworkContext.sendChatMessageToOperators(text: String) {
    val chatApi: ChatAPI = GlobalContext.get().get()
    serverDependencies.playerStorage.forEach { if (it.isOp) it.sendChatMessage(text, chatApi) }
}

// Packet

val MOD_IDS_ENDPOINT = NetworkChannel<ModIdsPacket>()
    .wrapEndpoint()

@Serializable
data class ModIdsPacket(val mods: List<String>) : Packet

fun KoinComponent.runModIdsHandler() {
    MOD_IDS_ENDPOINT
        .registerReceiver { ctx ->
            val session = ctx.connectionSession
            var passed = true
            if (!session.isOp) {
                MODS.forEach {
                    if (mods.contains<String>(it.id)) {
                        it.ifFounded(ctx, session)
                        passed = false
                    }
                }
            }
            if (passed) {
                ctx.serverDependencies.connectionManager.markAntiCheatPassed(ctx.connectionSession)
            }
        }
}