package org.qbrp.client.engine.players

import org.qbrp.main.core.components.ComponentManager
import org.qbrp.main.core.components.state.ComponentHolder
import org.qbrp.main.engine.players.model.PlayerObject
import org.qbrp.main.engine.players.model.ServerPlayerObject
import org.qbrp.main.engine.players.model.service.PlayerList
import org.qbrp.main.engine.world.model.World

class ClientWorld(
    val playerStorage: ClientPlayerStorage,
    val componentManager: ComponentManager = ComponentHolder()
) : World, ComponentManager by componentManager {
    override val players: List<PlayerObject> get() = playerStorage.getAll().toList()
}