package org.qbrp.main.engine.controls.model.player

import org.qbrp.main.core.dataformats.StringId
import org.qbrp.main.core.mc.player.model.PlayerSnapshot
import org.qbrp.main.engine.Colors
import org.qbrp.main.engine.chat.ChatAPI
import org.qbrp.main.engine.containers.util.ContainerId
import org.qbrp.main.engine.containers.v2.components.ContainerAnchor
import org.qbrp.main.engine.players.auth.SessionData
import org.qbrp.main.engine.players.model.ServerPlayerObject
import org.qbrp.main.engine.world.location.Position
import org.qbrp.main.engine.world.model.World

class PlayerInteractableImpl(
    private val logic: ServerPlayerObject,
) : PlayerInteractable {
    override val id: StringId get() = logic.stringId
    override val session: SessionData get() = logic.session
    override val pos: Position get() = logic.pos
    override val snapshot: PlayerSnapshot get() = logic.context
    override val world: World get() = logic.context.world

    override val interactableNameColor: Int
        get() = Colors.WHITE
    override val interactableDisplayName: String
        get() = logic.username.value

    override fun sendChatMessage(text: String, api: ChatAPI) {
        logic.sendChatMessage(text, api)
    }

    override fun getAnchoredContainer(name: String): ContainerId {
        return logic.getComponentAs<ContainerAnchor>(name)!!.id
    }
}