package org.qbrp.main.engine.players.model

import org.qbrp.main.core.events.Event

data class PlayerInitBeforeJsonLoadEvent(val player: ServerPlayerObject) : Event

data class PlayerInstantiateEvent(val player: ServerPlayerObject) : Event

data class PlayerInitAfterJsonLoadEvent(val player: ServerPlayerObject) : Event

data class PlayerDestroyEvent(val player: ServerPlayerObject) : Event

data class PlayerDisconnectEvent(val player: ServerPlayerObject) : Event