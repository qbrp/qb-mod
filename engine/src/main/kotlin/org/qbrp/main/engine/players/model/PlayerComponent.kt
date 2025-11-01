package org.qbrp.main.engine.players.model

import net.minecraft.util.ActionResult
import org.qbrp.main.core.components.AssociatedComponentState
import org.qbrp.main.core.components.Component
import org.qbrp.main.core.components.ComponentManager
import org.qbrp.main.core.components.InstantiationContext
import org.qbrp.main.core.storage.Table
import org.qbrp.main.engine.chat.core.messages.ChatMessage
import org.qbrp.main.engine.chat.core.messages.MessageSender
import org.qbrp.main.engine.players.accounts.Account
import org.qbrp.main.engine.players.accounts.AccountUpdate

typealias PlayerState = AssociatedComponentState<PlayerObject>

interface PlayerComponentBehaviour: Component<PlayerState> {
    override var state: PlayerState
    val player: PlayerObject get() = this.state.parent
    val serverPlayer: ServerPlayerObject get() = this.state.parent as ServerPlayerObject

    fun onLeave() {}
    fun onChatMessage(sender: MessageSender, message: ChatMessage): ActionResult = ActionResult.PASS
    fun onAccountSave(account: Account, table: Table) = AccountUpdate(emptyList())
    fun onKeybind(id: String) {}
}

open class PlayerComponentInstantiateContext(
    val player: PlayerObject,
    override val componentManager: PlayerState
) : InstantiationContext<PlayerState>, PlayerState by componentManager

abstract class PlayerComponent(
    ctx: PlayerComponentInstantiateContext
) : PlayerComponentBehaviour, ComponentManager by ctx.componentManager {
    override var state: PlayerState = ctx.componentManager
    protected fun sendMessage(text: String) { serverPlayer.sendChatMessage(text) }
}