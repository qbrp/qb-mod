package org.qbrp.core.game.player.interaction.model

class DelayProcess(val process: () -> String, val condition: () -> Boolean) {
}