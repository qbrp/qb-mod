package org.qbrp.client.engine.players.event

import org.qbrp.client.engine.players.ClientPlayerObject
import org.qbrp.main.core.events.Event
import org.qbrp.main.engine.players.model.networking.packet.PlayerPermissions

data class LocalPlayerSyncEvent(val localPlayer: ClientPlayerObject) : Event

data class LocalPlayerPermissionsEvent(val permissions: PlayerPermissions) : Event