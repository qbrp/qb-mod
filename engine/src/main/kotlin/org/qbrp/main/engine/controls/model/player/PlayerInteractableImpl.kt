package org.qbrp.main.engine.controls.model.player

import org.qbrp.main.core.components.extensions.get
import org.qbrp.main.core.components.extensions.getAs
import org.qbrp.main.core.util.StringId
import org.qbrp.main.core.fabricmc.eyePos
import org.qbrp.main.core.fabricmc.rotationVector
import org.qbrp.main.core.util.math.asVec3
import org.qbrp.main.engine.Colors
import org.qbrp.main.engine.chat.ChatAPI
import org.qbrp.main.engine.containers.util.ContainerId
import org.qbrp.main.engine.containers.components.ContainerAnchor
import org.qbrp.main.engine.players.characters.components.CharactersBank
import org.qbrp.main.engine.players.model.ServerPlayerObject
import org.qbrp.main.engine.players.model.pos
import org.qbrp.main.core.util.PlayerId
import org.qbrp.main.engine.players.model.world
import org.qbrp.main.engine.world.location.WorldPos
import org.qbrp.main.engine.world.location.WorldVec3
import org.qbrp.main.engine.world.model.World

class PlayerInteractableImpl(
    private val logic: ServerPlayerObject,
) : PlayerInteractable {
    override val id: StringId get() = logic.stringId
    override val playerId: PlayerId get() = logic.playerId
    override val eyePos: WorldVec3 get() = logic.eyePos.asVec3()
    override val rotationVector: WorldVec3 get() = logic.rotationVector
    override val pos: WorldPos get() = logic.pos
    override val world: World get() = logic.world
    override val characterApplied: Boolean get() = logic.get<CharactersBank>()?.character != null

    override val interactableNameColor: Int
        get() = Colors.WHITE
    override val interactableDisplayName: String
        get() = logic.username.value

    override fun sendChatMessage(text: String, api: ChatAPI) {
        logic.sendChatMessage(text, api)
    }

    override fun getAnchoredContainer(name: String): ContainerId {
        return logic.getAs<ContainerAnchor>(name)!!.id
    }
}