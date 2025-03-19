package org.qbrp.engine.chat.addons.records

import org.qbrp.core.game.player.PlayerManager

open class Action(author: String, action: String, command: String): Line(PlayerManager.getPlayerData(author)?.account?.displayName ?: author, action) {
}
