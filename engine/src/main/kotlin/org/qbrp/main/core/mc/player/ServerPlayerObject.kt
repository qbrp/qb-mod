package org.qbrp.main.core.mc.player

import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.server.world.ServerWorld
import net.minecraft.text.Text
import net.minecraft.util.ActionResult
import net.minecraft.util.math.Vec3d
import org.qbrp.main.core.game.loop.Tick
import org.qbrp.main.core.game.model.State
import org.qbrp.main.core.mc.player.service.PlayerBehaviour
import org.qbrp.main.engine.chat.core.messages.ChatMessage
import org.qbrp.main.engine.chat.core.messages.MessageSender
import org.qbrp.main.engine.players.nicknames.NicknamesModule
import org.qbrp.main.core.utils.networking.messages.Message
import org.qbrp.main.core.utils.networking.messages.Messages
import org.qbrp.main.core.utils.networking.messages.types.StringContent
import org.qbrp.main.core.utils.networking.messaging.NetworkUtil
import org.qbrp.main.core.utils.format.Format.asMiniMessage
import org.qbrp.main.core.utils.networking.messages.components.Cluster
import org.qbrp.main.core.utils.networking.messages.components.ClusterBuilder
import org.qbrp.main.core.synchronization.components.MessagingChannelSender
import org.qbrp.main.core.synchronization.components.S2CMessaging
import org.qbrp.main.core.synchronization.position.SquaredRadiusSynchronizable

class ServerPlayerObject(
    override val entity: ServerPlayerEntity,
    var account: Account,
    state: State,
    override val messageSender: MessagingChannelSender
): PlayerObject(state = state), Tick<ServerWorld>, SquaredRadiusSynchronizable, S2CMessaging {
    override val id: String get() = account.uuid
    override val entityName: String
        get() = entity.name.string

    override val pos: Vec3d = entity.pos
    override val syncDistance: Int = 128

    override fun getCluster(): Cluster {
        return ClusterBuilder().component("playerName", entityName).build()
    }

    fun sendNetworkMessage(message: Message) {
        NetworkUtil.sendMessage(message, entity)
    }

    fun onMessageSend(sender: MessageSender, message: ChatMessage): ActionResult {
        state.behaviours.forEach {
            it as PlayerBehaviour
            val result = it.onChatMessage(sender, message)
            if (result != ActionResult.PASS) return result
        }
        return ActionResult.PASS
    }

    val displayName: String
        get() = state.getComponent<NicknamesModule.NicknameManager>()?.getDisplayName()
            ?: entity.name.string

    val displayNameText: Text
        get() = displayName.asMiniMessage()

    fun executeCommand(command: String) {
        NetworkUtil.sendMessage(Message(Messages.INVOKE_COMMAND, StringContent(command)), entity)
    }
}
