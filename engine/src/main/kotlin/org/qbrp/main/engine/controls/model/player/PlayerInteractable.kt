package org.qbrp.main.engine.controls.model.player

import org.koin.core.context.GlobalContext
import org.qbrp.main.core.mc.player.model.PlayerSnapshot
import org.qbrp.main.engine.chat.ChatAPI
import org.qbrp.main.engine.containers.util.ContainerId
import org.qbrp.main.engine.controls.model.container.LocateableInteractable
import org.qbrp.main.engine.controls.entity.interactable.AnyInteractableType
import org.qbrp.main.engine.controls.entity.InteractableTypes
import org.qbrp.main.engine.controls.entity.display.InteractionIcon
import org.qbrp.main.engine.controls.entity.display.InteractableViewData
import org.qbrp.main.engine.players.auth.SessionData
import org.qbrp.main.engine.text.literal
import org.qbrp.main.engine.text.style.TextColor

interface PlayerInteractable: LocateableInteractable, IdentifiableInteractable {
    val interactableNameColor: Int
    val interactableDisplayName: String
    val session: SessionData
    val snapshot: PlayerSnapshot
    val characterApplied: Boolean

    override val interactableType: AnyInteractableType get() = InteractableTypes.PLAYER
    override val interactableDisplay: InteractableViewData
        get() {
            return InteractableViewData.of(
                literal(interactableDisplayName, TextColor(interactableNameColor)),
                InteractionIcon.Player(session.playerId),
            )
        }

    fun sendChatMessage(text: String, api: ChatAPI = GlobalContext.get().get())
    fun getAnchoredContainer(name: String): ContainerId
}