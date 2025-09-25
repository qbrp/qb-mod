package org.qbrp.main.engine.players.model

import net.minecraft.text.Text
import net.minecraft.util.ActionResult
import org.koin.core.context.GlobalContext
import org.qbrp.main.core.components.extensions.get
import org.qbrp.main.core.components.extensions.require
import org.qbrp.main.core.components.state.ComponentState
import org.qbrp.main.core.mc.player.dataformats.MinecraftPlayerId
import org.qbrp.main.core.mc.player.dataformats.Username
import org.qbrp.main.core.mc.player.model.PlayerSnapshot
import org.qbrp.main.core.utils.format.Format.formatMm
import org.qbrp.main.engine.Colors
import org.qbrp.main.engine.players.model.components.PlayerComponent
import org.qbrp.main.engine.chat.core.messages.ChatMessage
import org.qbrp.main.engine.chat.core.messages.MessageSender
import org.qbrp.main.engine.chat.ChatAPI
import org.qbrp.main.engine.controls.model.player.PlayerInteractable
import org.qbrp.main.engine.players.accounts.Account
import org.qbrp.main.engine.players.auth.SessionData
import org.qbrp.main.engine.players.model.components.PlayerState
import org.qbrp.main.engine.players.model.context.PlayerContext
import org.qbrp.main.engine.players.model.util.PlayerId
import org.qbrp.main.engine.players.nicknames.NicknamesModule

typealias PlayerStateImpl = ComponentState<PlayerObject>

open class ServerPlayerObject(
    val context: PlayerContext,
    minecraftPlayerId: MinecraftPlayerId,
    override val username: Username,
    var account: Account,
    val session: SessionData,
    state: PlayerState = PlayerStateImpl(),
): PlayerObject(username, minecraftPlayerId, PlayerId(account.playerId), state), PlayerSnapshot by context {
    val location
        get() = require<PlayerLocation>()
    val displayName: String
        get() = state.get<NicknamesModule.NicknameManager>()?.displayName ?: username.value

    @Deprecated("Использовать Engine-формат")
    val displayNameText: Text
        get() = displayName.formatMm()

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
