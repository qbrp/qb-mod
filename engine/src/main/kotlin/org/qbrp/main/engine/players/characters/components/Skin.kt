package org.qbrp.main.engine.players.characters.components

import org.qbrp.main.core.fabricmc.server.invokeCommandAsSelf

class Skin(ctx: CharacterComponentInstantiateContext): CharacterComponent(ctx) {
    val model get() = character.appearance.model

    fun apply(skinUrl: String) = with(serverPlayer) {
        invokeCommandAsSelf("""skin url "$skinUrl" $model""")
    }
}