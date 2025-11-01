package org.qbrp.main.engine.players.characters.components

import org.qbrp.main.core.fabricmc.server.invokeCommandAsRoot

class Height(
    ctx: CharacterComponentInstantiateContext,
) : CharacterComponent(ctx) {
    var value: Int = character.height

    fun applyScale() = with(player) {
        invokeCommandAsRoot("scale reset ${serverPlayer.username}")
        invokeCommandAsRoot("scale set pehkui:base ${character.scaleFactor} ${serverPlayer.username}")
        invokeCommandAsRoot("scale set pehkui:held_item ${1/(character.scaleFactor)} ${serverPlayer.username}")
    }

    override fun onEnable() {
        applyScale()
    }
}