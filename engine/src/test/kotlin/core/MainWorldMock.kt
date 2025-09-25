package core

import org.qbrp.main.engine.players.model.ServerPlayerObject
import org.qbrp.main.engine.world.model.World

class MainWorldMock(override val players: List<ServerPlayerObject> = mutableListOf()) : World