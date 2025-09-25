package org.qbrp.main.mechanics.characters.physical

import org.qbrp.main.core.mc.server.command.RawMinecraftCommand
import org.qbrp.main.core.mc.server.command.ServerCommandExecutor
import org.qbrp.main.engine.players.characters.components.CharacterComponent
import org.qbrp.main.engine.players.characters.components.CharacterComponentInstantiateContext

class Height(ctx: CharacterComponentInstantiateContext, private val commandExecutor: ServerCommandExecutor): CharacterComponent(ctx) {
    var value: Int = character.height

    fun applyScale() {
        commandExecutor.apply {
            executeAsRoot(RawMinecraftCommand("scale reset ${serverPlayer.username}"))
            executeAsRoot(RawMinecraftCommand("scale set pehkui:base ${character.scaleFactor} ${serverPlayer.username}"))
            executeAsRoot(RawMinecraftCommand("scale set pehkui:held_item ${1/(character.scaleFactor)} ${serverPlayer.username}"))
        }
    }

    override fun onEnable() {
        applyScale()
    }
}