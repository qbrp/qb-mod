package org.qbrp.main.engine.world.model

import net.minecraft.server.world.ServerWorld
import org.qbrp.main.core.components.AnyComponent
import org.qbrp.main.core.components.ComponentManager
import org.qbrp.main.core.components.extensions.add
import org.qbrp.main.core.components.state.ComponentHolder
import org.qbrp.main.core.fabricmc.server.CommandHolder
import org.qbrp.main.engine.players.model.ServerPlayerObject
import org.qbrp.main.engine.players.model.service.ServerPlayerStorage
import java.util.function.Consumer

class MainWorld(
    private val playerStorage: ServerPlayerStorage,
    private val componentManager: ComponentManager = ComponentHolder(),
) : World, ComponentManager by componentManager {
    override val players: List<ServerPlayerObject> get() = playerStorage.toList()

    init {
        add(CommandHolder())
    }
}