package org.qbrp.main.core.keybinds

import org.qbrp.main.core.events.Event
import org.qbrp.main.engine.players.model.ServerPlayerObject

data class KeybindCallback(val player: ServerPlayerObject): Event