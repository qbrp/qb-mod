package org.qbrp.client.engine.players

import org.qbrp.main.engine.players.model.ServerPlayerObject
import org.qbrp.main.engine.players.model.service.GenericPlayerList
import org.qbrp.main.engine.players.model.service.PlayerStorage

interface ClientPlayerList : GenericPlayerList<ClientPlayerObject>

class ClientPlayerStorage : PlayerStorage<ClientPlayerObject>(), ClientPlayerList