package org.qbrp.main.engine.players.model

import net.minecraft.util.ActionResult
import org.koin.core.context.GlobalContext
import org.qbrp.main.core.components.state.ComponentState
import org.qbrp.main.core.fabricmc.player.PlayerReference
import org.qbrp.main.core.fabricmc.player.MinecraftPlayerId
import org.qbrp.main.core.fabricmc.player.Username
import org.qbrp.main.engine.chat.core.messages.ChatMessage
import org.qbrp.main.engine.chat.core.messages.MessageSender
import org.qbrp.main.engine.chat.ChatAPI
import org.qbrp.main.engine.players.accounts.Account
import org.qbrp.main.network.player.ConnectionSession
import org.qbrp.main.core.util.PlayerId

typealias PlayerStateImpl = ComponentState<PlayerObject>

open class ServerPlayerObject(
    override val uuid: MinecraftPlayerId,
    override val username: Username,
    var account: Account,
    val session: ConnectionSession,
    state: PlayerState = PlayerStateImpl(),
) : PlayerObject(username, uuid, PlayerId(account.playerId), state), PlayerReference {
    fun onKeybind(id: String) {
        filterIsInstance<PlayerComponent>().forEach { it.onKeybind(id) }
    }

    fun sendChatMessage(text: String, api: ChatAPI = GlobalContext.get().get()) {
        api.sendMessage(this, text)
    }

    fun onMessageSend(sender: MessageSender, message: ChatMessage): ActionResult {
        forEach {
            it as PlayerComponent
            val result = it.onChatMessage(sender, message)
            if (result != ActionResult.PASS) return result
        }
        return ActionResult.PASS
    }
}
